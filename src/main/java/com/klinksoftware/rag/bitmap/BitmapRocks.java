package com.klinksoftware.rag.bitmap;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.bitmap.utility.BitmapInterface;
import com.klinksoftware.rag.bitmap.utility.BitmapBase;
import com.klinksoftware.rag.utility.RagColor;
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
        gridSize = textureSize / (count - 1);
        randomSize = (int) ((float) gridSize * randomPercentage);
        randomOffset = randomSize / 2;

        // random vertexes
        for (y = 0; y != count; y++) {

            for (x = 0; x != count; x++) {
                xOff = AppWindow.random.nextInt(randomSize) - randomOffset;
                yOff = AppWindow.random.nextInt(randomSize) - randomOffset;
                pnts.add(new GridPoint(((x * gridSize) + xOff), ((y * gridSize) + yOff)));
            }
        }

        // make right and bottom vertexes equal for wrapping
        for (y = 0; y != count; y++) {
            fromPnt = pnts.get(y * count);
            toPnt = pnts.get((y * count) + (count - 1));
            toPnt.x = ((count - 1) * gridSize) + fromPnt.x;
            toPnt.y = (y * gridSize) + (fromPnt.y - (y * gridSize));
        }

        for (x = 0; x != count; x++) {
            fromPnt = pnts.get(x);
            toPnt = pnts.get((count * (count - 1)) + x);
            toPnt.x = (x * gridSize) + (fromPnt.x - (x * gridSize));
            toPnt.y = ((count - 1) * gridSize) + fromPnt.y;
        }

        return (pnts);
    }

    protected void drawTriangleGradient(int x0, int y0, int x1, int y1, int x2, int y2, float fx0, float fy0, float fx1, float fy1, float fx2, float fy2, boolean doNormals, RagColor color) {
        int x, y, lx, rx, ty, my, by, tyX, myX, byX, idx;
        float f, fLx, fRx, fTyX, fMyX, fByX;

        if ((y0 <= y1) && (y0 <= y2)) {
            ty = y0;
            tyX = x0;
            fTyX = fx0;
            if (y1 < y2) {
                my = y1;
                myX = x1;
                fMyX = fx1;
                by = y2;
                byX = x2;
                fByX = fx2;
            } else {
                my = y2;
                myX = x2;
                fMyX = fx2;
                by = y1;
                byX = x1;
                fByX = fx1;
            }
        } else {
            if ((y1 <= y0) && (y1 <= y2)) {
                ty = y1;
                tyX = x1;
                fTyX = fx1;
                if (y0 < y2) {
                    my = y0;
                    myX = x0;
                    fMyX = fx0;
                    by = y2;
                    byX = x2;
                    fByX = fx2;
                } else {
                    my = y2;
                    myX = x2;
                    fMyX = fx2;
                    by = y0;
                    byX = x0;
                    fByX = fx0;
                }
            } else {
                ty = y2;
                tyX = x2;
                fTyX = fx2;
                if (y0 < y1) {
                    my = y0;
                    myX = x0;
                    fMyX = fx0;
                    by = y1;
                    byX = x1;
                    fByX = fx1;
                } else {
                    my = y1;
                    myX = x1;
                    fMyX = fx1;
                    by = y0;
                    byX = x0;
                    fByX = fx0;
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
            } else {
                lx = tyX + (int) ((float) ((byX - tyX) * (y - ty)) / (float) (by - ty));
                rx = tyX + (int) ((float) ((myX - tyX) * (y - ty)) / (float) (my - ty));
                fLx = fTyX + (((fByX - fTyX) * (float) (y - ty)) / (float) (by - ty));
                fRx = fTyX + (((fMyX - fTyX) * (float) (y - ty)) / (float) (my - ty));
            }

            if (lx < 0) {
                lx = 0;
            }
            if (rx >= textureSize) {
                rx = textureSize - 1;
            }
            if (lx > rx) {
                continue;
            }

            idx = ((y * textureSize) + lx) * 4;

            for (x = lx; x < rx; x++) {

                f = fLx + (((fRx - fLx) * (float) (x - lx)) / (float) (rx - lx));

                colorData[idx] = color.r * f;
                colorData[idx + 1] = color.g * f;
                colorData[idx + 2] = color.b * f;

                if (doNormals) {
                    normalData[idx] = (NORMAL_CLEAR.x + 1.0f) * 0.5f;
                    normalData[idx + 1] = (NORMAL_CLEAR.y + 1.0f) * 0.5f;
                    normalData[idx + 2] = (NORMAL_CLEAR.z + 1.0f) * 0.5f;
                }

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
            } else {
                lx = tyX + (int) ((float) ((byX - tyX) * (y - ty)) / (float) (by - ty));
                rx = myX + (int) ((float) ((byX - myX) * (y - my)) / (float) (by - my));
                fLx = fTyX + (((fByX - fTyX) * (float) (y - ty)) / (float) (by - ty));
                fRx = fMyX + (((fByX - fMyX) * (float) (y - my)) / (float) (by - my));
            }

            if (lx < 0) {
                lx = 0;
            }
            if (rx >= textureSize) {
                rx = textureSize - 1;
            }
            if (lx > rx) {
                continue;
            }

            idx = ((y * textureSize) + lx) * 4;

            for (x = lx; x < rx; x++) {
                f = fLx + (((fRx - fLx) * (float) (x - lx)) / (float) (rx - lx));

                colorData[idx] = color.r * f;
                colorData[idx + 1] = color.g * f;
                colorData[idx + 2] = color.b * f;

                if (doNormals) {
                    normalData[idx] = (NORMAL_CLEAR.x + 1.0f) * 0.5f;
                    normalData[idx + 1] = (NORMAL_CLEAR.y + 1.0f) * 0.5f;
                    normalData[idx + 2] = (NORMAL_CLEAR.z + 1.0f) * 0.5f;
                }

                idx += 4;
            }
        }

        // normals
        if (doNormals) {
            drawLineNormal(x0, y0, x1, y1, NORMAL_LEFT_45);
            drawLineNormal(x0, y0, x2, y2, NORMAL_RIGHT_45);
            drawLineNormal(x1, y1, x2, y2, NORMAL_BOTTOM_45);
        }
    }

    private void drawTriangleGradientWrap(int x0, int y0, int x1, int y1, int x2, int y2, float fx0, float fy0, float fx1, float fy1, float fx2, float fy2, boolean doNormals, RagColor color) {
        int txtSize = textureSize - 1;

        drawTriangleGradient(x0, y0, x1, y1, x2, y2, fx0, fy0, fx1, fy1, fx2, fy2, doNormals, color);

        if ((x0 < 0) || (x1 < 0) || (x2 < 0)) {
            drawTriangleGradient((txtSize + x0), y0, (txtSize + x1), y1, (txtSize + x2), y2, fx0, fy0, fx1, fy1, fx2, fy2, doNormals, color);
        }
        if ((x0 >= textureSize) || (x1 >= textureSize) || (x2 >= textureSize)) {
            drawTriangleGradient((x0 - txtSize), y0, (x1 - txtSize), y1, (x2 - txtSize), y2, fx0, fy0, fx1, fy1, fx2, fy2, doNormals, color);
        }
        if ((y0 < 0) || (y1 < 0) || (y2 < 0)) {
            drawTriangleGradient(x0, (txtSize + y0), x1, (txtSize + y1), x2, (txtSize + y2), fx0, fy0, fx1, fy1, fx2, fy2, doNormals, color);
        }
        if ((y0 >= textureSize) || (y1 >= textureSize) || (y2 >= textureSize)) {
            drawTriangleGradient(x0, (y0 - txtSize), x1, (y1 - txtSize), x2, (y2 - txtSize), fx0, fy0, fx1, fy1, fx2, fy2, doNormals, color);
        }
    }

    @Override
    public void generateInternal() {
        int x, y;
        ArrayList<GridPoint> pnts;
        GridPoint pnt0, pnt1, pnt2, pnt3, midPnt;

        drawRect(0, 0, textureSize, textureSize, COLOR_WHITE);

        int count = 8;
        pnts = createRandomGridVertexes(count, 0.4f);

        midPnt = new GridPoint(0, 0);

        for (y = 0; y != (count - 1); y++) {
            for (x = 0; x != (count - 1); x++) {
                pnt0 = pnts.get((y * count) + x);
                pnt1 = pnts.get(((y * count) + x) + 1);
                pnt2 = pnts.get((((y + 1) * count) + x));
                pnt3 = pnts.get(((((y + 1) * count) + x)) + 1);

                midPnt.x = (pnt0.x + pnt1.x + pnt2.x + pnt3.x) / 4;
                midPnt.y = (pnt0.y + pnt1.y + pnt2.y + pnt3.y) / 4;

                drawTriangleGradientWrap(pnt0.x, pnt0.y, pnt1.x, pnt1.y, midPnt.x, midPnt.y, 0.5f, 0.5f, 0.5f, 0.5f, 1.0f, 1.0f, false, getRandomColor());
                drawTriangleGradientWrap(pnt1.x, pnt1.y, pnt3.x, pnt3.y, midPnt.x, midPnt.y, 0.5f, 0.5f, 0.5f, 0.5f, 1.0f, 1.0f, false, getRandomColor());
                drawTriangleGradientWrap(pnt3.x, pnt3.y, pnt2.x, pnt2.y, midPnt.x, midPnt.y, 0.5f, 0.5f, 0.5f, 0.5f, 1.0f, 1.0f, false, getRandomColor());
                drawTriangleGradientWrap(pnt2.x, pnt2.y, pnt0.x, pnt0.y, midPnt.x, midPnt.y, 0.5f, 0.5f, 0.5f, 0.5f, 1.0f, 1.0f, false, getRandomColor());

            }
        }

        // finish with the metallic-roughness
        createMetallicRoughnessMap(0.35f, 0.3f);
    }
}
