package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

public class BitmapMosaic extends BitmapBase
{
    public BitmapMosaic()    {
        super();

        hasNormal=true;
        hasMetallicRoughness=true;
        hasEmissive=false;
        hasAlpha=false;
    }

        //
        // mosaic bitmaps
        //

    @Override
    public void generateInternal()    {
        int         x,y,lft,rgt,top,bot,tileWid,tileHigh,
                    splitCount;
        RagColor    groutColor,mosaicColor,mosaic2Color,col;

            // some random values

        splitCount=15+AppWindow.random.nextInt(10);

        groutColor=getRandomGray(0.4f,0.6f);
        mosaicColor=getRandomColor();
        mosaic2Color=getRandomColor();

        col=new RagColor(0.0f,0.0f,0.0f);

            // tile sizes

        tileWid=textureSize/splitCount;
        tileHigh=textureSize/splitCount;

            // clear canvases to grout

        drawRect(0,0,textureSize,textureSize,groutColor);
        createPerlinNoiseData(16,16);
        drawPerlinNoiseRect(0,0,textureSize,textureSize,0.6f,1.0f);
        drawStaticNoiseRect(0,0,textureSize,textureSize,0.7f,1.0f);
        blur(colorData,0,0,textureSize,textureSize,1,false);

        createNormalNoiseData(2.5f,0.5f);
        drawNormalNoiseRect(0,0,textureSize,textureSize);
        blur(normalData,0,0,textureSize,textureSize,1,false);

            // use a perlin noise rect for the colors

        createPerlinNoiseData(32,32);

            // draw the tiles

        for (y=0;y!=splitCount;y++) {
            for (x=0;x!=splitCount;x++) {

                    // slightly random position

                lft=(x*tileWid)+AppWindow.random.nextInt(3);
                rgt=((x*tileWid)+tileWid)-AppWindow.random.nextInt(3);
                top=(y*tileHigh)+AppWindow.random.nextInt(3);
                bot=((y*tileHigh)+tileHigh)-AppWindow.random.nextInt(3);

                    // the color

                col.setFromColorFactor(mosaicColor,mosaic2Color,getPerlineColorFactorForPosition(lft,top));

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
