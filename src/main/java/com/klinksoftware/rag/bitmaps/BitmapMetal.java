package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

public class BitmapMetal extends BitmapBase
{
    public final static float[][][][] CORRUGATION_LINES = {
        {{{0.0f, 1.0f}, {1.0f, 0.0f}}, {{0.0f, 0.0f}, {1.0f, 1.0f}}, {{0.0f, 0.0f}, {1.0f, 1.0f}}, {{0.0f, 1.0f}, {1.0f, 0.0f}}}, // diamonds
        {{{0.0f, 1.0f}, {1.0f, 0.0f}}, {{0.0f, 0.0f}, {1.0f, 1.0f}}, {{0.0f, 1.0f}, {1.0f, 0.0f}}, {{0.0f, 0.0f}, {1.0f, 1.0f}}}, // waves
        {{{0.5f, 0.0f}, {0.5f, 1.0f}}, {{0.0f, 0.5f}, {1.0f, 0.5f}}, {{0.0f, 0.5f}, {1.0f, 0.5f}}, {{0.5f, 0.0f}, {0.5f, 1.0f}}} // pluses
    };

    public BitmapMetal() {
        super();

        hasNormal=true;
        hasMetallicRoughness=true;
        hasEmissive=false;
        hasAlpha=false;
    }

        //
        // metal pieces
        //

    private void generateMetalCorrugation(int lft, int top, int rgt, int bot, RagColor metalColor) {
        int x, y, dx, dy, sx, sy, ex, ey, wid, high, idx;
        int corrCount, corrWid, corrHigh, lineStyle;
        float lineWid, lineHigh;
        float[][] line;
        RagColor metalCorrColor;

        wid=rgt-lft;
        high=bot-top;

        if ((wid<=0) || (high<=0)) return;

        metalCorrColor=adjustColorRandom(metalColor,0.6f,0.7f);

        corrCount=(int)(((float)wid*0.06f)+(AppWindow.random.nextFloat()*((float)wid*0.03f)));
        corrWid=wid/corrCount;
        corrHigh=high/corrCount;

        lineWid=(float)(corrWid-4);
        lineHigh=(float)(corrHigh-4);

        lineStyle=AppWindow.random.nextInt(CORRUGATION_LINES.length);

            // corrugations

        dy=top+((high-(corrHigh*corrCount))/2);

        for (y=0;y!=corrCount;y++) {

            dx=lft+((wid-(corrWid*corrCount))/2);

            for (x=0;x!=corrCount;x++) {

                idx=((y&0x1)*2)+(x&0x1);
                line=CORRUGATION_LINES[lineStyle][idx];

                sx=dx+(int)(line[0][0]*lineWid);
                sy=dy+(int)(line[0][1]*lineHigh);
                ex=dx+(int)(line[1][0]*lineWid);
                ey=dy+(int)(line[1][1]*lineHigh);

                drawLineColor(sx,sy,ex,ey,metalCorrColor);
                drawLineNormal(sx,sy,ex,ey,NORMAL_CLEAR);

                if (Math.abs(ex-sx)>Math.abs(ey-sy)) {
                    drawLineNormal(sx,(sy+1),ex,(ey+1),NORMAL_BOTTOM_45);
                    drawLineNormal(sx,(sy-1),ex,(ey-1),NORMAL_TOP_45);
                }
                else {
                    drawLineNormal((sx+1),sy,(ex+1),ey,NORMAL_RIGHT_45);
                    drawLineNormal((sx-1),sy,(ex-1),ey,NORMAL_LEFT_45);
                }

                dx+=corrWid;
            }

            dy+=corrHigh;
        }
    }

    private void generateMetalScrews(int lft, int top, int rgt, int bot, RagColor screwColor, int screwSize) {
        int mx, my, edgeSize;
        RagColor outlineColor;

        edgeSize=screwSize/2;
        outlineColor = adjustColor(screwColor, 0.5f);

            // corners

        if (AppWindow.random.nextFloat()<0.33f) {
            drawScrew(lft, top, screwColor, outlineColor, screwSize, edgeSize);
            drawScrew((rgt - screwSize), top, screwColor, outlineColor, screwSize, edgeSize);
            drawScrew((rgt - screwSize), (bot - screwSize), screwColor, outlineColor, screwSize, edgeSize);
            drawScrew(lft, (bot - screwSize), screwColor, outlineColor, screwSize, edgeSize);
        }

            // middles

        if (AppWindow.random.nextFloat()<0.33) {
            mx=((lft+rgt)/2)-(screwSize/2);
            my=((top+bot)/2)-(screwSize/2);
            drawScrew(mx, top, screwColor, outlineColor, screwSize, edgeSize);
            drawScrew((rgt - screwSize), my, screwColor, outlineColor, screwSize, edgeSize);
            drawScrew(mx, (bot - screwSize), screwColor, outlineColor, screwSize, edgeSize);
            drawScrew(lft, my, screwColor, outlineColor, screwSize, edgeSize);
        }
    }

