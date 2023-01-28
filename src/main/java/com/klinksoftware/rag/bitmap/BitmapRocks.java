package com.klinksoftware.rag.bitmap;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.bitmap.utility.BitmapInterface;
import com.klinksoftware.rag.bitmap.utility.BitmapBase;
import com.klinksoftware.rag.utility.RagColor;
import com.klinksoftware.rag.utility.RagPoint;
import java.util.ArrayList;

@BitmapInterface
public class BitmapRocks extends BitmapBase {

    public BitmapRocks() {
        super();

        hasNormal = true;
        hasMetallicRoughness = true;
        hasEmissive = false;
        hasAlpha = false;
    }

    private class GridPoint {

        public int x, y;

        public GridPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public ArrayList<GridPoint> createRandomGridVertexes(int count, float randomPercentage) {
        int x, y, xOff, yOff;
        int gridSize, randomSize, randomOffset;
        ArrayList<GridPoint> pnts;
        GridPoint fromPnt, toPnt;

        pnts = new ArrayList<>();
        gridSize = textureSize / count;
        randomSize = (int) ((float) gridSize * randomPercentage);
        randomOffset = randomSize / 2;

        // random vertexes
        // we need one extra at end for complete count blocks
        xOff = 0;
        yOff = 0;

        for (y = 0; y <= count; y++) {
            for (x = 0; x <= count; x++) {
                if (randomPercentage != 0.0f) {
                    xOff = AppWindow.random.nextInt(randomSize) - randomOffset;
                    yOff = AppWindow.random.nextInt(randomSize) - randomOffset;
                }
                pnts.add(new GridPoint(((x * gridSize) + xOff), ((y * gridSize) + yOff)));
            }
        }

        // make right and bottom vertexes equal for wrapping
        for (y = 0; y <= count; y++) {
            fromPnt = pnts.get(y * (count + 1));
            toPnt = pnts.get((y * (count + 1)) + count);
            toPnt.x = ((count * gridSize) + fromPnt.x) + 1;
            toPnt.y = (y * gridSize) + (fromPnt.y - (y * gridSize));
        }

        for (x = 0; x <= count; x++) {
            fromPnt = pnts.get(x);
            toPnt = pnts.get(((count + 1) * count) + x);
            toPnt.x = (x * gridSize) + (fromPnt.x - (x * gridSize));
            toPnt.y = ((count * gridSize) + fromPnt.y) + 1;
        }

        // the 4 corners need to end up straight because they will
        // interfere with each other
        toPnt = pnts.get(0);
        toPnt.x = 0;
        toPnt.y = 0;

        toPnt = pnts.get(count);
        toPnt.x = count * gridSize;
        toPnt.y = 0;

        toPnt = pnts.get((count + 1) * count);
        toPnt.x = 0;
        toPnt.y = count * gridSize;

        toPnt = pnts.get(((count + 1) * count) + count);
        toPnt.x = count * gridSize;
        toPnt.y = count * gridSize;
        return (pnts);
    }

