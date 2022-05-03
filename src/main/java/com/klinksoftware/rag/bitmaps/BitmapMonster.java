package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

public class BitmapMonster extends BitmapBase
{
    public BitmapMonster()    {
        super();

        hasNormal=true;
        hasMetallicRoughness=true;
        hasEmissive=false;
        hasAlpha=false;
    }

    private void generateSpotsOverlay() {
        int n, x, y, spotMin, spotAdd, spotCount, spotSize;

        spotMin=(int)((float)textureSize*0.1f);
        spotAdd=spotMin/2;
        spotCount=10+AppWindow.random.nextInt(10);

        for (n=0;n!=spotCount;n++) {
            spotSize=spotMin+AppWindow.random.nextInt(spotAdd);
            x=AppWindow.random.nextInt(textureSize-spotSize)-1;
            y=AppWindow.random.nextInt(textureSize-spotSize)-1;
            drawOvalDarken(x,y,(x+spotSize),(y+spotSize),(0.9f+(AppWindow.random.nextFloat()*0.1f)));
        }
    }

    private void generateStainsOverlay() {
        int n, k, lft, top, rgt, bot;
        int stainCount, stainSize;
        int xSize, ySize, markCount;

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
    }

    public void generateFurChunk()    {
        int x, y, halfHigh;
        RagColor furColor, lineColor;

        halfHigh=textureSize/2;

        furColor=getRandomColor();

            // fur background

        drawRect(0,0,textureSize,textureSize,furColor);

            // hair

        for (x=0;x!=textureSize;x++) {

                // hair color

            lineColor=this.adjustColorRandom(furColor,0.7f,1.3f);

                // hair half from top

            y=halfHigh+AppWindow.random.nextInt(halfHigh);
            drawRandomLine(x,-5,x,(y+5),0,0,textureSize,textureSize,10,lineColor,false);

                // hair half from bottom

            y=textureSize-(halfHigh+AppWindow.random.nextInt(halfHigh));
            drawRandomLine(x,(y-5),x,(textureSize+5),0,0,textureSize,textureSize,10,lineColor,false);
        }

            // any spots

        if (AppWindow.random.nextBoolean()) generateSpotsOverlay();

        createMetallicRoughnessMap(0.5f,0.5f);
    }

    public void generateScaleChunk() {
        int x, y, dx, dy, sx, sy, sx2, sy2;
        int xCount, scaleCount, sWid, sHigh;
        RagColor scaleColor, borderColor, col;

        scaleCount=12+AppWindow.random.nextInt(20);

        sWid=textureSize/scaleCount;
        sHigh=textureSize/scaleCount;

        scaleColor=getRandomColor();
        borderColor=adjustColor(scaleColor,0.7f);

            // background

        createPerlinNoiseData(32,32);
        createNormalNoiseData(1.5f,0.5f);

        drawRect(0,0,textureSize,textureSize,scaleColor);
        drawPerlinNoiseRect(0,0,textureSize,textureSize,0.8f,1.3f);
        drawNormalNoiseRect(0,0,textureSize,textureSize);
        blur(colorData,0,0,textureSize,textureSize,5,false);

            // scales (need extra row for overlap)

        dy=textureSize-sHigh;

        for (y=0;y!=(scaleCount+1);y++) {

            if ((y%2)==0) {
                dx=0;
                xCount=scaleCount;
            }
            else {
                dx=-(sWid/2);
                xCount=scaleCount+1;
            }

            for (x=0;x!=xCount;x++) {

                    // can have darkened scale if not on
                    // wrapping rows

                col=scaleColor;

                if ((y!=0) && (y!=scaleCount) && (x!=0) && (x!=(xCount-1))) {
                    if (AppWindow.random.nextFloat()<0.2f) {
                        col=adjustColor(scaleColor,(0.6f+(AppWindow.random.nextFloat()*0.3f)));
                    }
                }

                    // some slight offsets

                sx=dx+(AppWindow.random.nextInt(10)-5);
                sy=dy+(AppWindow.random.nextInt(10)-5);
                sx2=dx+sWid;
                sy2=dy+(sHigh*2);

                    // the scale itself
                    // we draw the scale as a solid, flat oval and
                    // then redraw the border with normals

                drawOval(sx,sy,sx2,sy2,0.25f,0.75f,0.0f,0.0f,3,0.8f,col,borderColor,0.5f,false,false,0.0f,1.0f);

                dx+=sWid;
            }

            dy-=sHigh;
        }

            // any spots

        if (AppWindow.random.nextBoolean()) generateSpotsOverlay();

        createMetallicRoughnessMap(0.5f,0.5f);
    }

    private void generateFaceChunkEye(int x, int y, RagColor eyeColor) {
        drawOval(x, y, (x + 40), (y + 15), 0.0f, 1.0f, 0.0f, 0.0f, 2, 0.5f, this.COLOR_WHITE, this.COLOR_BLACK, 0.5f, false, false, 1.0f, 0.0f);
        drawOval((x + 15), (y + 1), (x + 25), (y + 14), 0.0f, 1.0f, 0.0f, 0.0f, 2, 0.5f, eyeColor, null, 0.5f, false, false, 1.0f, 0.0f);
    }

    private void generateAddFace() {
        RagColor eyeColor;

        eyeColor = this.getRandomColor();

        drawOval(415, 235, 505, 245, 0.0f, 1.0f, 0.0f, 0.0f, 2, 0.5f, this.COLOR_BLACK, this.COLOR_BLACK, 0.5f, false, false, 1.0f, 0.0f);

        generateFaceChunkEye(415, 295, eyeColor);
        generateFaceChunkEye(465, 295, eyeColor);
    }

    @Override
    public void generateInternal()    {
        switch (AppWindow.random.nextInt(2)) {
            case 0:
                generateFurChunk();
                break;

            case 1:
                generateScaleChunk();
                break;
        }

        //generateAddFace();
    }
}
