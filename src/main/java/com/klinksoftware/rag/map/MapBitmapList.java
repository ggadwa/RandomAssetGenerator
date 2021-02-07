package com.klinksoftware.rag.map;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.bitmaps.*;

import java.util.*;

public class MapBitmapList
{
    private boolean     hasWallBitmap,hasFloorBitmap,hasCeilingBitmap,hasStepBitmap,
                        hasPlatformBitmap,hasPillarBitmap,hasBoxBitmap,hasComputerBitmap,
                        hasPanelBitmap,hasPipeBitmap;
    private String      basePath;
    
    public MapBitmapList(String basePath)
    {
        this.basePath=basePath;
        
        hasWallBitmap=false;
        hasFloorBitmap=false;
        hasCeilingBitmap=false;
        hasPlatformBitmap=false;
        hasStepBitmap=false;
        hasPillarBitmap=false;
        hasBoxBitmap=false;
        hasComputerBitmap=false;
        hasPipeBitmap=false;
    }

    public void generateWall()
    {
        int         variationMode;
        BitmapBase  bitmapBase;
        
        if (hasWallBitmap) return;
        
        switch (GeneratorMain.random.nextInt(4)) {
            case 0:
                bitmapBase=new BitmapBrick();
                variationMode=BitmapBrick.VARIATION_NONE;
                break;
            case 1:
                bitmapBase=new BitmapStone();
                variationMode=BitmapStone.VARIATION_NONE;
                break;
            case 2:
                bitmapBase=new BitmapWood();
                variationMode=BitmapWood.VARIATION_BOARDS;
                break;
            default:
                bitmapBase=new BitmapMetal();
                variationMode=BitmapMetal.VARIATION_PLATE;
                break;
        }

        bitmapBase.generate(variationMode,basePath,"wall");
        
        hasWallBitmap=true;
    }
    
    public void generateFloor()
    {
        int         variationMode;
        BitmapBase  bitmapBase;
        
        if (hasFloorBitmap) return;
        
        switch(GeneratorMain.random.nextInt(6)) {
            case 0:
                bitmapBase=new BitmapWood();
                variationMode=BitmapWood.VARIATION_BOARDS;
                break;
            case 1:
                bitmapBase=new BitmapConcrete();
                variationMode=BitmapConcrete.VARIATION_NONE;
                break;
            case 2:
                bitmapBase=new BitmapTile();
                variationMode=BitmapTile.VARIATION_NONE;
                break;
            case 3:
                bitmapBase=new BitmapMosaic();
                variationMode=BitmapMosaic.VARIATION_NONE;
                break;
            case 4:
                bitmapBase=new BitmapMetal();
                variationMode=BitmapMetal.VARIATION_HEXAGON;
                break;
            default:
                bitmapBase=new BitmapMetal();
                variationMode=BitmapMetal.VARIATION_PLATE;
                break;
        }
        
        bitmapBase.generate(variationMode,basePath,"floor");
        
        hasFloorBitmap=true;
    }
    
    public void generateCeiling()
    {
        int         variationMode;
        BitmapBase  bitmapBase;
        
        if (hasCeilingBitmap) return;
        
        switch(GeneratorMain.random.nextInt(4)) {
            case 0:
                bitmapBase=new BitmapWood();
                variationMode=BitmapWood.VARIATION_BOARDS;
                break;
            case 1:
                bitmapBase=new BitmapConcrete();
                variationMode=BitmapConcrete.VARIATION_NONE;
                break;
            case 2:
                bitmapBase=new BitmapMetal();
                variationMode=BitmapMetal.VARIATION_HEXAGON;
                break;
            default:
                bitmapBase=new BitmapMetal();
                variationMode=BitmapMetal.VARIATION_PLATE;
                break;
        }
        
        bitmapBase.generate(variationMode,basePath,"ceiling");
        
        hasCeilingBitmap=true;
    }
    
