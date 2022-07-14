package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

@BitmapInterface
public class BitmapStoneWall extends BitmapBase
{
    public BitmapStoneWall()    {
        super();

        hasNormal=true;
        hasMetallicRoughness=true;
        hasEmissive=false;
        hasAlpha=false;
    }

    protected void generateSingleStoneRow(int top, int bot, int yAdd, float[] backgroundData, float[] stoneColorData, float[] stoneNormalData, RagColor stoneColor, RagColor altStoneColor) {
        int lft, rgt, xOff, yOff, edgeSize;
        float xRoundFactor, yRoundFactor, normalZFactor;
        RagColor drawStoneColor, outlineColor;

        lft = 0;

        while (true) {
            rgt = lft + (yAdd + (int) (AppWindow.random.nextFloat((float) yAdd * 0.8f)));
            if (rgt >= textureSize) {
                rgt = textureSize - 1;
            }

            // special check if next stone would be too small,
            // so enlarge this stone to cover it
            if ((textureSize - rgt) < yAdd) {
                rgt = textureSize - 1;
            }

            // the stone itself
            drawStoneColor = adjustColorRandom(((AppWindow.random.nextFloat() < 0.7f) ? stoneColor : altStoneColor), 0.7f, 1.2f);

            xOff = (int) (AppWindow.random.nextFloat() * ((float) textureSize * 0.01f));
            yOff = (int) (AppWindow.random.nextFloat() * ((float) textureSize * 0.01f));

            edgeSize = 30 + AppWindow.random.nextInt(80);
            xRoundFactor = 0.02f + (AppWindow.random.nextFloat(0.05f));
            yRoundFactor = 0.02f + (AppWindow.random.nextFloat(0.05f));
            normalZFactor = 0.2f + (AppWindow.random.nextFloat(0.2f));

            if (AppWindow.random.nextBoolean()) {
                createPerlinNoiseData(16, 16);
            } else {
                createPerlinNoiseData(32, 32);
            }
            createNormalNoiseData((2.0f + AppWindow.random.nextFloat(0.3f)), (0.3f + AppWindow.random.nextFloat(0.2f)));

            // draw on the background
            colorData = backgroundData.clone();

            outlineColor = adjustColor(drawStoneColor, 0.5f);
            drawOval((lft + xOff), (top + yOff), rgt, bot, 0.0f, 1.0f, xRoundFactor, yRoundFactor, edgeSize, 0.5f, drawStoneColor, outlineColor, normalZFactor, false, true, 0.4f, 1.2f);

            // gravity distortions to make stones unique
            gravityDistortEdges((lft + xOff), (top + yOff), rgt, bot, 10, 35, 5);

            // and copy over
            blockCopy(colorData, normalData, (lft + xOff), (top + yOff), rgt, bot, stoneColorData, stoneNormalData);

            lft = rgt;
            if (rgt == (textureSize - 1)) {
                break;
            }
        }

    }

    @Override
    public void generateInternal() {
        int y, yCount, yAdd, top, bot;
        float[] backgroundData, stoneColorData, stoneNormalData;
        RagColor stoneColor, altStoneColor, groutColor;

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

        // we draw the stones all alone on the noise
            // background so we can distort the stones and
            // only catch the background

        backgroundData=colorData.clone();
        stoneColorData = colorData.clone();
        stoneNormalData = normalData.clone();

            // draw the stones

        yCount = 4 + AppWindow.random.nextInt(6);
        yAdd=textureSize/yCount;

        top=0;

        for (y=0;y!=yCount;y++) {
            bot=(y==(yCount-1))?(textureSize-1):(top+yAdd);
            if (bot >= textureSize) {
                bot = textureSize - 1;
            }

            generateSingleStoneRow(top, bot, yAdd, backgroundData, stoneColorData, stoneNormalData, stoneColor, altStoneColor);

            top+=yAdd;
        }

            // finally push over the stone copy version

        colorData = stoneColorData;
        normalData = stoneNormalData;

            // finish with the metallic-roughness

        createMetallicRoughnessMap(0.5f,0.5f);
    }
}
