package com.klinksoftware.rag.tool;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.model.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class ModelBuildWorker extends SwingWorker<Integer,Void>
{
    private AppWindow appWindow;
    
    public ModelBuildWorker(AppWindow appWindow) {
        this.appWindow=appWindow;
    }
    
    @Override
    protected Integer doInBackground() throws Exception {
        long seed;
        String mapName,basePath;
        File file;
        ModelBuilder modelBuilder;
        
        appWindow.enableButtons(false);
        
            // set the seed and base path for model
            // and make directories if necessary
            
        seed=Calendar.getInstance().getTimeInMillis();
        AppWindow.random.setSeed(seed);
        
        mapName="model_"+Long.toHexString(seed);
        basePath="output"+File.separator+mapName;
        
        file=new File(basePath+File.separator+"textures");  // will make root directory also
        if (!file.exists()) file.mkdirs();
        
            // run the model builder
       
        try {
            modelBuilder=new ModelBuilder(mapName);
            modelBuilder.build();
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
