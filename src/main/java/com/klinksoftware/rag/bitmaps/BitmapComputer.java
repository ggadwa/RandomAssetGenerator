package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

import java.util.*;

public class BitmapComputer extends BitmapBase
{
    public final static int VARIATION_COMPUTER_BANK=0;
    public final static int VARIATION_CONTROL_PANEL=1;
    public final static int VARIATION_MONITOR=2;
    
    public final static RagColor[] LED_COLORS={
                                new RagColor(0.0f,1.0f,0.0f),
                                new RagColor(1.0f,1.0f,0.0f),
                                new RagColor(1.0f,0.0f,0.0f)
                            };
    
    public BitmapComputer()
    {
        super();
        
        textureSize=1024;
        hasNormal=true;
        hasMetallicRoughness=true;
        hasEmissive=true;
        hasAlpha=false;
    }
    
        //
        // components
        //
 
    private void generateComputerComponentWires(int lft,int top,int rgt,int bot,int edgeSize)
    {
        int         n,nLine,x,y,lineVar;
        boolean     horz;
        RagColor    recessColor,lineColor;
        
        recessColor=getRandomGray(0.15f,0.25f);
        
            // wires background
            
        lft+=edgeSize;
        rgt-=edgeSize;
        top+=edgeSize;
        bot-=edgeSize;
            
        drawRect(lft,top,rgt,bot,COLOR_BLACK);
        draw3DFrameRect(lft,top,rgt,bot,2,recessColor,false);
        
        lft+=2;
        rgt-=2;
        top+=2;
        bot-=2;
        
            // determine if horz or vertical
            
        horz=((rgt-lft)>(bot-top));
        
        if (horz) {
            nLine=(int)((float)(bot-top)*0.7f);
            if (nLine<=0) return;
            
            lineVar=(int)((float)(rgt-lft)*0.035f);
            if (lineVar<4) lineVar=4;
            
            for (n=0;n!=nLine;n++) {
                y=top+GeneratorMain.random.nextInt(bot-top);
                
                lineColor=getRandomColor();
                drawRandomLine(lft,y,rgt,y,lft,top,rgt,bot,lineVar,lineColor,true);
            }
        }
        else {
            nLine=(int)((float)(rgt-lft)*0.7f);
            if (nLine<=0) return;
            
            lineVar=(int)((float)(bot-top)*0.035f);
            if (lineVar<4) lineVar=4;
            
            for (n=0;n!=nLine;n++) {
                x=lft+GeneratorMain.random.nextInt(rgt-lft);
                
                lineColor=getRandomColor();
                drawRandomLine(x,top,x,bot,lft,top,rgt,bot,lineVar,lineColor,true);
            }
        }
    }
    
    private void generateComputerComponentShutter(int lft,int top,int rgt,int bot,int edgeSize)
    {
        int         sz,shutterCount;
        RagColor    shutterColor,shutterEdgeColor;

        lft+=edgeSize;
        rgt-=edgeSize;
        top+=edgeSize;
        bot-=edgeSize;
       
        shutterColor=getRandomColor();
        shutterEdgeColor=adjustColor(shutterColor,0.9f);
        
        sz=(int)((float)Math.max((rgt-lft),(bot-top))*0.1f);
        shutterCount=sz+GeneratorMain.random.nextInt(sz);
        
        drawMetalShine(lft,top,rgt,bot,shutterColor);
        
        if ((rgt-lft)>(bot-top)) {
            drawNormalWaveHorizontal(lft,top,rgt,bot,shutterColor,shutterEdgeColor,shutterCount);
        }
        else {
            drawNormalWaveVertical(lft,top,rgt,bot,shutterColor,shutterEdgeColor,shutterCount);
        }
    }
    
    private void generateComputerComponentLights(int lft,int top,int rgt,int bot,int edgeSize)
    {
        int         x,y,xCount,yCount,xMargin,yMargin,
                    dx,dy,sz;
        RagColor    color;
        
        lft+=edgeSize;
        rgt-=edgeSize;
        top+=edgeSize;
        bot-=edgeSize;
        
        sz=12+GeneratorMain.random.nextInt(5);
        
        xCount=(rgt-lft)/sz;
        yCount=(bot-top)/sz;
        
        if (xCount<=0) xCount=1;
        if (yCount<=0) yCount=1;
        
        xMargin=(((rgt-lft)-(xCount*sz))/2)+1;
        yMargin=(((bot-top)-(yCount*sz))/2)+1;
        
        for (y=0;y!=yCount;y++) {
            dy=(top+yMargin)+(y*sz);
            
            for (x=0;x!=xCount;x++) {
                dx=(lft+xMargin)+(x*sz);
                
                    // the light
                    
                color=getRandomColor();
                if (GeneratorMain.random.nextBoolean()) color=adjustColor(color,0.8f);
                drawOval((dx+1),(dy+1),(dx+(sz-1)),(dy+(sz-1)),0.0f,1.0f,0.0f,0.0f,sz,0.8f,color,null,0.5f,false,false,1.0f,0.0f);
                
                    // the possible emissive
                    
                if (GeneratorMain.random.nextBoolean()) drawOvalEmissive(dx,dy,(dx+sz),(dy+sz),adjustColor(color,0.7f));
            }
        }
    }
    
