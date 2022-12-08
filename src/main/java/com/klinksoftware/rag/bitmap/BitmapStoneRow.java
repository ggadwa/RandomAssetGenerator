package com.klinksoftware.rag.bitmap;

import com.klinksoftware.rag.bitmap.utility.BitmapInterface;
import com.klinksoftware.rag.bitmap.utility.BitmapBase;
import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

@BitmapInterface
public class BitmapStoneRow extends BitmapBase {
    public BitmapStoneRow() {
        super();

        hasNormal=true;
        hasMetallicRoughness=true;
        hasEmissive=false;
        hasAlpha=false;
    }

    protected void generateSingleStone(int lft, int top, int rgt, int bot, float[] backgroundData, float[] stoneColorData, float[] stoneNormalData, boolean round, RagColor stoneColor, RagColor altStoneColor) {
        int xOff, yOff, edgeSize;
        float xRoundFactor, yRoundFactor, normalZFactor, edgeColorFactor;
        RagColor drawStoneColor, outlineColor;

        drawStoneColor = adjustColorRandom(((AppWindow.random.nextFloat() < 0.7f) ? stoneColor : altStoneColor), 0.7f, 1.2f);

        xOff = (int) (AppWindow.random.nextFloat((float) textureSize * 0.005f));
        yOff = (int) (AppWindow.random.nextFloat((float) textureSize * 0.005f));

        edgeSize = (textureSize / 15) + AppWindow.random.nextInt(textureSize / 6);
        edgeColorFactor = 0.4f + AppWindow.random.nextFloat(0.2f);
        if (round) {
            xRoundFactor = 0.01f + (AppWindow.random.nextFloat(0.1f));
            yRoundFactor = 0.01f + (AppWindow.random.nextFloat(0.1f));
        } else {
            xRoundFactor = 0.8f + (AppWindow.random.nextFloat(0.02f));
            yRoundFactor = 0.8f + (AppWindow.random.nextFloat(0.02f));
        }
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
    }

    protected void generateSingleStoneRow(int top, int bot, int yAdd, float[] backgroundData, float[] stoneColorData, float[] stoneNormalData, boolean round, RagColor stoneColor, RagColor altStoneColor) {
        int lft, rgt;

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

            generateSingleStone(lft, top, rgt, bot, backgroundData, stoneColorData, stoneNormalData, round, stoneColor, altStoneColor);

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
        RagColor stoneColor, altStoneColor;

        stoneColor=getRandomColor();
        altStoneColor = getRandomColorDull(0.9f);

        // the noise grout
        drawGrout();

        // we draw the stones all alone on the noise
        // background so we can distort the stones and
        // only catch the background

        backgroundData=colorData.clone();
        stoneColorData = colorData.clone();
        stoneNormalData = normalData.clone();

        createNormalNoiseData(2.5f, 0.5f);

        // number of stones
        yCount = 5 + AppWindow.random.nextInt(5);
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

            generateSingleStoneRow(top, bot, yAdd, backgroundData, stoneColorData, stoneNormalData, false, stoneColor, altStoneColor);

            top+=yAdd;
        }

        // finally push over the stone copy version
        colorData = stoneColorData;
        normalData = stoneNormalData;

        // finish with the metallic-roughness
        createMetallicRoughnessMap(0.5f,0.5f);
    }
}
