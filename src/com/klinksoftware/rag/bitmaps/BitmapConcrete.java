package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.utility.*;

public class BitmapConcrete extends BitmapBase
{
    public final int VARIATION_NONE=0;
    
    public BitmapConcrete(int colorScheme)
    {
        super(colorScheme);
        
        hasNormal=true;
        hasMetallicRoughness=true;
        hasGlow=false;
    }
    
        //
        // concrete bitmaps
        //

    @Override
    public void generateInternal(int variationMode)
    {
        RagColor        concreteColor,jointColor;
        
        concreteColor=getRandomColor();
        jointColor=getRandomColor(); //adjustColorRandom(concreteColor,0.75f,0.85f);
        
            // the concrete background
        
        drawRect(0,0,textureSize,textureSize,concreteColor);
        
        createPerlinNoiseData(16,16);
        drawPerlinNoiseRect(0,0,textureSize,textureSize,0.6f,1.0f);
        
        createNormalNoiseData(3.0f,0.4f);
        drawNormalNoiseRect(0,0,textureSize,textureSize);
        
            // concrete expansion cuts
            
        //if (Math.random()<0.5) {
            drawLineColor(1,0,1,textureSize,jointColor);
            jointColor=adjustColor(jointColor,0.9f);
            drawLineColor(0,0,0,textureSize,jointColor);
            drawLineColor(2,0,2,textureSize,jointColor);
            drawLineNormal(1,0,1,textureSize,NORMAL_CLEAR);
            drawLineNormal(0,0,0,textureSize,NORMAL_RIGHT_45);
            drawLineNormal(2,0,2,textureSize,NORMAL_LEFT_45);
        //}

        //if (Math.random()<0.5) {
            drawLineColor(0,1,textureSize,1,jointColor);
            jointColor=adjustColor(jointColor,0.9f);
            drawLineColor(0,0,textureSize,0,jointColor);
            drawLineColor(0,2,textureSize,2,jointColor);
            drawLineNormal(0,1,textureSize,1,NORMAL_CLEAR);
            drawLineNormal(0,0,textureSize,0,NORMAL_BOTTOM_45);
            drawLineNormal(0,2,textureSize,2,NORMAL_TOP_45);
        //}

            // finish with the metallic-roughness

        createMetallicRoughnessMap(0.35f,0.3f);
    }
}
