package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

@BitmapInterface
public class BitmapBrickPattern extends BitmapBrickRow {

    public BitmapBrickPattern() {
        super();

        hasNormal = true;
        hasMetallicRoughness = true;
        hasEmissive = false;
        hasAlpha = false;
    }

    @Override
    public void generateInternal() {
        int[][] grid;
        int n, x, y, gx, gy, gridPixelSize, tryCount;
        int gridSize, lft, top, rgt, bot, edgeSize, paddingSize;
        boolean badSpot;
        int[] sz;
        int[][] brickSizes = {{2, 2}, {3, 3}, {4, 4}, {2, 1}, {1, 2}, {3, 1}, {1, 3}, {2, 3}, {3, 2}};
        RagColor brickColor, altBrickColor, groutColor;

        brickColor = getRandomColor();
        altBrickColor = getRandomColor();
        groutColor = getRandomGray(0.4f, 0.6f);

        edgeSize = 3 + AppWindow.random.nextInt(7);
        paddingSize = 3 + AppWindow.random.nextInt(5);

        // create noise data
        createPerlinNoiseData(32, 32);
        createNormalNoiseData(1.5f, 0.5f);

        // grout is a static noise color
        drawRect(0, 0, textureSize, textureSize, groutColor);
        drawStaticNoiseRect(0, 0, textureSize, textureSize, 1.0f, 1.4f);
        blur(colorData, 0, 0, textureSize, textureSize, 1, false);

        gridSize = AppWindow.random.nextBoolean() ? 8 : 4;
        grid = new int[gridSize][gridSize];
        gridPixelSize = textureSize / gridSize;

        for (n = 0; n != gridSize; n++) {
            // random brick size
            sz = brickSizes[AppWindow.random.nextInt(brickSizes.length)];

            // find a place for brick
            tryCount = 0;
            while (tryCount < 10) {
                x = AppWindow.random.nextInt(gridSize - (sz[0] - 1));
                y = AppWindow.random.nextInt(gridSize - (sz[1] - 1));

                badSpot = false;

                for (gy = y; gy < (y + sz[1]); gy++) {
                    for (gx = x; gx < (x + sz[0]); gx++) {
                        if (grid[gx][gy] != 0) {
                            badSpot = true;
                            break;
                        }
                    }
                }

                if (!badSpot) {
                    for (gy = y; gy < (y + sz[1]); gy++) {
                        for (gx = x; gx < (x + sz[0]); gx++) {
                            grid[gx][gy] = 1;
                        }
                    }

                    lft = x * gridPixelSize;
                    top = y * gridPixelSize;
                    rgt = lft + (gridPixelSize * sz[0]);
                    bot = top + (gridPixelSize * sz[1]);

                    generateSingleBrick(lft, top, rgt, bot, edgeSize, paddingSize, brickColor, altBrickColor, false, false, false);

                }

                tryCount++;
            }

        }

        // fill in any missing bricks
        for (y = 0; y != gridSize; y++) {
            for (x = 0; x != gridSize; x++) {
                if (grid[x][y] != 0) {
                    continue;
                }

                lft = x * gridPixelSize;
                top = y * gridPixelSize;
                rgt = lft + gridPixelSize;
                bot = top + gridPixelSize;

                generateSingleBrick(lft, top, rgt, bot, edgeSize, paddingSize, brickColor, altBrickColor, false, false, false);
            }
        }

        // finish with the metallic-roughness
        createMetallicRoughnessMap(0.5f, 0.4f);
    }

}
