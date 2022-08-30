package com.klinksoftware.rag.bitmaps;

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

    @Override
    public void generateInternal() {
        int n, k, stainSize, xSize, ySize;
        int lft, rgt, top, bot, stainCount, markCount;
        RagColor concreteColor, jointColor, altJointColor, crackColor;

        concreteColor=getRandomColor();

            // the concrete background

        drawRect(0,0,textureSize,textureSize,concreteColor);

        createPerlinNoiseData(16,16);
        drawPerlinNoiseRect(0,0,textureSize,textureSize,0.6f,1.0f);

        createNormalNoiseData(3.0f,0.4f);
        drawNormalNoiseRect(0,0,textureSize,textureSize);

            // stains

        stainCount=AppWindow.random.nextInt(5);
        stainSize=(int)((float)textureSize*0.1f);

        for (n=0;n!=stainCount;n++) {
            lft=AppWindow.random.nextInt(textureSize);
            xSize=stainSize+AppWindow.random.nextInt(stainSize);

            top=AppWindow.random.nextInt(textureSize);
            ySize=stainSize+AppWindow.random.nextInt(stainSize);

            markCount=2+AppWindow.random.nextInt(4);

            for (k=0;k!=markCount;k++) {
                rgt=lft+xSize;
                if (rgt>=textureSize) rgt=textureSize-1;
                bot=top+ySize;
                if (bot>=textureSize) bot=textureSize-1;

                drawOvalStain(lft, top, rgt, bot, (0.01f + AppWindow.random.nextFloat(0.01f)), (0.15f + AppWindow.random.nextFloat(0.05f)), (0.7f + AppWindow.random.nextFloat(0.2f)));

                lft+=(AppWindow.random.nextBoolean())?(-(xSize/3)):(xSize/3);
                top+=(AppWindow.random.nextBoolean())?(-(ySize/3)):(ySize/3);
                xSize=(int)((float)xSize*0.8f);
                ySize=(int)((float)ySize*0.8f);
            }
        }

        blur(colorData, 0, 0, textureSize, textureSize, (1 + AppWindow.random.nextInt(textureSize / 125)), false);

        // concrete expansion joints
        jointColor = adjustColorRandom(concreteColor, 0.5f, 0.6f);
        altJointColor = adjustColor(jointColor, 0.9f);

        crackColor = adjustColorRandom(concreteColor, 0.4f, 0.5f);

        switch (AppWindow.random.nextInt(3)) {

            // long cuts
            case 0:
                drawLineColor(1, 0, 1, textureSize, jointColor);
                drawLineColor(0, 0, 0, textureSize, altJointColor);
                drawLineColor(2, 0, 2, textureSize, altJointColor);
                drawLineNormal(1, 0, 1, textureSize, NORMAL_CLEAR);
                drawLineNormal(0, 0, 0, textureSize, NORMAL_RIGHT_45);
                drawLineNormal(2, 0, 2, textureSize, NORMAL_LEFT_45);

                drawCracks(2, 2, (textureSize - 4), (textureSize - 4), crackColor);
                break;

            // big square
            case 1:
                drawLineColor(1, 0, 1, textureSize, jointColor);
                drawLineColor(0, 0, 0, textureSize, altJointColor);
                drawLineColor(2, 0, 2, textureSize, altJointColor);
                drawLineNormal(1, 0, 1, textureSize, NORMAL_CLEAR);
                drawLineNormal(0, 0, 0, textureSize, NORMAL_RIGHT_45);
                drawLineNormal(2, 0, 2, textureSize, NORMAL_LEFT_45);

                drawLineColor(0, 1, textureSize, 1, jointColor);
                drawLineColor(0, 0, textureSize, 0, altJointColor);
                drawLineColor(0, 2, textureSize, 2, altJointColor);
                drawLineNormal(0, 1, textureSize, 1, NORMAL_CLEAR);
                drawLineNormal(0, 0, textureSize, 0, NORMAL_RIGHT_45);
                drawLineNormal(0, 2, textureSize, 2, NORMAL_LEFT_45);

                drawCracks(2, 2, (textureSize - 4), (textureSize - 4), crackColor);
                break;

            // small square
            case 2:
                k = textureSize / 2;

                drawLineColor(1, 0, 1, textureSize, jointColor);
                drawLineColor(0, 0, 0, textureSize, altJointColor);
                drawLineColor(2, 0, 2, textureSize, altJointColor);
                drawLineNormal(1, 0, 1, textureSize, NORMAL_CLEAR);
                drawLineNormal(0, 0, 0, textureSize, NORMAL_RIGHT_45);
                drawLineNormal(2, 0, 2, textureSize, NORMAL_LEFT_45);

                drawLineColor((k + 1), 0, (k + 1), textureSize, jointColor);
                drawLineColor(k, 0, k, textureSize, altJointColor);
                drawLineColor((k + 2), 0, (k + 2), textureSize, altJointColor);
                drawLineNormal((k + 1), 0, (k + 1), textureSize, NORMAL_CLEAR);
                drawLineNormal(k, 0, k, textureSize, NORMAL_RIGHT_45);
                drawLineNormal((k + 2), 0, (k + 2), textureSize, NORMAL_LEFT_45);

                drawLineColor(0, 1, textureSize, 1, jointColor);
                drawLineColor(0, 0, textureSize, 0, altJointColor);
                drawLineColor(0, 2, textureSize, 2, altJointColor);
                drawLineNormal(0, 1, textureSize, 1, NORMAL_CLEAR);
                drawLineNormal(0, 0, textureSize, 0, NORMAL_RIGHT_45);
                drawLineNormal(0, 2, textureSize, 2, NORMAL_LEFT_45);

                drawLineColor(0, (k + 1), textureSize, (k + 1), jointColor);
                drawLineColor(0, (k + 0), textureSize, (k + 0), altJointColor);
                drawLineColor(0, (k + 2), textureSize, (k + 2), altJointColor);
                drawLineNormal(0, (k + 1), textureSize, (k + 1), NORMAL_CLEAR);
                drawLineNormal(0, (k + 0), textureSize, (k + 0), NORMAL_RIGHT_45);
                drawLineNormal(0, (k + 2), textureSize, (k + 2), NORMAL_LEFT_45);

                drawCracks(2, 2, (k - 2), (k - 2), crackColor);
                drawCracks((k + 2), 2, (textureSize - 4), (k - 2), crackColor);
                drawCracks(2, (k + 2), (k - 2), (textureSize - 4), crackColor);
                drawCracks((k + 2), (k + 2), (textureSize - 4), (textureSize - 4), crackColor);
                break;

        }

        // finish with the metallic-roughness
        createMetallicRoughnessMap(0.35f,0.3f);
    }
}
