package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

public class BitmapBody extends BitmapBase
{
    public final static int VARIATION_NONE=0;
    
    public BitmapBody()
    {
        super();
        
        hasNormal=true;
        hasMetallicRoughness=true;
        hasEmissive=false;
        hasAlpha=false;
    }
    
        //
        // fur
        //
        
    private void generateFurChunk(int lft,int top,int rgt,int bot)
    {
        /*
        let n,x,y;
        let darken,boost,lineColor;
        let wid=rgt-lft;
        let high=bot-top;
        let halfHigh=Math.trunc(high*0.5);
        let furColor=this.getRandomFurColor();
        
        this.drawRect(lft,top,rgt,bot,furColor);

            // hair
            
        for (x=lft;x!==rgt;x++) {
            
                // hair color
                
            if ((n%2)===0) {
                darken=0.5+(genRandom.random()*0.3);
                lineColor=this.darkenColor(furColor,darken);
            }
            else {
                boost=0.1+(genRandom.random()*0.3);
                lineColor=this.boostColor(furColor,boost);
            }
            
                // hair half from top
                
            y=halfHigh+genRandom.randomInt(top,halfHigh);
            this.drawRandomLine(x,(top-5),x,(y+5),lft,top,rgt,bot,10,lineColor,false);
            
                // hair half from bottom
                
            y=high-(halfHigh+genRandom.randomInt(top,halfHigh));
            this.drawRandomLine(x,(y-5),x,(bot+5),lft,top,rgt,bot,10,lineColor,false);
        }
*/
    }
    
        //
        // cloth
        //
        
    private void generateClothChunk(int lft,int top,int rgt,int bot)
    {
        /*
        let n,x,x2,y,y2,lineCount;
        let darken,lineColor;
        let wid=rgt-lft;
        let high=bot-top;
        let clothColor=this.getRandomColor();
         
        this.drawRect(lft,top,rgt,bot,clothColor);
        this.addNoiseRect(lft,top,rgt,bot,0.8,0.9,0.5);        
 
            // lines
            
        lineCount=genRandom.randomInt(30,30);
            
        for (n=0;n!==lineCount;n++) {
            x=genRandom.randomInt(lft,wid);
            y=genRandom.randomInt(top,high);
            y2=genRandom.randomInt(top,high);
            
            darken=0.6+(genRandom.random()*0.25);
            lineColor=this.darkenColor(clothColor,darken);
            
            this.drawRandomLine(x,y,x,y2,lft,top,rgt,bot,30,lineColor,false);
        }
        
        lineCount=genRandom.randomInt(30,30);
            
        for (n=0;n!==lineCount;n++) {
            x=genRandom.randomInt(lft,wid);
            x2=genRandom.randomInt(lft,wid);
            y=genRandom.randomInt(top,high);
            
            darken=0.6+(genRandom.random()*0.25);
            lineColor=this.darkenColor(clothColor,darken);
            
            this.drawRandomLine(x,y,x2,y,lft,top,rgt,bot,30,lineColor,false);
        }
        
            // blur it
            
        this.blur(lft,top,rgt,bot,25,false);
*/
    }
    
        //
        // scales
        //
        
    private void generateScaleChunk(int lft,int top,int rgt,int bot)
    {
        /*
        let x,y,dx,dy,sx,sy,sx2,sy2;
        let xCount,col;

        let scaleColor=this.getRandomScaleColor();
        let borderColor=this.darkenColor(scaleColor,0.7);
        let scaleCount=genRandom.randomInt(8,10);

        let wid=rgt-lft;
        let high=bot-top;
        let sWid=wid/scaleCount;
        let sHigh=high/scaleCount;
        
        this.startClip(lft,top,rgt,bot);
         
            // background

        this.drawRect(lft,top,rgt,bot,scaleColor);
        this.addNoiseRect(lft,top,rgt,bot,0.5,0.7,0.6);
        this.blur(lft,top,rgt,bot,5,false);
        
            // scales (need extra row for overlap)

        dy=bot-sHigh;
        
        for (y=0;y!==(scaleCount+1);y++) {

            if ((y%2)===0) {
                dx=lft;
                xCount=scaleCount;
            }
            else {
                dx=lft-Math.trunc(sWid*0.5);
                xCount=scaleCount+1;
            }
            
            for (x=0;x!==xCount;x++) {
                
                    // can have darkened scale if not on
                    // wrapping rows
                    
                col=scaleColor;
                
                if ((y!==0) && (y!==scaleCount) && (x!==0) && (x!==(xCount-1))) {
                    if (genRandom.randomPercentage(0.2)) {
                        col=this.darkenColor(scaleColor,genRandom.randomFloat(0.6,0.3));
                    }
                }
                
                    // some slight offsets
                    
                sx=Math.trunc(dx)+(5-genRandom.randomInt(0,10));
                sy=Math.trunc(dy)+(5-genRandom.randomInt(0,10));
                sx2=Math.trunc(dx+sWid);
                sy2=Math.trunc(dy+(sHigh*2));
                
                    // the scale itself
                    // we draw the scale as a solid, flat oval and
                    // then redraw the border with normals
                    
                this.draw3DOval(sx,sy,sx2,sy2,0.25,0.75,3,0,null,borderColor);
                this.drawOval(sx,sy,sx2,sy2,0.0,1.0,3,0,col,null);
                
                dx+=sWid;
            }
            
            dy-=sHigh;
        }
        
        this.endClip();
*/
    }
    
