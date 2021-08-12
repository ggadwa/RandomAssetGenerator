package com.klinksoftware.rag.tool;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.bitmaps.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class BitmapBuildWorker extends SwingWorker<Integer,Void>
{
    private AppWindow appWindow;
    
    public BitmapBuildWorker(AppWindow appWindow) {
        this.appWindow=appWindow;
    }
    
    @Override
    protected Integer doInBackground() throws Exception {
        long seed;
        String buildName,basePath;
        File file;
        
        appWindow.enableButtons(false);
        
            // set the seed and base path for bitmaps
            // and make directories if necessary
            
        seed=Calendar.getInstance().getTimeInMillis();
        AppWindow.random.setSeed(seed);
        
        buildName="bitmap_"+Long.toHexString(seed);
        basePath="output"+File.separator+buildName;
        
        file=new File(basePath+File.separator+"textures");  // will make root directory also
        if (!file.exists()) file.mkdirs();
        
        try {
            (new BitmapBrick()).generate(BitmapBrick.VARIATION_NONE,buildName,"brick");
            (new BitmapStone()).generate(BitmapStone.VARIATION_NONE,buildName,"stone");
            (new BitmapConcrete()).generate(BitmapConcrete.VARIATION_NONE,buildName,"concrete");
            (new BitmapTile()).generate(BitmapTile.VARIATION_NONE,buildName,"tile");
            (new BitmapMosaic()).generate(BitmapTile.VARIATION_NONE,buildName,"mosaic");
            (new BitmapWood()).generate(BitmapWood.VARIATION_BOARDS,buildName,"wood");
            (new BitmapWood()).generate(BitmapWood.VARIATION_BOX,buildName,"box");
            (new BitmapMetal()).generate(BitmapMetal.VARIATION_PLATE,buildName,"metal_plate");
            (new BitmapMetal()).generate(BitmapMetal.VARIATION_BOX,buildName,"metal_box");
            (new BitmapMetal()).generate(BitmapMetal.VARIATION_PIPE,buildName,"metal_pipe");
            (new BitmapMetal()).generate(BitmapMetal.VARIATION_HEXAGON,buildName,"metal_hexagon");
            (new BitmapMetal()).generate(BitmapMetal.VARIATION_SHEET,buildName,"metal_sheet");
            (new BitmapComputer()).generate(BitmapComputer.VARIATION_COMPUTER_BANK,buildName,"computer_bank");
            (new BitmapComputer()).generate(BitmapComputer.VARIATION_CONTROL_PANEL,buildName,"computer_panel");
            (new BitmapComputer()).generate(BitmapComputer.VARIATION_MONITOR,buildName,"computer_monitor");
            (new BitmapGlass()).generate(BitmapGlass.VARIATION_NONE,buildName,"glass");
            (new BitmapGround()).generate(BitmapGround.VARIATION_NONE,buildName,"ground");
            (new BitmapLiquid()).generate(BitmapLiquid.VARIATION_NONE,buildName,"liquid");
            (new BitmapSkin()).generate(BitmapSkin.VARIATION_BODY,buildName,"body");
            (new BitmapSkin()).generate(BitmapSkin.VARIATION_LIMB,buildName,"limb");
            (new BitmapSkin()).generate(BitmapSkin.VARIATION_HEAD,buildName,"head");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return(0);
    }
    
    @Override
    protected void done() {
        appWindow.enableButtons(true);
    }    
}
