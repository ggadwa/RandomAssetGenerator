package com.klinksoftware.rag.bitmap;

import com.klinksoftware.rag.bitmap.utility.BitmapInterface;
import com.klinksoftware.rag.bitmap.utility.BitmapBase;
import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;
import java.util.ArrayList;
import org.apache.commons.math3.util.Pair;

@BitmapInterface
public class BitmapRockCracked extends BitmapBase {

    public BitmapRockCracked() {
        super();

        hasNormal = true;
        hasMetallicRoughness = true;
        hasEmissive = false;
        hasAlpha = false;
    }

    private ArrayList<Pair<Integer, Integer>> drawCaveWallVerticalCut(int sx, int sy, int ex, int ey, int segCount, int cutVarient) {
        int n, k, x, y, dx, dy, dx2, dy2;
        int wid, sWid, dWid, dWid2;
        int idx;
        float nFactor;
        RagPoint normal;
        ArrayList<Pair<Integer, Integer>> pnts;

        normal = new RagPoint(0.0f, 0.0f, 0.0f);

        sWid = (textureSize / 170) + AppWindow.random.nextInt(textureSize / 50);

        dx = sx;
        dy = sy;
        dWid = sWid;

        pnts = new ArrayList<>();

        for (n = 0; n != segCount; n++) {

            if ((n + 1) == segCount) {
                dx2 = ex;
                dy2 = ey;
                dWid2 = sWid;
            } else {
                dWid2 = (textureSize / 100) + AppWindow.random.nextInt(textureSize / 60);
                dx2 = sx + (AppWindow.random.nextInt(cutVarient) * (AppWindow.random.nextBoolean() ? 1 : -1));
                if (dx2 < dWid2) {
                    dx2 = dWid2;
                }
                if (dx2 >= (textureSize - dWid2)) {
                    dx2 = (textureSize - dWid2) - 1;
                }
                dy2 = sy + (int) ((float) ((ey - sy) * (n + 1)) / (float) segCount);

                pnts.add(new Pair(dx2, dy2));
            }

            for (y = dy; y < dy2; y++) {

                x = dx + (((dx2 - dx) * (y - dy)) / (dy2 - dy));
                wid = dWid + (((dWid2 - dWid) * (y - dy)) / (dy2 - dy));

                idx = ((y * textureSize) + x) * 4;

                for (k = 0; k != wid; k++) {

                    nFactor = 0.7f + (((float) k / (float) wid) * 0.3f);
                    colorData[idx] = colorData[idx] * nFactor;
                    colorData[idx + 1] = colorData[idx + 1] * nFactor;
                    colorData[idx + 2] = colorData[idx + 2] * nFactor;

                    normal.x = -(0.45f + (normalData[idx] * 0.5f));
                    normal.y = normalData[idx + 1];
                    normal.z = normalData[idx + 2];

                    normal.normalize();
                    normalData[idx] = normal.x;
                    normalData[idx + 1] = normal.y;
                    normalData[idx + 2] = normal.z;

                    idx += 4;
                }

                idx = ((y * textureSize) + (x - wid)) * 4;

                for (k = 0; k != wid; k++) {

                    nFactor = (0.7f + (((float) (wid - k) / (float) wid) * 0.3f));
                    colorData[idx] = colorData[idx] * nFactor;
                    colorData[idx + 1] = colorData[idx + 1] * nFactor;
                    colorData[idx + 2] = colorData[idx + 2] * nFactor;

                    normal.x = 0.45f + (normalData[idx] * 0.5f);
                    normal.y = normalData[idx + 1];
                    normal.z = normalData[idx + 2];

                    normal.normalize();
                    normalData[idx] = normal.x;
                    normalData[idx + 1] = normal.y;
                    normalData[idx + 2] = normal.z;

                    idx += 4;
                }
            }

            dx = dx2;
            dy = dy2;
            dWid = dWid2;
        }

        return (pnts);
    }

