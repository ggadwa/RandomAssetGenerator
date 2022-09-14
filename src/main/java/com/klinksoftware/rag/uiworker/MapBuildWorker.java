package com.klinksoftware.rag.uiworker;

import com.klinksoftware.rag.map.utility.MapBase;
import com.klinksoftware.rag.AppWindow;
import java.util.*;
import javax.swing.*;

public class MapBuildWorker extends SwingWorker<Integer,Void>
{
    private AppWindow appWindow;
    private int textureSize;
    private float mainFloorMapSize, upperFloorMapSize, lowerFloorMapSize, mapCompact, tallRoom, sunkenRoom;
    private boolean complex, skyBox;
    private String mapName;

    public static MapBase generatedMap = null;

    public MapBuildWorker(AppWindow appWindow, String mapName, int textureSize, float mainFloorMapSize, float upperFloorMapSize, float lowerFloorMapSize, float mapCompact, float tallRoom, float sunkenRoom, boolean complex, boolean skyBox) {
        this.appWindow = appWindow;
        this.mapName = mapName;
        this.textureSize = textureSize;
        this.mainFloorMapSize = mainFloorMapSize;
        this.upperFloorMapSize = upperFloorMapSize;
        this.lowerFloorMapSize = lowerFloorMapSize;
        this.mapCompact = mapCompact;
        this.tallRoom = tallRoom;
        this.sunkenRoom = sunkenRoom;
        this.complex = complex;
        this.skyBox = skyBox;
    }

    @Override
    protected Integer doInBackground() throws Exception {
        long seed;
        MapBase map;

        appWindow.startBuild();

        // set the seed for map
        seed = Calendar.getInstance().getTimeInMillis();
        AppWindow.random.setSeed(seed);
        //AppWindow.random.setSeed(1659457963166L);

        // run the map builder
        try {
            map = (MapBase) (Class.forName("com.klinksoftware.rag.map.Map" + mapName.replace(" ", ""))).getConstructor().newInstance();
            map.build(textureSize, mainFloorMapSize, upperFloorMapSize, lowerFloorMapSize, mapCompact, tallRoom, sunkenRoom, complex, skyBox);
        } catch (Exception e) {
            e.printStackTrace();
            return (0);
        }

        generatedMap = map;

        // and set the walk view
        AppWindow.walkView.setCameraWalkView(map.viewCenterPoint.x, map.viewCenterPoint.y, map.viewCenterPoint.z, (MapBase.SEGMENT_SIZE * 0.5f));
        AppWindow.walkView.setIncommingScene(map.scene);

        appWindow.walkLabel.setGeneratedTitle("Map", seed);
        appWindow.switchView("walkView");

        return(0);
    }

    @Override
    protected void done() {
        appWindow.stopBuild();
    }
}
