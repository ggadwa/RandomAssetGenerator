package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

@BitmapInterface
public class BitmapScale extends BitmapBase {

    public BitmapScale(int textureSize) {
        super(textureSize);

        hasNormal = true;
        hasMetallicRoughness = true;
        hasEmissive = false;
        hasAlpha = false;
    }

    @Override
    public void generateInternal() {
        int x, y, dx, dy, sx, sy, sx2, sy2;
        int xCount, scaleCount, sWid, sHigh;
        RagColor scaleColor, borderColor, col;

        scaleCount = 5 + AppWindow.random.nextInt(20);

        sWid = textureSize / scaleCount;
        sHigh = textureSize / scaleCount;

        scaleCount = textureSize / sHigh;  // readjust scales so they always fit

        scaleColor = getRandomColor();
        borderColor = adjustColor(scaleColor, 0.7f);

        // background
        createPerlinNoiseData(32, 32);
        createNormalNoiseData(1.5f, 0.5f);

        drawRect(0, 0, textureSize, textureSize, scaleColor);
        drawPerlinNoiseRect(0, 0, textureSize, textureSize, 0.8f, 1.3f);
        drawNormalNoiseRect(0, 0, textureSize, textureSize);
        blur(colorData, 0, 0, textureSize, textureSize, 5, false);

        // scales (need extra row for overlap)
        dy = textureSize - (sHigh / 2);

        for (y = 0; y <= ((scaleCount + 1) * 2); y++) {

            if ((y % 2) == 0) {
                dx = 0;
                xCount = scaleCount;
            } else {
                dx = -(sWid / 2);
                xCount = scaleCount + 1;
            }

            for (x = 0; x != xCount; x++) {

                // can have darkened scale if not on
                // wrapping rows
                col = scaleColor;

                if ((y != 0) && (y != ((scaleCount + 1) * 2)) && (x != 0) && (x != (xCount - 1))) {
                    if (AppWindow.random.nextFloat() < 0.3f) {
                        col = adjustColor(scaleColor, (0.7f + (AppWindow.random.nextFloat() * 0.3f)));
                    }
                }

                // some slight offsets
                sx = dx + (AppWindow.random.nextInt(10) - 5);
                sy = dy + (AppWindow.random.nextInt(10) - 3);
                sx2 = dx + sWid;
                sy2 = dy + (sHigh * 2);

                // the scale itself
                // we draw the scale as a solid, flat oval and
                // then redraw the border with normals
                drawOval(sx, sy, sx2, sy2, 0.25f, 0.75f, 0.0f, 0.0f, 3, 0.8f, col, borderColor, 0.5f, false, false, 0.0f, 1.0f);

                dx += sWid;
            }

            dy -= (sHigh / 2);    // overlap by half
        }

        // any spots and stains
        if (AppWindow.random.nextBoolean()) {
            generateSpotsOverlay();
        }
        generateStainsOverlay();

        createMetallicRoughnessMap(0.5f, 0.5f);
    }

}
