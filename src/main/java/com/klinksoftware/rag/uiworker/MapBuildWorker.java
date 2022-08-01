package com.klinksoftware.rag.uiworker;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.map.*;
import java.util.*;
import javax.swing.*;

public class MapBuildWorker extends SwingWorker<Integer,Void>
{
    private AppWindow appWindow;
    private int mapType;
    private float mainFloorMapSize, upperFloorMapSize, lowerFloorMapSize, mapCompact, tallRoom, sunkenRoom;
    private boolean complex;

    public static MapBuilder generatedMap = null;

    public MapBuildWorker(AppWindow appWindow, int mapType, float mainFloorMapSize, float upperFloorMapSize, float lowerFloorMapSize, float mapCompact, float tallRoom, float sunkenRoom, boolean complex) {
        this.appWindow = appWindow;
        this.mapType = mapType;
        this.mainFloorMapSize = mainFloorMapSize;
        this.upperFloorMapSize = upperFloorMapSize;
        this.lowerFloorMapSize = lowerFloorMapSize;
        this.mapCompact = mapCompact;
        this.tallRoom = tallRoom;
        this.sunkenRoom = sunkenRoom;
        this.complex = complex;
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
        //AppWindow.random.setSeed(1659221247837L);

            // run the map builder

        try {
            mapBuilder = new MapBuilder();
            mapBuilder.build(mapType, mainFloorMapSize, upperFloorMapSize, lowerFloorMapSize, mapCompact, tallRoom, sunkenRoom, complex);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return (0);
        }

        generatedMap = mapBuilder;

        // and set the walk view
        AppWindow.walkView.setCameraWalkView(mapBuilder.viewCenterPoint.x, mapBuilder.viewCenterPoint.y, mapBuilder.viewCenterPoint.z, (MapBuilder.SEGMENT_SIZE * 0.5f));
        AppWindow.walkView.setIncommingMeshList(mapBuilder.meshList, mapBuilder.skeleton, mapBuilder.bitmaps);

        appWindow.walkLabel.setGeneratedTitle("Map", seed);
        appWindow.switchView("walkView");

        return(0);
    }

    @Override
    protected void done() {
        appWindow.enableSettings(true);
    }
}
