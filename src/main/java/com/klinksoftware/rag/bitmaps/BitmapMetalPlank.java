package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

@BitmapInterface
public class BitmapMetalPlank extends BitmapBase {

    public BitmapMetalPlank(int textureSize) {
        super(textureSize);

        hasNormal = true;
        hasMetallicRoughness = true;
        hasEmissive = false;
        hasAlpha = false;
    }

    private void generatePlank(int lx, int ty, int rx, int by, int edgeSize, int screwSize, int lineNumber, boolean alternateScrews, RagColor metalColor, RagColor altMetalColor) {
        int streakWid, sx, ex;
        RagColor color, streakColor, outlineColor;

        color = ((lineNumber & 0x1) == 0) ? metalColor : altMetalColor;

        createPerlinNoiseData(16, 16);
        drawRect(lx, ty, rx, by, color);
        drawPerlinNoiseRect(lx, ty, rx, by, 0.8f, 1.0f);
        drawMetalShine(lx, ty, rx, by, color);
        draw3DDarkenFrameRect(lx, ty, rx, by, edgeSize, (0.65f + AppWindow.random.nextFloat(0.1f)), true);

        // any dirt streaks
        if (AppWindow.random.nextFloat() < 0.3f) {
            streakWid = 25 + AppWindow.random.nextInt(25);

            sx = lx + AppWindow.random.nextInt((rx - lx) - streakWid);
            ex = sx + streakWid;

            streakColor = adjustColorRandom(color, 0.65f, 0.75f);
            drawStreakDirt(sx, ty, ex, by, 5, 0.25f, 0.45f, streakColor);
        }

        // any screws
        if ((!alternateScrews) || ((lineNumber & 0x1) == 0)) {
            color = ((lineNumber & 0x1) != 0) ? metalColor : altMetalColor;
            outlineColor = adjustColor(color, 0.5f);
            drawScrew(screwSize, (((ty + by) / 2) - (screwSize / 2)), color, outlineColor, screwSize, edgeSize);
            drawScrew((textureSize - (screwSize * 2)), (((ty + by) / 2) - (screwSize / 2)), (((lineNumber & 0x1) != 0) ? metalColor : altMetalColor), outlineColor, screwSize, edgeSize);
        }
    }

    @Override
    public void generateInternal() {
        int n, ty, by, yAdd, mx, mx2, treadCount;
        int edgeSize, screwSize;
        boolean alternateScrews;
        RagColor metalColor, altMetalColor;

        metalColor = getRandomMetalColor();
        altMetalColor = getRandomMetalColor();
        edgeSize = 4 + AppWindow.random.nextInt(5);
        screwSize = 5 + AppWindow.random.nextInt(25);

        alternateScrews = AppWindow.random.nextBoolean();
        altMetalColor = adjustColorRandom(metalColor, 0.7f, 1.1f);

        treadCount = 4 + AppWindow.random.nextInt(4);

        ty = 0;
        yAdd = textureSize / treadCount;

        for (n = 0; n != treadCount; n++) {
            by = (n == (treadCount - 1)) ? textureSize : (ty + yAdd);

            // the plank
            switch (AppWindow.random.nextInt(4)) {
                case 0: // long ones twice as much
                case 1:
                    generatePlank(0, ty, textureSize, by, edgeSize, screwSize, n, alternateScrews, metalColor, altMetalColor);
                    break;
                case 2:
                    mx = textureSize / 2;
                    generatePlank(0, ty, mx, by, edgeSize, screwSize, n, alternateScrews, metalColor, altMetalColor);
                    generatePlank((mx + 1), ty, textureSize, by, edgeSize, screwSize, n, alternateScrews, metalColor, altMetalColor);
                    break;
                case 3:
                    mx = textureSize / 3;
                    mx2 = textureSize - mx;
                    generatePlank(0, ty, mx, by, edgeSize, screwSize, n, alternateScrews, metalColor, altMetalColor);
                    generatePlank((mx + 1), ty, mx2, by, edgeSize, screwSize, n, alternateScrews, metalColor, altMetalColor);
                    generatePlank((mx2 + 1), ty, textureSize, by, edgeSize, screwSize, n, alternateScrews, metalColor, altMetalColor);
                    break;
            }

            ty += yAdd;
        }

        // finish with the metallic-roughness
        createMetallicRoughnessMap(0.5f, 0.6f);
    }
}
