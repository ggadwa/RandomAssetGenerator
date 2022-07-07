package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

@BitmapInterface
public class BitmapMetalTread extends BitmapBase {

    public final static float[][][][] TREAD_LINES = {
        {{{0.0f, 1.0f}, {1.0f, 0.0f}}, {{0.0f, 0.0f}, {1.0f, 1.0f}}, {{0.0f, 0.0f}, {1.0f, 1.0f}}, {{0.0f, 1.0f}, {1.0f, 0.0f}}}, // diamonds
        {{{0.0f, 1.0f}, {1.0f, 0.0f}}, {{0.0f, 0.0f}, {1.0f, 1.0f}}, {{0.0f, 1.0f}, {1.0f, 0.0f}}, {{0.0f, 0.0f}, {1.0f, 1.0f}}}, // waves
        {{{0.5f, 0.0f}, {0.5f, 1.0f}}, {{0.0f, 0.5f}, {1.0f, 0.5f}}, {{0.0f, 0.5f}, {1.0f, 0.5f}}, {{0.5f, 0.0f}, {0.5f, 1.0f}}} // pluses
    };

    public BitmapMetalTread() {
        super();

        hasNormal = true;
        hasMetallicRoughness = true;
        hasEmissive = false;
        hasAlpha = false;
    }

    //
    // metal bitmaps
    //
    @Override
    public void generateInternal() {
        int x, y, dx, dy, sx, sy, ex, ey, idx;
        int corrCount, corrWid, corrHigh, lineStyle;
        float lineWid, lineHigh;
        float[][] line;
        RagColor metalColor, metalCorrColor;

        // background
        metalColor = getRandomColor();

        createPerlinNoiseData(16, 16);
        drawRect(0, 0, textureSize, textureSize, metalColor);
        drawPerlinNoiseRect(0, 0, textureSize, textureSize, 0.8f, 1.0f);

        drawMetalShine(0, 0, textureSize, textureSize, metalColor);

        // treads
        metalCorrColor = adjustColorRandom(metalColor, 0.6f, 0.7f);

        corrCount = (4 + AppWindow.random.nextInt(20)) & 0b11111110;
        corrWid = textureSize / corrCount;
        corrHigh = textureSize / corrCount;

        lineWid = (float) (corrWid - 4);
        lineHigh = (float) (corrHigh - 4);

        lineStyle = AppWindow.random.nextInt(TREAD_LINES.length);

        dy = (textureSize - (corrHigh * corrCount)) / 2;

        for (y = 0; y != corrCount; y++) {

            dx = (textureSize - (corrWid * corrCount)) / 2;

            for (x = 0; x != corrCount; x++) {

                idx = ((y & 0x1) * 2) + (x & 0x1);
                line = TREAD_LINES[lineStyle][idx];

                sx = dx + (int) (line[0][0] * lineWid);
                sy = dy + (int) (line[0][1] * lineHigh);
                ex = dx + (int) (line[1][0] * lineWid);
                ey = dy + (int) (line[1][1] * lineHigh);

                drawLineColor(sx, sy, ex, ey, metalCorrColor);
                drawLineNormal(sx, sy, ex, ey, NORMAL_CLEAR);

                if (Math.abs(ex - sx) > Math.abs(ey - sy)) {
                    drawLineNormal(sx, (sy + 1), ex, (ey + 1), NORMAL_BOTTOM_45);
                    drawLineNormal(sx, (sy - 1), ex, (ey - 1), NORMAL_TOP_45);
                } else {
                    drawLineNormal((sx + 1), sy, (ex + 1), ey, NORMAL_RIGHT_45);
                    drawLineNormal((sx - 1), sy, (ex - 1), ey, NORMAL_LEFT_45);
                }

                dx += corrWid;
            }

            dy += corrHigh;
        }

        // finish with the metallic-roughness
        createMetallicRoughnessMap(0.5f, 0.6f);
    }
}
