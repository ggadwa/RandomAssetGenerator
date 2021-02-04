package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.utility.*;

import java.io.*;
import java.awt.*;
import java.awt.color.*;
import java.awt.image.*;
import javax.imageio.*;

public class BitmapBase
{
    public static final int COLOR_SCHEME_RANDOM=0;
    public static final int COLOR_SCHEME_DOOM=1;
    public static final int COLOR_SCHEME_GRAY=2;
    public static final int COLOR_SCHEME_PASTEL=3;
    
    public static final int COLOR_SCHEME_COUNT=4;

    public static final RagVector NORMAL_CLEAR=new RagVector(0.0f,0.0f,1.0f);

    public static final RagVector NORMAL_LEFT_45=new RagVector(-0.65f,0.02f,0.75f);
    public static final RagVector NORMAL_RIGHT_45=new RagVector(0.65f,-0.02f,0.75f);
    public static final RagVector NORMAL_TOP_45=new RagVector(-0.02f,0.65f,0.75f);
    public static final RagVector NORMAL_BOTTOM_45=new RagVector(0.02f,-0.65f,0.75f);

    public static final RagVector NORMAL_LEFT_10=new RagVector(-0.1f,0.0f,0.90f);
    public static final RagVector NORMAL_RIGHT_10=new RagVector(0.1f,0.0f,0.90f);
    public static final RagVector NORMAL_TOP_10=new RagVector(0.0f,0.1f,0.90f);
    public static final RagVector NORMAL_BOTTOM_10=new RagVector(0.0f,-0.1f,0.90f);
        
    public static final RagVector NORMAL_TOP_LEFT_45=new RagVector(-0.48f,0.48f,0.72f);
    public static final RagVector NORMAL_TOP_RIGHT_45=new RagVector(0.48f,0.48f,0.72f);
    public static final RagVector NORMAL_BOTTOM_LEFT_45=new RagVector(-0.48f,-0.48f,0.72f);
    public static final RagVector NORMAL_BOTTOM_RIGHT_45=new RagVector(0.48f,-0.48f,0.72f);
    
    public static final RagColor COLOR_BLACK=new RagColor(0.0f,0.0f,0.0f);
    public static final RagColor COLOR_WHITE=new RagColor(1.0f,1.0f,1.0f);
        
    public static final float[][] COLOR_PRIMARY_LIST={
                                        {0.7f,0.0f,0.0f},   // red
                                        {0.0f,0.7f,0.0f},   // green
                                        {0.0f,0.0f,0.7f},   // blue
                                        {0.7f,0.7f,0.0f},   // yellow
                                        {0.8f,0.0f,0.8f},   // purple
                                        {0.8f,0.8f,0.0f},   // light blue
                                        {0.0f,0.9f,0.6f},   // sea green
                                        {1.0f,0.4f,0.0f},   // orange
                                        {0.7f,0.4f,0.0f},   // brown
                                        {0.8f,0.6f,0.0f},   // gold
                                        {0.8f,0.6f,0.8f},   // lavender
                                        {1.0f,0.8f,0.8f},   // pink
                                        {0.6f,0.9f,0.0f},   // lime
                                        {0.2f,0.5f,0.0f},   // tree green
                                        {0.5f,0.5f,0.5f},   // gray
                                        {0.6f,0.0f,0.9f},   // dark purple
                                        {0.0f,0.3f,0.5f},   // slate blue
                                        {0.9f,0.6f,0.4f},   // peach
                                        {0.9f,0.0f,0.4f},   // muave
                                        {0.8f,0.5f,0.5f}    // dull red
                                    };
    
    protected int           colorScheme,textureSize;
    protected boolean       hasNormal,hasMetallicRoughness,hasGlow,hasAlpha;
    protected RagVector     specularFactor,emissiveFactor;
    protected float[]       colorData,normalData,metallicRoughnessData,glowData,
                            perlinNoiseColorFactor,noiseNormals;
    
    public BitmapBase(int colorScheme)
    {
        this.colorScheme=colorScheme;
        
            // will be reset in children classes
           
        textureSize=512;
        hasNormal=true;
        hasMetallicRoughness=true;
        hasGlow=false;
        hasAlpha=false;
        
        specularFactor=new RagVector(5,5,5);
        emissiveFactor=new RagVector(1,1,1);

            // the color, normal, metallic-roughness, and glow
            // define them later as child classes
            // can change texture size
 
        colorData=null;
        normalData=null;
        metallicRoughnessData=null;
        glowData=null;
        
            // noise
            
        perlinNoiseColorFactor=null;
        noiseNormals=null;
    }
    
        //
        // colors
        //
        
