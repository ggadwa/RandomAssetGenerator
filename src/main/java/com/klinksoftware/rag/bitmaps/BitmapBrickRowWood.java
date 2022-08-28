package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

@BitmapInterface
public class BitmapBrickRowWood extends BitmapBrickRow {

    public BitmapBrickRowWood(int textureSize) {
        super(textureSize);

        hasNormal = true;
        hasMetallicRoughness = true;
        hasEmissive = false;
        hasAlpha = false;
    }

    @Override
    public void generateInternal() {
        int y, xCount, xAdd, yCount, yAdd, halfWid, top, bot, edgeSize, paddingSize;
        int boardCount, boardLft, boardRgt, boardEdgeSize, boardNailSize;
        boolean halfBrick, wasBoard, boardNail, boardDoubleNail;
        RagColor brickColor, altBrickColor, woodColor;

        brickColor = getRandomColor();
        altBrickColor = getRandomColor();
        woodColor = getRandomWoodColor();

        edgeSize = 3 + AppWindow.random.nextInt(textureSize / 70);
        paddingSize = 3 + AppWindow.random.nextInt(textureSize / 100);

        // create noise data
        createPerlinNoiseData(32, 32);
        createNormalNoiseData(1.5f, 0.5f);

        // grout is a static noise color
        drawGrout();

        // brick settings
        xCount = 4 + AppWindow.random.nextInt(4);
        xAdd = textureSize / xCount;
        halfWid = xAdd / 2;

        yCount = 4 + AppWindow.random.nextInt(5);
        yAdd = textureSize / yCount;

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
            boardNail = false;
        }

        // draw the bricks
        top = 0;
        boardCount = 0;
        wasBoard = false;
        halfBrick = false;

        for (y = 0; y != yCount; y++) {

            if ((AppWindow.random.nextBoolean()) || (wasBoard) || (boardCount == 2)) {
                generateSingleBrickRow(xCount, xAdd, halfWid, top, yAdd, edgeSize, paddingSize, halfBrick, brickColor, altBrickColor);
                halfBrick = !halfBrick;
                wasBoard = false;
            } else {
                bot = top + yAdd;
                generateWoodDrawBoard(boardLft, top, boardRgt, bot, boardEdgeSize, woodColor);
                if (boardNail) {
                    generateWoodDrawBoardNails(0, top, textureSize, bot, boardEdgeSize, boardNailSize, boardDoubleNail);
                }
                boardCount++;
                wasBoard = true;
            }

            top += yAdd;
        }

        // finish with the metallic-roughness
        createMetallicRoughnessMap(0.5f, 0.4f);
    }

}
