package com.klinksoftware.rag.utility;

public class RagColor
{
    public float        r,g,b;
    
    public RagColor(float r,float g,float b)
    {
        this.r=r;
        if (this.r<0.0f) this.r=0.0f;
        if (this.r>1.0f) this.r=1.0f;
        
        this.g=g;
        if (this.g<0.0f) this.g=0.0f;
        if (this.g>1.0f) this.g=1.0f;
        
        this.b=b;
        if (this.b<0.0f) this.b=0.0f;
        if (this.b>1.0f) this.b=1.0f;
    }
    
    public void setFromColor(RagColor col)
    {
        this.r=col.r;
        this.g=col.g;
        this.b=col.b;
    }
    
    public void setFromColorFactor(RagColor col1,RagColor col2,float factor)
    {
        float       f2;
        
        f2=1.0f-factor;
        
        r=(col1.r*factor)+(col2.r*f2);
        g=(col1.g*factor)+(col2.g*f2);
        b=(col1.b*factor)+(col2.b*f2);
    }
    
    public void factor(float f)
    {
        r*=f;
        if (r<0.0f) r=0.0f;
        if (r>1.0f) r=1.0f;
        
        g*=f;
        if (g<0.0f) g=0.0f;
        if (g>1.0f) g=1.0f;
        
        b*=f;
        if (b<0.0f) b=0.0f;
        if (b>1.0f) b=1.0f;
    }
}