    private void generateMetalWave(int lft, int top, int rgt, int bot, RagColor color) {
        int sz, waveCount;
        RagColor frameColor;

        drawMetalShine(lft,top,rgt,bot,color);

        frameColor=adjustColorRandom(color,0.75f,0.85f);
        sz=(int)((float)Math.max((rgt-lft),(bot-top))*0.045f);
        waveCount=sz+AppWindow.random.nextInt(sz);

        if (AppWindow.random.nextBoolean()) {
            drawNormalWaveHorizontal(lft,top,rgt,bot,color,frameColor,waveCount);
        }
        else {
            drawNormalWaveVertical(lft,top,rgt,bot,color,frameColor,waveCount);
        }
    }

    public void generateMetalPanel(int lft, int top, int rgt, int bot, RagColor metalColor, RagColor altMetalColor, int edgeSize, int screwSize, boolean isBox) {
        int lft2, rgt2, top2, bot2, sz, panelType;
        RagColor color, screwColor, frameColor;

            // colors

        if (AppWindow.random.nextBoolean()) {
            color=metalColor;
            screwColor=altMetalColor;
        }
        else {
            color=altMetalColor;
            screwColor=metalColor;
        }

        frameColor=adjustColorRandom(color,0.85f,0.95f);

            // the plate

        createPerlinNoiseData(16,16);
        drawRect(lft,top,rgt,bot,color);
        drawPerlinNoiseRect(lft,top,rgt,bot,0.8f,1.0f);

        drawMetalShine(lft,top,rgt,bot,color);
        draw3DFrameRect(lft,top,rgt,bot,edgeSize,frameColor,true);

            // variations

        panelType = (isBox) ? 0 : AppWindow.random.nextInt(4);

        switch (panelType) {

                // internal box

            case 0:
                sz=((edgeSize+screwSize)*2)+(AppWindow.random.nextInt(edgeSize*3));
                lft2=lft+sz;
                rgt2=rgt-sz;
                top2=top+sz;
                bot2=bot-sz;
                frameColor=adjustColorRandom(color,0.75f,0.85f);
                draw3DFrameRect(lft2,top2,rgt2,bot2,edgeSize,frameColor,false);
                drawMetalShine((lft2+edgeSize),(top2+edgeSize),(rgt2-edgeSize),(bot2-edgeSize),color);

                sz=edgeSize+screwSize;
                generateMetalScrews((lft+sz),(top+sz),(rgt-sz),(bot-sz),screwColor,screwSize);
                break;

                // corrugation

            case 1:
                sz=(int)((float)edgeSize*2.5f);
                generateMetalCorrugation((lft+sz),(top+sz),(rgt-sz),(bot-sz),color);
                break;

                // wave

            case 2:
                generateMetalWave((lft+edgeSize),(top+edgeSize),(rgt-edgeSize),(bot-edgeSize),color);
                break;

                // empty

            case 3:
                sz=edgeSize+screwSize;
                generateMetalScrews((lft+sz),(top+sz),(rgt-sz),(bot-sz),screwColor,screwSize);
                break;
        }
    }

    private void generateMetalPlates(RagColor metalColor, RagColor altMetalColor, int edgeSize, int screwSize) {
        int mx, my;

            // either single, dual, or 4 panel

        mx=textureSize/2;
        my=textureSize/2;

        switch (AppWindow.random.nextInt(3)) {
            case 0:
                generateMetalPanel(0, 0, textureSize, textureSize, metalColor, altMetalColor, edgeSize, screwSize, false);
                break;
            case 1:
                generateMetalPanel(0, 0, mx, textureSize, metalColor, altMetalColor, edgeSize, screwSize, false);
                generateMetalPanel(mx, 0, textureSize, textureSize, metalColor, altMetalColor, edgeSize, screwSize, false);
                break;
            case 2:
                generateMetalPanel(0, 0, mx, my, metalColor, altMetalColor, edgeSize, screwSize, false);
                generateMetalPanel(mx, 0, textureSize, my, metalColor, altMetalColor, edgeSize, screwSize, false);
                generateMetalPanel(0, my, mx, textureSize, metalColor, altMetalColor, edgeSize, screwSize, false);
                generateMetalPanel(mx, my, textureSize, textureSize, metalColor, altMetalColor, edgeSize, screwSize, false);
                break;
        }
    }