    protected void drawTriangleGradient(int x0, int y0, int x1, int y1, int x2, int y2, float f0, float f1, float f2, RagPoint n0, RagPoint n1, RagPoint n2, RagColor color) {
        int x, y, lx, rx, ty, my, by, tyX, myX, byX, idx;
        float f, fLx, fRx, fTyX, fMyX, fByX;
        RagPoint drawNormal, nLx, nRx, nTyX, nMyX, nByX;

        drawNormal = new RagPoint(0.0f, 0.0f, 0.0f);
        nLx = new RagPoint(0.0f, 0.0f, 0.0f);
        nRx = new RagPoint(0.0f, 0.0f, 0.0f);

        if ((y0 <= y1) && (y0 <= y2)) {
            ty = y0;
            tyX = x0;
            fTyX = f0;
            nTyX = n0;
            if (y1 < y2) {
                my = y1;
                myX = x1;
                fMyX = f1;
                nMyX = n1;
                by = y2;
                byX = x2;
                fByX = f2;
                nByX = n2;
            } else {
                my = y2;
                myX = x2;
                fMyX = f2;
                nMyX = n2;
                by = y1;
                byX = x1;
                fByX = f1;
                nByX = n1;
            }
        } else {
            if ((y1 <= y0) && (y1 <= y2)) {
                ty = y1;
                tyX = x1;
                fTyX = f1;
                nTyX = n1;
                if (y0 < y2) {
                    my = y0;
                    myX = x0;
                    fMyX = f0;
                    nMyX = n0;
                    by = y2;
                    byX = x2;
                    fByX = f2;
                    nByX = n2;
                } else {
                    my = y2;
                    myX = x2;
                    fMyX = f2;
                    nMyX = n2;
                    by = y0;
                    byX = x0;
                    fByX = f0;
                    nByX = n0;
                }
            } else {
                ty = y2;
                tyX = x2;
                fTyX = f2;
                nTyX = n2;
                if (y0 < y1) {
                    my = y0;
                    myX = x0;
                    fMyX = f0;
                    nMyX = n0;
                    by = y1;
                    byX = x1;
                    fByX = f1;
                    nByX = n1;
                } else {
                    my = y1;
                    myX = x1;
                    fMyX = f1;
                    nMyX = n1;
                    by = y0;
                    byX = x0;
                    fByX = f0;
                    nByX = n0;
                }
            }
        }

        // top half
        for (y = ty; y < my; y++) {
            if ((y < 0) || (y >= textureSize)) {
                continue;
            }

            if (myX < tyX) {
                lx = tyX + (int) ((float) ((myX - tyX) * (y - ty)) / (float) (my - ty));
                rx = tyX + (int) ((float) ((byX - tyX) * (y - ty)) / (float) (by - ty));
                fLx = fTyX + (((fMyX - fTyX) * (float) (y - ty)) / (float) (my - ty));
                fRx = fTyX + (((fByX - fTyX) * (float) (y - ty)) / (float) (by - ty));

                nLx.x = nTyX.x + (((nMyX.x - nTyX.x) * (float) (y - ty)) / (float) (my - ty));
                nLx.y = nTyX.y + (((nMyX.y - nTyX.y) * (float) (y - ty)) / (float) (my - ty));
                nLx.z = nTyX.z + (((nMyX.z - nTyX.z) * (float) (y - ty)) / (float) (my - ty));

                nRx.x = nTyX.x + (((nByX.x - nTyX.x) * (float) (y - ty)) / (float) (by - ty));
                nRx.y = nTyX.y + (((nByX.y - nTyX.y) * (float) (y - ty)) / (float) (by - ty));
                nRx.z = nTyX.z + (((nByX.z - nTyX.z) * (float) (y - ty)) / (float) (by - ty));
            } else {
                lx = tyX + (int) ((float) ((byX - tyX) * (y - ty)) / (float) (by - ty));
                rx = tyX + (int) ((float) ((myX - tyX) * (y - ty)) / (float) (my - ty));
                fLx = fTyX + (((fByX - fTyX) * (float) (y - ty)) / (float) (by - ty));
                fRx = fTyX + (((fMyX - fTyX) * (float) (y - ty)) / (float) (my - ty));

                nLx.x = nTyX.x + (((nByX.x - nTyX.x) * (float) (y - ty)) / (float) (by - ty));
                nLx.y = nTyX.y + (((nByX.y - nTyX.y) * (float) (y - ty)) / (float) (by - ty));
                nLx.z = nTyX.z + (((nByX.z - nTyX.z) * (float) (y - ty)) / (float) (by - ty));

                nRx.x = nTyX.x + (((nMyX.x - nTyX.x) * (float) (y - ty)) / (float) (my - ty));
                nRx.y = nTyX.y + (((nMyX.y - nTyX.y) * (float) (y - ty)) / (float) (my - ty));
                nRx.z = nTyX.z + (((nMyX.z - nTyX.z) * (float) (y - ty)) / (float) (my - ty));
            }

            if (lx > rx) {
                continue;
            }

            idx = ((y * textureSize) + lx) * 4;

            for (x = lx; x < rx; x++) {
                if ((x < 0) || (x >= textureSize)) {
                    idx += 4;
                    continue;
                }

                f = fLx + (((fRx - fLx) * (float) (x - lx)) / (float) (rx - lx));

                colorData[idx] = color.r * f;
                colorData[idx + 1] = color.g * f;
                colorData[idx + 2] = color.b * f;

                drawNormal.x = nLx.x + (((nRx.x - nLx.x) * (float) (x - lx)) / (float) (rx - lx));
                drawNormal.y = nLx.y + (((nRx.y - nLx.y) * (float) (x - lx)) / (float) (rx - lx));
                drawNormal.z = nLx.z + (((nRx.z - nLx.z) * (float) (x - lx)) / (float) (rx - lx));
                drawNormal.normalize();

                normalData[idx] = (drawNormal.x + 1.0f) * 0.5f;
                normalData[idx + 1] = (drawNormal.y + 1.0f) * 0.5f;
                normalData[idx + 2] = (drawNormal.z + 1.0f) * 0.5f;

                idx += 4;
            }
        }

        // bottom half
        for (y = my; y < by; y++) {
            if ((y < 0) || (y >= textureSize)) {
                continue;
            }

            if (myX < tyX) {
                lx = myX + (int) ((float) ((byX - myX) * (y - my)) / (float) (by - my));
                rx = tyX + (int) ((float) ((byX - tyX) * (y - ty)) / (float) (by - ty));
                fLx = fMyX + (((fByX - fMyX) * (float) (y - my)) / (float) (by - my));
                fRx = fTyX + (((fByX - fTyX) * (float) (y - ty)) / (float) (by - ty));

                nLx.x = nMyX.x + (((nByX.x - nMyX.x) * (float) (y - my)) / (float) (by - my));
                nLx.y = nMyX.y + (((nByX.y - nMyX.y) * (float) (y - my)) / (float) (by - my));
                nLx.z = nMyX.z + (((nByX.z - nMyX.z) * (float) (y - my)) / (float) (by - my));

                nRx.x = nTyX.x + (((nByX.x - nTyX.x) * (float) (y - ty)) / (float) (by - ty));
                nRx.y = nTyX.y + (((nByX.y - nTyX.y) * (float) (y - ty)) / (float) (by - ty));
                nRx.z = nTyX.z + (((nByX.z - nTyX.z) * (float) (y - ty)) / (float) (by - ty));
            } else {
                lx = tyX + (int) ((float) ((byX - tyX) * (y - ty)) / (float) (by - ty));
                rx = myX + (int) ((float) ((byX - myX) * (y - my)) / (float) (by - my));
                fLx = fTyX + (((fByX - fTyX) * (float) (y - ty)) / (float) (by - ty));
                fRx = fMyX + (((fByX - fMyX) * (float) (y - my)) / (float) (by - my));

                nLx.x = nTyX.x + (((nByX.x - nTyX.x) * (float) (y - ty)) / (float) (by - ty));
                nLx.y = nTyX.y + (((nByX.y - nTyX.y) * (float) (y - ty)) / (float) (by - ty));
                nLx.z = nTyX.z + (((nByX.z - nTyX.z) * (float) (y - ty)) / (float) (by - ty));

                nRx.x = nMyX.x + (((nByX.x - nMyX.x) * (float) (y - my)) / (float) (by - my));
                nRx.y = nMyX.y + (((nByX.y - nMyX.y) * (float) (y - my)) / (float) (by - my));
                nRx.z = nMyX.z + (((nByX.z - nMyX.z) * (float) (y - my)) / (float) (by - my));
            }

            if (lx > rx) {
                continue;
            }

            idx = ((y * textureSize) + lx) * 4;

            for (x = lx; x < rx; x++) {
                if ((x < 0) || (x >= textureSize)) {
                    idx += 4;
                    continue;
                }

                f = fLx + (((fRx - fLx) * (float) (x - lx)) / (float) (rx - lx));

                colorData[idx] = color.r * f;
                colorData[idx + 1] = color.g * f;
                colorData[idx + 2] = color.b * f;

                drawNormal.x = nLx.x + (((nRx.x - nLx.x) * (float) (x - lx)) / (float) (rx - lx));
                drawNormal.y = nLx.y + (((nRx.y - nLx.y) * (float) (x - lx)) / (float) (rx - lx));
                drawNormal.z = nLx.z + (((nRx.z - nLx.z) * (float) (x - lx)) / (float) (rx - lx));
                drawNormal.normalize();

                normalData[idx] = (drawNormal.x + 1.0f) * 0.5f;
                normalData[idx + 1] = (drawNormal.y + 1.0f) * 0.5f;
                normalData[idx + 2] = (drawNormal.z + 1.0f) * 0.5f;

                idx += 4;
            }
        }
    }

