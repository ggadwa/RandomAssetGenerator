package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;
import java.util.ArrayList;

@BitmapInterface
public class BitmapBrickPattern extends BitmapBrickRow {

    public BitmapBrickPattern(int textureSize) {
        super(textureSize);

        hasNormal = true;
        hasMetallicRoughness = true;
        hasEmissive = false;
        hasAlpha = false;
    }

    @Override
    public void generateInternal() {
        int edgeSize, paddingSize;
        ArrayList<RagRect> rects;
        RagColor brickColor, altBrickColor;

        brickColor = getRandomColor();
        altBrickColor = getRandomColor();

        edgeSize = 3 + AppWindow.random.nextInt(textureSize / 70);
        paddingSize = 3 + AppWindow.random.nextInt(textureSize / 100);

        // create noise data
        createPerlinNoiseData(32, 32);
        createNormalNoiseData(1.5f, 0.5f);

        // grout is a static noise color
        drawGrout();

        // draw pattern
        rects = createBlockPattern();

        for (RagRect rect : rects) {
            generateSingleBrick(rect.getLeft(), rect.getTop(), rect.getRight(), rect.getBottom(), edgeSize, paddingSize, brickColor, altBrickColor, false, false, false);
        }

        // finish with the metallic-roughness
        createMetallicRoughnessMap(0.5f, 0.4f);
    }

}
