package com.klinksoftware.rag.bitmap;

import com.klinksoftware.rag.bitmap.utility.BitmapInterface;
import com.klinksoftware.rag.utility.RagColor;
import com.klinksoftware.rag.utility.RagRect;
import java.util.ArrayList;

@BitmapInterface
public class BitmapStonePattern extends BitmapStoneRow {

    public BitmapStonePattern(int textureSize) {
        super(textureSize);

        hasNormal = true;
        hasMetallicRoughness = true;
        hasEmissive = false;
        hasAlpha = false;
    }

    @Override
    public void generateInternal() {
        float[] backgroundData, stoneColorData, stoneNormalData;
        ArrayList<RagRect> rects;
        RagColor stoneColor, altStoneColor;

        stoneColor = getRandomColor();
        altStoneColor = getRandomColorDull(0.9f);

        // the noise grout
        drawGrout();

        // we draw the stones all alone on the noise
        // background so we can distort the stones and
        // only catch the background
        backgroundData = colorData.clone();
        stoneColorData = colorData.clone();
        stoneNormalData = normalData.clone();

        createNormalNoiseData(2.5f, 0.5f);

        // get the pattern
        rects = createBlockPattern();

        // create perlin based on # of stones
        createPerlinNoiseForStoneSize(rects.size());

        // draw pattern
        for (RagRect rect : rects) {
            generateSingleStone(rect.getLeft(), rect.getTop(), rect.getRight(), rect.getBottom(), backgroundData, stoneColorData, stoneNormalData, false, stoneColor, altStoneColor);
        }

        // finally push over the stone copy version
        colorData = stoneColorData;
        normalData = stoneNormalData;

        // finish with the metallic-roughness
        createMetallicRoughnessMap(0.5f, 0.5f);
    }
}
