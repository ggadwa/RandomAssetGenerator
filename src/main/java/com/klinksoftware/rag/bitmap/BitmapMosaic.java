package com.klinksoftware.rag.bitmap;

import com.klinksoftware.rag.bitmap.utility.BitmapInterface;
import com.klinksoftware.rag.bitmap.utility.BitmapBase;
import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

@BitmapInterface
public class BitmapMosaic extends BitmapBase
{
    public BitmapMosaic() {
        super();

        hasNormal=true;
        hasMetallicRoughness=true;
        hasEmissive=false;
        hasAlpha=false;
    }

        //
        // mosaic bitmaps
        //

    @Override
    public void generateInternal() {
        int x, y, mx, my, lft, rgt, top, bot, tileWid, tileHigh, splitCount, maxOffset;
        int n, maxMissingCount;
        int[][] missingTiles;
        float f;
        boolean hasBorder, blended, missing;
        RagColor mosaicColor, mosaic2Color, mosaic3Color, col;

            // some random values

        splitCount = 10 * (1 + AppWindow.random.nextInt(3));

        mosaicColor = getRandomColor();
        mosaic2Color = getRandomColorSkipColor(new RagColor[]{mosaicColor});
        mosaic3Color = getRandomColorSkipColor(new RagColor[]{mosaicColor, mosaic2Color});

        col = new RagColor(0.0f, 0.0f, 0.0f);

        // tile sizes
        tileWid=textureSize/splitCount;
        tileHigh=textureSize/splitCount;

        // clear canvases to grout
        drawGrout();

        // use a perlin noise rect for the colors
        if (AppWindow.random.nextBoolean()) {
            createPerlinNoiseData(16, 16);
        } else {
            createPerlinNoiseData(32, 32);
        }

        // create missing tiles
        maxMissingCount = AppWindow.random.nextInt(5);
        missingTiles = null;

        if (maxMissingCount != 0) {
            missingTiles = new int[maxMissingCount][2];

            for (n = 0; n != maxMissingCount; n++) {
                missingTiles[n][0] = AppWindow.random.nextInt(splitCount);
                missingTiles[n][1] = AppWindow.random.nextInt(splitCount);
            }
        }

        // draw the tiles
        hasBorder = AppWindow.random.nextBoolean();
        blended = AppWindow.random.nextBoolean();
        maxOffset = textureSize / 170;

        for (y=0;y!=splitCount;y++) {
            for (x = 0; x != splitCount; x++) {

                // every once and a while, a missing one
                missing = false;

                for (n = 0; n != maxMissingCount; n++) {
                    if ((x == missingTiles[n][0]) && (y == missingTiles[n][1])) {
                        missing = true;
                        break;
                    }
                }
                if (missing) {
                    continue;
                }

                // slightly random position
                lft = (x * tileWid) + AppWindow.random.nextInt(maxOffset);
                rgt = ((x * tileWid) + tileWid) - AppWindow.random.nextInt(maxOffset);
                top = (y * tileHigh) + AppWindow.random.nextInt(maxOffset);
                bot = ((y * tileHigh) + tileHigh) - AppWindow.random.nextInt(maxOffset);

                mx = lft + (tileWid / 2);
                my = top + (tileHigh / 2);

                // the color
                f = getPerlineColorFactorForPosition(mx, my);

                if ((hasBorder) && ((x == 0) || (y == 0))) {
                    col.setFromColorFactor(mosaicColor, mosaic3Color, f);
                } else {
                    if (blended) {
                        col.setFromColorFactor(mosaicColor, mosaic2Color, f);
                    } else {
                        col = (f > 0.5f) ? mosaic2Color : mosaicColor;
                    }
                }

                // draw
                drawRect(lft, top, rgt, bot, col);
                draw3DDarkenFrameRect(lft, top, rgt, bot, 1, (0.95f + AppWindow.random.nextFloat(0.05f)), true);

                // noise and blur
                drawStaticNoiseRect(lft, top, rgt, bot, 1.1f, 1.3f);
                blur(colorData, lft, top, rgt, bot, (textureSize / 500), true);
            }
        }

        // finish with the metallic-roughness
        createMetallicRoughnessMap(0.5f,0.5f);
    }
}
