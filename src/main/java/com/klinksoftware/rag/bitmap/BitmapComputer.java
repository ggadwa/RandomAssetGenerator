package com.klinksoftware.rag.bitmap;

import com.klinksoftware.rag.bitmap.utility.BitmapInterface;
import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

@BitmapInterface
public class BitmapComputer extends BitmapMonitor {
    public final static RagColor[] LED_COLORS = {
        new RagColor(0.0f, 1.0f, 0.0f),
        new RagColor(1.0f, 1.0f, 0.0f),
        new RagColor(1.0f, 0.0f, 0.0f)
    };

    public BitmapComputer() {
        super();

        hasNormal=true;
        hasMetallicRoughness=true;
        hasEmissive=true;
        hasAlpha=false;
    }

        //
        // components
        //

    private void generateComputerComponentWires(int lft, int top, int rgt, int bot, int edgeSize) {
        int n, nLine, x, y, lineVar;
        boolean horz;
        RagColor recessColor, lineColor;

        recessColor = getRandomGrayColor(0.15f, 0.25f);

            // wires background

        lft+=edgeSize;
        rgt-=edgeSize;
        top+=edgeSize;
        bot-=edgeSize;

        drawRect(lft,top,rgt,bot,COLOR_BLACK);
        draw3DFrameRect(lft,top,rgt,bot,2,recessColor,false);

        lft+=2;
        rgt-=2;
        top+=2;
        bot-=2;

            // determine if horz or vertical

        horz=((rgt-lft)>(bot-top));

        if (horz) {
            nLine=(int)((float)(bot-top)*0.7f);
            if (nLine<=0) return;

            lineVar=(int)((float)(rgt-lft)*0.035f);
            if (lineVar<4) lineVar=4;

            for (n=0;n!=nLine;n++) {
                y=top+AppWindow.random.nextInt(bot-top);

                lineColor=getRandomColor();
                drawRandomLine(lft,y,rgt,y,lft,top,rgt,bot,lineVar,lineColor,true);
            }
        }
        else {
            nLine=(int)((float)(rgt-lft)*0.7f);
            if (nLine<=0) return;

            lineVar=(int)((float)(bot-top)*0.035f);
            if (lineVar<4) lineVar=4;

            for (n=0;n!=nLine;n++) {
                x=lft+AppWindow.random.nextInt(rgt-lft);

                lineColor=getRandomColor();
                drawRandomLine(x,top,x,bot,lft,top,rgt,bot,lineVar,lineColor,true);
            }
        }
    }

    private void generateComputerComponentShutter(int lft,int top,int rgt,int bot,int edgeSize)
    {
        int         sz,shutterCount;
        RagColor    shutterColor,shutterEdgeColor;

        lft+=edgeSize;
        rgt-=edgeSize;
        top+=edgeSize;
        bot-=edgeSize;

        shutterColor=getRandomColor();
        shutterEdgeColor=adjustColor(shutterColor,0.9f);

        sz = (int) ((float) Math.max((rgt - lft), (bot - top)) * 0.05f);
        shutterCount=sz+AppWindow.random.nextInt(sz);

        drawMetalShine(lft,top,rgt,bot,shutterColor);

        if ((rgt-lft)>(bot-top)) {
            drawNormalWaveHorizontal(lft,top,rgt,bot,shutterColor,shutterEdgeColor,shutterCount);
        }
        else {
            drawNormalWaveVertical(lft,top,rgt,bot,shutterColor,shutterEdgeColor,shutterCount);
        }
    }

    private void generateComputerComponentLights(int lft, int top, int rgt, int bot, int edgeSize) {
        int x, y, xCount, yCount, xMargin, yMargin, dx, dy, sz;
        RagColor color;

        lft+=edgeSize;
        rgt-=edgeSize;
        top+=edgeSize;
        bot-=edgeSize;

        sz = (textureSize / 50) + AppWindow.random.nextInt(textureSize / 50);

        xCount = (rgt - lft) / sz;
        yCount = (bot - top) / sz;

        if (xCount<=0) xCount=1;
        if (yCount<=0) yCount=1;

        xMargin=(((rgt-lft)-(xCount*sz))/2)+1;
        yMargin=(((bot-top)-(yCount*sz))/2)+1;

        for (y=0;y!=yCount;y++) {
            dy=(top+yMargin)+(y*sz);

            for (x=0;x!=xCount;x++) {
                dx=(lft+xMargin)+(x*sz);

                    // the light

                color=getRandomColor();
                if (AppWindow.random.nextBoolean()) color=adjustColor(color,0.8f);
                drawOval((dx + 1), (dy + 1), (dx + (sz - 1)), (dy + (sz - 1)), 0.0f, 1.0f, 0.0f, 0.0f, sz, 0.8f, color, 0.5f, false, false, 1.0f, 0.0f);

                    // the possible emissive

                if (AppWindow.random.nextBoolean()) {
                    drawSimpleOval(emissiveData, (dx + 1), (dy + 1), (dx + (sz - 1)), (dy + (sz - 1)), adjustColor(color, 0.7f));
                }
            }
        }
    }

