package com.klinksoftware.rag.bitmap;

import com.klinksoftware.rag.bitmap.utility.BitmapInterface;
import com.klinksoftware.rag.bitmap.utility.BitmapBase;
import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

@BitmapInterface
public class BitmapMonitor extends BitmapBase {

    public BitmapMonitor() {
        super();

        textureSize = 1024;
        hasNormal = true;
        hasMetallicRoughness = true;
        hasEmissive = true;
        hasAlpha = false;
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
            if (AppWindow.random.nextFloat() > 0.2f) {
                charCount = (maxCharPerLine / 2) + AppWindow.random.nextInt(maxCharPerLine / 2);
                generateRandomCharacterLine((lft + charMargin), dy, charWid, charHigh, charPadding, charCount, charColor, emissiveCharColor);
            }
            dy += (charHigh + charPadding);
        }
    }

    private void generateScreenBars(int lft, int top, int rgt, int bot) {
        int n, barCount, margin, barHigh;
        int x, y;
        RagColor lineColor, emissiveColor;

        if (bot <= top) {
            return;
        }

        margin = textureSize / 50;

        // the graphcs
        barCount = 5 + AppWindow.random.nextInt(3);
        barHigh = ((bot - top) - (margin * 4)) / barCount;

        if (barHigh < 2) {
            barHigh = 2;
            margin = 1;
            barCount = (bot - top) / (barHigh + margin);
        }

        y = top + (margin * 2);

        for (n = 0; n != barCount; n++) {
            lineColor = getRandomColor();
            emissiveColor = adjustColor(lineColor, 0.8f);

            x = (lft + margin) + AppWindow.random.nextInt(((rgt - lft) - (margin * 4)));
            drawRect((lft + margin), y, x, (y + (barHigh - margin)), lineColor);
            drawRectEmissive((lft + margin), y, x, (y + (barHigh - margin)), emissiveColor);

            y += barHigh;
        }

        // the line
        drawLineColor((lft + margin), (top + margin), (lft + margin), (bot - margin), COLOR_WHITE);
    }

    private void generateScreenGraph(int lft, int top, int rgt, int bot) {
        int n, lineCount, margin, lineHigh, lineTopY;
        int x, y, lastX, lastY, spikeCount, spikeWid;
        RagColor lineColor, emissiveColor;

        if (bot <= top) {
            return;
        }

        margin = textureSize / 50;

        // the graphcs
        lineCount = 2 + AppWindow.random.nextInt(3);

        lineHigh = (int) ((float) (bot - top) * 0.7f);
        lineTopY = top + (((bot - top) - lineHigh) / 2);

        for (n = 0; n != lineCount; n++) {
            lineColor = getRandomColor();
            emissiveColor = adjustColor(lineColor, 0.8f);

            spikeCount = 5 + AppWindow.random.nextInt(15);
            spikeWid = ((rgt - lft) - (margin * 2)) / spikeCount;

            lastX = -1;
            lastY = -1;

            for (x = (lft + margin); x < (rgt - margin); x += spikeWid) {
                y = lineTopY + AppWindow.random.nextInt(lineHigh);

                if (lastX != -1) {
                    drawLineColorEmissive(lastX, lastY, x, y, lineColor, emissiveColor);
                }

                lastX = x;
                lastY = y;
            }
        }

        // the lines
        drawLineColor((lft + margin), (top + margin), (lft + margin), (bot - margin), COLOR_WHITE);
        drawLineColor((lft + margin), (bot - margin), (rgt - margin), (bot - margin), COLOR_WHITE);
    }

    private void generateScreenContentSinglePane(int lft, int top, int rgt, int bot) {
        switch (AppWindow.random.nextInt(3)) {
            case 0:
                generateScreenCharacterBlock(lft, top, rgt, bot);
                break;
            case 1:
                generateScreenBars(lft, top, rgt, bot);
                break;
            case 2:
                generateScreenGraph(lft, top, rgt, bot);
                break;
        }
    }

    private void generateScreenContentPanes(int lft, int top, int rgt, int bot) {
        int x, y;

        switch (AppWindow.random.nextInt(3)) {
            case 0: // two verticals
                y = (top + bot) / 2;
                generateScreenContentSinglePane(lft, top, rgt, y);
                generateScreenContentSinglePane(lft, y, rgt, bot);
                break;
            case 1: // three verticals
                y = (bot - top) / 3;
                generateScreenContentSinglePane(lft, top, rgt, (top + y));
                generateScreenContentSinglePane(lft, (top + y), rgt, (bot - y));
                generateScreenContentSinglePane(lft, (bot - y), rgt, bot);
                break;
            case 2: // four quarters
                x = (lft + rgt) / 2;
                y = (top + bot) / 2;
                generateScreenContentSinglePane(lft, top, x, y);
                generateScreenContentSinglePane(x, top, rgt, y);
                generateScreenContentSinglePane(lft, y, x, bot);
                generateScreenContentSinglePane(x, y, rgt, bot);
                break;
        }
    }

    public void generateScreen(int lft, int top, int rgt, int bot) {
        int panelEdgeSize, panelInsideEdgeSize, contentEdgeSize;
        int high;
        RagColor edgeColor, screenColor;

        edgeColor = getRandomMetalColor();
        screenColor = getRandomGrayColor(0.15f, 0.3f);

        // possible bottom
        if (AppWindow.random.nextBoolean()) {
            high = (int) ((float) (bot - top) * 0.05f);
            high = high + AppWindow.random.nextInt(high);
            drawRect(lft, (bot - high), rgt, bot, edgeColor);
            drawMetalShine(lft, (bot - high), rgt, bot, edgeColor);
            bot -= high;
        }

        // possible top
        if (AppWindow.random.nextBoolean()) {
            high = (int) ((float) (bot - top) * 0.05f);
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
        generateScreenContentPanes((lft + contentEdgeSize), (top + contentEdgeSize), (rgt - contentEdgeSize), (bot - contentEdgeSize));

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
