package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.utility.*;

@BitmapInterface
public class BitmapMonster extends BitmapBase
{
    public BitmapMonster(int textureSize) {
        super(textureSize);

        hasNormal=true;
        hasMetallicRoughness=true;
        hasEmissive=false;
        hasAlpha=false;
    }

    private void generateFaceChunkEye(int x, int y, RagColor eyeColor) {
        drawOval(x, y, (x + 30), (y + 20), 0.0f, 1.0f, 0.0f, 0.0f, 2, 0.5f, COLOR_WHITE, 0.5f, false, false, 1.0f, 0.0f);
        drawFrameOval(x, y, (x + 30), (y + 20), 0.0f, 0.0f, COLOR_BLACK);

        drawOval((x + 10), (y + 1), (x + 20), (y + 19), 0.0f, 1.0f, 0.0f, 0.0f, 2, 0.5f, eyeColor, 0.5f, false, false, 1.0f, 0.0f);
    }

    private void generateAddFace() {
        RagColor eyeColor;

        eyeColor = this.getRandomColor();

        drawOval(320, 355, 375, 380, 0.0f, 1.0f, 0.0f, 0.0f, 2, 0.5f, COLOR_BLACK, 0.5f, false, false, 1.0f, 0.0f);
        drawFrameOval(320, 355, 375, 380, 0.0f, 0.0f, COLOR_BLACK);

        generateFaceChunkEye(320, 400, eyeColor);
        generateFaceChunkEye(355, 400, eyeColor);
    }

    private void generateSingleChunk(int x, int y) {
        BitmapBase bitmap;

        switch (AppWindow.random.nextInt(3)) {
            case 0:
                bitmap = new BitmapFur(textureSize);
                break;
            case 1:
                bitmap = new BitmapScale(textureSize);
                break;
            default:
                bitmap = new BitmapOrganic(textureSize);
                break;
        }

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
