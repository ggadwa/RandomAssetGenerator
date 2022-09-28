package com.klinksoftware.rag.utility;

public class RagMatrix4f {

    public float[] data;

    public RagMatrix4f() {
        data=new float[16];
        setIdentity();
    }

    // matrix types

    public void setIdentity() {
        data[0]=1.0f;
        data[1]=0.0f;
        data[2]=0.0f;
        data[3]=0.0f;
        data[4]=0.0f;
        data[5]=1.0f;
        data[6]=0.0f;
        data[7]=0.0f;
        data[8]=0.0f;
        data[9]=0.0f;
        data[10]=1.0f;
        data[11]=0.0f;
        data[12]=0.0f;
        data[13]=0.0f;
        data[14]=0.0f;
        data[15]=1.0f;
    }

    public void setFromMatrix(RagMatrix4f mat) {
        System.arraycopy(mat.data, 0, data, 0, 16);
    }

    public void setPerspectiveMatrix(float viewFOV,float viewAspect,float glNearZ,float glFarZ) {
        float fov=1.0f/(float)Math.tan(viewFOV*0.5f);
        float dist=1.0f/(glNearZ-glFarZ);

            // create the perspective matrix

        data[0]=fov/viewAspect;
        data[1]=0.0f;
        data[2]=0.0f;
        data[3]=0.0f;
        data[4]=0.0f;
        data[5]=fov;
        data[6]=0.0f;
        data[7]=0.0f;
        data[8]=0.0f;
        data[9]=0.0f;
        data[10]=(glFarZ+glNearZ)*dist;
        data[11]=-1.0f;
        data[12]=0.0f;
        data[13]=0.0f;
        data[14]=((glFarZ*glNearZ)*2.0f)*dist;
        data[15]=0.0f;

            // now translate it for the near_z

        data[12]+=(data[8]*glNearZ);
        data[13]+=(data[9]*glNearZ);
        data[14]+=(data[10]*glNearZ);
        data[15]+=(data[11]*glNearZ);
    }

    public void setLookAtMatrix(RagPoint eyePnt,RagPoint centerPnt,RagPoint lookAtUpVector) {
        float x0,x1,x2,y0,y1,y2,z0,z1,z2;
        float f;

        z0=eyePnt.x-centerPnt.x;
        z1=eyePnt.y-centerPnt.y;
        z2=eyePnt.z-centerPnt.z;

        f=(float)Math.sqrt((z0*z0)+(z1*z1)+(z2*z2));
        f=1.0f/f;
        z0*=f;
        z1*=f;
        z2*=f;

        x0=(lookAtUpVector.y*z2)-(lookAtUpVector.z*z1);
        x1=(lookAtUpVector.z*z0)-(lookAtUpVector.x*z2);
        x2=(lookAtUpVector.x*z1)-(lookAtUpVector.y*z0);

        f=(float)Math.sqrt((x0*x0)+(x1*x1)+(x2*x2));
        if (f!=0.0f) f=1.0f/f;
        x0*=f;
        x1*=f;
        x2*=f;

        y0=(z1*x2)-(z2*x1);
        y1=(z2*x0)-(z0*x2);
        y2=(z0*x1)-(z1*x0);

        f=(float)Math.sqrt((y0*y0)+(y1*y1)+(y2*y2));
        if (f!=0.0f) f=1.0f/f;
        y0*=f;
        y1*=f;
        y2*=f;

        data[0]=x0;
        data[1]=y0;
        data[2]=z0;
        data[3]=0.0f;
        data[4]=x1;
        data[5]=y1;
        data[6]=z1;
        data[7]=0.0f;
        data[8]=x2;
        data[9]=y2;
        data[10]=z2;
        data[11]=0.0f;
        data[12]=-((x0*eyePnt.x)+(x1*eyePnt.y)+(x2*eyePnt.z));
        data[13]=-((y0*eyePnt.x)+(y1*eyePnt.y)+(y2*eyePnt.z));
        data[14]=-((z0*eyePnt.x)+(z1*eyePnt.y)+(z2*eyePnt.z));
        data[15]=1.0f;
    }

