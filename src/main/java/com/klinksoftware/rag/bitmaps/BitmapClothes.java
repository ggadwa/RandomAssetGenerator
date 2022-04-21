package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

public class BitmapClothes extends BitmapBase {

    public BitmapClothes() {
        super();

        hasNormal = true;
        hasMetallicRoughness = true;
        hasEmissive = false;
        hasAlpha = false;
    }

    private void generateStainsOverlay() {
        int n, k, lft, top, rgt, bot,
                stainCount, stainSize,
                xSize, ySize, markCount;

        stainCount = AppWindow.random.nextInt(5);
        stainSize = (int) ((float) textureSize * 0.1f);

        for (n = 0; n != stainCount; n++) {
            lft = AppWindow.random.nextInt(textureSize);
            xSize = stainSize + AppWindow.random.nextInt(stainSize);

            top = AppWindow.random.nextInt(textureSize);
            ySize = stainSize + AppWindow.random.nextInt(stainSize);

            markCount = 2 + AppWindow.random.nextInt(4);

            for (k = 0; k != markCount; k++) {
                rgt = lft + xSize;
                if (rgt >= textureSize) {
                    rgt = textureSize - 1;
                }
                bot = top + ySize;
                if (bot >= textureSize) {
                    bot = textureSize - 1;
                }

                drawOvalStain(lft, top, rgt, bot, 0.01f, 0.15f, 0.85f);

                lft += (AppWindow.random.nextBoolean()) ? (-(xSize / 3)) : (xSize / 3);
                top += (AppWindow.random.nextBoolean()) ? (-(ySize / 3)) : (ySize / 3);
                xSize = (int) ((float) xSize * 0.8f);
                ySize = (int) ((float) ySize * 0.8f);
            }
        }
    }

    @Override
    public void generateInternal() {
        int n, x, y, x2, y2,
                lineCount;
        RagColor clothColor, lineColor;

        clothColor = getRandomColor();

        createPerlinNoiseData(32, 32);
        createNormalNoiseData(1.5f, 0.5f);

        drawRect(0, 0, textureSize, textureSize, clothColor);
        drawPerlinNoiseRect(0, 0, textureSize, textureSize, 0.8f, 1.3f);
        drawNormalNoiseRect(0, 0, textureSize, textureSize);

        // lines
        lineCount = 30 + AppWindow.random.nextInt(30);

        for (n = 0; n != lineCount; n++) {
            x = AppWindow.random.nextInt(textureSize);
            y = AppWindow.random.nextInt(textureSize);
            y2 = AppWindow.random.nextInt(textureSize);

            lineColor = this.adjustColorRandom(clothColor, 0.6f, 0.25f);
            drawRandomLine(x, y, x, y2, 0, 0, textureSize, textureSize, 30, lineColor, false);
        }

        lineCount = 30 + AppWindow.random.nextInt(30);

        for (n = 0; n != lineCount; n++) {
            x = AppWindow.random.nextInt(textureSize);
            x2 = AppWindow.random.nextInt(textureSize);
            y = AppWindow.random.nextInt(textureSize);

            lineColor = this.adjustColorRandom(clothColor, 0.6f, 0.25f);
            drawRandomLine(x, y, x2, y, 0, 0, textureSize, textureSize, 30, lineColor, false);
        }

        // any stains
        if (AppWindow.random.nextBoolean()) {
            generateStainsOverlay();
        }

        blur(colorData, 0, 0, textureSize, textureSize, 25, false);

        createMetallicRoughnessMap(0.4f, 0.3f);
    }
}