        //
        // metal
        //
        
    private void generateMetalChunk(int lft,int top,int rgt,int bot)
    {
        /*
        let metalColor=this.getRandomMetalColor();
        
        this.draw3DRect(lft,top,rgt,bot,0,metalColor,genRandom.randomPercentage(0.5));
        this.generateMetalStreakShine(lft,top,rgt,bot,metalColor);
*/
    }
    
        //
        // spots
        //
        
    private void generateSpots(int lft,int top,int rgt,int bot)
    {
        /*
        let innerWid=rgt-lft;
        let innerHigh=bot-top;
        let spotMin=Math.trunc(innerWid/5);
        let spotAdd=Math.trunc(innerWid/10);
        let n,x,y,spotSize;
        let spotCount=genRandom.randomInt(10,10);
        
            // the fur
            
        this.generateFurChunk(lft,top,rgt,bot);
        
            // the fur spots
        
        this.bitmapCTX.globalAlpha=genRandom.randomFloat(0.1,0.3);
 
        for (n=0;n!==spotCount;n++) {
            spotSize=genRandom.randomInt(spotMin,spotAdd);
            x=genRandom.randomInt(0,(innerWid-spotSize))+lft;
            y=genRandom.randomInt(0,(innerHigh-spotSize))+top;
            this.drawOval(x,y,(x+spotSize),(y+spotSize),this.blackColor,null);
        }
        
        this.bitmapCTX.globalAlpha=1.0;
*/
    }
    
        //
        // face chunks
        //
        
    private void generateFaceChunkEye(int x,int top,int bot,RagColor eyeColor)
    {
        /*
        this.draw3DOval(this.normalCTX,x,(top+80),(x+30),(top+90),0.0,1.0,1,0,this.whiteColor,this.blackColor);
        this.drawOval((x+10),(top+81),(x+20),(top+89),eyeColor,null);
        
*/
    }
    
    private void generateFaceChunk(int lft,int top,int rgt,int bot)
    {
        /*
        let eyeColor=this.getRandomColor();
        
        this.generateFaceChunkEye(480,top,bot,eyeColor);
        this.generateFaceChunkEye(430,top,bot,eyeColor);
*/
    }

        //
        // random chunk
        //
    
    private void generateRandomChunk(int lft,int top,int rgt,int bot,boolean isFace)
    {
        switch (GeneratorMain.random.nextInt(5)) {
            
            case 0:
                generateFurChunk(lft,top,rgt,bot);
                break;
            
            case 1:
                generateClothChunk(lft,top,rgt,bot);
                break;
                
            case 2:
                generateScaleChunk(lft,top,rgt,bot);
                break;
                
            case 3:
                generateMetalChunk(lft,top,rgt,bot);
                break;
                
            case 4:
                generateSpots(lft,top,rgt,bot);
                break;
        }
        
        if (isFace) generateFaceChunk(lft,top,rgt,bot);
    }
    
        //
        // body bitmaps
        //

    @Override
    public void generateInternal(int variationMode)
    {
        int         mx,my;
        RagColor    color;
        
            // default to black
            
        drawRect(0,0,textureSize,textureSize,COLOR_BLACK);
        
            // the chunks

        mx=textureSize/2;
        my=textureSize/2;
            
        generateRandomChunk(0,0,mx,my,false);
        generateRandomChunk(mx,0,textureSize,my,true);
        generateRandomChunk(0,my,mx,textureSize,false);
       
            // finish with the metallic-roughness

        createMetallicRoughnessMap(0.5f,0.5f);
    }
}
