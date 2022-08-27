package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.utility.RagColor;

public class BitmapStonePattern extends BitmapStoneRow {

    public BitmapStonePattern(int textureSize) {
        super(textureSize);

        hasNormal = true;
        hasMetallicRoughness = true;
        hasEmissive = false;
        hasAlpha = false;
    }

    @Override
    public void generateInternal() {
        int y, yCount, yAdd, top, bot;
        float[] backgroundData, stoneColorData, stoneNormalData;
        RagColor stoneColor, altStoneColor, groutColor;

        stoneColor = getRandomColor();
        altStoneColor = getRandomColorDull(0.9f);
        groutColor = getRandomGrayColor(0.35f, 0.55f);

        // the noise grout
        drawGrout();

        // we draw the stones all alone on the noise
        // background so we can distort the stones and
        // only catch the background
        backgroundData = colorData.clone();
        stoneColorData = colorData.clone();
        stoneNormalData = normalData.clone();

        createNormalNoiseData(2.5f, 0.5f);

        // draw the stones
        yCount = 4 + AppWindow.random.nextInt(6);
        yAdd = textureSize / yCount;

        // create perlin based on # of stones
        createPerlinNoiseForStoneSize(yCount);

        // draw the stones
        top = 0;

        for (y = 0; y != yCount; y++) {
            bot = (y == (yCount - 1)) ? (textureSize - 1) : (top + yAdd);
            if (bot >= textureSize) {
                bot = textureSize - 1;
            }

            generateSingleStoneRow(top, bot, yAdd, backgroundData, stoneColorData, stoneNormalData, stoneColor, altStoneColor);

            top += yAdd;
        }

        // finally push over the stone copy version
        colorData = stoneColorData;
        normalData = stoneNormalData;

        // finish with the metallic-roughness
        createMetallicRoughnessMap(0.5f, 0.5f);
    }

}
