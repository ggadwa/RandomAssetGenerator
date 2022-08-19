package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

@BitmapInterface
public class BitmapMonitor extends BitmapComputer {

    public BitmapMonitor(int textureSize) {
        super(textureSize);

        textureSize = 1024;
        hasNormal = true;
        hasMetallicRoughness = true;
        hasEmissive = true;
        hasAlpha = false;
    }

    @Override
    public void generateInternal() {
        int offset, panelEdgeSize, panelInsideEdgeSize;
        RagColor panelColor;

        offset = textureSize / 2;
        panelEdgeSize = 4 + AppWindow.random.nextInt(6);
        panelInsideEdgeSize = 3 + AppWindow.random.nextInt(3);

        panelColor = getRandomGray(0.6f, 0.8f);
        panelColor.b *= (1.0f + AppWindow.random.nextFloat(0.2f));

        drawRect(0, 0, textureSize, textureSize, panelColor);
        draw3DDarkenFrameRect(0, 0, offset, offset, panelEdgeSize, (0.9f + AppWindow.random.nextFloat(0.1f)), true); // left and right
        generateComputerComponentScreen(offset, 0, textureSize, offset, panelInsideEdgeSize); // front and back
        draw3DDarkenFrameRect(offset, 0, textureSize, offset, panelEdgeSize, (0.9f + AppWindow.random.nextFloat(0.1f)), true); // top and bottom

        // set the emissive
        emissiveFactor = new RagPoint(1.0f, 1.0f, 1.0f);

        // finish with the metallic-roughness
        createMetallicRoughnessMap(0.75f, 0.4f);
    }
}
