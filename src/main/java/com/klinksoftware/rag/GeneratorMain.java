package com.klinksoftware.rag;

import com.klinksoftware.rag.bitmaps.*;
import com.klinksoftware.rag.map.*;
import com.klinksoftware.rag.model.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import com.fasterxml.jackson.core.type.*;
import com.fasterxml.jackson.databind.*;

public class GeneratorMain
{
    public static int                   colorScheme=BitmapBase.COLOR_SCHEME_RANDOM;
    public static Map<String,Object>    settings;
    public static Random                random;
    
        //
        // generic settings and setup for all build types
        //
    
    public static String getSettingJson(String name)
    {
        File        jsonFile;
        
        try {
            jsonFile=new File(GeneratorMain.class.getClassLoader().getResource("data/"+name+"_settings.json").getFile());
            return(new String(Files.readAllBytes(jsonFile.toPath())));
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return("");
        }
    }
    
    private static String runGenericSettings(String jsonSettingsStr)
    {
        long                seed;
        String              basePath,name;
        File                file;
        
            // get the json settings
            
        try {
            settings=(new ObjectMapper()).readValue(jsonSettingsStr,new TypeReference<Map<String,Object>>(){});
        }
        catch (Exception e)
        {
            System.out.println("unable to read settings: "+e.getMessage());
            e.printStackTrace();
            return(null);
        }
        
            // seed the random
            // if seed == 0, then seed is set randomly
            
        seed=(int)settings.get("seed");
        if (seed==0) seed=Calendar.getInstance().getTimeInMillis();
        
        random=new Random(seed);
        
        System.out.println("seed="+Long.toString(seed));

            // the color scheme
            
        colorScheme=BitmapBase.COLOR_SCHEME_RANDOM; // random.nextInt(BitmapBase.COLOR_SCHEME_COUNT);
        
            // create the model directory
        
        name=(String)settings.get("name");
        basePath="output"+File.separator+name;
        
        file=new File(basePath+File.separator+"textures");
        if (!file.exists()) file.mkdirs();
        
        return(basePath);
    }
    
        //
        // map
        //
    
    public static void runMap(String jsonSettingsStr)
    {
        String              basePath;
        MapBuilder    mapIndoorBuilder;
        
        System.out.println("starting map build");
        
        basePath=runGenericSettings(jsonSettingsStr);
        if (basePath==null) return;
        
            // run the map indoor creation
       
        try {
            mapIndoorBuilder=new MapBuilder(basePath);
            mapIndoorBuilder.build();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        System.out.println("finished map build");
    }
    
        //
        // model
        //
    
    public static void runModel(String jsonSettingsStr)
    {
        String                  basePath;
        ModelBuilder    modelHumanoidBuilder;
        
        System.out.println("starting model build");
        
        basePath=runGenericSettings(jsonSettingsStr);
        if (basePath==null) return;
        
            // run the model humanoid creation
       
        try {
            modelHumanoidBuilder=new ModelBuilder(basePath);
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
    }
    
    public static void runBitmaps(String jsonSettingsStr)
    {
        String          basePath;
        
        System.out.println("starting bitmap build");
        
        basePath=runGenericSettings(jsonSettingsStr);
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
        
        System.out.println("finished bitmap build");
    }
}