    public void setOrthoMatrix(int screenWidth,int screenHeight,float glNearZ,float glFarZ) {
        float horz=1.0f/(float)screenWidth;
        float vert=1.0f/(float)screenHeight;
        float dist=1.0f/(glNearZ-glFarZ);

        data[0]=horz*2.0f;
        data[1]=0.0f;
        data[2]=0.0f;
        data[3]=0.0f;
        data[4]=0.0f;
        data[5]=vert*-2.0f;
        data[6]=0.0f;
        data[7]=0.0f;
        data[8]=0.0f;
        data[9]=0.0f;
        data[10]=dist*2.0f;
        data[11]=0.0f;
        data[12]=-1.0f; // wid*-horz; -- these will always equal these numbers,
        data[13]=1.0f;  // high*vert; -- but leave in the code for readability
        data[14]=(glFarZ+glNearZ)*dist;
        data[15]=1.0f;
    }

    // translations

    public void setTranslationFromPoint(RagPoint pnt)
    {
        setIdentity();

        data[12]=pnt.x;
	data[13]=pnt.y;
	data[14]=pnt.z;
    }

    public void setNegativeTranslationFromPoint(RagPoint pnt) {
        setIdentity();

        data[12] = -pnt.x;
        data[13] = -pnt.y;
        data[14] = -pnt.z;
    }

    // rotations

    public void setRotationFromXAngle(float xAng)
    {
        float rad=xAng*(float)(Math.PI/180.0);

        setIdentity();

        data[5]=data[10]=(float)Math.cos(rad);
        data[6]=(float)Math.sin(rad);
        data[9]=-data[6];
    }

    public void setRotationFromYAngle(float yAng)
    {
        float rad=yAng*(float)(Math.PI/180.0);

        setIdentity();

        data[0]=data[10]=(float)Math.cos(rad);
        data[8]=(float)Math.sin(rad);
        data[2]=-data[8];
    }

    public void setRotationFromQuaternion(RagQuaternion quant) {
        float xx = quant.x * quant.x;
        float xy = quant.x * quant.y;
        float xz = quant.x * quant.z;
        float xw = quant.x * quant.w;
        float yy = quant.y * quant.y;
        float yz = quant.y * quant.z;
        float yw = quant.y * quant.w;
        float zz = quant.z * quant.z;
        float zw = quant.z * quant.w;

        setIdentity();

        data[0] = 1.0f - (2.0f * (yy + zz));
        data[4] = 2.0f * (xy - zw);
        data[8] = 2.0f * (xz + yw);
        data[1] = 2.0f * (xy + zw);
        data[5] = 1.0f - (2.0f * (xx + zz));
        data[9] = 2.0f * (yz - xw);
        data[2] = 2.0f * (xz - yw);
        data[6] = 2.0f * (yz + xw);
        data[10] = 1.0f - (2.0f * (xx + yy));
    }

    // math

