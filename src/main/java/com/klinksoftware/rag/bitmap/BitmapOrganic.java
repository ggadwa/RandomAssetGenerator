package com.klinksoftware.rag.bitmap;

import com.klinksoftware.rag.bitmap.utility.BitmapInterface;
import com.klinksoftware.rag.bitmap.utility.BitmapBase;
import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

@BitmapInterface
public class BitmapOrganic extends BitmapBase {

    public BitmapOrganic() {
        super();

        hasNormal = true;
        hasMetallicRoughness = true;
        hasEmissive = false;
        hasAlpha = false;
    }

    @Override
    public void generateInternal() {
        RagColor color;

        color = getRandomColor();
        drawRect(0, 0, textureSize, textureSize, color);

        createPerlinNoiseData(16, 16);
        color = getRandomColor();
        drawPerlinNoiseReplaceColorRect(0, 0, textureSize, textureSize, adjustColor(color, 0.8f), 0.5f, true);
        drawPerlinNoiseColorRect(0, 0, textureSize, textureSize, color, 0.7f);

        createMetallicRoughnessMap((0.2f + AppWindow.random.nextFloat(0.5f)), 0.6f);
    }

}
