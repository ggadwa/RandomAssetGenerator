package com.klinksoftware.rag.utility;

public class RagBound
{
    public float        min,max;
    
    public RagBound(float v1,float v2)
    {
        if (v1<v2) {
            this.min=v1;
            this.max=v2;
        }
        else {
            this.min=v2;
            this.max=v1;
        }
    }
    
    public float getSize()
    {
        return(this.max-this.min);
    }
}
