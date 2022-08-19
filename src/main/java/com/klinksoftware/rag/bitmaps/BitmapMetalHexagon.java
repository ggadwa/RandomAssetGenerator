package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

@BitmapInterface
public class BitmapMetalHexagon extends BitmapBase {

    public BitmapMetalHexagon(int textureSize) {
        super(textureSize);

        hasNormal = true;
        hasMetallicRoughness = true;
        hasEmissive = false;
        hasAlpha = false;
    }

    @Override
    public void generateInternal() {
        int x, y, lft, top, pointSize, xCount, yCount, xSize, ySize;
        int edgeSize;
        boolean hasBorder;
        RagColor color, metalColor, altMetalColor;

        metalColor = getRandomColor();
        altMetalColor = getRandomColor();
        edgeSize = 4 + AppWindow.random.nextInt(5);

        hasBorder = AppWindow.random.nextFloat() > 0.7f;

        xCount = 2 + (2 * AppWindow.random.nextInt(2));
        yCount = 2 + (2 * AppWindow.random.nextInt(3));

        xSize = textureSize / xCount;
        ySize = textureSize / yCount;

        pointSize = (int) ((float) xSize * 0.1f);

        lft = 0;

        for (x = 0; x <= xCount; x++) {
            top = ((x & 0x1) == 0) ? 0 : -(ySize / 2);

            for (y = 0; y <= yCount; y++) {

                if (((y == 0) || (y == yCount)) && (hasBorder)) {
                    color = altMetalColor;
                } else {
                    if ((x != 0) && (x != xCount) && (y != 0) && (y != yCount)) {
                        color = adjustColorRandom(metalColor, 0.9f, 1.1f);
                    } else {
                        color = metalColor;
                    }
                }

                drawHexagon(lft, top, ((lft + xSize) - pointSize), (top + ySize), pointSize, edgeSize, color);
                top += ySize;
            }

            lft += xSize;
        }

        // finish with the metallic-roughness
        createMetallicRoughnessMap(0.5f, 0.6f);
    }
}
