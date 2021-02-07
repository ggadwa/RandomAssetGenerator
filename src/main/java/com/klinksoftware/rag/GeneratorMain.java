package com.klinksoftware.rag;

import com.klinksoftware.rag.bitmaps.*;
import com.klinksoftware.rag.map.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import com.fasterxml.jackson.core.type.*;
import com.fasterxml.jackson.databind.*;

public class GeneratorMain
{
    private static final String modelName="test";
    private static final int roomCount=20;
    
    public static int                   colorScheme=BitmapBase.COLOR_SCHEME_RANDOM;
    public static Map<String,Object>    settings;
    public static Random                random;
    
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
    
    public static void runMap(String jsonSettingsStr)
    {
        String              basePath,name;
        File                file;
        MapBuilder          mapBuilder;
        
        System.out.println("starting map build");
        
            // get the json settings
            
        try {
            settings=(new ObjectMapper()).readValue(jsonSettingsStr,new TypeReference<Map<String,Object>>(){});
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }
        
            // seed the random
            
        random=new Random((int)settings.get("seed"));
        
            // create the model directory
        
        name=(String)settings.get("name");
        basePath="output"+File.separator+name;
        
        file=new File(basePath+File.separator+"textures");
        if (!file.exists()) file.mkdirs();
        
            // run the map creation
            
        mapBuilder=new MapBuilder(basePath);
        mapBuilder.build();
        
        
        
/*
        (new BitmapBrick(BitmapBase.COLOR_SCHEME_RANDOM,random)).generate(BitmapBrick.VARIATION_NONE,basePath,"brick");
        (new BitmapStone(BitmapBase.COLOR_SCHEME_RANDOM,random)).generate(BitmapStone.VARIATION_NONE,basePath,"stone");
        (new BitmapConcrete(BitmapBase.COLOR_SCHEME_RANDOM,random)).generate(BitmapConcrete.VARIATION_NONE,basePath,"concrete");
        (new BitmapTile(BitmapBase.COLOR_SCHEME_RANDOM,random)).generate(BitmapTile.VARIATION_NONE,basePath,"tile");
        (new BitmapMosaic(BitmapBase.COLOR_SCHEME_RANDOM,random)).generate(BitmapTile.VARIATION_NONE,basePath,"mosaic");
        (new BitmapWood(BitmapBase.COLOR_SCHEME_RANDOM,random)).generate(BitmapWood.VARIATION_BOARDS,basePath,"wood");
        (new BitmapWood(BitmapBase.COLOR_SCHEME_RANDOM,random)).generate(BitmapWood.VARIATION_BOX,basePath,"box");
        (new BitmapMetal(BitmapBase.COLOR_SCHEME_RANDOM,random)).generate(BitmapMetal.VARIATION_PLATE,basePath,"metal_plate");
        (new BitmapMetal(BitmapBase.COLOR_SCHEME_RANDOM,random)).generate(BitmapMetal.VARIATION_BOX,basePath,"metal_box");
        (new BitmapMetal(BitmapBase.COLOR_SCHEME_RANDOM,random)).generate(BitmapMetal.VARIATION_PIPE,basePath,"metal_pipe");
        (new BitmapMetal(BitmapBase.COLOR_SCHEME_RANDOM,random)).generate(BitmapMetal.VARIATION_HEXAGON,basePath,"metal_hexagon");
        (new BitmapComputer(BitmapBase.COLOR_SCHEME_RANDOM,random)).generate(BitmapComputer.VARIATION_COMPUTER_BANK,basePath,"computer_bank");
        (new BitmapComputer(BitmapBase.COLOR_SCHEME_RANDOM,random)).generate(BitmapComputer.VARIATION_CONTROL_PANEL,basePath,"computer_panel");
*/
        
        System.out.println("finished map build");
    }
}