    public void multiply(RagMatrix4f mat)
    {
        float d0=(data[0]*mat.data[0])+(data[4]*mat.data[1])+(data[8]*mat.data[2])+(data[12]*mat.data[3]);
        float d4=(data[0]*mat.data[4])+(data[4]*mat.data[5])+(data[8]*mat.data[6])+(data[12]*mat.data[7]);
        float d8=(data[0]*mat.data[8])+(data[4]*mat.data[9])+(data[8]*mat.data[10])+(data[12]*mat.data[11]);
        float d12=(data[0]*mat.data[12])+(data[4]*mat.data[13])+(data[8]*mat.data[14])+(data[12]*mat.data[15]);

        float d1=(data[1]*mat.data[0])+(data[5]*mat.data[1])+(data[9]*mat.data[2])+(data[13]*mat.data[3]);
        float d5=(data[1]*mat.data[4])+(data[5]*mat.data[5])+(data[9]*mat.data[6])+(data[13]*mat.data[7]);
        float d9=(data[1]*mat.data[8])+(data[5]*mat.data[9])+(data[9]*mat.data[10])+(data[13]*mat.data[11]);
        float d13=(data[1]*mat.data[12])+(data[5]*mat.data[13])+(data[9]*mat.data[14])+(data[13]*mat.data[15]);

        float d2=(data[2]*mat.data[0])+(data[6]*mat.data[1])+(data[10]*mat.data[2])+(data[14]*mat.data[3]);
        float d6=(data[2]*mat.data[4])+(data[6]*mat.data[5])+(data[10]*mat.data[6])+(data[14]*mat.data[7]);
        float d10=(data[2]*mat.data[8])+(data[6]*mat.data[9])+(data[10]*mat.data[10])+(data[14]*mat.data[11]);
        float d14=(data[2]*mat.data[12])+(data[6]*mat.data[13])+(data[10]*mat.data[14])+(data[14]*mat.data[15]);

        float d3=(data[3]*mat.data[0])+(data[7]*mat.data[1])+(data[11]*mat.data[2])+(data[15]*mat.data[3]);
        float d7=(data[3]*mat.data[4])+(data[7]*mat.data[5])+(data[11]*mat.data[6])+(data[15]*mat.data[7]);
        float d11=(data[3]*mat.data[8])+(data[7]*mat.data[9])+(data[11]*mat.data[10])+(data[15]*mat.data[11]);
        float d15=(data[3]*mat.data[12])+(data[7]*mat.data[13])+(data[11]*mat.data[14])+(data[15]*mat.data[15]);

        data[0]=d0;
        data[1]=d1;
        data[2]=d2;
        data[3]=d3;
        data[4]=d4;
        data[5]=d5;
        data[6]=d6;
        data[7]=d7;
        data[8]=d8;
        data[9]=d9;
        data[10]=d10;
        data[11]=d11;
        data[12]=d12;
        data[13]=d13;
        data[14]=d14;
        data[15]=d15;
    }
    public void setFromMultiply(RagMatrix4f mat1, RagMatrix4f mat2) {
        data[0] = (mat1.data[0] * mat2.data[0]) + (mat1.data[4] * mat2.data[1]) + (mat1.data[8] * mat2.data[2]) + (mat1.data[12] * mat2.data[3]);
        data[4] = (mat1.data[0] * mat2.data[4]) + (mat1.data[4] * mat2.data[5]) + (mat1.data[8] * mat2.data[6]) + (mat1.data[12] * mat2.data[7]);
        data[8] = (mat1.data[0] * mat2.data[8]) + (mat1.data[4] * mat2.data[9]) + (mat1.data[8] * mat2.data[10]) + (mat1.data[12] * mat2.data[11]);
        data[12] = (mat1.data[0] * mat2.data[12]) + (mat1.data[4] * mat2.data[13]) + (mat1.data[8] * mat2.data[14]) + (mat1.data[12] * mat2.data[15]);

        data[1] = (mat1.data[1] * mat2.data[0]) + (mat1.data[5] * mat2.data[1]) + (mat1.data[9] * mat2.data[2]) + (mat1.data[13] * mat2.data[3]);
        data[5] = (mat1.data[1] * mat2.data[4]) + (mat1.data[5] * mat2.data[5]) + (mat1.data[9] * mat2.data[6]) + (mat1.data[13] * mat2.data[7]);
        data[9] = (mat1.data[1] * mat2.data[8]) + (mat1.data[5] * mat2.data[9]) + (mat1.data[9] * mat2.data[10]) + (mat1.data[13] * mat2.data[11]);
        data[13] = (mat1.data[1] * mat2.data[12]) + (mat1.data[5] * mat2.data[13]) + (mat1.data[9] * mat2.data[14]) + (mat1.data[13] * mat2.data[15]);

        data[2] = (mat1.data[2] * mat2.data[0]) + (mat1.data[6] * mat2.data[1]) + (mat1.data[10] * mat2.data[2]) + (mat1.data[14] * mat2.data[3]);
        data[6] = (mat1.data[2] * mat2.data[4]) + (mat1.data[6] * mat2.data[5]) + (mat1.data[10] * mat2.data[6]) + (mat1.data[14] * mat2.data[7]);
        data[10] = (mat1.data[2] * mat2.data[8]) + (mat1.data[6] * mat2.data[9]) + (mat1.data[10] * mat2.data[10]) + (mat1.data[14] * mat2.data[11]);
        data[14] = (mat1.data[2] * mat2.data[12]) + (mat1.data[6] * mat2.data[13]) + (mat1.data[10] * mat2.data[14]) + (mat1.data[14] * mat2.data[15]);

        data[3] = (mat1.data[3] * mat2.data[0]) + (mat1.data[7] * mat2.data[1]) + (mat1.data[11] * mat2.data[2]) + (mat1.data[15] * mat2.data[3]);
        data[7] = (mat1.data[3] * mat2.data[4]) + (mat1.data[7] * mat2.data[5]) + (mat1.data[11] * mat2.data[6]) + (mat1.data[15] * mat2.data[7]);
        data[11] = (mat1.data[3] * mat2.data[8]) + (mat1.data[7] * mat2.data[9]) + (mat1.data[11] * mat2.data[10]) + (mat1.data[15] * mat2.data[11]);
        data[15] = (mat1.data[3] * mat2.data[12]) + (mat1.data[7] * mat2.data[13]) + (mat1.data[11] * mat2.data[14]) + (mat1.data[15] * mat2.data[15]);
    }

