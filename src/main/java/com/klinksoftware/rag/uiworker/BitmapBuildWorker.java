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

        // get a random seed and generate the bitmap
        seed=Calendar.getInstance().getTimeInMillis();
        AppWindow.random.setSeed(seed);

        switch (bitmapName) {
            case "Brick":
                bitmap = new BitmapBrick();
                break;
            case "Clothes":
                bitmap = new BitmapClothes();
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
            case "Geometric":
                bitmap = new BitmapGeometric();
                break;
            case "Glass":
                bitmap = new BitmapGlass();
                break;
            case "Ground":
                bitmap = new BitmapGround();
                break;
            case "Head":
                bitmap = new BitmapHead();
                break;
            case "Liquid":
                bitmap = new BitmapLiquid();
                break;
            case "Metal":
                bitmap = new BitmapMetal();
                break;
            case "Metal Box":
                bitmap = new BitmapMetalBox();
                break;
            case "Monitor":
                bitmap = new BitmapMonitor();
                break;
            case "Mosaic":
                bitmap = new BitmapMosaic();
                break;
            case "Organic":
                bitmap = new BitmapOrganic();
                break;
            case "Pipe":
                bitmap = new BitmapPipe();
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
            case "Wood Box":
                bitmap = new BitmapWoodBox();
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

        AppWindow.walkView.setCameraCenterRotate(4.0f, 135.0f, 0.0f, -2.0f);
        AppWindow.walkView.setIncommingMeshList(meshList, skeleton, bitmaps);

        return (0);
    }

    @Override
    protected void done() {
        appWindow.enableSettings(true);
    }
}
