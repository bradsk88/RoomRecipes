package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.core.Room;
import ca.bradj.roomrecipes.core.space.InclusiveSpace;
import ca.bradj.roomrecipes.core.space.Position;
import ca.bradj.roomrecipes.rooms.Wall;
import ca.bradj.roomrecipes.rooms.XWall;
import ca.bradj.roomrecipes.rooms.ZWall;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
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
        Optional<InclusiveSpace> space = asSpace(exclusion);
        return space.map(v -> new Room(doorPos, v));
    }

    public Optional<InclusiveSpace> asSpace(
            RoomDetection.WallExclusion exclusion
    ) {
        if (isRoom(exclusion)) {
            if (northWall != null && southWall != null) {
                return Optional.of(new InclusiveSpace(northWall.westCorner, southWall.eastCorner));
            }
            if (westWall != null && eastWall != null) {
                return Optional.of(new InclusiveSpace(westWall.northCorner, eastWall.southCorner));
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

    public boolean hasOneOpening() {
        int openCount = 0;
        openCount = northOpening == null ? openCount : openCount + 1;
        openCount = southOpening == null ? openCount : openCount + 1;
        openCount = westOpening == null ? openCount : openCount + 1;
        openCount = eastOpening == null ? openCount : openCount + 1;
        boolean hasOneOpening = openCount == 1;
        return hasOneOpening;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomHints roomHints = (RoomHints) o;
        return Objects.equals(northWall, roomHints.northWall) && Objects.equals(
                southWall,
                roomHints.southWall
        ) && Objects.equals(westWall, roomHints.westWall) && Objects.equals(
                eastWall,
                roomHints.eastWall
        ) && Objects.equals(northOpening, roomHints.northOpening) && Objects.equals(
                southOpening,
                roomHints.southOpening
        ) && Objects.equals(westOpening, roomHints.westOpening) && Objects.equals(
                eastOpening,
                roomHints.eastOpening
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                northWall,
                southWall,
                westWall,
                eastWall,
                northOpening,
                southOpening,
                westOpening,
                eastOpening
        );
    }

    public Optional<InclusiveSpace> adjoinedTo(
            Position doorPos,
            RoomHints space
    ) {
        if (northOpening != null) {
            boolean joinW = false;
            boolean joinE = false;
            if (space.southWall != null) {
                joinW = space.southWall.westCorner.equals(northOpening.westCorner);
                joinE = space.southWall.eastCorner.equals(northOpening.eastCorner);
            }
            if (space.southOpening != null) {
                joinW = space.southOpening.westCorner.equals(northOpening.westCorner);
                joinE = space.southOpening.eastCorner.equals(northOpening.eastCorner);
            }
            Optional<InclusiveSpace> s = space.asSpace(RoomDetection.WallExclusion.allowSouthOpen());
            if (joinW && joinE && s.isPresent()) {
                return s;
            }
        }
        if (southOpening != null) {
            boolean joinW = false;
            boolean joinE = false;
            if (space.northWall != null) {
                joinW = space.northWall.westCorner.equals(southOpening.westCorner);
                joinE = space.northWall.eastCorner.equals(southOpening.eastCorner);
            }
            if (space.northOpening != null) {
                joinW = space.northOpening.westCorner.equals(southOpening.westCorner);
                joinE = space.northOpening.eastCorner.equals(southOpening.eastCorner);
            }
            Optional<InclusiveSpace> s = space.asSpace(RoomDetection.WallExclusion.allowNorthOpen());
            if (joinW && joinE && s.isPresent()) {
                return s;
            }
        }
        if (westOpening != null) {
            boolean joinN = false;
            boolean joinS = false;
            if (space.eastWall != null) {
                joinN = space.eastWall.northCorner.equals(westOpening.northCorner);
                joinS = space.eastWall.southCorner.equals(westOpening.southCorner);
            }
            if (space.eastOpening != null) {
                joinN = space.eastOpening.northCorner.equals(westOpening.northCorner);
                joinS = space.eastOpening.southCorner.equals(westOpening.southCorner);
            }
            Optional<InclusiveSpace> s = space.asSpace(RoomDetection.WallExclusion.allowEastOpen());
            if (joinN && joinS && s.isPresent()) {
                return s;
            }
        }
        if (eastOpening != null) {
            boolean joinN = false;
            boolean joinS = false;
            if (space.westWall != null) {
                joinN = space.westWall.northCorner.equals(eastOpening.northCorner);
                joinS = space.westWall.southCorner.equals(eastOpening.southCorner);
            }
            if (space.westOpening != null) {
                joinN = space.westOpening.northCorner.equals(eastOpening.northCorner);
                joinS = space.westOpening.southCorner.equals(eastOpening.southCorner);
            }
            Optional<InclusiveSpace> s = space.asSpace(RoomDetection.WallExclusion.allowWestOpen());
            if (joinN && joinS && s.isPresent()) {
                return s;
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

    public Wall<?> getWall(Direction d) {
        return switch (d) {
            case NORTH -> northWall;
            case SOUTH -> southWall;
            case WEST -> westWall;
            case EAST -> eastWall;
        };
    }

    public Wall<?> getOpening(Direction d) {
        return switch (d) {
            case NORTH -> northOpening;
            case SOUTH -> southOpening;
            case WEST -> westOpening;
            case EAST -> eastOpening;
        };
    }

    public RoomHints withWall(
            Direction n,
            @Nullable Wall<?> w
    ) {
        return switch (n) {
            case NORTH -> w == null ? withNorthWall(null) : withNorthWall(w.toXWall());
            case SOUTH -> w == null ? withSouthWall(null) : withSouthWall(w.toXWall());
            case WEST -> w == null ? withWestWall(null) : withWestWall(w.toZWall());
            case EAST -> w == null ? withEastWall(null) : withEastWall(w.toZWall());
        };
    }

    public RoomHints withOpening(
            Direction n,
            @Nullable Wall<?> w
    ) {
        return switch (n) {
            case NORTH -> w == null ? withNorthOpening(null) : withNorthOpening(w.toXWall());
            case SOUTH -> w == null ? withSouthOpening(null) : withSouthOpening(w.toXWall());
            case WEST -> w == null ? withWestOpening(null) : withWestOpening(w.toZWall());
            case EAST -> w == null ? withEastOpening(null) : withEastOpening(w.toZWall());
        };
    }
}
