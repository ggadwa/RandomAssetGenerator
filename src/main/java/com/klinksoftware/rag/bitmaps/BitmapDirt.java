package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;
import java.awt.Rectangle;
import java.util.ArrayList;

@BitmapInterface
public class BitmapDirt extends BitmapBase {

    public BitmapDirt() {
        super();

        hasNormal = true;
        hasMetallicRoughness = true;
        hasEmissive = false;
        hasAlpha = false;
    }

    @Override
    public void generateInternal() {
        int n, k, stoneCount, maxStoneSize, failCount;
        int lft, rgt, top, bot, edgeSize;
        float f, xRoundFactor, yRoundFactor, normalZFactor;
        float[] backgroundData, stoneColorData, stoneNormalData;
        boolean hit;
        RagColor dirtColor, stoneColor, outlineColor;
        Rectangle testRect;
        ArrayList<Rectangle> rects;

        // background is always brownish
        dirtColor = new RagColor((0.4f + AppWindow.random.nextFloat(0.2f)), (0.2f + AppWindow.random.nextFloat(0.2f)), (0.0f + AppWindow.random.nextFloat(0.1f)));

        // ground
        drawRect(0, 0, textureSize, textureSize, dirtColor);

        createPerlinNoiseData(16, 16);
        f = 0.5f + AppWindow.random.nextFloat(0.2f);
        drawPerlinNoiseRect(0, 0, textureSize, textureSize, f, (f + 0.2f));
        createPerlinNoiseData(16, 16);
        f = 0.5f + AppWindow.random.nextFloat(0.2f);
        drawPerlinNoiseRect(0, 0, textureSize, textureSize, f, (f + 0.2f));
        createPerlinNoiseData(32, 32);
        f = 0.5f + AppWindow.random.nextFloat(0.2f);
        drawPerlinNoiseRect(0, 0, textureSize, textureSize, f, (f + 0.2f));

        createNormalNoiseData(2.5f, 0.5f);
        drawNormalNoiseRect(0, 0, textureSize, textureSize);
        blur(colorData, 0, 0, textureSize, textureSize, (1 + AppWindow.random.nextInt(4)), true);

        // stones
        backgroundData = colorData.clone();
        stoneColorData = colorData.clone();
        stoneNormalData = normalData.clone();

        stoneCount = 3 + AppWindow.random.nextInt(8);
        maxStoneSize = textureSize / 4;

        rects = new ArrayList<>();

        for (n = 0; n != stoneCount; n++) {
            stoneColor = getRandomColorDull(0.8f);

            failCount = 0;
            lft = rgt = top = bot = 0;
            testRect = null;

            while (failCount < 10) {
                lft = AppWindow.random.nextInt(textureSize - maxStoneSize);
                rgt = lft + ((maxStoneSize / 5) + AppWindow.random.nextInt((maxStoneSize * 4) / 5));
                top = AppWindow.random.nextInt(textureSize - maxStoneSize);
                bot = top + ((maxStoneSize / 5) + AppWindow.random.nextInt((maxStoneSize * 4) / 5));

                hit = false;

                testRect = new Rectangle(lft, top, (rgt - lft), (bot - top));
                for (k = 0; k != rects.size(); k++) {
                    if (testRect.intersects(rects.get(k))) {
                        hit = true;
                        break;
                    }
                }

                if (!hit) {
                    break;
                }

                failCount++;
            }

            rects.add(testRect);

            edgeSize = 50 + AppWindow.random.nextInt(60);
            xRoundFactor = 0.02f + (AppWindow.random.nextFloat(0.1f));
            yRoundFactor = 0.02f + (AppWindow.random.nextFloat(0.1f));
            normalZFactor = 0.2f + AppWindow.random.nextFloat(0.2f);

            if (AppWindow.random.nextBoolean()) {
                createPerlinNoiseData(16, 16);
            } else {
                createPerlinNoiseData(32, 32);
            }
            createNormalNoiseData((2.0f + AppWindow.random.nextFloat(0.3f)), (0.3f + AppWindow.random.nextFloat(0.2f)));

            // draw on the background
            colorData = backgroundData.clone();
            drawRectAlpha(0, 0, textureSize, textureSize, 1.0f);

            outlineColor = adjustColor(stoneColor, 0.5f);
            drawOval(lft, top, rgt, bot, 0.0f, 1.0f, xRoundFactor, yRoundFactor, edgeSize, 0.5f, stoneColor, outlineColor, normalZFactor, false, true, 0.4f, 1.2f);

            // gravity distortions to make stones unique
            gravityDistortEdges(lft, top, rgt, bot, 10, 35, 5);

            // and copy over
            blockCopy(colorData, normalData, lft, top, rgt, bot, stoneColorData, stoneNormalData);
        }

        // push over the stones
        colorData = stoneColorData;
        normalData = stoneNormalData;

        // finish with the metallic-roughness
        createMetallicRoughnessMap(0.5f, 0.4f);
    }

}
