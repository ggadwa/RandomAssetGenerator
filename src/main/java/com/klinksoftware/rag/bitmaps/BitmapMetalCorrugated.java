package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

@BitmapInterface
public class BitmapMetalCorrugated extends BitmapBase {

    public BitmapMetalCorrugated() {
        super();

        hasNormal = true;
        hasMetallicRoughness = true;
        hasEmissive = false;
        hasAlpha = false;
    }

    @Override
    public void generateInternal() {
        int sz, waveCount;
        RagColor metalColor, frameColor;

        metalColor = getRandomColor();

        // background
        createPerlinNoiseData(16, 16);
        drawRect(0, 0, textureSize, textureSize, metalColor);
        drawPerlinNoiseRect(0, 0, textureSize, textureSize, 0.8f, 1.0f);

        drawMetalShine(0, 0, textureSize, textureSize, metalColor);

        // corrugations
        frameColor = adjustColorRandom(metalColor, 0.75f, 0.85f);
        sz = 10 + AppWindow.random.nextInt(10);
        waveCount = sz + AppWindow.random.nextInt(sz);

        if (AppWindow.random.nextBoolean()) {
            drawNormalWaveHorizontal(0, 0, textureSize, textureSize, metalColor, frameColor, waveCount);
        } else {
            drawNormalWaveVertical(0, 0, textureSize, textureSize, metalColor, frameColor, waveCount);
        }

        // finish with the metallic-roughness
        createMetallicRoughnessMap(0.5f, 0.6f);
    }
}
