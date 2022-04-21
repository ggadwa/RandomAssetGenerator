package com.klinksoftware.rag.uiworker;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.map.*;
import java.util.*;
import javax.swing.*;

public class MapBuildWorker extends SwingWorker<Integer,Void>
{
    private AppWindow appWindow;

    public static MapBuilder generatedMap = null;

    public MapBuildWorker(AppWindow appWindow) {
        this.appWindow=appWindow;
    }

    @Override
    protected Integer doInBackground() throws Exception {
        long seed;
        MapBuilder mapBuilder;

        appWindow.enableSettings(false);

            // set the seed and base path for map
            // and make directories if necessary

        seed=Calendar.getInstance().getTimeInMillis();
        AppWindow.random.setSeed(seed);

            // run the map builder

        try {
            mapBuilder = new MapBuilder();
            mapBuilder.build();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return (0);
        }

        generatedMap = mapBuilder;

        // and set the walk view
        AppWindow.walkView.setCameraWalkView(mapBuilder.viewCenterPoint.x, mapBuilder.viewCenterPoint.y, mapBuilder.viewCenterPoint.z);
        AppWindow.walkView.setIncommingMeshList(mapBuilder.meshList, mapBuilder.skeleton, mapBuilder.bitmaps);

        return(0);
    }

    @Override
    protected void done() {
        appWindow.enableSettings(true);
    }
}
