package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

public class BitmapConcrete extends BitmapBase
{
    public BitmapConcrete()    {
        super();

        hasNormal=true;
        hasMetallicRoughness=true;
        hasEmissive=false;
        hasAlpha=false;
    }

        //
        // concrete bitmaps
        //

    @Override
    public void generateInternal()    {
        int             n,k,mx,my,stainSize,xSize,ySize,
                        lft,rgt,top,bot,stainCount,markCount;
        RagColor        concreteColor,jointColor;

        concreteColor=getRandomColor();
        jointColor=adjustColorRandom(concreteColor,0.65f,0.75f);

            // the concrete background

        drawRect(0,0,textureSize,textureSize,concreteColor);

        createPerlinNoiseData(16,16);
        drawPerlinNoiseRect(0,0,textureSize,textureSize,0.6f,1.0f);

        createNormalNoiseData(3.0f,0.4f);
        drawNormalNoiseRect(0,0,textureSize,textureSize);

            // stains

        stainCount=AppWindow.random.nextInt(5);
        stainSize=(int)((float)textureSize*0.1f);

        for (n=0;n!=stainCount;n++) {
            lft=AppWindow.random.nextInt(textureSize);
            xSize=stainSize+AppWindow.random.nextInt(stainSize);

            top=AppWindow.random.nextInt(textureSize);
            ySize=stainSize+AppWindow.random.nextInt(stainSize);

            markCount=2+AppWindow.random.nextInt(4);

            for (k=0;k!=markCount;k++) {
                rgt=lft+xSize;
                if (rgt>=textureSize) rgt=textureSize-1;
                bot=top+ySize;
                if (bot>=textureSize) bot=textureSize-1;

                drawOvalStain(lft,top,rgt,bot,0.01f,0.15f,0.85f);

                lft+=(AppWindow.random.nextBoolean())?(-(xSize/3)):(xSize/3);
                top+=(AppWindow.random.nextBoolean())?(-(ySize/3)):(ySize/3);
                xSize=(int)((float)xSize*0.8f);
                ySize=(int)((float)ySize*0.8f);
            }
        }

        blur(colorData,0,0,textureSize,textureSize,1,false);

            // concrete expansion cuts

        if (AppWindow.random.nextBoolean()) {
            drawLineColor(1,0,1,textureSize,jointColor);
            jointColor=adjustColor(jointColor,0.9f);
            drawLineColor(0,0,0,textureSize,jointColor);
            drawLineColor(2,0,2,textureSize,jointColor);
            drawLineNormal(1,0,1,textureSize,NORMAL_CLEAR);
            drawLineNormal(0,0,0,textureSize,NORMAL_RIGHT_45);
            drawLineNormal(2,0,2,textureSize,NORMAL_LEFT_45);
        }

        if (AppWindow.random.nextBoolean()) {
            mx=textureSize/2;
            drawLineColor(mx,0,mx,textureSize,jointColor);
            jointColor=adjustColor(jointColor,0.9f);
            drawLineColor((mx-1),0,(mx-1),textureSize,jointColor);
            drawLineColor((mx+1),0,(mx+1),textureSize,jointColor);
            drawLineNormal(mx,0,mx,textureSize,NORMAL_CLEAR);
            drawLineNormal((mx-1),0,(mx-1),textureSize,NORMAL_RIGHT_45);
            drawLineNormal((mx+1),0,(mx+1),textureSize,NORMAL_LEFT_45);
        }

        if (AppWindow.random.nextBoolean()) {
            drawLineColor(0,1,textureSize,1,jointColor);
            jointColor=adjustColor(jointColor,0.9f);
            drawLineColor(0,0,textureSize,0,jointColor);
            drawLineColor(0,2,textureSize,2,jointColor);
            drawLineNormal(0,1,textureSize,1,NORMAL_CLEAR);
            drawLineNormal(0,0,textureSize,0,NORMAL_BOTTOM_45);
            drawLineNormal(0,2,textureSize,2,NORMAL_TOP_45);
        }

        if (AppWindow.random.nextBoolean()) {
            my=textureSize/2;
            drawLineColor(0,my,textureSize,my,jointColor);
            jointColor=adjustColor(jointColor,0.9f);
            drawLineColor(0,(my-1),textureSize,(my-1),jointColor);
            drawLineColor(0,(my+1),textureSize,(my+1),jointColor);
            drawLineNormal(0,my,textureSize,my,NORMAL_CLEAR);
            drawLineNormal(0,(my-1),textureSize,(my-1),NORMAL_BOTTOM_45);
            drawLineNormal(0,(my+1),textureSize,(my+1),NORMAL_TOP_45);
        }

            // finish with the metallic-roughness

        createMetallicRoughnessMap(0.35f,0.3f);
    }
}
