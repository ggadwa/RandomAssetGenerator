package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

public class BitmapLiquid extends BitmapBase
{
    public final static int VARIATION_NONE=0;
    
    public BitmapLiquid()
    {
        super();
        
        hasNormal=true;
        hasMetallicRoughness=true;
        hasEmissive=false;
        hasAlpha=false;
    }
    
        //
        // glass bitmaps
        //

    @Override
    public void generateInternal(int variationMode)
    {
        int         n,x,y,halfHigh;
        RagColor    waterColor,waterAltColor;
          
        waterColor=getRandomColor();
        drawRect(0,0,textureSize,textureSize,waterColor);
        
        createPerlinNoiseData(16,16);
        waterColor=getRandomColor();
        drawPerlinNoiseColorRect(0,0,textureSize,textureSize,waterColor,0.5f);
        
        createPerlinNoiseData(32,32);
        waterColor=getRandomColor();
        drawPerlinNoiseColorRect(0,0,textureSize,textureSize,waterColor,0.7f);
    }
    
}