    private void drawTriangleGradientWrap(int x0, int y0, int x1, int y1, int x2, int y2, float f0, float f1, float f2, RagPoint n0, RagPoint n1, RagPoint n2, RagColor color) {
        int txtSize;

        // regular triangle
        drawTriangleGradient(x0, y0, x1, y1, x2, y2, f0, f1, f2, n0, n1, n2, color);

        // fix the wrap by drawing a triangle on the other side
        txtSize = textureSize - 1;
        if ((x0 < 0) || (x1 < 0) || (x2 < 0)) {
            drawTriangleGradient((txtSize + x0), y0, (txtSize + x1), y1, (txtSize + x2), y2, f0, f1, f2, n0, n1, n2, color);
        }
        if ((x0 >= textureSize) || (x1 >= textureSize) || (x2 >= textureSize)) {
            drawTriangleGradient((x0 - txtSize), y0, (x1 - txtSize), y1, (x2 - txtSize), y2, f0, f1, f2, n0, n1, n2, color);
        }
        if ((y0 < 0) || (y1 < 0) || (y2 < 0)) {
            drawTriangleGradient(x0, (txtSize + y0), x1, (txtSize + y1), x2, (txtSize + y2), f0, f1, f2, n0, n1, n2, color);
        }
        if ((y0 >= textureSize) || (y1 >= textureSize) || (y2 >= textureSize)) {
            drawTriangleGradient(x0, (y0 - txtSize), x1, (y1 - txtSize), x2, (y2 - txtSize), f0, f1, f2, n0, n1, n2, color);
        }
    }

