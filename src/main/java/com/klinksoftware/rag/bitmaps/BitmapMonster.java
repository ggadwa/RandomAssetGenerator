package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.utility.*;

public class BitmapMonster extends BitmapBase
{
    public BitmapMonster() {
        super();

        hasNormal=true;
        hasMetallicRoughness=true;
        hasEmissive=false;
        hasAlpha=false;
    }

    private void generateFaceChunkEye(int x, int y, RagColor eyeColor) {
        drawOval(x, y, (x + 40), (y + 15), 0.0f, 1.0f, 0.0f, 0.0f, 2, 0.5f, this.COLOR_WHITE, this.COLOR_BLACK, 0.5f, false, false, 1.0f, 0.0f);
        drawOval((x + 15), (y + 1), (x + 25), (y + 14), 0.0f, 1.0f, 0.0f, 0.0f, 2, 0.5f, eyeColor, null, 0.5f, false, false, 1.0f, 0.0f);
    }

    private void generateAddFace() {
        RagColor eyeColor;

        eyeColor = this.getRandomColor();

        drawOval(415, 235, 505, 245, 0.0f, 1.0f, 0.0f, 0.0f, 2, 0.5f, this.COLOR_BLACK, this.COLOR_BLACK, 0.5f, false, false, 1.0f, 0.0f);

        generateFaceChunkEye(415, 295, eyeColor);
        generateFaceChunkEye(465, 295, eyeColor);
    }

    @Override
    public void generateInternal() {
        BitmapFur bitmapFur;

        bitmapFur = new BitmapFur();
        bitmapFur.generate();

        blockQuarterCopy(bitmapFur.colorData, colorData, 0, 0);
        blockQuarterCopy(bitmapFur.normalData, normalData, 0, 0);
        blockQuarterCopy(bitmapFur.metallicRoughnessData, metallicRoughnessData, 0, 0);

        //generateAddFace();
    }
}
