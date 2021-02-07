package com.klinksoftware.rag.utility;

public class RagPoint
{
    public float        x,y,z;
    
    public RagPoint(float x,float y,float z)
    {
        this.x=x;
        this.y=y;
        this.z=z;
    }
    
    public void setFromValues(float x,float y,float z)
    {
        this.x=x;
        this.y=y;
        this.z=z;
    }
    
    public void scale(float f)
    {
        x*=f;
        y*=f;
        z*=f;
    }
    
    public void normalize()
    {
        float       f;
        
        f=(float)Math.sqrt((x*x)+(y*y)+(z*z));
        if (f!=0.0f) f=1.0f/f;
        
        x*=f;
        y*=f;
        z*=f;
    }
    
    public void normalize2D()
    {
        float       f;
        
        f=(float)Math.sqrt((x*x)+(y*y));
        if (f!=0.0f) f=1.0f/f;
        
        x*=f;
        y*=f;
    }
    
    public float dot(RagPoint vct)
    {
        return((x*vct.x)+(y*vct.y)+(z*vct.z));
    }
}
