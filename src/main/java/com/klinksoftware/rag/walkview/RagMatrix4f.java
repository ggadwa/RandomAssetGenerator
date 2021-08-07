package com.klinksoftware.rag.walkview;

public class RagMatrix4f {
    
    private float[] data;
    
    public RagMatrix4f() {
        data=new float[16];
        setIdentity();
    }
    
    public float[] getData()
    {
        return(data);
    }
    
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

    public void setOrthoMatrix(int screenWidth,int screenHeight,float glNearZ,float glFarZ)
    {
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
    
}
