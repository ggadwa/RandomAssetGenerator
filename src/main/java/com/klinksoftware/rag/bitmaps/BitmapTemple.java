package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

@BitmapInterface
public class BitmapTemple extends BitmapBase {

    public BitmapTemple(int textureSize) {
        super(textureSize);

        hasNormal = true;
        hasMetallicRoughness = true;
        hasEmissive = false;
        hasAlpha = false;
    }

    private void generateTempleBlock(int lft, int top, int rgt, int bot, int margin, int edgeSize, RagColor color, RagColor altColor) {
        int mx, my;
        int halfMargin, noiseSize;
        boolean faceIn;
        RagColor drawColor;

        lft += margin;
        top += margin;
        rgt -= margin;
        bot -= margin;

        if ((lft >= rgt) || (top >= bot)) {
            return;
        }

        // random color
        drawColor = (AppWindow.random.nextFloat() < 0.75f) ? color : altColor;

        // the stone background
        // new for each iteration so it doesn't look like it's outside the 3d effects
        drawRect(lft, top, rgt, bot, adjustColorRandom(drawColor, 0.8f, 1.0f));

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
                generateTempleBlock(lft, top, rgt, bot, margin, edgeSize, drawColor, altColor);
                draw3DDarkenFrameRect(lft, top, rgt, bot, edgeSize, (0.65f + AppWindow.random.nextFloat(0.1f)), faceIn);
                break;

            // horizontal boxes
            case 1:
                mx = (lft + rgt) / 2;
                generateTempleBlock(lft, top, (mx - halfMargin), bot, margin, edgeSize, drawColor, altColor);
                draw3DDarkenFrameRect(lft, top, (mx - halfMargin), bot, edgeSize, (0.65f + AppWindow.random.nextFloat(0.1f)), faceIn);

                generateTempleBlock((mx + halfMargin), top, rgt, bot, margin, edgeSize, drawColor, altColor);
                draw3DDarkenFrameRect((mx + halfMargin), top, rgt, bot, edgeSize, (0.65f + AppWindow.random.nextFloat(0.1f)), faceIn);
                break;

            // vertical boxes
            case 2:
                my = (top + bot) / 2;
                generateTempleBlock(lft, top, rgt, (my - halfMargin), margin, edgeSize, drawColor, altColor);
                draw3DDarkenFrameRect(lft, top, rgt, (my - halfMargin), edgeSize, (0.65f + AppWindow.random.nextFloat(0.1f)), faceIn);

                generateTempleBlock(lft, (my + halfMargin), rgt, bot, margin, edgeSize, drawColor, altColor);
                draw3DDarkenFrameRect(lft, (my + halfMargin), rgt, bot, edgeSize, (0.65f + AppWindow.random.nextFloat(0.1f)), faceIn);
                break;

            // 4 boxes
            case 3:
                mx = (lft + rgt) / 2;
                my = (top + bot) / 2;

                generateTempleBlock(lft, top, (mx - halfMargin), (my - halfMargin), margin, edgeSize, drawColor, altColor);
                draw3DDarkenFrameRect(lft, top, (mx - halfMargin), (my - halfMargin), edgeSize, (0.65f + AppWindow.random.nextFloat(0.1f)), faceIn);

                generateTempleBlock((mx + halfMargin), top, rgt, (my - halfMargin), margin, edgeSize, drawColor, altColor);
                draw3DDarkenFrameRect((mx + halfMargin), top, rgt, (my - halfMargin), edgeSize, (0.65f + AppWindow.random.nextFloat(0.1f)), faceIn);

                generateTempleBlock(lft, (my + halfMargin), (mx - halfMargin), bot, margin, edgeSize, drawColor, altColor);
                draw3DDarkenFrameRect(lft, (my + halfMargin), (mx - halfMargin), bot, edgeSize, (0.65f + AppWindow.random.nextFloat(0.1f)), faceIn);

                generateTempleBlock((mx + halfMargin), (my + halfMargin), rgt, bot, margin, edgeSize, drawColor, altColor);
                draw3DDarkenFrameRect((mx + halfMargin), (my + halfMargin), rgt, bot, edgeSize, (0.65f + AppWindow.random.nextFloat(0.1f)), faceIn);
                break;

        }
    }

    @Override
    public void generateInternal() {
        int margin, edgeSize;
        RagColor color, altColor;

        // recursive draw
        color = getRandomColor();
        altColor = getRandomColor();

        margin = 15 + AppWindow.random.nextInt(15);
        edgeSize = 4 + AppWindow.random.nextInt(4);

        generateTempleBlock(-margin, -margin, (textureSize + margin), (textureSize + margin), margin, edgeSize, color, altColor);

        // finish with the metallic-roughness
        createMetallicRoughnessMap(0.35f, 0.3f);
    }
}