    protected RagColor getRandomColor()
    {
        int             idx;
        float           f,midPoint,darken;
        float[]         col;
        
        switch (colorScheme) {
            
                // random primary colors
                
            case COLOR_SCHEME_RANDOM:
                idx=(int)((float)COLOR_PRIMARY_LIST.length*Math.random());
                col=COLOR_PRIMARY_LIST[idx];
                darken=0.1f-(float)(Math.random()*0.2);
                return(new RagColor((col[0]-darken),(col[1]-darken),(col[2]-darken)));
                
                // doom browns and green
                
            case COLOR_SCHEME_DOOM:
                if (Math.random()<0.5) return(adjustColorRandom(new RagColor(0.6f,0.3f,0.0f),0.7f,1.0f));

                f=(float)(Math.random()*0.1);
                return(new RagColor(f,(0.4f+(float)(Math.random()*0.2)),f));
            
                // black and white
                
            case COLOR_SCHEME_GRAY:
                return(getRandomGray(0.3f,0.7f));
                
                // pastel primary colors
                
            case COLOR_SCHEME_PASTEL:
                idx=(int)((float)COLOR_PRIMARY_LIST.length*Math.random());
                col=COLOR_PRIMARY_LIST[idx];
                midPoint=(col[0]+col[1]+col[2])*0.33f;
                return(new RagColor((col[0]+(midPoint-col[0])*0.8f),(col[1]+(midPoint-col[1])*0.8f),(col[2]+(midPoint-col[2])*0.8f)));
                
        }
        
        return(null);
    }
    
    protected RagColor getRandomColorDull(float dullFactor)
    {
        float               midPoint;
        RagColor            color;
        
        color=getRandomColor();
        
            // find the midpoint
            
        midPoint=(color.r+color.g+color.b)*0.33f;
        
            // move towards it
            
        color.r=color.r+(midPoint-color.r)*dullFactor;
        color.g=color.g+(midPoint-color.g)*dullFactor;
        color.b=color.b+(midPoint-color.b)*dullFactor;

        return(color);
    }

    protected RagColor getRandomGray(float minFactor,float maxFactor)
    {
        float       col;
        
        col=minFactor+((float)Math.random()*(maxFactor-minFactor));
        return(new RagColor(col,col,col));
    }

    protected RagColor adjustColor(RagColor color,float factor)
    {
        return(new RagColor((color.r*factor),(color.g*factor),(color.b*factor)));
    }
    
    protected RagColor adjustColorRandom(RagColor color,float minFactor,float maxFactor)
    {
        float       f;
        
        f=minFactor+((float)Math.random()*(maxFactor-minFactor));
        return(new RagColor((color.r*f),(color.g*f),(color.b*f)));
    }
    
        //
        // block copy
        //
    
    protected void blockCopy(float[] fromData,int lft,int top,int rgt,int bot,float[] toData)
    {
        int     x,y,idx,rowCount;
        
        rowCount=(rgt-lft)*4;

        for (y=top;y!=bot;y++) {
            idx=((y*textureSize)+lft)*4;

            for (x=0;x!=rowCount;x++) {
                toData[idx]=fromData[idx++];
            }
        }         
    }

        //
        // blur
        //

    protected void blur(float[] data,int lft,int top,int rgt,int bot,int blurCount,boolean clamp)
    {
        int             n,x,y,idx,
                        cx,cy,cxs,cxe,cys,cye,dx,dy;
        float           r,g,b;
        float[]         blurData;
        
        if ((lft>=rgt) || (top>=bot)) return;
        
        blurData=new float[data.length];
        
            // blur pixels to count

        for (n=0;n!=blurCount;n++) {
            
            for (y=top;y!=bot;y++) {

                cys=y-1;
                cye=y+2;

                for (x=lft;x!=rgt;x++) {

                        // get blur from 8 surrounding pixels

                    r=g=b=0;

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
                            if ((cy==y) && (cx==x)) continue;       // ignore self
                            
                            dx=cx;
                            if (!clamp) {
                                if (dx<lft) dx=rgt+(lft-dx);
                                if (dx>=rgt) dx=lft+(dx-rgt);
                            }
                            else {
                                if (dx<lft) dx=lft;
                                if (dx>=rgt) dx=rgt-1;
                            }
                            
                                // add up blur from the
                                // original pixels

                            idx=((dy*textureSize)+dx)*4;

                            r+=data[idx];
                            g+=data[idx+1];
                            b+=data[idx+2];
                        }
                    }
                    
                    idx=((y*textureSize)+x)*4;

                    blurData[idx]=r*0.125f;     // divide by 8.0f
                    blurData[idx+1]=g*0.125f;
                    blurData[idx+2]=b*0.125f;
                }
            }

                // transfer over the changed pixels