    private void drawCaveWallHorizontalCut(int sx, int sy, int ex, int ey, int segCount, int cutVarient) {
        int n, k, x, y, dx, dy, dx2, dy2;
        int wid, sWid, dWid, dWid2;
        int idx;
        float nFactor;
        RagPoint normal;

        normal = new RagPoint(0.0f, 0.0f, 0.0f);

        sWid = (textureSize / 50) + AppWindow.random.nextInt(textureSize / 50);

        dx = sx;
        dy = sy;
        dWid = sWid;

        for (n = 0; n != segCount; n++) {

            if ((n + 1) == segCount) {
                dx2 = ex;
                dy2 = ey;
                dWid2 = sWid;

            } else {
                dWid2 = (textureSize / 100) + AppWindow.random.nextInt(textureSize / 60);
                dx2 = sx + (int) ((float) ((ex - sx) * (n + 1)) / (float) segCount);
                dy2 = sy + (AppWindow.random.nextInt(cutVarient) * (AppWindow.random.nextBoolean() ? 1 : -1));
                if (dy2 < dWid2) {
                    dy2 = dWid2;
                }
                if (dy2 >= (textureSize - dWid2)) {
                    dy2 = (textureSize - dWid2) - 1;
                }
            }

            for (x = dx; x < dx2; x++) {

                y = dy + (((dy2 - dy) * (x - dx)) / (dx2 - dx));
                wid = dWid + (((dWid2 - dWid) * (x - dx)) / (dx2 - dx));

                idx = ((y * textureSize) + x) * 4;

                for (k = 0; k != wid; k++) {

                    nFactor = 0.7f + (((float) k / (float) wid) * 0.3f);
                    colorData[idx] = colorData[idx] * nFactor;
                    colorData[idx + 1] = colorData[idx + 1] * nFactor;
                    colorData[idx + 2] = colorData[idx + 2] * nFactor;

                    normal.x = normalData[idx];
                    normal.y = 0.45f + (normalData[idx] * 0.5f);
                    normal.z = normalData[idx + 2];

                    normal.normalize();
                    normalData[idx] = normal.x;
                    normalData[idx + 1] = normal.y;
                    normalData[idx + 2] = normal.z;

                    idx += (textureSize * 4);
                }

                idx = (((y - wid) * textureSize) + x) * 4;

                for (k = 0; k != wid; k++) {

                    nFactor = 0.7f + (((float) k / (float) wid) * 0.3f);
                    colorData[idx] = colorData[idx] * nFactor;
                    colorData[idx + 1] = colorData[idx + 1] * nFactor;
                    colorData[idx + 2] = colorData[idx + 2] * nFactor;

                    normal.x = normalData[idx];
                    normal.y = -(0.45f + (normalData[idx] * 0.5f));
                    normal.z = normalData[idx + 2];

                    normal.normalize();
                    normalData[idx] = normal.x;
                    normalData[idx + 1] = normal.y;
                    normalData[idx + 2] = normal.z;

                    idx += (textureSize * 4);
                }
            }

            dx = dx2;
            dy = dy2;
            dWid = dWid2;
        }
    }

    private void drawSingleStripeBackground(int lx, int rx, RagColor caveColor) {
        drawRect(lx, 0, rx, textureSize, adjustColorRandom(caveColor, 0.9f, 1.0f));

        if (AppWindow.random.nextBoolean()) {
            createPerlinNoiseData(8, 8);
        } else {
            createPerlinNoiseData(16, 16);
        }
        drawPerlinNoiseRect(lx, 0, rx, textureSize, (0.75f + AppWindow.random.nextFloat(0.1f)), 1.0f);

        createNormalNoiseData((1.0f + AppWindow.random.nextFloat(0.2f)), (0.1f + AppWindow.random.nextFloat(0.2f)));
        drawNormalNoiseRect(lx, 0, rx, textureSize);
    }

    @Override
    public void generateInternal() {
        int n, x, lx, cutVarient, pIdx, pIdx2;
        ArrayList<Integer> xs;
        ArrayList<Pair<Integer, Integer>> pnts1, pnts2;
        RagColor caveColor;

        caveColor = getRandomColor();

        cutVarient = (textureSize / 100) + AppWindow.random.nextInt(textureSize / 25);

        // get the rock stripes
        x = cutVarient;
        xs = new ArrayList<>();
        xs.add(0);

        while (true) {
            x += (cutVarient + AppWindow.random.nextInt(textureSize / 10));
            if (x < (textureSize - cutVarient)) {
                xs.add(x);
            } else {
                xs.add(textureSize);
                break;
            }
        }

        // start with one background so left and right stripe can wrap
        drawSingleStripeBackground(0, textureSize, caveColor);

        // different background for each stripe
        for (n = 1; n < (xs.size() - 2); n++) {
            drawSingleStripeBackground(xs.get(n), xs.get(n + 1), caveColor);
        }

        // the stripe edges
        pnts1 = null;
        pnts2 = null;

        for (n = 1; n < (xs.size() - 1); n++) {
            lx = xs.get(n);

            // the crack
            pnts2 = pnts1;
            pnts1 = drawCaveWallVerticalCut(lx, 0, lx, textureSize, (5 + AppWindow.random.nextInt(5)), cutVarient);

            // any possible ledge
            if ((AppWindow.random.nextBoolean()) && (pnts1 != null) && (pnts2 != null)) {
                pIdx = AppWindow.random.nextInt(pnts1.size());
                pIdx2 = pIdx;
                if (pIdx2 >= pnts2.size()) {
                    pIdx2 = pnts2.size() - 1;
                }

                drawCaveWallHorizontalCut(pnts2.get(pIdx2).getFirst(), pnts2.get(pIdx2).getSecond(), pnts1.get(pIdx).getFirst(), pnts1.get(pIdx).getSecond(), (2 + AppWindow.random.nextInt(2)), (cutVarient * 2));
            }
        }

        // finish with the metallic-roughness
        createMetallicRoughnessMap(0.35f, 0.3f);
    }
}
