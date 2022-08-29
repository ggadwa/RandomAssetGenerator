package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

@BitmapInterface
public class BitmapMonitor extends BitmapBase {

    public BitmapMonitor(int textureSize) {
        super(textureSize);

        textureSize = 1024;
        hasNormal = true;
        hasMetallicRoughness = true;
        hasEmissive = true;
        hasAlpha = false;
    }

    protected void generateScreenCharacterLine(int dx, int dy, int charWid, int charHigh, int charPadding, int charCount, RagColor charColor, RagColor emissiveCharColor) {
        int x;

        for (x = 0; x < charCount; x++) {

            switch (AppWindow.random.nextInt(5)) {
                case 0:
                    drawRect(dx, dy, (dx + charWid), (dy + charHigh), charColor);
                    if (emissiveCharColor != null) {
                        drawRectEmissive(dx, dy, (dx + charWid), (dy + charHigh), emissiveCharColor);
                    }
                    break;
                case 1:
                    drawRect((dx + (charWid / 2)), dy, (dx + charWid), (dy + (charHigh - (charHigh / 3))), charColor);
                    if (emissiveCharColor != null) {
                        drawRectEmissive((dx + (charWid / 2)), dy, (dx + charWid), (dy + (charHigh - (charHigh / 3))), emissiveCharColor);
                    }
                    break;
                case 2:
                    drawRect(dx, (dy + (charHigh / 2)), (dx + charWid), (dy + charHigh), charColor);
                    if (emissiveCharColor != null) {
                        drawRectEmissive(dx, (dy + (charHigh / 2)), (dx + charWid), (dy + charHigh), emissiveCharColor);
                    }
                    break;
                case 3:
                    drawRect(dx, dy, (dx + charWid), (dy + (charHigh / 3)), charColor);
                    if (emissiveCharColor != null) {
                        drawRectEmissive(dx, dy, (dx + charWid), (dy + (charHigh / 3)), emissiveCharColor);
                    }
                    break;
            }

            dx += (charWid + charPadding);
        }
    }

    private void generateScreenCharacterBlock(int lft, int top, int rgt, int bot) {
        int y, dy, lineCount, charCount;
        int charWid, charHigh, charPadding, charMargin, maxCharPerLine;
        RagColor charColor, emissiveCharColor;

        charColor = new RagColor(0.2f, 0.6f, 0.2f);
        emissiveCharColor = adjustColor(charColor, 0.8f);

        charWid = textureSize / 100;
        charHigh = (int) ((float) charWid * 1.6f);
        charPadding = textureSize / 250;
        charMargin = textureSize / 50;

        maxCharPerLine = ((rgt - lft) - (charMargin * 2)) / (charWid + charPadding);
        lineCount = ((bot - top) - (charMargin * 2)) / (charHigh + charPadding);

        // char lines
        dy = top + charMargin;

        for (y = 0; y < lineCount; y++) {
            charCount = (maxCharPerLine / 2) + AppWindow.random.nextInt(maxCharPerLine / 2);
            generateScreenCharacterLine((lft + charMargin), dy, charWid, charHigh, charPadding, charCount, charColor, emissiveCharColor);
            dy += (charHigh + charPadding);
        }
    }

    private void generateScreenGraph(int lft, int top, int rgt, int bot) {

    }

    public void generateScreen(int lft, int top, int rgt, int bot) {
        int panelEdgeSize, panelInsideEdgeSize, contentEdgeSize;
        int high, y;
        RagColor edgeColor, screenColor;

        edgeColor = getRandomMetalColor();
        screenColor = getRandomGrayColor(0.15f, 0.3f);

        // possible bottom
        if (AppWindow.random.nextBoolean()) {
            high = (int) ((float) (bot - top) * 0.1f);
            high = high + AppWindow.random.nextInt(high);
            drawRect(lft, (bot - high), rgt, bot, edgeColor);
            drawMetalShine(lft, (bot - high), rgt, bot, edgeColor);
            bot -= high;
        }

        // possible top
        if (AppWindow.random.nextBoolean()) {
            high = (int) ((float) (bot - top) * 0.1f);
            high = high + AppWindow.random.nextInt(high);
            drawRect(lft, top, rgt, (top + high), edgeColor);
            drawMetalShine(lft, top, rgt, (top + high), edgeColor);
            top += high;
        }

        // panel itself
        panelEdgeSize = (textureSize / 150) + AppWindow.random.nextInt(textureSize / 150);
        panelInsideEdgeSize = panelEdgeSize + ((textureSize / 50) + AppWindow.random.nextInt(textureSize / 100));
        contentEdgeSize = panelInsideEdgeSize + panelEdgeSize;

        // back internal outline
        drawRect(lft, top, rgt, bot, edgeColor);
        drawRect((lft + panelEdgeSize), (top + panelEdgeSize), (rgt - panelEdgeSize), (bot - panelEdgeSize), COLOR_BLACK);

        // inside monitor border
        drawOval((lft + panelInsideEdgeSize), (top + panelInsideEdgeSize), (rgt - panelInsideEdgeSize), (bot - panelInsideEdgeSize), 0.0f, 1.0f, 0.35f, 0.35f, 0, 0.0f, screenColor, 0.5f, false, false, 1.0f, 0.0f);

        // monitor contents
        //if (AppWindow.random.nextBoolean()) {
        generateScreenCharacterBlock((lft + contentEdgeSize), (top + contentEdgeSize), (rgt - contentEdgeSize), (bot - contentEdgeSize));
        //} else {
        //    generateScreenGraph((lft + contentEdgeSize), (top + contentEdgeSize), (rgt - contentEdgeSize), (bot - contentEdgeSize));
        //}

        // finally the edge
        draw3DDarkenFrameRect(lft, top, rgt, bot, panelEdgeSize, (0.9f + AppWindow.random.nextFloat(0.1f)), true);
    }

    @Override
    public void generateInternal() {
        // create the screen
        generateScreen(0, 0, textureSize, textureSize);

        // set the emissive
        emissiveFactor = new RagPoint(1.0f, 1.0f, 1.0f);

        // finish with the metallic-roughness
        createMetallicRoughnessMap(0.75f, 0.4f);
    }
}
