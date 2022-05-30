package com.klinksoftware.rag.uiworker;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.map.*;
import java.util.*;
import javax.swing.*;

public class MapBuildWorker extends SwingWorker<Integer,Void>
{
    private AppWindow appWindow;
    private float mapSize, mapCompact;
    private boolean complex, upperFloor, lowerFloor, decorations;

    public static MapBuilder generatedMap = null;

    public MapBuildWorker(AppWindow appWindow, float mapSize, float mapCompact, boolean complex, boolean upperFloor, boolean lowerFloor, boolean decorations) {
        this.appWindow = appWindow;
        this.mapSize = mapSize;
        this.mapCompact = mapCompact;
        this.complex = complex;
        this.upperFloor = upperFloor;
        this.lowerFloor = lowerFloor;
        this.decorations = decorations;
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
        //AppWindow.random.setSeed(2000);

            // run the map builder

        try {
            mapBuilder = new MapBuilder();
            mapBuilder.build(mapSize, mapCompact, complex, upperFloor, lowerFloor, decorations);
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
