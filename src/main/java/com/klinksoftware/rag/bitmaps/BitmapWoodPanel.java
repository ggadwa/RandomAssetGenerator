package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

@BitmapInterface
public class BitmapWoodPanel extends BitmapBase
{
    public BitmapWoodPanel()    {
        super();

        hasNormal=true;
        hasMetallicRoughness=true;
        hasEmissive=false;
        hasAlpha=false;
    }

    @Override
    public void generateInternal() {
        RagColor woodColor, col, frameColor;

        // some random values
        woodColor = getRandomColor();

        col = adjustColorRandom(woodColor, 0.7f, 1.2f);
        frameColor = adjustColorRandom(col, 0.65f, 0.75f);

        createPerlinNoiseData(32, 32);

        // background
        drawRect(0, 0, textureSize, textureSize, col);

        // stripes and a noise overlay
        if (AppWindow.random.nextBoolean()) {
            drawColorStripeVertical(0, 0, textureSize, textureSize, 0.1f, col);
        } else {
            drawColorStripeHorizontal(0, 0, textureSize, textureSize, 0.1f, col);
        }

        drawPerlinNoiseRect(0, 0, textureSize, textureSize, 0.8f, 1.2f);
        drawStaticNoiseRect(0, 0, textureSize, textureSize, 0.9f, 1.0f);

        // blur both the color and the normal
        blur(colorData, 0, 0, textureSize, textureSize, 2, true);
        blur(normalData, 0, 0, textureSize, textureSize, 5, true);

        createMetallicRoughnessMap(0.4f,0.2f);
    }
}
