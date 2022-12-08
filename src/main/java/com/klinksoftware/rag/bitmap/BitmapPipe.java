package com.klinksoftware.rag.bitmap;

import com.klinksoftware.rag.bitmap.utility.BitmapInterface;
import com.klinksoftware.rag.bitmap.utility.BitmapBase;
import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

@BitmapInterface
public class BitmapPipe extends BitmapBase {

    public BitmapPipe() {
        super();

        hasNormal = true;
        hasMetallicRoughness = true;
        hasEmissive = false;
        hasAlpha = false;
    }

    private void generateMetalPipe(RagColor metalColor, RagColor altMetalColor, int edgeSize, int screwSize) {
        int n, x, y, yAdd, yOff, screwCount;
        RagColor lineColor, outlineColor;

        createPerlinNoiseData(16, 16);
        drawRect(0, 0, textureSize, textureSize, metalColor);
        drawPerlinNoiseRect(0, 0, textureSize, textureSize, 0.8f, 1.0f);
        drawMetalShine(0, 0, textureSize, textureSize, metalColor);

        // run seam
        if (AppWindow.random.nextBoolean()) {
            lineColor = adjustColor(metalColor, 0.8f);

            drawLineColor(1, 0, 1, textureSize, lineColor);
            drawLineNormal(1, 0, 1, textureSize, NORMAL_CLEAR);
            drawLineNormal(2, 0, 2, textureSize, NORMAL_RIGHT_45);
            drawLineNormal(0, 0, 0, textureSize, NORMAL_LEFT_45);
        }

        // middle seam
        yOff = 0;

        if (AppWindow.random.nextBoolean()) {
            lineColor = adjustColor(metalColor, 0.8f);

            drawLineColor(0, 2, textureSize, 2, lineColor);
            drawLineColor(0, 3, textureSize, 3, lineColor);
            drawLineNormal(0, 2, textureSize, 2, NORMAL_CLEAR);
            drawLineNormal(0, 3, textureSize, 3, NORMAL_CLEAR);
            drawLineNormal(0, 4, textureSize, 4, NORMAL_BOTTOM_45);
            drawLineNormal(0, 5, textureSize, 5, NORMAL_BOTTOM_10);
            drawLineNormal(0, 1, textureSize, 1, NORMAL_TOP_45);
            drawLineNormal(0, 0, textureSize, 0, NORMAL_TOP_10);

            y = (int) ((float) textureSize * 0.1f);

            drawLineColor(0, (y + 2), textureSize, (y + 2), lineColor);
            drawLineColor(0, (y + 3), textureSize, (y + 3), lineColor);
            drawLineNormal(0, (y + 2), textureSize, (y + 2), NORMAL_CLEAR);
            drawLineNormal(0, (y + 3), textureSize, (y + 3), NORMAL_CLEAR);
            drawLineNormal(0, (y + 4), textureSize, (y + 4), NORMAL_BOTTOM_45);
            drawLineNormal(0, (y + 5), textureSize, (y + 5), NORMAL_BOTTOM_10);
            drawLineNormal(0, (y + 1), textureSize, (y + 1), NORMAL_TOP_45);
            drawLineNormal(0, (y + 0), textureSize, (y + 0), NORMAL_TOP_10);

            yOff = y;
        }

        // screws
        if (AppWindow.random.nextBoolean()) {
            screwCount = 5 + AppWindow.random.nextInt(5);

            yAdd = (textureSize - yOff) / screwCount;
            x = screwSize;
            y = screwSize + yOff + ((yAdd / 2) - screwSize);

            outlineColor = adjustColor(altMetalColor, 0.8f);

            for (n = 0; n != screwCount; n++) {
                drawScrew(x, y, altMetalColor, outlineColor, screwSize, (screwSize / 2));
                y += yAdd;
            }
        }
    }

    @Override
    public void generateInternal() {
        int edgeSize, screwSize;
        RagColor metalColor, altMetalColor;

        metalColor = getRandomColor();
        altMetalColor = getRandomColor();
        edgeSize = (int) (((float) textureSize * 0.005) + (AppWindow.random.nextFloat() * ((float) textureSize * 0.005)));
        screwSize = (int) (((float) textureSize * 0.03) + (AppWindow.random.nextFloat() * ((float) textureSize * 0.05)));

        generateMetalPipe(metalColor, altMetalColor, edgeSize, screwSize);

        // finish with the metallic-roughness
        createMetallicRoughnessMap(0.5f, 0.6f);
    }
}
