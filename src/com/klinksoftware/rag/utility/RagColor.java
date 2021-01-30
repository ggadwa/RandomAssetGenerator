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
}