    private void generateComputerComponentButtons(int lft,int top,int rgt,int bot,int edgeSize)
    {
        int         x,y,xCount,yCount,xMargin,yMargin,
                    dx,dy,sz;
        RagColor    color,outlineColor;
        
        lft+=edgeSize;
        rgt-=edgeSize;
        top+=edgeSize;
        bot-=edgeSize;
        
        sz=10+GeneratorMain.random.nextInt(15);
        
        xCount=(rgt-lft)/sz;
        yCount=(bot-top)/sz;
        
        if (xCount<=0) xCount=1;
        if (yCount<=0) yCount=1;
        
        xMargin=(((rgt-lft)-(xCount*sz))/2);
        yMargin=(((bot-top)-(yCount*sz))/2);
        
        outlineColor=getRandomGray(0.1f,0.3f);
        
        for (y=0;y!=yCount;y++) {
            dy=(top+yMargin)+(y*sz);
            
            for (x=0;x!=xCount;x++) {
                dx=(lft+xMargin)+(x*sz);
                
                    // the button
                
                color=getRandomColor();
                drawRect(dx,dy,(dx+sz),(dy+sz),color);
                draw3DFrameRect(dx,dy,(dx+sz),(dy+sz),2,outlineColor,true);
                
                    // the possible emissive
                    
                if (GeneratorMain.random.nextBoolean()) drawRectEmissive(dx,dy,(dx+sz),(dy+sz),color);
            }
        }
    }
    
    private void generateComputerComponentDrives(int lft,int top,int rgt,int bot,int edgeSize)
    {
        int         x,y,xCount,yCount,dx,dy,bx,by,
                    wid,high,ledWid,ledHigh,xMargin,yMargin;
        RagColor    color,outlineColor,ledColor;
        
        lft+=edgeSize;
        rgt-=edgeSize;
        top+=edgeSize;
        bot-=edgeSize;
        
            // the random color (always dark)
            
        color=getRandomGray(0.1f,0.3f);
        outlineColor=adjustColor(color,0.8f);
        
            // the drive sizes
            // pick randomly, but make sure they fill entire size
        
        high=15+GeneratorMain.random.nextInt(15);
        wid=high*2;
        
        ledWid=(int)((float)high*0.1f);
        if (ledWid<4) ledWid=4;
        ledHigh=(int)((float)ledWid*0.5f);
        
        xCount=(rgt-lft)/wid;
        yCount=(bot-top)/high;
        
        if (xCount<=0) xCount=1;
        if (yCount<=0) yCount=1;
        
        wid=(rgt-lft)/xCount;
        high=(bot-top)/yCount;
        
        xMargin=((rgt-lft)-(xCount*wid))/2;
        yMargin=((bot-top)-(yCount*high))/2;
        
        for (y=0;y!=yCount;y++) {
            dy=(top+yMargin)+(y*high);
            
            for (x=0;x!=xCount;x++) {
                dx=(lft+xMargin)+(x*wid);
                
                    // the drive
                
                drawRect(dx,dy,(dx+wid),(dy+high),color);
                draw3DFrameRect(dx,dy,(dx+wid),(dy+high),2,outlineColor,true);
                
                    // the emissive indicator
                
                ledColor=LED_COLORS[GeneratorMain.random.nextInt(3)];
                
                bx=(dx+wid)-(ledWid+5);
                by=(dy+high)-(ledHigh+5);
                drawRect(bx,by,(bx+ledWid),(by+ledHigh),ledColor);
                drawRectEmissive(bx,by,(bx+ledWid),(by+ledHigh),ledColor);
            }
        }
    }
    
