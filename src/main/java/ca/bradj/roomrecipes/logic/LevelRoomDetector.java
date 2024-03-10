package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.RoomRecipes;
import ca.bradj.roomrecipes.core.Room;
import ca.bradj.roomrecipes.core.space.Position;
import ca.bradj.roomrecipes.logic.interfaces.WallDetector;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class LevelRoomDetector {
    private final Queue<Position> doorsToProcess = new LinkedBlockingQueue<>();
    private final ImmutableList<Position> initialDoors;
    private Map<Position, Optional<Room>> processedRooms = new HashMap<>();
    private final int maxDistanceFromDoor;
    private final WallDetector checker;
    private int doorIteration = 0;
    private int iteration = 0;
    private int maxIterations;
    private boolean done;

    public LevelRoomDetector(
            Collection<Position> currentDoors,
            int maxDistanceFromDoor,
            int maxIterations,
            WallDetector checker
    ) {
        doorsToProcess.addAll(currentDoors);
        this.initialDoors = ImmutableList.copyOf(currentDoors);
        this.maxDistanceFromDoor = maxDistanceFromDoor;
        this.maxIterations = maxIterations;
        this.checker = checker;
    }

    public boolean isDone() {
        return done;
    }

    public @Nullable ImmutableMap<Position, Optional<Room>> proceed() {
        iteration++;
        if (iteration > maxIterations) {
            RoomRecipes.LOGGER.error("Failed to detect rooms after {} iterations", maxIterations);
            return giveUp();
        }
        if (doorsToProcess.isEmpty()) {
            this.done = true;
            if (processedRooms.isEmpty()) {
                return giveUp();
            }
            return ImmutableMap.copyOf(processedRooms);
        }
        Position nextDoor = doorsToProcess.remove();

        // TODO: Implement early exit.
        //  Accept a snapshot of the town. If nothing has changed, return.

        if (this.doorIteration > maxDistanceFromDoor - 2) {
            this.doorIteration = 0;
            return null;
        }
        Optional<Room> roomForDoor = RoomDetection.findRoomForDoorIteration(
                nextDoor,
                doorIteration + 2,
                maxDistanceFromDoor,
                checker
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

    private ImmutableMap<Position, Optional<Room>> giveUp() {
        ImmutableMap.Builder<Position, Optional<Room>> b = ImmutableMap.builder();
        initialDoors.forEach(v -> b.put(v, Optional.empty()));
        return b.build();
    }
}
