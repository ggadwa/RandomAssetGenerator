package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

@BitmapInterface
public class BitmapGeometric extends BitmapBase {

    private static final int SNAKE_PIXEL_SIZE = 32;

    public BitmapGeometric(int textureSize) {
        super(textureSize);

        hasNormal = true;
        hasMetallicRoughness = true;
        hasEmissive = false;
        hasAlpha = false;
    }

    //
    // snake geometric
    //
    private void recurseSnakeDraw(byte[] drawBytes, int x, int y, int dx, int dy, int branchCount, int pathCount, int maxPathCount) {
        int n;
        int offset;

        // fill in a line of cubes for the count
        for (n = 0; n != pathCount; n++) {
            if (n != 0) {         // this is so x,y end at the end of the line, not after it
                x += dx;
                y += dy;
            }

            offset = (y * SNAKE_PIXEL_SIZE) + x;
            if ((x < 0) || (y < 0) || (x >= SNAKE_PIXEL_SIZE) || (y >= SNAKE_PIXEL_SIZE) || (drawBytes[offset] != 0)) {
                return;
            }

            drawBytes[offset] = 1;
        }

        // branch
        branchCount--;
        if (branchCount <= 0) {
            return;
        }

        recurseSnakeDraw(drawBytes, x, (y - 1), 0, -1, branchCount, (1 + AppWindow.random.nextInt(maxPathCount)), maxPathCount);
        recurseSnakeDraw(drawBytes, x, (y + 1), 0, 1, branchCount, (1 + AppWindow.random.nextInt(maxPathCount)), maxPathCount);
        recurseSnakeDraw(drawBytes, (x - 1), y, -1, 0, branchCount, (1 + AppWindow.random.nextInt(maxPathCount)), maxPathCount);
        recurseSnakeDraw(drawBytes, (x + 1), y, 1, 0, branchCount, (1 + AppWindow.random.nextInt(maxPathCount)), maxPathCount);
    }

    private boolean snakeFloodFill(byte[] drawBytes, int x, int y, int recurseCount) {
        if (recurseCount > SNAKE_PIXEL_SIZE) {
            return (false);
        }
        if (drawBytes[(y * SNAKE_PIXEL_SIZE) + x] != 0) {
            return (true);
        }

        drawBytes[(y * SNAKE_PIXEL_SIZE) + x] = 2;

        if (y>0) {
            if (drawBytes[((y - 1) * SNAKE_PIXEL_SIZE) + x] == 0) {
                if (!snakeFloodFill(drawBytes, x, (y - 1), (recurseCount + 1))) {
                    return (false);
                }
            }
        }
        if (y < (SNAKE_PIXEL_SIZE - 1)) {
            if (drawBytes[((y + 1) * SNAKE_PIXEL_SIZE) + x] == 0) {
                if (!snakeFloodFill(drawBytes, x, (y + 1), (recurseCount + 1))) {
                    return (false);
                }
            }
        }
        if (x>0) {
            if (drawBytes[(y * SNAKE_PIXEL_SIZE) + (x - 1)] == 0) {
                if (!snakeFloodFill(drawBytes, (x - 1), y, (recurseCount + 1))) {
                    return (false);
                }
            }
        }
        if (x < (SNAKE_PIXEL_SIZE - 1)) {
            if (drawBytes[(y * SNAKE_PIXEL_SIZE) + (x + 1)] == 0) {
                if (!snakeFloodFill(drawBytes, (x + 1), y, (recurseCount + 1))) {
                    return (false);
                }
            }
        }

        return(true);
    }

    public void generateSingleSnakeGeometric(float normalFactor) {
        int x, y, idx, div;
        int maxPathCount, branchCount;
        float fh, fv;
        byte[] drawBytes, drawBackup;
        RagColor colorLft, colorRgt, colorTop, colorBot;

        // recurse snake draw
        drawBytes = new byte[SNAKE_PIXEL_SIZE * SNAKE_PIXEL_SIZE];
        maxPathCount = 25 + AppWindow.random.nextInt(25);
        branchCount = 10 + AppWindow.random.nextInt(10);

        recurseSnakeDraw(drawBytes, (SNAKE_PIXEL_SIZE / 2), (SNAKE_PIXEL_SIZE / 2), 0, 0, branchCount, 0, maxPathCount);

        // fill in the spots
        drawBackup = new byte[SNAKE_PIXEL_SIZE * SNAKE_PIXEL_SIZE];

        for (y = 0; y != SNAKE_PIXEL_SIZE; y++) {
            for (x = 0; x != SNAKE_PIXEL_SIZE; x++) {
                if (drawBytes[(y * SNAKE_PIXEL_SIZE) + x] != 0) {
                    continue;
                }

                System.arraycopy(drawBytes, 0, drawBackup, 0, drawBytes.length);
                if (!snakeFloodFill(drawBytes, x, y, 0)) {
                    System.arraycopy(drawBackup, 0, drawBytes, 0, drawBytes.length);
                }
            }
        }

        // turn into color bitmap
        idx = 0;
        div = textureSize / SNAKE_PIXEL_SIZE;

        colorLft = getRandomColor();
        colorRgt = getRandomColor();
        colorTop = getRandomColor();
        colorBot = getRandomColor();

        for (y = 0; y != textureSize; y++) {
            for (x = 0; x != textureSize; x++) {
                if (drawBytes[((y / div) * SNAKE_PIXEL_SIZE) + (x / div)] == 1) {

                    fh = (float) (textureSize - x);
                    fv = (float) (textureSize - y);

                    colorData[idx] = (((colorLft.r * (1.0f - fh)) + (colorRgt.r * fh)) * 0.5f) + (((colorTop.r * (1.0f - fv)) + (colorBot.r * fv)) * 0.5f);
                    colorData[idx + 1] = (((colorLft.g * (1.0f - fh)) + (colorRgt.g * fh)) * 0.5f) + (((colorTop.g * (1.0f - fv)) + (colorBot.g * fv)) * 0.5f);
                    colorData[idx + 2] = (((colorLft.b * (1.0f - fh)) + (colorRgt.b * fh)) * 0.5f) + (((colorTop.b * (1.0f - fv)) + (colorBot.b * fv)) * 0.5f);

                    normalData[idx] = 0.65f * normalFactor;
                    normalData[idx + 1] = 0.02f * normalFactor;
                    normalData[idx + 2] = 0.75f * normalFactor;
                }

                idx += 4;
            }
        }
    }

    @Override
    public void generateInternal() {
        int n, geoCount;
        float normalFactor;
        RagColor color;

        // background
        color = getRandomColor();
        drawRect(0, 0, textureSize, textureSize, color);

        createNormalNoiseData(3.0f, 0.4f);
        drawNormalNoiseRect(0, 0, textureSize, textureSize);

        // random geometrics
        geoCount = 1 + AppWindow.random.nextInt(3);
        normalFactor = 1.0f;

        for (n = 0; n != geoCount; n++) {
            generateSingleSnakeGeometric(normalFactor);
            normalFactor = -normalFactor;
        }

        // frame
        draw3DDarkenFrameRect(0, 0, textureSize, textureSize, (2 + AppWindow.random.nextInt(8)), (0.6f + AppWindow.random.nextFloat(0.2f)), true);

        // finish with metallic roughness
        createMetallicRoughnessMap((0.1f + AppWindow.random.nextFloat(0.6f)), 0.5f);
    }

}
