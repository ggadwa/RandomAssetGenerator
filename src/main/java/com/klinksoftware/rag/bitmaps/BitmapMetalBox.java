package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

public class BitmapMetalBox extends BitmapMetal {

    public BitmapMetalBox() {
        super();

        hasNormal = true;
        hasMetallicRoughness = true;
        hasEmissive = false;
        hasAlpha = false;
    }

    @Override
    public void generateInternal() {
        int edgeSize, screwSize;
        RagColor metalColor, altMetalColor;

        metalColor = getRandomColor();
        altMetalColor = getRandomColor();
        edgeSize = (int) (((float) textureSize * 0.005) + (AppWindow.random.nextFloat() * ((float) textureSize * 0.005)));
        screwSize = (int) (((float) textureSize * 0.03) + (AppWindow.random.nextFloat() * ((float) textureSize * 0.05)));

        generateMetalPanel(0, 0, textureSize, textureSize, metalColor, altMetalColor, edgeSize, screwSize, true);

        // finish with the metallic-roughness
        createMetallicRoughnessMap(0.5f, 0.6f);
    }
}
