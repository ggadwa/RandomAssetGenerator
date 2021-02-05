package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.utility.*;

import java.util.*;

public class BitmapMetal extends BitmapBase
{
    public final static int VARIATION_PLATE=0;
    public final static int VARIATION_BOX=1;
    public final static int VARIATION_PIPE=2;
    public final static int VARIATION_HEXAGON=3;

    public final static float[][][][] CORRUGATION_LINES=
                                        {
                                            {{{0.0f,1.0f},{1.0f,0.0f}},{{0.0f,0.0f},{1.0f,1.0f}},{{0.0f,0.0f},{1.0f,1.0f}},{{0.0f,1.0f},{1.0f,0.0f}}},  // diamonds
                                            {{{0.0f,1.0f},{1.0f,0.0f}},{{0.0f,0.0f},{1.0f,1.0f}},{{0.0f,1.0f},{1.0f,0.0f}},{{0.0f,0.0f},{1.0f,1.0f}}},  // waves
                                            {{{0.5f,0.0f},{0.5f,1.0f}},{{0.0f,0.5f},{1.0f,0.5f}},{{0.0f,0.5f},{1.0f,0.5f}},{{0.5f,0.0f},{0.5f,1.0f}}}   // pluses
                                        };
    
    public BitmapMetal(int colorScheme,Random random)
    {
        super(colorScheme,random);
        
        hasNormal=true;
        hasMetallicRoughness=true;
        hasGlow=false;
        hasAlpha=false;
    }
    
        //
        // metal pieces
        //
  
    private void generateMetalCorrugation(int lft,int top,int rgt,int bot,RagColor metalColor)
    {
        int         x,y,dx,dy,sx,sy,ex,ey,wid,high,idx,
                    corrCount,corrWid,corrHigh,
                    lineStyle;
        float       lineWid,lineHigh;
        float[][]   line;
        RagColor    metalCorrColor;
        
        wid=rgt-lft;
        high=bot-top;
        
        if ((wid<=0) || (high<=0)) return;
        
        metalCorrColor=adjustColorRandom(metalColor,0.6f,0.7f);

        corrCount=(int)(((float)wid*0.06f)+(random.nextFloat()*((float)wid*0.03f)));
        corrWid=wid/corrCount;
        corrHigh=high/corrCount;

        lineWid=(float)(corrWid-4);
        lineHigh=(float)(corrHigh-4);

        lineStyle=random.nextInt(CORRUGATION_LINES.length);

            // corrugations

        dy=top+((high-(corrHigh*corrCount))/2);

        for (y=0;y!=corrCount;y++) {

            dx=lft+((wid-(corrWid*corrCount))/2);

            for (x=0;x!=corrCount;x++) {

                idx=((y&0x1)*2)+(x&0x1);
                line=CORRUGATION_LINES[lineStyle][idx];

                sx=dx+(int)(line[0][0]*lineWid);
                sy=dy+(int)(line[0][1]*lineHigh);
                ex=dx+(int)(line[1][0]*lineWid);
                ey=dy+(int)(line[1][1]*lineHigh);

                drawLineColor(sx,sy,ex,ey,metalCorrColor);
                drawLineNormal(sx,sy,ex,ey,NORMAL_CLEAR);
                
                if (Math.abs(ex-sx)>Math.abs(ey-sy)) {
                    drawLineNormal(sx,(sy+1),ex,(ey+1),NORMAL_BOTTOM_45);
                    drawLineNormal(sx,(sy-1),ex,(ey-1),NORMAL_TOP_45);
                }
                else {
                    drawLineNormal((sx+1),sy,(ex+1),ey,NORMAL_RIGHT_45);
                    drawLineNormal((sx-1),sy,(ex-1),ey,NORMAL_LEFT_45);
                }
                
                dx+=corrWid;
            }

            dy+=corrHigh;
        }
    }
    
