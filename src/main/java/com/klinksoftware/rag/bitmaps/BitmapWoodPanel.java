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
        int n, stainCount, lx, ty, rx, by, sz;
        boolean vert;
        RagColor woodColor, col;

        // some random values
        woodColor = getRandomColor();

        col = adjustColorRandom(woodColor, 0.7f, 1.2f);

        createPerlinNoiseData(32, 32);

        // background
        drawRect(0, 0, textureSize, textureSize, col);

        // stripes and a noise overlay
        vert = AppWindow.random.nextBoolean();
        if (vert) {
            drawColorStripeVertical(0, 0, textureSize, textureSize, 0.1f, col);
        } else {
            drawColorStripeHorizontal(0, 0, textureSize, textureSize, 0.1f, col);
        }

        drawPerlinNoiseRect(0, 0, textureSize, textureSize, 0.8f, 1.2f);
        drawStaticNoiseRect(0, 0, textureSize, textureSize, 0.9f, 1.0f);

        // stains
        // stains are longer in the direction of the grain
        stainCount = 5 + AppWindow.random.nextInt(15);

        for (n = 0; n != stainCount; n++) {
            sz = 15 + AppWindow.random.nextInt(textureSize / 15);

            if (vert) {
                lx = AppWindow.random.nextInt(textureSize - sz);
                rx = lx + sz;

                sz *= (1.0f + AppWindow.random.nextFloat(2.0f));
                ty = AppWindow.random.nextInt(textureSize - sz);
                by = ty + sz;
            } else {
                ty = AppWindow.random.nextInt(textureSize - sz);
                by = ty + sz;

                sz *= (1.0f + AppWindow.random.nextFloat(2.0f));
                lx = AppWindow.random.nextInt(textureSize - sz);
                rx = lx + sz;
            }

            drawOvalStain(lx, ty, rx, by, (0.01f + AppWindow.random.nextFloat(0.01f)), (0.15f + AppWindow.random.nextFloat(0.05f)), (0.7f + AppWindow.random.nextFloat(0.2f)));
        }

        // blur both the color and the normal
        blur(colorData, 0, 0, textureSize, textureSize, 2, true);
        blur(normalData, 0, 0, textureSize, textureSize, 5, true);

        createMetallicRoughnessMap(0.4f,0.2f);
    }
}
