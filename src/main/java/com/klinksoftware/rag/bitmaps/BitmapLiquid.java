package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

@BitmapInterface
public class BitmapLiquid extends BitmapBase
{
    public BitmapLiquid()    {
        super();

        hasNormal=true;
        hasMetallicRoughness=true;
        hasEmissive=false;
        hasAlpha=false;
    }

    @Override
    public void generateInternal() {
        int sz;
        int[] noiseData = {8, 16, 32, 64};
        RagColor liquidColor;

        drawRect(0, 0, textureSize, textureSize, this.COLOR_WHITE);

        sz = noiseData[AppWindow.random.nextInt(4)];
        createPerlinNoiseData(sz, sz);
        liquidColor = getRandomGray(0.0f, 0.5f); // getRandomColor();
        drawPerlinNoiseColorRect(0, 0, textureSize, textureSize, liquidColor, (0.4f + AppWindow.random.nextFloat(0.3f)));

        sz = noiseData[AppWindow.random.nextInt(4)];
        createPerlinNoiseData(sz, sz);
        liquidColor = getRandomGray(0.0f, 0.5f); // getRandomColor();
        drawPerlinNoiseColorRect(0, 0, textureSize, textureSize, liquidColor, (0.4f + AppWindow.random.nextFloat(0.3f)));

        tint(0, 0, textureSize, textureSize, getRandomColor(), 0.5f);

        createMetallicRoughnessMap((0.2f + AppWindow.random.nextFloat(0.5f)), 0.6f);
    }

}
