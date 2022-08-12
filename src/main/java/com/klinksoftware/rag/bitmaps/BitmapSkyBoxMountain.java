package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.utility.*;

@BitmapInterface
public class BitmapSkyBoxMountain extends BitmapBase {

    public BitmapSkyBoxMountain() {
        super();

        hasNormal = true;
        hasMetallicRoughness = true;
        hasEmissive = false;
        hasAlpha = false;
    }

    private void generateClouds(int lft, int top, int rgt, int bot, boolean allSides, RagColor cloudColor) {
        int n, x, y, x2, y2, xsz, ysz;
        int wid, high, quarterWid, quarterHigh;

        wid = rgt - lft;
        high = bot - top;
        quarterWid = wid / 4;
        quarterHigh = high / 4;

        createPerlinNoiseData(32, 32);
        createNormalNoiseData((2.0f + AppWindow.random.nextFloat(0.3f)), (0.3f + AppWindow.random.nextFloat(0.2f)));

        // random clouds
        for (n = 0; n != 20; n++) {
            xsz = quarterWid + AppWindow.random.nextInt(quarterWid);
            ysz = quarterHigh + quarterWid + AppWindow.random.nextInt(quarterHigh);

            x = quarterWid + ((lft + AppWindow.random.nextInt(wid)) - (xsz / 2));
            y = top - (ysz / 2);

            // the top
            drawOval(x, y, (x + xsz), (y + ysz), 0.0f, 1.0f, AppWindow.random.nextFloat(0.1f), AppWindow.random.nextFloat(0.1f), 0, 0.0f, cloudColor, cloudColor, 0.5f, false, true, 0.9f, 1.0f);

            //this.drawOval(x,y,(x+xsz),(y+ysz),cloudColor,null);
            if (x < lft) {
                x2 = rgt + x;
                //this.drawOval(x2,y,(x2+xsz),(y+ysz),cloudColor,null);
            }
            if ((x + xsz) > rgt) {
                x2 = lft - ((x + xsz) - rgt);
                //this.drawOval(x2,y,(x2+xsz),(y+ysz),cloudColor,null);
            }

            if (!allSides) {
                continue;
            }

            // the bottom
            y = bot - (ysz / 2);

            //this.drawOval(x,y,(x+xsz),(y+ysz),cloudColor,null);
            if (x < lft) {
                x2 = rgt + x;
                //this.drawOval(x2,y,(x2+xsz),(y+ysz),cloudColor,null);
            }
            if ((x + xsz) > rgt) {
                x2 = lft - ((x + xsz) - rgt);
                //this.drawOval(x2,y,(x2+xsz),(y+ysz),cloudColor,null);
            }

            // the left
            x = lft - (xsz / 2);
            y = top + ((top + AppWindow.random.nextInt(high)) - (ysz / 2));

            //this.drawOval(x,y,(x+xsz),(y+ysz),cloudColor,null);
            if (y < bot) {
                y2 = bot + y;
                //this.drawOval(x,y2,(x+xsz),(y2+ysz),cloudColor,null);
            }
            if ((y + ysz) > bot) {
                y2 = top - ((y + ysz) - bot);
                //this.drawOval(x,y2,(x+xsz),(y2+ysz),cloudColor,null);
            }

            // the right
            x = rgt - (xsz / 2);

            //this.drawOval(x,y,(x+xsz),(y+ysz),cloudColor,null);
            if (y < bot) {
                y2 = bot + y;
                //this.drawOval(x,y2,(x+xsz),(y2+ysz),cloudColor,null);
            }
            if ((y + ysz) > bot) {
                y2 = top - ((y + ysz) - bot);
                //this.drawOval(x,y2,(x+xsz),(y2+ysz),cloudColor,null);
            }

        }
    }

    @Override
    public void generateInternal() {
        int mx, my, qx, rangeY;
        RagColor cloudColor, skyColor, mountainColor, groundColor;

        mx = textureSize / 2;
        my = textureSize / 2;
        qx = textureSize / 4;

        cloudColor = new RagColor(1.0f, 1.0f, 1.0f);
        skyColor = new RagColor(0.1f, 0.95f, 1.0f);
        mountainColor = new RagColor(0.65f, 0.35f, 0.0f);
        groundColor = new RagColor(0.1f, 1.0f, 0.1f);

        // top and bottom
        drawRect(0, my, qx, textureSize, skyColor);
        generateClouds(0, my, qx, textureSize, true, cloudColor);

        this.drawRect(qx, my, mx, textureSize, groundColor);

        /*
        // side
        this.drawVerticalGradient(0,0,this.bitmapCanvas.width,my,skyColor,this.darkenColor(skyColor,0.5));
        generateClouds(0,0,textureSize,my,false,cloudColor);
        this.blur(0,my,this.bitmapCanvas.width,this.bitmapCanvas.height,3,true);

        rangeY=this.generateMountainsBuildRange(0,Math.trunc(my*0.75),this.bitmapCanvas.width,8);
        this.generateMountainsDraw(0,0,this.bitmapCanvas.width,my,rangeY,this.darkenColor(mountainColor,0.8));

        rangeY=this.generateMountainsBuildRange(Math.trunc(my*0.25),my,this.bitmapCanvas.width,5);
        this.generateMountainsDraw(0,0,this.bitmapCanvas.width,my,rangeY,mountainColor);

        this.drawVerticalGradient(0,Math.trunc(my*0.9),this.bitmapCanvas.width,my,this.darkenColor(groundColor,0.8),groundColor);
         */
    }
}
