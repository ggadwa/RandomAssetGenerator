package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.utility.*;

@BitmapInterface
public class BitmapRobot extends BitmapBase {

    public BitmapRobot() {
        super();

        hasNormal = true;
        hasMetallicRoughness = true;
        hasEmissive = false;
        hasAlpha = false;
    }

    private void generateFaceChunkEye(int x, int y, RagColor eyeColor) {
        drawRect(x, y, (x + 15), (y + 10), eyeColor);
        draw3DFrameRect(x, y, (x + 15), (y + 10), 2, COLOR_BLACK, true);
    }

    private void generateAddFace() {
        RagColor eyeColor;

        eyeColor = this.getRandomColor();

        drawRect(415, 355, 445, 375, COLOR_WHITE);
        draw3DFrameRect(415, 355, 445, 375, 2, COLOR_BLACK, true);

        generateFaceChunkEye(412, 400, eyeColor);
        generateFaceChunkEye(433, 400, eyeColor);
    }

    private void generateSingleChunk(int x, int y) {
        BitmapBase bitmap;

        bitmap = new BitmapMetal();
        bitmap.generate();

        blockQuarterCopy(bitmap.colorData, colorData, x, y);
        blockQuarterCopy(bitmap.normalData, normalData, x, y);
        blockQuarterCopy(bitmap.metallicRoughnessData, metallicRoughnessData, x, y);
    }

    @Override
    public void generateInternal() {
        generateSingleChunk(0, 0);
        generateSingleChunk(256, 0);
        generateSingleChunk(0, 256);
        generateSingleChunk(256, 256);

        generateAddFace();
    }
}
