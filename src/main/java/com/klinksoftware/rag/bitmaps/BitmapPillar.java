package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

@BitmapInterface
public class BitmapPillar extends BitmapBase {

    public BitmapPillar() {
        super();

        hasNormal = true;
        hasMetallicRoughness = true;
        hasEmissive = false;
        hasAlpha = false;
    }

    private void generatePillarBlock(int lft, int top, int rgt, int bot, int margin, int edgeSize, RagColor concreteColor, RagColor lineColor) {
        int mx, my;
        int halfMargin, noiseSize;
        boolean faceIn;

        lft += margin;
        top += margin;
        rgt -= margin;
        bot -= margin;

        if ((lft >= rgt) || (top >= bot)) {
            return;
        }

        // the stone background
        // new for each iteration so it doesn't look like it's outside the 3d effects
        drawRect(lft, top, rgt, bot, adjustColorRandom(concreteColor, 0.8f, 1.0f));

        noiseSize = AppWindow.random.nextBoolean() ? 16 : 8;
        createPerlinNoiseData(noiseSize, noiseSize);
        drawPerlinNoiseRect(lft, top, rgt, bot, 0.8f, 1.0f);

        createNormalNoiseData(3.0f, 0.4f);
        drawNormalNoiseRect(lft, top, rgt, bot);

        blur(colorData, lft, top, rgt, bot, 1, false);

        // skip out if we don't have enough margin
        if ((Math.abs(lft - rgt) <= margin) || (Math.abs(top - bot) <= margin)) {
            return;
        }

        halfMargin = margin / 2;

        faceIn = AppWindow.random.nextBoolean();

        switch (AppWindow.random.nextInt(4)) {

            // one box
            case 0:
                draw3DFrameRect(lft, top, rgt, bot, edgeSize, lineColor, faceIn);
                generatePillarBlock(lft, top, rgt, bot, margin, edgeSize, concreteColor, lineColor);
                break;

            // horizontal boxes
            case 1:
                mx = (lft + rgt) / 2;
                draw3DFrameRect(lft, top, (mx - halfMargin), bot, edgeSize, lineColor, faceIn);
                generatePillarBlock(lft, top, (mx - halfMargin), bot, margin, edgeSize, concreteColor, lineColor);

                draw3DFrameRect((mx + halfMargin), top, rgt, bot, edgeSize, lineColor, faceIn);
                generatePillarBlock((mx + halfMargin), top, rgt, bot, margin, edgeSize, concreteColor, lineColor);
                break;

            // vertical boxes
            case 2:
                my = (top + bot) / 2;
                draw3DFrameRect(lft, top, rgt, (my - halfMargin), edgeSize, lineColor, faceIn);
                generatePillarBlock(lft, top, rgt, (my - halfMargin), margin, edgeSize, concreteColor, lineColor);

                draw3DFrameRect(lft, (my + halfMargin), rgt, bot, edgeSize, lineColor, faceIn);
                generatePillarBlock(lft, (my + halfMargin), rgt, bot, margin, edgeSize, concreteColor, lineColor);
                break;

            // 4 boxes
            case 3:
                mx = (lft + rgt) / 2;
                my = (top + bot) / 2;

                draw3DFrameRect(lft, top, (mx - halfMargin), (my - halfMargin), edgeSize, lineColor, faceIn);
                generatePillarBlock(lft, top, (mx - halfMargin), (my - halfMargin), margin, edgeSize, concreteColor, lineColor);

                draw3DFrameRect((mx + halfMargin), top, rgt, (my - halfMargin), edgeSize, lineColor, faceIn);
                generatePillarBlock((mx + halfMargin), top, rgt, (my - halfMargin), margin, edgeSize, concreteColor, lineColor);

                draw3DFrameRect(lft, (my + halfMargin), (mx - halfMargin), bot, edgeSize, lineColor, faceIn);
                generatePillarBlock(lft, (my + halfMargin), (mx - halfMargin), bot, margin, edgeSize, concreteColor, lineColor);

                draw3DFrameRect((mx + halfMargin), (my + halfMargin), rgt, bot, edgeSize, lineColor, faceIn);
                generatePillarBlock((mx + halfMargin), (my + halfMargin), rgt, bot, margin, edgeSize, concreteColor, lineColor);
                break;

        }
    }

    @Override
    public void generateInternal() {
        int margin, edgeSize;
        RagColor concreteColor, lineColor;

        // recursive draw
        concreteColor = getRandomColor();
        lineColor = adjustColor(concreteColor, 0.75f);

        margin = 15 + AppWindow.random.nextInt(15);
        edgeSize = 4 + AppWindow.random.nextInt(4);

        generatePillarBlock(-margin, -margin, (textureSize + margin), (textureSize + margin), margin, edgeSize, concreteColor, lineColor);

        // finish with the metallic-roughness
        createMetallicRoughnessMap(0.35f, 0.3f);
    }
}
