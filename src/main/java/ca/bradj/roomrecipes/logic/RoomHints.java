package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.core.Room;
import ca.bradj.roomrecipes.core.space.InclusiveSpace;
import ca.bradj.roomrecipes.core.space.Position;
import ca.bradj.roomrecipes.rooms.XWall;
import ca.bradj.roomrecipes.rooms.ZWall;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class RoomHints {

    public final XWall northWall;
    public final XWall southWall;
    public final ZWall westWall;
    public final ZWall eastWall;
    public final XWall northOpening;
    public final XWall southOpening;
    public final ZWall westOpening;
    public final ZWall eastOpening;

    public RoomHints(
            @Nullable XWall northWall,
            @Nullable XWall southWall,
            @Nullable ZWall westWall,
            @Nullable ZWall eastWall
    ) {
        this(northWall, southWall, westWall, eastWall, null, null, null, null);
    }

    public RoomHints(
            @Nullable XWall northWall,
            @Nullable XWall southWall,
            @Nullable ZWall westWall,
            @Nullable ZWall eastWall,
            @Nullable XWall northOpening,
            @Nullable XWall southOpening,
            @Nullable ZWall westOpening,
            @Nullable ZWall eastOpening
    ) {
        this.northWall = northWall;
        this.southWall = southWall;
        this.westWall = westWall;
        this.eastWall = eastWall;
        this.northOpening = northOpening;
        this.southOpening = southOpening;
        this.westOpening = westOpening;
        this.eastOpening = eastOpening;
    }

    public static RoomHints empty() {
        return new RoomHints(null, null, null, null);
    }

    public RoomHints withNorthWall(XWall northWall) {
        return new RoomHints(
                northWall,
                this.southWall,
                this.westWall,
                this.eastWall,
                northOpening,
                southOpening,
                westOpening,
                eastOpening
        );
    }

    public RoomHints withSouthWall(XWall southWall) {
        return new RoomHints(
                this.northWall,
                southWall,
                this.westWall,
                this.eastWall,
                northOpening,
                southOpening,
                westOpening,
                eastOpening
        );
    }

    public RoomHints withWestWall(ZWall westWall) {
        return new RoomHints(
                this.northWall,
                this.southWall,
                westWall,
                this.eastWall,
                northOpening,
                southOpening,
                westOpening,
                eastOpening
        );
    }

    public RoomHints withEastWall(ZWall eastWall) {
        return new RoomHints(
                this.northWall,
                this.southWall,
                this.westWall,
                eastWall,
                northOpening,
                southOpening,
                westOpening,
                eastOpening
        );
    }

    public boolean isRoom(RoomDetection.WallExclusion exclusion) {
        if ((northOpening != null || northWall == null) && !exclusion.allowOpenNorthWall) {
            return false;
        }
        if ((southOpening != null || southWall == null) && !exclusion.allowOpenSouthWall) {
            return false;
        }
        if ((westOpening != null || westWall == null) && !exclusion.allowOpenWestWall) {
            return false;
        }
        if ((eastOpening != null || eastWall == null) && !exclusion.allowOpenEastWall) {
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
        return new RoomHints(
                this.northWall, this.southWall, this.westWall, this.eastWall,
                this.northOpening, this.southOpening, this.westOpening, this.eastOpening
        );
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
        int openCount = 0;
        openCount = northOpening == null ? openCount : openCount + 1;
        openCount = southOpening == null ? openCount : openCount + 1;
        openCount = westOpening == null ? openCount : openCount + 1;
        openCount = eastOpening == null ? openCount : openCount + 1;
        boolean hasOneOpening = openCount == 1;
        return hasOneOpening;
    }

    public Optional<Room> adjoinedTo(
            Position doorPos,
            InclusiveSpace space
    ) {
        if (!hasOneOpening()) {
            throw new IllegalStateException("RoomHints can only be adjoined if they have a singular opening");
        }
        if (northOpening != null) {
            boolean joinW = space.getSouthXWall().westCorner.equals(northOpening.westCorner);
            boolean joinE = space.getSouthXWall().eastCorner.equals(northOpening.eastCorner);
            if (joinW && joinE) {
                return this.asRoom(doorPos, RoomDetection.WallExclusion.allowNorthOpen())
                        .map(r -> r.withExtraSpace(space));
            }
        }
        if (southOpening != null) {
            boolean joinW = space.getNorthXWall().westCorner.equals(southOpening.westCorner);
            boolean joinE = space.getNorthXWall().eastCorner.equals(southOpening.eastCorner);
            if (joinW && joinE) {
                return this.asRoom(doorPos, RoomDetection.WallExclusion.allowSouthOpen())
                        .map(r -> r.withExtraSpace(space));
            }
        }
        if (westOpening != null) {
            boolean joinN = space.getEastZWall().northCorner.equals(westOpening.northCorner);
            boolean joinS = space.getEastZWall().southCorner.equals(westOpening.southCorner);
            if (joinN && joinS) {
                return this.asRoom(doorPos, RoomDetection.WallExclusion.allowWestOpen())
                        .map(r -> r.withExtraSpace(space));
            }
        }
        if (eastOpening != null) {
            boolean joinN = space.getWestZWall().northCorner.equals(eastOpening.northCorner);
            boolean joinS = space.getWestZWall().southCorner.equals(eastOpening.southCorner);
            if (joinN && joinS) {
                return this.asRoom(doorPos, RoomDetection.WallExclusion.allowEastOpen())
                        .map(r -> r.withExtraSpace(space));
            }
        }
        return Optional.empty();
    }

    public RoomHints withNorthOpening(XWall opening) {
        return new RoomHints(
                northWall, southWall, westWall, eastWall,
                opening, southOpening, westOpening, eastOpening
        );
    }

    public RoomHints withSouthOpening(XWall xWall) {
        return new RoomHints(
                northWall, southWall, westWall, eastWall,
                northOpening, xWall, westOpening, eastOpening
        );
    }

    public RoomHints withWestOpening(ZWall opening) {
        return new RoomHints(
                northWall, southWall, westWall, eastWall,
                northOpening, southOpening, opening, eastOpening
        );
    }

    public RoomHints withEastOpening(ZWall opening) {
        return new RoomHints(
                northWall, southWall, westWall, eastWall,
                northOpening, southOpening, westOpening, opening
        );
    }

    public boolean hasAnyOpenings() {
        return northOpening != null || southOpening != null || westOpening != null || eastOpening != null;
    }
}
