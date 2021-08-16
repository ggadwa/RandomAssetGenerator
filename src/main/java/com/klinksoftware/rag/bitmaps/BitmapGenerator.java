package com.klinksoftware.rag.bitmaps;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.bitmaps.*;

import java.util.*;

public class BitmapGenerator
{
    private String basePath;
    public HashMap<String,BitmapBase> bitmaps;
    
    public BitmapGenerator(String basePath) {
        this.basePath=basePath;
        bitmaps=new HashMap<>();
    }

    public void generateWall() {
        int         variationMode;
        BitmapBase  bitmapBase;
        
        if (bitmaps.containsKey("wall")) return;
        
        switch (AppWindow.random.nextInt(4)) {
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
        bitmaps.put("wall", bitmapBase);
    }
    
    public void generateFloor() {
        int         variationMode;
        BitmapBase  bitmapBase;
        
        if (bitmaps.containsKey("floor")) return;
        
        switch(AppWindow.random.nextInt(7)) {
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
        bitmaps.put("floor", bitmapBase);
    }
    
    public void generateCeiling()
    {
        int         variationMode;
        BitmapBase  bitmapBase;
        
        if (bitmaps.containsKey("ceiling")) return;
        
        switch(AppWindow.random.nextInt(4)) {
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
        bitmaps.put("ceiling", bitmapBase);
    }
    
    public void generatePlatform()
    {
        int         variationMode;
        BitmapBase  bitmapBase;
        
        if (bitmaps.containsKey("platform")) return;
        
        switch (AppWindow.random.nextInt(4)) {
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
        bitmaps.put("platform", bitmapBase);
    }
    
    public void generateStep()
    {
        int         variationMode;
        BitmapBase  bitmapBase;
        
        if (bitmaps.containsKey("step")) return;
        
        switch (AppWindow.random.nextInt(3)) {
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
        bitmaps.put("step", bitmapBase);
    }
    
    public void generatePillar()
    {
        int         variationMode;
        BitmapBase  bitmapBase;
        
        if (bitmaps.containsKey("pillar")) return;
        
        switch (AppWindow.random.nextInt(4)) {
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
        bitmaps.put("pillar", bitmapBase);
    }
    
    public void generateBox()
    {
        int         variationMode;
        BitmapBase  bitmapBase;
        
        if (bitmaps.containsKey("box")) return;
        
        switch (AppWindow.random.nextInt(2)) {
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
        bitmaps.put("box", bitmapBase);
    }
    
    public void generateComputer()
    {
        BitmapBase  bitmapBase;
        
        if (bitmaps.containsKey("computer")) return;
        
        bitmapBase=new BitmapComputer();
        bitmapBase.generate(BitmapComputer.VARIATION_COMPUTER_BANK,basePath,"computer");
        bitmaps.put("computer", bitmapBase);
    }
    
    public void generatePanel()
    {
        BitmapBase  bitmapBase;
        
        if (bitmaps.containsKey("panel")) return;
        
        bitmapBase=new BitmapComputer();
        bitmapBase.generate(BitmapComputer.VARIATION_CONTROL_PANEL,basePath,"panel");
        bitmaps.put("panel", bitmapBase);
    }
    
    public void generateMonitor()
    {
        BitmapBase  bitmapBase;
        
        if (bitmaps.containsKey("monitor")) return;
        
        bitmapBase=new BitmapComputer();
        bitmapBase.generate(BitmapComputer.VARIATION_MONITOR,basePath,"monitor");
        bitmaps.put("monitor", bitmapBase);
    }
    
    public void generatePipe()
    {
        BitmapBase  bitmapBase;
        
        if (bitmaps.containsKey("pipe")) return;
        
        bitmapBase=new BitmapMetal();
        bitmapBase.generate(BitmapMetal.VARIATION_PIPE,basePath,"pipe");
        bitmaps.put("pipe", bitmapBase);
    }
    
    public void generateLiquid()
    {
        BitmapBase  bitmapBase;
        
        if (bitmaps.containsKey("liquid")) return;
        
        bitmapBase=new BitmapLiquid();
        bitmapBase.generate(BitmapLiquid.VARIATION_NONE,basePath,"liquid");
        bitmaps.put("liquid", bitmapBase);
    }
    
    public void generateGlass()
    {
        BitmapBase  bitmapBase;
        
        if (bitmaps.containsKey("glass")) return;
        
        bitmapBase=new BitmapGlass();
        bitmapBase.generate(BitmapGlass.VARIATION_NONE,basePath,"glass");
        bitmaps.put("glass", bitmapBase);
    }
    
    public void generateAccessory()
    {
        BitmapBase  bitmapBase;
        
        if (bitmaps.containsKey("accessory")) return;
        
        bitmapBase=new BitmapMetal();
        bitmapBase.generate(BitmapMetal.VARIATION_SHEET,basePath,"accessory");
        bitmaps.put("accessory", bitmapBase);
    }
    
    public void generateBody()
    {
        BitmapBase  bitmapBase;
        
        if (bitmaps.containsKey("body")) return;
        
        bitmapBase=new BitmapSkin();
        bitmapBase.generate(BitmapSkin.VARIATION_BODY,basePath,"body");
        bitmaps.put("body", bitmapBase);
    }
    
    public void generateLimb()
    {
        BitmapBase  bitmapBase;
        
        if (bitmaps.containsKey("limb")) return;
        
        bitmapBase=new BitmapSkin();
        bitmapBase.generate(BitmapSkin.VARIATION_LIMB,basePath,"limb");
        bitmaps.put("limb", bitmapBase);
    }
    
    public void generateHead()
    {
        BitmapBase  bitmapBase;
        
        if (bitmaps.containsKey("head")) return;
        
        bitmapBase=new BitmapSkin();
        bitmapBase.generate(BitmapSkin.VARIATION_HEAD,basePath,"head");
        bitmaps.put("head", bitmapBase);
    }
    
        // this is a little hacky but it's a way to tell what
        // generated bitmaps have emissives
    
    public static boolean hasEmissive(String name)
    {
        ArrayList<String>       emissiveList=new ArrayList<>(Arrays.asList("computer","panel"));
        
        return(emissiveList.contains(name));
    }

}
