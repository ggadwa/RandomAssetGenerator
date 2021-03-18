package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.bitmaps.*;

import java.util.*;

public class BitmapGenerator
{
    private boolean     hasWallBitmap,hasFloorBitmap,hasCeilingBitmap,hasStepBitmap,
                        hasPlatformBitmap,hasPillarBitmap,hasBoxBitmap,hasComputerBitmap,
                        hasPanelBitmap,hasMonitorBitmap,hasPipeBitmap,hasLiquidBitmap,
                        hasGlassBitmap,hasAccessoryBitmap,hasBodyBitmap,hasLimbBitmap,hasHeadBitmap;
    private String      basePath;
    
    public BitmapGenerator(String basePath)
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
        hasPanelBitmap=false;
        hasMonitorBitmap=false;
        hasPipeBitmap=false;
        hasLiquidBitmap=false;
        hasGlassBitmap=false;
        hasAccessoryBitmap=false;
        hasBodyBitmap=false;
        hasLimbBitmap=false;
        hasHeadBitmap=false;
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
        
        switch(GeneratorMain.random.nextInt(7)) {
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
                bitmapBase=new BitmapGround();
                variationMode=BitmapGround.VARIATION_NONE;
                break;
            case 5:
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
        if (hasComputerBitmap) return;
        
        (new BitmapComputer()).generate(BitmapComputer.VARIATION_COMPUTER_BANK,basePath,"computer");
        
        hasComputerBitmap=true;
    }
    
    public void generatePanel()
    {
        if (hasPanelBitmap) return;
        
        (new BitmapComputer()).generate(BitmapComputer.VARIATION_CONTROL_PANEL,basePath,"panel");
        
        hasPanelBitmap=true;
    }
    
    public void generateMonitor()
    {
        if (hasMonitorBitmap) return;
        
        (new BitmapComputer()).generate(BitmapComputer.VARIATION_MONITOR,basePath,"monitor");
        
        hasMonitorBitmap=true;
    }
    
    public void generatePipe()
    {
        if (hasPipeBitmap) return;
        
        (new BitmapMetal()).generate(BitmapMetal.VARIATION_PIPE,basePath,"pipe");
        
        hasPipeBitmap=true;
    }
    
    public void generateLiquid()
    {
        if (hasLiquidBitmap) return;
        
        (new BitmapLiquid()).generate(BitmapLiquid.VARIATION_NONE,basePath,"liquid");
        
        hasLiquidBitmap=true;
    }
    
    public void generateGlass()
    {
        if (hasGlassBitmap) return;
        
        (new BitmapGlass()).generate(BitmapGlass.VARIATION_NONE,basePath,"glass");
        
        hasGlassBitmap=true;
    }
    
    public void generateAccessory()
    {
        if (hasAccessoryBitmap) return;
        
        (new BitmapMetal()).generate(BitmapMetal.VARIATION_SHEET,basePath,"accessory");
        
        hasAccessoryBitmap=true;
    }
    
    public void generateBody()
    {
        if (hasBodyBitmap) return;
        
        (new BitmapSkin()).generate(BitmapSkin.VARIATION_BODY,basePath,"body");
        
        hasBodyBitmap=true;
    }
    
    public void generateLimb()
    {
        if (hasLimbBitmap) return;
        
        (new BitmapSkin()).generate(BitmapSkin.VARIATION_LIMB,basePath,"limb");
        
        hasLimbBitmap=true;
    }
    
    public void generateHead()
    {
        if (hasHeadBitmap) return;
        
        (new BitmapSkin()).generate(BitmapSkin.VARIATION_HEAD,basePath,"head");
        
        hasHeadBitmap=true;
    }
    
        // this is a little hacky but it's a way to tell what
        // generated bitmaps have emissives
    
    public static boolean hasEmissive(String name)
    {
        ArrayList<String>       emissiveList=new ArrayList<>(Arrays.asList("computer","panel"));
        
        return(emissiveList.contains(name));
    }

}
