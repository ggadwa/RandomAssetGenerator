package com.klinksoftware.rag.bitmap;

import com.klinksoftware.rag.bitmap.utility.BitmapInterface;
import com.klinksoftware.rag.bitmap.utility.BitmapBase;
import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

@BitmapInterface
public class BitmapStorage extends BitmapBase {

    public BitmapStorage(int textureSize) {
        super(textureSize);

        hasNormal = true;
        hasMetallicRoughness = true;
        hasEmissive = false;
        hasAlpha = false;
    }

    public void generateWoodPlanks(int boardCount, int boardSize, int edgeSize, RagColor woodColor) {
        int n, lft, rgt;

        // perlin noise
        createPerlinNoiseData(32, 32);

        // regular wood planking
        lft = 0;

        for (n = 0; n != boardCount; n++) {
            rgt = lft + boardSize;
            if (n == (boardCount - 1)) {
                rgt = textureSize;
            }

            generateWoodDrawBoard(lft, 0, rgt, textureSize, edgeSize, woodColor);

            lft = rgt;
        }
    }

    private void generateMetalTread(int lx, int ty, int rx, int by, int edgeSize, RagColor color) {
        createPerlinNoiseData(16, 16);
        drawRect(lx, ty, rx, by, color);
        drawPerlinNoiseRect(lx, ty, rx, by, 0.8f, 1.0f);
        drawMetalShine(lx, ty, rx, by, color);
        draw3DDarkenFrameRect(lx, ty, rx, by, edgeSize, (0.6f + AppWindow.random.nextFloat(0.1f)), true);
    }

    private void generateMetalTreads(int edgeSize, RagColor metalColor) {
        int n, ty, by, yAdd, treadCount;
        RagColor color, altMetalColor;

        altMetalColor = adjustColorRandom(metalColor, 0.7f, 1.1f);

        treadCount = 4 + AppWindow.random.nextInt(4);

        ty = 0;
        yAdd = textureSize / treadCount;

        for (n = 0; n != treadCount; n++) {
            by = (n == (treadCount - 1)) ? textureSize : (ty + yAdd);

            // the plank
            color = ((n & 0x1) == 0) ? metalColor : altMetalColor;
            generateMetalTread(0, ty, textureSize, by, edgeSize, color);

            ty += yAdd;
        }
    }

    @Override
    public void generateInternal() {
        int boardCount, boardSize, edgeSize, screwSize, screwOffset;
        RagColor innerColor, outerColor, screwColor, outlineColor;

        // some random values
        boardCount = 4 + AppWindow.random.nextInt(12);
        boardSize = textureSize / boardCount;

        // internal planks
        innerColor = getRandomColor();
        edgeSize = 2 + AppWindow.random.nextInt(3);

        if (AppWindow.random.nextBoolean()) {
            generateWoodPlanks(boardCount, boardSize, edgeSize, innerColor);
        } else {
            generateMetalTreads(edgeSize, innerColor);
        }

        // outside planks
        edgeSize = 3 + AppWindow.random.nextInt(4);

        if (AppWindow.random.nextBoolean()) {
            outerColor = getRandomWoodColor();
            generateWoodDrawBoard(0, 0, boardSize, textureSize, edgeSize, outerColor);
            generateWoodDrawBoard((textureSize - boardSize), 0, textureSize, textureSize, edgeSize, outerColor);
            generateWoodDrawBoard(boardSize, 0, (textureSize - boardSize), boardSize, edgeSize, outerColor);
            generateWoodDrawBoard(boardSize, (textureSize - boardSize), (textureSize - boardSize), textureSize, edgeSize, outerColor);
        } else {
            outerColor = getRandomMetalColor();
            generateMetalTread(0, 0, boardSize, textureSize, edgeSize, outerColor);
            generateMetalTread((textureSize - boardSize), 0, textureSize, textureSize, edgeSize, outerColor);
            generateMetalTread(boardSize, 0, (textureSize - boardSize), boardSize, edgeSize, outerColor);
            generateMetalTread(boardSize, (textureSize - boardSize), (textureSize - boardSize), textureSize, edgeSize, outerColor);
        }

        // screws
        screwSize = 15 + AppWindow.random.nextInt(15);
        if (screwSize > boardSize) {
            screwSize = boardSize - 1;
        }
        screwColor = getRandomColor();
        outlineColor = adjustColor(screwColor, 0.5f);
        edgeSize = 3 + AppWindow.random.nextInt(4);
        screwOffset = (boardSize - screwSize) / 2;

        if (AppWindow.random.nextBoolean()) {
            drawScrew(screwOffset, screwOffset, screwColor, outlineColor, screwSize, edgeSize);
            drawScrew((textureSize - (screwOffset + screwSize)), screwOffset, screwColor, outlineColor, screwSize, edgeSize);
        }
        if (AppWindow.random.nextBoolean()) {
            drawScrew(screwOffset, (textureSize - (screwOffset + screwSize)), screwColor, outlineColor, screwSize, edgeSize);
            drawScrew((textureSize - (screwOffset + screwSize)), (textureSize - (screwOffset + screwSize)), screwColor, outlineColor, screwSize, edgeSize);
        }

        // finish with the metallic-roughness
        if (AppWindow.random.nextBoolean()) {
            createMetallicRoughnessMap(0.4f, 0.2f);
        } else {
            createMetallicRoughnessMap(0.5f, 0.6f);
        }
    }
}