    private void generateComputerComponentButtons(int lft, int top, int rgt, int bot, int edgeSize) {
        int x, y, xCount, yCount, xMargin, yMargin, dx, dy, sz;
        RagColor color, outlineColor;

        lft+=edgeSize;
        rgt-=edgeSize;
        top+=edgeSize;
        bot-=edgeSize;

        sz = (textureSize / 50) + AppWindow.random.nextInt(textureSize / 20);

        xCount=(rgt-lft)/sz;
        yCount=(bot-top)/sz;

        if (xCount<=0) xCount=1;
        if (yCount<=0) yCount=1;

        xMargin=(((rgt-lft)-(xCount*sz))/2);
        yMargin=(((bot-top)-(yCount*sz))/2);

        outlineColor = getRandomGrayColor(0.1f, 0.3f);

        for (y=0;y!=yCount;y++) {
            dy=(top+yMargin)+(y*sz);

            for (x=0;x!=xCount;x++) {
                dx=(lft+xMargin)+(x*sz);

                    // the button

                color=getRandomColor();
                drawRect(dx,dy,(dx+sz),(dy+sz),color);
                draw3DFrameRect(dx,dy,(dx+sz),(dy+sz),2,outlineColor,true);

                    // the possible emissive

                if (AppWindow.random.nextBoolean()) {
                    drawRectEmissive((dx + 2), (dy + 2), (dx + (sz - 4)), (dy + (sz - 4)), color);
                }
            }
        }
    }

    private void generateComputerComponentDials(int lft, int top, int rgt, int bot) {
        int x, y, mx, my, xCount, yCount, xMargin, yMargin;
        int margin, dx, dy, dx2, dy2, sz, charSize, charMargin;
        RagColor dialColor, outlineColor;
        RagPoint pnt;

        margin = (textureSize / 100) + AppWindow.random.nextInt(textureSize / 100);

        lft += margin;
        rgt -= margin;
        top += margin;
        bot -= margin;

        if ((rgt - lft) > (bot - top)) {
            sz = (bot - top) - (margin * 2);
            xCount = (rgt - lft) / (sz + margin);
            yCount = 1;
        } else {
            sz = (rgt - lft) - (margin * 2);
            xCount = 1;
            yCount = (bot - top) / (sz + margin);
        }

        if (xCount <= 0) {
            xCount = 1;
        }
        if (yCount <= 0) {
            yCount = 1;
        }

        xMargin = ((rgt - lft) - ((xCount * sz) + ((xCount - 1) * margin))) / 2;
        yMargin = ((bot - top) - ((yCount * sz) + ((yCount - 1) * margin))) / 2;

        for (y = 0; y != yCount; y++) {
            dy = (top + yMargin) + (y * (sz + margin));
            dy2 = dy + sz;

            for (x = 0; x != xCount; x++) {
                dx = (lft + xMargin) + (x * (sz + margin));
                dx2 = dx + sz;

                dialColor = getRandomColor();
                outlineColor = adjustColor(dialColor, 0.5f);

                drawOval(dx, dy, dx2, dy2, 0.0f, 1.0f, 0.0f, 0.0f, 2, 0.8f, outlineColor, 0.5f, false, false, 1.0f, 0.0f);
                drawFrameOval(dx, dy, dx2, dy2, 0.0f, 0.0f, COLOR_BLACK);
                drawOval((dx + 2), (dy + 2), (dx2 - 2), (dy2 - 2), 0.75f, 1.25f, 0.0f, 0.0f, 0, 0.8f, dialColor, 0.5f, false, false, 1.0f, 0.0f);
                drawFrameOval((dx + 2), (dy + 2), (dx2 - 2), (dy2 - 2), 0.0f, 0.0f, COLOR_BLACK);

                if (AppWindow.random.nextBoolean()) {
                    drawRect(dx, ((dy + dy2) / 2), dx2, dy2, outlineColor);
                    draw3DFrameRect(dx, ((dy + dy2) / 2), dx2, dy2, 2, outlineColor, true);
                } else {
                    drawOval((dx + 2), (dy + 2), (dx2 - 2), (dy2 - 2), 0.25f, 0.75f, 0.0f, 0.0f, 0, 0.8f, outlineColor, 0.5f, false, false, 1.0f, 0.0f);
                    drawFrameOval((dx + 2), (dy + 2), (dx2 - 2), (dy2 - 2), 0.0f, 0.0f, COLOR_BLACK);
                }

                mx = dx + (sz / 2);
                my = dy + (sz / 2);

                drawOval((mx - margin), (my - margin), (mx + margin), (my + margin), 0.0f, 1.0f, 0.0f, 0.0f, 0, 0.8f, outlineColor, 0.5f, false, false, 1.0f, 0.0f);
                drawFrameOval((mx - margin), (my - margin), (mx + margin), (my + margin), 0.0f, 0.0f, COLOR_BLACK);

                pnt = new RagPoint(0, 0, (dy - my));
                pnt.rotateY(AppWindow.random.nextFloat(180.0f) - 90.0f);

                drawLineColor(mx, my, (mx + (int) pnt.x), (my + (int) pnt.z), outlineColor);
                drawLineColor((mx + 1), my, ((mx + 1) + (int) pnt.x), (my + (int) pnt.z), outlineColor);
                drawLineNormal(mx, my, (mx + (int) pnt.x), (my + (int) pnt.z), NORMAL_LEFT_45);
                drawLineNormal((mx + 1), my, ((mx + 1) + (int) pnt.x), (my + (int) pnt.z), NORMAL_RIGHT_45);

                charSize = (int) ((float) sz * 0.1f);
                charMargin = (int) ((float) charSize * 0.1f);
                generateRandomCharacterLine((mx - ((charSize + charMargin) * 3)), (my + (charSize + (charMargin * 2))), charSize, charSize, charMargin, 6, COLOR_BLACK, null);
            }
        }
    }

