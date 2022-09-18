package com.klinksoftware.rag.utility;

public class RagQuaternion {

    public float x, y, z, w;

    public RagQuaternion() {
        setIdentity();
    }

    public RagQuaternion(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public void setIdentity() {
        x = 0;
        y = 0;
        z = 0;
        w = 1;
    }

    public void setFromValues(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public void setFromEuler(float rx, float ry, float rz) {
        double xRad, yRad, zRad;
        float cy, sy, cp, sp, cr, sr;

        xRad = (double) rx * (Math.PI / 180.0);
        yRad = (double) ry * (Math.PI / 180.0);
        zRad = (double) rz * (Math.PI / 180.0);

        cy = (float) Math.cos(zRad * 0.5);
        sy = (float) Math.sin(zRad * 0.5);
        cp = (float) Math.cos(yRad * 0.5);
        sp = (float) Math.sin(yRad * 0.5);
        cr = (float) Math.cos(xRad * 0.5);
        sr = (float) Math.sin(xRad * 0.5);

        x = (sr * cp * cy) - (cr * sp * sy);
        y = (cr * sp * cy) + (sr * cp * sy);
        z = (cr * cp * sy) - (sr * sp * cy);
        w = (cr * cp * cy) + (sr * sp * sy);
    }

    public void multiply(RagQuaternion quat) {
        float x2 = (x * quat.w) + (y * quat.z) - (z * quat.y) + (w * quat.x);
        float y2 = (-x * quat.z) + (y * quat.w) + (z * quat.x) + (w * quat.y);
        float z2 = (x * quat.y) - (y * quat.x) + (z * quat.w) + (w * quat.z);
        float w2 = (-x * quat.x) - (y * quat.y) - (z * quat.z) + (w * quat.w);

        x = x2;
        y = y2;
        z = z2;
        w = w2;
    }

    public RagQuaternion copy() {
        return (new RagQuaternion(x, y, z, w));
    }
}
