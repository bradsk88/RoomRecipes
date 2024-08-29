package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.core.space.Position;
import ca.bradj.roomrecipes.logic.interfaces.WallDetector;

import java.util.HashSet;
import java.util.function.Predicate;

public class TestHelpers {

    public static class TrackedWD implements WallDetector {

        private final String[][] map;
        private final HashSet<Position> checks = new HashSet<>();

        public TrackedWD(String[][] map) {
            this.map = map;
        }

        @Override
        public boolean IsWall(Position p) {
            Predicate<Position> cz = dp -> {
                if (dp.x < 0 || dp.z < 0) {
                    return false;
                }
                if (dp.x >= map[0].length || dp.z >= map.length) {
                    return false;
                }
                String v = map[dp.z][dp.x];
                return "W".equals(v) || "D".equals(v) || "w".equals(v);
            };
            if (!checks.add(p)) {
                throw new AssertionError("Double-checked spot - inefficient!");
            }
            return cz.test(p);
        }
    }

    public static TrackedWD WD(String[][] map) {
        return new TrackedWD(map);
    }


}
