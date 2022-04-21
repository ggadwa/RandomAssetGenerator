package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

public class BitmapControlPanel extends BitmapComputer {

    public BitmapControlPanel() {
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
        RagColor panelColor, panelInsideColor;

        offset = textureSize / 2;
        panelEdgeSize = 2 + AppWindow.random.nextInt(4);
        panelInsideEdgeSize = 2 + AppWindow.random.nextInt(3);

        panelColor = getRandomGray(0.6f, 0.8f);
        panelInsideColor = adjustColor(panelColor, 1.1f);

        // this is a collection of plates that are
        // used to wrap the object around cubes
        drawRect(0, 0, textureSize, textureSize, panelColor);

        generateComputerComponents(0, 0, offset, offset, panelInsideColor, panelInsideEdgeSize, true);             // left and right
        generateComputerComponents(offset, 0, textureSize, offset, panelInsideColor, panelInsideEdgeSize, true);   // front and back
        draw3DFrameRect(0, offset, offset, textureSize, panelEdgeSize, panelColor, true);                     // top and bottom

        // set the emissive
        emissiveFactor = new RagPoint(1.0f, 1.0f, 1.0f);

        // finish with the metallic-roughness
        createMetallicRoughnessMap(0.75f, 0.4f);
    }
}
