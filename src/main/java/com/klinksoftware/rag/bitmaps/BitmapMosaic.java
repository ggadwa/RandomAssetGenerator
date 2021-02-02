package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.utility.*;

public class BitmapMosaic extends BitmapBase
{
    public final static int VARIATION_NONE=0;
    
    public BitmapMosaic(int colorScheme)
    {
        super(colorScheme);
        
        hasNormal=true;
        hasMetallicRoughness=true;
        hasGlow=false;
        hasAlpha=false;
    }
    
        //
        // mosaic bitmaps
        //

    @Override
    public void generateInternal(int variationMode)
    {
        int         x,y,lft,rgt,top,bot,tileWid,tileHigh,
                    splitCount;
        RagColor    groutColor,mosaicColor,mosaic2Color,col;
        
            // some random values

        splitCount=15+(int)(Math.random()*10.0);
        
        groutColor=getRandomGray(0.4f,0.6f);
        mosaicColor=getRandomColor();
        mosaic2Color=getRandomColor();
        
        col=new RagColor(0.0f,0.0f,0.0f);
        
            // tile sizes
            
        tileWid=textureSize/splitCount;
        tileHigh=textureSize/splitCount;

            // clear canvases to grout

        this.drawRect(0,0,textureSize,textureSize,groutColor);
        this.createPerlinNoiseData(16,16);
        this.drawPerlinNoiseRect(0,0,textureSize,textureSize,0.6f,1.0f);
        this.drawStaticNoiseRect(0,0,textureSize,textureSize,0.7f,1.0f);
        this.blur(colorData,0,0,textureSize,textureSize,1,false);
        
        this.createNormalNoiseData(2.5f,0.5f);
        this.drawNormalNoiseRect(0,0,textureSize,textureSize);
        this.blur(normalData,0,0,textureSize,textureSize,1,false);

            // use a perlin noise rect for the colors
        
        this.createPerlinNoiseData(32,32);
        
            // draw the tiles
        
        for (y=0;y!=splitCount;y++) {
            for (x=0;x!=splitCount;x++) {
                
                    // slightly random position
                    
                lft=(x*tileWid)+(int)(Math.random()*3.0);
                rgt=((x*tileWid)+tileWid)-(int)(Math.random()*3.0);
                top=(y*tileHigh)+(int)(Math.random()*3.0);
                bot=((y*tileHigh)+tileHigh)-(int)(Math.random()*3.0);
                
                    // the color

                col.setFromColorFactor(mosaicColor,mosaic2Color,this.getPerlineColorFactorForPosition(lft,top));

                    // draw
                    
                drawRect(lft,top,rgt,bot,col);
                draw3DFrameRect(lft,top,rgt,bot,1,col,true);
                
                    // noise and blur
                
                drawStaticNoiseRect(lft,top,rgt,bot,1.1f,1.3f);
                blur(colorData,lft,top,rgt,bot,1,true);
            }
        }

            // finish with the metallic-roughness

        createMetallicRoughnessMap(0.5f,0.5f);
    }
}