    private void generateComputerComponentDrives(int lft, int top, int rgt, int bot, int edgeSize) {
        int x, y, xCount, yCount, dx, dy, bx, by;
        int wid, high, ledWid, ledHigh, xMargin, yMargin;
        RagColor color, outlineColor, ledColor;

        lft+=edgeSize;
        rgt-=edgeSize;
        top+=edgeSize;
        bot-=edgeSize;

        // the random color (always dark)
        color = getRandomGrayColor(0.1f, 0.3f);
        outlineColor = adjustColor(color, 0.8f);

        // drives are 1 across on the shortest side
        if ((rgt - lft) > (bot - top)) {
            yCount = 1;
            xCount = 4 + AppWindow.random.nextInt(4);
            ledWid = (int) ((float) (bot - top) * 0.1f);
        } else {
            xCount = 1;
            yCount = 4 + AppWindow.random.nextInt(4);
            ledWid = (int) ((float) (rgt - lft) * 0.1f);
        }

        // led sizes
        if (ledWid < (textureSize / 100)) {
            ledWid = (textureSize / 100);
        }
        ledHigh = ledWid / 2;

        wid=(rgt-lft)/xCount;
        high=(bot-top)/yCount;

        xMargin=((rgt-lft)-(xCount*wid))/2;
        yMargin=((bot-top)-(yCount*high))/2;

        for (y=0;y!=yCount;y++) {
            dy=(top+yMargin)+(y*high);

            for (x=0;x!=xCount;x++) {
                dx=(lft+xMargin)+(x*wid);

                // the drive
                drawRect(dx,dy,(dx+wid),(dy+high),color);
                draw3DFrameRect(dx, dy, (dx + wid), (dy + high), edgeSize, outlineColor, true);

                // the emissive indicator
                ledColor=LED_COLORS[AppWindow.random.nextInt(3)];

                bx = (dx + wid) - ((ledWid + ledHigh) + edgeSize);
                by = (dy + high) - ((ledHigh + ledHigh) + edgeSize);
                drawRect(bx,by,(bx+ledWid),(by+ledHigh),ledColor);
                drawRectEmissive(bx,by,(bx+ledWid),(by+ledHigh),ledColor);
            }
        }
    }

    private void generateComputerComponentScreen(int lft, int top, int rgt, int bot) {
        generateScreen(lft, top, rgt, bot); // from bitmap monitor
    }

    private void generateComputerComponentAccess(int lft, int top, int rgt, int bot, int edgeSize, RagColor altPanelColor) {
        int charWid, charHigh, charPadding, charMargin;

        charWid = textureSize / 100;
        charHigh = (int) ((float) charWid * 1.6f);
        charPadding = textureSize / 250;
        charMargin = ((textureSize / 100) + edgeSize);

        generateRandomCharacterLine((lft + charMargin), (top + charMargin), charWid, charHigh, charPadding, (3 + AppWindow.random.nextInt(3)), COLOR_BLACK, null);
    }

