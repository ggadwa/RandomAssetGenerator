package com.klinksoftware.rag.utility;

public class RagPlane {

    public float a, b, c, d;

    public RagPlane(float a, float b, float c, float d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    public void normalize() {
        float f;

        f = (float) Math.sqrt((a * a) + (b * b) + (c * c));
        if (f == 0.0f) {
            return;
        }

        a /= f;
        b /= f;
        c /= f;
        d /= f;
    }

    public boolean boundBoxOutsidePlane(RagBound xBound, RagBound yBound, RagBound zBound) {
        float xMin = xBound.min;
        float yMin = yBound.min;
        float zMin = zBound.min;

        float xMax = xBound.max;
        float yMax = yBound.max;
        float zMax = zBound.max;

        if (((a * xMin) + (b * yMin) + (c * zMin) + d) > 0.0f) {
            return (true);
        }
        if (((a * xMax) + (b * yMin) + (c * zMin) + d) > 0.0f) {
            return (true);
        }
        if (((a * xMin) + (b * yMax) + (c * zMin) + d) > 0.0f) {
            return (true);
        }
        if (((a * xMax) + (b * yMax) + (c * zMin) + d) > 0.0f) {
            return (true);
        }
        if (((a * xMin) + (b * yMin) + (c * zMax) + d) > 0.0f) {
            return (true);
        }
        if (((a * xMax) + (b * yMin) + (c * zMax) + d) > 0.0f) {
            return (true);
        }
        if (((a * xMin) + (b * yMax) + (c * zMax) + d) > 0.0f) {
            return (true);
        }
        if (((a * xMax) + (b * yMax) + (c * zMax) + d) > 0.0f) {
            return (true);
        }

        return (false);
    }
}
