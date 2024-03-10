package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.RoomRecipes;
import ca.bradj.roomrecipes.core.Room;
import ca.bradj.roomrecipes.core.space.InclusiveSpace;
import ca.bradj.roomrecipes.core.space.Position;
import ca.bradj.roomrecipes.logic.interfaces.WallDetector;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;

public class LevelRoomDetector {
    private final Queue<Position> doorsToProcess = new LinkedBlockingQueue<>();
    private final ImmutableList<Position> initialDoors;
    private final boolean enableDebugArt;
    private Map<Position, Optional<Room>> processedRooms = new HashMap<>();
    private final int maxDistanceFromDoor;
    private final WallDetector checker;
    private int doorIteration = 0;
    private int iteration = 0;
    private int maxIterations;
    private boolean done;
    private final Map<Position, String[][]> debugArt = new HashMap<>();

    public LevelRoomDetector(
            Collection<Position> currentDoors,
            int maxDistanceFromDoor,
            int maxIterations,
            WallDetector checker,
            boolean enableDebugArt
    ) {
        doorsToProcess.addAll(currentDoors);
        this.initialDoors = ImmutableList.copyOf(currentDoors);
        this.maxDistanceFromDoor = maxDistanceFromDoor;
        this.maxIterations = maxIterations;
        this.checker = checker;
        this.enableDebugArt = enableDebugArt;
        if (enableDebugArt) {
            currentDoors.forEach(door -> {
                debugArt.put(door, new String[(maxDistanceFromDoor * 2) + 1][(maxDistanceFromDoor * 2) + 1]);
                debugArt.get(door)[maxDistanceFromDoor][maxDistanceFromDoor] = "D";
            });
        }
    }

    public boolean isDone() {
        return done;
    }

    public int iterationsUsed() {
        return iteration;
    }

    public @Nullable ImmutableMap<Position, Optional<Room>> proceed() {
        iteration++;
        if (iteration > maxIterations) {
            RoomRecipes.LOGGER.error(
                    "Failed to detect rooms after {} iterations",
                    maxIterations
            );
            return giveUp();
        }
        if (doorsToProcess.isEmpty()) {
            this.done = true;
            if (processedRooms.isEmpty()) {
                return giveUp();
            }
            return removeOverlaps(processedRooms);
        }
        Position nextDoor = doorsToProcess.remove();

        // TODO: Implement early exit.
        //  Accept a snapshot of the town. If nothing has changed, return.

        if (this.doorIteration > maxDistanceFromDoor - 2) {
            this.doorIteration = 0;
            return null;
        }

        String[][] art = debugArt.get(nextDoor);

        Optional<Room> roomForDoor = RoomDetection.findRoomForDoorIteration(
                nextDoor,
                doorIteration + 2,
                maxDistanceFromDoor,
                p -> {
                    boolean b = checker.IsWall(p);
                    if (enableDebugArt) {
                        int zOffset = p.z - nextDoor.z;
                        int xOffset = p.x - nextDoor.x;
                        if (art[maxDistanceFromDoor + zOffset][maxDistanceFromDoor + xOffset] == null) {
                            art[maxDistanceFromDoor + zOffset][maxDistanceFromDoor + xOffset] = b ? "W" : "_";
                        }
                    }
                    return b;
                }
        );

        if (roomForDoor.isEmpty()) {
            doorsToProcess.add(nextDoor);
            doorIteration++;
            return null;
        }

        processedRooms.put(
                nextDoor,
                roomForDoor
        );
        return null;
    }

