package com.klinksoftware.rag.utility;

public class RagPoint
{
    public float x, y, z;

    public RagPoint(float x, float y, float z) {
        this.x=x;
        this.y=y;
        this.z=z;
    }

    public void setFromPoint(RagPoint pnt) {
        this.x=pnt.x;
        this.y=pnt.y;
        this.z=pnt.z;
    }

    public void setFromValues(float x, float y, float z) {
        this.x=x;
        this.y=y;
        this.z=z;
    }

    public void addFromValues(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }

    public void setFromSubPoint(RagPoint p1, RagPoint p2) {
        this.x=p1.x-p2.x;
        this.y=p1.y-p2.y;
        this.z=p1.z-p2.z;
    }

    public void setFromScale(RagPoint p, float f) {
        this.x=p.x*f;
        this.y=p.y*f;
        this.z=p.z*f;
    }

    public void setFromScaleNoY(RagPoint p, float f) {
        this.x = p.x * f;
        this.y = 0.0f;
        this.z = p.z * f;
    }

    public void addPoint(RagPoint p) {
        this.x+=p.x;
        this.y+=p.y;
        this.z+=p.z;
    }

    public void subPoint(RagPoint p) {
        this.x-=p.x;
        this.y-=p.y;
        this.z-=p.z;
    }

    public void setIfMin(RagPoint p) {
        if (p.x < this.x) {
            this.x = p.x;
        }
        if (p.y < this.y) {
            this.y = p.y;
        }
        if (p.z < this.z) {
            this.z = p.z;
        }
    }

    public void setIfMax(RagPoint p) {
        if (p.x > this.x) {
            this.x = p.x;
        }
        if (p.y > this.y) {
            this.y = p.y;
        }
        if (p.z > this.z) {
            this.z = p.z;
        }
    }

    public void scale(float f) {
        x*=f;
        y*=f;
        z*=f;
    }

    public float distance(RagPoint pnt) {
        float px, py, pz;

        px=x-pnt.x;
        py=y-pnt.y;
        pz=z-pnt.z;
        return((float)Math.sqrt((px*px)+(py*py)+(pz*pz)));
    }

    public void normalize() {
        float f;

        f=(float)Math.sqrt((x*x)+(y*y)+(z*z));
        if (f!=0.0f) f=1.0f/f;

        x*=f;
        y*=f;
        z*=f;
    }

    public void normalize2D() {
        float       f;

        f=(float)Math.sqrt((x*x)+(y*y));
        if (f!=0.0f) f=1.0f/f;

        x*=f;
        y*=f;
    }

    public float dot(RagPoint vct) {
        return((x*vct.x)+(y*vct.y)+(z*vct.z));
    }

    public void tween(RagPoint pnt1, RagPoint pnt2, float factor) {
        x = pnt1.x + (pnt2.x - pnt1.x) * factor;
        y = pnt1.y + (pnt2.y - pnt1.y) * factor;
        z = pnt1.z + (pnt2.z - pnt1.z) * factor;
    }


    public void rotateAroundPoint(RagPoint centerPnt, RagPoint ang) {
        float rd, sn, cs, rx, ry, rz;

        if (centerPnt!=null) {
            x-=centerPnt.x;
            y-=centerPnt.y;
            z-=centerPnt.z;
        }

            // rotate X

        if (ang.x!=0.0f) {
            rd=ang.x*((float)Math.PI/180.0f);
            sn=(float)Math.sin(rd);
            cs=(float)Math.cos(rd);

            ry=(y*cs)-(z*sn);
            rz=(y*sn)+(z*cs);

            y=ry;
            z=rz;
        }

            // rotate Y

        if (ang.y!=0.0f) {
            rd=ang.y*((float)Math.PI/180.0f);
            sn=(float)Math.sin(rd);
            cs=(float)Math.cos(rd);

            rx=(z*sn)+(x*cs);
            rz=(z*cs)-(x*sn);

            x=rx;
            z=rz;
        }

            // rotate Z

        if (ang.z!=0.0f) {
            rd=ang.z*((float)Math.PI/180.0f);
            sn=(float)Math.sin(rd);
            cs=(float)Math.cos(rd);

            rx=(x*cs)-(y*sn);
            ry=(x*sn)+(y*cs);

            x=rx;
            y=ry;
        }

        if (centerPnt!=null) {
            x+=centerPnt.x;
            y+=centerPnt.y;
            z+=centerPnt.z;
        }
    }

    public void rotateX(float fx) {
        float rd, sn, cs, ry, rz;

        rd=fx*((float)Math.PI/180.0f);
        sn=(float)Math.sin(rd);
        cs=(float)Math.cos(rd);

        ry=(y*cs)-(z*sn);
        rz=(y*sn)+(z*cs);

        y=ry;
        z=rz;
    }

    public void rotateY(float fy) {
        float rd, sn, cs, rx, rz;

        rd=fy*((float)Math.PI/180.0f);
        sn=(float)Math.sin(rd);
        cs=(float)Math.cos(rd);

        rx=(z*sn)+(x*cs);
        rz=(z*cs)-(x*sn);

        x=rx;
        z=rz;
    }

    public void rotateZ(float fx) {
        float rd, sn, cs, ry, rx;

        rd = fx * ((float) Math.PI / 180.0f);
        sn = (float) Math.sin(rd);
        cs = (float) Math.cos(rd);

        ry = (y * cs) - (x * sn);
        rx = (y * sn) + (x * cs);

        y = ry;
        x = rx;
    }

    public void matrixMultiply(RagMatrix4f mat) {
        float mx=(x*mat.data[0])+(y*mat.data[4])+(z*mat.data[8])+mat.data[12];
        float my=(x*mat.data[1])+(y*mat.data[5])+(z*mat.data[9])+mat.data[13];
        float mz=(x*mat.data[2])+(y*mat.data[6])+(z*mat.data[10])+mat.data[14];

        x=mx;
        y=my;
        z=mz;
    }

    public RagPoint copy() {
        return(new RagPoint(x,y,z));
    }

    public boolean isCloseEqual(RagPoint pnt) {
        return (((int) (pnt.x * 100.0f) == (int) (x * 100.0f)) && ((int) (pnt.y * 100.0f) == (int) (y * 100.0f)) && ((int) (pnt.z * 100.0f) == (int) (z * 100.0f)));
    }

    public boolean isCloseEqualIgnoreY(RagPoint pnt) {
        return (((int) (pnt.x * 100.0f) == (int) (x * 100.0f)) && ((int) (pnt.z * 100.0f) == (int) (z * 100.0f)));
    }

    public boolean hasXZValues() {
        return ((x != 0.0f) || (z != 0.0f));
    }

    public boolean isZero() {
        return ((x == 0.0f) && (y == 0.0f) && (z == 0.0f));
    }
}
