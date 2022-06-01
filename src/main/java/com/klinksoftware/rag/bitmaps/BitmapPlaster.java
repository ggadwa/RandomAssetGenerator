package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

@BitmapInterface
public class BitmapPlaster extends BitmapBase {

    public BitmapPlaster() {
        super();

        hasNormal = true;
        hasMetallicRoughness = true;
        hasEmissive = false;
        hasAlpha = false;
    }

    @Override
    public void generateInternal() {
        int n, x, y, x2, y2, lineCount;
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
        generateStainsOverlay();

        blur(colorData, 0, 0, textureSize, textureSize, 25, false);

        createMetallicRoughnessMap(0.4f, 0.3f);
    }
}
