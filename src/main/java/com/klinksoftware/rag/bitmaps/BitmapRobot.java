package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.utility.*;

public class BitmapRobot extends BitmapBase {

    public BitmapRobot() {
        super();

        hasNormal = true;
        hasMetallicRoughness = true;
        hasEmissive = false;
        hasAlpha = false;
    }

    private void generateFaceChunkEye(int x, int y, RagColor eyeColor) {
        this.drawRect(x, y, (x + 30), (y + 20), eyeColor);
        this.draw3DFrameRect(x, y, (x + 30), (y + 20), 5, COLOR_BLACK, true);
        //drawOval(x, y, (x + 30), (y + 20), 0.0f, 1.0f, 0.0f, 0.0f, 2, 0.5f, this.COLOR_WHITE, this.COLOR_BLACK, 0.5f, false, false, 1.0f, 0.0f);
        //drawOval((x + 10), (y + 1), (x + 20), (y + 19), 0.0f, 1.0f, 0.0f, 0.0f, 2, 0.5f, eyeColor, null, 0.5f, false, false, 1.0f, 0.0f);
    }

    private void generateAddFace() {
        RagColor eyeColor;

        eyeColor = this.getRandomColor();

        drawOval(440, 400, 520, 425, 0.0f, 1.0f, 0.0f, 0.0f, 2, 0.5f, this.COLOR_BLACK, this.COLOR_BLACK, 0.5f, false, false, 1.0f, 0.0f);

        generateFaceChunkEye(440, 450, eyeColor);
        generateFaceChunkEye(490, 450, eyeColor);
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
