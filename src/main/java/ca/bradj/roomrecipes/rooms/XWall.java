package ca.bradj.roomrecipes.rooms;

import ca.bradj.roomrecipes.core.space.Position;
import ca.bradj.roomrecipes.logic.interfaces.WallDetector;

import java.util.Objects;

// XWall is a wall that runs from west to east
public class XWall {
    public final Position westCorner;
    public final Position eastCorner;

    public XWall(
            Position westCorner,
            Position eastCorner
    ) {
        Position wc = westCorner;
        Position ec = eastCorner;
        if (wc.x > ec.x) {
            wc = eastCorner;
            ec = westCorner;
        }
        this.westCorner = wc;
        this.eastCorner = ec;
    }

    public int getLength() {
        return this.eastCorner.x - this.westCorner.x;
    }

    public XWall shortenWestEnd(int i) {
        return new XWall(this.westCorner.WithX(this.westCorner.x + i), this.eastCorner);
    }

    public XWall shortenEastEnd(int i) {
        return new XWall(this.westCorner, this.eastCorner.WithX(this.eastCorner.x - i));
    }

    public XWall shiftedNorthBy(int i) {
        return shiftedSouthBy(-i);
    }

    public XWall shiftedSouthBy(int i) {
        return new XWall(this.westCorner.offset(0, i), this.eastCorner.offset(0, i));
    }

    public int getZ() {
        return westCorner.z;
    }

    @Override
    public String toString() {
        return "XWall{" +
                "westCorner=" + westCorner +
                ", eastCorner=" + eastCorner +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XWall xWall = (XWall) o;
        return Objects.equals(westCorner, xWall.westCorner) && Objects.equals(
                eastCorner,
                xWall.eastCorner
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(westCorner, eastCorner);
    }

    public Position getMidpoint() {
        return new Position((westCorner.x + eastCorner.x) / 2, getZ());
    }

    public boolean sameWidth(XWall southOpening) {
        return this.westCorner.x == southOpening.westCorner.x && this.eastCorner.x == southOpening.eastCorner.x;
    }

    public boolean isSameContentAs(XWall shifted, WallDetector wd) {
        for (int i = 0; i < shifted.westCorner.x; i++) {
            boolean isWall = wd.IsWall(westCorner.offset(i, 0));
            boolean shiftedIsWall = wd.IsWall(shifted.westCorner.offset(i, 0));
            if (isWall != shiftedIsWall) {
                return false;
            }
        }
        return true;
    }

    public boolean isLargerThan(XWall xWall) {
        return eastCorner.x - westCorner.x > xWall.eastCorner.x - xWall.westCorner.x;
    }
}
