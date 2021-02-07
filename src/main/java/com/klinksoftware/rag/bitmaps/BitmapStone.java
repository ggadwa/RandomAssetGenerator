package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

import java.util.*;

public class BitmapStone extends BitmapBase
{
    public final static int VARIATION_NONE=0;
    
    public BitmapStone()
    {
        super();
        
        hasNormal=true;
        hasMetallicRoughness=true;
        hasGlow=false;
        hasAlpha=false;
    }
    
        //
        // stone bitmaps
        //

    @Override
    public void generateInternal(int variationMode)
    {
        int             y,yCount,yAdd,xOff,yOff,edgeSize,
                        lft,rgt,top,bot;
        float           xRoundFactor,yRoundFactor,normalZFactor;
        float[]         backgroundData,stoneCopyData;
        RagColor        stoneColor,altStoneColor,drawStoneColor,groutColor,outlineColor;
        
        stoneColor=getRandomColor();
        altStoneColor=getRandomColorDull(0.9f);
        groutColor=getRandomGray(0.35f,0.55f);
        
            // the noise grout
            
        drawRect(0,0,textureSize,textureSize,groutColor);
        createPerlinNoiseData(32,32);
        drawStaticNoiseRect(0,0,textureSize,textureSize,0.5f,1.1f);
        drawPerlinNoiseRect(0,0,textureSize,textureSize,0.3f,0.9f);
        blur(colorData,0,0,textureSize,textureSize,1,false);
        
        createNormalNoiseData(2.5f,0.5f);
        drawNormalNoiseRect(0,0,textureSize,textureSize);
        blur(normalData,0,0,textureSize,textureSize,1,false);

            // noise for stones
            
        this.createPerlinNoiseData(32,32);
        this.createNormalNoiseData(5.0f,0.3f);
        
            // we draw the stones all alone on the noise
            // background so we can distort the stones and
            // only catch the background
            
        backgroundData=colorData.clone();
        stoneCopyData=colorData.clone();
        
            // draw the stones
            
        yCount=4+GeneratorMain.random.nextInt(4);
        yAdd=textureSize/yCount;
        
        top=0;
        
        for (y=0;y!=yCount;y++) {
            bot=(y==(yCount-1))?(textureSize-1):(top+yAdd);
            if (bot>=textureSize) bot=textureSize-1;
            
            lft=0;
            
            while (true) {
                rgt=lft+(yAdd+(int)(GeneratorMain.random.nextFloat()*((float)yAdd*0.8f)));
                if (rgt>=textureSize) rgt=textureSize-1;

                    // special check if next stone would be too small,
                    // so enlarge this stone to cover it
                    
                if ((textureSize-rgt)<yAdd) rgt=textureSize-1;
                
                    // the stone itself
                    
                drawStoneColor=adjustColorRandom(((GeneratorMain.random.nextFloat()<0.7f)?stoneColor:altStoneColor),0.7f,1.2f);

                xOff=(int)(GeneratorMain.random.nextFloat()*((float)textureSize*0.01f));
                yOff=(int)(GeneratorMain.random.nextFloat()*((float)textureSize*0.01f));

                edgeSize=(int)((GeneratorMain.random.nextFloat()*((float)textureSize*0.1f))+(GeneratorMain.random.nextFloat()*((float)textureSize*0.2f)));     // new edge size as stones aren't the same
                xRoundFactor=0.02f+(GeneratorMain.random.nextFloat()*0.05f);
                yRoundFactor=0.02f+(GeneratorMain.random.nextFloat()*0.05f);
                normalZFactor=(GeneratorMain.random.nextFloat()*0.2f);           // different z depths
                
                    // draw on the background
                    
                colorData=backgroundData.clone();
                
                outlineColor=adjustColor(drawStoneColor,0.5f);
                drawOval((lft+xOff),(top+yOff),rgt,bot,0.0f,1.0f,xRoundFactor,yRoundFactor,edgeSize,0.5f,drawStoneColor,outlineColor,normalZFactor,false,true,0.4f,1.2f);

                    // gravity distortions to make stones unique
                    
                gravityDistortEdges((lft+xOff),(top+yOff),rgt,bot,10,35,5);
                
                    // and copy over
                    
                blockCopy(colorData,(lft+xOff),(top+yOff),rgt,bot,stoneCopyData);

                lft=rgt;
                if (rgt==(textureSize-1)) break;
            }
            
            top+=yAdd;
        }
        
            // finally push over the stone copy version
            
        colorData=stoneCopyData;

            // finish with the metallic-roughness

        createMetallicRoughnessMap(0.5f,0.5f);
    }
}
