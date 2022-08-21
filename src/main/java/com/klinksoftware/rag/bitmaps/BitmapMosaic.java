package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

@BitmapInterface
public class BitmapMosaic extends BitmapBase
{
    public BitmapMosaic(int textureSize) {
        super(textureSize);

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
        float f;
        boolean hasBorder, blended;
        RagColor mosaicColor, mosaic2Color, mosaic3Color, col;

            // some random values

        splitCount = AppWindow.random.nextBoolean() ? 16 : 32;

        mosaicColor = getRandomColor();
        mosaic2Color = getRandomColor();
        mosaic3Color = getRandomColor();

        col = new RagColor(0.0f, 0.0f, 0.0f);

        // tile sizes
        tileWid=textureSize/splitCount;
        tileHigh=textureSize/splitCount;

        // clear canvases to grout
        drawGrout();

        // use a perlin noise rect for the colors
        createPerlinNoiseData(splitCount, splitCount);

        // draw the tiles
        hasBorder = AppWindow.random.nextBoolean();
        blended = AppWindow.random.nextBoolean();
        maxOffset = textureSize / 170;

        for (y=0;y!=splitCount;y++) {
            for (x=0;x!=splitCount;x++) {

                // slightly random position
                lft = (x * tileWid) + AppWindow.random.nextInt(maxOffset);
                rgt = ((x * tileWid) + tileWid) - AppWindow.random.nextInt(maxOffset);
                top = (y * tileHigh) + AppWindow.random.nextInt(maxOffset);
                bot = ((y * tileHigh) + tileHigh) - AppWindow.random.nextInt(maxOffset);

                mx = lft + (tileWid / 2);
                my = top + (tileWid / 2);

                // the color
                f = getPerlineColorFactorForPosition(mx, my);

                if (blended) {
                    if ((hasBorder) && ((x == 0) || (y == 0))) {
                        col.setFromColorFactor(mosaicColor, mosaic3Color, f);
                    } else {
                        col.setFromColorFactor(mosaicColor, mosaic2Color, f);
                    }
                } else {
                    if ((hasBorder) && ((x == 0) || (y == 0))) {
                        col = (f > 0.5f) ? mosaic3Color : mosaicColor;
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
