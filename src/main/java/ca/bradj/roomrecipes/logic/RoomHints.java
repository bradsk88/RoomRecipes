package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.core.Room;
import ca.bradj.roomrecipes.core.space.InclusiveSpace;
import ca.bradj.roomrecipes.core.space.Position;
import ca.bradj.roomrecipes.logic.interfaces.WallDetector;
import ca.bradj.roomrecipes.rooms.XWall;
import ca.bradj.roomrecipes.rooms.ZWall;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class RoomHints {

    public final XWall northWall;
    public final XWall southWall;
    public final ZWall westWall;
    public final ZWall eastWall;

    public RoomHints(
            @Nullable XWall northWall,
            @Nullable XWall southWall,
            @Nullable ZWall westWall,
            @Nullable ZWall eastWall
    ) {
        this.northWall = northWall;
        this.southWall = southWall;
        this.westWall = westWall;
        this.eastWall = eastWall;
    }

    public static RoomHints empty() {
        return new RoomHints(null, null, null, null);
    }

    public RoomHints withNorthWall(XWall northWall) {
        return new RoomHints(
                northWall,
                this.southWall,
                this.westWall,
                this.eastWall
        );
    }

    public RoomHints withSouthWall(XWall southWall) {
        return new RoomHints(
                this.northWall,
                southWall,
                this.westWall,
                this.eastWall
        );
    }

    public RoomHints withWestWall(ZWall westWall) {
        return new RoomHints(
                this.northWall,
                this.southWall,
                westWall,
                this.eastWall
        );
    }

    public RoomHints withEastWall(ZWall eastWall) {
        return new RoomHints(
                this.northWall,
                this.southWall,
                this.westWall,
                eastWall
        );
    }

    public boolean isRoom(RoomDetection.WallExclusion exclusion) {
        if (northWall == null && !exclusion.allowOpenNorthWall) {
            return false;
        }
        if (southWall == null && !exclusion.allowOpenSouthWall) {
            return false;
        }
        if (westWall == null && !exclusion.allowOpenWestWall) {
            return false;
        }
        if (eastWall == null && !exclusion.allowOpenEastWall) {
            return false;
        }
        return true;
    }

    public Optional<Room> asRoom(
            Position doorPos,
            RoomDetection.WallExclusion exclusion
    ) {
        if (isRoom(exclusion)) {
            if (northWall != null && southWall != null) {
                return Optional.of(new Room(doorPos, new InclusiveSpace(northWall.westCorner, southWall.eastCorner)));
            }
            if (westWall != null && eastWall != null) {
                return Optional.of(new Room(doorPos, new InclusiveSpace(westWall.northCorner, eastWall.southCorner)));
            }
        }
        return Optional.empty();
    }

    public RoomHints copy() {
        return new RoomHints(this.northWall, this.southWall, this.westWall, this.eastWall);
    }

    public Optional<RoomHints> getOpening() {
        boolean hasOneOpening = hasOneOpening();
        if (hasOneOpening) {
            return Optional.empty();
        }
        if (northWall == null) {
            return Optional.of(new RoomHints(new XWall(
                    westWall.northCorner, eastWall.northCorner), null, null, null
            ));
        }
        if (southWall == null) {
            return Optional.of(new RoomHints(
                    null, new XWall(westWall.southCorner, eastWall.southCorner), null, null
            ));
        }
        if (westWall == null) {
            return Optional.of(new RoomHints(
                    null, null, new ZWall(northWall.westCorner, southWall.westCorner), null
            ));
        }
        return Optional.of(new RoomHints(
                null, null, null, new ZWall(northWall.eastCorner, southWall.eastCorner)
        ));
    }

    private boolean hasOneOpening() {
        int wallsCount = 0;
        wallsCount = northWall == null ? wallsCount : wallsCount + 1;
        wallsCount = southWall == null ? wallsCount : wallsCount + 1;
        wallsCount = westWall == null ? wallsCount : wallsCount + 1;
        wallsCount = eastWall == null ? wallsCount : wallsCount + 1;
        boolean hasOneOpening = wallsCount != 3;
        return hasOneOpening;
    }

    public Optional<Room> adjoinedTo(Position doorPos, InclusiveSpace space) {
        if (!hasOneOpening()) {
            throw new IllegalStateException("RoomHints can only be adjoined if they have a singular opening");
        }
        if (northWall == null) {
            boolean joinW = space.getSouthXWall().westCorner.equals(westWall.northCorner);
            boolean joinE = space.getSouthXWall().eastCorner.equals(eastWall.northCorner);
            if (joinW && joinE) {
                return this.withNorthWall(space.getSouthXWall())
                        .asRoom(doorPos, RoomDetection.WallExclusion.mustHaveAllWalls())
                        .map(r -> r.withExtraSpace(space));
            }
        }
        if (southWall == null) {
            boolean joinW = space.getNorthXWall().westCorner.equals(westWall.southCorner);
            boolean joinE = space.getNorthXWall().eastCorner.equals(eastWall.southCorner);
            if (joinW && joinE) {
                return this.withSouthWall(space.getNorthXWall())
                        .asRoom(doorPos, RoomDetection.WallExclusion.mustHaveAllWalls())
                        .map(r -> r.withExtraSpace(space));
            }
        }
        if (westWall == null) {
            boolean joinN = space.getEastZWall().northCorner.equals(northWall.westCorner);
            boolean joinS = space.getEastZWall().southCorner.equals(southWall.westCorner);
            if (joinN && joinS) {
                return this.withWestWall(space.getEastZWall())
                        .asRoom(doorPos, RoomDetection.WallExclusion.mustHaveAllWalls())
                        .map(r -> r.withExtraSpace(space));
            }
        }
        if (eastWall == null) {
            boolean joinN = space.getWestZWall().northCorner.equals(northWall.eastCorner);
            boolean joinS = space.getWestZWall().southCorner.equals(southWall.eastCorner);
            if (joinN && joinS) {
                return this.withWestWall(space.getWestZWall())
                        .asRoom(doorPos, RoomDetection.WallExclusion.mustHaveAllWalls())
                        .map(r -> r.withExtraSpace(space));
            }
        }
        return Optional.empty();
    }
}
