package com.klinksoftware.rag.uiworker;

import com.klinksoftware.rag.map.utility.MapBase;
import com.klinksoftware.rag.AppWindow;
import java.util.*;
import javax.swing.*;

public class MapBuildWorker extends SwingWorker<Integer,Void>
{
    private AppWindow appWindow;
    private String mapName;

    public static MapBase generatedMap = null;

    public MapBuildWorker(AppWindow appWindow, String mapName) {
        this.appWindow = appWindow;
        this.mapName = mapName;
    }

    @Override
    protected Integer doInBackground() throws Exception {
        long seed;
        MapBase map;

        appWindow.startBuild();

        // set the seed for map
        seed = Calendar.getInstance().getTimeInMillis();
        //seed = 1673889609994L;
        AppWindow.random.setSeed(seed);

        // run the map builder
        try {
            map = (MapBase) (Class.forName("com.klinksoftware.rag.map." + mapName.replace(" ", ""))).getConstructor().newInstance();
            map.build();
        } catch (Exception e) {
            e.printStackTrace();
            return (0);
        }

        generatedMap = map;

        // reset toolbar
        AppWindow.toolBar.reset(false);

        // and set the walk view
        AppWindow.walkView.setCameraWalkView(map.viewCenterPoint.x, map.viewCenterPoint.y, map.viewCenterPoint.z, (MapBase.SEGMENT_SIZE * 0.5f), 30, 90, 20);
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
