package com.klinksoftware.rag.prop;

import com.klinksoftware.rag.prop.utility.PropBase;
import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.map.utility.MapBase;
import com.klinksoftware.rag.scene.Mesh;
import com.klinksoftware.rag.utility.MeshUtility;
import com.klinksoftware.rag.prop.utility.PropInterface;

@PropInterface
public class PropTable extends PropBase {

    @Override
    public float getCameraRotateX() {
        return (-15.0f);
    }

    @Override
    public float getCameraRotateY() {
        return (45.0f);
    }

    @Override
    public float getCameraDistance() {
        return (12.0f);
    }

    @Override
    public void buildMeshes() {
        float tableHeight, tableLegWidth, xTableWidth, zTableWidth;
        float tableXMin, tableXMax, tableZMin, tableZMax;
        Mesh mesh;

        scene.bitmapGroup.add("table", new String[]{"BitmapMetal", "BitmapMetalPlank", "BitmapWoodPanel"});

        tableHeight = MapBase.SEGMENT_SIZE * (0.2f + AppWindow.random.nextFloat(0.15f));
        tableLegWidth = MapBase.SEGMENT_SIZE * (0.03f + AppWindow.random.nextFloat(0.05f));
        xTableWidth = MapBase.SEGMENT_SIZE * (0.9f + AppWindow.random.nextFloat(0.7f));
        zTableWidth = MapBase.SEGMENT_SIZE * (0.9f + AppWindow.random.nextFloat(0.3f));

        // some preset bounds
        tableXMin = -(xTableWidth / 2.0f);
        tableXMax = xTableWidth / 2.0f;
        tableZMin = -(zTableWidth / 2.0f);
        tableZMax = zTableWidth / 2.0f;

        // the legs
        mesh = MeshUtility.createCube("table", tableXMin, (tableXMin + tableLegWidth), 0, tableHeight, tableZMin, (tableZMin + tableLegWidth), true, true, true, true, false, false, false, MeshUtility.UV_MAP);
        mesh.combine(MeshUtility.createCube("table", tableXMin, (tableXMin + tableLegWidth), 0, tableHeight, (tableZMax - tableLegWidth), tableZMax, true, true, true, true, false, false, false, MeshUtility.UV_MAP));
        mesh.combine(MeshUtility.createCube("table", (tableXMax - tableLegWidth), tableXMax, 0, tableHeight, tableZMin, (tableZMin + tableLegWidth), true, true, true, true, false, false, false, MeshUtility.UV_MAP));
        mesh.combine(MeshUtility.createCube("table", (tableXMax - tableLegWidth), tableXMax, 0, tableHeight, (tableZMax - tableLegWidth), tableZMax, true, true, true, true, false, false, false, MeshUtility.UV_MAP));

        // the table
        mesh.combine(MeshUtility.createCube("table", tableXMin, tableXMax, tableHeight, (tableHeight + tableLegWidth), tableZMin, tableZMax, true, true, true, true, true, true, false, MeshUtility.UV_MAP));

        scene.rootNode.addMesh(mesh);
    }
}
