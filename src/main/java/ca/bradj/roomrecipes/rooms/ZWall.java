package ca.bradj.roomrecipes.rooms;

import ca.bradj.roomrecipes.core.space.Position;
import ca.bradj.roomrecipes.logic.RoomDetector;

import java.util.Optional;

public class ZWall {
    public final Position northCorner;
    public final Position southCorner;

    public ZWall(
            Position northCorner,
            Position southCorner
    ) {
        Position nc = northCorner;
        Position sc = southCorner;
        if (nc.z > sc.z) {
            nc = southCorner;
            sc = northCorner;
        }
        this.northCorner = nc;
        this.southCorner = sc;
    }
    public static Optional<ZWall> northFromCorner(
            RoomDetector.WallDetector wd,
            Position cornerPos,
            int maxDistFromCorner
    ) {
        int southCornerZ = -Integer.MAX_VALUE, northCornerZ = Integer.MAX_VALUE;
        boolean started = false;
        for (int i = 0; i < maxDistFromCorner; i++) {
            Position op = cornerPos.offset(0, 0, -i);
            if (wd.IsWall(op)) {
                started = true;
                southCornerZ = Math.max(southCornerZ, op.z);
                northCornerZ = Math.min(northCornerZ, op.z);
            } else if (started) {
                break;
            }
        }
        if (!started) {
            return Optional.empty();
        }
        if (Math.abs(northCornerZ - southCornerZ) < 2) {
            return Optional.empty();
        }
        return Optional.of(
                new ZWall(cornerPos.WithZ(southCornerZ), cornerPos.WithZ(northCornerZ))
        );
    }

    public static Optional<ZWall> southFromCorner(
            RoomDetector.WallDetector wd,
            Position doorPos,
            int maxDistFromCorner
    ) {
        int southCornerZ = -Integer.MAX_VALUE, northCornerZ = Integer.MAX_VALUE;
        boolean started = false;
        for (int i = 0; i < maxDistFromCorner; i++) {
            Position op = doorPos.offset(0, 0, i);
            if (wd.IsWall(op)) {
                started = true;
                southCornerZ = Math.max(southCornerZ, op.z);
                northCornerZ = Math.min(northCornerZ, op.z);
            } else if (started) {
                break;
            }
        }
        if (!started) {
            return Optional.empty();
        }
        if (Math.abs(northCornerZ - southCornerZ) < 2) {
            return Optional.empty();
        }
        return Optional.of(
                new ZWall(doorPos.WithZ(southCornerZ), doorPos.WithZ(northCornerZ))
        );
    }

    public int getLength() {
        return southCorner.z - northCorner.z;
    }

    public ZWall shortenSouthEnd(int i) {
        return new ZWall(northCorner, southCorner.WithZ(southCorner.z - i));
    }
    public ZWall shortenNorthEnd(int i) {
        return new ZWall(northCorner.WithZ(northCorner.z + i), southCorner);
    }
}