    private void generateComputerComponentScreen(int lft,int top,int rgt,int bot,int edgeSize)
    {
        int         x,y,dx,dy,rowCount,colCount,colCount2;
        RagColor    screenColor,charColor,emissiveCharColor;
        
        lft+=edgeSize;
        rgt-=edgeSize;
        top+=edgeSize;
        bot-=edgeSize;
        
        screenColor=new RagColor(0.2f,0.25f,0.2f);
        charColor=new RagColor(0.2f,0.6f,0.2f);
        emissiveCharColor=adjustColor(charColor,0.8f);

            // screen
            
        drawRect(lft,top,rgt,bot,COLOR_BLACK);
        
        drawOval((lft+3),(top+3),(lft+13),(top+13),0.0f,1.0f,0.0f,0.0f,0,0.0f,screenColor,null,0.5f,false,false,1.0f,0.0f);
        drawOval((rgt-13),(top+3),(rgt-3),(top+13),0.0f,1.0f,0.0f,0.0f,0,0.0f,screenColor,null,0.5f,false,false,1.0f,0.0f);
        drawOval((lft+3),(bot-13),(lft+13),(bot-3),0.0f,1.0f,0.0f,0.0f,0,0.0f,screenColor,null,0.5f,false,false,1.0f,0.0f);
        drawOval((rgt-13),(bot-13),(rgt-3),(bot-3),0.0f,1.0f,0.0f,0.0f,0,0.0f,screenColor,null,0.5f,false,false,1.0f,0.0f);
        
        drawRect((lft+8),(top+8),(rgt-8),(bot-8),screenColor);
        drawRect((lft+8),(top+3),(rgt-8),(top+8),screenColor);
        drawRect((lft+8),(bot-8),(rgt-8),(bot-3),screenColor);
        drawRect((lft+3),(top+8),(lft+8),(bot-8),screenColor);
        drawRect((rgt-8),(top+8),(rgt-3),(bot-8),screenColor);
        
            // chars
            
        dy=top+10;
        rowCount=((bot-top)-20)/10;
        
        for (y=0;y<rowCount;y++) {
            colCount=3+((((rgt-lft)-20)/7)-3);
            
            dx=lft+10;
            
            colCount2=(colCount/2)+GeneratorMain.random.nextInt(colCount/2);
            
            for (x=0;x<colCount2;x++) {
                
                switch (GeneratorMain.random.nextInt(5)) {
                    case 0:
                        drawRect(dx,dy,(dx+5),(dy+8),charColor);
                        drawRectEmissive(dx,dy,(dx+5),(dy+8),emissiveCharColor);
                        break;
                    case 1:
                        drawRect((dx+2),dy,(dx+5),(dy+6),charColor);
                        drawRectEmissive((dx+2),dy,(dx+5),(dy+6),emissiveCharColor);
                        break;
                    case 2:
                        drawRect(dx,(dy+5),(dx+5),(dy+8),charColor);
                        drawRectEmissive(dx,(dy+5),(dx+5),(dy+8),emissiveCharColor);
                        break;
                    case 3:
                        drawRect(dx,dy,(dx+5),(dy+3),charColor);
                        drawRectEmissive(dx,dy,(dx+5),(dy+3),emissiveCharColor);
                        break;
                }
                
                dx+=7;
            }
            
            dy+=10;
        }
    }

