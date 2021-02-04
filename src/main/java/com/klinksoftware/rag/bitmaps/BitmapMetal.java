package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.utility.*;

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
    
    public BitmapMetal(int colorScheme)
    {
        super(colorScheme);
        
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

        corrCount=(int)((float)wid*0.06f)+(int)(Math.random()*((double)wid*0.03));
        corrWid=wid/corrCount;
        corrHigh=high/corrCount;

        lineWid=(float)(corrWid-4);
        lineHigh=(float)(corrHigh-4);

        lineStyle=(int)(Math.random()*(double)CORRUGATION_LINES.length);

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

    private void generateMetalScrews(int lft,int top,int rgt,int bot,RagColor screwColor,int screwSize)
    {
        int         mx,my,edgeSize;
        RagColor    outlineColor;
        
        edgeSize=screwSize/2;
        outlineColor=adjustColor(screwColor,0.8f);
        
            // corners
            
        if (Math.random()<0.33) {
            drawOval(lft,top,(lft+screwSize),(top+screwSize),0.0f,1.0f,0.0f,0.0f,edgeSize,0.8f,screwColor,outlineColor,0.5f,false,false,1.0f,0.0f);
            drawOval((rgt-screwSize),top,rgt,(top+screwSize),0.0f,1.0f,0.0f,0.0f,edgeSize,0.8f,screwColor,outlineColor,0.5f,false,false,1.0f,0.0f);
            drawOval((rgt-screwSize),(bot-screwSize),rgt,bot,0.0f,1.0f,0.0f,0.0f,edgeSize,0.8f,screwColor,outlineColor,0.5f,false,false,1.0f,0.0f);
            drawOval(lft,(bot-screwSize),(lft+screwSize),bot,0.0f,1.0f,0.0f,0.0f,edgeSize,0.8f,screwColor,outlineColor,0.5f,false,false,1.0f,0.0f);
        }
        
            // middles
            
        if (Math.random()<0.33) {
            mx=((lft+rgt)/2)-(screwSize/2);
            my=((top+bot)/2)-(screwSize/2);
            drawOval(mx,top,(mx+screwSize),(top+screwSize),0.0f,1.0f,0.0f,0.0f,edgeSize,0.8f,screwColor,outlineColor,0.5f,false,false,1.0f,0.0f);
            drawOval((rgt-screwSize),my,rgt,(my+screwSize),0.0f,1.0f,0.0f,0.0f,edgeSize,0.8f,screwColor,outlineColor,0.5f,false,false,1.0f,0.0f);
            drawOval(mx,(bot-screwSize),(mx+screwSize),bot,0.0f,1.0f,0.0f,0.0f,edgeSize,0.8f,screwColor,outlineColor,0.5f,false,false,1.0f,0.0f);
            drawOval(lft,my,(lft+screwSize),(my+screwSize),0.0f,1.0f,0.0f,0.0f,edgeSize,0.8f,screwColor,outlineColor,0.5f,false,false,1.0f,0.0f);
        }
    }
    
    private void generateMetalWave(int lft,int top,int rgt,int bot,RagColor color)
    {
        int             sz,waveCount;
        RagColor        frameColor;
        
        drawMetalShine(lft,top,rgt,bot,color);
        
        frameColor=adjustColorRandom(color,0.75f,0.85f);
        sz=(int)((float)Math.max((rgt-lft),(bot-top))*0.045f);
        waveCount=sz+(int)(Math.random()*(double)sz);
        
        if (Math.random()<0.5) {
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
            
        if (Math.random()<0.5) {
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
            
        panelType=(variationMode==VARIATION_BOX)?0:(int)(Math.random()*4.0);
            
        switch (panelType) {
            
                // internal box
                
            case 0:
                sz=((edgeSize+screwSize)*2)+(int)(Math.random()*(double)(edgeSize*3));
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
        
        panelCount=(int)(Math.random()*3.0);
            
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
        createPerlinNoiseData(16,16);
        drawRect(0,0,textureSize,textureSize,metalColor);
        drawPerlinNoiseRect(0,0,textureSize,textureSize,0.8f,1.0f);
        drawMetalShine(0,0,textureSize,textureSize,metalColor);
        
        // drawOval(lft,top,(lft+screwSize),(top+screwSize),0,1,0,0,edgeSize,0.8,screwColor,outlineColor,0.5,false,false,1,0);
    }
    
    private void generateMetalHexagon(RagColor metalColor,RagColor altMetalColor,int edgeSize,int screwSize)
    {
        int         x,y,lft,top,pointSize,
                    xCount,yCount,xSize,ySize;
        RagColor    color;
        
            // sizing
        
        xCount=2+(2*(int)(Math.random()*2.0));
        yCount=2+(2*(int)(Math.random()*5.0));
        
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
        edgeSize=(int)((float)textureSize*0.005)+(int)(Math.random()*((double)textureSize*0.005));
        screwSize=(int)((float)textureSize*0.01)+(int)(Math.random()*((double)textureSize*0.02));
        
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
