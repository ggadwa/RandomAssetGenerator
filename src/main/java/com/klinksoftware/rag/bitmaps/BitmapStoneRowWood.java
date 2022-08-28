package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

@BitmapInterface
public class BitmapStoneRowWood extends BitmapStoneRow {

    public BitmapStoneRowWood(int textureSize) {
        super(textureSize);

        hasNormal = true;
        hasMetallicRoughness = true;
        hasEmissive = false;
        hasAlpha = false;
    }

    @Override
    public void generateInternal() {
        int y, yCount, yAdd, top, bot;
        int boardCount, boardLft, boardRgt, boardEdgeSize, boardNailSize;
        float[] backgroundData, stoneColorData, stoneNormalData;
        boolean wasBoard, boardNail, boardDoubleNail;
        RagColor stoneColor, altStoneColor, woodColor;

        stoneColor = getRandomColor();
        altStoneColor = getRandomColorDull(0.9f);
        woodColor = getRandomWoodColor();

        // the noise grout
        drawGrout();

        // we draw the stones all alone on the noise
        // background so we can distort the stones and
        // only catch the background
        backgroundData = colorData.clone();
        stoneColorData = colorData.clone();
        stoneNormalData = normalData.clone();

        createNormalNoiseData(2.5f, 0.5f);

        // number of stones
        yCount = 5 + AppWindow.random.nextInt(5);
        yAdd = textureSize / yCount;

        // create perlin based on # of stones
        createPerlinNoiseForStoneSize(yCount);

        // board settings
        boardLft = 0;
        boardRgt = textureSize;
        boardEdgeSize = 3 + AppWindow.random.nextInt(textureSize / 70);
        boardNailSize = yAdd / 10;
        boardNail = AppWindow.random.nextBoolean();
        boardDoubleNail = AppWindow.random.nextBoolean();
        if (AppWindow.random.nextBoolean()) {
            boardLft -= boardEdgeSize;
            boardRgt += boardEdgeSize;
        }

        // draw stones
        top = 0;
        boardCount = 0;
        wasBoard = true;

        for (y = 0; y != yCount; y++) {
            bot = (y == (yCount - 1)) ? (textureSize - 1) : (top + yAdd);
            if (bot >= textureSize) {
                bot = textureSize - 1;
            }

            if ((AppWindow.random.nextBoolean()) || (wasBoard) || (boardCount == 2)) {
                generateSingleStoneRow(top, bot, yAdd, backgroundData, stoneColorData, stoneNormalData, false, stoneColor, altStoneColor);
                wasBoard = false;
            } else {
                // need to copy this over because how stones are drawn
                generateWoodDrawBoard(boardLft, top, boardRgt, bot, boardEdgeSize, woodColor);
                if (boardNail) {
                    generateWoodDrawBoardNails(0, top, textureSize, bot, boardEdgeSize, boardNailSize, boardDoubleNail);
                }
                drawRectAlpha(0, top, textureSize, bot, 0.0f);
                blockCopy(colorData, normalData, 0, top, textureSize, bot, stoneColorData, stoneNormalData);
                boardCount++;
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
