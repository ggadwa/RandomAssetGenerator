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

    private void generateSingleStone(int lft, int top, int rgt, int bot, int edgeSize, float xRoundFactor, float yRoundFactor, float normalZFactor, RagColor stoneColor, RagColor outlineColor, float[] stoneColorData, float[] stoneNormalData) {
        drawRect(0, 0, textureSize, textureSize, new RagColor(0.0f, 0.0f, 0.0f));
        drawRectAlpha(0, 0, textureSize, textureSize, 1.0f);

        outlineColor = adjustColor(stoneColor, 0.5f);
        drawOval(lft, top, rgt, bot, 0.0f, 1.0f, xRoundFactor, yRoundFactor, edgeSize, 0.5f, stoneColor, outlineColor, normalZFactor, false, true, 0.4f, 1.2f);

        // gravity distortions to make stones unique
        gravityDistortEdges(lft, top, rgt, bot, 10, 35, 5);

        // copy over
        blockCopy(colorData, normalData, lft, top, rgt, bot, stoneColorData, stoneNormalData);
    }

    private void generateSingleStoneWrap(int lft, int top, int rgt, int bot, float[] stoneColorData, float[] stoneNormalData) {
        int edgeSize;
        float xRoundFactor, yRoundFactor, normalZFactor;
        RagColor stoneColor, outlineColor;

        edgeSize = 30 + AppWindow.random.nextInt(80);
        xRoundFactor = 0.02f + (AppWindow.random.nextFloat(0.05f));
        yRoundFactor = 0.02f + (AppWindow.random.nextFloat(0.05f));
        normalZFactor = 0.2f + (AppWindow.random.nextFloat(0.2f)); // different z depths

        if (AppWindow.random.nextBoolean()) {
            createPerlinNoiseData(16, 16);
        } else {
            createPerlinNoiseData(32, 32);
        }
        createNormalNoiseData((2.0f + AppWindow.random.nextFloat(0.3f)), (0.3f + AppWindow.random.nextFloat(0.2f)));

        stoneColor = getRandomColorDull(0.8f);
        outlineColor = adjustColor(stoneColor, 0.5f);

        // we need to wrap all stones
        generateSingleStone(lft, top, rgt, bot, edgeSize, xRoundFactor, yRoundFactor, normalZFactor, stoneColor, outlineColor, stoneColorData, stoneNormalData);

        if (bot > textureSize) {
            generateSingleStone(lft, (top - textureSize), rgt, (bot - textureSize), edgeSize, xRoundFactor, yRoundFactor, normalZFactor, stoneColor, outlineColor, stoneColorData, stoneNormalData);
        }
        if (rgt > textureSize) {
            generateSingleStone((lft - textureSize), top, (rgt - textureSize), bot, edgeSize, xRoundFactor, yRoundFactor, normalZFactor, stoneColor, outlineColor, stoneColorData, stoneNormalData);
        }
    }

    private void generateStoneGroup(int stoneCount, int minStoneSize, int maxStoneSize) {
        int n, lft, top, rgt, bot, edgeSize;
        float xRoundFactor, yRoundFactor, normalZFactor;
        float[] stoneColorData, stoneNormalData;
        RagColor stoneColor, drawStoneColor, outlineColor;

        // stones
        stoneColor = this.getRandomGray(0.5f, 0.8f);
        stoneColorData = colorData.clone();
        stoneNormalData = normalData.clone();

        for (n = 0; n != stoneCount; n++) {
            lft = AppWindow.random.nextInt(textureSize);
            rgt = lft + (minStoneSize + AppWindow.random.nextInt(maxStoneSize - minStoneSize));
            top = AppWindow.random.nextInt(textureSize);
            bot = top + (minStoneSize + AppWindow.random.nextInt(maxStoneSize - minStoneSize));

            generateSingleStoneWrap(lft, top, rgt, bot, stoneColorData, stoneNormalData);
        }

        // push over the stones
        colorData = stoneColorData;
        normalData = stoneNormalData;
    }

    protected void drawSimpleCrack2(int sx, int sy, int ex, int ey, int segCount, int lineXVarient, int lineYVarient, RagColor color) {
        int n, dx, dy, dx2, dy2;

        if ((Math.abs(lineXVarient) <= 1) || (Math.abs(lineYVarient) <= 1)) {
            return;
        }

        dx = sx;
        dy = sy;

        for (n = 0; n != segCount; n++) {

            if ((n + 1) == segCount) {
                dx2 = ex;
                dy2 = ey;
            } else {
                dx2 = (int) (sx + ((float) ((ex - sx) * (n + 1)) / (float) segCount)) + (AppWindow.random.nextInt(Math.abs(lineXVarient)) * (int) Math.signum(lineXVarient));
                dy2 = (int) (sy + ((float) ((ey - sy) * (n + 1)) / (float) segCount)) + (AppWindow.random.nextInt(Math.abs(lineYVarient)) * (int) Math.signum(lineYVarient));
            }

            drawLineColor(dx, dy, dx2, dy2, color);
            drawLineNormal(dx, dy, dx2, dy2, NORMAL_CLEAR);
            drawLineNormal((dx - 1), dy, (dx2 - 1), dy2, NORMAL_RIGHT_45);
            drawLineNormal((dx + 1), dy, (dx2 + 1), dy2, NORMAL_LEFT_45);

            dx = dx2;
            dy = dy2;
        }
    }


    @Override
    public void generateInternal() {
        int n, k, stainSize, xSize, ySize;
        int lft, rgt, top, bot, stainCount, markCount;
        RagColor concreteColor, jointColor, altJointColor, crackColor;

        concreteColor = getRandomColor();

        // the concrete background
        drawRect(0, 0, textureSize, textureSize, concreteColor);

        createPerlinNoiseData(16, 16);
        drawPerlinNoiseRect(0, 0, textureSize, textureSize, 0.7f, 1.0f);

        createNormalNoiseData(3.0f, 0.3f);
        drawNormalNoiseRect(0, 0, textureSize, textureSize);

        crackColor = new RagColor(1.0f, 1.0f, 0.0f);
        //drawCrackLine(10, 10, 200, 200, crackColor);

        RagColor stoneColor, drawStoneColor, outlineColor;
        float[] stoneColorData, stoneNormalData;
        int stoneCount, maxStoneSize, edgeSize;
        float xRoundFactor, yRoundFactor, normalZFactor;

        // stones
        stoneColor = this.getRandomGray(0.5f, 0.8f);
        stoneColorData = colorData.clone();
        stoneNormalData = normalData.clone();

        //stoneCount =;
        //maxStoneSize = ;

        //createPerlinNoiseData(32, 32);
        //createNormalNoiseData(5.0f, 0.3f);

        //generateStoneGroup((10 + AppWindow.random.nextInt(10)), 250, 300);
        //generateStoneGroup((10 + AppWindow.random.nextInt(5)), 100, 200);
        //generateStoneGroup((5 + AppWindow.random.nextInt(5)), 50, 100);
        /*
        int x = -50;
        int wid;

        while (true) {
            wid = 50 + AppWindow.random.nextInt(50);
            generateSingleStoneWrap(x, -50, (x + wid), (textureSize + 50), stoneColorData, stoneNormalData);

            x += wid / (2 + AppWindow.random.nextInt(2));
            if (x >= textureSize) {
                break;
            }
        }

        colorData = stoneColorData;
        normalData = stoneNormalData;
         */
        drawSimpleCrack2(100, 0, 100, textureSize, 10, 20, 20, stoneColor);


        // finish with the metallic-roughness
        createMetallicRoughnessMap(0.35f, 0.3f);
    }
}
