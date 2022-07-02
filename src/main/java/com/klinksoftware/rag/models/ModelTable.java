package com.klinksoftware.rag.models;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.map.MapBuilder;
import com.klinksoftware.rag.mesh.Mesh;
import com.klinksoftware.rag.mesh.MeshUtility;

@ModelInterface
public class ModelTable extends ModelBase {

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
    public void buildInternal() {
        float tableHeight, tableLegWidth, xTableWidth, zTableWidth;
        float tableXMin, tableXMax, tableZMin, tableZMax;
        Mesh mesh;

        addBitmap("table", new String[]{"Metal", "MetalPlank", "Wood"});

        tableHeight = MapBuilder.SEGMENT_SIZE * (0.2f + AppWindow.random.nextFloat(0.15f));
        tableLegWidth = MapBuilder.SEGMENT_SIZE * (0.03f + AppWindow.random.nextFloat(0.05f));
        xTableWidth = MapBuilder.SEGMENT_SIZE * (0.9f + AppWindow.random.nextFloat(0.7f));
        zTableWidth = MapBuilder.SEGMENT_SIZE * (0.9f + AppWindow.random.nextFloat(0.3f));

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

        meshList.add(mesh);

        // now build a fake skeleton for the glTF
        skeleton = meshList.rebuildMapMeshesWithSkeleton();
    }
}
