package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.utility.*;

public class BitmapComputer extends BitmapBase
{
    public final static int VARIATION_COMPUTER_BANK=0;
    public final static int VARIATION_CONTROL_PANEL=1;
    
    public BitmapComputer(int colorScheme)
    {
        super(colorScheme);
        
        textureSize=1024;
        hasNormal=true;
        hasMetallicRoughness=true;
        hasGlow=true;
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
                y=top+(int)(Math.random()*(double)(bot-top));
                
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
                x=lft+(int)(Math.random()*(double)(rgt-lft));
                
                lineColor=this.getRandomColor();
                drawRandomLine(x,top,x,bot,lft,top,rgt,bot,lineVar,lineColor,true);
            }
        }
    }
    
    private void generateComputerComponentShutter(int lft,int top,int rgt,int bot,int edgeSize)
    {
        /*
        let sz;
        let shutterCount,shutterColor,shutterEdgeColor;

        lft+=edgeSize;
        rgt-=edgeSize;
        top+=edgeSize;
        bot-=edgeSize;
       
        shutterColor=this.getRandomColor();
        shutterEdgeColor=this.adjustColor(shutterColor,0.9);
        
        sz=Math.trunc(Math.max((rgt-lft),(bot-top))*0.1);
        shutterCount=this.core.randomInt(sz,sz);
        
        this.drawMetalShine(lft,top,rgt,bot,shutterColor);
        
        if ((rgt-lft)>(bot-top)) {
            this.drawNormalWaveHorizontal(lft,top,rgt,bot,shutterColor,shutterEdgeColor,shutterCount);
        }
        else {
            this.drawNormalWaveVertical(lft,top,rgt,bot,shutterColor,shutterEdgeColor,shutterCount);
        }
        */
    }
    
    private void generateComputerComponentLights(int lft,int top,int rgt,int bot,int edgeSize)
    {
        /*
        let x,y,xCount,yCount,xMargin,yMargin,dx,dy,sz;
        let color;
        
        lft+=edgeSize;
        rgt-=edgeSize;
        top+=edgeSize;
        bot-=edgeSize;
        
        sz=this.core.randomInt(12,5);
        
        xCount=Math.trunc((rgt-lft)/sz);
        yCount=Math.trunc((bot-top)/sz);
        
        if (xCount<=0) xCount=1;
        if (yCount<=0) yCount=1;
        
        xMargin=Math.trunc(((rgt-lft)-(xCount*sz))*0.5)+1;
        yMargin=Math.trunc(((bot-top)-(yCount*sz))*0.5)+1;
        
        for (y=0;y!==yCount;y++) {
            dy=(top+yMargin)+(y*sz);
            
            for (x=0;x!==xCount;x++) {
                dx=(lft+xMargin)+(x*sz);
                
                    // the light
                    
                color=this.getRandomColor();
                if (this.core.randomPercentage(0.5)) color=this.adjustColor(color,0.8);
                this.drawOval((dx+1),(dy+1),(dx+(sz-1)),(dy+(sz-1)),0,1,0,0,sz,0.8,color,null,0.5,false,false,1,0);
                
                    // the possible glow
                    
                if (this.core.randomPercentage(0.5)) this.drawOvalGlow(dx,dy,(dx+sz),(dy+sz),this.adjustColor(color,0.7));
            }
        }
        */
    }
    
    private void generateComputerComponentButtons(int lft,int top,int rgt,int bot,int edgeSize)
    {
        /*
        let x,y,xCount,yCount,xMargin,yMargin,dx,dy,sz;
        let color,outlineColor;
        
        lft+=edgeSize;
        rgt-=edgeSize;
        top+=edgeSize;
        bot-=edgeSize;
        
        sz=this.core.randomInt(10,15);
        
        xCount=Math.trunc((rgt-lft)/sz);
        yCount=Math.trunc((bot-top)/sz);
        
        if (xCount<=0) xCount=1;
        if (yCount<=0) yCount=1;
        
        xMargin=Math.trunc(((rgt-lft)-(xCount*sz))*0.5);
        yMargin=Math.trunc(((bot-top)-(yCount*sz))*0.5);
        
        outlineColor=this.getRandomGray(0.1,0.3);
        
        for (y=0;y!==yCount;y++) {
            dy=(top+yMargin)+(y*sz);
            
            for (x=0;x!==xCount;x++) {
                dx=(lft+xMargin)+(x*sz);
                
                    // the button
                
                color=this.getRandomColor();
                this.drawRect(dx,dy,(dx+sz),(dy+sz),color);
                this.draw3DFrameRect(dx,dy,(dx+sz),(dy+sz),2,outlineColor,true);
                
                    // the possible glow
                    
                if (this.core.randomPercentage(0.5)) this.drawRectGlow(dx,dy,(dx+sz),(dy+sz),color);
            }
        }
        */
    }
    
    private void generateComputerComponentDrives(int lft,int top,int rgt,int bot,int edgeSize)
    {
        /*
        let x,y,xCount,yCount,dx,dy,bx,by,wid,high,ledWid,ledHigh,xMargin,yMargin;
        let color,outlineColor,ledColor;
        let ledColors=[new ColorClass(0.0,1.0,0.0),new ColorClass(1.0,1.0,0.0),new ColorClass(1.0,0.0,0.0)];
        
        lft+=edgeSize;
        rgt-=edgeSize;
        top+=edgeSize;
        bot-=edgeSize;
        
            // the random color (always dark)
            
        color=this.getRandomGray(0.1,0.3);
        outlineColor=this.adjustColor(color,0.8);
        
            // the drive sizes
            // pick randomly, but make sure they fill entire size
        
        high=this.core.randomInt(15,15);
        wid=high*2;
        
        ledWid=Math.trunc(high*0.1);
        if (ledWid<4) ledWid=4;
        ledHigh=Math.trunc(ledWid*0.5);
        
        xCount=Math.trunc((rgt-lft)/wid);
        yCount=Math.trunc((bot-top)/high);
        
        if (xCount<=0) xCount=1;
        if (yCount<=0) yCount=1;
        
        wid=Math.trunc((rgt-lft)/xCount);
        high=Math.trunc((bot-top)/yCount);
        
        xMargin=Math.trunc(((rgt-lft)-(xCount*wid))*0.5);
        yMargin=Math.trunc(((bot-top)-(yCount*high))*0.5);
        
        for (y=0;y!==yCount;y++) {
            dy=(top+yMargin)+(y*high);
            
            for (x=0;x!==xCount;x++) {
                dx=(lft+xMargin)+(x*wid);
                
                    // the drive
                
                this.drawRect(dx,dy,(dx+wid),(dy+high),color);
                this.draw3DFrameRect(dx,dy,(dx+wid),(dy+high),2,outlineColor,true);
                
                    // the glowing indicator
                
                ledColor=ledColors[this.core.randomIndex(3)];
                
                bx=(dx+wid)-(ledWid+5);
                by=(dy+high)-(ledHigh+5);
                this.drawRect(bx,by,(bx+ledWid),(by+ledHigh),ledColor);
                this.drawRectGlow(bx,by,(bx+ledWid),(by+ledHigh),ledColor);
            }
        }
        */
    }
    
    private void generateComputerComponentScreen(int lft,int top,int rgt,int bot,int edgeSize)
    {
    /*
        let x,y,dx,dy,rowCount,colCount;
        let screenColor=new ColorClass(0.2,0.25,0.2);
        let charColor=new ColorClass(0.2,0.6,0.2);
        
        lft+=edgeSize;
        rgt-=edgeSize;
        top+=edgeSize;
        bot-=edgeSize;

            // screen
            
        this.drawRect(lft,top,rgt,bot,this.blackColor);
        
        this.drawOval((lft+3),(top+3),(lft+13),(top+13),0,1,0,0,0,0,screenColor,null,0.5,false,false,1,0);
        this.drawOval((rgt-13),(top+3),(rgt-3),(top+13),0,1,0,0,0,0,screenColor,null,0.5,false,false,1,0);
        this.drawOval((lft+3),(bot-13),(lft+13),(bot-3),0,1,0,0,0,0,screenColor,null,0.5,false,false,1,0);
        this.drawOval((rgt-13),(bot-13),(rgt-3),(bot-3),0,1,0,0,0,0,screenColor,null,0.5,false,false,1,0);
        
        this.drawRect((lft+8),(top+8),(rgt-8),(bot-8),screenColor);
        this.drawRect((lft+8),(top+3),(rgt-8),(top+8),screenColor);
        this.drawRect((lft+8),(bot-8),(rgt-8),(bot-3),screenColor);
        this.drawRect((lft+3),(top+8),(lft+8),(bot-8),screenColor);
        this.drawRect((rgt-8),(top+8),(rgt-3),(bot-8),screenColor);
        
            // chars
            
        dy=top+10;
        rowCount=Math.trunc(((bot-top)-20)/10);
        
        for (y=0;y<rowCount;y++) {
            colCount=this.core.randomInt(3,(Math.trunc(((rgt-lft)-20)/7)-3));
            
            dx=lft+10;
            
            for (x=0;x<colCount;x++) {
                
                switch (this.core.randomIndex(5)) {
                    case 0:
                        this.drawRect(dx,dy,(dx+5),(dy+8),charColor);
                        break;
                    case 1:
                        this.drawRect((dx+2),dy,(dx+5),(dy+6),charColor);
                        break;
                    case 2:
                        this.drawRect(dx,(dy+5),(dx+5),(dy+8),charColor);
                        break;
                    case 3:
                        this.drawRect(dx,dy,(dx+5),(dy+3),charColor);
                        break;
                }
                
                dx+=7;
            }
            
            dy+=10;
        }
*/
    }

    private void generateComputerComponents(int lft,int top,int rgt,int bot,RagColor panelColor,int edgeSize)
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
            sz=minPanelSize+(int)(Math.random()*(double)extraPanelSize);
            
                // vertical stack
                
            if (Math.random()<0.5) {
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
            draw3DFrameRect(lx,ty,rx,by,edgeSize,panelColor,(Math.random()<0.5));
            
                // draw the components
                // we only allow one blank, wires, or shutter

            rndTry=0;
            
            while (rndTry<25) {
                componentType=(int)(Math.random()*7.0);
                
                rndSuccess=false;

                switch (componentType) {
                    case 0:
                        if (hadWires) break;
                        if ((rx-lx)>(by-ty)) break;     // wires only vertical
                        hadWires=true;
                        generateComputerComponentWires(lx,ty,rx,by,edgeSize);
                        rndSuccess=true;
                        break;
                    case 1:
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
                        generateComputerComponentDrives(lx,ty,rx,by,edgeSize);
                        rndSuccess=true;
                        break;
                    case 5:
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
        panelEdgeSize=2+(int)(Math.random()*3.0);
        panelInsideEdgeSize=2+(int)(Math.random()*2.0);
        
        panelColor=getRandomColor();
        panelInsideColor=adjustColor(panelColor,1.1f);
       
            // this is a collection of plates that are
            // used to wrap the object around cubes
            
        drawRect(0,0,textureSize,textureSize,panelColor);
        
        generateComputerComponents(0,0,offset,offset,panelInsideColor,panelInsideEdgeSize);             // left and right
        generateComputerComponents(offset,0,textureSize,offset,panelInsideColor,panelInsideEdgeSize);   // front and back
        draw3DFrameRect(0,offset,offset,textureSize,panelEdgeSize,panelColor,true);                     // top and bottom
        
            // set the emissive
            
        emissiveFactor=new RagVector(1.0f,1.0f,1.0f);
   
            // finish with the metallic-roughness

        createMetallicRoughnessMap(0.75f,0.4f);
    }
}
