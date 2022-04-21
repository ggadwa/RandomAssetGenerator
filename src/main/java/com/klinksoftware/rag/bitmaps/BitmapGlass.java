package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

public class BitmapGlass extends BitmapBase
{
    public BitmapGlass()    {
        super();

        hasNormal=true;
        hasMetallicRoughness=true;
        hasEmissive=false;
        hasAlpha=true;
    }

        //
        // glass bitmaps
        //

    @Override
    public void generateInternal()    {
        int         n,x,y,x2,y2,lineCount,
                    startWid;
        RagColor    color;

            // default glass to white

        drawRect(0,0,textureSize,textureSize,COLOR_WHITE);

            // back noise and blur

        drawStaticNoiseRect(0,0,textureSize,textureSize,0.9f,0.95f);
        blur(colorData,0,0,textureSize,textureSize,10,false);

            // reflection lines

        lineCount=5+AppWindow.random.nextInt(20);

        startWid=(int)((float)textureSize*0.4f);

        for (n=0;n!=lineCount;n++) {
            color=getRandomGray(0.7f,0.9f);
            x=AppWindow.random.nextInt(startWid);
            x2=(x+1)+AppWindow.random.nextInt(textureSize-x);
            y=textureSize-AppWindow.random.nextInt(textureSize-(x2-x));
            y2=y-((x2-x)/2);

            drawLineColor(x,y,x2,y2,color);
            drawLineNormal(x,y,x2,y2,((n&0x1)==0x0)?NORMAL_BOTTOM_RIGHT_45:NORMAL_TOP_LEFT_45);
        }

            // front noise and blur

        drawStaticNoiseRect(0,0,textureSize,textureSize,0.95f,1.0f);
        blur(colorData,0,0,textureSize,textureSize,5,false);

            // the alpha

        setImageAlpha(0.75f);

            // finish with the metallic-roughness

        createMetallicRoughnessMap(0.5f,0.6f);
    }
}
