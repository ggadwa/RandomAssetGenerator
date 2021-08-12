package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

public class BitmapGround extends BitmapBase
{
    public final static int VARIATION_NONE=0;
    
    public BitmapGround()
    {
        super();
        
        hasNormal=true;
        hasMetallicRoughness=true;
        hasEmissive=false;
        hasAlpha=false;
    }
    
        //
        // ground bitmaps
        //

    @Override
    public void generateInternal(int variationMode)
    {
        int         n,x,y,halfHigh;
        RagColor    groundColor,lineColor;
        
        groundColor=getRandomColor();
        
            // ground
            
        drawRect(0,0,textureSize,textureSize,groundColor);
        
        createPerlinNoiseData(16,16);
        drawPerlinNoiseRect(0,0,textureSize,textureSize,0.8f,1.0f);
        
        createPerlinNoiseData(32,32);
        drawPerlinNoiseRect(0,0,textureSize,textureSize,0.8f,1.0f);
        
            // vegetation=
            
        halfHigh=textureSize/2;
            
        for (x=0;x<textureSize;x++) {
            
                // vegetation color
                
            lineColor=adjustColorRandom(groundColor,0.7f,1.3f);
            
                // line half from top
                
            y=halfHigh+AppWindow.random.nextInt(halfHigh);
            drawRandomLine(x,0,x,(y+5),0,0,textureSize,textureSize,10,lineColor,false);
            drawLineNormal(x,0,x,(y+5),((x&0x1)==0x0)?NORMAL_BOTTOM_RIGHT_45:NORMAL_TOP_LEFT_45);
            
                // line half from bottom
                
            y=textureSize-(halfHigh+AppWindow.random.nextInt(halfHigh));
            drawRandomLine(x,(y-5),x,textureSize,0,0,textureSize,textureSize,10,lineColor,false);
            drawLineNormal(x,0,x,(y+5),((x&0x1)==0x0)?NORMAL_TOP_LEFT_45:NORMAL_BOTTOM_RIGHT_45);
        }
        
            // finish with the metallic-roughness

        createMetallicRoughnessMap(0.5f,0.4f);
    }
    
}