            for (y=top;y!=bot;y++) {
                idx=((y*textureSize)+lft)*4;
                for (x=lft;x!=rgt;x++) {       
                    data[idx]=blurData[idx];
                    data[idx+1]=blurData[idx+1];
                    data[idx+2]=blurData[idx+2];
                    idx+=4;
                }
            }
        } 
    }

        //
        // noise
        //
    
    private float getDotGridVector(RagVector[][] vectors,int gridX,int gridY,int gridWid,int gridHigh,int x,int y)
    {
        float       dx,dy;
        
        dx=(float)(x-(gridX*gridWid))/gridWid;
        dy=(float)(y-(gridY*gridHigh))/gridHigh;
        
        return((dx*vectors[gridY][gridX].x)+(dy*vectors[gridY][gridX].y));
    }
    
    private float lerp(float a,float b,float w)
    {
        double      d;
        
        d=(Math.pow(w,2)*3)-(Math.pow(w,3)*2);
        return((float)(((1.0-d)*a)+(d*b)));
    }
    
    protected void createPerlinNoiseData(int gridXSize,int gridYSize)
    {
        int             x,y,gridWid,gridHigh,
                        gridX0,gridX1,gridY0,gridY1;
        float           sx,sy,ix0,ix1,n0,n1;
        RagVector       normal;
        RagVector[][]   vectors;
        
            // the grid
            // it must be evenly divisible
            
        gridWid=textureSize/gridXSize;
        gridHigh=textureSize/gridYSize;
        
            // noise data arrays
            // this is a single float
            
        this.perlinNoiseColorFactor=new float[textureSize*textureSize];
        
            // generate the random grid vectors
            // these need to wrap around so textures can tile
            
        vectors=new RagVector[gridXSize+1][gridYSize+1];
        
        for (y=0;y!=gridYSize;y++) {
            
            for (x=0;x!=gridXSize;x++) {
                normal=new RagVector((float)((Math.random()*2.0)-1.0),(float)((Math.random()*2.0)-1.0),0);
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
        normal=new RagVector(0.0f,0.0f,0.0f);
        
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
                
                n0=this.getDotGridVector(vectors,gridX0,gridY0,gridWid,gridHigh,x,y);
                n1=this.getDotGridVector(vectors,gridX1,gridY0,gridWid,gridHigh,x,y);
                ix0=lerp(n0,n1,sx);
                
                n0=this.getDotGridVector(vectors,gridX0,gridY1,gridWid,gridHigh,x,y);
                n1=this.getDotGridVector(vectors,gridX1,gridY1,gridWid,gridHigh,x,y);
                ix1=lerp(n0,n1,sx);
                
                    // turn this into a color factor for the base color
                
                perlinNoiseColorFactor[(y*textureSize)+x]=(lerp(ix0,ix1,sy)+1.0f)*0.5f;      // get it in 0..1
            }
        }
    }

    protected float getPerlineColorFactorForPosition(int x,int y)
    {
        return(this.perlinNoiseColorFactor[(y*textureSize)+x]);
    }
    
    protected void drawPerlinNoiseRect(int lft,int top,int rgt,int bot,float colorFactorMin,float colorFactorMax)
    {
        int         x,y,idx;
        float       colFactor,colorFactorAdd;
        
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
    
    protected void drawStaticNoiseRect(int lft,int top,int rgt,int bot,float colorFactorMin,float colorFactorMax)
    {
        int         x,y,idx;
        float       colFactor,colorFactorAdd;
        
        colorFactorAdd=colorFactorMax-colorFactorMin;
        
        for (y=top;y!=bot;y++) {
            for (x=lft;x!=rgt;x++) {
                
                    // the static random color factor
                    
                colFactor=colorFactorMin+(colorFactorAdd*(float)Math.random());

                idx=((y*textureSize)+x)*4;
                
                colorData[idx]=colorData[idx]*colFactor;
                colorData[idx+1]=colorData[idx+1]*colFactor;
                colorData[idx+2]=colorData[idx+2]*colFactor;
             }
        }
    }

    private void createNormalNoiseDataSinglePolygonLine(int x,int y,int x2,int y2,RagVector normal)
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
        RagVector   normal;
        
        if ((rgt<=lft) || (bot<=top)) return;
        
            // random settings
            
        lineSize=2+(int)(Math.random()*3.0);
        startArc=(int)(Math.random()*36.0);
        endArc=startArc+(int)(Math.random()*36.0);
        
        mx=(lft+rgt)/2;
        my=(top+bot)/2;
        halfWid=(rgt-lft)/2;
        halfHigh=(bot-top)/2;
        
            // create randomized points
            // for oval
            
        rx=new int[36];
        ry=new int[36];
        
        for (n=0;n!=36;n++) {
            rx[n]=(int)(Math.random()*20.0)-10;
            ry[n]=(int)(Math.random()*20.0)-10;
        }

            // build the polygon/oval
            
        lx=ly=0;
        normal=new RagVector(0.0f,0.0f,0.0f);
        
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
                    this.createNormalNoiseDataSinglePolygonLine(lx,ly,x,y,normal);
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

    public void createNormalNoiseData(float density,float normalZFactor)
    {
        int         n,x,y,wid,high,
                    pixelSize,nCount;
        
            // initialize the noise data
            
        pixelSize=(textureSize*textureSize)*4;
        noiseNormals=new float[pixelSize];
        
        for (n=0;n!=pixelSize;n+=4) {
            noiseNormals[n]=0.5f;
            noiseNormals[n+1]=0.5f;
            noiseNormals[n+2]=1.0f;
        }
        
            // create the random normal chunks
            
        nCount=(int)(((float)textureSize*0.5f)*density);
        
        for (n=0;n!=nCount;n++) {
            x=(int)(Math.random()*(textureSize-1));
            y=(int)(Math.random()*(textureSize-1));
            wid=20+(int)(Math.random()*40.f);
            high=20+(int)(Math.random()*40.f);
            
            createNormalNoiseDataSinglePolygon(x,y,(x+wid),(y+high),normalZFactor,(Math.random()<0.5f));
        }
        
            // blur to fix any missing pixels and make the
            // height change not as drastic
            
        blur(noiseNormals,0,0,textureSize,textureSize,5,false);
    }

    public void drawNormalNoiseRect(int lft,int top,int rgt,int bot)
    {
        int         x,y,idx;

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

            switch ((int)(Math.random()*4.0)) {
                case 0:
                    gx=lft+distortSize;
                    gy=top+(int)(Math.random()*(double)(bot-top));
                    break;
                case 1:
                    gx=rgt-distortSize;
                    gy=top+(int)(Math.random()*(double)(bot-top));
                    break;
                case 2:
                    gx=lft+(int)(Math.random()*(double)(rgt-lft));
                    gy=top+distortSize;
                    break;
                default:
                    gx=lft+(int)(Math.random()*(double)(rgt-lft));
                    gy=bot-distortSize;
                    break;
            }
        
                // distort bitmap
                
            for (y=top;y!=bot;y++) {
                for (x=lft;x!=rgt;x++) {

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
                    colorData[idx+2]=colorDataCopy[idx2+2];

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
    /*
    drawRectGlow(lft,top,rgt,bot,color)
    {
        let x,y,idx;
        let glowData=this.glowImgData.data;
        
        if ((lft>=rgt) || (top>=bot)) return;

        for (y=top;y<=bot;y++) {
            if ((y<0) || (y>=this.glowImgData.height)) continue;
            
            for (x=lft;x<=rgt;x++) {
                if ((x<0) || (x>=this.glowImgData.width)) continue;
                
                idx=((y*this.glowImgData.width)+x)*4;
                
                glowData[idx]=color.r*255.0;
                glowData[idx+1]=color.g*255.0;
                glowData[idx+2]=color.b*255.0;
            }
        }
    }
    */
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

    protected void drawOval(int lft,int top,int rgt,int bot,float startArc,float endArc,float xRoundFactor,float yRoundFactor,int edgeSize,float edgeColorFactor,RagColor color,RagColor outlineColor,float normalZFactor,boolean flipNormals,boolean addNoise,float colorFactorMin,float colorFactorMax)
    {
        int         n,x,y,mx,my,wid,high,idx,
                    edgeCount,drawStartArc,drawEndArc;
        float       fx,fy,halfWid,halfHigh,rad,
                    colorFactorAdd,colorFactor,nFactor;
        RagColor    col;
        RagVector   normal;
        
        if ((lft>=rgt) || (top>=bot)) return;
        
            // start and end arc
            
        drawStartArc=(int)(startArc*1000.0f);
        drawEndArc=(int)(endArc*1000.0f);
        if (drawStartArc>=drawEndArc) return;
        
            // the drawing size
            
        wid=(rgt-lft)-1;
        high=(bot-top)-1;         // avoids clipping on bottom from being on wid,high
        mx=lft+(wid/2);
        my=top+(high/2);
        
        col=new RagColor(0.0f,0.0f,0.0f);
        normal=new RagVector(0.0f,0.0f,0.0f);
        colorFactorAdd=colorFactorMax-colorFactorMin;

        edgeCount=edgeSize;
        
            // fill the oval

        while ((wid>0) && (high>0)) {

            halfWid=(float)wid*0.5f;
            halfHigh=(float)high*0.5f;
            
            for (n=drawStartArc;n<drawEndArc;n++) {
                rad=(float)(Math.PI*2.0)*((float)n*0.001f);

                fx=(float)Math.sin(rad);
                fx+=(fx*xRoundFactor);
                if (fx>1.0f) fx=1.0f;
                if (fx<-1.0f) fx=-1.0f;
                
                x=mx+(int)(halfWid*fx);
                if ((x<0) || (x>=textureSize)) continue;

                fy=(float)Math.cos(rad);
                fy+=(fy*yRoundFactor);
                if (fy>1.0f) fy=1.0f;
                if (fy<-1.0f) fy=-1.0f;
                
                y=my-(int)(halfHigh*fy);
                if ((y<0) || (y>=textureSize)) continue;
                
                    // edge darkening
                    
                col.setFromColor(color);
                
                if (edgeCount>0) {
                    colorFactor=edgeColorFactor+((1.0f-((float)edgeCount/(float)edgeSize))*(1.0f-edgeColorFactor));
                    col.factor(colorFactor);
                }
                
                if (addNoise) {
                    colorFactor=colorFactorMin+(colorFactorAdd*perlinNoiseColorFactor[(y*textureSize)+x]);
                    col.factor(colorFactor);
                }

                    // the color
                
                idx=((y*textureSize)+x)*4;
                
                colorData[idx]=col.r;
                colorData[idx+1]=col.g;
                colorData[idx+2]=col.b;

                    // get a normal for the pixel change
                    // if we are outside the edge, gradually fade it
                    // to the default pointing out normal

                normal.x=0.0f;
                normal.y=0.0f;
                normal.z=1.0f;
                
                if (edgeCount>0) {
                    nFactor=(float)edgeCount/(float)edgeSize;
                    normal.x=(fx*nFactor)+(normal.x*(1.0f-nFactor));
                    normal.y=(fy*nFactor)+(normal.y*(1.0f-nFactor));
                    normal.z=(normalZFactor*nFactor)+(normal.z*(1.0f-nFactor));
                    if (flipNormals) {
                        normal.x=-normal.x;
                        normal.y=-normal.y;
                    }
                }
              
                    // add in noise normal
                    
                if (addNoise) {
                    normal.x=(((noiseNormals[idx]*0.5f)-1.0f)*0.4f)+(normal.x*0.6f);
                    normal.y=(((noiseNormals[idx+1]*0.5f)-1.0f)*0.4f)+(normal.y*0.6f);
                    normal.z=(((noiseNormals[idx+2]*0.5f)-1.0f)*0.4f)+(normal.z*0.6f);
                }
                
                normal.normalize();

                normalData[idx]=(normal.x+1.0f)*0.5f;           // normals are -1...1 packed into a byte
                normalData[idx+1]=(normal.y+1.0f)*0.5f;
                normalData[idx+2]=(normal.z+1.0f)*0.5f;
            }

            if (edgeCount>0) edgeCount--;

            wid--;
            high--;
        }
        
            // any outline
            
        if (outlineColor!=null) {
            wid=(rgt-lft)-1;
            high=(bot-top)-1;         // avoids clipping on bottom from being on wid,high

            halfWid=(float)wid*0.5f;
            halfHigh=(float)high*0.5f;
            
            for (n=drawStartArc;n<drawEndArc;n++) {
                rad=(float)(Math.PI*2.0)*((float)n*0.001f);

                fx=(float)Math.sin(rad);
                fx+=(fx*xRoundFactor);
                if (fx>1.0f) fx=1.0f;
                if (fx<-1.0f) fx=-1.0f;
                
                x=mx+(int)(halfWid*fx);
                if ((x<0) || (x>=textureSize)) continue;

                fy=(float)Math.cos(rad);
                fy+=(fy*yRoundFactor);
                if (fy>1.0f) fy=1.0f;
                if (fy<-1.0f) fy=-1.0f;
                
                y=my-(int)(halfHigh*fy);
                if ((y<0) || (y>=textureSize)) continue;
                
                    // the color pixel

                idx=((y*textureSize)+x)*4;

                colorData[idx]=outlineColor.r;
                colorData[idx+1]=outlineColor.g;
                colorData[idx+2]=outlineColor.b;
            }
        }

    }
    /*
    drawOvalGlow(lft,top,rgt,bot,color)
    {
        let n,x,y,mx,my,halfWid,halfHigh;
        let rad,fx,fy,idx;
        let wid,high;
        let glowData=this.glowImgData.data;
        
        if ((lft>=rgt) || (top>=bot)) return;
        
            // the drawing size
            
        wid=(rgt-lft)-1;
        high=(bot-top)-1;         // avoids clipping on bottom from being on wid,high
        mx=lft+Math.trunc(wid*0.5);
        my=top+Math.trunc(high*0.5);
        
            // fill the oval

        while ((wid>0) && (high>0)) {

            halfWid=wid*0.5;
            halfHigh=high*0.5;
            
            for (n=0;n<1000;n++) {
                rad=(Math.PI*2.0)*(n*0.001);

                fx=Math.sin(rad);
                if (fx>1.0) fx=1.0;
                if (fx<-1.0) fx=-1.0;
                
                x=mx+Math.trunc(halfWid*fx);
                if ((x<0) || (x>=this.glowImgData.width)) continue;

                fy=Math.cos(rad);
                if (fy>1.0) fy=1.0;
                if (fy<-1.0) fy=-1.0;
                
                y=my-Math.trunc(halfHigh*fy);
                if ((y<0) || (y>=this.glowImgData.height)) continue;

                    // the color
                
                idx=((y*this.glowImgData.width)+x)*4;
                
                glowData[idx]=Math.trunc(color.r*255.0);
                glowData[idx+1]=Math.trunc(color.g*255.0);
                glowData[idx+2]=Math.trunc(color.b*255.0);
            }

            wid--;
            high--;
        }
    }
    */
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
    
    protected void drawTriangle(int x0,int y0,int x1,int y1,int x2,int y2,RagColor color)
    {
        int         x,y,lx,rx,ty,my,by,tyX,myX,byX,idx;
        
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
                
                normalData[idx]=(NORMAL_CLEAR.x+1.0f)*0.5f;
                normalData[idx+1]=(NORMAL_CLEAR.y+1.0f)*0.5f;
                normalData[idx+2]=(NORMAL_CLEAR.z+1.0f)*0.5f;
                
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
                
                normalData[idx]=(NORMAL_CLEAR.x+1.0f)*0.5f;
                normalData[idx+1]=(NORMAL_CLEAR.y+1.0f)*0.5f;
                normalData[idx+2]=(NORMAL_CLEAR.z+1.0f)*0.5f;
                
                idx+=4;
            }
        }
    }

    protected void drawHexagon(int lft,int top,int rgt,int bot,int pointSize,int edgeSize,RagColor color)
    {
        int         n,lx,rx,my;
        float       darkenFactor;
        RagColor    darkColor;

            // build the hexagon

        my=(top+bot)/2;
        
        lx=lft;
        rx=rgt;
        lft-=pointSize;
        rgt+=pointSize;
        
        if (lft>=rgt) return;
        
            // fill the hexagon
            
        if (color!=null) {
            this.drawRect(lx,top,rx,bot,color);
            this.drawTriangle(lx,top,lft,my,lx,bot,color);
            this.drawTriangle(rx,top,rgt,my,rx,bot,color);
        }    
        
            // draw the edges
            
        for (n=0;n!=edgeSize;n++) {

                // the colors

            darkenFactor=(((float)(n+1)/(float)edgeSize)*0.3f)+0.7f;
            darkColor=adjustColor(color,darkenFactor);
            
                // top-left to top to top-right

            this.drawLineColor((lft+n),my,(lx+n),(top+n),darkColor);
            this.drawLineNormal((lft+n),my,(lx+n),(top+n),NORMAL_TOP_LEFT_45);

            this.drawLineColor((lx+n),(top+n),(rx-n),(top+n),darkColor);
            this.drawLineNormal((lx+n),(top+n),(rx-n),(top+n),NORMAL_TOP_45);

            this.drawLineColor((rx-n),(top+n),(rgt-n),my,darkColor);
            this.drawLineNormal((rx-n),(top+n),(rgt-n),my,NORMAL_TOP_RIGHT_45);

                // bottom-right to bottom to bottom-left

            this.drawLineColor((lft+n),my,(lx+n),(bot-n),darkColor);
            this.drawLineNormal((lft+n),my,(lx+n),(bot-n),NORMAL_BOTTOM_LEFT_45);
                
            this.drawLineColor((lx+n),(bot-n),(rx-n),(bot-n),darkColor);
            this.drawLineNormal((lx+n),(bot-n),(rx-n),(bot-n),NORMAL_BOTTOM_45);

            this.drawLineColor((rx-n),(bot-n),(rgt-n),my,darkColor);
            this.drawLineNormal((rx-n),(bot-n),(rgt-n),my,NORMAL_BOTTOM_RIGHT_45);
        }
    }

        //
        // metals
        //
        
    private void drawMetalShineLine(int x,int top,int bot,int shineWid,RagColor baseColor)
    {
        int         n,lx,rx,y,idx;
        double      density,densityReduce;
        
        if (top>=bot) return;
        if (shineWid<=0) return;
        
            // since we draw the shines from both sides,
            // we need to move the X into the middle and cut width in half
            
        shineWid=shineWid/2;
            
        x+=shineWid;
        
            // start with 100 density and reduce
            // as we go across the width
            
        density=1.0;
        densityReduce=0.9/(double)shineWid;
        
            // write the shine lines
            
        for (n=0;n!=shineWid;n++) {
            
            lx=x-n;
            rx=x+n;
            
            for (y=top;y!=bot;y++) {
                
                if (Math.random()<density) {
                    if ((lx>=0) && (lx<textureSize)) {
                        idx=((y*textureSize)+lx)*4;
                        colorData[idx]=baseColor.r;
                        colorData[idx+1]=baseColor.g;
                        colorData[idx+2]=baseColor.b;
                    }
                }
                
                if (Math.random()<density) {
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
            shineWid=(int)((float)wid*0.035f)+(int)(Math.random()*0.15);
            if ((x+shineWid)>rgt) shineWid=rgt-x;
            
                // small % are no lines
                
            if (Math.random()<0.9) {
                shineColor=adjustColorRandom(metalColor,0.7f,1.3f);
                this.drawMetalShineLine(x,top,bot,shineWid,shineColor);
            }
            
            x+=(shineWid+((int)((float)wid*0.03f)+(int)(Math.random()*0.05)));
            if (x>=rgt) break;
        }
        
        this.blur(colorData,lft,top,rgt,bot,3,true);
    }

        //
        // streaks and stains
        //
        
    private void drawStreakDirtSingle(int lft,int top,int rgt,int bot,float minMix,float addMix,RagColor color,float minXReduce)
    {
        int             x,y,lx,rx,wid,high,idx;
        float           xAdd,flx,frx,factor,factor2;
        
        wid=rgt-lft;
        high=bot-top;
        
        if ((wid<=0) || (high<=0)) return;
        
            // random shrink
            
        xAdd=(float)(Math.random()*minXReduce);
        
            // draw the dirt
            
        flx=(int)lft;
        frx=(int)rgt;
            
        for (y=top;y!=bot;y++) {
            factor=(float)(bot-y)/(float)high;
            
            lx=(int)flx;
            rx=(int)frx;
            if (lx>=rx) break;
            
            for (x=lx;x!=rx;x++) {
                factor2=factor*(minMix+(float)(Math.random()*addMix));

                idx=((y*textureSize)+x)*4;
                colorData[idx]=((1.0f-factor2)*colorData[idx])+(color.r*factor2);
                colorData[idx+1]=((1.0f-factor2)*colorData[idx+1])+(color.g*factor2);
                colorData[idx+2]=((1.0f-factor2)*colorData[idx+2])+(color.b*factor2);
            }
            
            flx+=xAdd;
            frx-=xAdd;
        }
    }
    
    protected void drawStreakDirt(int lft,int top,int rgt,int bot,int additionalStreakCount,float minMix,float maxMix,RagColor color)
    {
        int         n,sx,ex,minWid;
        float       addMix;
        
        addMix=maxMix-minMix;
        
            // original streak
            
        drawStreakDirtSingle(lft,top,rgt,bot,minMix,addMix,color,0.25f);
        
            // additional streaks
            
        minWid=(int)((float)(rgt-lft)*0.1f);
        
        for (n=0;n!=additionalStreakCount;n++) {
            sx=lft+(int)(Math.random()*(double)((rgt-minWid)-lft));
            ex=(sx+minWid)+(int)(Math.random()*(double)(rgt-(sx+minWid)));
            if (sx>=ex) continue;
            
            drawStreakDirtSingle(sx,top,ex,bot,minMix,addMix,color,0.1f);
        }
    }
    
    protected void drawOvalStain(int lft,int top,int rgt,int bot,float outerPercentage,float innerPercentage,float darken)
    {
        int         n,x,y,mx,my,wid,high,idx;
        float       halfWid,halfHigh,rad,
                    percentageAdd,curPercentage;
        float[]     origColorData;
        
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

        while ((wid>0) && (high>0)) {

            halfWid=(float)wid*0.5f;
            halfHigh=(float)high*0.5f;
            
            for (n=0;n!=1000;n++) {
                if (Math.random()>curPercentage) continue;
                
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
        // color stripes, gradients, waves
        //
        
    protected void drawColorStripeHorizontal(int lft,int top,int rgt,int bot,float factor,RagColor baseColor)
    {
        int                 x,y,count,idx;
        float               f,r,g,b,nx,ny,nz;
        RagVector           normal;

        if ((rgt<=lft) || (bot<=top)) return;
        
            // the rotating normal
            
        normal=new RagVector(0.0f,0.1f,1.0f);
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
                count=2+(int)(Math.random()*4.0);
                
                f=1.0f+((1.0f-(float)(Math.random()*2.0))*factor);
                
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

    protected void drawColorStripeVertical(int lft,int top,int rgt,int bot,float factor,RagColor baseColor)
    {
        int                 x,y,count,idx;
        float               f,r,g,b,nx,ny,nz;
        RagVector           normal;

        if ((rgt<=lft) || (bot<=top)) return;
        
            // the rotating normal
            
        normal=new RagVector(0.1f,0.0f,1.0f);
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
                count=2+(int)(Math.random()*4.0);
                
                f=1.0f+((1.0f-(float)(Math.random()*2.0))*factor);
                
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

    protected void drawNormalWaveHorizontal(int lft,int top,int rgt,int bot,RagColor color,RagColor lineColor,int waveCount)
    {
        int         x,y,idx,
                    waveIdx,wavePos,waveAdd;
        float       nx,ny,nz;

        if ((rgt<=lft) || (bot<=top)) return;
        
            // the waves
            
        waveAdd=(rgt-lft)/waveCount;
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
                waveIdx=(waveIdx+1)%3;
            }
        }
        
            // extra lines
        
        waveAdd*=3;
        
        for (x=lft;x<rgt;x+=waveAdd) {
            for (y=top;y!=bot;y++) {
                idx=((y*textureSize)+x)*4;
                normalData[idx]=(NORMAL_CLEAR.x+1.0f)*0.5f;
                normalData[idx+1]=(NORMAL_CLEAR.y+1.0f)*0.5f;
                normalData[idx+2]=(NORMAL_CLEAR.z+1.0f)*0.5f;
                
                colorData[idx]=lineColor.r;
                colorData[idx+1]=lineColor.g;
                colorData[idx+2]=lineColor.b;
            }
        }
    }
    
    protected void drawNormalWaveVertical(int lft,int top,int rgt,int bot,RagColor color,RagColor lineColor,int waveCount)
    {
        int         x,y,idx,
                    waveIdx,wavePos,waveAdd;
        float       nx,ny,nz;
        
        if ((rgt<=lft) || (bot<=top)) return;
        
            // the waves
        
        waveAdd=(bot-top)/waveCount;
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
                waveIdx=(waveIdx+1)%3;
            }
        }
        
            // extra lines
        
        waveAdd*=3;
        
        for (y=top;y<bot;y+=waveAdd) {
            for (x=lft;x!=rgt;x++) {
                idx=((y*textureSize)+x)*4;
                normalData[idx]=(NORMAL_CLEAR.x+1.0f)*0.5f;
                normalData[idx+1]=(NORMAL_CLEAR.y+1.0f)*0.5f;
                normalData[idx+2]=(NORMAL_CLEAR.z+1.0f)*0.5f;
                
                colorData[idx]=lineColor.r;
                colorData[idx+1]=lineColor.g;
                colorData[idx+2]=lineColor.b;
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
                    colorData[idx+2]=color.b;
                
                    if (prevY!=-1) {
                        if (dy!=prevY) {
                            idx=((prevY*textureSize)+dx)*4;
                            colorData[idx]=(colorData[idx]*0.5f)+(color.r*0.5f);
                            colorData[idx+1]=(colorData[idx+1]*0.5f)+(color.g*0.5f);
                            colorData[idx+2]=(colorData[idx+2]*0.5f)+(color.b*0.5f);
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
                    colorData[idx+2]=color.b;
                
                    if (prevX!=-1) {
                        if (dx!=prevX) {
                            idx=((dy*textureSize)+prevX)*4;
                            colorData[idx]=(colorData[idx]*0.5f)+(color.r*0.5f);
                            colorData[idx+1]=(colorData[idx+1]*0.5f)+(color.g*0.5f);
                            colorData[idx+2]=(colorData[idx+2]*0.5f)+(color.b*0.5f);
                        }
                    }
                    
                    prevX=dx;
                }
                
                f+=slope;
            }
        }
    }
    
    protected void drawLineNormal(int x,int y,int x2,int y2,RagVector normal)
    {
        int         xLen,yLen,sp,ep,dx,dy,idx,
                    prevX,prevY;
        float       f,slope,r,g,b;
        
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
        
        segCount=2+(int)(Math.random()*5.0);
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
                    r=lineVariant-(int)(Math.random()*2.0);

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
        
        segCount=2+(int)(Math.random()*5.0);
        xAdd=(int)((x2-x)/segCount);
        
        sx=ex=x;
        sy=ey=y;
        
        for (n=0;n!=segCount;n++) {
            
            if ((n+1)==segCount) {
                ex=x2;
            }
            else {
                ey=sy+((int)(Math.random()*(double)lineVariant)*lineDir);
                ex=sx+xAdd;
            }
            
            if (ey<clipTop) ey=clipTop;
            if (ey>clipBot) ey=clipBot;
            
            if (sx==ex) return;
            
            this.drawLineColor(sx,sy,ex,ey,color);
            this.drawLineNormal(sx,sy,ex,ey,NORMAL_CLEAR);
            this.drawLineNormal(sx,(sy-1),ex,(ey-1),NORMAL_BOTTOM_45);
            this.drawLineNormal(sx,(sy+1),ex,(ey+1),NORMAL_TOP_45);
            
            if ((ey==clipTop) || (ey==clipBot)) break;
            
            if ((canSplit) && (Math.random()<0.5)) {
                if (lineDir>0) {
                    this.drawHorizontalCrack(ey,ex,x2,clipTop,clipBot,-lineDir,lineVariant,color,false);
                }
                else {
                    this.drawHorizontalCrack(ey,ex,x2,clipTop,clipBot,-lineDir,lineVariant,color,false);
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
        
        segCount=2+(int)(Math.random()*5.0);
        yAdd=(int)((y2-y)/segCount);
        
        sx=ex=x;
        sy=ey=y;
        
        for (n=0;n!=segCount;n++) {
            
            if ((n+1)==segCount) {
                ey=y2;
            }
            else {
                ex=sx+((int)(Math.random()*(double)lineVariant)*lineDir);
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
            
            if ((canSplit) && (Math.random()<0.5)) {
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
        int         n,dx,dy,dx2,dy2;
        
        dx=sx;
        dy=sy;
        
        for (n=0;n!=segCount;n++) {
            
            if ((n+1)==segCount) {
                dx2=ex;
                dy2=ey;
            }
            else {
                dx2=(int)(sx+((float)((ex-sx)*(n+1))/(float)segCount))+(int)(Math.random()*(double)lineXVarient);
                dy2=(int)(sy+((float)((ey-sy)*(n+1))/(float)segCount))+(int)(Math.random()*(double)lineYVarient);
            }
            
            this.drawLineColor(dx,dy,dx2,dy2,color);
            this.drawLineNormal(dx,dy,dx2,dy2,NORMAL_CLEAR);
            this.drawLineNormal((dx-1),dy,(dx2-1),dy2,NORMAL_RIGHT_45);
            this.drawLineNormal((dx+1),dy,(dx2+1),dy2,NORMAL_LEFT_45);
             
            dx=dx2;
            dy=dy2;
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
        // clear and write images
        //
        
    private void clearImageData(float[] imgData,float r,float g,float b,float a)
    {
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
    
    private void writeImageData(float[] imgData,String path)
    {
        int                 n,k,idx,channelCount;
        int[]               channelOffsets;
        byte[]              imgDataByte;
        DataBuffer          dataBuffer;
        WritableRaster      writeRaster;
        ColorModel          colorModel;
        BufferedImage       bufImage;
        
            // clamp all the floats
            
        clampImageData(imgData);
        
            // image data if all floats, so covert to bytes here
            
        if (hasAlpha) {
            channelCount=4;
            channelOffsets=new int[]{0,1,2,3};

            imgDataByte=new byte[imgData.length];

            for (n=0;n!=imgData.length;n++) {
                imgDataByte[n]=(byte)((int)(imgData[n]*255.0f));
            }
        }
        else {
            channelCount=3;
            channelOffsets=new int[]{0,1,2};

            idx=0;
            imgDataByte=new byte[(imgData.length/4)*3];

            for (n=0;n!=imgData.length;n+=4) {
                imgDataByte[idx++]=(byte)((int)(imgData[n]*255.0f));
                imgDataByte[idx++]=(byte)((int)(imgData[n+1]*255.0f));
                imgDataByte[idx++]=(byte)((int)(imgData[n+2]*255.0f));
            }
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
    
        //
        // generate mainline
        //
        
    protected void generateInternal(int variationMode)
    {
        int         mid;
        
        mid=textureSize/2;
        
        drawRect(0,0,mid,mid,new RagColor(1.0f,1.0f,0.0f));
        drawRect(mid,0,textureSize,mid,new RagColor(1.0f,0.0f,0.0f));
        drawRect(0,mid,mid,textureSize,new RagColor(0.0f,1.0f,0.0f));
        drawRect(mid,mid,textureSize,textureSize,new RagColor(0.0f,0.0f,1.0f));
    }

    public void generate(int variationMode,String name)
    {
        int     imgSize;
        
            // setup all the bitmaps for drawing
            
        imgSize=(textureSize*4)*textureSize;
        
        colorData=new float[imgSize];
        normalData=new float[imgSize];
        metallicRoughnessData=new float[imgSize];
        glowData=new float[imgSize];

        clearImageData(colorData,1.0f,1.0f,1.0f,1.0f);
        clearImageData(normalData,0.5f,0.5f,1.0f,1.0f);
        clearImageData(metallicRoughnessData,0.0f,0.0f,0.0f,1.0f);
        clearImageData(glowData,0.0f,0.0f,0.0f,1.0f);

            // run the internal generator

        generateInternal(variationMode);
        
            // write out the bitmaps
            
        writeImageData(colorData,("output"+File.separator+name+"_color.png"));
        if (hasNormal) writeImageData(normalData,("output"+File.separator+name+"_normal.png"));
        if (hasMetallicRoughness) writeImageData(metallicRoughnessData,("output"+File.separator+name+"_metallic_roughness.png"));
        if (hasGlow) writeImageData(glowData,("output"+File.separator+name+"_glow.png"));
    }

}
