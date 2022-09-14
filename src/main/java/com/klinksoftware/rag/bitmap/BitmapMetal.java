package com.klinksoftware.rag.bitmap;

import com.klinksoftware.rag.bitmap.utility.BitmapInterface;
import com.klinksoftware.rag.bitmap.utility.BitmapBase;
import com.klinksoftware.rag.utility.*;

@BitmapInterface
public class BitmapMetal extends BitmapBase {

    public BitmapMetal(int textureSize) {
        super(textureSize);

        hasNormal=true;
        hasMetallicRoughness=true;
        hasEmissive=false;
        hasAlpha=false;
    }

    @Override
    public void generateInternal() {
        RagColor metalColor;

        metalColor = getRandomMetalColor();

        createPerlinNoiseData(16, 16);
        drawRect(0, 0, textureSize, textureSize, metalColor);
        drawPerlinNoiseRect(0, 0, textureSize, textureSize, 0.8f, 1.0f);

        drawMetalShine(0, 0, textureSize, textureSize, metalColor);

        createMetallicRoughnessMap(0.5f, 0.6f);
    }
}