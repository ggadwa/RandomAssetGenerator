package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

@BitmapInterface
public class BitmapBrickRow extends BitmapBase {
    public BitmapBrickRow(int textureSize) {
        super(textureSize);

        hasNormal=true;
        hasMetallicRoughness=true;
        hasEmissive=false;
        hasAlpha=false;
    }

        //
        // brick bitmaps
        //

    protected void generateSingleBrick(int lft, int top, int rgt, int bot, int edgeSize, int paddingSize, RagColor brickColor, RagColor altBrickColor, boolean isHalf, boolean isSmall, boolean isLarge)    {
        int sx, ex, streakWid;
        float f;
        RagColor drawBrickColor, lineColor, streakColor;

            // the brick

        f=1.0f;
        if (!((lft<0) || (rgt>textureSize))) {        // don't darken bricks that fall off edges
            f=0.6f+(AppWindow.random.nextFloat()*0.4f);
        }

        if (isLarge) {
            drawBrickColor = adjustColor(altBrickColor, f);
            drawRect(0,top,textureSize,(bot-paddingSize),drawBrickColor);
            drawPerlinNoiseRect(0, top, textureSize, (bot - paddingSize), 0.8f, 1.3f);
            drawNormalNoiseRect(0, top, textureSize, (bot - paddingSize));
            draw3DDarkenFrameRect(-edgeSize, top, (textureSize + edgeSize), (bot - paddingSize), edgeSize, (0.85f + AppWindow.random.nextFloat(0.1f)), true);
        }
        else {
            drawBrickColor = adjustColor((isSmall ? altBrickColor : brickColor), f);
            drawRect(lft, top, (rgt - paddingSize), (bot - paddingSize), drawBrickColor);
            drawPerlinNoiseRect(lft, top, (rgt - paddingSize), (bot - paddingSize), 0.8f, 1.3f);
            drawNormalNoiseRect(lft, top, (rgt - paddingSize), (bot - paddingSize));
            draw3DDarkenFrameRect(lft, top, (rgt - paddingSize), (bot - paddingSize), edgeSize, (0.85f + AppWindow.random.nextFloat(0.1f)), true);
        }

        // any streaks/stains/cracks
        // do not do on odd bricks
        if ((!isHalf) && (!isSmall)) {
            lft += edgeSize;
            rgt -= (edgeSize + paddingSize);
            top += edgeSize;
            bot -= (edgeSize + paddingSize);

            // any cracks
            if (AppWindow.random.nextFloat()<0.1f) {
                if ((rgt-lft)>45) {
                    sx=(lft+15)+AppWindow.random.nextInt((rgt-15)-(lft+15));
                    ex=sx+((5+AppWindow.random.nextInt(25))-15);

                    lineColor=adjustColorRandom(drawBrickColor,0.65f,0.75f);
                    drawVerticalCrack(sx, top, bot, lft, rgt, (int) Math.signum(AppWindow.random.nextFloat() - 0.5f), 10, lineColor, true);
                }
            }

            // streaks
            if (AppWindow.random.nextFloat()<0.2f) {
                streakWid = (int) (((float) (rgt - lft) * 0.3f) + (AppWindow.random.nextFloat((float) (rgt - lft) * 0.3f)));
                if (streakWid<10) streakWid=10;
                if (streakWid>(int)((float)textureSize*0.1f)) streakWid=(int)((float)textureSize*0.1f);

                sx=lft+AppWindow.random.nextInt((rgt-lft)-streakWid);
                ex=sx+streakWid;

                streakColor=adjustColorRandom(drawBrickColor,0.65f,0.75f);
                drawStreakDirt(sx, top, ex, bot, 5, 0.25f, 0.45f, streakColor);
            }
        }
    }

    protected void generateSingleBrickRow(int xCount, int xAdd, int halfWid, int top, int yAdd, int edgeSize, int paddingSize, boolean halfBrick, RagColor brickColor, RagColor altBrickColor) {
        int x, lft;

        // special lines (full line or double bricks)
        if (AppWindow.random.nextFloat() < 0.2f) {
            if (AppWindow.random.nextBoolean()) {
                generateSingleBrick(0, top, (textureSize - 1), (top + yAdd), edgeSize, paddingSize, brickColor, altBrickColor, false, false, true);
            } else {
                lft = 0;

                for (x = 0; x != xCount; x++) {
                    generateSingleBrick(lft, top, (lft + halfWid), (top + yAdd), edgeSize, paddingSize, brickColor, altBrickColor, false, true, false);
                    generateSingleBrick((lft + halfWid), top, ((x == (xCount - 1)) ? (textureSize - 1) : (lft + xAdd)), (top + yAdd), edgeSize, paddingSize, brickColor, altBrickColor, false, true, false);
                    lft += xAdd;
                }
            }
        } // regular lines
        else {
            if (halfBrick) {
                lft = -halfWid;

                for (x = 0; x != (xCount + 1); x++) {
                    generateSingleBrick(lft, top, (lft + xAdd), (top + yAdd), edgeSize, paddingSize, brickColor, altBrickColor, ((x == 0) || (x == xCount)), false, false);
                    lft += xAdd;
                }
            } else {
                lft = 0;

                for (x = 0; x != xCount; x++) {
                    generateSingleBrick(lft, top, ((x == (xCount - 1)) ? (textureSize - 1) : (lft + xAdd)), (top + yAdd), edgeSize, paddingSize, brickColor, altBrickColor, (lft < 0), false, false);
                    lft += xAdd;
                }
            }
        }
    }

    @Override
    public void generateInternal()    {
        int x, y, xCount, xAdd, yCount, yAdd, halfWid, lft, top, edgeSize, paddingSize;
        boolean halfBrick;
        RagColor brickColor, altBrickColor;

        brickColor=getRandomColor();
        altBrickColor = getRandomColor();

        edgeSize = 3 + AppWindow.random.nextInt(textureSize / 70);
        paddingSize = 3 + AppWindow.random.nextInt(textureSize / 100);

        // create noise data
        createPerlinNoiseData(32,32);
        createNormalNoiseData(1.5f,0.5f);

        // grout is a static noise color
        drawGrout();

        // draw the bricks
        xCount=4+AppWindow.random.nextInt(4);
        xAdd=textureSize/xCount;
        halfWid=xAdd/2;

        yCount=4+AppWindow.random.nextInt(5);
        yAdd=textureSize/yCount;

        top=0;
        halfBrick=false;

        for (y=0;y!=yCount;y++) {
            generateSingleBrickRow(xCount, xAdd, halfWid, top, yAdd, edgeSize, paddingSize, halfBrick, brickColor, altBrickColor);

            top+=yAdd;
            halfBrick=!halfBrick;
        }

        // finish with the metallic-roughness
        createMetallicRoughnessMap(0.5f,0.4f);
    }

}
