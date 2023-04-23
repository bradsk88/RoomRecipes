package ca.bradj.roomrecipes.core.space;

import ca.bradj.roomrecipes.rooms.XWall;
import ca.bradj.roomrecipes.rooms.ZWall;

import java.util.Objects;

// InclusiveSpace is defined by two corner positions. An inclusive space is the
// cube between the two positions, up to the outermost corner of those corner blocks.
public class InclusiveSpace {

    public InclusiveSpace(
            Position aa,
            Position bb
    ) {
        this.aa = aa;
        this.bb = bb;
    }

    private final Position aa;
    private final Position bb;

    @Override
    public String toString() {
        return "BoundingBox{" +
                "aa=" + aa +
                ", bb=" + bb +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InclusiveSpace that = (InclusiveSpace) o;
        return Objects.equals(aa, that.aa) && Objects.equals(bb, that.bb);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aa, bb);
    }

    public Position getCornerA() {
        return aa;
    }

    public Position getCornerB() {
        return bb;
    }

    public int getNorthZ() {
        return Math.min(aa.z, bb.z);
    }

    public int getSouthZ() {
        return Math.max(aa.z, bb.z);
    }

    public ZWall getWestZWall() {
        return new ZWall(
                new Position(
                        Math.min(aa.x, bb.x),
                        getNorthZ()
                ),
                new Position(
                        Math.min(aa.x, bb.x),
                        getSouthZ()
                )
        );
    }

    public ZWall getEastZWall() {
        return new ZWall(
                new Position(
                        Math.max(aa.x, bb.x),
                        getNorthZ()
                ),
                new Position(
                        Math.max(aa.x, bb.x),
                        getSouthZ()
                )
        );
    }

    public XWall getNorthXWall() {
        return new XWall(
                new Position(
                        getWestX(),
                        Math.min(aa.z, bb.z)
                ),
                new Position(
                        getEastX(),
                        Math.min(aa.z, bb.z)
                )
        );
    }

    public int getWestX() {
        return Math.min(aa.x, bb.x);
    }

    public int getEastX() {
        return Math.max(aa.x, bb.x);
    }

    public XWall getSouthXWall() {
        return new XWall(
                new Position(
                        getWestX(),
                        Math.max(aa.z, bb.z)
                ),
                new Position(
                        getEastX(),
                        Math.max(aa.z, bb.z)
                )
        );
    }
}
