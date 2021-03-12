package com.klinksoftware.rag.utility;

public class RagBound
{
    public float        min,max;
    
    public RagBound(float v1,float v2)
    {
        if (v1<v2) {
            min=v1;
            max=v2;
        }
        else {
            min=v2;
            max=v1;
        }
    }
    
    public float getMidPoint()
    {
        return((max+min)*0.5f);
    }
    
    public float getSize()
    {
        return(max-min);
    }
    
    public void adjust(float value)
    {
        if (value<min) min=value;
        if (value>max) max=value;
    }

}