    @Override
    public void generateInternal() {
        int x, y;
        ArrayList<GridPoint> pnts;
        GridPoint pnt0, pnt1, pnt2, pnt3, midPnt;
        RagColor baseCol, col;

        baseCol = getRandomColor();
        drawRect(0, 0, textureSize, textureSize, COLOR_WHITE);


        int count = 32;
        pnts = createRandomGridVertexes(count, 0.6f);

        midPnt = new GridPoint(0, 0);

        for (y = 0; y != count; y++) {
            for (x = 0; x != count; x++) {
                pnt0 = pnts.get((y * (count + 1)) + x);
                pnt1 = pnts.get(((y * (count + 1)) + x) + 1);
                pnt2 = pnts.get((((y + 1) * (count + 1)) + x));
                pnt3 = pnts.get(((((y + 1) * (count + 1)) + x)) + 1);

                midPnt.x = (pnt0.x + pnt1.x + pnt2.x + pnt3.x) / 4;
                midPnt.y = (pnt0.y + pnt1.y + pnt2.y + pnt3.y) / 4;

                col = adjustColorRandom(baseCol, 0.8f, 0.2f);

                drawTriangleGradientWrap(pnt0.x, pnt0.y, pnt1.x, pnt1.y, midPnt.x, midPnt.y, 0.5f, 0.5f, 1.0f, NORMAL_TOP_10, NORMAL_TOP_10, NORMAL_CLEAR, col);
                drawTriangleGradientWrap(pnt1.x, pnt1.y, pnt3.x, pnt3.y, midPnt.x, midPnt.y, 0.5f, 0.5f, 1.0f, NORMAL_RIGHT_10, NORMAL_RIGHT_10, NORMAL_CLEAR, col);
                drawTriangleGradientWrap(pnt3.x, pnt3.y, pnt2.x, pnt2.y, midPnt.x, midPnt.y, 0.5f, 0.5f, 1.0f, NORMAL_BOTTOM_10, NORMAL_BOTTOM_10, NORMAL_CLEAR, col);
                drawTriangleGradientWrap(pnt2.x, pnt2.y, pnt0.x, pnt0.y, midPnt.x, midPnt.y, 0.5f, 0.5f, 1.0f, NORMAL_LEFT_10, NORMAL_LEFT_10, NORMAL_CLEAR, col);
            }
        }

        // finish with the metallic-roughness
        createMetallicRoughnessMap(0.35f, 0.3f);
    }
}