    private ImmutableMap<Position, Optional<Room>> removeOverlaps(
            Map<Position, Optional<Room>> detectedRooms
    ) {
        int attempts = 0;
        int corrections = 1;
        while (corrections > 0 && attempts <= 3) {
            attempts++;
            Stream<Optional<Room>> onlyPresent = detectedRooms.values()
                                                              .stream()
                                                              .filter(Optional::isPresent);
            List<Room> rooms = onlyPresent.map(Optional::get)
                                          .toList();
            corrections = 0;
            for (Room r1 : rooms) {
                if (corrections > 0) {
                    break;
                }
                for (Room r2 : rooms) {
                    if (r1.equals(r2)) {
                        continue;
                    }
                    if (r1.getSpace()
                          .equals(r2.getSpace())) {
                        final Optional<Room> alternate = RoomDetection.findRoomForDoor(
                                r2.getDoorPos(),
                                maxDistanceFromDoor,
                                Optional.of(r1.getSpace()),
                                0,
                                checker
                        );
                        if (alternate.isPresent()) {
                            if (rooms.stream()
                                     .anyMatch(v -> v.getSpace()
                                                     .equals(alternate.get()
                                                                      .getSpace()))) {
                                detectedRooms.put(
                                        r2.getDoorPos(),
                                        Optional.empty()
                                );
                                corrections++;
                                break;
                            }
                            RoomRecipes.LOGGER.trace("Using alternate room: " + alternate.get());
                            detectedRooms.put(
                                    r2.getDoorPos(),
                                    alternate
                            );
                            corrections++;
                            break;
                        }
                        Optional<Room> alternate2 = RoomDetection.findRoomForDoor(
                                r1.getDoorPos(),
                                maxDistanceFromDoor,
                                Optional.of(r2.getSpace()),
                                0,
                                checker
                        );
                        if (alternate2.isPresent()) {
                            detectedRooms.put(
                                    r1.getDoorPos(),
                                    alternate2
                            );
                            corrections++;
                            RoomRecipes.LOGGER.trace("Using alternate room: " + alternate2.get());
                            break;
                        }
                        detectedRooms.put(
                                r2.getDoorPos(),
                                Optional.empty()
                        );
                        corrections++;
                        break;
                    } else {
                        if (InclusiveSpaces.overlapOnXZPlane(
                                r1.getSpace(),
                                r2.getSpace()
                        )) {
                            double a1 = InclusiveSpaces.calculateArea(r1.getSpace());
                            double a2 = InclusiveSpaces.calculateArea(r2.getSpace());
                            if (a1 > a2) {
                                RoomRecipes.LOGGER.debug("Chopping " + r2 + " off of " + r1);
                                InclusiveSpace chopped = r1.getSpace()
                                                           .chopOff(r2.getSpace());
                                detectedRooms.put(
                                        r1.getDoorPos(),
                                        Optional.of(r1.withSpace(chopped))
                                );
                                corrections++;
                                break;
                            }
                            if (a2 > a1) {
                                RoomRecipes.LOGGER.debug("Chopping " + r1 + " off of " + r2);
                                InclusiveSpace chopped = r2.getSpace()
                                                           .chopOff(r1.getSpace());
                                detectedRooms.put(
                                        r2.getDoorPos(),
                                        Optional.of(r2.withSpace(chopped))
                                );
                                corrections++;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return ImmutableMap.copyOf(detectedRooms);
    }

    private ImmutableMap<Position, Optional<Room>> giveUp() {
        ImmutableMap.Builder<Position, Optional<Room>> b = ImmutableMap.builder();
        initialDoors.forEach(v -> b.put(
                v,
                Optional.empty()
        ));
        return b.build();
    }

    public ImmutableMap<Position, String> getDebugArt(boolean cropNulls) {
        if (!enableDebugArt) {
            RoomRecipes.LOGGER.error("Debug art was not enabled in the room detector constructor");
            return ImmutableMap.of();
        }

        ImmutableMap.Builder<Position, String> b = ImmutableMap.builder();
        debugArt.forEach((k, v) -> {
            if (cropNulls) {
                v = findSmallestRectangle(v);
            }
            StringBuilder sb = new StringBuilder();
            sb.append("{\n");
            for (int i = 0; i < v.length; i++) {
                sb.append("    {");
                for (int j = 0; j < v[i].length; j++) {
                    String val = v[i][j];
                    sb.append("\"")
                      .append(val == null ? '?' : val)
                      .append("\"");
                    if (j < v[i].length - 1) {
                        sb.append(", ");
                    }
                }
                sb.append("}");
                if (i < v.length - 1) {
                    sb.append(",\n");
                } else {
                    sb.append("\n");
                }
            }
            sb.append("}");

            b.put(k, sb.toString());
        });
        return b.build();
    }

    public static String[][] findSmallestRectangle(String[][] array) {
        // Find bounds of the non-null values
        int minRow = Integer.MAX_VALUE;
        int maxRow = Integer.MIN_VALUE;
        int minCol = Integer.MAX_VALUE;
        int maxCol = Integer.MIN_VALUE;

        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                if (array[i][j] != null) {
                    minRow = Math.min(minRow, i);
                    maxRow = Math.max(maxRow, i);
                    minCol = Math.min(minCol, j);
                    maxCol = Math.max(maxCol, j);
                }
            }
        }

        // Calculate the dimensions of the smallest rectangle
        int numRows = maxRow - minRow + 1;
        int numCols = maxCol - minCol + 1;

        // Create a new 2D array to store the smallest rectangle
        String[][] smallestRectangle = new String[numRows][numCols];

        // Populate the new array with the values from the original array
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                smallestRectangle[i][j] = array[minRow + i][minCol + j];
            }
        }

        return smallestRectangle;
    }
}
