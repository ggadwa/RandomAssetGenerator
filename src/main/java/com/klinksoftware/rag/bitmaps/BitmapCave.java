package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

@BitmapInterface
public class BitmapCave extends BitmapBase {

    public BitmapCave() {
        super();

        hasNormal = true;
        hasMetallicRoughness = true;
        hasEmissive = false;
        hasAlpha = false;
    }

    protected void drawCaveWallVerticalCut(int sx, int sy, int ex, int ey, int segCount, int cutVarient) {
        int n, k, x, y, dx, dy, dx2, dy2;
        int wid, sWid, dWid, dWid2;
        int idx;
        float nFactor;
        RagPoint normal;

        normal = new RagPoint(0.0f, 0.0f, 0.0f);

        sWid = 5 + AppWindow.random.nextInt(8);

        dx = sx;
        dy = sy;
        dWid = sWid;

        for (n = 0; n != segCount; n++) {

            if ((n + 1) == segCount) {
                dx2 = sx;
                dy2 = ey;
                dWid2 = sWid;

            } else {
                dx2 = sx + (AppWindow.random.nextInt(cutVarient) * (AppWindow.random.nextBoolean() ? 1 : -1));
                dy2 = sy + (int) ((float) ((ey - sy) * (n + 1)) / (float) segCount);
                dWid2 = 5 + AppWindow.random.nextInt(8);
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
    }

    protected void drawCaveWallHorizontalCut(int sx, int sy, int ex, int ey, int segCount, int cutVarient) {
        int n, k, x, y, dx, dy, dx2, dy2;
        int wid, sWid, dWid, dWid2;
        int idx;
        float nFactor;
        RagPoint normal;

        normal = new RagPoint(0.0f, 0.0f, 0.0f);

        sWid = 5 + AppWindow.random.nextInt(8);

        dx = sx;
        dy = sy;
        dWid = sWid;

        for (n = 0; n != segCount; n++) {

            if ((n + 1) == segCount) {
                dx2 = ex;
                dy2 = sy;
                dWid2 = sWid;

            } else {
                dx2 = sx + (int) ((float) ((ex - sx) * (n + 1)) / (float) segCount);
                dy2 = sy + (AppWindow.random.nextInt(cutVarient) * (AppWindow.random.nextBoolean() ? 1 : -1));
                dWid2 = 5 + AppWindow.random.nextInt(8);
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

                    normal.x = -(0.45f + (normalData[idx] * 0.5f));
                    normal.y = normalData[idx + 1];
                    normal.z = normalData[idx + 2];

                    normal.normalize();
                    normalData[idx] = normal.x;
                    normalData[idx + 1] = normal.y;
                    normalData[idx + 2] = normal.z;

                    idx += (textureSize * 4);
                }

                idx = (((y - wid) * textureSize) + x) * 4;

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

                    idx += (textureSize * 4);
                }
            }

            dx = dx2;
            dy = dy2;
            dWid = dWid2;
        }
    }

    @Override
    public void generateInternal() {
        int n, x, y, crackCount, crackWid, cutVarient;
        RagColor caveColor;

        caveColor = getRandomColor();

        // the concrete background
        drawRect(0, 0, textureSize, textureSize, caveColor);

        createPerlinNoiseData(16, 16);
        drawPerlinNoiseRect(0, 0, textureSize, textureSize, 0.8f, 1.0f);

        createNormalNoiseData((1.0f + AppWindow.random.nextFloat(0.2f)), (0.1f + AppWindow.random.nextFloat(0.2f)));
        drawNormalNoiseRect(0, 0, textureSize, textureSize);

        cutVarient = 5 + AppWindow.random.nextInt(20);
        crackCount = 2 + AppWindow.random.nextInt(4);
        crackWid = textureSize / (crackCount + 1);

        for (n = 0; n != crackCount; n++) {

            // the crack
            x = (n + 1) * crackWid;
            drawCaveWallVerticalCut(x, 0, x, textureSize, (5 + AppWindow.random.nextInt(5)), cutVarient);

            // any possible ledge
            if (AppWindow.random.nextBoolean()) {
                y = 20 + AppWindow.random.nextInt(textureSize - 40);
                drawCaveWallHorizontalCut(x, y, x + crackWid, y, (1 + AppWindow.random.nextInt(3)), cutVarient);
            }
        }

        // finish with the metallic-roughness
        createMetallicRoughnessMap(0.35f, 0.3f);
    }
}
