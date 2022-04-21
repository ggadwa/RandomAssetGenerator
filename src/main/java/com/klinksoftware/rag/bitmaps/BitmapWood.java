package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

public class BitmapWood extends BitmapBase
{
    public BitmapWood()    {
        super();

        hasNormal=true;
        hasMetallicRoughness=true;
        hasEmissive=false;
        hasAlpha=false;
    }

    public void generateWoodDrawBoard(int lft, int top, int rgt, int bot, int edgeSize, RagColor woodColor)    {
        RagColor        col,frameColor;

        col=adjustColorRandom(woodColor,0.7f,1.2f);
        frameColor=adjustColorRandom(col,0.65f,0.75f);

            // the board edge

        drawRect(lft,top,rgt,bot,col);
        draw3DFrameRect(lft,top,rgt,bot,edgeSize,frameColor,true);

            // stripes and a noise overlay

        if ((bot-top)>(rgt-lft)) {
            drawColorStripeVertical((lft+edgeSize),(top+edgeSize),(rgt-edgeSize),(bot-edgeSize),0.1f,col);
        }
        else {
            drawColorStripeHorizontal((lft+edgeSize),(top+edgeSize),(rgt-edgeSize),(bot-edgeSize),0.1f,col);
        }

        drawPerlinNoiseRect((lft+edgeSize),(top+edgeSize),(rgt-edgeSize),(bot-edgeSize),0.8f,1.2f);
        drawStaticNoiseRect((lft+edgeSize),(top+edgeSize),(rgt-edgeSize),(bot-edgeSize),0.9f,1.0f);

            // blur both the color and the normal

        blur(colorData,(lft+edgeSize),(top+edgeSize),(rgt-edgeSize),(bot-edgeSize),2,true);
        blur(normalData,(lft+edgeSize),(top+edgeSize),(rgt-edgeSize),(bot-edgeSize),5,true);
    }

    public void generateWoodPlanks(int boardCount, int boardSize, int edgeSize, RagColor woodColor, boolean isBox) {
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

            boardType = (isBox) ? 0 : AppWindow.random.nextInt(5);

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
        generateWoodPlanks(boardCount, boardSize, edgeSize, woodColor, false);

        createMetallicRoughnessMap(0.4f,0.2f);
    }
}
