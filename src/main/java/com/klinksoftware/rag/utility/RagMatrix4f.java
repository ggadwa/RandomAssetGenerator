package com.klinksoftware.rag.utility;

public class RagMatrix4f {
    
    private float[] data;
    
    public RagMatrix4f() {
        data=new float[16];
        setIdentity();
    }
    
    public float[] getData() {
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
    
}
