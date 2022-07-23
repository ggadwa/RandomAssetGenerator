package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;
import org.apache.commons.math3.complex.Complex;

@BitmapInterface
public class BitmapMandelbrot extends BitmapBase {

    public BitmapMandelbrot() {
        super();

        hasNormal = true;
        hasMetallicRoughness = true;
        hasEmissive = false;
        hasAlpha = false;
    }

    private int mand(Complex zc, int max) {
        int t;
        Complex zc2;

        zc2 = zc;

        for (t = 0; t < max; t++) {
            if (zc2.abs() > 2.0) {
                return (t);
            }
            zc2 = zc2.multiply(zc2).add(zc);
        }
        return (max);
    }

    @Override
    public void generateInternal() {
        int n, x, y, colorIdx, idx;
        int max;
        float normalFactor;
        double dx, dy, mid, sz, zoom, xOff, yOff;
        Complex zc;
        RagColor[] colors;
        RagColor color, color2;

        // background
        color = getRandomColor();
        drawRect(0, 0, textureSize, textureSize, color);

        // mandlebrot
        sz = (double) textureSize;
        mid = sz * 0.5;
        max = 255;

        colors = new RagColor[8];
        for (n = 0; n != 8; n++) {
            colors[n] = getRandomColor();
        }

        zoom = (0.3 + AppWindow.random.nextDouble(1.5)) / sz;
        xOff = AppWindow.random.nextDouble(2.0f) - 1.0f;
        yOff = AppWindow.random.nextDouble(2.0f) - 1.0f;

        for (x = 0; x != textureSize; x++) {
            for (y = 0; y != textureSize; y++) {
                dx = (((double) x - mid) * zoom) + xOff;
                dy = (((double) y - mid) * zoom) + yOff;
                zc = new Complex(dx, dy);

                colorIdx = mand(zc, max);
                if (colorIdx >= max) {
                    continue;
                }

                color2 = colors[colorIdx % 8];

                idx = ((y * textureSize) + x) * 4;
                colorData[idx] = color2.r;
                colorData[idx + 1] = color2.g;
                colorData[idx + 2] = color2.b;

                normalFactor = ((float) colorIdx / 8.0f);
                normalData[idx] = 0.65f * normalFactor;
                normalData[idx + 1] = 0.02f * normalFactor;
                normalData[idx + 2] = 0.75f * normalFactor;
            }
        }

        // frame
        draw3DDarkenFrameRect(0, 0, textureSize, textureSize, (2 + AppWindow.random.nextInt(8)), (0.6f + AppWindow.random.nextFloat(0.2f)), true);

        // finish with metallic roughness
        createMetallicRoughnessMap((0.1f + AppWindow.random.nextFloat(0.6f)), 0.5f);
    }

}
