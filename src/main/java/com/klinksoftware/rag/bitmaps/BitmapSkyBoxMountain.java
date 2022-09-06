package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.utility.*;

@BitmapInterface
public class BitmapSkyBoxMountain extends BitmapBase {

    public BitmapSkyBoxMountain(int textureSize) {
        super(textureSize);

        hasNormal = false;
        hasMetallicRoughness = false;
        hasEmissive = false;
        hasAlpha = false;
    }

    private void generateClouds(int lft, int top, int rgt, int bot, RagColor cloudColor) {
        int n, x, y, xsz, ysz;
        int wid, high, cloudMaxWid, cloudMaxHigh, cloudParts;
        RagColor color;

        wid = rgt - lft;
        high = bot - top;
        cloudMaxWid = wid / 4;
        cloudMaxHigh = high / 8;

        cloudParts = 10 + AppWindow.random.nextInt(20);

        // lighter cloud parts
        for (n = 0; n != cloudParts; n++) {
            xsz = cloudMaxWid + AppWindow.random.nextInt(cloudMaxWid);
            ysz = cloudMaxHigh + AppWindow.random.nextInt(cloudMaxHigh);

            x = lft + AppWindow.random.nextInt(wid - xsz);
            y = top + AppWindow.random.nextInt(high - ysz);

            drawSimpleOval(colorData, x, y, (x + xsz), (y + ysz), cloudColor);
        }

        // darker cloud parts
        cloudParts = cloudParts / 2;
        top = top + ((bot - top) / 3);
        high = bot - top;
        if (high <= 0) {
            return;
        }

        for (n = 0; n != cloudParts; n++) {
            xsz = cloudMaxWid + AppWindow.random.nextInt(cloudMaxWid);
            ysz = cloudMaxHigh + AppWindow.random.nextInt(cloudMaxHigh);

            x = lft + AppWindow.random.nextInt(wid - xsz);
            y = top + AppWindow.random.nextInt(high - ysz);

            color = this.adjustColor(cloudColor, (0.85f + AppWindow.random.nextFloat(0.15f)));
            drawSimpleOval(colorData, x, y, (x + xsz), (y + ysz), color);
        }
    }

