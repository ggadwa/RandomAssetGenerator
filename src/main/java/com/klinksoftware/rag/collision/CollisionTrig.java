package com.klinksoftware.rag.collision;

import com.klinksoftware.rag.utility.RagBound;
import com.klinksoftware.rag.utility.RagPoint;

public class CollisionTrig {

    private RagPoint v0, v1, v2;
    private RagPoint normal;
    private RagPoint vct1, vct2, perpVector, lineToTrigPointVector, lineToTrigPerpVector;
    private RagBound xBound, yBound, zBound;

    public CollisionTrig(RagPoint v0, RagPoint v1, RagPoint v2, RagPoint normal) {
        this.v0 = v0;
        this.v1 = v1;
        this.v2 = v2;

        this.normal = normal;

        // bounds
        xBound = new RagBound(v0.x, v1.x);
        xBound.adjust(v2.x);

        yBound = new RagBound(v0.y, v1.y);
        yBound.adjust(v2.y);

        zBound = new RagBound(v0.z, v1.z);
        zBound.adjust(v2.z);

        // triangle vectors for ray tracing
        vct1 = new RagPoint((v1.x - v0.x), (v1.y - v0.y), (v1.z - v0.z));
        vct2 = new RagPoint((v2.x - v0.x), (v2.y - v0.y), (v2.z - v0.z));

        // precalc these so ray tracing doesn't
        // keep creating stuff to garbage collect
        perpVector = new RagPoint(0.0f, 0.0f, 0.0f);
        lineToTrigPointVector = new RagPoint(0.0f, 0.0f, 0.0f);
        lineToTrigPerpVector = new RagPoint(0.0f, 0.0f, 0.0f);
    }

    public boolean overlapBounds(RagBound xLapBound, RagBound yLapBound, RagBound zLapBound) {
        if (xBound.min >= xLapBound.max) {
            return (false);
        }
        if (xBound.max <= xLapBound.min) {
            return (false);
        }
        if (yBound.min >= yLapBound.max) {
            return (false);
        }
        if (yBound.max <= yLapBound.min) {
            return (false);
        }
        if (zBound.min >= zLapBound.max) {
            return (false);
        }
        return (!(zBound.max <= zLapBound.min));
    }

    public boolean rayOverlapBounds(RagPoint pnt, RagPoint ray) {
        float k;

        k = pnt.x + ray.x;
        if ((pnt.x < xBound.min) && (k < xBound.min)) {
            return (false);
        }
        if ((pnt.x > xBound.max) && (k > xBound.max)) {
            return (false);
        }

        k = pnt.y + ray.y;
        if ((pnt.y < yBound.min) && (k < yBound.min)) {
            return (false);
        }
        if ((pnt.y > yBound.max) && (k > yBound.max)) {
            return (false);
        }

        k = pnt.z + ray.z;
        if ((pnt.z < zBound.min) && (k < zBound.min)) {
            return (false);
        }
        return (!((pnt.z > zBound.max) && (k > zBound.max)));
    }

    public boolean rayTrace(RagPoint rayPnt, RagPoint rayVct, RagPoint hitPnt) {
        float det, invDet, t, u, v;

        // calculate the cross product and
        // then the inner product to get the
        // determinate
        perpVector.x = (rayVct.y * vct2.z) - (vct2.y * rayVct.z);
        perpVector.y = (rayVct.z * vct2.x) - (vct2.z * rayVct.x);
        perpVector.z = (rayVct.x * vct2.y) - (vct2.x * rayVct.y);

        det = (vct1.x * perpVector.x) + (vct1.y * perpVector.y) + (vct1.z * perpVector.z);

        // is line on the same plane as triangle?
        if ((det > -0.00001) && (det < 0.00001)) {
            return (false);
        }

        // get the inverse determinate
        invDet = 1.0f / det;

        // calculate triangle U and test
        // using the vector from spt to tpt_0
        // and the inner product of that result and
        // the perpVector
        lineToTrigPointVector.x = rayPnt.x - v0.x;
        lineToTrigPointVector.y = rayPnt.y - v0.y;
        lineToTrigPointVector.z = rayPnt.z - v0.z;

        u = invDet * ((lineToTrigPointVector.x * perpVector.x) + (lineToTrigPointVector.y * perpVector.y) + (lineToTrigPointVector.z * perpVector.z));
        if ((u < 0.0f) || (u > 1.0f)) {
            return (false);
        }

        // calculate triangle V and test
        // using the cross product of lineToTrigPointVector
        // and vct1 and the inner product of that result and rayVct
        lineToTrigPerpVector.x = (lineToTrigPointVector.y * vct1.z) - (vct1.y * lineToTrigPointVector.z);
        lineToTrigPerpVector.y = (lineToTrigPointVector.z * vct1.x) - (vct1.z * lineToTrigPointVector.x);
        lineToTrigPerpVector.z = (lineToTrigPointVector.x * vct1.y) - (vct1.x * lineToTrigPointVector.y);

        v = invDet * ((rayVct.x * lineToTrigPerpVector.x) + (rayVct.y * lineToTrigPerpVector.y) + (rayVct.z * lineToTrigPerpVector.z));
        if ((v < 0.0f) || ((u + v) > 1.0f)) {
            return (false);
        }

        // get line T for point(t) =  start_point + (vector*t)
        // use the inner product of vct2 and lineToTrigPerpVector
        // -t are on the negative vector behind the point, so ignore
        t = invDet * ((vct2.x * lineToTrigPerpVector.x) + (vct2.y * lineToTrigPerpVector.y) + (vct2.z * lineToTrigPerpVector.z));
        if (t < 0.0f) {
            return (false);
        }

        // if past ray, ignore
        if (t >= 1.0f) {
            return (false);
        }

        // get point on line of intersection
        hitPnt.x = rayPnt.x + (rayVct.x * t);
        hitPnt.y = rayPnt.y + (rayVct.y * t);
        hitPnt.z = rayPnt.z + (rayVct.z * t);

        return (true);
    }

}
