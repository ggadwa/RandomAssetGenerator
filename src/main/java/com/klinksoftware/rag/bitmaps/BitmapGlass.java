package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;

@BitmapInterface
public class BitmapGlass extends BitmapBase
{
    public BitmapGlass(int textureSize) {
        super(textureSize);

        hasNormal=true;
        hasMetallicRoughness=true;
        hasEmissive=false;
        hasAlpha=true;
    }

        //
        // glass bitmaps
        //

    @Override
    public void generateInternal() {
        int n, x, y, x2, y2, lineCount;

        // default glass to dull color
        drawRect(0, 0, textureSize, textureSize, getRandomColorDull(0.5f));

        // back noise and blur
        drawStaticNoiseRect(0, 0, textureSize, textureSize, 0.95f, 0.99f);
        blur(colorData, 0, 0, textureSize, textureSize, 10, false);

        // reflection lines in alpha
        setImageAlpha(0.75f);

        lineCount=5+AppWindow.random.nextInt(20);

        for (n = 0; n != lineCount; n++) {
            x = AppWindow.random.nextInt(textureSize / 2);
            x2=(x+1)+AppWindow.random.nextInt(textureSize-x);
            y=textureSize-AppWindow.random.nextInt(textureSize-(x2-x));
            y2=y-((x2-x)/2);

            drawLineAlpha(x, y, x2, y2, (0.7f + AppWindow.random.nextFloat(0.2f)));
            drawLineNormal(x,y,x2,y2,((n&0x1)==0x0)?NORMAL_BOTTOM_RIGHT_45:NORMAL_TOP_LEFT_45);
        }

        // blur the lines
        blurAlpha(colorData, 0, 0, textureSize, textureSize, 5, false);

        // finish with the metallic-roughness
        createMetallicRoughnessMap(0.5f,0.6f);
    }
}