    private void generateMetalSingleScrew(int x,int y,RagColor screwColor,RagColor outlineColor,int screwSize,int edgeSize)
    {
        drawOval(x,y,(x+screwSize),(y+screwSize),0.0f,1.0f,0.0f,0.0f,edgeSize,0.8f,screwColor,outlineColor,0.5f,false,false,1.0f,0.0f);
    }

    private void generateMetalScrews(int lft,int top,int rgt,int bot,RagColor screwColor,int screwSize)
    {
        int         mx,my,edgeSize;
        RagColor    outlineColor;
        
        edgeSize=screwSize/2;
        outlineColor=adjustColor(screwColor,0.8f);
        
            // corners
            
        if (random.nextFloat()<0.33f) {
            generateMetalSingleScrew(lft,top,screwColor,outlineColor,screwSize,edgeSize);
            generateMetalSingleScrew((rgt-screwSize),top,screwColor,outlineColor,screwSize,edgeSize);
            generateMetalSingleScrew((rgt-screwSize),(bot-screwSize),screwColor,outlineColor,screwSize,edgeSize);
            generateMetalSingleScrew(lft,(bot-screwSize),screwColor,outlineColor,screwSize,edgeSize);
        }
        
            // middles
            
        if (random.nextFloat()<0.33) {
            mx=((lft+rgt)/2)-(screwSize/2);
            my=((top+bot)/2)-(screwSize/2);
            generateMetalSingleScrew(mx,top,screwColor,outlineColor,screwSize,edgeSize);
            generateMetalSingleScrew((rgt-screwSize),my,screwColor,outlineColor,screwSize,edgeSize);
            generateMetalSingleScrew(mx,(bot-screwSize),screwColor,outlineColor,screwSize,edgeSize);
            generateMetalSingleScrew(lft,my,screwColor,outlineColor,screwSize,edgeSize);
        }
    }
    
    private void generateMetalWave(int lft,int top,int rgt,int bot,RagColor color)
    {
        int             sz,waveCount;
        RagColor        frameColor;
        
        drawMetalShine(lft,top,rgt,bot,color);
        
        frameColor=adjustColorRandom(color,0.75f,0.85f);
        sz=(int)((float)Math.max((rgt-lft),(bot-top))*0.045f);
        waveCount=sz+random.nextInt(sz);
        
        if (random.nextBoolean()) {
            drawNormalWaveHorizontal(lft,top,rgt,bot,color,frameColor,waveCount);
        }
        else {
            drawNormalWaveVertical(lft,top,rgt,bot,color,frameColor,waveCount);
        }
    }

    private void generateMetalPanel(int lft,int top,int rgt,int bot,RagColor metalColor,RagColor altMetalColor,int edgeSize,int screwSize,int variationMode)
    {
        int         lft2,rgt2,top2,bot2,
                    sz,panelType;
        RagColor    color,screwColor,frameColor;
        
            // colors
            
        if (random.nextBoolean()) {
            color=metalColor;
            screwColor=altMetalColor;
        }
        else {
            color=altMetalColor;
            screwColor=metalColor;
        }
        
        frameColor=adjustColorRandom(color,0.85f,0.95f);
        
            // the plate
            
        createPerlinNoiseData(16,16);
        drawRect(lft,top,rgt,bot,color);
        drawPerlinNoiseRect(lft,top,rgt,bot,0.8f,1.0f);
        
        drawMetalShine(lft,top,rgt,bot,color);
        draw3DFrameRect(lft,top,rgt,bot,edgeSize,frameColor,true);
        
            // variations
            
        panelType=(variationMode==VARIATION_BOX)?0:random.nextInt(4);
            
        switch (panelType) {
            
                // internal box
                
            case 0:
                sz=((edgeSize+screwSize)*2)+(random.nextInt(edgeSize*3));
                lft2=lft+sz;
                rgt2=rgt-sz;
                top2=top+sz;
                bot2=bot-sz;
                frameColor=adjustColorRandom(color,0.75f,0.85f);
                draw3DFrameRect(lft2,top2,rgt2,bot2,edgeSize,frameColor,false);
                drawMetalShine((lft2+edgeSize),(top2+edgeSize),(rgt2-edgeSize),(bot2-edgeSize),color);
                
                sz=edgeSize+screwSize;
                generateMetalScrews((lft+sz),(top+sz),(rgt-sz),(bot-sz),screwColor,screwSize);
                break;
                
                // corrugation
                
            case 1:
                sz=(int)((float)edgeSize*2.5f);
                generateMetalCorrugation((lft+sz),(top+sz),(rgt-sz),(bot-sz),color);
                break;
                
                // wave
                
            case 2:
                generateMetalWave((lft+edgeSize),(top+edgeSize),(rgt-edgeSize),(bot-edgeSize),color);
                break;
                
                // empty
                
            case 3:
                sz=edgeSize+screwSize;
                generateMetalScrews((lft+sz),(top+sz),(rgt-sz),(bot-sz),screwColor,screwSize);
                break;
        }
    }
    
