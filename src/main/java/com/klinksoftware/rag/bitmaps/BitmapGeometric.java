package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

public class BitmapGeometric extends BitmapBase {

    private static final int SNAKE_PIXEL_SIZE = 32;

    public BitmapGeometric() {
        super();

        hasNormal = true;
        hasMetallicRoughness = true;
        hasEmissive = false;
        hasAlpha = false;
    }

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

    private boolean floodFill(byte[] drawBytes, int x, int y, int recurseCount)    {
        if (recurseCount > SNAKE_PIXEL_SIZE) {
            return (false);
        }
        if (drawBytes[(y * SNAKE_PIXEL_SIZE) + x] != 0) {
            return (true);
        }

        drawBytes[(y * SNAKE_PIXEL_SIZE) + x] = 2;

        if (y>0) {
            if (drawBytes[((y - 1) * SNAKE_PIXEL_SIZE) + x] == 0) {
                if (!floodFill(drawBytes, x, (y - 1), (recurseCount + 1))) {
                    return (false);
                }
            }
        }
        if (y < (SNAKE_PIXEL_SIZE - 1)) {
            if (drawBytes[((y + 1) * SNAKE_PIXEL_SIZE) + x] == 0) {
                if (!floodFill(drawBytes, x, (y + 1), (recurseCount + 1))) {
                    return (false);
                }
            }
        }
        if (x>0) {
            if (drawBytes[(y * SNAKE_PIXEL_SIZE) + (x - 1)] == 0) {
                if (!floodFill(drawBytes, (x - 1), y, (recurseCount + 1))) {
                    return (false);
                }
            }
        }
        if (x < (SNAKE_PIXEL_SIZE - 1)) {
            if (drawBytes[(y * SNAKE_PIXEL_SIZE) + (x + 1)] == 0) {
                if (!floodFill(drawBytes, (x + 1), y, (recurseCount + 1))) {
                    return (false);
                }
            }
        }

        return(true);
    }

    @Override
    public void generateInternal() {
        int x, y, idx, div;
        int maxPathCount, branchCount;
        byte[] drawBytes, drawBackup;
        RagColor color;

        color = getRandomColor();
        drawRect(0, 0, textureSize, textureSize, color);

        // recurse snake draw
        drawBytes = new byte[SNAKE_PIXEL_SIZE * SNAKE_PIXEL_SIZE];
        maxPathCount = 20 + AppWindow.random.nextInt(20);
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
                if (!floodFill(drawBytes, x, y, 0)) {
                    System.arraycopy(drawBackup, 0, drawBytes, 0, drawBytes.length);
                }
            }
        }

        // turn into color bitmap
        idx = 0;
        div = textureSize / SNAKE_PIXEL_SIZE;

        color = getRandomColor();

        for (y = 0; y != textureSize; y++) {
            for (x = 0; x != textureSize; x++) {
                if (drawBytes[((y / div) * SNAKE_PIXEL_SIZE) + (x / div)] == 1) {
                    colorData[idx] = color.r;
                    colorData[idx + 1] = color.g;
                    colorData[idx + 2] = color.b;

                    normalData[idx] = -0.65f;
                    normalData[idx + 1] = 0.02f;
                    normalData[idx + 2] = 0.75f;
                }

                idx += 4;
            }

            adjustColor(color, (AppWindow.random.nextBoolean() ? 0.05f : 1.05f));
        }

        // finish with metallic roughness
        createMetallicRoughnessMap((0.1f + AppWindow.random.nextFloat(0.6f)), 0.5f);
    }

}
