package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

public class BitmapWoodBox extends BitmapWood {

    public BitmapWoodBox() {
        super();

        hasNormal = true;
        hasMetallicRoughness = true;
        hasEmissive = false;
        hasAlpha = false;
    }

    @Override
    public void generateInternal() {
        int boardCount, boardSize, edgeSize;
        RagColor woodColor;

        // some random values
        boardCount = 4 + AppWindow.random.nextInt(12);
        boardSize = textureSize / boardCount;
        edgeSize = (int) (((float) textureSize * 0.005f) + (AppWindow.random.nextFloat() * ((float) textureSize * 0.005f)));
        woodColor = getRandomColor();

        // the planks
        generateWoodPlanks(boardCount, boardSize, edgeSize, woodColor, false);

        // box outlines
        woodColor = adjustColor(woodColor, 0.7f);
        generateWoodDrawBoard(0, 0, boardSize, textureSize, edgeSize, woodColor);
        generateWoodDrawBoard((textureSize - boardSize), 0, textureSize, textureSize, edgeSize, woodColor);
        generateWoodDrawBoard(boardSize, 0, (textureSize - boardSize), boardSize, edgeSize, woodColor);
        generateWoodDrawBoard(boardSize, (textureSize - boardSize), (textureSize - boardSize), textureSize, edgeSize, woodColor);

        // finish with the metallic-roughness
        createMetallicRoughnessMap(0.4f, 0.2f);
    }
}
