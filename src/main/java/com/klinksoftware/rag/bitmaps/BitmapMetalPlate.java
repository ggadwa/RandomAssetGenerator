package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

@BitmapInterface
public class BitmapMetalPlate extends BitmapBase {

    public BitmapMetalPlate() {
        super();

        hasNormal = true;
        hasMetallicRoughness = true;
        hasEmissive = false;
        hasAlpha = false;
    }

    //
    // metal pieces
    //
    private void generateMetalScrews(int lft, int top, int rgt, int bot, RagColor screwColor, int screwSize) {
        int mx, my, edgeSize;
        RagColor outlineColor;

        edgeSize = screwSize / 2;
        outlineColor = adjustColor(screwColor, 0.5f);

        // corners
        if (AppWindow.random.nextFloat() < 0.33f) {
            drawScrew(lft, top, screwColor, outlineColor, screwSize, edgeSize);
            drawScrew((rgt - screwSize), top, screwColor, outlineColor, screwSize, edgeSize);
            drawScrew((rgt - screwSize), (bot - screwSize), screwColor, outlineColor, screwSize, edgeSize);
            drawScrew(lft, (bot - screwSize), screwColor, outlineColor, screwSize, edgeSize);
        }

        // middles
        if (AppWindow.random.nextFloat() < 0.33) {
            mx = ((lft + rgt) / 2) - (screwSize / 2);
            my = ((top + bot) / 2) - (screwSize / 2);
            drawScrew(mx, top, screwColor, outlineColor, screwSize, edgeSize);
            drawScrew((rgt - screwSize), my, screwColor, outlineColor, screwSize, edgeSize);
            drawScrew(mx, (bot - screwSize), screwColor, outlineColor, screwSize, edgeSize);
            drawScrew(lft, my, screwColor, outlineColor, screwSize, edgeSize);
        }
    }

    public void generateMetalPanel(int lft, int top, int rgt, int bot, RagColor metalColor, RagColor altMetalColor, int edgeSize, int screwSize, boolean isBox) {
        int lft2, rgt2, top2, bot2, sz;
        RagColor color, screwColor, frameColor;

        // colors
        if (AppWindow.random.nextBoolean()) {
            color = metalColor;
            screwColor = altMetalColor;
        } else {
            color = altMetalColor;
            screwColor = metalColor;
        }

        frameColor = adjustColorRandom(color, 0.85f, 0.95f);

        // the plate
        createPerlinNoiseData(16, 16);
        drawRect(lft, top, rgt, bot, color);
        drawPerlinNoiseRect(lft, top, rgt, bot, 0.8f, 1.0f);

        drawMetalShine(lft, top, rgt, bot, color);
        draw3DFrameRect(lft, top, rgt, bot, edgeSize, frameColor, true);

        sz = ((edgeSize + screwSize) * 2) + (AppWindow.random.nextInt(edgeSize * 3));
        lft2 = lft + sz;
        rgt2 = rgt - sz;
        top2 = top + sz;
        bot2 = bot - sz;
        frameColor = adjustColorRandom(color, 0.75f, 0.85f);
        draw3DFrameRect(lft2, top2, rgt2, bot2, edgeSize, frameColor, false);
        drawMetalShine((lft2 + edgeSize), (top2 + edgeSize), (rgt2 - edgeSize), (bot2 - edgeSize), color);

        sz = edgeSize + screwSize;
        generateMetalScrews((lft + sz), (top + sz), (rgt - sz), (bot - sz), screwColor, screwSize);
    }

    //
    // metal bitmaps
    //
    @Override
    public void generateInternal() {
        int mx, my;
        int edgeSize, screwSize;
        RagColor metalColor, altMetalColor;

        metalColor = getRandomColor();
        altMetalColor = getRandomColor();
        edgeSize = 4 + AppWindow.random.nextInt(5);
        screwSize = 5 + AppWindow.random.nextInt(25);

        // either single, dual, or 4 panel
        mx = textureSize / 2;
        my = textureSize / 2;

        switch (AppWindow.random.nextInt(3)) {
            case 0:
                generateMetalPanel(0, 0, textureSize, textureSize, metalColor, altMetalColor, edgeSize, screwSize, false);
                break;
            case 1:
                generateMetalPanel(0, 0, mx, textureSize, metalColor, altMetalColor, edgeSize, screwSize, false);
                generateMetalPanel(mx, 0, textureSize, textureSize, metalColor, altMetalColor, edgeSize, screwSize, false);
                break;
            case 2:
                generateMetalPanel(0, 0, mx, my, metalColor, altMetalColor, edgeSize, screwSize, false);
                generateMetalPanel(mx, 0, textureSize, my, metalColor, altMetalColor, edgeSize, screwSize, false);
                generateMetalPanel(0, my, mx, textureSize, metalColor, altMetalColor, edgeSize, screwSize, false);
                generateMetalPanel(mx, my, textureSize, textureSize, metalColor, altMetalColor, edgeSize, screwSize, false);
                break;
        }

        // finish with the metallic-roughness
        createMetallicRoughnessMap(0.5f, 0.6f);
    }
}