    private void generateMetalHexagon(RagColor metalColor, RagColor altMetalColor, int edgeSize) {
        int x, y, lft, top, pointSize, xCount, yCount, xSize, ySize;
        boolean hasBorder;
        RagColor color;

        hasBorder = AppWindow.random.nextFloat() > 0.7f;

        xCount = 2 + (2 * AppWindow.random.nextInt(2));
        yCount = 2 + (2 * AppWindow.random.nextInt(3));

        xSize=textureSize/xCount;
        ySize=textureSize/yCount;

        pointSize=(int)((float)xSize*0.1f);

        lft=0;

        for (x=0;x<=xCount;x++) {
            top=((x&0x1)==0)?0:-(ySize/2);

            for (y = 0; y <= yCount; y++) {

                if (((y == 0) || (y == yCount)) && (hasBorder)) {
                    color = altMetalColor;
                } else {
                    if ((x != 0) && (x != xCount) && (y != 0) && (y != yCount)) {
                        color = adjustColorRandom(metalColor, 0.9f, 1.1f);
                    } else {
                        color = metalColor;
                    }
                }

                drawHexagon(lft,top,((lft+xSize)-pointSize),(top+ySize),pointSize,edgeSize,color);
                top+=ySize;
            }

            lft+=xSize;
        }
    }

    private void generateMetalTreads(RagColor metalColor, int edgeSize, int screwSize) {
        int n, ty, by, yAdd, treadCount;
        boolean alternateScrews;
        RagColor color, altMetalColor, frameColor, outlineColor;

        alternateScrews = AppWindow.random.nextBoolean();
        altMetalColor = adjustColorRandom(metalColor, 0.7f, 1.1f);

        treadCount = 4 + AppWindow.random.nextInt(4);

        ty = 0;
        yAdd = textureSize / treadCount;

        for (n = 0; n != treadCount; n++) {
            by = (n == (treadCount - 1)) ? textureSize : (ty + yAdd);

            // the plank
            color = ((n & 0x1) == 0) ? metalColor : altMetalColor;
            frameColor = adjustColor(color, 0.7f);

            createPerlinNoiseData(16, 16);
            drawRect(0, ty, textureSize, by, color);
            drawPerlinNoiseRect(0, ty, textureSize, by, 0.8f, 1.0f);
            drawMetalShine(0, ty, textureSize, by, color);
            draw3DFrameRect(0, ty, textureSize, by, edgeSize, frameColor, true);

            // any screws
            if ((!alternateScrews) || ((n & 0x1) == 0)) {
                color = ((n & 0x1) != 0) ? metalColor : altMetalColor;
                outlineColor = adjustColor(color, 0.5f);
                drawScrew(screwSize, (((ty + by) / 2) - (screwSize / 2)), color, outlineColor, screwSize, edgeSize);
                drawScrew((textureSize - (screwSize * 2)), (((ty + by) / 2) - (screwSize / 2)), (((n & 0x1) != 0) ? metalColor : altMetalColor), outlineColor, screwSize, edgeSize);
            }

            ty += yAdd;
        }
    }

        //
        // metal bitmaps
        //

    @Override
    public void generateInternal() {
        int edgeSize, screwSize;
        RagColor metalColor, altMetalColor;

        metalColor=getRandomColor();
        altMetalColor=getRandomColor();
        edgeSize = 4 + AppWindow.random.nextInt(5);
        screwSize = 5 + AppWindow.random.nextInt(25);

        switch (AppWindow.random.nextInt(3)) {
            case 0:
                generateMetalPlates(metalColor, altMetalColor, edgeSize, screwSize);
                break;
            case 1:
                generateMetalHexagon(metalColor, altMetalColor, edgeSize);
                break;
            default:
                generateMetalTreads(metalColor, edgeSize, screwSize);
                break;
        }

            // finish with the metallic-roughness

        createMetallicRoughnessMap(0.5f,0.6f);
    }
}
