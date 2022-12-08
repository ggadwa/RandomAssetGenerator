package com.klinksoftware.rag.bitmap;

import com.klinksoftware.rag.bitmap.utility.BitmapInterface;
import com.klinksoftware.rag.bitmap.utility.BitmapBase;
import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

@BitmapInterface
public class BitmapFur extends BitmapBase {

    public BitmapFur() {
        super();

        hasNormal = true;
        hasMetallicRoughness = true;
        hasEmissive = false;
        hasAlpha = false;
    }

    @Override
    public void generateInternal() {
        int x, y, halfHigh;
        RagColor furColor, lineColor;

        halfHigh = textureSize / 2;

        furColor = getRandomColor();

        // fur background
        drawRect(0, 0, textureSize, textureSize, furColor);

        // hair
        for (x = 0; x != textureSize; x++) {

            // hair color
            lineColor = this.adjustColorRandom(furColor, 0.7f, 1.3f);

            // hair half from top
            y = halfHigh + AppWindow.random.nextInt(halfHigh);
            drawRandomLine(x, -5, x, (y + 5), 0, 0, textureSize, textureSize, 10, lineColor, false);
            drawLineNormal(x, -5, x, (y + 5), ((x & 0x1) == 0x0) ? NORMAL_BOTTOM_RIGHT_45 : NORMAL_TOP_LEFT_45);

            // hair half from bottom
            y = textureSize - (halfHigh + AppWindow.random.nextInt(halfHigh));
            drawRandomLine(x, (y - 5), x, (textureSize + 5), 0, 0, textureSize, textureSize, 10, lineColor, false);
            drawLineNormal(x, (y - 5), x, (textureSize + 5), ((x & 0x1) == 0x0) ? NORMAL_BOTTOM_RIGHT_45 : NORMAL_TOP_LEFT_45);
        }

        // any spots
        if (AppWindow.random.nextBoolean()) {
            generateSpotsOverlay();
        }

        createMetallicRoughnessMap(0.5f, 0.5f);
    }

}
