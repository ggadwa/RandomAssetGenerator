package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

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
    public void generateInternal()    {
        RagColor liquidColor;

        liquidColor = getRandomColor();
        drawRect(0, 0, textureSize, textureSize, liquidColor);

        createPerlinNoiseData(16,16);
        liquidColor = getRandomColor();
        drawPerlinNoiseColorRect(0, 0, textureSize, textureSize, liquidColor, 0.5f);

        createPerlinNoiseData(32,32);
        liquidColor = getRandomColor();
        drawPerlinNoiseColorRect(0, 0, textureSize, textureSize, liquidColor, 0.7f);

        createMetallicRoughnessMap((0.2f + AppWindow.random.nextFloat(0.5f)), 0.6f);
    }

}
