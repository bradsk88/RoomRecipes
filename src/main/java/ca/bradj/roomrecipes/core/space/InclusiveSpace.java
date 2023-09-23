package ca.bradj.roomrecipes.core.space;

import ca.bradj.roomrecipes.logic.InclusiveSpaces;
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
        // TODO: Ensure space corners are aligned correctly.
        this.aa = new Position(Math.min(aa.x, bb.x), Math.min(aa.z, bb.z));
        this.bb = new Position(Math.max(aa.x, bb.x), Math.max(aa.z, bb.z));
    }

    private final Position aa;
    private final Position bb;

    @Override
    public String toString() {
        return "InclusiveSpace{" +
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

    public InclusiveSpace chopOff(InclusiveSpace otherSpace) {
        if (!InclusiveSpaces.overlapOnXZPlane(this, otherSpace)) {
            // If the otherSpace does not overlap with this space, return a copy of this space
            return new InclusiveSpace(aa, bb);
        }

        // Calculate the overlapping region between the two spaces
        int overlapNorthZ = Math.max(getNorthZ(), otherSpace.getNorthZ());
        int overlapSouthZ = Math.min(getSouthZ(), otherSpace.getSouthZ());
        int overlapWestX = Math.max(getWestX(), otherSpace.getWestX());
        int overlapEastX = Math.min(getEastX(), otherSpace.getEastX());

        // Determine which side(s) of this space need to be chopped off
        boolean chopNorth = overlapNorthZ == getNorthZ();
        boolean chopSouth = overlapSouthZ == getSouthZ();
        boolean chopWest = overlapWestX == getWestX();
        boolean chopEast = overlapEastX == getEastX();

        // Calculate the new bounding box
        Position newAA = new Position(
                chopWest ? overlapEastX : getWestX(),
                chopNorth ? overlapSouthZ : getNorthZ()
        );
        Position newBB = new Position(
                chopEast ? overlapWestX : getEastX(),
                chopSouth ? overlapNorthZ : getSouthZ()
        );

        return new InclusiveSpace(newAA, newBB);
    }
}
