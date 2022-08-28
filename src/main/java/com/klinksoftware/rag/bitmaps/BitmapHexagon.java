package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

@BitmapInterface
public class BitmapHexagon extends BitmapBase {

    public BitmapHexagon(int textureSize) {
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
        RagColor color, hexColor, altHexColor;

        hexColor = getRandomColor();
        altHexColor = getRandomColor();
        edgeSize = (textureSize / 120) + AppWindow.random.nextInt(textureSize / 100);

        hasBorder = AppWindow.random.nextFloat() > 0.7f;

        xCount = (textureSize / 250) + (2 * AppWindow.random.nextInt((textureSize / 250)));
        yCount = (textureSize / 250) + (2 * AppWindow.random.nextInt((textureSize / 150)));

        xSize = textureSize / xCount;
        ySize = textureSize / yCount;

        pointSize = (int) ((float) xSize * 0.1f);

        lft = 0;

        for (x = 0; x <= xCount; x++) {
            top = ((x & 0x1) == 0) ? 0 : -(ySize / 2);

            for (y = 0; y <= yCount; y++) {

                if (((y == 0) || (y == yCount)) && (hasBorder)) {
                    color = altHexColor;
                } else {
                    if ((x != 0) && (x != xCount) && (y != 0) && (y != yCount)) {
                        color = adjustColorRandom(hexColor, 0.9f, 1.1f);
                    } else {
                        color = hexColor;
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
