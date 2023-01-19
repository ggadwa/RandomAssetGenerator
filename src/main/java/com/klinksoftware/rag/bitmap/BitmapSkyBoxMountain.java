package com.klinksoftware.rag.bitmap;

import com.klinksoftware.rag.bitmap.utility.BitmapInterface;
import com.klinksoftware.rag.bitmap.utility.BitmapBase;
import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.utility.*;

@BitmapInterface
public class BitmapSkyBoxMountain extends BitmapBase {

    public BitmapSkyBoxMountain() {
        super();

        textureSize = DEFAULT_SKYBOX_TEXTURE_SIZE;
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

    private void generateMountainsDraw(int lft, int rgt, int drawBot, int centerY, int trimTop, int trimBot, RagColor col) {
        int x, y, lx, rx, quarterX, ty, oy;
        int wid, idx;
        int[] rangeY;
        float colFactor, colorFactorAdd;
        float fy, fyAdd, midY;

        wid = rgt - lft;

        // create the range by random segments
        midY = (float) centerY;
        fy = (float) midY;
        rangeY = new int[wid];

        quarterX = (wid / 4) * 3;

        lx = 0;
        while (true) {
            // we want to creep close to equal when we are 75% of the way there
            fyAdd = (0.1f + AppWindow.random.nextFloat(1.2f));
            if (lx < quarterX) {
                fyAdd *= (AppWindow.random.nextBoolean() ? -1.0f : 1.0f);
            } else {
                fyAdd *= ((fy < midY) ? 1.0f : -1.0f);
            }

            rx = lx + ((textureSize / 500) + AppWindow.random.nextInt(textureSize / 20));
            if (rx > wid) {
                rx = wid;
                fyAdd = (midY - fy) / (float) (rx - lx);
            }

            for (x = lx; x < rx; x++) {
                fy += fyAdd;
                if (fy < (float) trimTop) {
                    fy = (float) trimTop;
                }
                if (fy > (float) trimBot) {
                    fy = (float) trimBot;
                }

                rangeY[x] = (int) fy;
            }

            lx = rx;
            if (lx == wid) {
                break;
            }
        }

        // perlin noise for mountain
        createPerlinNoiseData(32, 32);
        colorFactorAdd = 0.3f + AppWindow.random.nextFloat(0.2f);

        // run through the ranges
        for (x = lft; x != rgt; x++) {
            ty = rangeY[x - lft];
            oy = ty + AppWindow.random.nextInt((drawBot - centerY) / 5);

            for (y = ty; y <= drawBot; y++) {
                colFactor = 0.5f + (colorFactorAdd * perlinNoiseColorFactor[(y * textureSize) + x]);

                if (y < oy) {
                    if (y == ty) {
                        colFactor = 0.2f;
                    } else {
                        colFactor *= (0.7f + (0.3f * ((float) (y - ty) / (float) (oy - ty))));
                    }
                }

                idx = ((y * wid) + x) * 4;
                colorData[idx] = (col.r * colFactor);
                colorData[idx + 1] = (col.g * colFactor);
                colorData[idx + 2] = (col.b * colFactor);
            }
        }
    }

    private void generateGrassDraw(int lft, int rgt, int drawBot, int centerY, int trimTop, int trimBot, RagColor col) {
        int x, y, lx, rx, quarterX, ty;
        int wid, idx;
        int[] rangeY;
        float colFactor, colorFactorAdd;
        float fy, fyAdd, midY;

        wid = rgt - lft;

        // create the range by random segments
        midY = (float) centerY;
        fy = (float) midY;
        rangeY = new int[wid];

        quarterX = (wid / 4) * 3;

        lx = 0;
        while (true) {
            // we want to creep close to equal when we are 75% of the way there
            fyAdd = (0.05f + AppWindow.random.nextFloat(0.1f));
            if (lx < quarterX) {
                fyAdd *= (AppWindow.random.nextBoolean() ? -1.0f : 1.0f);
            } else {
                fyAdd *= ((fy < midY) ? 1.0f : -1.0f);
            }

            rx = lx + ((textureSize / 500) + AppWindow.random.nextInt(textureSize / 20));
            if (rx > wid) {
                rx = wid;
                fyAdd = (midY - fy) / (float) (rx - lx);
            }

            for (x = lx; x < rx; x++) {
                fy += fyAdd;
                if (fy < (float) trimTop) {
                    fy = (float) trimTop;
                }
                if (fy > (float) trimBot) {
                    fy = (float) trimBot;
                }

                rangeY[x] = (int) fy;
            }

            lx = rx;
            if (lx == wid) {
                break;
            }
        }

        // perlin noise for grass
        createPerlinNoiseData(16, 16);
        colorFactorAdd = 0.3f + AppWindow.random.nextFloat(0.2f);

        // run through the ranges
        for (x = lft; x != rgt; x++) {
            ty = rangeY[x - lft];

            for (y = ty; y < drawBot; y++) {
                colFactor = (y == ty) ? 0.2f : (0.5f + (colorFactorAdd * perlinNoiseColorFactor[(y * textureSize) + x]));

                idx = ((y * wid) + x) * 4;
                colorData[idx] = (col.r * colFactor);
                colorData[idx + 1] = (col.g * colFactor);
                colorData[idx + 2] = (col.b * colFactor);
            }
        }
    }

    @Override
    public void generateInternal() {
        int n, y, lft, top, qtr, cloudCount, cloudXSize, cloudYSize;
        int edgeSize, sunSize;
        RagColor cloudColor, skyColor, sunColor, mountainColor, grassColor;

        qtr = textureSize / 4;

        cloudColor = adjustColor(new RagColor(1.0f, 1.0f, 1.0f), (0.7f + AppWindow.random.nextFloat(0.3f)));
        skyColor = adjustColor(new RagColor(0.1f, 0.95f, 1.0f), (0.7f + AppWindow.random.nextFloat(0.3f)));
        sunColor = adjustColor(new RagColor(1.0f, 0.75f, 0.0f), (0.7f + AppWindow.random.nextFloat(0.3f)));

        createPerlinNoiseData(32, 32);
        createNormalNoiseData((2.0f + AppWindow.random.nextFloat(0.3f)), (0.3f + AppWindow.random.nextFloat(0.2f)));

        // top
        drawRect(0, 0, textureSize, qtr, skyColor);

        cloudCount = 1 + AppWindow.random.nextInt(5);

        for (n = 0; n != cloudCount; n++) {
            cloudXSize = (qtr / 5) + AppWindow.random.nextInt(qtr / 3);
            lft = qtr + AppWindow.random.nextInt(qtr - cloudXSize);

            cloudYSize = (qtr / 5) + AppWindow.random.nextInt(qtr / 3);
            top = AppWindow.random.nextInt(qtr - cloudYSize);

            generateClouds(lft, top, (lft + cloudXSize), (top + cloudYSize), cloudColor);
        }

        blur(colorData, qtr, 0, (qtr + qtr), qtr, (textureSize / 100), true);

        // sides
        drawVerticalGradient(0, qtr, (qtr * 4), (qtr * 3), skyColor, adjustColor(skyColor, (0.6f + AppWindow.random.nextFloat(0.3f))));

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

        // mountain on top sides
        y = qtr + qtr;
        mountainColor = adjustColor(new RagColor(0.65f, 0.35f, 0.0f), (0.3f + AppWindow.random.nextFloat(0.3f)));
        generateMountainsDraw(0, textureSize, (qtr * 3), y, (y - qtr), (y + qtr), mountainColor);

        y = qtr + qtr + (qtr / 3);
        mountainColor = adjustColor(new RagColor(0.65f, 0.35f, 0.0f), (0.6f + AppWindow.random.nextFloat(0.3f)));
        generateMountainsDraw(0, textureSize, (qtr * 3), y, (y - (qtr / 2)), (y + (qtr / 2)), mountainColor);

        // grass on sides and also draw through to the bottom
        y = (qtr * 3) - (qtr / 4);
        grassColor = adjustColor(new RagColor(0.2f, 1.0f, 0.2f), (0.4f + AppWindow.random.nextFloat(0.3f)));
        generateGrassDraw(0, textureSize, textureSize, y, (y - (qtr / 3)), (y + (qtr / 3)), grassColor);

        // never lit
        clearImageData(normalData, 0.5f, 0.5f, 1.0f, 1.0f);
        clearImageData(metallicRoughnessData, 0.0f, 0.0f, 0.0f, 1.0f);
    }
}
