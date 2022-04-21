package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

public class BitmapMonitor extends BitmapComputer {

    public BitmapMonitor() {
        super();

        textureSize = 1024;
        hasNormal = true;
        hasMetallicRoughness = true;
        hasEmissive = true;
        hasAlpha = false;
    }

    @Override
    public void generateInternal(int variationMode) {
        int offset, panelEdgeSize, panelInsideEdgeSize;
        RagColor panelColor;

        offset = textureSize / 2;
        panelEdgeSize = 2 + AppWindow.random.nextInt(4);
        panelInsideEdgeSize = 2 + AppWindow.random.nextInt(3);

        panelColor = getRandomGray(0.6f, 0.8f);

        drawRect(0, 0, textureSize, textureSize, panelColor);
        draw3DFrameRect(0, 0, offset, offset, panelEdgeSize, panelColor, true);                       // left and right
        generateComputerComponentScreen(offset, 0, textureSize, offset, panelInsideEdgeSize);       // front and back
        draw3DFrameRect(offset, 0, textureSize, offset, panelEdgeSize, panelColor, true);             // top and bottom

        // set the emissive
        emissiveFactor = new RagPoint(1.0f, 1.0f, 1.0f);

        // finish with the metallic-roughness
        createMetallicRoughnessMap(0.75f, 0.4f);
    }
}
