package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.utility.*;

public class BitmapWood extends BitmapBase
{
    public final static int VARIATION_BOARDS=0;
    public final static int VARIATION_BOX=1;
    
    public BitmapWood(int colorScheme)
    {
        super(colorScheme);
        
        hasNormal=true;
        hasMetallicRoughness=true;
        hasGlow=false;
        hasAlpha=false;
    }
    
        //
        // wood bitmaps
        //
    
    private void generateWoodDrawBoard(int lft,int top,int rgt,int bot,int edgeSize,RagColor woodColor)
    {
        RagColor        col,frameColor;
        
        col=adjustColorRandom(woodColor,0.7f,1.2f);
        frameColor=adjustColorRandom(col,0.65f,0.75f);
        
            // the board edge
            
        drawRect(lft,top,rgt,bot,col);
        draw3DFrameRect(lft,top,rgt,bot,edgeSize,frameColor,true);
        
            // stripes and a noise overlay
            
        if ((bot-top)>(rgt-lft)) {
            drawColorStripeVertical((lft+edgeSize),(top+edgeSize),(rgt-edgeSize),(bot-edgeSize),0.1f,col);
        }
        else {
            drawColorStripeHorizontal((lft+edgeSize),(top+edgeSize),(rgt-edgeSize),(bot-edgeSize),0.1f,col);
        }
        
        drawPerlinNoiseRect((lft+edgeSize),(top+edgeSize),(rgt-edgeSize),(bot-edgeSize),0.8f,1.2f);
        drawStaticNoiseRect((lft+edgeSize),(top+edgeSize),(rgt-edgeSize),(bot-edgeSize),0.9f,1.0f);
        
            // blur both the color and the normal
            
        this.blur(colorData,(lft+edgeSize),(top+edgeSize),(rgt-edgeSize),(bot-edgeSize),2,true);
        this.blur(normalData,(lft+edgeSize),(top+edgeSize),(rgt-edgeSize),(bot-edgeSize),5,true);
    }

    @Override
    public void generateInternal(int variationMode)
    {
        int         n,y,ty,by,lft,rgt,
                    boardType,boardCount,boardSize,edgeSize;
        RagColor    woodColor;
        
            // some random values

        boardCount=4+(int)(Math.random()*12.0);
        boardSize=textureSize/boardCount;
        edgeSize=(int)(textureSize*0.005f)+(int)(Math.random()*(textureSize*0.005));
        woodColor=getRandomColor();
        
            // perlin noise
            
        this.createPerlinNoiseData(32,32);

            // regular wood planking

        lft=0;
        
        y=(int)((float)textureSize*0.5);
        ty=(int)((float)textureSize*0.33);
        by=(int)((float)textureSize*0.66);

        for (n=0;n!=boardCount;n++) {
            rgt=lft+boardSize;
            if (n==(boardCount-1)) rgt=textureSize;
            
            boardType=(variationMode==VARIATION_BOX)?0:(int)(Math.random()*5.0);
            
            switch (boardType) {
                case 0:
                    generateWoodDrawBoard(lft,0,rgt,textureSize,edgeSize,woodColor);
                    break;
                case 1:
                    generateWoodDrawBoard(lft,0,rgt,y,edgeSize,woodColor);
                    generateWoodDrawBoard(lft,y,rgt,textureSize,edgeSize,woodColor);
                    break;
                case 2:
                    generateWoodDrawBoard(lft,-edgeSize,rgt,y,edgeSize,woodColor);
                    generateWoodDrawBoard(lft,y,rgt,(textureSize+edgeSize),edgeSize,woodColor);
                    break;
                case 3:
                    generateWoodDrawBoard(lft,0,rgt,ty,edgeSize,woodColor);
                    generateWoodDrawBoard(lft,ty,rgt,by,edgeSize,woodColor);
                    generateWoodDrawBoard(lft,by,rgt,textureSize,edgeSize,woodColor);
                    break;
                case 4:
                    generateWoodDrawBoard(lft,-edgeSize,rgt,(textureSize+edgeSize),edgeSize,woodColor);
                    break;
            }
            
            lft=rgt;
        }

            // box outlines
            
        if (variationMode==VARIATION_BOX) {
            woodColor=adjustColor(woodColor,0.7f);
            generateWoodDrawBoard(0,0,boardSize,textureSize,edgeSize,woodColor);
            generateWoodDrawBoard((textureSize-boardSize),0,textureSize,textureSize,edgeSize,woodColor);
            generateWoodDrawBoard(boardSize,0,(textureSize-boardSize),boardSize,edgeSize,woodColor);
            generateWoodDrawBoard(boardSize,(textureSize-boardSize),(textureSize-boardSize),textureSize,edgeSize,woodColor);
        }
   
            // finish with the metallic-roughness

        createMetallicRoughnessMap(0.4f,0.2f);
    }
}