    private void generateMountainsDraw(int lft, int top, int rgt, int bot, int rangeVerticalMove, boolean outline, RagColor col, float colorAdjust) {
        int x, y, ty, oy, halfWid, midY, midDir, midCount;
        int wid, maxOutlineSize, idx;
        int[] rangeY;
        float colFactor, colorFactorAdd;

        wid = rgt - lft;
        maxOutlineSize = textureSize / 100;

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
                midCount = AppWindow.random.nextInt(textureSize / 20);
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
            ty = rangeY[x - lft];
            oy = ty + AppWindow.random.nextInt(maxOutlineSize);

            for (y = ty; y <= bot; y++) {
                colFactor = 0.5f + (colorFactorAdd * perlinNoiseColorFactor[(y * textureSize) + x]);

                if ((outline) && (y < oy)) {
                    colFactor *= 0.8f;
                }

                idx = ((y * wid) + x) * 4;

                colorData[idx] = (col.r * colFactor) * colorAdjust;
                colorData[idx + 1] = (col.g * colFactor) * colorAdjust;
                colorData[idx + 2] = (col.b * colFactor) * colorAdjust;
            }
        }
    }

    @Override
    public void generateInternal() {
        int n, lft, top, qtr, cloudCount, cloudXSize, cloudYSize;
        int edgeSize, sunSize, mountainCount;
        float colorAdjustSize, colorAdjustAdd, colorAdjust;
        RagColor cloudColor, skyColor, sunColor, mountainColor;

        qtr = textureSize / 4;

        cloudColor = adjustColor(new RagColor(1.0f, 1.0f, 1.0f), (0.7f + AppWindow.random.nextFloat(0.3f)));
        skyColor = adjustColor(new RagColor(0.1f, 0.95f, 1.0f), (0.7f + AppWindow.random.nextFloat(0.3f)));
        sunColor = adjustColor(new RagColor(1.0f, 0.75f, 0.0f), (0.7f + AppWindow.random.nextFloat(0.3f)));
        mountainColor = new RagColor(0.65f, 0.35f, 0.0f);

        createPerlinNoiseData(32, 32);
        createNormalNoiseData((2.0f + AppWindow.random.nextFloat(0.3f)), (0.3f + AppWindow.random.nextFloat(0.2f)));

        // top
        drawRect(qtr, 0, (qtr * 2), qtr, skyColor);

        cloudCount = 1 + AppWindow.random.nextInt(5);

        for (n = 0; n != cloudCount; n++) {
            cloudXSize = (qtr / 5) + AppWindow.random.nextInt(qtr / 3);
            lft = qtr + AppWindow.random.nextInt(qtr - cloudXSize);

            cloudYSize = (qtr / 5) + AppWindow.random.nextInt(qtr / 3);
            top = AppWindow.random.nextInt(qtr - cloudYSize);

            generateClouds(lft, top, (lft + cloudXSize), (top + cloudYSize), cloudColor);
        }

        blur(colorData, qtr, 0, (qtr + qtr), qtr, (textureSize / 100), true);

        // bottom
        drawRect(qtr, (qtr * 2), (qtr * 2), (qtr * 3), adjustColor(mountainColor, 0.5f));

        // sides
        drawVerticalGradient(0, qtr, (qtr * 4), (qtr * 2), skyColor, adjustColor(skyColor, (0.6f + AppWindow.random.nextFloat(0.3f))));

        //sun
        sunSize = (qtr / 5) + AppWindow.random.nextInt(qtr / 2);
        lft = AppWindow.random.nextInt(textureSize - sunSize);
        top = qtr + AppWindow.random.nextInt(qtr - sunSize);
        edgeSize = AppWindow.random.nextInt(sunSize);

        drawOval(lft, top, (lft + sunSize), (top + sunSize), 0.0f, 1.0f, 0.0f, 0.0f, edgeSize, (1.0f + AppWindow.random.nextFloat(0.5f)), sunColor, 0.5f, false, true, 0.9f, 1.0f);

        // clouds on sides
        cloudCount = 1 + AppWindow.random.nextInt(5);

        for (n = 0; n != cloudCount; n++) {
            cloudXSize = qtr + AppWindow.random.nextInt(qtr);
            lft = AppWindow.random.nextInt(textureSize - cloudXSize);

            cloudYSize = (int) ((float) cloudXSize * (0.1f + AppWindow.random.nextFloat(0.3f)));
            if (cloudYSize > qtr) {
                cloudYSize = qtr;
            }
            top = qtr + AppWindow.random.nextInt(qtr - cloudYSize);

            generateClouds(lft, top, (lft + cloudXSize), (top + cloudYSize), cloudColor);
        }

        blur(colorData, 0, qtr, textureSize, (qtr + qtr), (textureSize / 100), true);

        // mountain on sides
        mountainCount = 2 + AppWindow.random.nextInt(3);
        colorAdjustSize = 0.4f + AppWindow.random.nextFloat(0.3f);
        colorAdjustAdd = colorAdjustSize / (float) mountainCount;
        colorAdjust = 1.0f - colorAdjustSize;

        for (n = 0; n != mountainCount; n++) {
            generateMountainsDraw(0, (qtr + (qtr / 4)), (qtr * 4), (qtr * 2), ((textureSize / 500) + AppWindow.random.nextInt(textureSize / 170)), (n == (mountainCount - 1)), mountainColor, colorAdjust);
            colorAdjust += colorAdjustAdd;
        }

        // never lit
        clearImageData(normalData, 0.5f, 0.5f, 1.0f, 1.0f);
        clearImageData(metallicRoughnessData, 0.0f, 0.0f, 0.0f, 1.0f);
    }
}
