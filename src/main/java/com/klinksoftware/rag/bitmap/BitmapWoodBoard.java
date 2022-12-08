package com.klinksoftware.rag.bitmap;

import com.klinksoftware.rag.bitmap.utility.BitmapInterface;
import com.klinksoftware.rag.bitmap.utility.BitmapBase;
import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.utility.RagColor;

@BitmapInterface
public class BitmapWoodBoard extends BitmapBase {

    public BitmapWoodBoard() {
        super();

        hasNormal = true;
        hasMetallicRoughness = true;
        hasEmissive = false;
        hasAlpha = false;
    }

    public void generateSingleWoodPlank(int lft, int top, int rgt, int bot, int edgeSize, int boardNailSize, boolean boardNail, boolean boardDoubleNail, RagColor woodColor) {
        generateWoodDrawBoard(lft, top, rgt, bot, edgeSize, woodColor);
        generateWoodDrawBoardNails(lft, top, rgt, bot, edgeSize, boardNailSize, boardDoubleNail);
    }

    public void generateWoodPlanks(int boardCount, int boardSize, int edgeSize, int boardNailSize, boolean boardNail, boolean boardDoubleNail, RagColor woodColor) {
        int n, y, ty, by, lft, rgt, boardType;

        // perlin noise
        createPerlinNoiseData(32, 32);

        // regular wood planking
        lft = 0;

        y = (int) ((float) textureSize * 0.5);
        ty = (int) ((float) textureSize * 0.33);
        by = (int) ((float) textureSize * 0.66);

        for (n = 0; n != boardCount; n++) {
            rgt = lft + boardSize;
            if (n == (boardCount - 1)) {
                rgt = textureSize;
            }

            boardType = AppWindow.random.nextInt(5);

            switch (boardType) {
                case 0:
                    generateSingleWoodPlank(lft, 0, rgt, textureSize, edgeSize, boardNailSize, boardNail, boardDoubleNail, woodColor);
                    break;
                case 1:
                    generateSingleWoodPlank(lft, 0, rgt, y, edgeSize, boardNailSize, boardNail, boardDoubleNail, woodColor);
                    generateSingleWoodPlank(lft, y, rgt, textureSize, edgeSize, boardNailSize, boardNail, boardDoubleNail, woodColor);
                    break;
                case 2:
                    generateSingleWoodPlank(lft, -edgeSize, rgt, y, edgeSize, boardNailSize, boardNail, boardDoubleNail, woodColor);
                    generateSingleWoodPlank(lft, y, rgt, (textureSize + edgeSize), edgeSize, boardNailSize, boardNail, boardDoubleNail, woodColor);
                    break;
                case 3:
                    generateSingleWoodPlank(lft, 0, rgt, ty, edgeSize, boardNailSize, boardNail, boardDoubleNail, woodColor);
                    generateSingleWoodPlank(lft, ty, rgt, by, edgeSize, boardNailSize, boardNail, boardDoubleNail, woodColor);
                    generateSingleWoodPlank(lft, by, rgt, textureSize, edgeSize, boardNailSize, boardNail, boardDoubleNail, woodColor);
                    break;
                case 4:
                    generateSingleWoodPlank(lft, -edgeSize, rgt, (textureSize + edgeSize), edgeSize, boardNailSize, boardNail, boardDoubleNail, woodColor);
                    break;
            }

            lft = rgt;
        }
    }

    @Override
    public void generateInternal() {
        int boardCount, boardSize, edgeSize;
        int boardNailSize;
        boolean boardNail, boardDoubleNail;
        RagColor woodColor;

        // some random values
        boardCount = 4 + AppWindow.random.nextInt(12);
        boardSize = textureSize / boardCount;
        edgeSize = (int) (((float) textureSize * 0.005f) + (AppWindow.random.nextFloat() * ((float) textureSize * 0.005f)));
        woodColor = getRandomWoodColor();

        // nails
        boardNailSize = boardSize / 10;
        boardNail = AppWindow.random.nextBoolean();
        boardDoubleNail = AppWindow.random.nextBoolean();

        // the planks
        generateWoodPlanks(boardCount, boardSize, edgeSize, boardNailSize, boardNail, boardDoubleNail, woodColor);

        createMetallicRoughnessMap(0.4f, 0.2f);
    }
}
