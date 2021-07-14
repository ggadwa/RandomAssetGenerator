package com.klinksoftware.rag;

import com.klinksoftware.rag.bitmaps.*;
import com.klinksoftware.rag.map.*;
import com.klinksoftware.rag.model.*;

import java.io.*;
import java.util.*;

public class GeneratorMain
{
    public static int                   colorScheme=BitmapBase.COLOR_SCHEME_RANDOM;
    public static String                name,basePath;
    public static Random                random;
    
        //
        // generic setup for all build types
        //
    
    private static String genericSetup(AppWindow appWindow,String namePrefix)
    {
        long seed;
        File file;
        
            // seed the random
            
        seed=Calendar.getInstance().getTimeInMillis();
        appWindow.writeLog("seed="+Long.toString(seed));
        
        random=new Random(seed);

            // the color scheme
            
        colorScheme=BitmapBase.COLOR_SCHEME_RANDOM; // random.nextInt(BitmapBase.COLOR_SCHEME_COUNT);
        
            // create the model directory
        
        name=namePrefix+"_"+appWindow.getName();
        basePath="output"+File.separator+name;
        
        file=new File(basePath+File.separator+"textures");
        if (!file.exists()) file.mkdirs();
        
        return(basePath);
    }
    
        //
        // map
        //
    
    public static void runMap(AppWindow appWindow)
    {
        String        basePath;
        MapBuilder    mapBuilder;
        
        appWindow.writeLog("starting map build");
        
        basePath=genericSetup(appWindow,"map");
        if (basePath==null) return;
        
            // run the map builder
       
        try {
            mapBuilder=new MapBuilder();
            mapBuilder.build();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        appWindow.writeLog("finished map build");
    }
    
        //
        // model
        //
    
    public static void runModel(AppWindow appWindow)
    {
        /*
        String                  basePath;
        ModelBuilder    modelHumanoidBuilder;
        
        System.out.println("starting model build");
        
        basePath=runGenericSettings(jsonSettingsStr);
        if (basePath==null) return;
        
            // run the model humanoid creation
       
        try {
            modelHumanoidBuilder=new ModelBuilder();
            modelHumanoidBuilder.build();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        try {
            (new BitmapSkin()).generate(BitmapBrick.VARIATION_NONE,basePath,"body");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        System.out.println("finished model build");
*/
    }
    
    public static void runBitmaps(AppWindow appWindow)
    {
        String          basePath;
        
        appWindow.writeLog("starting bitmap build");
        
        basePath=genericSetup(appWindow,"bitmap");
        if (basePath==null) return;

        try {
            (new BitmapBrick()).generate(BitmapBrick.VARIATION_NONE,basePath,"brick");
            (new BitmapStone()).generate(BitmapStone.VARIATION_NONE,basePath,"stone");
            (new BitmapConcrete()).generate(BitmapConcrete.VARIATION_NONE,basePath,"concrete");
            (new BitmapTile()).generate(BitmapTile.VARIATION_NONE,basePath,"tile");
            (new BitmapMosaic()).generate(BitmapTile.VARIATION_NONE,basePath,"mosaic");
            (new BitmapWood()).generate(BitmapWood.VARIATION_BOARDS,basePath,"wood");
            (new BitmapWood()).generate(BitmapWood.VARIATION_BOX,basePath,"box");
            (new BitmapMetal()).generate(BitmapMetal.VARIATION_PLATE,basePath,"metal_plate");
            (new BitmapMetal()).generate(BitmapMetal.VARIATION_BOX,basePath,"metal_box");
            (new BitmapMetal()).generate(BitmapMetal.VARIATION_PIPE,basePath,"metal_pipe");
            (new BitmapMetal()).generate(BitmapMetal.VARIATION_HEXAGON,basePath,"metal_hexagon");
            (new BitmapMetal()).generate(BitmapMetal.VARIATION_SHEET,basePath,"metal_sheet");
            (new BitmapComputer()).generate(BitmapComputer.VARIATION_COMPUTER_BANK,basePath,"computer_bank");
            (new BitmapComputer()).generate(BitmapComputer.VARIATION_CONTROL_PANEL,basePath,"computer_panel");
            (new BitmapComputer()).generate(BitmapComputer.VARIATION_MONITOR,basePath,"computer_monitor");
            (new BitmapGlass()).generate(BitmapGlass.VARIATION_NONE,basePath,"glass");
            (new BitmapGround()).generate(BitmapGround.VARIATION_NONE,basePath,"ground");
            (new BitmapLiquid()).generate(BitmapLiquid.VARIATION_NONE,basePath,"liquid");
            (new BitmapSkin()).generate(BitmapSkin.VARIATION_BODY,basePath,"body");
            (new BitmapSkin()).generate(BitmapSkin.VARIATION_LIMB,basePath,"limb");
            (new BitmapSkin()).generate(BitmapSkin.VARIATION_HEAD,basePath,"head");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        appWindow.writeLog("finished bitmap build");
    }
}
