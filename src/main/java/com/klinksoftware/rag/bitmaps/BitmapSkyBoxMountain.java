package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.utility.*;

@BitmapInterface
public class BitmapSkyBoxMountain extends BitmapBase {

    public BitmapSkyBoxMountain() {
        super();

        hasNormal = false;
        hasMetallicRoughness = false;
        hasEmissive = false;
        hasAlpha = false;
    }

    private void generateClouds(int lft, int top, int rgt, int bot, RagColor cloudColor) {
        int n, x, y, xsz, ysz;
        int wid, high, quarterWid, quarterHigh, edgeSize;
        float edgeColorFactor;

        wid = rgt - lft;
        high = bot - top;
        quarterWid = wid / 4;
        quarterHigh = high / 4;

        createPerlinNoiseData(32, 32);
        createNormalNoiseData((2.0f + AppWindow.random.nextFloat(0.3f)), (0.3f + AppWindow.random.nextFloat(0.2f)));

        edgeSize = 1 + AppWindow.random.nextInt(4);
        edgeColorFactor = 0.6f + AppWindow.random.nextFloat(0.3f);

        // cloud parts
        for (n = 0; n != 20; n++) {
            xsz = (quarterWid + AppWindow.random.nextInt(quarterWid)) - (edgeSize * 2);
            ysz = (quarterHigh + AppWindow.random.nextInt(quarterHigh)) - (edgeSize * 2);

            x = (lft + edgeSize) + AppWindow.random.nextInt(wid - xsz);
            y = (top + edgeSize) + AppWindow.random.nextInt(high - ysz);

            drawOval(x, y, (x + xsz), (y + ysz), 0.0f, 1.0f, (0.1f + AppWindow.random.nextFloat(0.2f)), (0.1f + AppWindow.random.nextFloat(0.2f)), edgeSize, edgeColorFactor, cloudColor, cloudColor, 0.5f, false, true, 0.9f, 1.0f);
        }
    }

    private void generateMountainsDraw(int lft, int top, int rgt, int bot, int rangeVerticalMove, RagColor col, float colorAdjust) {
        int x, y, halfWid, midY, midDir, midCount;
        int wid, idx;
        int[] rangeY;
        float colFactor, colorFactorAdd;

        wid = rgt - lft;

        // we only do half the range, and reverse for the other half so
        // they match up
        halfWid = wid / 2;

        // remember the mid point and we either favor going
        // down or up if we pass it
        midY = (top + bot) / 2;
        midDir = 0;
        midCount = 0;

        // create the range
        y = midY;
        rangeY = new int[wid];

        for (x = 0; x != halfWid; x++) {
            rangeY[x] = y;
            rangeY[(wid - 1) - x] = y;

            if (midCount <= 0) {
                midCount = AppWindow.random.nextInt(50);
                midDir = (y > midY) ? -1 : 1;
            }

            y += (AppWindow.random.nextInt(rangeVerticalMove) * midDir);

            if (y < top) {
                y = top;
                midDir = 1;
            }
            if (y > bot) {
                y = bot;
                midDir = -1;
            }

            midCount--;
        }

        // perlin noise for mountain
        createPerlinNoiseData(32, 32);
        colorFactorAdd = 0.3f + AppWindow.random.nextFloat(0.2f);

        // run through the ranges
        for (x = lft; x != rgt; x++) {
            for (y = rangeY[x - lft]; y <= bot; y++) {
                colFactor = 0.5f + (colorFactorAdd * perlinNoiseColorFactor[(y * textureSize) + x]);

                idx = ((y * wid) + x) * 4;

                colorData[idx] = (col.r * colFactor) * colorAdjust;
                colorData[idx + 1] = (col.g * colFactor) * colorAdjust;
                colorData[idx + 2] = (col.b * colFactor) * colorAdjust;
            }
        }
    }

    @Override
    public void generateInternal() {
        int n, qtr, cloudCount, mountainCount;
        float colorAdjustSize, colorAdjustAdd, colorAdjust;
        RagColor cloudColor, skyColor, mountainColor;

        qtr = textureSize / 4;

        cloudColor = adjustColor(new RagColor(1.0f, 1.0f, 1.0f), (0.7f + AppWindow.random.nextFloat(0.3f)));
        skyColor = adjustColor(new RagColor(0.1f, 0.95f, 1.0f), (0.7f + AppWindow.random.nextFloat(0.3f)));
        mountainColor = new RagColor(0.65f, 0.35f, 0.0f);

        // top
        drawRect(qtr, 0, (qtr * 2), qtr, skyColor);
        generateClouds(qtr, 0, (qtr * 2), qtr, cloudColor);
        blur(colorData, 0, 0, textureSize, textureSize, 5, true);

        // bottom
        drawRect(qtr, (qtr * 2), (qtr * 2), (qtr * 3), adjustColor(mountainColor, 0.5f));

        // sides
        drawVerticalGradient(0, qtr, (qtr * 4), (qtr * 2), skyColor, adjustColor(skyColor, (0.6f + AppWindow.random.nextFloat(0.3f))));

        // clouds on sides
        cloudCount = 1 + AppWindow.random.nextInt(5);

        for (n = 0; n != cloudCount; n++) {
            generateClouds(0, (qtr + (qtr / 5)), (qtr * 4), ((qtr * 2) - (qtr / 4)), cloudColor);
        }

        blur(colorData, 0, 0, textureSize, textureSize, 5, true);

        // mountain on sides
        mountainCount = 1 + AppWindow.random.nextInt(3);
        colorAdjustSize = 0.2f + AppWindow.random.nextFloat(0.3f);
        colorAdjustAdd = colorAdjustSize / (float) mountainCount;
        colorAdjust = 1.0f - colorAdjustSize;

        for (n = 0; n != mountainCount; n++) {
            generateMountainsDraw(0, (qtr + (qtr / 4)), (qtr * 4), (qtr * 2), (2 + AppWindow.random.nextInt(6)), mountainColor, colorAdjust);
            colorAdjust += colorAdjustAdd;
        }

        // never lit
        clearImageData(normalData, 0.5f, 0.5f, 1.0f, 1.0f);
        clearImageData(metallicRoughnessData, 0.0f, 0.0f, 0.0f, 1.0f);
    }
}
