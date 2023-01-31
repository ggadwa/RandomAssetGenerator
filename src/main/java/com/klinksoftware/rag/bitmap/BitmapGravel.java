package com.klinksoftware.rag.bitmap;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.bitmap.utility.BitmapInterface;
import com.klinksoftware.rag.bitmap.utility.BitmapBase;
import com.klinksoftware.rag.utility.RagColor;
import java.util.ArrayList;

@BitmapInterface
public class BitmapGravel extends BitmapBase {

    public BitmapGravel() {
        super();

        hasNormal = true;
        hasMetallicRoughness = true;
        hasEmissive = false;
        hasAlpha = false;
    }

    private void drawGravelLayer(int count, float randomVertexPercentage, float randomShowPercentage, RagColor baseCol) {
        int x, y;
        float darkFactor;
        boolean flipNormals;
        ArrayList<GridPoint> pnts;
        GridPoint pnt0, pnt1, pnt2, pnt3, midPnt;
        RagColor col;

        // perlin noise and random points
        createPerlinNoiseData(32, 32);
        pnts = createRandomGridVertexes(count, randomVertexPercentage, 0, 0, textureSize);

        // draw the gravel layer
        midPnt = new GridPoint(0, 0);

        for (y = 0; y != count; y++) {
            for (x = 0; x != count; x++) {

                // skip some
                if (AppWindow.random.nextFloat() > randomShowPercentage) {
                    continue;
                }

                // small percentage flipped
                flipNormals = (AppWindow.random.nextFloat() > 0.85f);

                // 0 = top left, 1 = top right, 2 = bottom right, 3 = bottom left
                pnt0 = pnts.get((y * (count + 1)) + x);
                pnt1 = pnts.get(((y * (count + 1)) + x) + 1);
                pnt2 = pnts.get(((((y + 1) * (count + 1)) + x)) + 1);
                pnt3 = pnts.get((((y + 1) * (count + 1)) + x));

                midPnt.x = (pnt0.x + pnt1.x + pnt2.x + pnt3.x) / 4;
                midPnt.y = (pnt0.y + pnt1.y + pnt2.y + pnt3.y) / 4;

                col = adjustColorRandom(baseCol, 0.8f, 0.2f);
                darkFactor = 0.5f + AppWindow.random.nextFloat(0.2f);

                drawTriangleGradientWrap(pnt0.x, pnt0.y, pnt1.x, pnt1.y, midPnt.x, midPnt.y, darkFactor, darkFactor, 1.0f, NORMAL_TOP_10, NORMAL_TOP_10, NORMAL_CLEAR, col, true, 0.75f, 0.25f, flipNormals, false);
                drawTriangleGradientWrap(pnt1.x, pnt1.y, pnt2.x, pnt2.y, midPnt.x, midPnt.y, darkFactor, darkFactor, 1.0f, NORMAL_RIGHT_10, NORMAL_RIGHT_10, NORMAL_CLEAR, col, true, 0.75f, 0.25f, flipNormals, false);
                drawTriangleGradientWrap(pnt2.x, pnt2.y, pnt3.x, pnt3.y, midPnt.x, midPnt.y, darkFactor, darkFactor, 1.0f, NORMAL_BOTTOM_10, NORMAL_BOTTOM_10, NORMAL_CLEAR, col, true, 0.75f, 0.25f, flipNormals, false);
                drawTriangleGradientWrap(pnt3.x, pnt3.y, pnt0.x, pnt0.y, midPnt.x, midPnt.y, darkFactor, darkFactor, 1.0f, NORMAL_LEFT_10, NORMAL_LEFT_10, NORMAL_CLEAR, col, true, 0.75f, 0.25f, flipNormals, false);
            }
        }
    }

    @Override
    public void generateInternal() {
        int n, layerCount;
        float showPercentage;
        RagColor baseCol;

        // background gray
        baseCol = getRandomGrayColor(0.4f, 0.6f);
        drawRect(0, 0, textureSize, textureSize, baseCol);

        createPerlinNoiseData(32, 32);
        drawPerlinNoiseRect(0, 0, textureSize, textureSize, 0.5f, 1.0f);

        createNormalNoiseData(2.5f, 0.5f);
        drawNormalNoiseRect(0, 0, textureSize, textureSize);

        // gravel layers
        showPercentage = 0.7f;
        layerCount = 2 + AppWindow.random.nextInt(3);

        for (n = 0; n != layerCount; n++) {
            baseCol = getRandomGrayColor(0.4f, 0.8f);
            drawGravelLayer(((n == 0) ? 64 : 32), 0.8f, showPercentage, baseCol);
            showPercentage -= 0.1f;
        }

        // blur
        blur(colorData, 0, 0, textureSize, textureSize, (2 + AppWindow.random.nextInt(textureSize / 125)), true);

        // finish with the metallic-roughness
        createMetallicRoughnessMap((0.45f + AppWindow.random.nextFloat(0.2f)), 0.3f);
    }
}
