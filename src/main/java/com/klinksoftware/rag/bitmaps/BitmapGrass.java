package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

public class BitmapGrass extends BitmapDirt {
    public BitmapGrass() {
        super();

        hasNormal=true;
        hasMetallicRoughness=true;
        hasEmissive=false;
        hasAlpha=false;
    }

    @Override
    public void generateInternal() {
        int x, x2, y, halfSize, xMove, xSkip;
        RagColor grassColor, lineColor;

        // use dirt for background
        super.generateInternal();

        // grass
        grassColor = new RagColor((0.2f + AppWindow.random.nextFloat(0.2f)), (0.5f + AppWindow.random.nextFloat(0.5f)), (0.2f + AppWindow.random.nextFloat(0.2f)));

        halfSize = textureSize / 2;
        xMove = textureSize / 10;
        xSkip = 1 + AppWindow.random.nextInt(4);

        for (x = 0; x < textureSize; x += xSkip) {
            lineColor = adjustColorRandom(grassColor, 0.7f, 1.3f);

            // line half from top
            x2 = x + AppWindow.random.nextInt(xMove * 2) - xMove;
            y = halfSize + AppWindow.random.nextInt(halfSize);
            drawLineColor(x, 0, x2, (y + 5), lineColor);
            drawLineNormal(x, 0, x2, (y + 5), ((x & 0x1) == 0x0) ? NORMAL_BOTTOM_RIGHT_45 : NORMAL_TOP_LEFT_45);

            // line half from bottom
            x2 = x + AppWindow.random.nextInt(xMove * 2) - xMove;
            y = textureSize - (halfSize + AppWindow.random.nextInt(halfSize));
            drawLineColor(x2, (y - 5), x, textureSize, lineColor);
            drawLineNormal(x2, (y - 5), x, textureSize, ((x & 0x1) == 0x0) ? NORMAL_TOP_LEFT_45 : NORMAL_BOTTOM_RIGHT_45);
        }

        // finish with the metallic-roughness
        createMetallicRoughnessMap(0.5f,0.4f);
    }

}
