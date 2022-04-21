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
    private String bitmapName;

    public static BitmapBase generatedBitmap = null;

    public BitmapBuildWorker(AppWindow appWindow, String bitmapName) {
        this.appWindow = appWindow;
        this.bitmapName = bitmapName;
    }

    @Override
    protected Integer doInBackground() throws Exception {
        long seed;
        BitmapBase bitmap;
        HashMap<String, BitmapBase> bitmaps;

        appWindow.enableSettings(false);

            // set the seed and base path for bitmaps
            // and make directories if necessary

        seed=Calendar.getInstance().getTimeInMillis();
        AppWindow.random.setSeed(seed);

        switch (bitmapName) {
            case "Brick":
                bitmap = new BitmapBrick();
                break;
            case "Computer":
                bitmap = new BitmapComputer();
                break;
            case "Concrete":
                bitmap = new BitmapConcrete();
                break;
            case "Control Panel":
                bitmap = new BitmapControlPanel();
                break;
            case "Glass":
                bitmap = new BitmapGlass();
                break;
            case "Ground":
                bitmap = new BitmapGround();
                break;
            case "Liquid":
                bitmap = new BitmapLiquid();
                break;
            case "Metal":
                bitmap = new BitmapMetal();
                break;
            case "Monitor":
                bitmap = new BitmapMonitor();
                break;
            case "Mosaic":
                bitmap = new BitmapMosaic();
                break;
            case "Skin":
                bitmap = new BitmapSkin();
                break;
            case "Stone":
                bitmap = new BitmapStone();
                break;
            case "Tile":
                bitmap = new BitmapTile();
                break;
            case "Wood":
                bitmap = new BitmapWood();
                break;
            default:
                bitmap = new BitmapBase();
                break;
        }

        bitmap.generate();
        bitmaps = new HashMap<>();
        bitmaps.put("bitmap", bitmap);

        generatedBitmap = bitmap;

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
