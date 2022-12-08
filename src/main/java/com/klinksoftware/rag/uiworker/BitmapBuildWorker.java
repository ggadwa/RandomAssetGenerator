package com.klinksoftware.rag.uiworker;

import com.klinksoftware.rag.bitmap.utility.BitmapBase;
import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.scene.Scene;
import java.util.*;
import javax.swing.*;

public class BitmapBuildWorker extends SwingWorker<Integer,Void>
{
    private AppWindow appWindow;
    private String bitmapName;

    public static BitmapBase generatedBitmap = null;

    public BitmapBuildWorker(AppWindow appWindow, String bitmapName) {
        this.appWindow = appWindow;
        this.bitmapName = bitmapName;
    }

    @Override
    protected Integer doInBackground() throws Exception {
        long seed;
        Scene scene;

        appWindow.startBuild();

        // get a random seed and generate the bitmap
        seed = Calendar.getInstance().getTimeInMillis();
        //seed = 1663272043095L;
        AppWindow.random.setSeed(seed);
        System.out.println("seed=" + seed);

        scene = new Scene();
        scene.bitmapGroup.add("bitmap", bitmapName);
        scene.makeSceneSimpleCube();
        scene.bitmapGroup.generateAll();

        generatedBitmap = scene.bitmapGroup.getBitmap("bitmap");

        // reset toolbar
        AppWindow.toolBar.reset(false);

        // and set the walk view
        AppWindow.walkView.setCameraCenterRotate(4.0f, -25.0f, 225.0f, 0.0f, -2.0f);
        AppWindow.walkView.setIncommingScene(scene);

        appWindow.walkLabel.setGeneratedTitle("Bitmap", seed);
        appWindow.switchView("walkView");

        return (0);
    }

    @Override
    protected void done() {
        appWindow.stopBuild();
    }
}
