package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

@BitmapInterface
public class BitmapWood extends BitmapBase
{
    public BitmapWood()    {
        super();

        hasNormal=true;
        hasMetallicRoughness=true;
        hasEmissive=false;
        hasAlpha=false;
    }

    public void generateWoodPlanks(int boardCount, int boardSize, int edgeSize, RagColor woodColor) {
        int n, y, ty, by, lft, rgt, boardType;

        // perlin noise
        createPerlinNoiseData(32, 32);

        // regular wood planking
        lft = 0;

        y = (int) ((float) textureSize * 0.5);
        ty = (int) ((float) textureSize * 0.33);
        by = (int) ((float) textureSize * 0.66);

        for (n = 0; n != boardCount; n++) {
            rgt = lft + boardSize;
            if (n == (boardCount - 1)) {
                rgt = textureSize;
            }

            boardType = AppWindow.random.nextInt(5);

            switch (boardType) {
                case 0:
                    generateWoodDrawBoard(lft, 0, rgt, textureSize, edgeSize, woodColor);
                    break;
                case 1:
                    generateWoodDrawBoard(lft, 0, rgt, y, edgeSize, woodColor);
                    generateWoodDrawBoard(lft, y, rgt, textureSize, edgeSize, woodColor);
                    break;
                case 2:
                    generateWoodDrawBoard(lft, -edgeSize, rgt, y, edgeSize, woodColor);
                    generateWoodDrawBoard(lft, y, rgt, (textureSize + edgeSize), edgeSize, woodColor);
                    break;
                case 3:
                    generateWoodDrawBoard(lft, 0, rgt, ty, edgeSize, woodColor);
                    generateWoodDrawBoard(lft, ty, rgt, by, edgeSize, woodColor);
                    generateWoodDrawBoard(lft, by, rgt, textureSize, edgeSize, woodColor);
                    break;
                case 4:
                    generateWoodDrawBoard(lft, -edgeSize, rgt, (textureSize + edgeSize), edgeSize, woodColor);
                    break;
            }

            lft = rgt;
        }
    }

    @Override
    public void generateInternal()    {
        int boardCount, boardSize, edgeSize;
        RagColor woodColor;

        // some random values
        boardCount = 4 + AppWindow.random.nextInt(12);
        boardSize = textureSize / boardCount;
        edgeSize = (int) (((float) textureSize * 0.005f) + (AppWindow.random.nextFloat() * ((float) textureSize * 0.005f)));
        woodColor = getRandomColor();

        // the planks
        generateWoodPlanks(boardCount, boardSize, edgeSize, woodColor);

        createMetallicRoughnessMap(0.4f,0.2f);
    }
}
