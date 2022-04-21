package com.klinksoftware.rag.uiworker;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.bitmaps.*;
import com.klinksoftware.rag.mesh.MeshList;
import com.klinksoftware.rag.skeleton.Skeleton;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class SoundBuildWorker extends SwingWorker<Integer, Void> {

    private AppWindow appWindow;

    public SoundBuildWorker(AppWindow appWindow) {
        this.appWindow = appWindow;
    }

    @Override
    protected Integer doInBackground() throws Exception {
        long seed;
        String buildName, basePath;
        File file;

        appWindow.enableSettings(false);

        // set the seed and base path for bitmaps
        // and make directories if necessary
        seed = Calendar.getInstance().getTimeInMillis();
        AppWindow.random.setSeed(seed);

        BitmapBase bitmap = new BitmapBase();
        bitmap.generate();
        HashMap<String, BitmapBase> bitmaps = new HashMap<>();
        bitmaps.put("bitmap", bitmap);

        MeshList meshList = new MeshList();
        meshList.makeListSimpleCube("bitmap");

        Skeleton skeleton = meshList.rebuildMapMeshesWithSkeleton();

        AppWindow.walkView.setCameraCenterRotate(4.0f, 0.0f);
        AppWindow.walkView.setIncommingMeshList(meshList, skeleton, bitmaps);

        return (0);
    }

    @Override
    protected void done() {
        appWindow.enableSettings(true);
    }
}
