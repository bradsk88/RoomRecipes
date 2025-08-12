package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.RoomRecipes;
import ca.bradj.roomrecipes.core.Room;
import ca.bradj.roomrecipes.core.space.InclusiveSpace;
import ca.bradj.roomrecipes.core.space.Position;
import ca.bradj.roomrecipes.logic.interfaces.WallDetector;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraftforge.common.util.TriPredicate;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class LevelRoomDetector {
    private final Queue<Position> doorsToProcess = new LinkedBlockingQueue<>();
    private final ImmutableList<Position> initialDoors;
    private final boolean enableDebugArt;
    private final @Nullable Consumer<String> flightRecorder;
    private Map<Position, Optional<Room>> processedRooms = new HashMap<>();
    private final int maxDistanceFromDoor;
    private final TriPredicate<Position, @Nullable Position, @Nullable String[][]> checker;
    private Map<Position, Integer> doorIteration = new HashMap<>();
    private int iteration = 0;
    private int maxIterations;
    private boolean done;
    private final Map<Position, String[][]> debugArt = new HashMap<>();

    public LevelRoomDetector(
            Collection<Position> currentDoors,
            int maxDistanceFromDoor,
            int maxIterations,
            WallDetector checker,
            boolean enableDebugArt,
            @Nullable Consumer<String> flightRecorder
    ) {
        doorsToProcess.addAll(currentDoors);
        this.initialDoors = ImmutableList.copyOf(currentDoors);
        this.maxDistanceFromDoor = maxDistanceFromDoor;
        this.maxIterations = maxIterations;
        Map<Position, Boolean> cache = new HashMap<>();
        this.checker = (p, nextDoor, art) -> cache.compute(
                p, (p2, r) -> {
                    if (r != null) {
                        return r;
                    }
                    boolean b = checker.IsWall(p2);
                    if (enableDebugArt && nextDoor != null && art != null) {
                        captureAsArtPixel(p2, nextDoor, art, b, "W", " ");
                        captureSurroundingAsArtPixels(checker::IsWall, p2, nextDoor, art, "w", "_");
                    }
                    return b;
                }
        );
        this.enableDebugArt = enableDebugArt;
        if (enableDebugArt) {
            currentDoors.forEach(door -> {
                debugArt.put(door, new String[(maxDistanceFromDoor * 2) + 2][(maxDistanceFromDoor * 2) + 2]);
                debugArt.get(door)[maxDistanceFromDoor][maxDistanceFromDoor] = "D";
            });
        }
        this.flightRecorder = flightRecorder == null ? s -> {
        } : flightRecorder;
    }

    public boolean isDone() {
        return done;
    }

    public int iterationsUsed() {
        return iteration;
    }

    public @Nullable ImmutableMap<Position, Optional<Room>> proceed() {
        return proceed(p -> Optional.empty());
    }

    public @Nullable ImmutableMap<Position, Optional<Room>> proceed(
            Function<Position, Optional<Room>> existingRooms
    ) {
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

        Optional<Room> existing = existingRooms.apply(nextDoor);
        if (existing.isPresent() && InclusiveSpaces.isWhole(
                existing.get().getSpace(), p -> checker.test(p, null, null), true
        )) {
            processedRooms.put(nextDoor, existing);
            return null;
        }

        if (this.doorIteration.getOrDefault(nextDoor, 0) > maxDistanceFromDoor - 2) {
            return null;
        }

        Optional<Room> roomForDoor = WallWalkingRoomDetection.tryFind(
                nextDoor,
                this.doorIteration.getOrDefault(nextDoor, 0),
                flightRecorder,
                p -> checker.test(p, nextDoor, debugArt.get(nextDoor))
        ).toOptional();

        if (roomForDoor.isEmpty()) {
            doorsToProcess.add(nextDoor);
            doorIteration.compute(nextDoor, (pos, cur) -> cur == null ? 1 : cur + 1);
            return null;
        }

        processedRooms.put(
                nextDoor,
                roomForDoor
        );
        return null;
    }

    private void captureSurroundingAsArtPixels(
            Predicate<Position> isWall,
            Position p,
            Position nextDoor,
            String[][] art,
            String w,
            String a
    ) {
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                Position op = p.offset(i, j);
                boolean b2 = isWall.test(op);
                captureAsArtPixel(op, nextDoor, art, b2, w, a);
            }
        }
    }

    private void captureAsArtPixel(
            Position p,
            Position nextDoor,
            String[][] art,
            boolean b,
            String w,
            String a
    ) {
        int zOffset = p.z - nextDoor.z;
        int xOffset = p.x - nextDoor.x;
        int zzz = maxDistanceFromDoor + zOffset;
        int xxx = maxDistanceFromDoor + xOffset;
        if (zzz >= art.length || xxx >= art[0].length || zzz < 0 || xxx < 0) {
            return;
        }

        try {
            String v = art[zzz][xxx];
            if (v == null || art[zzz][xxx].equals("w") || art[zzz][xxx].equals("_")) {
                art[zzz][xxx] = b ? w : a;
            }
        } catch (Exception e) {
            RoomRecipes.LOGGER.error(
                    "Failed to register art pixel for {} around door {}", p.getUIString(),
                    nextDoor.getUIString()
            );
            RoomRecipes.LOGGER.error("Exception", e);
        }
    }

    private ImmutableMap<Position, Optional<Room>> removeOverlaps(
            Map<Position, Optional<Room>> detectedRooms
    ) {
        int attempts = 0;
        int corrections = 1;
        while (corrections > 0 && attempts <= 3) {
            attempts++;
            flightRecorder.accept(String.format(
                    "Running overlap removal. Attempt %d. Corrections: %d",
                    attempts,
                    corrections
            ));
            Stream<Optional<Room>> onlyPresent = detectedRooms.values()
                                                              .stream()
                                                              .filter(Optional::isPresent);
            Map<Position, Room> rooms = ImmutableMap.copyOf(onlyPresent.map(Optional::get)
                                                                       .map(v -> new AbstractMap.SimpleEntry<>(
                                                                               v.doorPos,
                                                                               v
                                                                       )).toList());
            List<Position> leastUsedDoorPositions = getLeastUsedDoorPositionsFirst(rooms.values());
            corrections = 0;
            for (Position p1 : leastUsedDoorPositions) {
                Room r1 = rooms.get(p1);
                if (corrections > 0) {
                    break;
                }
                for (Position p2 : leastUsedDoorPositions) {
                    Room r2 = rooms.get(p2);
                    if (r1.equals(r2)) {
                        continue;
                    }
                    Position r1dp = r1.getDoorPos();
                    Position r2dp = r2.getDoorPos();
                    if (r1.getSpace()
                          .equals(r2.getSpace())) {
                        final Optional<Room> alternate = RoomDetection.findRoomForDoor(
                                r2dp,
                                maxDistanceFromDoor,
                                Optional.of(r1.getSpace()),
                                0,
                                this::checkWithoutArt
                        ).toOptional();
                        if (alternate.isPresent()) {
                            if (rooms.values().stream()
                                     .anyMatch(v -> v.getSpace()
                                                     .equals(alternate.get()
                                                                      .getSpace()))) {
                                detectedRooms.put(
                                        r2dp,
                                        Optional.empty()
                                );
                                corrections++;
                                break;
                            }
                            flightRecorder.accept("Using alternate room: " + sm(alternate.get()));
                            detectedRooms.put(
                                    r2dp,
                                    alternate
                            );
                            corrections++;
                            break;
                        }
                        Optional<Room> alternate2 = RoomDetection.findRoomForDoor(
                                r1dp,
                                maxDistanceFromDoor,
                                Optional.of(r2.getSpace()),
                                0,
                                this::checkWithoutArt
                        ).toOptional();
                        if (alternate2.isPresent()) {
                            detectedRooms.put(
                                    r1dp,
                                    alternate2
                            );
                            corrections++;
                            flightRecorder.accept("Using alternate room: " + sm(alternate2.get()));
                            break;
                        }
                        detectedRooms.put(
                                r2dp,
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
                                flightRecorder.accept("Chopping " + sm(r2.getSpace()) + " off of " + sm(r1.getSpace()));
                                InclusiveSpace chopped = r1.getSpace()
                                                           .chopOff(r2.getSpace());

                                if (!InclusiveSpaces.getWallPositions(chopped).contains(r1dp)) {
                                    flightRecorder.accept("Giving " + sm(r2.getSpace()) + " to " + r2dp + " instead of " + r1dp);
                                    detectedRooms.put(r2dp, Optional.of(r2.withSpace(chopped)));
                                    detectedRooms.put(r1dp, Optional.of(r1.withSpace(r2.getSpace())));
                                    corrections++;
                                    break;
                                }
                                flightRecorder.accept("Giving chopped-off portion to " + r1dp);
                                detectedRooms.put(
                                        r1dp,
                                        Optional.of(r1.withSpace(chopped))
                                );
                                corrections++;
                                break;
                            }
                            if (a2 > a1) {
                                flightRecorder.accept("Chopping " + sm(r1.getSpace()) + " off of " + sm(r2.getSpace()));
                                InclusiveSpace chopped = r2.getSpace()
                                                           .chopOff(r1.getSpace());
                                if (!InclusiveSpaces.getWallPositions(chopped).contains(r2dp)) {
                                    flightRecorder.accept("Giving " + sm(r1.getSpace()) + " to " + r1dp + " instead of " + r2dp);
                                    detectedRooms.put(r1dp, Optional.of(r1.withSpace(chopped)));
                                    detectedRooms.put(r2dp, Optional.of(r2.withSpace(r1.getSpace())));
                                    corrections++;
                                    break;
                                }
                                flightRecorder.accept("Giving chopped-off portion to " + r2dp);
                                detectedRooms.put(
                                        r2dp,
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

    private List<Position> getLeastUsedDoorPositionsFirst(Collection<Room> values) {
        ImmutableList.Builder<Position> b = ImmutableList.builder();
        for (Room value : values) {
            for (InclusiveSpace space : value.getSpaces()) {
                b.addAll(InclusiveSpaces.getWallPositions(space));
            }
        }
        ImmutableList<Position> wallPos = b.build();
        HashMap<Position, Integer> m = new HashMap<>();
        for (Room value : values) {
            if (wallPos.contains(value.doorPos)) {
                m.merge(value.doorPos, -1, Integer::sum);
            }
        }
        return m.entrySet()
                .stream()
                .sorted(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .collect(ImmutableList.toImmutableList());
    }

    private String sm(Room room) {
        return "Room{" + room.getDoorPos().getUIString() + ": " + sm(room.getSpace()) + "}";
    }

    private String sm(InclusiveSpace space) {
        return InclusiveSpaces.getShortString(space);
    }

    private boolean checkWithoutArt(Position p) {
        return checker.test(p, null, null);
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
