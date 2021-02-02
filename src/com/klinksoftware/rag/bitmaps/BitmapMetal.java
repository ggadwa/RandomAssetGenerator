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
        /*

        let x,y,dx,dy,sx,sy,ex,ey,idx,line;
        let corrCount,corrWid,corrHigh;
        let lineStyle,lineWid,lineHigh;
        let metalCorrColor;
        let wid=rgt-lft;
        let high=bot-top;
        
        if ((wid<=0) || (high<=0)) return;
        
        metalCorrColor=adjustColorRandom(metalColor,0.6,0.7);

        corrCount=this.core.randomInt(Math.trunc(wid*0.06),Math.trunc(wid*0.03));
        corrWid=Math.trunc(wid/corrCount);
        corrHigh=Math.trunc(high/corrCount);

        lineWid=corrWid-4;
        lineHigh=corrHigh-4;

        lineStyle=this.core.randomIndex(CORRUGATION_LINES.length);

            // corrugations

        dy=top+Math.trunc((high-(corrHigh*corrCount))*0.5);

        for (y=0;y!==corrCount;y++) {

            dx=lft+Math.trunc((wid-(corrWid*corrCount))*0.5);

            for (x=0;x!==corrCount;x++) {

                idx=((y&0x1)*2)+(x&0x1);
                line=CORRUGATION_LINES[lineStyle][idx];

                sx=dx+(line[0][0]*lineWid);
                sy=dy+(line[0][1]*lineHigh);
                ex=dx+(line[1][0]*lineWid);
                ey=dy+(line[1][1]*lineHigh);

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
        */
    }

    private void generateMetalScrews(int lft,int top,int rgt,int bot,RagColor screwColor,int screwSize)
    {
        /*
        let mx,my;
        let edgeSize=Math.trunc(screwSize*0.5);
        let outlineColor=adjustColor(screwColor,0.8);
        
            // corners
            
        if (this.core.randomPercentage(0.33)) {
            drawOval(lft,top,(lft+screwSize),(top+screwSize),0,1,0,0,edgeSize,0.8,screwColor,outlineColor,0.5,false,false,1,0);
            drawOval((rgt-screwSize),top,rgt,(top+screwSize),0,1,0,0,edgeSize,0.8,screwColor,outlineColor,0.5,false,false,1,0);
            drawOval((rgt-screwSize),(bot-screwSize),rgt,bot,0,1,0,0,edgeSize,0.8,screwColor,outlineColor,0.5,false,false,1,0);
            drawOval(lft,(bot-screwSize),(lft+screwSize),bot,0,1,0,0,edgeSize,0.8,screwColor,outlineColor,0.5,false,false,1,0);
        }
        
            // middles
            
        if (this.core.randomPercentage(0.33)) {
            mx=Math.trunc((lft+rgt)*0.5)-Math.trunc(screwSize*0.5);
            my=Math.trunc((top+bot)*0.5)-Math.trunc(screwSize*0.5);
            drawOval(mx,top,(mx+screwSize),(top+screwSize),0,1,0,0,edgeSize,0.8,screwColor,outlineColor,0.5,false,false,1,0);
            drawOval((rgt-screwSize),my,rgt,(my+screwSize),0,1,0,0,edgeSize,0.8,screwColor,outlineColor,0.5,false,false,1,0);
            drawOval(mx,(bot-screwSize),(mx+screwSize),bot,0,1,0,0,edgeSize,0.8,screwColor,outlineColor,0.5,false,false,1,0);
            drawOval(lft,my,(lft+screwSize),(my+screwSize),0,1,0,0,edgeSize,0.8,screwColor,outlineColor,0.5,false,false,1,0);
        }
        */
    }
    
    private void generateMetalWave(int lft,int top,int rgt,int bot,RagColor color)
    {
        /*
        let frameColor;
        let sz,waveCount;
        
        drawMetalShine(lft,top,rgt,bot,color);
        
        frameColor=adjustColorRandom(color,0.75,0.85);
        sz=Math.trunc(Math.max((rgt-lft),(bot-top))*0.045);
        waveCount=this.core.randomInt(sz,sz);
        if (this.core.randomPercentage(0.5)) {
            drawNormalWaveHorizontal(lft,top,rgt,bot,color,frameColor,waveCount);
        }
        else {
            drawNormalWaveVertical(lft,top,rgt,bot,color,frameColor,waveCount);
        }
        */
    }

    private void generateMetalPanel(int lft,int top,int rgt,int bot,RagColor metalColor,RagColor altMetalColor,int edgeSize,int screwSize,int variationMode)
    {
        /*
        let lft2,rgt2,top2,bot2,sz;
        let color,frameColor,screwColor,panelType;
        
            // colors
            
        if (this.core.randomPercentage(0.5)) {
            color=metalColor;
            screwColor=altMetalColor;
        }
        else {
            color=altMetalColor;
            screwColor=metalColor;
        }
        
        frameColor=adjustColorRandom(color,0.85,0.95);
        
            // the plate
            
        createPerlinNoiseData(16,16);
        drawRect(lft,top,rgt,bot,color);
        drawPerlinNoiseRect(lft,top,rgt,bot,0.8,1.0);
        
        drawMetalShine(lft,top,rgt,bot,color);
        draw3DFrameRect(lft,top,rgt,bot,edgeSize,frameColor,true);
        
            // variations
            
        panelType=(variationMode==VARIATION_BOX)?0:this.core.randomIndex(4);
            
        switch (panelType) {
            
                // internal box
                
            case 0:
                sz=this.core.randomInt(((edgeSize+screwSize)*2),(edgeSize*3));
                lft2=lft+sz;
                rgt2=rgt-sz;
                top2=top+sz;
                bot2=bot-sz;
                frameColor=adjustColorRandom(color,0.75,0.85);
                draw3DFrameRect(lft2,top2,rgt2,bot2,edgeSize,frameColor,false);
                drawMetalShine((lft2+edgeSize),(top2+edgeSize),(rgt2-edgeSize),(bot2-edgeSize),color);
                
                sz=edgeSize+screwSize;
                generateMetalScrews((lft+sz),(top+sz),(rgt-sz),(bot-sz),screwColor,screwSize);
                break;
                
                // corrugation
                
            case 1:
                sz=Math.trunc(edgeSize*2.5);
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
        */
    }
    
    private void generateMetalRegular(RagColor metalColor,RagColor altMetalColor,int edgeSize,int screwSize,int variationMode)
    {
        /*
        let mx,my,panelCount;
        
            // either single, dual, or 4 panel
            
        mx=Math.trunc(textureSize*0.5);
        my=Math.trunc(textureSize*0.5);
        
        panelCount=this.core.randomIndex(3);
            
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
        */
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
            /*
        let x,y,lft,top,pointSize;
        let color,edgeSize;
        let xCount,yCount,xSize,ySize;
        
            // colors
            
        color=getRandomColor();
        
            // sizing
        
        edgeSize=this.core.randomInt(3,5);
        xCount=2+(2*this.core.randomInt(0,2));
        yCount=2+(2*this.core.randomInt(0,5));
        
        xSize=Math.trunc(textureSize/xCount);
        ySize=Math.trunc(textureSize/yCount);
        
        pointSize=Math.trunc(xSize*0.1);
        
        lft=0;
        
        for (x=0;x<=xCount;x++) {
            top=((x&0x1)===0)?0:-Math.trunc(ySize*0.5);
        
            for (y=0;y<=yCount;y++) {
                drawHexagon(lft,top,(Math.trunc(lft+xSize)-pointSize),Math.trunc(top+ySize),pointSize,edgeSize,color);
                top+=ySize;
            }
            
            lft+=xSize;
        }
*/
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
        edgeSize=(int)((float)textureSize*0.005)+(int)(Math.random()*0.005);
        screwSize=(int)((float)textureSize*0.008)+(int)(Math.random()*0.015);
        
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
