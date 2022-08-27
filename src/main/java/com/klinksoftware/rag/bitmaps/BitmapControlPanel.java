package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

@BitmapInterface
public class BitmapControlPanel extends BitmapComputer {

    public BitmapControlPanel(int textureSize) {
        super(textureSize);

        hasNormal = true;
        hasMetallicRoughness = true;
        hasEmissive = true;
        hasAlpha = false;
    }

    @Override
    public void generateInternal() {
        int panelEdgeSize;
        RagColor panelColor;

        panelColor = getRandomColor();
        panelEdgeSize = (textureSize / 150) + AppWindow.random.nextInt(textureSize / 150);

        // draw the panel
        drawRect(0, 0, textureSize, textureSize, panelColor);
        generateComputerComponents(panelColor, panelEdgeSize, true);

        // set the emissive
        emissiveFactor = new RagPoint(1.0f, 1.0f, 1.0f);

        // finish with the metallic-roughness
        createMetallicRoughnessMap(0.75f, 0.4f);
    }
}