    public void setFromInvertMatrix(RagMatrix4f mat) {
        int n;
        float det;

        data[0] = (mat.data[5] * mat.data[10] * mat.data[15]) - (mat.data[5] * mat.data[11] * mat.data[14]) - (mat.data[9] * mat.data[6] * mat.data[15]) + (mat.data[9] * mat.data[7] * mat.data[14]) + (mat.data[13] * mat.data[6] * mat.data[11]) - (mat.data[13] * mat.data[7] * mat.data[10]);
        data[4] = (-mat.data[4] * mat.data[10] * mat.data[15]) + (mat.data[4] * mat.data[11] * mat.data[14]) + (mat.data[8] * mat.data[6] * mat.data[15]) - (mat.data[8] * mat.data[7] * mat.data[14]) - (mat.data[12] * mat.data[6] * mat.data[11]) + (mat.data[12] * mat.data[7] * mat.data[10]);
        data[8] = (mat.data[4] * mat.data[9] * mat.data[15]) - (mat.data[4] * mat.data[11] * mat.data[13]) - (mat.data[8] * mat.data[5] * mat.data[15]) + (mat.data[8] * mat.data[7] * mat.data[13]) + (mat.data[12] * mat.data[5] * mat.data[11]) - (mat.data[12] * mat.data[7] * mat.data[9]);
        data[12] = (-mat.data[4] * mat.data[9] * mat.data[14]) + (mat.data[4] * mat.data[10] * mat.data[13]) + (mat.data[8] * mat.data[5] * mat.data[14]) - (mat.data[8] * mat.data[6] * mat.data[13]) - (mat.data[12] * mat.data[5] * mat.data[10]) + (mat.data[12] * mat.data[6] * mat.data[9]);
        data[1] = (-mat.data[1] * mat.data[10] * mat.data[15]) + (mat.data[1] * mat.data[11] * mat.data[14]) + (mat.data[9] * mat.data[2] * mat.data[15]) - (mat.data[9] * mat.data[3] * mat.data[14]) - (mat.data[13] * mat.data[2] * mat.data[11]) + (mat.data[13] * mat.data[3] * mat.data[10]);
        data[5] = (mat.data[0] * mat.data[10] * mat.data[15]) - (mat.data[0] * mat.data[11] * mat.data[14]) - (mat.data[8] * mat.data[2] * mat.data[15]) + (mat.data[8] * mat.data[3] * mat.data[14]) + (mat.data[12] * mat.data[2] * mat.data[11]) - (mat.data[12] * mat.data[3] * mat.data[10]);
        data[9] = (-mat.data[0] * mat.data[9] * mat.data[15]) + (mat.data[0] * mat.data[11] * mat.data[13]) + (mat.data[8] * mat.data[1] * mat.data[15]) - (mat.data[8] * mat.data[3] * mat.data[13]) - (mat.data[12] * mat.data[1] * mat.data[11]) + (mat.data[12] * mat.data[3] * mat.data[9]);
        data[13] = (mat.data[0] * mat.data[9] * mat.data[14]) - (mat.data[0] * mat.data[10] * mat.data[13]) - (mat.data[8] * mat.data[1] * mat.data[14]) + (mat.data[8] * mat.data[2] * mat.data[13]) + (mat.data[12] * mat.data[1] * mat.data[10]) - (mat.data[12] * mat.data[2] * mat.data[9]);
        data[2] = (mat.data[1] * mat.data[6] * mat.data[15]) - (mat.data[1] * mat.data[7] * mat.data[14]) - (mat.data[5] * mat.data[2] * mat.data[15]) + (mat.data[5] * mat.data[3] * mat.data[14]) + (mat.data[13] * mat.data[2] * mat.data[7]) - (mat.data[13] * mat.data[3] * mat.data[6]);
        data[6] = (-mat.data[0] * mat.data[6] * mat.data[15]) + (mat.data[0] * mat.data[7] * mat.data[14]) + (mat.data[4] * mat.data[2] * mat.data[15]) - (mat.data[4] * mat.data[3] * mat.data[14]) - (mat.data[12] * mat.data[2] * mat.data[7]) + (mat.data[12] * mat.data[3] * mat.data[6]);
        data[10] = (mat.data[0] * mat.data[5] * mat.data[15]) - (mat.data[0] * mat.data[7] * mat.data[13]) - (mat.data[4] * mat.data[1] * mat.data[15]) + (mat.data[4] * mat.data[3] * mat.data[13]) + (mat.data[12] * mat.data[1] * mat.data[7]) - (mat.data[12] * mat.data[3] * mat.data[5]);
        data[14] = (-mat.data[0] * mat.data[5] * mat.data[14]) + (mat.data[0] * mat.data[6] * mat.data[13]) + (mat.data[4] * mat.data[1] * mat.data[14]) - (mat.data[4] * mat.data[2] * mat.data[13]) - (mat.data[12] * mat.data[1] * mat.data[6]) + (mat.data[12] * mat.data[2] * mat.data[5]);
        data[3] = (-mat.data[1] * mat.data[6] * mat.data[11]) + (mat.data[1] * mat.data[7] * mat.data[10]) + (mat.data[5] * mat.data[2] * mat.data[11]) - (mat.data[5] * mat.data[3] * mat.data[10]) - (mat.data[9] * mat.data[2] * mat.data[7]) + (mat.data[9] * mat.data[3] * mat.data[6]);
        data[7] = (mat.data[0] * mat.data[6] * mat.data[11]) - (mat.data[0] * mat.data[7] * mat.data[10]) - (mat.data[4] * mat.data[2] * mat.data[11]) + (mat.data[4] * mat.data[3] * mat.data[10]) + (mat.data[8] * mat.data[2] * mat.data[7]) - (mat.data[8] * mat.data[3] * mat.data[6]);
        data[11] = (-mat.data[0] * mat.data[5] * mat.data[11]) + (mat.data[0] * mat.data[7] * mat.data[9]) + (mat.data[4] * mat.data[1] * mat.data[11]) - (mat.data[4] * mat.data[3] * mat.data[9]) - (mat.data[8] * mat.data[1] * mat.data[7]) + (mat.data[8] * mat.data[3] * mat.data[5]);
        data[15] = (mat.data[0] * mat.data[5] * mat.data[10]) - (mat.data[0] * mat.data[6] * mat.data[9]) - (mat.data[4] * mat.data[1] * mat.data[10]) + (mat.data[4] * mat.data[2] * mat.data[9]) + (mat.data[8] * mat.data[1] * mat.data[6]) - (mat.data[8] * mat.data[2] * mat.data[5]);

        det = (mat.data[0] * data[0]) + (mat.data[1] * data[4]) + (mat.data[2] * data[8]) + (mat.data[3] * data[12]);
        if (det == 0.0f) {
            return;
        }

        det = 1.0f / det;

        for (n = 0; n != 16; n++) {
            data[n] *= det;
        }
    }

}
