package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.utility.*;

public class BitmapStone extends BitmapBase
{
    public final int VARIATION_NONE=0;
    
    public BitmapStone(int colorScheme)
    {
        super(colorScheme);
        
        hasNormal=true;
        hasMetallicRoughness=true;
        hasGlow=false;
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
        RagColor        stoneColor,altStoneColor,drawStoneColor,groutColor,outlineColor;
        
        stoneColor=getRandomColor();
        altStoneColor=getRandomColor();
        groutColor=getRandomGray(0.35f,0.55f);
        outlineColor=null; // this.adjustColor(groutColor,0.95);        // this doesn't make it any better
        
            // the noise grout
            
        drawRect(0,0,textureSize,textureSize,groutColor);
        createPerlinNoiseData(16,16);
        drawPerlinNoiseRect(0,0,textureSize,textureSize,0.4f,1.2f);
        drawStaticNoiseRect(0,0,textureSize,textureSize,0.7f,1.1f);
        blur(colorData,0,0,textureSize,textureSize,1,false);
        
        createNormalNoiseData(2.5f,0.5f);
        drawNormalNoiseRect(0,0,textureSize,textureSize);
        blur(normalData,0,0,textureSize,textureSize,1,false);

            // noise for stones
            
        this.createPerlinNoiseData(32,32);
        this.createNormalNoiseData(5.0f,0.3f);
        
            // draw the stones
            
        yCount=4+(int)(Math.random()*4.0);
        yAdd=(int)((float)textureSize/(float)yCount);
        
        top=0;
        
        for (y=0;y!=yCount;y++) {
            bot=(y==(yCount-1))?textureSize:(top+yAdd);
            
            lft=0;
            
            while (true) {
                rgt=lft+(yAdd+(int)(Math.random()*((double)yAdd*0.8)));
                if (rgt>textureSize) rgt=textureSize;

                    // special check if next stone would be too small,
                    // so enlarge this stone to cover it
                    
                if ((textureSize-rgt)<yAdd) rgt=textureSize;
                
                    // the stone itself
                    
                drawStoneColor=adjustColorRandom(((Math.random()<0.7)?stoneColor:altStoneColor),0.7f,1.2f);

                xOff=(int)(Math.random()*((double)textureSize*0.01));
                yOff=(int)(Math.random()*((double)textureSize*0.01));

                edgeSize=(int)(Math.random()*((double)textureSize*0.1))+(int)(Math.random()*((double)textureSize*0.2));     // new edge size as stones aren't the same
                xRoundFactor=0.02f+(float)(Math.random()*0.05);
                yRoundFactor=0.02f+(float)(Math.random()*0.05);
                normalZFactor=(float)(Math.random()*0.2);           // different z depths

                drawOval((lft+xOff),(top+yOff),(rgt+xOff),(bot+yOff),0.0f,1.0f,xRoundFactor,yRoundFactor,edgeSize,0.5f,drawStoneColor,outlineColor,normalZFactor,false,true,0.4f,1.2f);

                    // gravity distortions to make stones unique
                    
                //gravityDistortEdges((lft+xOff),(top+yOff),(rgt+xOff),(bot+yOff),5,20,5);

                lft=rgt;
                if (rgt==textureSize) break;
            }
            
            top+=yAdd;
        }

            // finish with the metallic-roughness

        this.createMetallicRoughnessMap(0.5f,0.5f);
    }
}