    private void generateMetalRegular(RagColor metalColor,RagColor altMetalColor,int edgeSize,int screwSize,int variationMode)
    {
        int     mx,my,panelCount;
        
            // either single, dual, or 4 panel
            
        mx=textureSize/2;
        my=textureSize/2;
        
        panelCount=random.nextInt(3);
            
        switch (panelCount) {
            case 0:
                generateMetalPanel(0,0,textureSize,textureSize,metalColor,altMetalColor,edgeSize,screwSize,variationMode);
                break;
            case 1:
                generateMetalPanel(0,0,mx,textureSize,metalColor,altMetalColor,edgeSize,screwSize,variationMode);
                generateMetalPanel(mx,0,textureSize,textureSize,metalColor,altMetalColor,edgeSize,screwSize,variationMode);
                break;
            case 2:
                generateMetalPanel(0,0,mx,my,metalColor,altMetalColor,edgeSize,screwSize,variationMode);
                generateMetalPanel(mx,0,textureSize,my,metalColor,altMetalColor,edgeSize,screwSize,variationMode);
                generateMetalPanel(0,my,mx,textureSize,metalColor,altMetalColor,edgeSize,screwSize,variationMode);
                generateMetalPanel(mx,my,textureSize,textureSize,metalColor,altMetalColor,edgeSize,screwSize,variationMode);
                break;
        }
    }
    
    private void generateMetalBox(RagColor metalColor,RagColor altMetalColor,int edgeSize,int screwSize,int variationMode)
    {
        generateMetalPanel(0,0,textureSize,textureSize,metalColor,altMetalColor,edgeSize,screwSize,variationMode);
    }
    
