package com.klinksoftware.rag.uiworker;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.map.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class MapBuildWorker extends SwingWorker<Integer,Void>
{
    private AppWindow appWindow;

    public MapBuildWorker(AppWindow appWindow) {
        this.appWindow=appWindow;
    }

    @Override
    protected Integer doInBackground() throws Exception {
        long seed;
        String mapName,basePath;
        File file;
        MapBuilder mapBuilder;

        appWindow.enableSettings(false);

            // set the seed and base path for map
            // and make directories if necessary

        seed=Calendar.getInstance().getTimeInMillis();
        AppWindow.random.setSeed(seed);

        mapName="map_"+Long.toHexString(seed);
        basePath="output"+File.separator+mapName;

        file=new File(basePath+File.separator+"textures");  // will make root directory also
        if (!file.exists()) file.mkdirs();

            // run the map builder

        try {
            mapBuilder=new MapBuilder(mapName);
            mapBuilder.build();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return(0);
    }

    @Override
    protected void done() {
        appWindow.enableSettings(true);
    }
}
