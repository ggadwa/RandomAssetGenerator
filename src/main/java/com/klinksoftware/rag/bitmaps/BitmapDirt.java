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
        float xRoundFactor, yRoundFactor, normalZFactor;
        float[] backgroundData, stoneCopyData;
        boolean hit;
        RagColor dirtColor, stoneColor, drawStoneColor, outlineColor;
        Rectangle testRect;
        ArrayList<Rectangle> rects;

        // background is always brownish
        dirtColor = new RagColor((0.4f + AppWindow.random.nextFloat(0.2f)), (0.2f + AppWindow.random.nextFloat(0.2f)), (0.0f + AppWindow.random.nextFloat(0.1f)));

        // ground
        drawRect(0, 0, textureSize, textureSize, dirtColor);

        createPerlinNoiseData(16, 16);
        drawPerlinNoiseRect(0, 0, textureSize, textureSize, 0.8f, 1.0f);
        createPerlinNoiseData(16, 16);
        drawPerlinNoiseRect(0, 0, textureSize, textureSize, 0.8f, 1.0f);
        createPerlinNoiseData(32, 32);
        drawPerlinNoiseRect(0, 0, textureSize, textureSize, 0.8f, 1.0f);

        createNormalNoiseData(2.5f, 0.5f);
        drawNormalNoiseRect(0, 0, textureSize, textureSize);
        blur(colorData, 0, 0, textureSize, textureSize, 5, true);

        // stones
        stoneColor = this.getRandomGray(0.5f, 0.8f);

        backgroundData = colorData.clone();
        stoneCopyData = colorData.clone();

        stoneCount = 3 + AppWindow.random.nextInt(8);
        maxStoneSize = textureSize / 5;

        createPerlinNoiseData(32, 32);
        createNormalNoiseData(5.0f, 0.3f);

        rects = new ArrayList<>();

        for (n = 0; n != stoneCount; n++) {
            drawStoneColor = adjustColorRandom(stoneColor, 0.7f, 1.2f);

            failCount = 0;
            lft = rgt = top = bot = 0;
            testRect = null;

            while (failCount < 10) {
                lft = AppWindow.random.nextInt(textureSize - maxStoneSize);
                rgt = lft + ((maxStoneSize / 4) + AppWindow.random.nextInt((maxStoneSize * 3) / 4));
                top = AppWindow.random.nextInt(textureSize - maxStoneSize);
                bot = top + ((maxStoneSize / 4) + AppWindow.random.nextInt((maxStoneSize * 3) / 4));

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

            edgeSize = (int) ((AppWindow.random.nextFloat() * ((float) textureSize * 0.1f)) + (AppWindow.random.nextFloat() * ((float) textureSize * 0.2f))); // new edge size as stones aren't the same
            xRoundFactor = 0.02f + (AppWindow.random.nextFloat() * 0.05f);
            yRoundFactor = 0.02f + (AppWindow.random.nextFloat() * 0.05f);
            normalZFactor = (AppWindow.random.nextFloat() * 0.2f); // different z depths

            // draw on the background
            colorData = backgroundData.clone();
            outlineColor = adjustColor(drawStoneColor, 0.5f);
            drawOval(lft, top, rgt, bot, 0.0f, 1.0f, xRoundFactor, yRoundFactor, edgeSize, 0.5f, drawStoneColor, outlineColor, normalZFactor, false, true, 0.4f, 1.2f);

            // gravity distortions to make stones unique
            gravityDistortEdges(lft, top, rgt, bot, 10, 35, 5);

            // and copy over
            blockCopy(colorData, lft, top, rgt, bot, stoneCopyData);
        }

        // push over the stones
        colorData = stoneCopyData;

        /*
            // vegetation=

        halfHigh=textureSize/2;

        for (x=0;x<textureSize;x++) {

                // vegetation color

            lineColor=adjustColorRandom(groundColor,0.7f,1.3f);

                // line half from top

            y=halfHigh+AppWindow.random.nextInt(halfHigh);
            drawRandomLine(x,0,x,(y+5),0,0,textureSize,textureSize,10,lineColor,false);
            drawLineNormal(x,0,x,(y+5),((x&0x1)==0x0)?NORMAL_BOTTOM_RIGHT_45:NORMAL_TOP_LEFT_45);

                // line half from bottom

            y=textureSize-(halfHigh+AppWindow.random.nextInt(halfHigh));
            drawRandomLine(x,(y-5),x,textureSize,0,0,textureSize,textureSize,10,lineColor,false);
            drawLineNormal(x,0,x,(y+5),((x&0x1)==0x0)?NORMAL_TOP_LEFT_45:NORMAL_BOTTOM_RIGHT_45);
        }
         */
        // finish with the metallic-roughness
        createMetallicRoughnessMap(0.5f, 0.4f);
    }

}
