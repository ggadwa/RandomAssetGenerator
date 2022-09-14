package com.klinksoftware.rag.bitmap;

import com.klinksoftware.rag.bitmap.utility.BitmapInterface;
import com.klinksoftware.rag.bitmap.utility.BitmapBase;
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

    private void generateBlockBackground(int lft, int top, int rgt, int bot, RagColor drawColor) {
        int noiseSize;

        drawRect(lft, top, rgt, bot, adjustColorRandom(drawColor, 0.8f, 1.0f));

        noiseSize = AppWindow.random.nextBoolean() ? 16 : 8;
        createPerlinNoiseData(noiseSize, noiseSize);
        drawPerlinNoiseRect(lft, top, rgt, bot, 0.8f, 1.0f);

        createNormalNoiseData(3.0f, 0.4f);
        drawNormalNoiseRect(lft, top, rgt, bot);

        blur(colorData, lft, top, rgt, bot, (textureSize / 500), false);
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
        /*
        drawRect(lft, top, rgt, bot, adjustColorRandom(drawColor, 0.8f, 1.0f));

        noiseSize = AppWindow.random.nextBoolean() ? 16 : 8;
        createPerlinNoiseData(noiseSize, noiseSize);
        drawPerlinNoiseRect(lft, top, rgt, bot, 0.8f, 1.0f);

        createNormalNoiseData(3.0f, 0.4f);
        drawNormalNoiseRect(lft, top, rgt, bot);

        blur(colorData, lft, top, rgt, bot, (textureSize / 500), false);
*/
        // skip out if we don't have enough margin
        if ((Math.abs(lft - rgt) <= margin) || (Math.abs(top - bot) <= margin)) {
            generateBlockBackground(lft, top, rgt, bot, drawColor);
            return;
        }

        halfMargin = margin / 2;

        faceIn = AppWindow.random.nextBoolean();

        switch (AppWindow.random.nextInt(4)) {

            // one box
            case 0:
                generateBlockBackground(lft, top, rgt, bot, drawColor);
                generateTempleBlock(lft, top, rgt, bot, margin, edgeSize, drawColor, altColor);
                draw3DDarkenFrameRect(lft, top, rgt, bot, edgeSize, (0.65f + AppWindow.random.nextFloat(0.1f)), faceIn);
                break;

            // horizontal boxes
            case 1:
                mx = (lft + rgt) / 2;
                generateBlockBackground(lft, top, (mx - halfMargin), bot, drawColor);
                generateTempleBlock(lft, top, (mx - halfMargin), bot, margin, edgeSize, drawColor, altColor);
                draw3DDarkenFrameRect(lft, top, (mx - halfMargin), bot, edgeSize, (0.65f + AppWindow.random.nextFloat(0.1f)), faceIn);

                generateBlockBackground((mx + halfMargin), top, rgt, bot, drawColor);
                generateTempleBlock((mx + halfMargin), top, rgt, bot, margin, edgeSize, drawColor, altColor);
                draw3DDarkenFrameRect((mx + halfMargin), top, rgt, bot, edgeSize, (0.65f + AppWindow.random.nextFloat(0.1f)), faceIn);
                break;

            // vertical boxes
            case 2:
                my = (top + bot) / 2;
                generateBlockBackground(lft, top, rgt, (my - halfMargin), drawColor);
                generateTempleBlock(lft, top, rgt, (my - halfMargin), margin, edgeSize, drawColor, altColor);
                draw3DDarkenFrameRect(lft, top, rgt, (my - halfMargin), edgeSize, (0.65f + AppWindow.random.nextFloat(0.1f)), faceIn);

                generateBlockBackground(lft, (my + halfMargin), rgt, bot, drawColor);
                generateTempleBlock(lft, (my + halfMargin), rgt, bot, margin, edgeSize, drawColor, altColor);
                draw3DDarkenFrameRect(lft, (my + halfMargin), rgt, bot, edgeSize, (0.65f + AppWindow.random.nextFloat(0.1f)), faceIn);
                break;

            // 4 boxes
            case 3:
                mx = (lft + rgt) / 2;
                my = (top + bot) / 2;

                generateBlockBackground(lft, top, (mx - halfMargin), (my - halfMargin), drawColor);
                generateTempleBlock(lft, top, (mx - halfMargin), (my - halfMargin), margin, edgeSize, drawColor, altColor);
                draw3DDarkenFrameRect(lft, top, (mx - halfMargin), (my - halfMargin), edgeSize, (0.65f + AppWindow.random.nextFloat(0.1f)), faceIn);

                generateBlockBackground((mx + halfMargin), top, rgt, (my - halfMargin), drawColor);
                generateTempleBlock((mx + halfMargin), top, rgt, (my - halfMargin), margin, edgeSize, drawColor, altColor);
                draw3DDarkenFrameRect((mx + halfMargin), top, rgt, (my - halfMargin), edgeSize, (0.65f + AppWindow.random.nextFloat(0.1f)), faceIn);

                generateBlockBackground(lft, (my + halfMargin), (mx - halfMargin), bot, drawColor);
                generateTempleBlock(lft, (my + halfMargin), (mx - halfMargin), bot, margin, edgeSize, drawColor, altColor);
                draw3DDarkenFrameRect(lft, (my + halfMargin), (mx - halfMargin), bot, edgeSize, (0.65f + AppWindow.random.nextFloat(0.1f)), faceIn);

                generateBlockBackground((mx + halfMargin), (my + halfMargin), rgt, bot, drawColor);
                generateTempleBlock((mx + halfMargin), (my + halfMargin), rgt, bot, margin, edgeSize, drawColor, altColor);
                draw3DDarkenFrameRect((mx + halfMargin), (my + halfMargin), rgt, bot, edgeSize, (0.65f + AppWindow.random.nextFloat(0.1f)), faceIn);
                break;

        }
    }

    @Override
    public void generateInternal() {
        int margin, edgeSize;
        RagColor color, altColor, backColor;

        // recursive draw
        color = getRandomColor();
        altColor = getRandomColorSkipColor(new RagColor[]{color});
        backColor = getRandomColorSkipColor(new RagColor[]{color, altColor});

        margin = (textureSize / 30) + AppWindow.random.nextInt(textureSize / 30);
        edgeSize = (textureSize / 120) + AppWindow.random.nextInt(textureSize / 120);

        generateBlockBackground(0, 0, textureSize, textureSize, backColor);
        generateTempleBlock(-margin, -margin, (textureSize + margin), (textureSize + margin), margin, edgeSize, color, altColor);

        // finish with the metallic-roughness
        createMetallicRoughnessMap(0.35f, 0.3f);
    }
}
