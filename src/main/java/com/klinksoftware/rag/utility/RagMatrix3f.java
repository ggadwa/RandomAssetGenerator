package com.klinksoftware.rag.utility;

public class RagMatrix3f {
    public float[] data;
    
    public RagMatrix3f() {
        data=new float[12];
        setIdentity();
    }
    
    public void setIdentity() {
        data[0]=1.0f;
        data[1]=0.0f;
        data[2]=0.0f;
        data[3]=0.0f;
        data[4]=1.0f;
        data[5]=0.0f;
        data[6]=0.0f;
        data[7]=0.0f;
        data[8]=1.0f;
    }
    
    public void setInvertTransposeFromMat4(RagMatrix4f mat4)
    {
        float m00,m01,m02,m03,m04,m05,m06;
        float m07,m08,m09,m10,m11,det;
        
            // create the inversion
            
        m00=(mat4.data[0]*mat4.data[5])-(mat4.data[1]*mat4.data[4]);
        m01=(mat4.data[0]*mat4.data[6])-(mat4.data[2]*mat4.data[4]);
        m02=(mat4.data[0]*mat4.data[7])-(mat4.data[3]*mat4.data[4]);
        m03=(mat4.data[1]*mat4.data[6])-(mat4.data[2]*mat4.data[5]);
        m04=(mat4.data[1]*mat4.data[7])-(mat4.data[3]*mat4.data[5]);
        m05=(mat4.data[2]*mat4.data[7])-(mat4.data[3]*mat4.data[6]);
        m06=(mat4.data[8]*mat4.data[13])-(mat4.data[9]*mat4.data[12]);
        m07=(mat4.data[8]*mat4.data[14])-(mat4.data[10]*mat4.data[12]);
        m08=(mat4.data[8]*mat4.data[15])-(mat4.data[11]*mat4.data[12]);
        m09=(mat4.data[9]*mat4.data[14])-(mat4.data[10]*mat4.data[13]);
        m10=(mat4.data[9]*mat4.data[15])-(mat4.data[11]*mat4.data[13]);
        m11=(mat4.data[10]*mat4.data[15])-(mat4.data[11]*mat4.data[14]);

        det=(m00*m11)-(m01*m10)+(m02*m09)+(m03*m08)-(m04*m07)+(m05*m06);
        if (det!=0.0f) det=1.0f/det;

            // transpose while finishing the inversion
            // and dropping into 3x3
            
        data[0]=((mat4.data[5]*m11)-(mat4.data[6]*m10)+(mat4.data[7]*m09))*det;
        data[3]=((mat4.data[2]*m10)-(mat4.data[1]*m11)-(mat4.data[3]*m09))*det;
        data[6]=((mat4.data[13]*m05)-(mat4.data[14]*m04)+(mat4.data[15]*m03))*det;
        data[1]=((mat4.data[6]*m08)-(mat4.data[4]*m11)-(mat4.data[7]*m07))*det;
        data[4]=((mat4.data[0]*m11)-(mat4.data[2]*m08)+(mat4.data[3]*m07))*det;
        data[7]=((mat4.data[14]*m02)-(mat4.data[12]*m05)-(mat4.data[15]*m01))*det;
        data[2]=((mat4.data[4]*m10)-(mat4.data[5]*m08)+(mat4.data[7]*m06))*det;
        data[5]=((mat4.data[1]*m08)-(mat4.data[0]*m10)-(mat4.data[3]*m06))*det;
        data[8]=((mat4.data[12]*m04)-(mat4.data[13]*m02)+(mat4.data[15]*m00))*det;
    }
    
}
