package com.klinksoftware.rag.bitmap;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.bitmap.utility.BitmapInterface;
import com.klinksoftware.rag.bitmap.utility.BitmapBase;
import com.klinksoftware.rag.utility.RagColor;
import com.klinksoftware.rag.utility.RagPoint;
import java.util.ArrayList;

@BitmapInterface
public class BitmapRockMountain extends BitmapBase {

    public BitmapRockMountain() {
        super();

        hasNormal = true;
        hasMetallicRoughness = true;
        hasEmissive = false;
        hasAlpha = false;
    }

    @Override
    public void generateInternal() {
        int x, y, count;
        int[] mountainCounts = {4, 8};
        float minCol, addCol;
        ArrayList<GridPoint> pnts;
        GridPoint pnt0, pnt1, pnt2, pnt3;
        RagPoint normal;
        RagColor baseCol, col;

        // random points
        count = mountainCounts[AppWindow.random.nextInt(mountainCounts.length)];
        pnts = createRandomGridVertexes(count, 0.3f, 0, 0, textureSize);

        // draw the mountains
        baseCol = getRandomColor();

        createPerlinNoiseData(8, 8);
        //} else {
        //    createPerlinNoiseData(16, 16);
        //}
        createNormalNoiseData((1.0f + AppWindow.random.nextFloat(0.2f)), (0.1f + AppWindow.random.nextFloat(0.2f)));

        for (y = 0; y != count; y++) {
            for (x = 0; x != count; x++) {

                System.out.println(x + "," + y);

                // perlin and noise
                //if (AppWindow.random.nextBoolean()) {
                //    createPerlinNoiseData(8, 8);
                //} else {
                //    createPerlinNoiseData(16, 16);
                //}
                //createNormalNoiseData((1.0f + AppWindow.random.nextFloat(0.2f)), (0.1f + AppWindow.random.nextFloat(0.2f)));

                // direction
                switch (AppWindow.random.nextInt(4)) {
                    case 0:
                        normal = NORMAL_TOP_LEFT_45;
                        break;
                    case 1:
                        normal = NORMAL_TOP_RIGHT_45;
                        break;
                    case 2:
                        normal = NORMAL_TOP_45;
                        break;
                    default:
                        normal = NORMAL_TOP_10;
                        break;
                }

                // 0 = top left, 1 = top right, 2 = bottom right, 3 = bottom left
                pnt0 = pnts.get((y * (count + 1)) + x);
                pnt1 = pnts.get(((y * (count + 1)) + x) + 1);
                pnt2 = pnts.get(((((y + 1) * (count + 1)) + x)) + 1);
                pnt3 = pnts.get((((y + 1) * (count + 1)) + x));

                col = baseCol; // adjustColorRandom(baseCol, 0.8f, 0.2f);
                minCol = 0.5f + AppWindow.random.nextFloat(0.2f);
                addCol = 1.0f - minCol;

                drawTriangleGradientWrap(pnt0.x, pnt0.y, pnt1.x, pnt1.y, pnt2.x, pnt2.y, 1.0f, 1.0f, 1.0f, normal, normal, normal, col, true, minCol, addCol, false, true);
                drawTriangleGradientWrap(pnt0.x, pnt0.y, pnt2.x, pnt2.y, pnt3.x, pnt3.y, 1.0f, 1.0f, 1.0f, normal, normal, normal, col, true, minCol, addCol, false, true);
            }
        }

        // blur
        blur(colorData, 0, 0, textureSize, textureSize, (2 + AppWindow.random.nextInt(textureSize / 125)), true);

        // finish with the metallic-roughness
        createMetallicRoughnessMap((0.45f + AppWindow.random.nextFloat(0.2f)), 0.3f);
    }
}
