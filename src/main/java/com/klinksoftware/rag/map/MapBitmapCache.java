package com.klinksoftware.rag.map;

import com.klinksoftware.rag.bitmaps.*;

public class MapBitmapCache
{
    private int         colorScheme;
    private BitmapBase  wallBitmap,floorBitmap,ceilingBitmap,stepBitmap,
                        platformBitmap,pillarBitmap,boxBitmap,computerBitmap,
                        pipeBitmap;
    
    public MapBitmapCache(int colorScheme)
    {
        this.colorScheme=colorScheme;
        
        wallBitmap=null;
        floorBitmap=null;
        ceilingBitmap=null;
        platformBitmap=null;
        stepBitmap=null;
        pillarBitmap=null;
        boxBitmap=null;
        computerBitmap=null;
        pipeBitmap=null;
    }
/*
    public BitmapBase generateWall()
    {
        let variationMode=0;
        let genBitmap;
        
        if (this.wallBitmap!==null) return(this.wallBitmap);
        
        switch (this.core.randomIndex(4)) {
            case 0:
                genBitmap=new GenerateBitmapBrickClass(this.core,this.colorScheme);
                break;
            case 1:
                genBitmap=new GenerateBitmapStoneClass(this.core,this.colorScheme);
                break;
            case 2:
                genBitmap=new GenerateBitmapWoodClass(this.core,this.colorScheme);
                break;
            case 3:
                genBitmap=new GenerateBitmapMetalClass(this.core,this.colorScheme);
                break;
        }

        this.wallBitmap=genBitmap.generate(variationMode);
        
        return(this.wallBitmap);
    }
    
    public BitmapBase generateFloor()
    {
        let variationMode=0;
        let genBitmap;
        
        if (this.floorBitmap!==null) return(this.floorBitmap);
        
        switch(this.core.randomIndex(5)) {
            case 0:
                genBitmap=new GenerateBitmapWoodClass(this.core,this.colorScheme);
                break;
            case 1:
                genBitmap=new GenerateBitmapConcreteClass(this.core,this.colorScheme);
                break;
            case 2:
                genBitmap=new GenerateBitmapTileClass(this.core,this.colorScheme);
                break;
            case 3:
                genBitmap=new GenerateBitmapMosaicClass(this.core,this.colorScheme);
                break;
            case 4:
                genBitmap=new GenerateBitmapHexagonClass(this.core,this.colorScheme);
                break;
        }
        
        this.floorBitmap=genBitmap.generate(variationMode);
        
        return(this.floorBitmap);
    }
    
    public BitmapBase generateCeiling()
    {
        let variationMode=0;
        let genBitmap;
        
        if (this.ceilingBitmap!==null) return(this.ceilingBitmap);
        
        switch(this.core.randomIndex(3)) {
            case 0:
                genBitmap=new GenerateBitmapWoodClass(this.core,this.colorScheme);
                break;
            case 1:
                genBitmap=new GenerateBitmapConcreteClass(this.core,this.colorScheme);
                break;
            case 2:
                genBitmap=new GenerateBitmapMetalClass(this.core,this.colorScheme);
                break;
        }
        
        this.ceilingBitmap=genBitmap.generate(variationMode);
        
        return(this.ceilingBitmap);
    }
    
    public BitmapBase generatePlatform()
    {
        let variationMode=0;
        let genBitmap;
        
        if (this.platformBitmap!==null) return(this.platformBitmap);
        
        switch (this.core.randomIndex(3)) {
            case 0:
                genBitmap=new GenerateBitmapBrickClass(this.core,this.colorScheme);
                break;
            case 1:
                genBitmap=new GenerateBitmapWoodClass(this.core,this.colorScheme);
                break;
            case 2:
                genBitmap=new GenerateBitmapMetalClass(this.core,this.colorScheme);
                break;
        }
        
        this.platformBitmap=genBitmap.generate(variationMode);

        return(this.platformBitmap);
    }
    
    public BitmapBase generateStep()
    {
        let variationMode=0;
        let genBitmap;
        
        if (this.stepBitmap!==null) return(this.stepBitmap);
        
        switch (this.core.randomIndex(3)) {
            case 0:
                genBitmap=new GenerateBitmapBrickClass(this.core,this.colorScheme);
                break;
            case 1:
                genBitmap=new GenerateBitmapConcreteClass(this.core,this.colorScheme);
                break;
            case 2:
                genBitmap=new GenerateBitmapTileClass(this.core,this.colorScheme);
                break;
        }
        
        this.stepBitmap=genBitmap.generate(variationMode);

        return(this.stepBitmap);
    }
    
    public BitmapBase generatePillar()
    {
        let variationMode=0;
        let genBitmap;
        
        if (this.pillarBitmap!==null) return(this.pillarBitmap);
        
        switch (this.core.randomIndex(4)) {
            case 0:
                genBitmap=new GenerateBitmapBrickClass(this.core,this.colorScheme);
                break;
            case 1:
                genBitmap=new GenerateBitmapStoneClass(this.core,this.colorScheme);
                break;
            case 2:
                genBitmap=new GenerateBitmapConcreteClass(this.core,this.colorScheme);
                break;
            case 3:
                genBitmap=new GenerateBitmapMetalClass(this.core,this.colorScheme);
                break;
        }
        
        this.pillarBitmap=genBitmap.generate(variationMode);

        return(this.pillarBitmap);
    }
    
    public BitmapBase generateBox()
    {
        let variationMode=0;
        let genBitmap;
        
        if (this.boxBitmap!==null) return(this.boxBitmap);
        
        switch (this.core.randomIndex(2)) {
            case 0:
                genBitmap=new GenerateBitmapWoodClass(this.core,this.colorScheme);
                variationMode=genBitmap.VARIATION_BOX;
                break;
            case 1:
                genBitmap=new GenerateBitmapMetalClass(this.core,this.colorScheme);
                variationMode=genBitmap.VARIATION_BOX;
                break;
        }
        
        this.boxBitmap=genBitmap.generate(variationMode);

        return(this.boxBitmap);
    }
    
    public BitmapBase generateComputer()
    {
        let genBitmap;
        
        if (this.computerBitmap!==null) return(this.computerBitmap);
        
        genBitmap=new GenerateBitmapComputerClass(this.core,this.colorScheme);
        this.computerBitmap=genBitmap.generate(genBitmap.VARIATION_NONE);
        
        return(this.computerBitmap);
    }
    
    public BitmapBase generatePipe()
    {
        let genBitmap;
        
        if (this.pipeBitmap!==null) return(this.pipeBitmap);
        
        genBitmap=new GenerateBitmapMetalClass(this.core,this.colorScheme);
        this.pipeBitmap=genBitmap.generate(genBitmap.VARIATION_PIPE);
        
        return(this.pipeBitmap);
    }
    */
}