    protected void generateComputerComponents(RagColor panelColor, int edgeSize) {
        int mx, my, sz, lx, ty, rx, by, rndTry;
        int lightCount, buttonCount, screenCount;
        int minPanelSize, extraPanelSize, skipPanelSize;
        boolean hadWires, hadShutter, hadDisks, hadDials, hadAccess, hadBlank, rndSuccess;
        RagColor altPanelColor;

        // inside components
        // these are stacks of vertical or horizontal chunks
        mx = edgeSize;
        my = edgeSize;

        hadWires=false;
        hadShutter = false;
        hadDisks = false;
        hadDials = false;
        hadAccess = false;
        hadBlank=false;
        lightCount=0;
        buttonCount = 0;
        screenCount = 0;

        minPanelSize = (int) ((float) textureSize * 0.2f);
        extraPanelSize=(int)((float)textureSize*0.04f);
        skipPanelSize=(int)((float)textureSize*0.05f);

        while (true) {

            lx=mx;
            ty=my;
            sz=minPanelSize+AppWindow.random.nextInt(extraPanelSize);

            // vertical stack
            if (AppWindow.random.nextBoolean()) {
                rx=lx+sz;
                if (rx >= (textureSize - (skipPanelSize + edgeSize))) {
                    rx = textureSize - edgeSize;
                }
                by = textureSize - edgeSize;

                mx=rx+edgeSize;
            }

            // horizontal stack
            else {
                by=ty+sz;
                if (by >= (textureSize - (skipPanelSize + edgeSize))) {
                    by = textureSize - edgeSize;
                }
                rx = textureSize - edgeSize;

                my=by+edgeSize;
            }

            // box around components, can
            // be randonly in or out
            altPanelColor = adjustColorRandom(panelColor, 0.8f, 1.0f);
            drawRect(lx, ty, rx, by, altPanelColor);
            draw3DFrameRect(lx, ty, rx, by, edgeSize, altPanelColor, true);

            // draw the components
            // we only allow one blank, wires, or shutter
            rndTry=0;

            while (rndTry<25) {
                rndSuccess = false;

                switch (AppWindow.random.nextInt(8)) {
                    case 0:
                        // no wires on control panels or horizontal plates
                        if ((hadWires) || ((rx - lx) > (by - ty))) {
                            break; // no wires on panels
                        }

                        hadWires = true;
                        generateComputerComponentWires(lx,ty,rx,by,edgeSize);
                        rndSuccess=true;
                        break;
                    case 1:
                        if (hadShutter) {
                            break;
                        }
                        hadShutter=true;
                        generateComputerComponentShutter(lx,ty,rx,by,edgeSize);
                        rndSuccess=true;
                        break;
                    case 2:
                        if (lightCount > 1) {
                            break;
                        }
                        lightCount++;
                        generateComputerComponentLights(lx, ty, rx, by, edgeSize);
                        rndSuccess=true;
                        break;
                    case 3:
                        if (buttonCount>2) break;
                        buttonCount++;
                        generateComputerComponentButtons(lx, ty, rx, by, edgeSize);
                        rndSuccess=true;
                        break;
                    case 4:
                        if (hadDisks) {
                            break;
                        }
                        hadDisks = true;
                        generateComputerComponentDrives(lx, ty, rx, by, edgeSize);
                        rndSuccess=true;
                        break;
                    case 5:
                        // screens only horizontal
                        if ((screenCount > 2) || ((rx - lx) < (by - ty))) {
                            break;
                        }
                        screenCount++;
                        generateComputerComponentScreen(lx, ty, rx, by);
                        rndSuccess=true;
                        break;
                    case 6:
                        if (hadDials) {
                            break;
                        }
                        hadDials = true;
                        generateComputerComponentDials(lx, ty, rx, by);
                        rndSuccess = true;
                        break;
                    case 7:
                        if (hadAccess) {
                            break;
                        }
                        hadAccess = true;
                        generateComputerComponentAccess(lx, ty, rx, by, edgeSize, altPanelColor);
                        rndSuccess = true;
                        break;

                    default:
                        if (hadBlank) {
                            break;
                        }
                        hadBlank = true;
                        rndSuccess=true;
                        break;
                }

                if (rndSuccess) break;

                rndTry++;
            }

                // no more panels

            if ((mx >= (textureSize - edgeSize)) || (my >= (textureSize - edgeSize))) {
                break;
            }
        }
    }

        //
        // computer bitmaps
        //

    @Override
    public void generateInternal() {
        int panelEdgeSize;
        RagColor panelColor;

        panelColor = getRandomColor();
        panelEdgeSize = (textureSize / 200) + AppWindow.random.nextInt(textureSize / 150);

        // draw the computer
        drawRect(0, 0, textureSize, textureSize, panelColor);
        generateComputerComponents(panelColor, panelEdgeSize);

        // set the emissive
        emissiveFactor=new RagPoint(1.0f,1.0f,1.0f);

        // finish with the metallic-roughness
        createMetallicRoughnessMap(0.75f,0.4f);
    }
}
