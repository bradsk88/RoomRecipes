package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.core.space.Position;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.Collection;

public class Crawl {
    private final Position checkPos;
    private final boolean isDiagonal;
    private final String dirDesc;
    private final Checks checks;
    private final ImmutableList<Position> checkedAlready;
    private Direction heading;

    public Crawl(
            Position checkPos,
            Direction heading,
            boolean isDiagonal,
            String dirDesc,
            Checks checks,
            ImmutableList<Position> checkedAlready
    ) {
        this.checkPos = checkPos;
        this.isDiagonal = isDiagonal;
        this.heading = heading;
        this.dirDesc = dirDesc;
        this.checks = checks;
        this.checkedAlready = checkedAlready;
    }

    @Override
    public String toString() {
        return String.format("Checking %s (%s)", dirDesc, checkPos.getUIString());
    }

    public ImmutableList<Crawl> getNextSteps() {
        if (checks.isWall(checkPos)) {
            if (isDiagonal) {
                // Give the non-diagonal crawls a chance to catch up,
                // Otherwise, diagonal crawls will always win the "race" around
                // the room and corner blocks will never be found.
                return ImmutableList.of(this);
            }
                return Crawl.forwardSpread(
                        checkPos, heading, checks,
                        getCheckedPositions()
                );
        }
        return null;
    }

    public static ImmutableList<Crawl> forwardSpread(
            Position checkPos,
            Direction heading,
            Checks checks,
            Collection<Position> alreadyChecked
    ) {
        Crawl rightTurn = Crawl.from(checkPos).inDirection(heading.cw()).build(checks, alreadyChecked);
        Crawl leftTurn = Crawl.from(checkPos).inDirection(heading.ccw()).build(checks, alreadyChecked);
        Crawl straight = Crawl.from(checkPos).inDirection(heading).build(checks, alreadyChecked);
        Crawl rightDiag = Crawl.from(checkPos).inDirection(heading, heading.cw()).build(checks, alreadyChecked);
        Crawl leftDiag = Crawl.from(checkPos).inDirection(heading, heading.ccw()).build(checks, alreadyChecked);

        ImmutableList.Builder<Crawl> b = ImmutableList.builder();
        if (!alreadyChecked.contains(rightTurn.checkPos)) {
            b.add(rightTurn);
        }
        if (!alreadyChecked.contains(leftTurn.checkPos)) {
            b.add(leftTurn);
        }
        if (!alreadyChecked.contains(straight.checkPos)) {
            b.add(straight);
        }
        if (!alreadyChecked.contains(rightDiag.checkPos)) {
            b.add(rightDiag);
        }
        if (!alreadyChecked.contains(leftDiag.checkPos)) {
            b.add(leftDiag);
        }
        return b.build();
    }

    public ImmutableSet<Position> getCheckedPositions() {
        return ImmutableSet.<Position>builder()
                           .addAll(checkedAlready)
                           .add(checkPos)
                           .build();
    }

    public boolean isOrigin() {
        return !isDiagonal && checks.isOrigin(checkPos);
    }

    public static final class Builder {

        private final Position prevPos;
        private Direction dir;
        private Direction dir2;

        public Builder(Position prevPos) {
            this.prevPos = prevPos;
        }

        public Builder inDirection(Direction dir90) {
            this.dir = dir90;
            return this;
        }

        public Builder inDirection(
                Direction dir1,
                Direction dir2
        ) {
            this.dir = dir1;
            this.dir2 = dir2;
            return this;
        }

        public Crawl build(
                Checks doneCondition,
                Collection<Position> alreadyChecked
        ) {
            Position startPos = prevPos.relative(dir);
            String dirDesc = dir.toString();
            if (dir2 != null) {
                startPos = startPos.relative(dir2);
                dirDesc = Direction.toString(dir, dir2);
            }
            return new Crawl(startPos, dir, dir2 != null, dirDesc, doneCondition, ImmutableList.copyOf(alreadyChecked));
        }
    }

    public static Builder from(Position prevPos) {
        return new Builder(prevPos);
    }

    public interface Checks {
        boolean isWall(Position pos);

        boolean isOrigin(Position pos);
    }
}
