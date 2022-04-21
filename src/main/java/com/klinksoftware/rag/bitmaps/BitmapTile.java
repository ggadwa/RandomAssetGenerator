package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

public class BitmapTile extends BitmapBase
{
    public BitmapTile()    {
        super();

        hasNormal=true;
        hasMetallicRoughness=true;
        hasEmissive=false;
        hasAlpha=false;
    }

        //
        // tile bitmaps
        //

    private void generateTilePiece(int lft,int top,int rgt,int bot,RagColor[] tileColor,RagColor designColor,int splitCount,boolean complex)
    {
        int         x,y,sx,sy,ex,ey,mx,
                    dLft,dRgt,dTop,dBot,padding,
                    crackSegCount,crackXVarient,crackYVarient,
                    tileStyle,tileContent,edgeSize,tileWid,tileHigh;
        boolean     tileDirty;
        RagColor    col,frameCol;

            // tile style

        tileStyle=AppWindow.random.nextInt(3);
        tileContent=AppWindow.random.nextInt(4);
        tileDirty=(AppWindow.random.nextFloat()<0.2f);
        edgeSize=(int)(((float)textureSize*0.005f)+(AppWindow.random.nextFloat()*((float)textureSize*0.01f)));

            // splits

        tileWid=(rgt-lft)/splitCount;
        tileHigh=(bot-top)/splitCount;

        for (y=0;y!=splitCount;y++) {

            dTop=top+(tileHigh*y);
            dBot=dTop+tileHigh;
            if (y==(splitCount-1)) dBot=bot;

            dLft=lft;

            for (x=0;x!=splitCount;x++) {

                dLft=lft+(tileWid*x);
                dRgt=dLft+tileWid;
                if (x==(splitCount-1)) dRgt=rgt;

                    // sometimes a tile piece is a recursion to
                    // another tile set

                if ((complex) && (AppWindow.random.nextFloat()<0.25f)) {
                    tileStyle=AppWindow.random.nextInt(3);
                    generateTilePiece(dLft,dTop,dRgt,dBot,tileColor,designColor,2,false);
                    continue;
                }

                    // make the tile

                col=adjustColorRandom(tileColor[0],0.9f,1.1f);

                switch (tileStyle) {

                    case 0:         // border style
                        if ((x!=0) && (y!=0)) col=tileColor[1];
                        break;

                    case 1:         // checker board style
                        col=tileColor[(x+y)&0x1];
                        break;

                    case 2:         // stripe style
                        if ((x&0x1)!=0) col=tileColor[1];
                        break;

                }

                frameCol=adjustColorRandom(col,0.85f,0.95f);

                drawRect(dLft,dTop,dRgt,dBot,col);
                draw3DFrameRect(dLft,dTop,dRgt,dBot,edgeSize,frameCol,true);

                    // possible design
                    // 0 = nothing

                if (complex) {
                    col=adjustColorRandom(col,0.75f,0.85f);
                    padding=(edgeSize*2)+AppWindow.random.nextInt(10);

                    switch (tileContent) {
                        case 0:
                            drawOval((dLft+padding),(dTop+padding),(dRgt-padding),(dBot-padding),0.0f,1.0f,0.0f,0.0f,edgeSize,0.8f,designColor,null,0.5f,false,false,1.0f,0.0f);
                            break;
                        case 1:
                            drawDiamond((dLft+padding),(dTop+padding),(dRgt-padding),(dBot-padding),designColor);
                            break;
                        case 2:
                            mx=(dLft+dRgt)/2;
                            drawTriangle(mx,(dTop+padding),(dLft+padding),(dBot-padding),(dRgt-padding),(dBot-padding),designColor);
                            break;
                    }
                }

                drawPerlinNoiseRect((dLft+edgeSize),(dTop+edgeSize),(dRgt-edgeSize),(dBot-edgeSize),0.8f,1.1f);

                    // possible dirt

                if (tileDirty) {
                    drawStaticNoiseRect((dLft+edgeSize),(dTop+edgeSize),(dRgt-edgeSize),(dBot-edgeSize),0.8f,1.2f);
                    blur(colorData,(dLft+edgeSize),(dTop+edgeSize),(dRgt-edgeSize),(dBot-edgeSize),5,false);
                }

                    // possible crack

                if ((AppWindow.random.nextFloat()<0.2f) && (!complex)) {
                    switch (AppWindow.random.nextInt(4)) {
                        case 0:
                            sy=dTop+edgeSize;
                            ey=(dTop+edgeSize)+AppWindow.random.nextInt((dBot-dTop)/2);
                            sx=(dLft+edgeSize)+AppWindow.random.nextInt((dRgt-dLft)/2);
                            ex=dLft+edgeSize;
                            crackXVarient=5;
                            crackYVarient=5;
                            break;
                        case 1:
                            sy=dTop+edgeSize;
                            ey=(dTop+edgeSize)+AppWindow.random.nextInt((dBot-dTop)/2);
                            sx=((dLft+dRgt)/2)+AppWindow.random.nextInt((dRgt-dLft)/2);
                            ex=dRgt-edgeSize;
                            crackXVarient=-5;
                            crackYVarient=5;
                            break;
                        case 2:
                            sy=dBot-edgeSize;
                            ey=((dTop+dBot)/2)+AppWindow.random.nextInt((dBot-dTop)/2);
                            sx=(dLft+edgeSize)+AppWindow.random.nextInt((dRgt-dLft)/2);
                            ex=dLft+edgeSize;
                            crackXVarient=-5;
                            crackYVarient=5;
                            break;
                        default:
                            sy=dBot-edgeSize;
                            ey=((dTop+dBot)/2)+AppWindow.random.nextInt((dBot-dTop)/2);
                            sx=((dLft+dRgt)/2)+AppWindow.random.nextInt((dRgt-dLft)/2);
                            ex=dRgt-edgeSize;
                            crackXVarient=-5;
                            crackYVarient=-5;
                            break;
                    }

                    crackSegCount=2+AppWindow.random.nextInt(2);
                    drawSimpleCrack(sx,sy,ex,ey,crackSegCount,crackXVarient,crackYVarient,frameCol);
                }
            }
        }
    }

    @Override
    public void generateInternal()    {
        int             splitCount;
        boolean         complex,small;
        RagColor[]      tileColor;
        RagColor        designColor;

            // get splits

        complex=(AppWindow.random.nextBoolean());

        small=false;
        if (!complex) small=(AppWindow.random.nextBoolean());

        if (!small) {
            splitCount=2+AppWindow.random.nextInt(2);
        }
        else {
            splitCount=6+AppWindow.random.nextInt(4);
        }

            // colors

        tileColor=new RagColor[2];
        tileColor[0]=getRandomColor();
        tileColor[1]=getRandomColor();
        designColor=getRandomColor();

        createPerlinNoiseData(16,16);

            // original splits

        generateTilePiece(0,0,textureSize,textureSize,tileColor,designColor,splitCount,complex);

            // finish with the metallic-roughness

        createMetallicRoughnessMap(0.45f,0.4f);
    }
}
