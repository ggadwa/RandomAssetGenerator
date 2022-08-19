package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

@BitmapInterface
public class BitmapStoneWoodWall extends BitmapStoneWall {

    public BitmapStoneWoodWall(int textureSize) {
        super(textureSize);

        hasNormal = true;
        hasMetallicRoughness = true;
        hasEmissive = false;
        hasAlpha = false;
    }

    @Override
    public void generateInternal() {
        int y, yCount, yAdd, top, bot, edgeSize;
        float[] backgroundData, stoneColorData, stoneNormalData;
        boolean wasBoard;
        RagColor stoneColor, altStoneColor, woodColor, groutColor;

        stoneColor = getRandomColor();
        altStoneColor = getRandomColorDull(0.9f);
        woodColor = getRandomColor();
        groutColor = getRandomGray(0.35f, 0.55f);

        // the noise grout
        drawRect(0, 0, textureSize, textureSize, groutColor);
        createPerlinNoiseData(32, 32);
        drawStaticNoiseRect(0, 0, textureSize, textureSize, 0.5f, 1.1f);
        drawPerlinNoiseRect(0, 0, textureSize, textureSize, 0.3f, 0.9f);
        blur(colorData, 0, 0, textureSize, textureSize, 1, false);

        createNormalNoiseData(2.5f, 0.5f);
        drawNormalNoiseRect(0, 0, textureSize, textureSize);
        blur(normalData, 0, 0, textureSize, textureSize, 1, false);

        // we draw the stones all alone on the noise
        // background so we can distort the stones and
        // only catch the background
        backgroundData = colorData.clone();
        stoneColorData = colorData.clone();
        stoneNormalData = normalData.clone();

        // draw the stones
        yCount = 4 + AppWindow.random.nextInt(6);
        yAdd = textureSize / yCount;

        top = 0;
        wasBoard = true;

        for (y = 0; y != yCount; y++) {
            bot = (y == (yCount - 1)) ? (textureSize - 1) : (top + yAdd);
            if (bot >= textureSize) {
                bot = textureSize - 1;
            }

            if ((AppWindow.random.nextBoolean()) || (wasBoard)) {
                generateSingleStoneRow(top, bot, yAdd, backgroundData, stoneColorData, stoneNormalData, stoneColor, altStoneColor);
                wasBoard = false;
            } else {
                // need to copy this over because how stones are drawn
                edgeSize = 3 + AppWindow.random.nextInt(10);
                generateWoodDrawBoard(0, top, textureSize, bot, edgeSize, woodColor);
                drawRectAlpha(0, top, textureSize, bot, 0.0f);
                blockCopy(colorData, normalData, 0, top, textureSize, bot, stoneColorData, stoneNormalData);
                wasBoard = true;
            }

            top += yAdd;
        }

        // finally push over the stone copy version
        colorData = stoneColorData;
        normalData = stoneNormalData;

        // finish with the metallic-roughness
        createMetallicRoughnessMap(0.5f, 0.5f);
    }
}