    private void generateComputerComponents(int lft,int top,int rgt,int bot,RagColor panelColor,int edgeSize,int variationMode)
    {
        int         mx,my,sz,lx,ty,rx,by,rndTry,
                    componentType,lightCount,buttonCount,
                    minPanelSize,extraPanelSize,skipPanelSize;
        boolean     hadWires,hadShutter,hadScreen,hadBlank,
                    rndSuccess;
        
            // inside components
            // these are stacks of vertical or horizontal chunks
            
        mx=lft+edgeSize;
        my=top+edgeSize;
        
        hadWires=false;
        hadShutter=false;
        hadScreen=false;
        hadBlank=false;
        lightCount=0;
        buttonCount=0;
        
        minPanelSize=(int)((float)textureSize*0.1f);
        extraPanelSize=(int)((float)textureSize*0.04f);
        skipPanelSize=(int)((float)textureSize*0.05f);
        
        while (true) {
            
            lx=mx;
            ty=my;
            sz=minPanelSize+GeneratorMain.random.nextInt(extraPanelSize);
            
                // vertical stack
                
            if (GeneratorMain.random.nextBoolean()) {
                rx=lx+sz;
                if (rx>=(rgt-(skipPanelSize+edgeSize))) rx=rgt-edgeSize;
                by=bot-edgeSize;
                
                mx=rx+edgeSize;
            }
            
                // horizontal stack
                
            else {
                by=ty+sz;
                if (by>=(bot-(skipPanelSize+edgeSize))) by=bot-edgeSize;
                rx=rgt-edgeSize;
                
                my=by+edgeSize;
            }
            
                // box around components, can
                // be randonly in or out
                
            drawRect(lx,ty,rx,by,panelColor);
            draw3DFrameRect(lx,ty,rx,by,edgeSize,panelColor,(GeneratorMain.random.nextBoolean()));
            
                // draw the components
                // we only allow one blank, wires, or shutter

            rndTry=0;
            
            while (rndTry<25) {
                componentType=GeneratorMain.random.nextInt(7);
                
                rndSuccess=false;

                switch (componentType) {
                    case 0:
                        if (variationMode==VARIATION_CONTROL_PANEL) break;              // no wires on panels
                        if (hadWires) break;
                        if ((rx-lx)>(by-ty)) break;     // wires only vertical
                        hadWires=true;
                        generateComputerComponentWires(lx,ty,rx,by,edgeSize);
                        rndSuccess=true;
                        break;
                    case 1:
                        if (variationMode==VARIATION_CONTROL_PANEL) break;              // no shutters on panels
                        if (hadShutter) break;
                        hadShutter=true;
                        generateComputerComponentShutter(lx,ty,rx,by,edgeSize);
                        rndSuccess=true;
                        break;
                    case 2:
                        if (lightCount>1) break;
                        lightCount++;
                        generateComputerComponentLights(lx,ty,rx,by,edgeSize);
                        rndSuccess=true;
                        break;
                    case 3:
                        if (buttonCount>2) break;
                        buttonCount++;
                        generateComputerComponentButtons(lx,ty,rx,by,edgeSize);
                        rndSuccess=true;
                        break;
                    case 4:
                        if (variationMode==VARIATION_CONTROL_PANEL) break;              // no drives on panels
                        generateComputerComponentDrives(lx,ty,rx,by,edgeSize);
                        rndSuccess=true;
                        break;
                    case 5:
                        if (variationMode==VARIATION_CONTROL_PANEL) break;              // no monitors on panels
                        if (hadScreen) break;
                        if ((rx-lx)<(by-ty)) break;     // screens only horizontal
                        hadScreen=true;
                        generateComputerComponentScreen(lx,ty,rx,by,edgeSize);
                        rndSuccess=true;
                        break;
                    case 6:
                        if (hadBlank) break;
                        hadBlank=true;
                        rndSuccess=true;
                        break;
                }
                
                if (rndSuccess) break;
                
                rndTry++;
            }
            
                // no more panels
                
            if ((mx>=(rgt-edgeSize)) || (my>=(bot-edgeSize))) break;
        }
    }

        //
        // computer bitmaps
        //
    
    @Override
    public void generateInternal(int variationMode)
    {
        int         offset,panelEdgeSize,panelInsideEdgeSize;
        RagColor    panelColor,panelInsideColor;
        
        offset=textureSize/2;
        panelEdgeSize=2+GeneratorMain.random.nextInt(4);
        panelInsideEdgeSize=2+GeneratorMain.random.nextInt(3);
        
        panelColor=getRandomGray(0.6f,0.8f);
        panelInsideColor=adjustColor(panelColor,1.1f);
       
            // this is a collection of plates that are
            // used to wrap the object around cubes
            
        drawRect(0,0,textureSize,textureSize,panelColor);
        
        if ((variationMode==VARIATION_COMPUTER_BANK) || (variationMode==VARIATION_CONTROL_PANEL)) {
            generateComputerComponents(0,0,offset,offset,panelInsideColor,panelInsideEdgeSize,variationMode);             // left and right
            generateComputerComponents(offset,0,textureSize,offset,panelInsideColor,panelInsideEdgeSize,variationMode);   // front and back
            draw3DFrameRect(0,offset,offset,textureSize,panelEdgeSize,panelColor,true);                     // top and bottom
        }
        else {
            draw3DFrameRect(0,0,offset,offset,panelEdgeSize,panelColor,true);                       // left and right
            generateComputerComponentScreen(offset,0,textureSize,offset,panelInsideEdgeSize);       // front and back
            draw3DFrameRect(offset,0,textureSize,offset,panelEdgeSize,panelColor,true);             // top and bottom
        }
        
            // set the emissive
            
        emissiveFactor=new RagPoint(1.0f,1.0f,1.0f);
   
            // finish with the metallic-roughness

        createMetallicRoughnessMap(0.75f,0.4f);
    }
}
