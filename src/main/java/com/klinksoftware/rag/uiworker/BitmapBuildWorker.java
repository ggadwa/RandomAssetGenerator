package com.klinksoftware.rag.uiworker;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.bitmaps.*;
import com.klinksoftware.rag.mesh.MeshList;
import com.klinksoftware.rag.skeleton.Skeleton;
import java.util.*;
import javax.swing.*;

public class BitmapBuildWorker extends SwingWorker<Integer,Void>
{
    private AppWindow appWindow;
    private int textureSize;
    private String bitmapName;

    public static BitmapBase generatedBitmap = null;

    public BitmapBuildWorker(AppWindow appWindow, int textureSize, String bitmapName) {
        this.appWindow = appWindow;
        this.textureSize = textureSize;
        this.bitmapName = bitmapName;
    }

    @Override
    protected Integer doInBackground() throws Exception {
        long seed;
        BitmapBase bitmap;
        HashMap<String, BitmapBase> bitmaps;

        appWindow.enableSettings(false);

        // get a random seed and generate the bitmap
        seed=Calendar.getInstance().getTimeInMillis();
        AppWindow.random.setSeed(seed);

        try {
            bitmap = (BitmapBase) (Class.forName("com.klinksoftware.rag.bitmaps.Bitmap" + bitmapName.replace(" ", ""))).getConstructor(int.class).newInstance(textureSize);
            bitmap.generate();
        } catch (Exception e) {
            e.printStackTrace();
            return (0);
        }

        bitmaps = new HashMap<>();
        bitmaps.put("bitmap", bitmap);

        generatedBitmap = bitmap;

        MeshList meshList = new MeshList();
        meshList.makeListSimpleCube("bitmap");

        Skeleton skeleton = meshList.rebuildMapMeshesWithSkeleton();

        AppWindow.walkView.setCameraCenterRotate(4.0f, -25.0f, 225.0f, 0.0f, -2.0f);
        AppWindow.walkView.setIncommingMeshList(meshList, skeleton, bitmaps);

        appWindow.walkLabel.setGeneratedTitle("Bitmap", seed);
        appWindow.switchView("walkView");

        return (0);
    }

    @Override
    protected void done() {
        appWindow.enableSettings(true);
    }
}
