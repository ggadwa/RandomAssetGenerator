package com.klinksoftware.rag.bitmap;

import com.klinksoftware.rag.bitmap.utility.BitmapInterface;
import com.klinksoftware.rag.bitmap.utility.BitmapBase;
import com.klinksoftware.rag.utility.*;

@BitmapInterface
public class BitmapTest extends BitmapBase {

    public BitmapTest() {
        super();

        hasNormal = true;
        hasMetallicRoughness = true;
        hasEmissive = false;
        hasAlpha = false;
    }

    @Override
    public void generateInternal() {
        int mid;

        mid = textureSize / 2;

        drawRect(0, 0, mid, mid, new RagColor(1.0f, 1.0f, 0.0f));
        drawRect(mid, 0, textureSize, mid, new RagColor(1.0f, 0.0f, 0.0f));
        drawRect(0, mid, mid, textureSize, new RagColor(0.0f, 1.0f, 0.0f));
        drawRect(mid, mid, textureSize, textureSize, new RagColor(0.0f, 0.0f, 1.0f));
    }
}
