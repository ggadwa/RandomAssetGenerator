package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

@BitmapInterface
public class BitmapStoneRow extends BitmapBase
{
    public BitmapStoneRow(int textureSize) {
        super(textureSize);

        hasNormal=true;
        hasMetallicRoughness=true;
        hasEmissive=false;
        hasAlpha=false;
    }

    protected void generateSingleStoneRow(int top, int bot, int yAdd, float[] backgroundData, float[] stoneColorData, float[] stoneNormalData, RagColor stoneColor, RagColor altStoneColor) {
        int lft, rgt, xOff, yOff, edgeSize;
        float xRoundFactor, yRoundFactor, normalZFactor, edgeColorFactor;
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

            xOff = 0; // (int) (AppWindow.random.nextFloat() * ((float) textureSize * 0.01f));
            yOff = 0; // (int) (AppWindow.random.nextFloat() * ((float) textureSize * 0.01f));

            edgeSize = (textureSize / 15) + AppWindow.random.nextInt(textureSize / 6);
            edgeColorFactor = 0.4f + AppWindow.random.nextFloat(0.2f);
            xRoundFactor = 0.01f + (AppWindow.random.nextFloat(0.03f));
            yRoundFactor = 0.01f + (AppWindow.random.nextFloat(0.03f));
            normalZFactor = 0.2f + (AppWindow.random.nextFloat(0.2f));

            createNormalNoiseData((2.0f + AppWindow.random.nextFloat(0.3f)), (0.3f + AppWindow.random.nextFloat(0.2f)));

            // draw on the background
            colorData = backgroundData.clone();

            outlineColor = adjustColor(drawStoneColor, (0.1f + AppWindow.random.nextFloat(0.2f)));
            drawOval((lft + xOff), (top + yOff), rgt, bot, 0.0f, 1.0f, xRoundFactor, yRoundFactor, edgeSize, edgeColorFactor, drawStoneColor, normalZFactor, false, true, (0.2f + AppWindow.random.nextFloat(0.3f)), (1.0f + AppWindow.random.nextFloat(0.2f)));
            drawFrameOval((lft + xOff), (top + yOff), rgt, bot, xRoundFactor, yRoundFactor, outlineColor);

            // gravity distortions to make stones unique
            gravityDistortEdges((lft + xOff), (top + yOff), rgt, bot, (8 + AppWindow.random.nextInt(4)), (textureSize / 14), (textureSize / 100));

            // and copy over
            blockCopy(colorData, normalData, (lft + xOff), (top + yOff), rgt, bot, stoneColorData, stoneNormalData);

            lft = rgt;
            if (rgt == (textureSize - 1)) {
                break;
            }
        }
    }

    protected void createPerlinNoiseForStoneSize(int yCount) {
        if (yCount < 6) {
            createPerlinNoiseData(16, 16);
        } else {
            createPerlinNoiseData(32, 32);
        }
    }

    @Override
    public void generateInternal() {
        int y, yCount, yAdd, top, bot;
        float[] backgroundData, stoneColorData, stoneNormalData;
        RagColor stoneColor, altStoneColor, groutColor;

        stoneColor=getRandomColor();
        altStoneColor=getRandomColorDull(0.9f);
        groutColor = getRandomGrayColor(0.35f, 0.55f);

        // the noise grout
        drawGrout();

        // we draw the stones all alone on the noise
        // background so we can distort the stones and
        // only catch the background

        backgroundData=colorData.clone();
        stoneColorData = colorData.clone();
        stoneNormalData = normalData.clone();

        createNormalNoiseData(2.5f, 0.5f);

        // draw the stones
        yCount = 4 + AppWindow.random.nextInt(6);
        yAdd = textureSize / yCount;

        // create perlin based on # of stones
        createPerlinNoiseForStoneSize(yCount);

        // draw the stones
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
