package com.klinksoftware.rag.bitmap;

import com.klinksoftware.rag.bitmap.utility.BitmapInterface;
import com.klinksoftware.rag.bitmap.utility.BitmapBase;
import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

@BitmapInterface
public class BitmapConcrete extends BitmapBase
{
    public BitmapConcrete(int textureSize) {
        super(textureSize);

        hasNormal=true;
        hasMetallicRoughness=true;
        hasEmissive=false;
        hasAlpha=false;
    }

        //
        // concrete bitmaps
        //
    private void drawCracks(int lft, int top, int rgt, int bot, RagColor crackColor) {
        int sx, sy, ex, ey;

        if (AppWindow.random.nextBoolean()) {
            return;
        }

        switch (AppWindow.random.nextInt(4)) {
            case 0:
                sx = lft + AppWindow.random.nextInt(rgt - lft);
                sy = top;
                ex = AppWindow.random.nextBoolean() ? lft : rgt;
                ey = top + AppWindow.random.nextInt(bot - top);
                break;
            case 1:
                sx = lft + AppWindow.random.nextInt(rgt - lft);
                sy = bot;
                ex = AppWindow.random.nextBoolean() ? lft : rgt;
                ey = top + AppWindow.random.nextInt(bot - top);
                break;
            case 2:
                sx = lft;
                sy = top + AppWindow.random.nextInt(bot - top);
                ex = lft + AppWindow.random.nextInt(rgt - lft);
                ey = AppWindow.random.nextBoolean() ? lft : rgt;
                break;
            default:
                sx = rgt;
                sy = top + AppWindow.random.nextInt(rgt - lft);
                ex = lft + AppWindow.random.nextInt(rgt - lft);
                ey = AppWindow.random.nextBoolean() ? lft : rgt;
                break;
        }

        drawSimpleCrack(sx, sy, ex, ey, (4 + AppWindow.random.nextInt(4)), AppWindow.random.nextInt(textureSize / 15), AppWindow.random.nextInt(textureSize / 15), crackColor);
    }

    private void drawStains(int lft, int top, int rgt, int bot) {
        int n, k, stainCount, stainSize;
        int xSize, ySize, markCount;

        stainCount = AppWindow.random.nextInt(5);
        stainSize = textureSize / 10;

        for (n = 0; n != stainCount; n++) {
            lft = AppWindow.random.nextInt(textureSize);
            xSize = stainSize + AppWindow.random.nextInt(stainSize);

            top = AppWindow.random.nextInt(textureSize);
            ySize = stainSize + AppWindow.random.nextInt(stainSize);

            markCount = 2 + AppWindow.random.nextInt(4);

            for (k = 0; k != markCount; k++) {
                rgt = lft + xSize;
                if (rgt >= textureSize) {
                    rgt = textureSize - 1;
                }
                bot = top + ySize;
                if (bot >= textureSize) {
                    bot = textureSize - 1;
                }

                drawOvalStain(lft, top, rgt, bot, (0.01f + AppWindow.random.nextFloat(0.01f)), (0.15f + AppWindow.random.nextFloat(0.05f)), (0.7f + AppWindow.random.nextFloat(0.2f)));

                lft += (AppWindow.random.nextBoolean()) ? (-(xSize / 3)) : (xSize / 3);
                top += (AppWindow.random.nextBoolean()) ? (-(ySize / 3)) : (ySize / 3);
                xSize = (int) ((float) xSize * 0.8f);
                ySize = (int) ((float) ySize * 0.8f);
            }
        }
    }

    private void drawSingleChunk(int lft, int top, int rgt, int bot, int jointSize, RagColor concreteColor) {
        int n, crackCount;
        RagColor crackColor;

        // the concrete background
        drawRect(lft, top, rgt, bot, concreteColor);

        createPerlinNoiseData(16, 16);
        drawPerlinNoiseRect(lft, top, rgt, bot, 0.6f, 1.0f);

        createNormalNoiseData(1.0f, 0.2f);
        drawNormalNoiseRect(lft, top, rgt, bot);

        // stains
        drawStains(lft, top, rgt, bot);
        blur(colorData, lft, top, rgt, bot, (1 + AppWindow.random.nextInt(textureSize / 125)), false);
        blur(normalData, lft, top, rgt, bot, (3 + AppWindow.random.nextInt(textureSize / 125)), false);

        // concrete expansion joints
        draw3DDarkenFrameRect(lft, top, rgt, bot, jointSize, (0.85f + AppWindow.random.nextFloat(0.1f)), true);

        // cracks
        crackCount = AppWindow.random.nextInt(3);

        for (n = 0; n != crackCount; n++) {
            crackColor = adjustColorRandom(concreteColor, 0.4f, 0.5f);
            drawCracks((lft + jointSize), (top + jointSize), (rgt - 2), (bot - 2), crackColor);
        }
    }

    @Override
    public void generateInternal() {
        int mid, jointSize;
        RagColor concreteColor;

        concreteColor = getRandomColor();
        jointSize = (textureSize / 150) + AppWindow.random.nextInt(textureSize / 250);

        mid = textureSize / 2;

        switch (AppWindow.random.nextInt(3)) {
            case 0: // 2 long cuts
                drawSingleChunk(0, 0, mid, textureSize, jointSize, concreteColor);
                drawSingleChunk(mid, 0, textureSize, textureSize, jointSize, concreteColor);
                break;
            case 1: // big square
                drawSingleChunk(0, 0, textureSize, textureSize, jointSize, concreteColor);
                break;
            case 2: // small squares
                drawSingleChunk(0, 0, mid, mid, jointSize, concreteColor);
                drawSingleChunk(mid, 0, textureSize, mid, jointSize, concreteColor);
                drawSingleChunk(0, mid, mid, textureSize, jointSize, concreteColor);
                drawSingleChunk(mid, mid, textureSize, textureSize, jointSize, concreteColor);
                break;
        }

        // finish with the metallic-roughness
        createMetallicRoughnessMap(0.35f,0.3f);
    }
}
