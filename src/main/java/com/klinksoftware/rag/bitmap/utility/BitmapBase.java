package com.klinksoftware.rag.bitmap.utility;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.utility.*;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class BitmapBase
{

    public static final int DEFAULT_TEXTURE_SIZE = 1024;

    public static final RagPoint NORMAL_CLEAR=new RagPoint(0.0f,0.0f,1.0f);

    public static final RagPoint NORMAL_LEFT_45=new RagPoint(-0.65f,0.02f,0.75f);
    public static final RagPoint NORMAL_RIGHT_45=new RagPoint(0.65f,-0.02f,0.75f);
    public static final RagPoint NORMAL_TOP_45=new RagPoint(-0.02f,0.65f,0.75f);
    public static final RagPoint NORMAL_BOTTOM_45=new RagPoint(0.02f,-0.65f,0.75f);

    public static final RagPoint NORMAL_LEFT_10=new RagPoint(-0.1f,0.0f,0.90f);
    public static final RagPoint NORMAL_RIGHT_10=new RagPoint(0.1f,0.0f,0.90f);
    public static final RagPoint NORMAL_TOP_10=new RagPoint(0.0f,0.1f,0.90f);
    public static final RagPoint NORMAL_BOTTOM_10=new RagPoint(0.0f,-0.1f,0.90f);

    public static final RagPoint NORMAL_TOP_LEFT_45=new RagPoint(-0.48f,0.48f,0.72f);
    public static final RagPoint NORMAL_TOP_RIGHT_45=new RagPoint(0.48f,0.48f,0.72f);
    public static final RagPoint NORMAL_BOTTOM_LEFT_45=new RagPoint(-0.48f,-0.48f,0.72f);
    public static final RagPoint NORMAL_BOTTOM_RIGHT_45=new RagPoint(0.48f,-0.48f,0.72f);

    public static final RagColor COLOR_BLACK=new RagColor(0.0f,0.0f,0.0f);
    public static final RagColor COLOR_WHITE=new RagColor(1.0f,1.0f,1.0f);

    public static final float[][] COLOR_PRIMARY_LIST = {
        {0.7f, 0.0f, 0.0f}, // red
        {0.0f, 0.7f, 0.0f}, // green
        {0.0f, 0.0f, 0.7f}, // blue
        {0.7f, 0.7f, 0.0f}, // yellow
        {0.8f, 0.0f, 0.8f}, // purple
        {0.8f, 0.8f, 0.0f}, // light blue
        {0.0f, 0.9f, 0.6f}, // sea green
        {1.0f, 0.4f, 0.0f}, // orange
        {0.7f, 0.4f, 0.0f}, // brown
        {0.8f, 0.6f, 0.0f}, // gold
        {0.8f, 0.6f, 0.8f}, // lavender
        {1.0f, 0.8f, 0.8f}, // pink
        {0.6f, 0.9f, 0.0f}, // lime
        {0.2f, 0.5f, 0.0f}, // tree green
        {0.5f, 0.5f, 0.5f}, // gray
        {0.6f, 0.0f, 0.9f}, // dark purple
        {0.0f, 0.3f, 0.5f}, // slate blue
        {0.9f, 0.6f, 0.4f}, // peach
        {0.9f, 0.0f, 0.4f}, // muave
        {0.8f, 0.5f, 0.5f} // dull red
    };
    public static final float[][] COLOR_TINT_LIST = {
        {1.0f, 0.0f, 0.0f},
        {0.0f, 1.0f, 0.0f},
        {0.0f, 0.0f, 1.0f},
        {1.0f, 1.0f, 0.0f},
        {0.0f, 1.0f, 1.0f},
        {1.0f, 0.0f, 1.0f}
    };

    protected int textureSize;
    protected boolean hasNormal, hasMetallicRoughness, hasEmissive, hasAlpha;
    protected RagPoint specularFactor, emissiveFactor;
    protected float[] colorData, normalData, metallicRoughnessData, emissiveData;
    protected float[] perlinNoiseColorFactor, noiseNormals, blurData;

    public BitmapBase() {
        textureSize = DEFAULT_TEXTURE_SIZE;

        // will be reset in child classes
        hasNormal=true;
        hasMetallicRoughness=true;
        hasEmissive=false;
        hasAlpha=false;

        specularFactor=new RagPoint(5.0f,5.0f,5.0f);
        emissiveFactor=new RagPoint(1.0f,1.0f,1.0f);

        // the color, normal, metallic-roughness, and emissive
        colorData = null;
        normalData=null;
        metallicRoughnessData=null;
        emissiveData=null;

        // noise
        perlinNoiseColorFactor=null;
        noiseNormals = null;

        // blur
        blurData = null;
    }

        //
        // getters
        //

    public boolean hasAlpha() {
        return(hasAlpha);
    }

    public boolean hasNormal() {
        return (hasNormal);
    }

    public boolean hasMetallicRoughness() {
        return (hasMetallicRoughness);
    }

    public boolean hasEmissive() {
        return (hasEmissive);
    }

    public int getTextureSize() {
        return(textureSize);
    }

        //
        // colors
        //

    protected RagColor getRandomColor() {
        int idx;
        float midPoint, darken;
        float[] col;

        // random color
        idx=AppWindow.random.nextInt(COLOR_PRIMARY_LIST.length);
        col=COLOR_PRIMARY_LIST[idx];

        // every once and a while, a pastel
        if (AppWindow.random.nextFloat() < 0.2f) {
            midPoint=(col[0]+col[1]+col[2])*0.33f;
            return(new RagColor((col[0]+(midPoint-col[0])*0.8f),(col[1]+(midPoint-col[1])*0.8f),(col[2]+(midPoint-col[2])*0.8f)));
        }

        // every once and a while, a darker color
        if (AppWindow.random.nextFloat() < 0.2f) {
            darken = 1.0f - AppWindow.random.nextFloat(0.2f);
            return (new RagColor((col[0] * darken), (col[1] * darken), (col[2] * darken)));
        }

        // regular color
        return (new RagColor(col[0], col[1], col[2]));
    }

    protected RagColor getRandomColorSkipColor(RagColor[] skipColors) {
        int n;
        boolean hit;
        RagColor color;

        while (true) {
            color = getRandomColor();

            hit = false;
            for (n = 0; n != skipColors.length; n++) {
                if (color.equals(skipColors[n])) {
                    hit = true;
                    break;
                }
            }

            if (!hit) {
                return (color);
            }
        }
    }

    protected RagColor getRandomTintColor() {
        float[] col;

        col = COLOR_PRIMARY_LIST[AppWindow.random.nextInt(COLOR_TINT_LIST.length)];
        return (new RagColor(col[0], col[1], col[2]));
    }

    protected RagColor getRandomColorDull(float dullFactor) {
        float midPoint;
        RagColor color;

        color=getRandomColor();

            // find the midpoint

        midPoint=(color.r+color.g+color.b)*0.33f;

            // move towards it

        color.r=color.r+(midPoint-color.r)*dullFactor;
        color.g=color.g+(midPoint-color.g)*dullFactor;
        color.b=color.b+(midPoint-color.b)*dullFactor;

        return(color);
    }

    protected RagColor dullColor(RagColor color, float dullFactor) {
        float midPoint;

            // find the midpoint

        midPoint=(color.r+color.g+color.b)*0.33f;

            // move towards it

        return(new RagColor((color.r+(midPoint-color.r)*dullFactor),(color.g+(midPoint-color.g)*dullFactor),(color.b+(midPoint-color.b)*dullFactor)));
    }

    protected RagColor getRandomGrayColor(float minFactor, float maxFactor) {
        float col;

        col=minFactor+(AppWindow.random.nextFloat()*(maxFactor-minFactor));
        return(new RagColor(col,col,col));
    }

    protected RagColor getRandomWoodColor() {
        float f;

        if (AppWindow.random.nextBoolean()) {
            return (new RagColor((0.4f + AppWindow.random.nextFloat(0.2f)), (0.15f + AppWindow.random.nextFloat(0.2f)), AppWindow.random.nextFloat(0.1f)));
        } else {
            f = (0.15f + AppWindow.random.nextFloat(0.2f));
            return (new RagColor((0.4f + AppWindow.random.nextFloat(0.2f)), f, f));
        }
    }

    protected RagColor getRandomMetalColor() {
        float f;

        switch (AppWindow.random.nextInt(4)) {
            case 0: // blue-ish
                return (new RagColor(AppWindow.random.nextFloat(0.3f), AppWindow.random.nextFloat(0.1f), (0.5f + AppWindow.random.nextFloat(0.5f))));
            case 1: // copper-ish
                return (new RagColor((0.65f + AppWindow.random.nextFloat(0.2f)), (0.35f + AppWindow.random.nextFloat(0.1f)), (0.1f + AppWindow.random.nextFloat(0.1f))));
            case 2: // gold-ish
                return (new RagColor((0.8f + AppWindow.random.nextFloat(0.2f)), (0.6f + AppWindow.random.nextFloat(0.2f)), AppWindow.random.nextFloat(0.1f)));
            default: // silver-ish
                f = (0.5f + AppWindow.random.nextFloat(0.2f));
                return (new RagColor(f, f, (f + AppWindow.random.nextFloat(0.2f))));
        }
    }

    protected RagColor adjustColor(RagColor color, float factor) {
        return(new RagColor((color.r*factor),(color.g*factor),(color.b*factor)));
    }

    protected RagColor adjustColorRandom(RagColor color, float minFactor, float maxFactor) {
        float f;

        f=minFactor+(AppWindow.random.nextFloat()*(maxFactor-minFactor));
        return(new RagColor((color.r*f),(color.g*f),(color.b*f)));
    }

        //
        // block copy
        //
    protected void blockCopy(float[] fromColor, float[] fromNormal, int lft, int top, int rgt, int bot, float[] toColor, float[] toNormal) {
        int x, y, idx;

        for (y = top; y != bot; y++) {
            if ((y < 0) || (y >= textureSize)) {
                continue;
            }

            for (x = lft; x != rgt; x++) {
                if ((x < 0) || (x >= textureSize)) {
                    continue;
                }

                idx = ((y * textureSize) + x) * 4;
                if (fromColor[idx + 3] == 0.0f) {
                    toColor[idx] = fromColor[idx];
                    toColor[idx + 1] = fromColor[idx + 1];
                    toColor[idx + 2] = fromColor[idx + 2];

                    toNormal[idx] = fromNormal[idx];
                    toNormal[idx + 1] = fromNormal[idx + 1];
                    toNormal[idx + 2] = fromNormal[idx + 2];
                }
            }
        }
    }

    protected void blockQuarterCopy(float[] fromData, float[] toData, int toOffsetX, int toOffsetY) {
        int x, y, count;
        int fromIdx, toIdx;

        count = textureSize / 2;

        for (y = 0; y != count; y++) {
            toIdx = (((y + toOffsetY) * textureSize) + toOffsetX) * 4;
            fromIdx = ((y * 2) * textureSize) * 4;

            for (x = 0; x != count; x++) {
                toData[toIdx] = fromData[fromIdx];
                toData[toIdx + 1] = fromData[fromIdx + 1];
                toData[toIdx + 2] = fromData[fromIdx + 2];
                toData[toIdx + 3] = fromData[fromIdx + 3];
                toIdx += 4;
                fromIdx += 8;
            }
        }
    }

        //
        // blur
        //

    protected void blur(float[] data, int lft, int top, int rgt, int bot, int blurCount, boolean clamp) {
        int n, x, y, idx, idx2;
        int cx, cy, cxs, cxe, cys, cye, dx, dy;
        float r, g, b;

        if ((lft >= rgt) || (top >= bot)) {
            return;
        }

        if (blurData == null) {
            blurData = new float[data.length];
        }

        // blur pixels to count
        for (n = 0; n != blurCount; n++) {

            for (y=top;y!=bot;y++) {

                cys=y-1;
                cye = y + 2;

                idx = ((y * textureSize) + lft) * 4;

                for (x=lft;x!=rgt;x++) {

                    // get blur from 8 surrounding pixels
                    r = g = b = 0.0f;

                    cxs=x-1;
                    cxe=x+2;

                    for (cy=cys;cy!=cye;cy++) {

                        dy=cy;
                        if (!clamp) {
                            if (dy<top) dy=bot+(top-dy);
                            if (dy>=bot) dy=top+(dy-bot);
                        }
                        else {
                            if (dy<top) dy=top;
                            if (dy>=bot) dy=bot-1;
                        }

                        for (cx=cxs;cx!=cxe;cx++) {
                            dx = cx;
                            if (!clamp) {
                                if (dx<lft) dx=rgt+(lft-dx);
                                if (dx>=rgt) dx=lft+(dx-rgt);
                            }
                            else {
                                if (dx<lft) dx=lft;
                                if (dx>=rgt) dx=rgt-1;
                            }

                            // add up blur from the original pixels
                            idx2 = ((dy * textureSize) + dx) * 4;

                            r += data[idx2];
                            g += data[idx2 + 1];
                            b += data[idx2 + 2];
                        }
                    }

                    //idx=((y*textureSize)+x)*4;

                    blurData[idx] = r * 0.111f; // divide by 9.0f
                    blurData[idx + 1] = g * 0.111f;
                    blurData[idx + 2] = b * 0.111f;
                    idx += 4;
                }
            }

            // transfer over the changed pixels
            for (y = top; y != bot; y++) {
                idx = ((y * textureSize) + lft) * 4;
                for (x=lft;x!=rgt;x++) {
                    data[idx] = blurData[idx];
                    data[idx + 1] = blurData[idx + 1];
                    data[idx + 2] = blurData[idx + 2];
                    idx += 4;
                }
            }
        }
    }

    protected void blurAlpha(float[] data, int lft, int top, int rgt, int bot, int blurCount, boolean clamp) {
        int n, x, y, idx, cx, cy, cxs, cxe, cys, cye, dx, dy;
        float alpha;

        if ((lft >= rgt) || (top >= bot)) {
            return;
        }

        if (blurData == null) {
            blurData = new float[data.length];
        }

        // blur pixels to count
        for (n = 0; n != blurCount; n++) {

            for (y = top; y != bot; y++) {

                cys = y - 1;
                cye = y + 2;

                for (x = lft; x != rgt; x++) {

                    // get blur from 8 surrounding pixels
                    alpha = 0.0f;

                    cxs = x - 1;
                    cxe = x + 2;

                    for (cy = cys; cy != cye; cy++) {

                        dy = cy;
                        if (!clamp) {
                            if (dy < top) {
                                dy = bot + (top - dy);
                            }
                            if (dy >= bot) {
                                dy = top + (dy - bot);
                            }
                        } else {
                            if (dy < top) {
                                dy = top;
                            }
                            if (dy >= bot) {
                                dy = bot - 1;
                            }
                        }

                        for (cx = cxs; cx != cxe; cx++) {
                            if ((cy == y) && (cx == x)) {
                                continue; // ignore self
                            }
                            dx = cx;
                            if (!clamp) {
                                if (dx < lft) {
                                    dx = rgt + (lft - dx);
                                }
                                if (dx >= rgt) {
                                    dx = lft + (dx - rgt);
                                }
                            } else {
                                if (dx < lft) {
                                    dx = lft;
                                }
                                if (dx >= rgt) {
                                    dx = rgt - 1;
                                }
                            }

                            // add up blur from the
                            // original pixels
                            idx = ((dy * textureSize) + dx) * 4;
                            alpha += data[idx + 3];
                        }
                    }

                    idx = ((y * textureSize) + x) * 4;
                    blurData[idx + 3] = alpha * 0.125f;     // divide by 8.0f
                }
            }

            // transfer over the changed pixels
            for (y = top; y != bot; y++) {
                idx = ((y * textureSize) + lft) * 4;
                for (x = lft; x != rgt; x++) {
                    data[idx + 3] = blurData[idx + 3];
                    idx += 4;
                }
            }
        }
    }

        //
        // noise
        //

    private float getDotGridVector(RagPoint[][] vectors, int gridX, int gridY, int gridWid, int gridHigh, int x, int y) {
        float dx, dy;

        dx=(float)(x-(gridX*gridWid))/gridWid;
        dy=(float)(y-(gridY*gridHigh))/gridHigh;

        return((dx*vectors[gridY][gridX].x)+(dy*vectors[gridY][gridX].y));
    }

    private float lerp(float a, float b, float w) {
        double d;

        d=(Math.pow(w,2)*3)-(Math.pow(w,3)*2);
        return((float)(((1.0-d)*a)+(d*b)));
    }

    protected void createPerlinNoiseData(int gridXSize, int gridYSize) {
        int x, y, gridWid, gridHigh;
        int gridX0, gridX1, gridY0, gridY1;
        float sx, sy, ix0, ix1, n0, n1;
        RagPoint normal;
        RagPoint[][] vectors;

            // the grid
            // it must be evenly divisible

        gridWid=textureSize/gridXSize;
        gridHigh=textureSize/gridYSize;

            // noise data arrays
            // this is a single float

        perlinNoiseColorFactor=new float[textureSize*textureSize];

            // generate the random grid vectors
            // these need to wrap around so textures can tile

        vectors=new RagPoint[gridXSize+1][gridYSize+1];

        for (y=0;y!=gridYSize;y++) {

            for (x=0;x!=gridXSize;x++) {
                normal=new RagPoint(((AppWindow.random.nextFloat()*2.0f)-1.0f),((AppWindow.random.nextFloat()*2.0f)-1.0f),0.0f);
                normal.normalize2D();
                vectors[x][y]=normal;
            }

            vectors[gridXSize][y]=vectors[0][y];
        }

        for (x=0;x!=gridXSize;x++) {
            vectors[x][gridYSize]=vectors[x][0];
        }

        vectors[gridYSize][gridXSize]=vectors[0][0];      // final wrap around from top-left to top-right

            // create the noise arrays

        gridY0=0;
        normal=new RagPoint(0.0f,0.0f,0.0f);

        for (y=0;y!=textureSize;y++) {

            gridY0=y/gridHigh;
            gridY1=gridY0+1;

            for (x=0;x!=textureSize;x++) {
                gridX0=x/gridWid;
                gridX1=gridX0+1;

                    // interpolate the grid normals and take
                    // the dot product to get a -1->1 elevation

                sx=(float)(x-(gridX0*gridWid))/gridWid;
                sy=(float)(y-(gridY0*gridHigh))/gridHigh;

                n0=getDotGridVector(vectors,gridX0,gridY0,gridWid,gridHigh,x,y);
                n1=getDotGridVector(vectors,gridX1,gridY0,gridWid,gridHigh,x,y);
                ix0=lerp(n0,n1,sx);

                n0=getDotGridVector(vectors,gridX0,gridY1,gridWid,gridHigh,x,y);
                n1=getDotGridVector(vectors,gridX1,gridY1,gridWid,gridHigh,x,y);
                ix1=lerp(n0,n1,sx);

                    // turn this into a color factor for the base color

                perlinNoiseColorFactor[(y*textureSize)+x]=(lerp(ix0,ix1,sy)+1.0f)*0.5f;      // get it in 0..1
            }
        }
    }

    protected float getPerlineColorFactorForPosition(int x, int y) {
        return(perlinNoiseColorFactor[(y*textureSize)+x]);
    }

    protected void drawPerlinNoiseRect(int lft, int top, int rgt, int bot, float colorFactorMin, float colorFactorMax) {
        int x, y, idx;
        float colFactor, colorFactorAdd;

        colorFactorAdd=colorFactorMax-colorFactorMin;

        for (y=top;y!=bot;y++) {
            for (x=lft;x!=rgt;x++) {

                    // the perlin color factor (a single float)

                colFactor=colorFactorMin+(colorFactorAdd*perlinNoiseColorFactor[(y*textureSize)+x]);

                    // now merge with bitmap color

                idx=((y*textureSize)+x)*4;

                colorData[idx]=colorData[idx]*colFactor;
                colorData[idx+1]=colorData[idx+1]*colFactor;
                colorData[idx+2]=colorData[idx+2]*colFactor;
            }
        }
    }

    protected void drawPerlinNoiseColorRect(int lft, int top, int rgt, int bot, RagColor color, float mixColorFactor) {
        int x, y, idx;
        float colFactor;

        for (y=top;y!=bot;y++) {
            for (x=lft;x!=rgt;x++) {

                    // the perlin color factor (a single float)

                colFactor=mixColorFactor*perlinNoiseColorFactor[(y*textureSize)+x];

                    // now merge with bitmap color

                idx=((y*textureSize)+x)*4;

                colorData[idx]=(colorData[idx]*(1.0f-colFactor))+(color.r*colFactor);
                colorData[idx+1]=(colorData[idx+1]*(1.0f-colFactor))+(color.g*colFactor);
                colorData[idx+2]=(colorData[idx+2]*(1.0f-colFactor))+(color.b*colFactor);
            }
        }
    }

    protected void drawPerlinNoiseColorSwitchRect(int lft, int top, int rgt, int bot, RagColor col1, RagColor col2) {
        int x, y, idx;

        for (y = top; y != bot; y++) {
            for (x = lft; x != rgt; x++) {
                idx = ((y * textureSize) + x) * 4;

                if (perlinNoiseColorFactor[(y * textureSize) + x] >= 0.5f) {
                    colorData[idx] = col1.r;
                    colorData[idx + 1] = col1.g;
                    colorData[idx + 2] = col1.b;
                } else {
                    colorData[idx] = col2.r;
                    colorData[idx + 1] = col2.g;
                    colorData[idx + 2] = col2.b;
                }
            }
        }
    }

    protected void drawPerlinNoiseReplaceColorRect(int lft, int top, int rgt, int bot, RagColor color, float replaceValue, boolean doNormals) {
        int x, y, idx;
        float noiseValue;
        RagPoint normal;

        normal = new RagPoint(0.0f, 0.0f, 0.0f);

        for (y = top; y != bot; y++) {
            for (x = lft; x != rgt; x++) {

                noiseValue = perlinNoiseColorFactor[(y * textureSize) + x];

                if (noiseValue > replaceValue) {
                    idx = ((y * textureSize) + x) * 4;

                    colorData[idx] = color.r;
                    colorData[idx + 1] = color.g;
                    colorData[idx + 2] = color.b;

                    if (doNormals) {
                        normal.setFromValues((1.0f - noiseValue), 0.0f, noiseValue);
                        normal.normalize();

                        normalData[idx] = (normal.x + 1.0f) * 0.5f;           // normals are -1...1 packed into a byte
                        normalData[idx + 1] = (normal.y + 1.0f) * 0.5f;
                        normalData[idx + 2] = (normal.z + 1.0f) * 0.5f;
                    }
                }
            }
        }
    }

    protected void drawStaticNoiseRect(int lft, int top, int rgt, int bot, float colorFactorMin, float colorFactorMax) {
        int x, y, x2, y2, idx, sz;
        float colFactor, colorFactorAdd;

        sz = textureSize / 512;
        colorFactorAdd=colorFactorMax-colorFactorMin;

        for (y = top; y < bot; y += sz) {
            for (x = lft; x < rgt; x += sz) {

                // the static random color factor
                colFactor = colorFactorMin + (AppWindow.random.nextFloat(colorFactorAdd));

                for (y2 = y; y2 < (y + sz); y2++) {
                    if ((y2 < 0) || (y2 >= textureSize)) {
                        continue;
                    }

                    for (x2 = x; x2 < (x + sz); x2++) {
                        if ((x2 < 0) || (x2 >= textureSize)) {
                            continue;
                        }

                        idx = ((y2 * textureSize) + x2) * 4;
                        colorData[idx] = colorData[idx] * colFactor;
                        colorData[idx + 1] = colorData[idx + 1] * colFactor;
                        colorData[idx + 2] = colorData[idx + 2] * colFactor;
                    }
                }
            }
        }
    }

    private void createNormalNoiseDataSinglePolygonLine(int x,int y,int x2,int y2,RagPoint normal)
    {
        int         xLen,yLen,sp,ep,dx,dy,wx,wy,idx;
        float       slope,f,r,g,b;

        r=(normal.x+1.0f)*0.5f;
        g=(normal.y+1.0f)*0.5f;
        b=(normal.z+1.0f)*0.5f;

            // the line

        xLen=Math.abs(x2-x);
        yLen=Math.abs(y2-y);

        if ((xLen==0) && (yLen==0)) return;

        if (xLen>yLen) {
            slope=(float)yLen/(float)xLen;

            if (x<x2) {
                sp=x;
                ep=x2;
                f=y;
                slope*=Math.signum(y2-y);
            }
            else {
                sp=x2;
                ep=x;
                f=y2;
                slope*=Math.signum(y-y2);
            }

            for (dx=sp;dx!=ep;dx++) {
                wx=dx;
                if (wx<0) wx=textureSize+wx;
                if (wx>=textureSize) wx-=textureSize;        // wrap around

                wy=(int)f;
                if (wy<0) wy=textureSize+wy;
                if (wy>=textureSize) wy-=textureSize;        // wrap around

                idx=((wy*textureSize)+wx)*4;
                noiseNormals[idx]=(noiseNormals[idx]*0.5f)+(r*0.5f);
                noiseNormals[idx+1]=(noiseNormals[idx+1]*0.5f)+(g*0.5f);
                noiseNormals[idx+2]=(noiseNormals[idx+2]*0.5f)+(b*0.5f);

                f+=slope;
            }
        }
        else {
            slope=(float)xLen/(float)yLen;

            if (y<y2) {
                sp=y;
                ep=y2;
                f=x;
                slope*=Math.signum(x2-x);
            }
            else {
                sp=y2;
                ep=y;
                f=x2;
                slope*=Math.signum(x-x2);
            }

            for (dy=sp;dy!=ep;dy++) {
                wx=(int)f;
                if (wx<0) wx=textureSize+wx;
                if (wx>=textureSize) wx-=textureSize;        // wrap around

                wy=dy;
                if (wy<0) wy=textureSize+wy;
                if (wy>=textureSize) wy-=textureSize;        // wrap around

                idx=((wy*textureSize)+wx)*4;
                noiseNormals[idx]=(noiseNormals[idx]*0.5f)+(r*0.5f);
                noiseNormals[idx+1]=(noiseNormals[idx+1]*0.5f)+(g*0.5f);
                noiseNormals[idx+2]=(noiseNormals[idx+2]*0.5f)+(b*0.5f);

                f+=slope;
            }
        }
    }

    private void createNormalNoiseDataSinglePolygon(int lft,int top,int rgt,int bot,float normalZFactor,boolean flipNormals)
    {
        int         n,k,idx,x,y,lx,ly,
                    lineSize,startArc,endArc,
                    mx,my,halfWid,halfHigh;
        int[]       rx,ry;
        float       rad,fx,fy,nFactor;
        RagPoint    normal;

        if ((rgt<=lft) || (bot<=top)) return;

            // random settings

        lineSize=2+AppWindow.random.nextInt(3);
        startArc=AppWindow.random.nextInt(36);
        endArc=startArc+AppWindow.random.nextInt(36);

        mx=(lft+rgt)/2;
        my=(top+bot)/2;
        halfWid=(rgt-lft)/2;
        halfHigh=(bot-top)/2;

            // create randomized points
            // for oval

        rx=new int[36];
        ry=new int[36];

        for (n=0;n!=36;n++) {
            rx[n]=AppWindow.random.nextInt(20)-10;
            ry[n]=AppWindow.random.nextInt(20)-10;
        }

            // build the polygon/oval

        lx=ly=0;
        normal=new RagPoint(0.0f,0.0f,0.0f);

        for (n=0;n!=lineSize;n++) {

            for (k=startArc;k<endArc;k++) {
                idx=k%36;
                rad=(float)((Math.PI*2.0)*(double)(idx/36.0f));

                fx=(float)Math.sin(rad);
                x=(mx+(int)((float)halfWid*fx))+rx[idx];

                fy=(float)Math.cos(rad);
                y=(my-(int)((float)halfHigh*fy))+ry[idx];

                nFactor=1.0f-((float)n/(float)lineSize);
                normal.x=(fx*nFactor)+(normal.x*(1.0f-nFactor));
                normal.y=(fy*nFactor)+(normal.y*(1.0f-nFactor));
                normal.z=(normalZFactor*nFactor)+(normal.z*(1.0f-nFactor));
                if (flipNormals) {
                    normal.x=-normal.x;
                    normal.y=-normal.y;
                }

                normal.normalize();

                if (k!=startArc) {
                    createNormalNoiseDataSinglePolygonLine(lx,ly,x,y,normal);
                }

                lx=x;
                ly=y;
            }

            halfWid--;
            if (halfWid==0) break;

            halfHigh--;
            if (halfHigh==0) break;
        }
    }

    public void createNormalNoiseData(float density, float normalZFactor) {
        int n, x, y, wid, high, pixelSize, nCount;
        int margin;

        // initialize the noise data
        pixelSize=(textureSize*textureSize)*4;
        noiseNormals=new float[pixelSize];

        for (n=0;n!=pixelSize;n+=4) {
            noiseNormals[n]=0.5f;
            noiseNormals[n+1]=0.5f;
            noiseNormals[n+2]=1.0f;
        }

        // create the random normal chunks
        nCount = (int) (((float) textureSize * 0.5f) * density);
        margin = textureSize / 25;

        for (n=0;n!=nCount;n++) {
            x=AppWindow.random.nextInt(textureSize-1);
            y=AppWindow.random.nextInt(textureSize-1);
            wid = margin + AppWindow.random.nextInt(margin * 2);
            high = margin + AppWindow.random.nextInt(margin * 2);

            createNormalNoiseDataSinglePolygon(x,y,(x+wid),(y+high),normalZFactor,AppWindow.random.nextBoolean());
        }

        // blur to fix any missing pixels and make the
        // height change not as drastic
        blur(noiseNormals, 0, 0, textureSize, textureSize, (textureSize / 100), false);
    }

    public void drawNormalNoiseRect(int lft, int top, int rgt, int bot) {
        int x, y, idx;

        for (y=top;y!=bot;y++) {
            for (x=lft;x!=rgt;x++) {
                idx=((y*textureSize)+x)*4;

                normalData[idx]=noiseNormals[idx];
                normalData[idx+1]=noiseNormals[idx+1];
                normalData[idx+2]=noiseNormals[idx+2];
             }
        }
    }

    //
    // tinting
    //
    protected void tint(int lft, int top, int rgt, int bot, RagColor color, float colFactor) {
        int x, y, idx;

        for (y = top; y != bot; y++) {
            for (x = lft; x != rgt; x++) {
                idx = ((y * textureSize) + x) * 4;

                colorData[idx] = (colorData[idx] * (1.0f - colFactor)) + (color.r * colFactor);
                colorData[idx + 1] = (colorData[idx + 1] * (1.0f - colFactor)) + (color.g * colFactor);
                colorData[idx + 2] = (colorData[idx + 2] * (1.0f - colFactor)) + (color.b * colFactor);
            }
        }
    }

        //
        // distortions
        //

    protected void gravityDistortEdges(int lft,int top,int rgt,int bot,int distortCount,int distortRadius,int distortSize)
    {
        int         n,x,y,gx,gy,sx,sy,dx,dy,
                    idx,idx2;
        float       d;
        float[]     colorDataCopy,normalDataCopy;

        if ((lft>=rgt) || (top>=bot)) return;

            // color and normal copies

        colorDataCopy=colorData.clone();
        normalDataCopy=normalData.clone();

            // run a number of gravity distortions

        for (n=0;n!=distortCount;n++) {

                // find a gravity point on an edge

            switch (AppWindow.random.nextInt(4)) {
                case 0:
                    gx=lft+distortSize;
                    gy=top+AppWindow.random.nextInt(bot-top);
                    break;
                case 1:
                    gx=rgt-distortSize;
                    gy=top+AppWindow.random.nextInt(bot-top);
                    break;
                case 2:
                    gx=lft+AppWindow.random.nextInt(rgt-lft);
                    gy=top+distortSize;
                    break;
                default:
                    gx=lft+AppWindow.random.nextInt(rgt-lft);
                    gy=bot-distortSize;
                    break;
            }

                // distort bitmap

            for (y = top; y != bot; y++) {
                if ((y < 0) || (y >= textureSize)) {
                    continue;
                }

                for (x = lft; x != rgt; x++) {
                    if ((x < 0) || (x >= textureSize)) {
                        continue;
                    }

                    sx=x;
                    sy=y;

                    dx=gx-x;
                    dy=gy-y;
                    d=(float)Math.sqrt((dx*dx)+(dy*dy));
                    if (d>(float)distortRadius) continue;

                    d=1.0f-(d/(float)distortRadius);
                    sx=sx-(int)(((float)Math.signum(dx)*(float)distortSize)*d);
                    sy=sy-(int)(((float)Math.signum(dy)*(float)distortSize)*d);

                    if (sx<0) sx=0;
                    if (sx>=textureSize) sx=textureSize-1;
                    if (sy<0) sy=0;
                    if (sy>=textureSize) sy=textureSize-1;

                        // shift the pixels

                    idx=((y*textureSize)+x)*4;
                    idx2=((sy*textureSize)+sx)*4;

                    colorData[idx]=colorDataCopy[idx2];
                    colorData[idx+1]=colorDataCopy[idx2+1];
                    colorData[idx + 2] = colorDataCopy[idx2 + 2];
                    colorData[idx + 3] = colorDataCopy[idx2 + 3];

                    normalData[idx]=normalDataCopy[idx2];
                    normalData[idx+1]=normalDataCopy[idx2+1];
                    normalData[idx+2]=normalDataCopy[idx2+2];
                }
            }
        }
    }

        //
        // shape drawing
        //

    protected void drawRect(int lft,int top,int rgt,int bot,RagColor color)
    {
        int         x,y,idx;

        if ((lft>=rgt) || (top>=bot)) return;

        for (y=top;y<=bot;y++) {
            if ((y<0) || (y>=textureSize)) continue;

            for (x=lft;x<=rgt;x++) {
                if ((x<0) || (x>=textureSize)) continue;

                idx=((y*textureSize)+x)*4;

                colorData[idx]=color.r;
                colorData[idx+1]=color.g;
                colorData[idx+2]=color.b;

                normalData[idx]=(NORMAL_CLEAR.x+1.0f)*0.5f;
                normalData[idx+1]=(NORMAL_CLEAR.y+1.0f)*0.5f;
                normalData[idx+2]=(NORMAL_CLEAR.z+1.0f)*0.5f;
            }
        }
    }

    protected void drawRectAlpha(int lft, int top, int rgt, int bot, float a) {
        int x, y;

        if ((lft >= rgt) || (top >= bot)) {
            return;
        }

        for (y = top; y <= bot; y++) {
            if ((y < 0) || (y >= textureSize)) {
                continue;
            }

            for (x = lft; x <= rgt; x++) {
                if ((x < 0) || (x >= textureSize)) {
                    continue;
                }
                colorData[(((y * textureSize) + x) * 4) + 3] = a;
            }
        }
    }

    protected void drawRectEmissive(int lft,int top,int rgt,int bot,RagColor color)
    {
        int         x,y,idx;

        if ((lft>=rgt) || (top>=bot)) return;

        for (y=top;y<=bot;y++) {
            if ((y<0) || (y>=textureSize)) continue;

            for (x=lft;x<=rgt;x++) {
                if ((x<0) || (x>=textureSize)) continue;

                idx=((y*textureSize)+x)*4;

                emissiveData[idx]=color.r;
                emissiveData[idx+1]=color.g;
                emissiveData[idx+2]=color.b;
            }
        }
    }

    protected void draw3DFrameRect(int lft,int top,int rgt,int bot,int size,RagColor color,boolean faceOut)
    {
        int         n,x,y,idx;

        if ((lft>=rgt) || (top>=bot)) return;

            // draw the edges

        for (n=0;n<=size;n++) {

            for (x=lft;x<=rgt;x++) {
                if ((x<0) || (x>=textureSize)) continue;

                if ((top>=0) && (top<textureSize)) {
                    idx=((top*textureSize)+x)*4;
                    colorData[idx]=color.r;
                    colorData[idx+1]=color.g;
                    colorData[idx+2]=color.b;

                    normalData[idx]=((faceOut?NORMAL_TOP_45.x:NORMAL_BOTTOM_45.x)+1.0f)*0.5f;
                    normalData[idx+1]=((faceOut?NORMAL_TOP_45.y:NORMAL_BOTTOM_45.y)+1.0f)*0.5f;
                    normalData[idx+2]=((faceOut?NORMAL_TOP_45.z:NORMAL_BOTTOM_45.z)+1.0f)*0.5f;
                }

                if ((bot>=0) && (bot<textureSize)) {
                    idx=((bot*textureSize)+x)*4;
                    colorData[idx]=color.r;
                    colorData[idx+1]=color.g;
                    colorData[idx+2]=color.b;

                    normalData[idx]=((faceOut?NORMAL_BOTTOM_45.x:NORMAL_TOP_45.x)+1.0f)*0.5f;
                    normalData[idx+1]=((faceOut?NORMAL_BOTTOM_45.y:NORMAL_TOP_45.y)+1.0f)*0.5f;
                    normalData[idx+2]=((faceOut?NORMAL_BOTTOM_45.z:NORMAL_TOP_45.z)+1.0f)*0.5f;
                }
            }

            for (y=top;y<=bot;y++) {
                if ((y<0) || (y>=textureSize)) continue;

                if ((lft>=0) && (lft<textureSize)) {
                    idx=((y*textureSize)+lft)*4;
                    colorData[idx]=color.r;
                    colorData[idx+1]=color.g;
                    colorData[idx+2]=color.b;

                    normalData[idx]=((faceOut?NORMAL_LEFT_45.x:NORMAL_RIGHT_45.x)+1.0f)*0.5f;
                    normalData[idx+1]=((faceOut?NORMAL_LEFT_45.y:NORMAL_RIGHT_45.y)+1.0f)*0.5f;
                    normalData[idx+2]=((faceOut?NORMAL_LEFT_45.z:NORMAL_RIGHT_45.z)+1.0f)*0.5f;
                }

                if ((rgt>=0) && (rgt<textureSize)) {
                    idx=((y*textureSize)+rgt)*4;
                    colorData[idx]=color.r;
                    colorData[idx+1]=color.g;
                    colorData[idx+2]=color.b;

                    normalData[idx]=((faceOut?NORMAL_RIGHT_45.x:NORMAL_LEFT_45.x)+1.0f)*0.5f;
                    normalData[idx+1]=((faceOut?NORMAL_RIGHT_45.y:NORMAL_LEFT_45.y)+1.0f)*0.5f;
                    normalData[idx+2]=((faceOut?NORMAL_RIGHT_45.z:NORMAL_LEFT_45.z)+1.0f)*0.5f;
                }
            }
                // next edge

            lft++;
            rgt--;
            top++;
            bot--;
        }
    }

    protected void draw3DDarkenFrameRect(int lft, int top, int rgt, int bot, int size, float darken, boolean faceOut) {
        int n, x, y, idx;

        if ((lft >= rgt) || (top >= bot)) {
            return;
        }

        // draw the edges
        for (n = 0; n <= size; n++) {

            for (x = lft; x <= rgt; x++) {
                if ((x < 0) || (x >= textureSize)) {
                    continue;
                }

                if ((top >= 0) && (top < textureSize)) {
                    idx = ((top * textureSize) + x) * 4;
                    colorData[idx] = colorData[idx] * darken;
                    colorData[idx + 1] = colorData[idx + 1] * darken;
                    colorData[idx + 2] = colorData[idx + 2] * darken;

                    normalData[idx] = (normalData[idx] * 0.3f) + ((((faceOut ? NORMAL_TOP_45.x : NORMAL_BOTTOM_45.x) + 1.0f) * 0.5f) * 0.7f);
                    normalData[idx + 1] = (normalData[idx + 1] * 0.3f) + ((((faceOut ? NORMAL_TOP_45.y : NORMAL_BOTTOM_45.y) + 1.0f) * 0.5f) * 0.7f);
                    normalData[idx + 2] = (normalData[idx + 2] * 0.3f) + ((((faceOut ? NORMAL_TOP_45.z : NORMAL_BOTTOM_45.z) + 1.0f) * 0.5f) * 0.7f);
                }

                if ((bot >= 0) && (bot < textureSize)) {
                    idx = ((bot * textureSize) + x) * 4;
                    colorData[idx] = colorData[idx] * darken;
                    colorData[idx + 1] = colorData[idx + 1] * darken;
                    colorData[idx + 2] = colorData[idx + 2] * darken;

                    normalData[idx] = (normalData[idx] * 0.3f) + ((((faceOut ? NORMAL_BOTTOM_45.x : NORMAL_TOP_45.x) + 1.0f) * 0.5f) * 0.7f);
                    normalData[idx + 1] = (normalData[idx + 1] * 0.3f) + ((((faceOut ? NORMAL_BOTTOM_45.y : NORMAL_TOP_45.y) + 1.0f) * 0.5f) * 0.7f);
                    normalData[idx + 2] = (normalData[idx + 2] * 0.3f) + ((((faceOut ? NORMAL_BOTTOM_45.z : NORMAL_TOP_45.z) + 1.0f) * 0.5f) * 0.7f);
                }
            }

            for (y = top; y <= bot; y++) {
                if ((y < 0) || (y >= textureSize)) {
                    continue;
                }

                if ((lft >= 0) && (lft < textureSize)) {
                    idx = ((y * textureSize) + lft) * 4;
                    colorData[idx] = colorData[idx] * darken;
                    colorData[idx + 1] = colorData[idx + 1] * darken;
                    colorData[idx + 2] = colorData[idx + 2] * darken;

                    normalData[idx] = (normalData[idx] * 0.3f) + ((((faceOut ? NORMAL_LEFT_45.x : NORMAL_RIGHT_45.x) + 1.0f) * 0.5f) * 0.7f);
                    normalData[idx + 1] = (normalData[idx + 1] * 0.3f) + ((((faceOut ? NORMAL_LEFT_45.y : NORMAL_RIGHT_45.y) + 1.0f) * 0.5f) * 0.7f);
                    normalData[idx + 2] = (normalData[idx + 2] * 0.3f) + ((((faceOut ? NORMAL_LEFT_45.z : NORMAL_RIGHT_45.z) + 1.0f) * 0.5f) * 0.7f);
                }

                if ((rgt >= 0) && (rgt < textureSize)) {
                    idx = ((y * textureSize) + rgt) * 4;
                    colorData[idx] = colorData[idx] * darken;
                    colorData[idx + 1] = colorData[idx + 1] * darken;
                    colorData[idx + 2] = colorData[idx + 2] * darken;

                    normalData[idx] = (normalData[idx] * 0.3f) + ((((faceOut ? NORMAL_RIGHT_45.x : NORMAL_LEFT_45.x) + 1.0f) * 0.5f) * 0.7f);
                    normalData[idx + 1] = (normalData[idx + 1] * 0.3f) + ((((faceOut ? NORMAL_RIGHT_45.y : NORMAL_LEFT_45.y) + 1.0f) * 0.5f) * 0.7f);
                    normalData[idx + 2] = (normalData[idx + 2] * 0.3f) + ((((faceOut ? NORMAL_RIGHT_45.z : NORMAL_LEFT_45.z) + 1.0f) * 0.5f) * 0.7f);
                }
            }

            // next edge
            lft++;
            rgt--;
            top++;
            bot--;
        }
    }

    protected void drawSimpleOval(float[] data, int lft, int top, int rgt, int bot, RagColor color) {
        int x, y, mx, my, cx, cy, halfWid, halfHigh, idx;
        double dx, dy, ovalDist;
        double squareHalfWid, squareHalfHigh, squareHalfWidAndHigh;

        if ((lft >= rgt) || (top >= bot)) {
            return;
        }

        // draw oval in box
        halfWid = (rgt - lft) / 2;
        squareHalfWid = (double) halfWid * (double) halfWid;

        halfHigh = (bot - top) / 2;
        squareHalfHigh = (double) halfHigh * (double) halfHigh;

        squareHalfWidAndHigh = squareHalfWid * squareHalfHigh;

        mx = (lft + rgt) / 2;
        my = (top + bot) / 2;

        for (y = 0; y != halfHigh; y++) {
            dy = (double) y * (double) y;
            dy = dy * squareHalfWid;

            for (x = 0; x != halfWid; x++) {
                dx = (double) x * (double) x;

                // in oval?
                ovalDist = (dx * squareHalfHigh) + dy;
                if (ovalDist > squareHalfWidAndHigh) {
                    continue;
                }

                // top
                cy = my - y;
                if ((cy >= 0) && (cy < textureSize)) {

                    cx = mx + x;
                    if ((cx >= 0) && (cx < textureSize)) {
                        idx = ((cy * textureSize) + cx) * 4;
                        data[idx] = color.r;
                        data[idx + 1] = color.g;
                        data[idx + 2] = color.b;
                    }

                    cx = mx - x;
                    if ((cx >= 0) && (cx < textureSize)) {
                        idx = ((cy * textureSize) + cx) * 4;
                        data[idx] = color.r;
                        data[idx + 1] = color.g;
                        data[idx + 2] = color.b;
                    }
                }

                // bottom
                cy = my + y;
                if ((cy >= 0) && (cy < textureSize)) {

                    cx = mx + x;
                    if ((cx >= 0) && (cx < textureSize)) {
                        idx = ((cy * textureSize) + cx) * 4;
                        data[idx] = color.r;
                        data[idx + 1] = color.g;
                        data[idx + 2] = color.b;
                    }

                    cx = mx - x;
                    if ((cx >= 0) && (cx < textureSize)) {
                        idx = ((cy * textureSize) + cx) * 4;
                        data[idx] = color.r;
                        data[idx + 1] = color.g;
                        data[idx + 2] = color.b;
                    }
                }
            }
        }
    }

    protected void drawDarkenOval(float[] data, int lft, int top, int rgt, int bot, float darken) {
        int x, y, mx, my, cx, cy, halfWid, halfHigh, idx;
        double dx, dy, ovalDist;
        double squareHalfWid, squareHalfHigh, squareHalfWidAndHigh;

        // draw oval in box
        halfWid = (rgt - lft) / 2;
        squareHalfWid = (double) halfWid * (double) halfWid;

        halfHigh = (bot - top) / 2;
        squareHalfHigh = (double) halfHigh * (double) halfHigh;

        squareHalfWidAndHigh = squareHalfWid * squareHalfHigh;

        mx = (lft + rgt) / 2;
        my = (top + bot) / 2;

        for (y = 0; y != halfHigh; y++) {
            dy = (double) y * (double) y;
            dy = dy * squareHalfWid;

            for (x = 0; x != halfWid; x++) {
                dx = (double) x * (double) x;

                // in oval?
                ovalDist = (dx * squareHalfHigh) + dy;
                if (ovalDist > squareHalfWidAndHigh) {
                    continue;
                }

                // top
                cy = my - y;
                if ((cy >= 0) && (cy < textureSize)) {

                    cx = mx + x;
                    if ((cx >= 0) && (cx < textureSize)) {
                        idx = ((cy * textureSize) + cx) * 4;
                        data[idx] *= darken;
                        data[idx + 1] *= darken;
                        data[idx + 2] *= darken;
                    }

                    cx = mx - x;
                    if ((cx >= 0) && (cx < textureSize)) {
                        idx = ((cy * textureSize) + cx) * 4;
                        data[idx] *= darken;
                        data[idx + 1] *= darken;
                        data[idx + 2] *= darken;
                    }
                }

                // bottom
                cy = my + y;
                if ((cy >= 0) && (cy < textureSize)) {

                    cx = mx + x;
                    if ((cx >= 0) && (cx < textureSize)) {
                        idx = ((cy * textureSize) + cx) * 4;
                        data[idx] *= darken;
                        data[idx + 1] *= darken;
                        data[idx + 2] *= darken;
                    }

                    cx = mx - x;
                    if ((cx >= 0) && (cx < textureSize)) {
                        idx = ((cy * textureSize) + cx) * 4;
                        data[idx] *= darken;
                        data[idx + 1] *= darken;
                        data[idx + 2] *= darken;
                    }
                }
            }
        }
    }

    protected void drawOval(int lft, int top, int rgt, int bot, float startArc, float endArc, float xRoundFactor, float yRoundFactor, int edgeSize, float edgeColorFactor, RagColor color, float normalZFactor, boolean flipNormals, boolean addNoise, float colorFactorMin, float colorFactorMax) {
        int n, x, y, mx, my, wid, high, idx;
        int edgeCount, drawStartArc, drawEndArc;
        float drawCount, drawMult;
        float fx, fy, halfWid, halfHigh, rad;
        float colorFactorAdd, colorFactor, nFactor;
        RagColor col;
        RagPoint normal;

        if ((lft >= rgt) || (top >= bot)) {
            return;
        }

        wid = (rgt - lft) - 1;
        high = (bot - top) - 1; // avoids clipping on bottom from being on wid,high

        // yes this is a crazy way to draw an oval but it makes it much easier to
        // deal with sliding color/normals factors in the calcs

        // start and end arc
        drawCount = (float) Math.max(wid, high) * 10.0f;
        drawMult = 1.0f / drawCount;

        drawStartArc = (int) (startArc * drawCount);
        drawEndArc = (int) (endArc * drawCount);
        if (drawStartArc >= drawEndArc) {
            return;
        }

        // the drawing setup
        mx = lft + (wid / 2);
        my = top + (high / 2);

        col = new RagColor(0.0f, 0.0f, 0.0f);
        normal = new RagPoint(0.0f, 0.0f, 0.0f);
        colorFactorAdd = colorFactorMax - colorFactorMin;

        edgeCount = edgeSize;

        // fill the oval
        while ((wid > 0) && (high > 0)) {

            halfWid = (float) wid * 0.5f;
            halfHigh = (float) high * 0.5f;

            for (n = drawStartArc; n < drawEndArc; n++) {
                rad = (float) (Math.PI * 2.0) * ((float) n * drawMult);

                fx = (float) Math.sin(rad);
                fx += (fx * xRoundFactor);
                if (fx > 1.0f) {
                    fx = 1.0f;
                }
                if (fx < -1.0f) {
                    fx = -1.0f;
                }

                x = mx + (int) (halfWid * fx);
                if ((x < 0) || (x >= textureSize)) {
                    continue;
                }

                fy = (float) Math.cos(rad);
                fy += (fy * yRoundFactor);
                if (fy > 1.0f) {
                    fy = 1.0f;
                }
                if (fy < -1.0f) {
                    fy = -1.0f;
                }

                y = my - (int) (halfHigh * fy);
                if ((y < 0) || (y >= textureSize)) {
                    continue;
                }

                // edge darkening
                col.setFromColor(color);

                if (edgeCount > 0) {
                    colorFactor = edgeColorFactor + ((1.0f - ((float) edgeCount / (float) edgeSize)) * (1.0f - edgeColorFactor));
                    col.factor(colorFactor);
                }

                if (addNoise) {
                    colorFactor = colorFactorMin + (colorFactorAdd * perlinNoiseColorFactor[(y * textureSize) + x]);
                    col.factor(colorFactor);
                }

                // the color
                idx = ((y * textureSize) + x) * 4;

                colorData[idx] = col.r;
                colorData[idx + 1] = col.g;
                colorData[idx + 2] = col.b;
                colorData[idx + 3] = 0.0f;

                // get a normal for the pixel change
                // if we are outside the edge, gradually fade it
                // to the default pointing out normal
                normal.x = 0.0f;
                normal.y = 0.0f;
                normal.z = 1.0f;

                if (edgeCount > 0) {
                    nFactor = (float) edgeCount / (float) edgeSize;
                    normal.x = (fx * nFactor) + (normal.x * (1.0f - nFactor));
                    normal.y = (fy * nFactor) + (normal.y * (1.0f - nFactor));
                    normal.z = (normalZFactor * nFactor) + (normal.z * (1.0f - nFactor));
                    if (flipNormals) {
                        normal.x = -normal.x;
                        normal.y = -normal.y;
                    }
                }

                // add in noise normal
                if (addNoise) {
                    normal.x = (((noiseNormals[idx] * 0.5f) - 1.0f) * 0.4f) + (normal.x * 0.6f);
                    normal.y = (((noiseNormals[idx + 1] * 0.5f) - 1.0f) * 0.4f) + (normal.y * 0.6f);
                    normal.z = (((noiseNormals[idx + 2] * 0.5f) - 1.0f) * 0.4f) + (normal.z * 0.6f);
                }

                normal.normalize();

                normalData[idx] = (normal.x + 1.0f) * 0.5f;           // normals are -1...1 packed into a byte
                normalData[idx + 1] = (normal.y + 1.0f) * 0.5f;
                normalData[idx + 2] = (normal.z + 1.0f) * 0.5f;
            }

            if (edgeCount > 0) {
                edgeCount--;
            }

            wid--;
            high--;
        }
    }

    protected void drawFrameOval(int lft, int top, int rgt, int bot, float xRoundFactor, float yRoundFactor, RagColor color) {
        int n, x, y, mx, my;
        int lastX, lastY, firstX, firstY;
        float halfWid, halfHigh;
        float fx, fy, rad;

        if ((lft >= rgt) || (top >= bot)) {
            return;
        }

        halfWid = (float) (rgt - lft) * 0.5f;
        halfHigh = (float) (bot - top) * 0.5f;

        mx = (lft + rgt) / 2;
        my = (top + bot) / 2;

        lastX = -1;
        lastY = -1;
        firstX = -1;
        firstY = -1;

        for (n = 0; n != 360; n++) {
            rad = (float) (Math.PI * 2.0) * (((float) n) / 360.0f);

            fx = (float) Math.sin(rad);
            fx += (fx * xRoundFactor);
            if (fx > 1.0f) {
                fx = 1.0f;
            }
            if (fx < -1.0f) {
                fx = -1.0f;
            }

            x = mx + (int) (halfWid * fx);
            if ((x < 0) || (x >= textureSize)) {
                continue;
            }

            fy = (float) Math.cos(rad);
            fy += (fy * yRoundFactor);
            if (fy > 1.0f) {
                fy = 1.0f;
            }
            if (fy < -1.0f) {
                fy = -1.0f;
            }

            y = my - (int) (halfHigh * fy);
            if ((y < 0) || (y >= textureSize)) {
                continue;
            }

            // line to line for pixels
            if (n == 0) {
                firstX = x;
                firstY = y;
            } else {
                if (n == 359) {
                    drawLineColor(x, y, firstX, firstY, color);
                } else {
                    drawLineColor(lastX, lastY, x, y, color);
                }
            }

            lastX = x;
            lastY = y;
        }
    }

    protected void drawDiamond(int lft,int top,int rgt,int bot,RagColor color)
    {
        int         x,y,lx,rx,mx,my,idx;
        float       f,halfWid;
        RagColor    frameColor;

        if ((lft>=rgt) || (top>=bot)) return;

            // the fill

        mx=(lft+rgt)/2;
        my=(top+bot)/2;
        halfWid=(float)(rgt-lft)*0.5f;

        for (y=top;y!=bot;y++) {

            if (y<my) {
                f=1.0f-((float)(my-y)/(float)(my-top));
                lx=mx-(int)(halfWid*f);
                rx=mx+(int)(halfWid*f);
            }
            else {
                f=1.0f-((float)(y-my)/(float)(my-top));
                lx=mx-(int)(halfWid*f);
                rx=mx+(int)(halfWid*f);
            }

            if (lx>=rx) continue;

            for (x=lx;x!=rx;x++) {
                idx=((y*textureSize)+x)*4;

                colorData[idx]=color.r;
                colorData[idx+1]=color.g;
                colorData[idx+2]=color.b;
            }
        }

            // the border

        frameColor=adjustColorRandom(color,0.85f,0.95f);

        drawLineColor((mx+1),top,(lft+1),my,frameColor);
        drawLineColor(mx,top,lft,my,frameColor);
        drawLineNormal((mx+1),top,(lft+1),my,NORMAL_TOP_LEFT_45);
        drawLineNormal(mx,top,lft,my,NORMAL_TOP_LEFT_45);

        drawLineColor((mx-1),top,(rgt-1),my,frameColor);
        drawLineColor(mx,top,rgt,my,frameColor);
        drawLineNormal((mx-1),top,(rgt-1),my,NORMAL_TOP_RIGHT_45);
        drawLineNormal(mx,top,rgt,my,NORMAL_TOP_RIGHT_45);

        drawLineColor((lft+1),my,(mx+1),bot,frameColor);
        drawLineColor(lft,my,mx,bot,frameColor);
        drawLineNormal((lft+1),my,(mx+1),bot,NORMAL_BOTTOM_LEFT_45);
        drawLineNormal(lft,my,mx,bot,NORMAL_TOP_LEFT_45);

        drawLineColor((rgt-1),my,(mx-1),bot,frameColor);
        drawLineColor(lft,my,mx,bot,frameColor);
        drawLineNormal((rgt-1),my,(mx-1),bot,NORMAL_BOTTOM_RIGHT_45);
        drawLineNormal(rgt,my,mx,bot,NORMAL_TOP_RIGHT_45);
    }

    protected void drawTriangle(int x0, int y0, int x1, int y1, int x2, int y2, boolean doNormals, RagColor color) {
        int x, y, lx, rx, ty, my, by, tyX, myX, byX, idx;

        if ((y0<=y1) && (y0<=y2)) {
            ty=y0;
            tyX=x0;
            if (y1<y2) {
                my=y1;
                myX=x1;
                by=y2;
                byX=x2;
            }
            else {
                my=y2;
                myX=x2;
                by=y1;
                byX=x1;
            }
        }
        else {
            if ((y1<=y0) && (y1<=y2)) {
                ty=y1;
                tyX=x1;
                if (y0<y2) {
                    my=y0;
                    myX=x0;
                    by=y2;
                    byX=x2;
                }
                else {
                    my=y2;
                    myX=x2;
                    by=y0;
                    byX=x0;
                }
            }
            else {
                ty=y2;
                tyX=x2;
                if (y0<y1) {
                    my=y0;
                    myX=x0;
                    by=y1;
                    byX=x1;
                }
                else {
                    my=y1;
                    myX=x1;
                    by=y0;
                    byX=x0;
                }
            }
        }

            // top half

        for (y=ty;y<my;y++) {
            if ((y<0) || (y>=textureSize)) continue;

            if (myX<tyX) {
                lx=tyX+(int)((float)((myX-tyX)*(y-ty))/(float)(my-ty));
                rx=tyX+(int)((float)((byX-tyX)*(y-ty))/(float)(by-ty));
            }
            else {
                lx=tyX+(int)((float)((byX-tyX)*(y-ty))/(float)(by-ty));
                rx=tyX+(int)((float)((myX-tyX)*(y-ty))/(float)(my-ty));
            }

            if (lx<0) lx=0;
            if (rx>=textureSize) rx=textureSize-1;
            if (lx>rx) continue;

            idx=((y*textureSize)+lx)*4;

            for (x=lx;x<rx;x++) {
                colorData[idx]=color.r;
                colorData[idx+1]=color.g;
                colorData[idx+2]=color.b;

                if (doNormals) {
                    normalData[idx] = (NORMAL_CLEAR.x + 1.0f) * 0.5f;
                    normalData[idx + 1] = (NORMAL_CLEAR.y + 1.0f) * 0.5f;
                    normalData[idx + 2] = (NORMAL_CLEAR.z + 1.0f) * 0.5f;
                }

                idx+=4;
            }
        }

        // bottom half
        for (y=my;y<by;y++) {
            if ((y<0) || (y>=textureSize)) continue;

            if (myX<tyX) {
                lx=myX+(int)((float)((byX-myX)*(y-my))/(float)(by-my));
                rx=tyX+(int)((float)((byX-tyX)*(y-ty))/(float)(by-ty));
            }
            else {
                lx=tyX+(int)((float)((byX-tyX)*(y-ty))/(float)(by-ty));
                rx=myX+(int)((float)((byX-myX)*(y-my))/(float)(by-my));
            }

            if (lx<0) lx=0;
            if (rx>=textureSize) rx=textureSize-1;
            if (lx>rx) continue;

            idx=((y*textureSize)+lx)*4;

            for (x=lx;x<rx;x++) {
                colorData[idx]=color.r;
                colorData[idx+1]=color.g;
                colorData[idx+2]=color.b;

                if (doNormals) {
                    normalData[idx] = (NORMAL_CLEAR.x + 1.0f) * 0.5f;
                    normalData[idx + 1] = (NORMAL_CLEAR.y + 1.0f) * 0.5f;
                    normalData[idx + 2] = (NORMAL_CLEAR.z + 1.0f) * 0.5f;
                }

                idx+=4;
            }
        }

        // normals
        if (doNormals) {
            drawLineNormal(x0, y0, x1, y1, NORMAL_LEFT_45);
            drawLineNormal(x0, y0, x2, y2, NORMAL_RIGHT_45);
            drawLineNormal(x1, y1, x2, y2, NORMAL_BOTTOM_45);
        }
    }

    protected void darkenTriangle(int x0, int y0, int x1, int y1, int x2, int y2, boolean doNormals, float factor) {
        int x, y, lx, rx, ty, my, by, tyX, myX, byX, idx;

        if ((y0 <= y1) && (y0 <= y2)) {
            ty = y0;
            tyX = x0;
            if (y1 < y2) {
                my = y1;
                myX = x1;
                by = y2;
                byX = x2;
            } else {
                my = y2;
                myX = x2;
                by = y1;
                byX = x1;
            }
        } else {
            if ((y1 <= y0) && (y1 <= y2)) {
                ty = y1;
                tyX = x1;
                if (y0 < y2) {
                    my = y0;
                    myX = x0;
                    by = y2;
                    byX = x2;
                } else {
                    my = y2;
                    myX = x2;
                    by = y0;
                    byX = x0;
                }
            } else {
                ty = y2;
                tyX = x2;
                if (y0 < y1) {
                    my = y0;
                    myX = x0;
                    by = y1;
                    byX = x1;
                } else {
                    my = y1;
                    myX = x1;
                    by = y0;
                    byX = x0;
                }
            }
        }

        // top half
        for (y = ty; y < my; y++) {
            if ((y < 0) || (y >= textureSize)) {
                continue;
            }

            if (myX < tyX) {
                lx = tyX + (int) ((float) ((myX - tyX) * (y - ty)) / (float) (my - ty));
                rx = tyX + (int) ((float) ((byX - tyX) * (y - ty)) / (float) (by - ty));
            } else {
                lx = tyX + (int) ((float) ((byX - tyX) * (y - ty)) / (float) (by - ty));
                rx = tyX + (int) ((float) ((myX - tyX) * (y - ty)) / (float) (my - ty));
            }

            if (lx < 0) {
                lx = 0;
            }
            if (rx >= textureSize) {
                rx = textureSize - 1;
            }
            if (lx > rx) {
                continue;
            }

            idx = ((y * textureSize) + lx) * 4;

            for (x = lx; x < rx; x++) {
                colorData[idx] = colorData[idx] * factor;
                colorData[idx + 1] = colorData[idx + 1] * factor;
                colorData[idx + 2] = colorData[idx + 2] * factor;

                if (doNormals) {
                    normalData[idx] = (NORMAL_CLEAR.x + 1.0f) * 0.5f;
                    normalData[idx + 1] = (NORMAL_CLEAR.y + 1.0f) * 0.5f;
                    normalData[idx + 2] = (NORMAL_CLEAR.z + 1.0f) * 0.5f;
                }

                idx += 4;
            }
        }

        // bottom half
        for (y = my; y < by; y++) {
            if ((y < 0) || (y >= textureSize)) {
                continue;
            }

            if (myX < tyX) {
                lx = myX + (int) ((float) ((byX - myX) * (y - my)) / (float) (by - my));
                rx = tyX + (int) ((float) ((byX - tyX) * (y - ty)) / (float) (by - ty));
            } else {
                lx = tyX + (int) ((float) ((byX - tyX) * (y - ty)) / (float) (by - ty));
                rx = myX + (int) ((float) ((byX - myX) * (y - my)) / (float) (by - my));
            }

            if (lx < 0) {
                lx = 0;
            }
            if (rx >= textureSize) {
                rx = textureSize - 1;
            }
            if (lx > rx) {
                continue;
            }

            idx = ((y * textureSize) + lx) * 4;

            for (x = lx; x < rx; x++) {
                colorData[idx] = colorData[idx] * factor;
                colorData[idx + 1] = colorData[idx + 1] * factor;
                colorData[idx + 2] = colorData[idx + 2] * factor;

                if (doNormals) {
                    normalData[idx] = (NORMAL_CLEAR.x + 1.0f) * 0.5f;
                    normalData[idx + 1] = (NORMAL_CLEAR.y + 1.0f) * 0.5f;
                    normalData[idx + 2] = (NORMAL_CLEAR.z + 1.0f) * 0.5f;
                }

                idx += 4;
            }
        }

        // normals
        if (doNormals) {
            drawLineNormal(x0, y0, x1, y1, NORMAL_LEFT_45);
            drawLineNormal(x0, y0, x2, y2, NORMAL_RIGHT_45);
            drawLineNormal(x1, y1, x2, y2, NORMAL_BOTTOM_45);
        }
    }

    protected void drawHexagon(int lft,int top,int rgt,int bot,int pointSize,int edgeSize,RagColor color)
    {
        int         n,lx,rx,my;
        float       darkenFactor;
        RagColor    darkColor;

        // hexagon size
        my=(top+bot)/2;

        lx=lft;
        rx=rgt;
        lft-=pointSize;
        rgt+=pointSize;

        if (lft>=rgt) return;

        // fill the hexagon
        if (color!=null) {
            drawRect((lx - 1), top, (rx + 1), bot, color);
            drawTriangle(lx, top, lft, my, lx, bot, false, color);
            drawTriangle(rx, top, rgt, my, rx, bot, false, color);
        }

        // draw the edges
        for (n=0;n!=edgeSize;n++) {

                // the colors

            darkenFactor=(((float)(n+1)/(float)edgeSize)*0.3f)+0.7f;
            darkColor=adjustColor(color,darkenFactor);

                // top-left to top to top-right

            drawLineColor((lft+n),my,(lx+n),(top+n),darkColor);
            drawLineNormal((lft+n),my,(lx+n),(top+n),NORMAL_TOP_LEFT_45);

            drawLineColor((lx+n),(top+n),(rx-n),(top+n),darkColor);
            drawLineNormal((lx+n),(top+n),(rx-n),(top+n),NORMAL_TOP_45);

            drawLineColor((rx-n),(top+n),(rgt-n),my,darkColor);
            drawLineNormal((rx-n),(top+n),(rgt-n),my,NORMAL_TOP_RIGHT_45);

                // bottom-right to bottom to bottom-left

            drawLineColor((lft+n),my,(lx+n),(bot-n),darkColor);
            drawLineNormal((lft+n),my,(lx+n),(bot-n),NORMAL_BOTTOM_LEFT_45);

            drawLineColor((lx+n),(bot-n),(rx-n),(bot-n),darkColor);
            drawLineNormal((lx+n),(bot-n),(rx-n),(bot-n),NORMAL_BOTTOM_45);

            drawLineColor((rx-n),(bot-n),(rgt-n),my,darkColor);
            drawLineNormal((rx-n),(bot-n),(rgt-n),my,NORMAL_BOTTOM_RIGHT_45);
        }
    }

        //
        // metals
        //

    private void drawMetalShineLine(int x,int top,int bot,int shineWid,RagColor baseColor)
    {
        int         n,lx,rx,y,idx;
        float       density,densityReduce;

        if (top>=bot) return;
        if (shineWid<=0) return;

            // since we draw the shines from both sides,
            // we need to move the X into the middle and cut width in half

        shineWid=shineWid/2;

        x+=shineWid;

            // start with 100 density and reduce
            // as we go across the width

        density=1.0f;
        densityReduce=0.9f/(float)shineWid;

            // write the shine lines

        for (n=0;n!=shineWid;n++) {

            lx=x-n;
            rx=x+n;

            for (y=top;y!=bot;y++) {

                if (AppWindow.random.nextFloat()<density) {
                    if ((lx>=0) && (lx<textureSize)) {
                        idx=((y*textureSize)+lx)*4;
                        colorData[idx]=baseColor.r;
                        colorData[idx+1]=baseColor.g;
                        colorData[idx+2]=baseColor.b;
                    }
                }

                if (AppWindow.random.nextFloat()<density) {
                    if ((rx>=0) && (rx<textureSize)) {
                        idx=((y*textureSize)+rx)*4;
                        colorData[idx]=baseColor.r;
                        colorData[idx+1]=baseColor.g;
                        colorData[idx+2]=baseColor.b;
                    }
                }

            }

            density-=densityReduce;
        }
    }

    protected void drawMetalShine(int lft,int top,int rgt,int bot,RagColor metalColor)
    {
        int         x,wid,shineWid;
        RagColor    shineColor;

        x=lft;
        wid=rgt-lft;

        while (true) {
            shineWid=(int)(((float)wid*0.035f)+(AppWindow.random.nextFloat()*((float)wid*0.15)));
            if ((x+shineWid)>rgt) shineWid=rgt-x;

                // small % are no lines

            if (AppWindow.random.nextFloat()<0.9f) {
                shineColor=adjustColorRandom(metalColor,0.7f,1.3f);
                drawMetalShineLine(x,top,bot,shineWid,shineColor);
            }

            x+=(shineWid+(int)(((float)wid*0.03f)+(AppWindow.random.nextFloat()*((float)wid*0.05))));
            if (x>=rgt) break;
        }

        blur(colorData, lft, top, rgt, bot, (textureSize / 150), true);
    }

        //
        // streaks and stains
        //

    private void drawStreakDirtSingle(int lft, int top, int rgt, int bot, float minMix, float addMix, RagColor color, float minXReduce) {
        int x, y, lx, rx, wid, high, idx;
        float xAdd, flx, frx, factor, factor2;

        wid=rgt-lft;
        high=bot-top;

        if ((wid<=0) || (high<=0)) return;

            // random shrink

        xAdd = AppWindow.random.nextFloat(minXReduce);

            // draw the dirt

        flx=(int)lft;
        frx = (int) rgt;

        for (y = top; y != bot; y++) {
            factor=(float)(bot-y)/(float)high;

            lx=(int)flx;
            rx=(int)frx;
            if (lx>=rx) break;

            for (x=lx;x!=rx;x++) {
                factor2 = factor * (minMix + (AppWindow.random.nextFloat(addMix)));

                idx=((y*textureSize)+x)*4;
                colorData[idx] = ((1.0f - factor2) * colorData[idx]) + (color.r * factor2);
                colorData[idx + 1] = ((1.0f - factor2) * colorData[idx + 1]) + (color.g * factor2);
                colorData[idx + 2] = ((1.0f - factor2) * colorData[idx + 2]) + (color.b * factor2);
            }

            flx+=xAdd;
            frx-=xAdd;
        }
    }

    protected void drawStreakDirt(int lft, int top, int rgt, int bot, int additionalStreakCount, float minMix, float maxMix, RagColor color) {
        int n, sx, ex, minWid;
        float addMix;

        addMix=maxMix-minMix;

        // original streak
        drawStreakDirtSingle(lft, top, rgt, bot, minMix, addMix, color, 0.25f);

        // additional streaks
        minWid=(int)((float)(rgt-lft)*0.1f);

        for (n=0;n!=additionalStreakCount;n++) {
            sx=lft+AppWindow.random.nextInt((rgt-minWid)-lft);
            ex=(sx+minWid)+AppWindow.random.nextInt(rgt-(sx+minWid));
            if (sx>=ex) continue;

            drawStreakDirtSingle(sx, top, ex, bot, minMix, addMix, color, 0.1f);
        }
    }

    protected void drawOvalStain(int lft, int top, int rgt, int bot, float outerPercentage, float innerPercentage, float darken) {
        int n, x, y, mx, my, wid, high, idx, stainPixelCount;
        float halfWid, halfHigh, rad, percentageAdd, curPercentage;
        float[] origColorData;

        if ((lft>=rgt) || (top>=bot)) return;

            // we darken against the original
            // bitmap as ovals tend to overdraw

        origColorData=colorData.clone();

            // the drawing size

        wid=(rgt-lft)-1;
        high=(bot-top)-1;         // avoids clipping on bottom from being on wid,high
        mx=lft+(wid/2);
        my=top+(high/2);

            // the random pixel percentages

        if (wid>high) {
            percentageAdd=(innerPercentage-outerPercentage)/wid;
        }
        else {
            percentageAdd=(innerPercentage-outerPercentage)/high;
        }

        curPercentage=outerPercentage;

        // fill the oval
        stainPixelCount = textureSize * 2;

        while ((wid>0) && (high>0)) {

            halfWid=(float)wid*0.5f;
            halfHigh=(float)high*0.5f;

            for (n = 0; n != stainPixelCount; n++) {
                if (AppWindow.random.nextFloat()>curPercentage) continue;

                rad=(float)(Math.PI*2.0)*((float)n*0.001f);

                x=mx+(int)(halfWid*(float)Math.sin(rad));
                if ((x<0) || (x>=textureSize)) continue;

                y=my-(int)(halfHigh*(float)Math.cos(rad));
                if ((y<0) || (y>=textureSize)) continue;

                    // the color

                idx=((y*textureSize)+x)*4;

                colorData[idx]=origColorData[idx]*darken;
                colorData[idx+1]=origColorData[idx+1]*darken;
                colorData[idx+2]=origColorData[idx+2]*darken;
            }

            wid--;
            high--;

            curPercentage+=percentageAdd;
        }
    }

    //
    // grout
    //
    protected void drawGrout() {
        int n, nStain, x, y, wid, high;
        RagColor groutColor;

        // background
        groutColor = getRandomGrayColor(0.2f, 0.4f);
        drawRect(0, 0, textureSize, textureSize, groutColor);
        drawStaticNoiseRect(0, 0, textureSize, textureSize, 0.4f, 0.8f);

        // some stains
        nStain = 5 + AppWindow.random.nextInt(textureSize / 100);

        for (n = 0; n != nStain; n++) {
            wid = 20 + AppWindow.random.nextInt(textureSize / 4);
            x = AppWindow.random.nextInt(textureSize - wid);

            high = 20 + AppWindow.random.nextInt(textureSize / 4);
            y = AppWindow.random.nextInt(textureSize - high);

            drawOvalStain(x, y, (x + wid), (y + high), (0.01f + AppWindow.random.nextFloat(0.01f)), (0.15f + AppWindow.random.nextFloat(0.05f)), (0.3f + AppWindow.random.nextFloat(0.4f)));
        }

        // blur
        blur(colorData, 0, 0, textureSize, textureSize, (textureSize / 500), false);
    }

        //
        // color stripes, gradients, waves
        //

    protected void drawColorStripeHorizontal(int lft,int top,int rgt,int bot,float factor,RagColor baseColor)
    {
        int                 x,y,count,idx;
        float               f,r,g,b,nx,ny,nz;
        RagPoint            normal;

        if ((rgt<=lft) || (bot<=top)) return;

            // the rotating normal

        normal=new RagPoint(0.0f,0.1f,1.0f);
        normal.normalize();

        nx=(normal.x+1.0f)*0.5f;
        ny=(normal.y+1.0f)*0.5f;
        nz=(normal.z+1.0f)*0.5f;

            // write the stripes

        count=1;
        r=g=b=0.0f;

        for (y=top;y!=bot;y++) {

            count--;
            if (count<=0) {
                count=2+AppWindow.random.nextInt(4);

                f=1.0f+((1.0f-(AppWindow.random.nextFloat()*2.0f))*factor);

                r=baseColor.r*f;
                g=baseColor.g*f;
                b=baseColor.b*f;

                ny=(ny/0.5f)-1.0f;
                ny=(1.0f-ny)*0.5f;
            }

            idx=((y*textureSize)+lft)*4;

            for (x=lft;x!=rgt;x++) {
                colorData[idx]=r;
                colorData[idx+1]=g;
                colorData[idx+2]=b;

                normalData[idx]=nx;
                normalData[idx+1]=ny;
                normalData[idx+2]=nz;

                idx+=4;
            }
        }
    }

    protected void drawColorStripeVertical(int lft, int top, int rgt, int bot, float factor, RagColor baseColor) {
        int x, y, count, idx;
        float f, r, g, b, nx, ny, nz;
        RagPoint normal;

        if ((rgt<=lft) || (bot<=top)) return;

            // the rotating normal

        normal=new RagPoint(0.1f,0.0f,1.0f);
        normal.normalize();

        nx=(normal.x+1.0f)*0.5f;
        ny=(normal.y+1.0f)*0.5f;
        nz=(normal.z+1.0f)*0.5f;

            // write the stripes

        count=1;
        r=g=b=0.0f;

        for (x=lft;x!=rgt;x++) {

            count--;
            if (count<=0) {
                count=2+AppWindow.random.nextInt(4);

                f=1.0f+((1.0f-(AppWindow.random.nextFloat()*2.0f))*factor);

                r=baseColor.r*f;
                g=baseColor.g*f;
                b=baseColor.b*f;

                nx=(nx/0.5f)-1.0f;
                nx=(1.0f-nx)*0.5f;
            }

            for (y=top;y!=bot;y++) {
                idx=((y*textureSize)+x)*4;
                colorData[idx]=r;
                colorData[idx+1]=g;
                colorData[idx+2]=b;

                normalData[idx]=nx;
                normalData[idx+1]=ny;
                normalData[idx+2]=nz;
            }
        }
    }

    protected void drawNormalWaveHorizontal(int lft, int top, int rgt, int bot, RagColor color, RagColor lineColor, int waveCount) {
        int x, y, idx, waveIdx, wavePos, waveAdd;
        float nx, ny, nz;

        if ((rgt<=lft) || (bot<=top)) return;

            // the waves

        waveAdd = ((rgt - lft) / waveCount) / 4;
        waveIdx=0;
        wavePos=0;

        nx=ny=nz=0.0f;

        for (x=lft;x!=rgt;x++) {

            switch(waveIdx) {
                case 0:
                    nx=(NORMAL_RIGHT_45.x+1.0f)*0.5f;
                    ny=(NORMAL_RIGHT_45.y+1.0f)*0.5f;
                    nz=(NORMAL_RIGHT_45.z+1.0f)*0.5f;
                    break;
                case 1:
                case 3:
                    nx=(NORMAL_CLEAR.x+1.0f)*0.5f;
                    ny=(NORMAL_CLEAR.y+1.0f)*0.5f;
                    nz=(NORMAL_CLEAR.z+1.0f)*0.5f;
                    break;
                case 2:
                    nx=(NORMAL_LEFT_45.x+1.0f)*0.5f;
                    ny=(NORMAL_LEFT_45.y+1.0f)*0.5f;
                    nz=(NORMAL_LEFT_45.z+1.0f)*0.5f;
                    break;
            }

            for (y=top;y!=bot;y++) {
                idx=((y*textureSize)+x)*4;
                normalData[idx]=nx;
                normalData[idx+1]=ny;
                normalData[idx+2]=nz;
            }

            wavePos++;
            if (wavePos>=waveAdd) {
                wavePos=0;
                waveIdx = (waveIdx + 1) & 0b11;
            }
        }

            // extra lines

        for (x = lft; x < rgt; x += waveAdd) {
            for (y=top;y!=bot;y++) {
                idx=((y*textureSize)+x)*4;
                colorData[idx] = lineColor.r;
                colorData[idx+1]=lineColor.g;
                colorData[idx+2]=lineColor.b;
            }
        }
    }

    protected void drawNormalWaveVertical(int lft, int top, int rgt, int bot, RagColor color, RagColor lineColor, int waveCount) {
        int x, y, idx, waveIdx, wavePos, waveAdd;
        float nx, ny, nz;

        if ((rgt<=lft) || (bot<=top)) return;

            // the waves

        waveAdd = ((bot - top) / waveCount) / 4;
        waveIdx=0;
        wavePos=0;

        nx=ny=nz=0.0f;

        for (y=top;y!=bot;y++) {

            switch(waveIdx) {
                case 0:
                    nx=(NORMAL_BOTTOM_45.x+1.0f)*0.5f;
                    ny=(NORMAL_BOTTOM_45.y+1.0f)*0.5f;
                    nz=(NORMAL_BOTTOM_45.z+1.0f)*0.5f;
                    break;
                case 1:
                    nx=(NORMAL_CLEAR.x+1.0f)*0.5f;
                    ny=(NORMAL_CLEAR.y+1.0f)*0.5f;
                    nz=(NORMAL_CLEAR.z+1.0f)*0.5f;
                    break;
                case 2:
                    nx=(NORMAL_TOP_45.x+1.0f)*0.5f;
                    ny=(NORMAL_TOP_45.y+1.0f)*0.5f;
                    nz=(NORMAL_TOP_45.z+1.0f)*0.5f;
                    break;
            }

            for (x=lft;x!=rgt;x++) {
                idx=((y*textureSize)+x)*4;
                normalData[idx]=nx;
                normalData[idx+1]=ny;
                normalData[idx+2]=nz;
            }

            wavePos++;
            if (wavePos>=waveAdd) {
                wavePos=0;
                waveIdx = (waveIdx + 1) & 0b11;
            }
        }

            // extra lines

        for (y = top; y < bot; y += waveAdd) {
            for (x=lft;x!=rgt;x++) {
                idx=((y*textureSize)+x)*4;
                colorData[idx] = lineColor.r;
                colorData[idx+1]=lineColor.g;
                colorData[idx+2]=lineColor.b;
            }
        }
    }

    protected void drawVerticalGradient(int lft, int top, int rgt, int bot, RagColor topColor, RagColor botColor) {
        int x, y, idx;
        float f, fInv, fHigh;

        if ((rgt <= lft) || (bot <= top)) {
            return;
        }

        fHigh = (float) (bot - top);

        for (y = top; y != bot; y++) {

            f = (float) (y - top) / fHigh;
            fInv = 1.0f - f;

            for (x = lft; x != rgt; x++) {
                idx = ((y * textureSize) + x) * 4;
                colorData[idx] = (topColor.r * fInv) + (botColor.r * f);
                colorData[idx + 1] = (topColor.g * fInv) + (botColor.g * f);
                colorData[idx + 2] = (topColor.b * fInv) + (botColor.b * f);
            }
        }
    }

        //
        // line drawings
        //

    protected void drawLineColor(int x,int y,int x2,int y2,RagColor color)
    {
        int         xLen,yLen,sp,ep,dx,dy,idx,
                    prevX,prevY;
        float       f,slope;

            // the line

        xLen=Math.abs(x2-x);
        yLen=Math.abs(y2-y);

        if ((xLen==0) && (yLen==0)) return;

        if (xLen>yLen) {
            slope=(float)yLen/(float)xLen;

            if (x<x2) {
                sp=x;
                ep=x2;
                f=y;
                slope*=(float)Math.signum(y2-y);
            }
            else {
                sp=x2;
                ep=x;
                f=y2;
                slope*=(float)Math.signum(y-y2);
            }

            prevY=-1;

            for (dx=sp;dx<ep;dx++) {
                dy=(int)f;
                if ((dx>=0) && (dx<textureSize) && (dy>=0) && (dy<textureSize)) {

                    idx=((dy*textureSize)+dx)*4;
                    colorData[idx]=color.r;
                    colorData[idx+1]=color.g;
                    colorData[idx + 2] = color.b;
                    colorData[idx + 3] = 0.0f;

                    if (prevY!=-1) {
                        if (dy!=prevY) {
                            idx=((prevY*textureSize)+dx)*4;
                            colorData[idx]=(colorData[idx]*0.5f)+(color.r*0.5f);
                            colorData[idx+1]=(colorData[idx+1]*0.5f)+(color.g*0.5f);
                            colorData[idx + 2] = (colorData[idx + 2] * 0.5f) + (color.b * 0.5f);
                            colorData[idx + 3] = 0.0f;
                        }
                    }

                    prevY=dy;
                }

                f+=slope;
            }
        }
        else {
            slope=(float)xLen/(float)yLen;

            if (y<y2) {
                sp=y;
                ep=y2;
                f=x;
                slope*=(float)Math.signum(x2-x);
            }
            else {
                sp=y2;
                ep=y;
                f=x2;
                slope*=(float)Math.signum(x-x2);
            }

            prevX=-1;

            for (dy=sp;dy<ep;dy++) {
                dx=(int)f;
                if ((dx>=0) && (dx<textureSize) && (dy>=0) && (dy<textureSize)) {

                    idx=((dy*textureSize)+dx)*4;
                    colorData[idx]=color.r;
                    colorData[idx+1]=color.g;
                    colorData[idx + 2] = color.b;
                    colorData[idx + 3] = 0.0f;

                    if (prevX!=-1) {
                        if (dx!=prevX) {
                            idx=((dy*textureSize)+prevX)*4;
                            colorData[idx]=(colorData[idx]*0.5f)+(color.r*0.5f);
                            colorData[idx+1]=(colorData[idx+1]*0.5f)+(color.g*0.5f);
                            colorData[idx + 2] = (colorData[idx + 2] * 0.5f) + (color.b * 0.5f);
                            colorData[idx + 3] = 0.0f;
                        }
                    }

                    prevX=dx;
                }

                f+=slope;
            }
        }
    }

    protected void drawLineDarken(int x, int y, int x2, int y2, float darken) {
        int xLen, yLen, sp, ep, dx, dy, idx, prevX, prevY;
        float f, slope;

        // the line
        xLen = Math.abs(x2 - x);
        yLen = Math.abs(y2 - y);

        if ((xLen == 0) && (yLen == 0)) {
            return;
        }

        if (xLen > yLen) {
            slope = (float) yLen / (float) xLen;

            if (x < x2) {
                sp = x;
                ep = x2;
                f = y;
                slope *= (float) Math.signum(y2 - y);
            } else {
                sp = x2;
                ep = x;
                f = y2;
                slope *= (float) Math.signum(y - y2);
            }

            prevY = -1;

            for (dx = sp; dx < ep; dx++) {
                dy = (int) f;
                if ((dx >= 0) && (dx < textureSize) && (dy >= 0) && (dy < textureSize)) {

                    idx = ((dy * textureSize) + dx) * 4;
                    colorData[idx] = colorData[idx] * darken;
                    colorData[idx + 1] = colorData[idx + 1] * darken;
                    colorData[idx + 2] = colorData[idx + 2] * darken;
                    colorData[idx + 3] = 0.0f;

                    if (prevY != -1) {
                        if (dy != prevY) {
                            idx = ((prevY * textureSize) + dx) * 4;
                            colorData[idx] = colorData[idx] * (darken * 0.5f);
                            colorData[idx + 1] = colorData[idx + 1] * (darken * 0.5f);
                            colorData[idx + 2] = colorData[idx + 2] * (darken * 0.5f);
                            colorData[idx + 3] = 0.0f;
                        }
                    }

                    prevY = dy;
                }

                f += slope;
            }
        } else {
            slope = (float) xLen / (float) yLen;

            if (y < y2) {
                sp = y;
                ep = y2;
                f = x;
                slope *= (float) Math.signum(x2 - x);
            } else {
                sp = y2;
                ep = y;
                f = x2;
                slope *= (float) Math.signum(x - x2);
            }

            prevX = -1;

            for (dy = sp; dy < ep; dy++) {
                dx = (int) f;
                if ((dx >= 0) && (dx < textureSize) && (dy >= 0) && (dy < textureSize)) {

                    idx = ((dy * textureSize) + dx) * 4;
                    colorData[idx] = colorData[idx] * darken;
                    colorData[idx + 1] = colorData[idx + 1] * darken;
                    colorData[idx + 2] = colorData[idx + 2] * darken;
                    colorData[idx + 3] = 0.0f;

                    if (prevX != -1) {
                        if (dx != prevX) {
                            idx = ((dy * textureSize) + prevX) * 4;
                            colorData[idx] = colorData[idx] * (darken * 0.5f);
                            colorData[idx + 1] = colorData[idx + 1] * (darken * 0.5f);
                            colorData[idx + 2] = colorData[idx + 2] * (darken * 0.5f);
                            colorData[idx + 3] = 0.0f;
                        }
                    }

                    prevX = dx;
                }

                f += slope;
            }
        }
    }

    protected void drawLineColorEmissive(int x, int y, int x2, int y2, RagColor color, RagColor emissiveColor) {
        int xLen, yLen, sp, ep, dx, dy, idx, prevX, prevY;
        float f, slope;

        // the line
        xLen = Math.abs(x2 - x);
        yLen = Math.abs(y2 - y);

        if ((xLen == 0) && (yLen == 0)) {
            return;
        }

        if (xLen > yLen) {
            slope = (float) yLen / (float) xLen;

            if (x < x2) {
                sp = x;
                ep = x2;
                f = y;
                slope *= (float) Math.signum(y2 - y);
            } else {
                sp = x2;
                ep = x;
                f = y2;
                slope *= (float) Math.signum(y - y2);
            }

            prevY = -1;

            for (dx = sp; dx < ep; dx++) {
                dy = (int) f;
                if ((dx >= 0) && (dx < textureSize) && (dy >= 0) && (dy < textureSize)) {

                    idx = ((dy * textureSize) + dx) * 4;
                    colorData[idx] = color.r;
                    colorData[idx + 1] = color.g;
                    colorData[idx + 2] = color.b;
                    colorData[idx + 3] = 0.0f;

                    if (emissiveColor != null) {
                        emissiveData[idx] = emissiveColor.r;
                        emissiveData[idx + 1] = emissiveColor.g;
                        emissiveData[idx + 2] = emissiveColor.b;
                        emissiveData[idx + 3] = 0.0f;
                    }

                    if (prevY != -1) {
                        if (dy != prevY) {
                            idx = ((prevY * textureSize) + dx) * 4;
                            colorData[idx] = (colorData[idx] * 0.5f) + (color.r * 0.5f);
                            colorData[idx + 1] = (colorData[idx + 1] * 0.5f) + (color.g * 0.5f);
                            colorData[idx + 2] = (colorData[idx + 2] * 0.5f) + (color.b * 0.5f);
                            colorData[idx + 3] = 0.0f;

                            if (emissiveColor != null) {
                                emissiveData[idx] = (emissiveData[idx] * 0.5f) + (emissiveColor.r * 0.5f);
                                emissiveData[idx + 1] = (emissiveData[idx + 1] * 0.5f) + (emissiveColor.g * 0.5f);
                                emissiveData[idx + 2] = (emissiveData[idx + 2] * 0.5f) + (emissiveColor.b * 0.5f);
                                emissiveData[idx + 3] = 0.0f;
                            }
                        }
                    }

                    prevY = dy;
                }

                f += slope;
            }
        } else {
            slope = (float) xLen / (float) yLen;

            if (y < y2) {
                sp = y;
                ep = y2;
                f = x;
                slope *= (float) Math.signum(x2 - x);
            } else {
                sp = y2;
                ep = y;
                f = x2;
                slope *= (float) Math.signum(x - x2);
            }

            prevX = -1;

            for (dy = sp; dy < ep; dy++) {
                dx = (int) f;
                if ((dx >= 0) && (dx < textureSize) && (dy >= 0) && (dy < textureSize)) {

                    idx = ((dy * textureSize) + dx) * 4;
                    colorData[idx] = color.r;
                    colorData[idx + 1] = color.g;
                    colorData[idx + 2] = color.b;
                    colorData[idx + 3] = 0.0f;

                    if (emissiveColor != null) {
                        emissiveData[idx] = emissiveColor.r;
                        emissiveData[idx + 1] = emissiveColor.g;
                        emissiveData[idx + 2] = emissiveColor.b;
                        emissiveData[idx + 3] = 0.0f;
                    }

                    if (prevX != -1) {
                        if (dx != prevX) {
                            idx = ((dy * textureSize) + prevX) * 4;
                            colorData[idx] = (colorData[idx] * 0.5f) + (color.r * 0.5f);
                            colorData[idx + 1] = (colorData[idx + 1] * 0.5f) + (color.g * 0.5f);
                            colorData[idx + 2] = (colorData[idx + 2] * 0.5f) + (color.b * 0.5f);
                            colorData[idx + 3] = 0.0f;

                            if (emissiveColor != null) {
                                emissiveData[idx] = (emissiveData[idx] * 0.5f) + (emissiveColor.r * 0.5f);
                                emissiveData[idx + 1] = (emissiveData[idx + 1] * 0.5f) + (emissiveColor.g * 0.5f);
                                emissiveData[idx + 2] = (emissiveData[idx + 2] * 0.5f) + (emissiveColor.b * 0.5f);
                                emissiveData[idx + 3] = 0.0f;
                            }
                        }
                    }

                    prevX = dx;
                }

                f += slope;
            }
        }
    }

    protected void drawLineAlpha(int x, int y, int x2, int y2, float alpha) {
        int xLen, yLen, sp, ep, dx, dy, idx,
                prevX, prevY;
        float f, slope;

        // the line
        xLen = Math.abs(x2 - x);
        yLen = Math.abs(y2 - y);

        if ((xLen == 0) && (yLen == 0)) {
            return;
        }

        if (xLen > yLen) {
            slope = (float) yLen / (float) xLen;

            if (x < x2) {
                sp = x;
                ep = x2;
                f = y;
                slope *= (float) Math.signum(y2 - y);
            } else {
                sp = x2;
                ep = x;
                f = y2;
                slope *= (float) Math.signum(y - y2);
            }

            prevY = -1;

            for (dx = sp; dx < ep; dx++) {
                dy = (int) f;
                if ((dx >= 0) && (dx < textureSize) && (dy >= 0) && (dy < textureSize)) {

                    idx = ((dy * textureSize) + dx) * 4;
                    colorData[idx + 3] = alpha;

                    if (prevY != -1) {
                        if (dy != prevY) {
                            idx = ((prevY * textureSize) + dx) * 4;
                            colorData[idx + 3] = (colorData[idx + 3] * 0.5f) + (alpha * 0.5f);
                        }
                    }

                    prevY = dy;
                }

                f += slope;
            }
        } else {
            slope = (float) xLen / (float) yLen;

            if (y < y2) {
                sp = y;
                ep = y2;
                f = x;
                slope *= (float) Math.signum(x2 - x);
            } else {
                sp = y2;
                ep = y;
                f = x2;
                slope *= (float) Math.signum(x - x2);
            }

            prevX = -1;

            for (dy = sp; dy < ep; dy++) {
                dx = (int) f;
                if ((dx >= 0) && (dx < textureSize) && (dy >= 0) && (dy < textureSize)) {

                    idx = ((dy * textureSize) + dx) * 4;
                    colorData[idx + 3] = alpha;

                    if (prevX != -1) {
                        if (dx != prevX) {
                            idx = ((dy * textureSize) + prevX) * 4;
                            colorData[idx + 3] = (colorData[idx + 3] * 0.5f) + (alpha * 0.5f);
                        }
                    }

                    prevX = dx;
                }

                f += slope;
            }
        }
    }

    protected void drawLineNormal(int x, int y, int x2, int y2, RagPoint normal) {
        int xLen, yLen, sp, ep, dx, dy, idx, prevX, prevY;
        float f, slope, r, g, b;

        // get normals in correct format
        r=(normal.x+1.0f)*0.5f;
        g=(normal.y+1.0f)*0.5f;
        b=(normal.z+1.0f)*0.5f;

        // the line
        xLen=Math.abs(x2-x);
        yLen=Math.abs(y2-y);

        if ((xLen==0) && (yLen==0)) return;

        if (xLen>yLen) {
            slope=(float)yLen/(float)xLen;

            if (x<x2) {
                sp=x;
                ep=x2;
                f=y;
                slope*=(float)Math.signum(y2-y);
            }
            else {
                sp=x2;
                ep=x;
                f=y2;
                slope*=(float)Math.signum(y-y2);
            }

            prevY=-1;

            for (dx=sp;dx<ep;dx++) {
                dy=(int)f;
                if ((dx>=0) && (dx<textureSize) && (dy>=0) && (dy<textureSize)) {

                    idx=((dy*textureSize)+dx)*4;
                    normalData[idx]=(normalData[idx]*0.5f)+(r*0.5f);
                    normalData[idx+1]=(normalData[idx+1]*0.5f)+(g*0.5f);
                    normalData[idx+2]=(normalData[idx+2]*0.5f)+(b*0.5f);

                    if (prevY!=-1) {
                        if (dy!=prevY) {
                            idx=((prevY*textureSize)+dx)*4;
                            normalData[idx]=(normalData[idx]*0.5f)+(r*0.5f);
                            normalData[idx+1]=(normalData[idx+1]*0.5f)+(g*0.5f);
                            normalData[idx+2]=(normalData[idx+2]*0.5f)+(b*0.5f);
                        }
                    }

                    prevY=dy;
                }

                f+=slope;
            }
        }
        else {
            slope=(float)xLen/(float)yLen;

            if (y<y2) {
                sp=y;
                ep=y2;
                f=x;
                slope*=(float)Math.signum(x2-x);
            }
            else {
                sp=y2;
                ep=y;
                f=x2;
                slope*=(float)Math.signum(x-x2);
            }

            prevX=-1;

            for (dy=sp;dy<ep;dy++) {
                dx=(int)f;
                if ((dx>=0) && (dx<textureSize) && (dy>=0) && (dy<textureSize)) {

                    idx=((dy*textureSize)+dx)*4;
                    normalData[idx]=(normalData[idx]*0.5f)+(r*0.5f);
                    normalData[idx+1]=(normalData[idx+1]*0.5f)+(g*0.5f);
                    normalData[idx+2]=(normalData[idx+2]*0.5f)+(b*0.5f);

                    if (prevX!=-1) {
                        if (dx!=prevX) {
                            idx=((dy*textureSize)+prevX)*4;
                            normalData[idx]=(normalData[idx]*0.5f)+(r*0.5f);
                            normalData[idx+1]=(normalData[idx+1]*0.5f)+(g*0.5f);
                            normalData[idx+2]=(normalData[idx+2]*0.5f)+(b*0.5f);
                        }
                    }

                    prevX=dx;
                }

                f+=slope;
            }
        }
    }

    protected void drawRandomLine(int x,int y,int x2,int y2,int clipLft,int clipTop,int clipRgt,int clipBot,int lineVariant,RagColor color,boolean antiAlias)
    {
        int         n,sx,sy,ex,ey,xAdd,yAdd,r,
                    segCount;
        boolean     horizontal;
        RagColor    aliasColor;

        segCount=2+AppWindow.random.nextInt(5);
        horizontal=Math.abs(x2-x)>Math.abs(y2-y);

        xAdd=(x2-x)/segCount;
        yAdd=(y2-y)/segCount;

        sx=x;
        sy=y;

        for (n=0;n!=segCount;n++) {

            if ((n+1)==segCount) {
                ex=x2;
                ey=y2;
            }
            else {
                ex=sx+xAdd;
                ey=sy+yAdd;

                if ((n&0x1)==0) {      // straighten out line every other variation
                    r=lineVariant-AppWindow.random.nextInt(lineVariant*2);

                    if (horizontal) {
                        ey+=r;
                    }
                    else {
                        ex+=r;
                    }
                }
            }

            if (ex<clipLft) ex=clipLft;
            if (ex>clipRgt) ex=clipRgt;
            if (ey<clipTop) ey=clipTop;
            if (ey>clipBot) ey=clipBot;

            drawLineColor(sx,sy,ex,ey,color);

            if (horizontal) {
                drawLineNormal(sx,sy,ex,ey,NORMAL_CLEAR);
                if (antiAlias) {
                    aliasColor=adjustColor(color,0.9f);
                    drawLineColor(sx,(sy-1),ex,(ey-1),aliasColor);
                    drawLineColor(sx,(sy+1),ex,(ey+1),aliasColor);
                    drawLineNormal(sx,(sy-1),ex,(ey-1),NORMAL_BOTTOM_45);
                    drawLineNormal(sx,(sy+1),ex,(ey+1),NORMAL_TOP_45);
                }
            }
            else {
                drawLineNormal(sx,sy,ex,ey,NORMAL_CLEAR);
                if (antiAlias) {
                    aliasColor=adjustColor(color,0.9f);
                    drawLineColor((sx-1),sy,(ex-1),ey,aliasColor);
                    drawLineColor((sx+1),sy,(ex+1),ey,aliasColor);
                    drawLineNormal((sx-1),sy,(ex-1),ey,NORMAL_RIGHT_45);
                    drawLineNormal((sx+1),sy,(ex+1),ey,NORMAL_LEFT_45);
                }
            }

            sx=ex;
            sy=ey;
        }
    }

    protected void drawHorizontalCrack(int y,int x,int x2,int clipTop,int clipBot,int lineDir,int lineVariant,RagColor color,boolean canSplit)
    {
        int     n,sx,sy,ex,ey,segCount,xAdd;

        segCount=2+AppWindow.random.nextInt(5);
        xAdd=(x2-x)/segCount;

        sx=ex=x;
        sy=ey=y;

        for (n=0;n!=segCount;n++) {

            if ((n+1)==segCount) {
                ex=x2;
            }
            else {
                ey=sy+(AppWindow.random.nextInt(lineVariant)*lineDir);
                ex=sx+xAdd;
            }

            if (ey<clipTop) ey=clipTop;
            if (ey>clipBot) ey=clipBot;

            if (sx==ex) return;

            drawLineColor(sx,sy,ex,ey,color);
            drawLineNormal(sx,sy,ex,ey,NORMAL_CLEAR);
            drawLineNormal(sx,(sy-1),ex,(ey-1),NORMAL_BOTTOM_45);
            drawLineNormal(sx,(sy+1),ex,(ey+1),NORMAL_TOP_45);

            if ((ey==clipTop) || (ey==clipBot)) break;

            if ((canSplit) && (AppWindow.random.nextBoolean())) {
                if (lineDir>0) {
                    drawHorizontalCrack(ey,ex,x2,clipTop,clipBot,-lineDir,lineVariant,color,false);
                }
                else {
                    drawHorizontalCrack(ey,ex,x2,clipTop,clipBot,-lineDir,lineVariant,color,false);
                }

                canSplit=false;
            }

            sx=ex;
            sy=ey;
        }
    }

    protected void drawVerticalCrack(int x,int y,int y2,int clipLft,int clipRgt,int lineDir,int lineVariant,RagColor color,boolean canSplit)
    {
        int     n,sx,sy,ex,ey,segCount,yAdd;

        segCount=2+AppWindow.random.nextInt(5);
        yAdd=(y2-y)/segCount;

        sx=ex=x;
        sy=ey=y;

        for (n=0;n!=segCount;n++) {

            if ((n+1)==segCount) {
                ey=y2;
            }
            else {
                ex=sx+(AppWindow.random.nextInt(lineVariant)*lineDir);
                ey=sy+yAdd;
            }

            if (ex<clipLft) ex=clipLft;
            if (ex>clipRgt) ex=clipRgt;

            if (sy==ey) return;

            drawLineColor(sx,sy,ex,ey,color);
            drawLineNormal(sx,sy,ex,ey,NORMAL_CLEAR);
            drawLineNormal((sx-1),sy,(ex-1),ey,NORMAL_RIGHT_45);
            drawLineNormal((sx+1),sy,(ex+1),ey,NORMAL_LEFT_45);

            if ((ex==clipLft) || (ex==clipRgt)) break;

            if ((canSplit) && (AppWindow.random.nextBoolean())) {
                if (lineDir>0) {
                    drawVerticalCrack(ex,ey,y2,clipLft,clipRgt,-lineDir,lineVariant,color,false);
                }
                else {
                    drawVerticalCrack(ex,ey,y2,clipLft,clipRgt,-lineDir,lineVariant,color,false);
                }

                canSplit=false;
            }

            sx=ex;
            sy=ey;
        }
    }

    protected void drawSimpleCrack(int sx,int sy,int ex,int ey,int segCount,int lineXVarient,int lineYVarient,RagColor color)
    {
        int n, dx, dy, dx2, dy2;

        if ((Math.abs(lineXVarient) <= 1) || (Math.abs(lineYVarient) <= 1)) {
            return;
        }

        dx=sx;
        dy=sy;

        for (n=0;n!=segCount;n++) {

            if ((n+1)==segCount) {
                dx2=ex;
                dy2=ey;
            }
            else {
                dx2=(int)(sx+((float)((ex-sx)*(n+1))/(float)segCount))+(AppWindow.random.nextInt(Math.abs(lineXVarient))*(int)Math.signum(lineXVarient));
                dy2=(int)(sy+((float)((ey-sy)*(n+1))/(float)segCount))+(AppWindow.random.nextInt(Math.abs(lineYVarient))*(int)Math.signum(lineYVarient));
            }

            drawLineColor(dx,dy,dx2,dy2,color);
            drawLineNormal(dx,dy,dx2,dy2,NORMAL_CLEAR);
            drawLineNormal((dx-1),dy,(dx2-1),dy2,NORMAL_RIGHT_45);
            drawLineNormal((dx+1),dy,(dx2+1),dy2,NORMAL_LEFT_45);

            dx=dx2;
            dy=dy2;
        }
    }

    protected void drawHorizontalRidge(int y, int x, int x2, int wid, float darken) {
        int dy;

        wid = wid / 2;

        for (dy = (y - wid); dy < y; dy++) {
            drawLineDarken(x, dy, x2, dy, darken);
            drawLineNormal(x, dy, x2, dy, NORMAL_TOP_45);
        }

        for (dy = y; dy < (y + wid); dy++) {
            drawLineDarken(x, dy, x2, dy, darken);
            drawLineNormal(x, dy, x2, dy, NORMAL_BOTTOM_45);
        }
    }

    protected void drawVerticalRidge(int x, int y, int y2, int wid, float darken) {
        int dx;

        wid = wid / 2;

        for (dx = (x - wid); dx < x; dx++) {
            drawLineDarken(dx, y, dx, y2, darken);
            drawLineNormal(dx, y, dx, y2, NORMAL_TOP_45);
        }

        for (dx = x; dx < (x + wid); dx++) {
            drawLineDarken(dx, y, dx, y2, darken);
            drawLineNormal(dx, y, dx, y2, NORMAL_BOTTOM_45);
        }
    }

    //
    // random characters
    //
    protected void generateRandomCharacterLine(int dx, int dy, int charWid, int charHigh, int charPadding, int charCount, RagColor charColor, RagColor emissiveCharColor) {
        int x;

        // draw characters in a figure 8 with two cross bars
        for (x = 0; x < charCount; x++) {

            // top, middle, bottom
            if (AppWindow.random.nextBoolean()) {
                this.drawLineColorEmissive(dx, dy, (dx + charWid), dy, charColor, emissiveCharColor);
            }
            if (AppWindow.random.nextBoolean()) {
                this.drawLineColorEmissive(dx, (dy + (charHigh / 2)), (dx + charWid), (dy + (charHigh / 2)), charColor, emissiveCharColor);
            }
            if (AppWindow.random.nextBoolean()) {
                this.drawLineColorEmissive(dx, (dy + charHigh), (dx + charWid), (dy + charHigh), charColor, emissiveCharColor);
            }

            // left and right
            if (AppWindow.random.nextBoolean()) {
                this.drawLineColorEmissive(dx, dy, dx, (dy + (charHigh / 2)), charColor, emissiveCharColor);
            }
            if (AppWindow.random.nextBoolean()) {
                this.drawLineColorEmissive((dx + charWid), dy, (dx + charWid), (dy + (charHigh / 2)), charColor, emissiveCharColor);
            }
            if (AppWindow.random.nextBoolean()) {
                this.drawLineColorEmissive(dx, (dy + (charHigh / 2)), dx, (dy + charHigh), charColor, emissiveCharColor);
            }
            if (AppWindow.random.nextBoolean()) {
                this.drawLineColorEmissive((dx + charWid), (dy + (charHigh / 2)), (dx + charWid), (dy + charHigh), charColor, emissiveCharColor);
            }

            // two cross overs
            if (AppWindow.random.nextBoolean()) {
                this.drawLineColorEmissive(dx, dy, (dx + charWid), (dy + (charHigh / 2)), charColor, emissiveCharColor);
            }
            if (AppWindow.random.nextBoolean()) {
                this.drawLineColorEmissive((dx + charWid), (dy + (charHigh / 2)), dx, (dy + charHigh), charColor, emissiveCharColor);
            }

            dx += (charWid + charPadding);
        }
    }

    //
    // planks
    //
    protected void generateWoodDrawBoard(int lft, int top, int rgt, int bot, int edgeSize, RagColor woodColor) {
        int n, chipCount, stainCount, stainMinSize, lx, ty, rx, by, sz;
        int x1, y1, x2, y2, x3, y3, maxChipSize, maxChipLen;
        float f;
        boolean vert;
        RagColor color;

        color = adjustColorRandom(woodColor, 0.7f, 1.2f);
        vert = ((bot - top) > (rgt - lft));

        // board background
        drawRect(lft, top, rgt, bot, color);

        // stripes and a noise overlay
        f = 0.1f + AppWindow.random.nextFloat(0.3f);
        if (vert) {
            drawColorStripeVertical((lft + edgeSize), (top + edgeSize), (rgt - edgeSize), (bot - edgeSize), f, color);
        } else {
            drawColorStripeHorizontal((lft + edgeSize), (top + edgeSize), (rgt - edgeSize), (bot - edgeSize), f, color);
        }

        // stains
        stainCount = AppWindow.random.nextInt(10);
        stainMinSize = textureSize / 50;

        for (n = 0; n != stainCount; n++) {
            sz = stainMinSize + AppWindow.random.nextInt((rgt - lft) / (stainMinSize / 2));
            lx = lft + AppWindow.random.nextInt((rgt - lft) - sz);
            rx = lx + sz;

            sz = stainMinSize + AppWindow.random.nextInt((bot - top) / (stainMinSize / 2));
            ty = top + AppWindow.random.nextInt((bot - top) - sz);
            by = ty + sz;

            drawOvalStain(lx, ty, rx, by, (0.01f + AppWindow.random.nextFloat(0.01f)), (0.15f + AppWindow.random.nextFloat(0.05f)), (0.8f + AppWindow.random.nextFloat(0.2f)));
        }

        // some noise
        drawPerlinNoiseRect((lft + edgeSize), (top + edgeSize), (rgt - edgeSize), (bot - edgeSize), 0.8f, 1.2f);
        drawStaticNoiseRect((lft + edgeSize), (top + edgeSize), (rgt - edgeSize), (bot - edgeSize), 0.9f, 1.0f);

        // blur both the color and the normal
        blur(colorData, (lft + edgeSize), (top + edgeSize), (rgt - edgeSize), (bot - edgeSize), (textureSize / 250), true);
        blur(normalData, (lft + edgeSize), (top + edgeSize), (rgt - edgeSize), (bot - edgeSize), (textureSize / 100), true);

        // chipped ends
        maxChipLen = edgeSize * 3;
        maxChipSize = edgeSize * 2;

        // lft-top end
        chipCount = ((lft < 0) || (top < 0)) ? 0 : AppWindow.random.nextInt(5);
        for (n = 0; n != chipCount; n++) {

            if (vert) {
                x1 = (lft + edgeSize) + (AppWindow.random.nextInt((rgt - lft) - maxChipSize));
                y1 = top + edgeSize;
                x2 = x1 + (edgeSize + AppWindow.random.nextInt(maxChipSize));
                y2 = top + edgeSize;
                x3 = (x1 + x2) / 2;
                y3 = y2 + (edgeSize + AppWindow.random.nextInt(maxChipLen));
            } else {
                x1 = lft + edgeSize;
                y1 = (top + edgeSize) + (AppWindow.random.nextInt((bot - top) - maxChipSize));
                x2 = lft + edgeSize;
                y2 = y1 + (edgeSize + AppWindow.random.nextInt(maxChipSize));
                x3 = x2 + (edgeSize + AppWindow.random.nextInt(maxChipLen));
                y3 = (y1 + y2) / 2;
            }

            darkenTriangle(x1, y1, x2, y2, x3, y3, true, (0.4f + AppWindow.random.nextFloat(0.2f)));
        }

        // rgt-bot end
        chipCount = ((rgt > textureSize) || (bot > textureSize)) ? 0 : AppWindow.random.nextInt(5);
        for (n = 0; n != chipCount; n++) {

            if (vert) {
                x1 = (lft + edgeSize) + (AppWindow.random.nextInt((rgt - lft) - maxChipSize));
                y1 = bot - edgeSize;
                x2 = x1 + (edgeSize + AppWindow.random.nextInt(maxChipSize));
                y2 = bot - edgeSize;
                x3 = (x1 + x2) / 2;
                y3 = y2 - (edgeSize + AppWindow.random.nextInt(maxChipLen));
            } else {
                x1 = rgt - edgeSize;
                y1 = (top + edgeSize) + (AppWindow.random.nextInt((bot - top) - maxChipSize));
                x2 = rgt - edgeSize;
                y2 = y1 + (edgeSize + AppWindow.random.nextInt(maxChipSize));
                x3 = x2 + (edgeSize + AppWindow.random.nextInt(maxChipLen));
                y3 = (y1 + y2) / 2;
            }

            darkenTriangle(x1, y1, x2, y2, x3, y3, true, (0.4f + AppWindow.random.nextFloat(0.2f)));
        }

        // the board edge
        draw3DDarkenFrameRect(lft, top, rgt, bot, edgeSize, (0.65f + AppWindow.random.nextFloat(0.1f)), true);
    }

    protected void generateWoodDrawBoardNails(int lft, int top, int rgt, int bot, int edgeSize, int nailSize, boolean doubleNail) {
        int x, y, wid, high;
        int lft2, top2, rgt2, bot2;

        lft2 = lft + edgeSize;
        rgt2 = rgt - edgeSize;
        top2 = top + edgeSize;
        bot2 = bot - edgeSize;

        wid = rgt - lft;
        high = bot - top;

        if (nailSize > 10) {
            nailSize = 10;
        }

        // square, so four nails
        if (wid == high) {
            this.drawSimpleOval(colorData, (lft2 + nailSize), (top2 + nailSize), (lft2 + (nailSize * 2)), (top2 + (nailSize * 2)), COLOR_BLACK);
            this.drawSimpleOval(colorData, (rgt2 - (nailSize * 2)), (top2 + nailSize), (rgt2 - nailSize), (top2 + (nailSize * 2)), COLOR_BLACK);
            this.drawSimpleOval(colorData, (lft2 + nailSize), (bot2 - (nailSize * 2)), (lft2 + (nailSize * 2)), (bot2 - nailSize), COLOR_BLACK);
            this.drawSimpleOval(colorData, (rgt2 - (nailSize * 2)), (bot2 - (nailSize * 2)), (rgt2 - nailSize), (bot2 - nailSize), COLOR_BLACK);
            return;
        }

        // vertical
        if (high > wid) {
            if (doubleNail) {
                if (top >= 0) {
                    this.drawSimpleOval(colorData, (lft2 + nailSize), (top2 + nailSize), (lft2 + (nailSize * 2)), (top2 + (nailSize * 2)), COLOR_BLACK);
                    this.drawSimpleOval(colorData, (rgt2 - (nailSize * 2)), (top2 + nailSize), (rgt2 - nailSize), (top2 + (nailSize * 2)), COLOR_BLACK);
                }
                if (bot <= textureSize) {
                    this.drawSimpleOval(colorData, (lft2 + nailSize), (bot2 - (nailSize * 2)), (lft2 + (nailSize * 2)), (bot2 - nailSize), COLOR_BLACK);
                    this.drawSimpleOval(colorData, (rgt2 - (nailSize * 2)), (bot2 - (nailSize * 2)), (rgt2 - nailSize), (bot2 - nailSize), COLOR_BLACK);
                }
            } else {
                x = ((lft + rgt) / 2) - (nailSize / 2);
                if (top >= 0) {
                    this.drawSimpleOval(colorData, x, (top2 + nailSize), (x + nailSize), (top2 + (nailSize * 2)), COLOR_BLACK);
                }
                if (bot <= textureSize) {
                    this.drawSimpleOval(colorData, x, (bot2 - (nailSize * 2)), (x + nailSize), (bot2 - nailSize), COLOR_BLACK);
                }
            }

            return;
        }

        // horizontal
        if (doubleNail) {
            if (lft >= 0) {
                this.drawSimpleOval(colorData, (lft2 + nailSize), (top2 + nailSize), (lft2 + (nailSize * 2)), (top2 + (nailSize * 2)), COLOR_BLACK);
                this.drawSimpleOval(colorData, (lft2 + nailSize), (bot2 - (nailSize * 2)), (lft2 + (nailSize * 2)), (bot2 - nailSize), COLOR_BLACK);
            }
            if (rgt <= textureSize) {
                this.drawSimpleOval(colorData, (rgt2 - (nailSize * 2)), (top2 + nailSize), (rgt2 - nailSize), (top2 + (nailSize * 2)), COLOR_BLACK);
                this.drawSimpleOval(colorData, (rgt2 - (nailSize * 2)), (bot2 - (nailSize * 2)), (rgt2 - nailSize), (bot2 - nailSize), COLOR_BLACK);
            }
        } else {
            y = ((top + bot) / 2) - (nailSize / 2);
            if (lft >= 0) {
                this.drawSimpleOval(colorData, (lft2 + nailSize), y, (lft2 + (nailSize * 2)), (y + nailSize), COLOR_BLACK);
            }
            if (rgt <= textureSize) {
                this.drawSimpleOval(colorData, (rgt2 - (nailSize * 2)), y, (rgt2 - nailSize), (y + nailSize), COLOR_BLACK);
            }
        }
    }

    //
    // overlays
    //
    public void generateSpotsOverlay() {
        int x, y, dx, dy, rx, ry;
        int n, extraCount;
        int spotMin, xCount, yCount, xAdd, yAdd, spotSize, jiggleSize;

        xCount = 4 + AppWindow.random.nextInt(4);
        xAdd = textureSize / xCount;

        yCount = 4 + AppWindow.random.nextInt(4);
        yAdd = textureSize / yCount;

        spotMin = (xAdd > yAdd) ? (yAdd / 2) : (xAdd / 2);
        jiggleSize = spotMin / 10;
        if (jiggleSize < 10) {
            jiggleSize = 10;
        }

        dy = 0;

        for (y = 0; y != yCount; y++) {

            dx = 0;

            for (x = 0; x != xCount; x++) {
                spotSize = spotMin + AppWindow.random.nextInt(spotMin);

                extraCount = 5 + AppWindow.random.nextInt(5);

                for (n = 0; n != extraCount; n++) {
                    rx = dx + (AppWindow.random.nextInt(jiggleSize * 2) - jiggleSize);
                    ry = dy + (AppWindow.random.nextInt(jiggleSize * 2) - jiggleSize);
                    drawDarkenOval(colorData, rx, ry, (rx + spotSize), (ry + spotSize), (0.95f + (AppWindow.random.nextFloat() * 0.05f)));
                }

                dx += xAdd;
            }

            dy += yAdd;
        }
    }

    public void generateStainsOverlay() {
        int n, k, lft, top, rgt, bot;
        int stainCount, stainSize;
        int xSize, ySize, markCount;
        float outerPercentage, innerPercentage, darken;

        stainCount = AppWindow.random.nextInt(8);
        stainSize = (int) ((float) textureSize * 0.1f);

        for (n = 0; n != stainCount; n++) {
            lft = AppWindow.random.nextInt(textureSize);
            xSize = stainSize + AppWindow.random.nextInt(stainSize);

            top = AppWindow.random.nextInt(textureSize);
            ySize = stainSize + AppWindow.random.nextInt(stainSize);

            darken = 0.7f + AppWindow.random.nextFloat(0.25f);
            outerPercentage = 0.01f + AppWindow.random.nextFloat(0.05f);
            innerPercentage = 0.1f + AppWindow.random.nextFloat(0.2f);
            markCount = 2 + AppWindow.random.nextInt(4);

            for (k = 0; k != markCount; k++) {
                rgt = lft + xSize;
                if (rgt >= textureSize) {
                    rgt = textureSize - 1;
                }
                bot = top + ySize;
                if (bot >= textureSize) {
                    bot = textureSize - 1;
                }

                drawOvalStain(lft, top, rgt, bot, outerPercentage, innerPercentage, darken);

                lft += (AppWindow.random.nextBoolean()) ? (-(xSize / 3)) : (xSize / 3);
                top += (AppWindow.random.nextBoolean()) ? (-(ySize / 3)) : (ySize / 3);
                xSize = (int) ((float) xSize * 0.8f);
                ySize = (int) ((float) ySize * 0.8f);
            }
        }
    }

    //
    // patterns
    //
    public ArrayList<RagRect> createBlockPattern() {
        int[][] grid;
        int n, x, y, gx, gy, gridSize, gridPixelSize, tryCount;
        boolean badSpot;
        int[] sz;
        int[][] blockSizes = {{2, 2}, {3, 3}, {4, 4}, {2, 1}, {1, 2}, {3, 1}, {1, 3}, {2, 3}, {3, 2}};
        ArrayList<RagRect> rects;

        rects = new ArrayList<>();

        gridSize = AppWindow.random.nextBoolean() ? 8 : 4;
        grid = new int[gridSize][gridSize];
        gridPixelSize = textureSize / gridSize;

        for (n = 0; n != gridSize; n++) {
            // random block size
            sz = blockSizes[AppWindow.random.nextInt(blockSizes.length)];

            // find a place for block
            tryCount = 0;
            while (tryCount < 10) {
                x = AppWindow.random.nextInt(gridSize - (sz[0] - 1));
                y = AppWindow.random.nextInt(gridSize - (sz[1] - 1));

                badSpot = false;

                for (gy = y; gy < (y + sz[1]); gy++) {
                    for (gx = x; gx < (x + sz[0]); gx++) {
                        if (grid[gx][gy] != 0) {
                            badSpot = true;
                            break;
                        }
                    }
                }

                if (!badSpot) {
                    for (gy = y; gy < (y + sz[1]); gy++) {
                        for (gx = x; gx < (x + sz[0]); gx++) {
                            grid[gx][gy] = 1;
                        }
                    }

                    rects.add(new RagRect((x * gridPixelSize), (y * gridPixelSize), ((x * gridPixelSize) + (gridPixelSize * sz[0])), ((y * gridPixelSize) + (gridPixelSize * sz[1]))));
                    break;
                }

                tryCount++;
            }

        }

        // fill in any missing blocks
        for (y = 0; y != gridSize; y++) {
            for (x = 0; x != gridSize; x++) {
                if (grid[x][y] != 0) {
                    continue;
                }

                rects.add(new RagRect((x * gridPixelSize), (y * gridPixelSize), ((x * gridPixelSize) + gridPixelSize), ((y * gridPixelSize) + gridPixelSize)));
            }
        }

        return (rects);
    }

    //
    // misc
    //
    public void drawScrew(int x, int y, RagColor screwColor, RagColor outlineColor, int screwSize, int edgeSize) {
        int mx, my;

        drawOval(x, y, (x + screwSize), (y + screwSize), 0.0f, 1.0f, 0.0f, 0.0f, edgeSize, 0.8f, screwColor, 0.5f, false, false, 1.0f, 0.0f);
        drawFrameOval(x, y, (x + screwSize), (y + screwSize), 0.0f, 0.0f, outlineColor);

        if (AppWindow.random.nextBoolean()) {
            my = y + (screwSize / 2);
            drawLineColor(x, my, (x + screwSize), my, outlineColor);
            drawLineNormal(x, my, (x + screwSize), my, NORMAL_TOP_45);
        } else {
            mx = x + (screwSize / 2);
            drawLineColor(mx, y, mx, (y + screwSize), outlineColor);
            drawLineNormal(mx, y, mx, (y + screwSize), NORMAL_LEFT_45);
        }
    }

        //
        // metallic-roughness routines
        //

    public void createMetallicRoughnessMap(float contrast,float clamp)
    {
        int     n,idx,pixelSize;
        float   f,min,max,expandFactor,contrastFactor;

        pixelSize=textureSize*textureSize;

            // get the contrast factor

        contrastFactor=(1.02f*(contrast+1.0f))/(1.0f*(1.02f-contrast));

            // find a min-max across the entire map, we do this
            // so we can readjust the contrast to be 0..1

        min=max=(colorData[0]+colorData[1]+colorData[2])*0.33f;

        idx=0;

        for (n=0;n!=pixelSize;n++) {
            f=(colorData[idx]+colorData[idx+1]+colorData[idx+2])*0.33f;
            if (f<min) min=f;
            if (f>max) max=f;

            idx+=4;
        }

        if (min>=max) {
            expandFactor=0.0f;
            min=0.0f;
        }
        else {
            expandFactor=1.0f/(max-min);
        }

            // now run the contrast to make
            // the metallic in blue channel

        idx=0;

        for (n=0;n!=pixelSize;n++) {

                // get the pixel back into 0..1

            f=(colorData[idx]+colorData[idx+1]+colorData[idx+2])*0.33f;
            f=(f-min)*expandFactor;

                // apply the contrast and
                // clamp it

            f=((contrastFactor*(f-0.5f))+0.5f);
            if (f<0.0f) f=0.0f;
            if (f>1.0f) f=1.0f;

            f*=clamp;

            metallicRoughnessData[idx+2]=f;
            idx+=4;
        }
    }

        //
        // alpha utilities
        //

    protected void setImageAlpha(float a)
    {
        int     n;

        for (n=0;n!=colorData.length;n+=4) {
            colorData[n+3]=a;
        }
    }

        //
        // clear and write images
        //

    public void clearImageData(float[] imgData, float r, float g, float b, float a)    {
        int     n;

        for (n=0;n!=imgData.length;n+=4) {
            imgData[n]=r;
            imgData[n+1]=g;
            imgData[n+2]=b;
            imgData[n+3]=a;
        }
    }

    private void clampImageData(float[] imgData)
    {
        int     n;

        for (n=0;n!=imgData.length;n++) {
            if (imgData[n]<0.0f) imgData[n]=0.0f;
            if (imgData[n]>1.0f) imgData[n]=1.0f;
        }
    }

    private byte[] imageDataToBytes(float[] imgData, boolean includeAlpha) {
        int n,idx;
        byte[] imgDataByte;

        // image data if 4 floats per pixel, so covert to bytes

        if (includeAlpha) {
            imgDataByte=new byte[imgData.length];

            for (n=0;n!=imgData.length;n++) {
                imgDataByte[n]=(byte)((int)(imgData[n]*255.0f));
            }
        }
        else {
            idx=0;
            imgDataByte=new byte[(imgData.length/4)*3];

            for (n=0;n!=imgData.length;n+=4) {
                imgDataByte[idx++]=(byte)((int)(imgData[n]*255.0f));
                imgDataByte[idx++]=(byte)((int)(imgData[n+1]*255.0f));
                imgDataByte[idx++]=(byte)((int)(imgData[n+2]*255.0f));
            }
        }

        return(imgDataByte);
    }

    private void writeImageData(float[] imgData, boolean includeAlpha, String path) {
        int channelCount;
        int[] channelOffsets;
        byte[] imgDataByte;
        DataBuffer dataBuffer;
        WritableRaster writeRaster;
        ColorModel colorModel;
        BufferedImage bufImage;

        imgDataByte = imageDataToBytes(imgData, includeAlpha);

        if (hasAlpha) {
            channelCount=4;
            channelOffsets=new int[]{0,1,2,3};
        }
        else {
            channelCount=3;
            channelOffsets=new int[]{0,1,2};
        }

            // save to PNG

        try {
            dataBuffer=new DataBufferByte(imgDataByte,imgDataByte.length);
            writeRaster=Raster.createInterleavedRaster(dataBuffer,textureSize,textureSize,(textureSize*channelCount),channelCount,channelOffsets,(Point)null);
            colorModel=new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),hasAlpha,true,Transparency.OPAQUE,DataBuffer.TYPE_BYTE);
            bufImage=new BufferedImage(colorModel,writeRaster,true,null);
            ImageIO.write(bufImage,"png",new File(path));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public byte[] getColorDataAsBytes() {
        return (imageDataToBytes(colorData, hasAlpha));
    }

    public byte[] getNormalDataAsBytes() {
        return (imageDataToBytes(normalData, false));
    }

    public byte[] getMetallicRoughnessDataAsBytes() {
        return (imageDataToBytes(metallicRoughnessData, false));
    }

    public byte[] getEmissiveDataAsBytes() {
        if (!hasEmissive) return(null);
        return (imageDataToBytes(emissiveData, false));
    }

        //
        // generate mainline
        //

    protected void generateInternal() {
    }

    public void generate() {
        int imgSize;

            // setup all the bitmaps for drawing

        imgSize=(textureSize*4)*textureSize;

        colorData=new float[imgSize];
        normalData=new float[imgSize];
        metallicRoughnessData=new float[imgSize];
        emissiveData=new float[imgSize];

        clearImageData(colorData,1.0f,1.0f,1.0f,1.0f);
        clearImageData(normalData,0.5f,0.5f,1.0f,1.0f);
        clearImageData(metallicRoughnessData,0.0f,0.0f,0.0f,1.0f);
        clearImageData(emissiveData,0.0f,0.0f,0.0f,1.0f);

            // run the internal generator

        generateInternal();

            // clamp the floats

        clampImageData(colorData);
        clampImageData(normalData);
        clampImageData(metallicRoughnessData);
        clampImageData(emissiveData);
    }

    public void writeToFile(String path, String name) {
        if (name == null) {
            name = this.getClass().getSimpleName().substring(6).toLowerCase();
        }

        writeImageData(colorData, hasAlpha, (path + File.separator + name + "_color.png"));
        if (hasNormal) {
            writeImageData(normalData, false, (path + File.separator + name + "_normal.png"));
        }
        if (hasMetallicRoughness) {
            writeImageData(metallicRoughnessData, false, (path + File.separator + name + "_metallic_roughness.png"));
        }
        if (hasEmissive) {
            writeImageData(emissiveData, false, (path + File.separator + name + "_emissive.png"));
        }
    }

}
