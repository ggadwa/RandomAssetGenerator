package com.klinksoftware.rag.bitmap;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.bitmap.utility.BitmapInterface;
import com.klinksoftware.rag.bitmap.utility.BitmapBase;
import com.klinksoftware.rag.utility.RagColor;
import java.util.ArrayList;

@BitmapInterface
public class BitmapDiamond extends BitmapBase {

    public BitmapDiamond() {
        super();

        hasNormal = true;
        hasMetallicRoughness = true;
        hasEmissive = false;
        hasAlpha = false;
    }

    private void singleDiamondChunk(int xStart, int yStart, int pixelSize, int[] diamondCounts, RagColor baseCol) {
        int x, y, count;
        float darkFactor, colorAdjust;
        boolean darkLeft, darkRight, darkTop, darkBottom;
        ArrayList<GridPoint> pnts;
        GridPoint pnt0, pnt1, pnt2, pnt3, midPnt;
        RagColor col;

        // random points
        count = diamondCounts[AppWindow.random.nextInt(diamondCounts.length)];
        pnts = createRandomGridVertexes(count, 0.0f, xStart, yStart, pixelSize);

        // random color
        darkFactor = 0.9f + AppWindow.random.nextFloat(0.1f);

        colorAdjust = 0.6f + AppWindow.random.nextFloat(0.15f);
        darkLeft = AppWindow.random.nextBoolean();
        darkRight = AppWindow.random.nextBoolean();
        darkTop = AppWindow.random.nextBoolean();
        darkBottom = AppWindow.random.nextBoolean();

        // draw the diamonds
        midPnt = new GridPoint(0, 0);

        for (y = 0; y != count; y++) {
            for (x = 0; x != count; x++) {
                // 0 = top left, 1 = top right, 2 = bottom right, 3 = bottom left
                pnt0 = pnts.get((y * (count + 1)) + x);
                pnt1 = pnts.get(((y * (count + 1)) + x) + 1);
                pnt2 = pnts.get(((((y + 1) * (count + 1)) + x)) + 1);
                pnt3 = pnts.get((((y + 1) * (count + 1)) + x));

                midPnt.x = (pnt0.x + pnt1.x + pnt2.x + pnt3.x) / 4;
                midPnt.y = (pnt0.y + pnt1.y + pnt2.y + pnt3.y) / 4;

                col = adjustColor(baseCol, (((y == 0) && (darkTop)) || ((y == (count - 1) && (darkBottom)) || ((x == 0) && (darkLeft)) || ((x == (count - 1)) && (darkRight)))) ? colorAdjust : 1.0f);

                drawTriangleGradient(pnt0.x, pnt0.y, pnt1.x, pnt1.y, midPnt.x, midPnt.y, 0.8f, darkFactor, 1.0f, NORMAL_TOP_45, NORMAL_TOP_45, NORMAL_TOP_45, col, false, 0.0f, 0.0f, false, false);
                drawTriangleGradient(pnt1.x, pnt1.y, pnt2.x, pnt2.y, midPnt.x, midPnt.y, 0.8f, darkFactor, 1.0f, NORMAL_RIGHT_45, NORMAL_RIGHT_45, NORMAL_RIGHT_45, col, false, 0.0f, 0.0f, false, false);
                drawTriangleGradient(pnt2.x, pnt2.y, pnt3.x, pnt3.y, midPnt.x, midPnt.y, 0.8f, darkFactor, 1.0f, NORMAL_BOTTOM_45, NORMAL_BOTTOM_45, NORMAL_BOTTOM_45, col, false, 0.0f, 0.0f, false, false);
                drawTriangleGradient(pnt3.x, pnt3.y, pnt0.x, pnt0.y, midPnt.x, midPnt.y, 0.8f, darkFactor, 1.0f, NORMAL_LEFT_45, NORMAL_LEFT_45, NORMAL_LEFT_45, col, false, 0.0f, 0.0f, false, false);
            }
        }
    }

    @Override
    public void generateInternal() {
        int[] diamondLargeCounts = {8, 16};
        int[] diamondSmallCounts = {4, 8};
        RagColor baseCol;

        // base color
        baseCol = getRandomColor();

        // one big panel of diamonds or 4 small ones
        if (AppWindow.random.nextBoolean()) {
            singleDiamondChunk(0, 0, textureSize, diamondLargeCounts, baseCol);
        } else {
            singleDiamondChunk(0, 0, (textureSize / 2), diamondSmallCounts, baseCol);
            singleDiamondChunk((textureSize / 2), 0, (textureSize / 2), diamondSmallCounts, baseCol);
            singleDiamondChunk(0, (textureSize / 2), (textureSize / 2), diamondSmallCounts, baseCol);
            singleDiamondChunk((textureSize / 2), (textureSize / 2), (textureSize / 2), diamondSmallCounts, baseCol);
        }

        // finish with the metallic-roughness
        createMetallicRoughnessMap((0.6f + AppWindow.random.nextFloat(0.1f)), 0.3f);
    }
}