    public void generatePlatform()
    {
        int         variationMode;
        BitmapBase  bitmapBase;
        
        if (hasPlatformBitmap) return;
        
        switch (GeneratorMain.random.nextInt(4)) {
            case 0:
                bitmapBase=new BitmapBrick();
                variationMode=BitmapBrick.VARIATION_NONE;
                break;
            case 1:
                bitmapBase=new BitmapWood();
                variationMode=BitmapWood.VARIATION_BOARDS;
                break;
            case 2:
                bitmapBase=new BitmapMetal();
                variationMode=BitmapMetal.VARIATION_HEXAGON;
                break;
            default:
                bitmapBase=new BitmapMetal();
                variationMode=BitmapMetal.VARIATION_PLATE;
                break;
        }
        
        bitmapBase.generate(variationMode,basePath,"platform");

        hasPlatformBitmap=true;
    }
    
    public void generateStep()
    {
        int         variationMode;
        BitmapBase  bitmapBase;
        
        if (hasStepBitmap) return;
        
        switch (GeneratorMain.random.nextInt(3)) {
            case 0:
                bitmapBase=new BitmapBrick();
                variationMode=BitmapBrick.VARIATION_NONE;
                break;
            case 1:
                bitmapBase=new BitmapConcrete();
                variationMode=BitmapConcrete.VARIATION_NONE;
                break;
            default:
                bitmapBase=new BitmapTile();
                variationMode=BitmapTile.VARIATION_NONE;
                break;
        }
        
        bitmapBase.generate(variationMode,basePath,"step");

        hasStepBitmap=true;
    }
    
    public void generatePillar()
    {
        int         variationMode;
        BitmapBase  bitmapBase;
        
        if (hasPillarBitmap) return;
        
        switch (GeneratorMain.random.nextInt(4)) {
            case 0:
                bitmapBase=new BitmapBrick();
                variationMode=BitmapBrick.VARIATION_NONE;
                break;
            case 1:
                bitmapBase=new BitmapStone();
                variationMode=BitmapStone.VARIATION_NONE;
                break;
            case 2:
                bitmapBase=new BitmapConcrete();
                variationMode=BitmapConcrete.VARIATION_NONE;
                break;
            default:
                bitmapBase=new BitmapMetal();
                variationMode=BitmapMetal.VARIATION_PLATE;
                break;
        }
        
        bitmapBase.generate(variationMode,basePath,"pillar");

        hasPillarBitmap=true;
    }
    
    public void generateBox()
    {
        int         variationMode;
        BitmapBase  bitmapBase;
        
        if (hasBoxBitmap) return;
        
        switch (GeneratorMain.random.nextInt(2)) {
            case 0:
                bitmapBase=new BitmapWood();
                variationMode=BitmapWood.VARIATION_BOX;
                break;
            default:
                bitmapBase=new BitmapMetal();
                variationMode=BitmapMetal.VARIATION_BOX;
                break;
        }
        
        bitmapBase.generate(variationMode,basePath,"box");

        hasBoxBitmap=true;
    }
    
    public void generateComputer()
    {
        int         variationMode;
        BitmapBase  bitmapBase;
        
        if (hasComputerBitmap) return;
        
        bitmapBase=new BitmapComputer();
        bitmapBase.generate(BitmapComputer.VARIATION_COMPUTER_BANK,basePath,"computer");
        
        hasComputerBitmap=true;
    }
    
    public void generatePanel()
    {
        int         variationMode;
        BitmapBase  bitmapBase;
        
        if (hasPanelBitmap) return;
        
        bitmapBase=new BitmapComputer();
        bitmapBase.generate(BitmapComputer.VARIATION_CONTROL_PANEL,basePath,"panel");
        
        hasPanelBitmap=true;
    }
    
    public void generatePipe()
    {
        int         variationMode;
        BitmapBase  bitmapBase;
        
        if (hasPipeBitmap) return;
        
        bitmapBase=new BitmapMetal();
        bitmapBase.generate(BitmapMetal.VARIATION_PIPE,basePath,"pipe");
        
        hasPipeBitmap=true;
    }

}