    private void generateMetalPipe(RagColor metalColor,RagColor altMetalColor,int edgeSize,int screwSize)
    {
        int                 n,x,y,yAdd,yOff,screwCount;
        RagColor            lineColor,outlineColor;
        
        createPerlinNoiseData(16,16);
        drawRect(0,0,textureSize,textureSize,metalColor);
        drawPerlinNoiseRect(0,0,textureSize,textureSize,0.8f,1.0f);
        drawMetalShine(0,0,textureSize,textureSize,metalColor);
        
            // run seam
            
        if (random.nextBoolean()) {
            lineColor=adjustColor(metalColor,0.8f);
            
            drawLineColor(1,0,1,textureSize,lineColor);
            drawLineNormal(1,0,1,textureSize,NORMAL_CLEAR);
            drawLineNormal(2,0,2,textureSize,NORMAL_RIGHT_45);
            drawLineNormal(0,0,0,textureSize,NORMAL_LEFT_45);
        }
        
            // middle seam
            
        yOff=0;
        
        if (random.nextBoolean()) {
            lineColor=adjustColor(metalColor,0.8f);
            
            drawLineColor(0,2,textureSize,2,lineColor);
            drawLineColor(0,3,textureSize,3,lineColor);
            drawLineNormal(0,2,textureSize,2,NORMAL_CLEAR);
            drawLineNormal(0,3,textureSize,3,NORMAL_CLEAR);
            drawLineNormal(0,4,textureSize,4,NORMAL_BOTTOM_45);
            drawLineNormal(0,5,textureSize,5,NORMAL_BOTTOM_10);
            drawLineNormal(0,1,textureSize,1,NORMAL_TOP_45);
            drawLineNormal(0,0,textureSize,0,NORMAL_TOP_10);
            
            y=(int)((float)textureSize*0.1f);
            
            drawLineColor(0,(y+2),textureSize,(y+2),lineColor);
            drawLineColor(0,(y+3),textureSize,(y+3),lineColor);
            drawLineNormal(0,(y+2),textureSize,(y+2),NORMAL_CLEAR);
            drawLineNormal(0,(y+3),textureSize,(y+3),NORMAL_CLEAR);
            drawLineNormal(0,(y+4),textureSize,(y+4),NORMAL_BOTTOM_45);
            drawLineNormal(0,(y+5),textureSize,(y+5),NORMAL_BOTTOM_10);
            drawLineNormal(0,(y+1),textureSize,(y+1),NORMAL_TOP_45);
            drawLineNormal(0,(y+0),textureSize,(y+0),NORMAL_TOP_10);
            
            yOff=y;
        }        
        
            // screws
            
        if (random.nextBoolean()) {
            screwCount=5+random.nextInt(5);
            
            yAdd=(textureSize-yOff)/screwCount;
            x=screwSize;
            y=screwSize+yOff+((yAdd/2)-screwSize);
            
            outlineColor=adjustColor(altMetalColor,0.8f);
            
            for (n=0;n!=screwCount;n++) {
                generateMetalSingleScrew(x,y,altMetalColor,outlineColor,screwSize,(screwSize/2));
                y+=yAdd;
            }
        }
    }
    
    private void generateMetalHexagon(RagColor metalColor,RagColor altMetalColor,int edgeSize,int screwSize)
    {
        int         x,y,lft,top,pointSize,
                    xCount,yCount,xSize,ySize;
        RagColor    color;
        
            // sizing
        
        xCount=2+(2*random.nextInt(2));
        yCount=2+(2*random.nextInt(5));
        
        xSize=textureSize/xCount;
        ySize=textureSize/yCount;
        
        pointSize=(int)((float)xSize*0.1f);
        
        lft=0;
        
        for (x=0;x<=xCount;x++) {
            top=((x&0x1)==0)?0:-(ySize/2);
        
            for (y=0;y<=yCount;y++) {
                
                    // sometimes an alt color, but never on wrapping rows
                    
                color=metalColor;
                if ((x!=0) && (x!=xCount) && (y!=0) && (y!=yCount)) {
                    color=adjustColorRandom(metalColor,0.9f,1.1f);
                }
                
                drawHexagon(lft,top,((lft+xSize)-pointSize),(top+ySize),pointSize,edgeSize,color);
                top+=ySize;
            }
            
            lft+=xSize;
        }
    }
 
        //
        // metal bitmaps
        //

    @Override
    public void generateInternal(int variationMode)
    {
        int         edgeSize,screwSize;
        RagColor    metalColor,altMetalColor;
        
        metalColor=getRandomColor();
        altMetalColor=getRandomColor();
        edgeSize=(int)(((float)textureSize*0.005)+(random.nextFloat()*((float)textureSize*0.005)));
        screwSize=(int)(((float)textureSize*0.03)+(random.nextFloat()*((float)textureSize*0.05)));
        
        switch (variationMode) {
            case VARIATION_PLATE:
                generateMetalRegular(metalColor,altMetalColor,edgeSize,screwSize,variationMode);
                break;
            case VARIATION_BOX:
                generateMetalBox(metalColor,altMetalColor,edgeSize,screwSize,variationMode);
                break;
            case VARIATION_PIPE:
                generateMetalPipe(metalColor,altMetalColor,edgeSize,screwSize);
                break;
            case VARIATION_HEXAGON:
                generateMetalHexagon(metalColor,altMetalColor,edgeSize,screwSize);
                break;
        }

            // finish with the metallic-roughness

        createMetallicRoughnessMap(0.5f,0.6f);
    }
}
