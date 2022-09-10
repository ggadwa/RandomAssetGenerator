package com.klinksoftware.rag.models;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.map.MapBuilder;
import com.klinksoftware.rag.scene.Mesh;
import com.klinksoftware.rag.utility.MeshUtility;
import com.klinksoftware.rag.utility.RagPoint;

@ModelInterface
public class ModelPC extends ModelBase {

    private Mesh deskMesh, standMesh, drawerMesh;

    private void addSideStand(float minX, float maxX, float deskLegHeight, float minZ, float maxZ) {
        int n, drawCount;
        float y, high, margin;
        Mesh mesh;

        // the stand
        mesh = MeshUtility.createCube("stand", minX, maxX, 0, deskLegHeight, minZ, maxZ, true, true, true, true, false, true, false, MeshUtility.UV_MAP);
        if (standMesh == null) {
            standMesh = mesh;
        } else {
            standMesh.combine(mesh);
        }

        // drawers
        drawCount = 2 + AppWindow.random.nextInt(2);
        margin = deskLegHeight * 0.02f;
        high = (deskLegHeight - margin) / drawCount;

        y = margin;

        for (n = 0; n != drawCount; n++) {
            mesh = MeshUtility.createCube("drawer", (minX + margin), (maxX - margin), y, (y + (high - margin)), maxZ, (maxZ + margin), true, true, false, true, false, true, false, MeshUtility.UV_MAP);
            if (drawerMesh == null) {
                drawerMesh = mesh;
            } else {
                drawerMesh.combine(mesh);
            }
            y += high;
        }
    }

    private void addSideLegs(float x, float deskLegWidth, float deskLegHeight, float minZ, float maxZ) {
        float y;
        Mesh mesh;

        // legs
        mesh = MeshUtility.createCube("stand", x, (x + deskLegWidth), 0, deskLegHeight, minZ, (minZ + deskLegWidth), true, true, true, true, false, true, false, MeshUtility.UV_MAP);
        if (standMesh == null) {
            standMesh = mesh;
        } else {
            standMesh.combine(mesh);
        }
        standMesh.combine(MeshUtility.createCube("stand", x, (x + deskLegWidth), 0, deskLegHeight, (maxZ - deskLegWidth), maxZ, true, true, true, true, false, true, false, MeshUtility.UV_MAP));

        // crossbars
        if (AppWindow.random.nextBoolean()) {
            y = deskLegWidth + (deskLegWidth * 0.5f);
            standMesh.combine(MeshUtility.createCube("stand", x, (x + deskLegWidth), y, (y + deskLegWidth), (minZ + deskLegWidth), (maxZ - deskLegWidth), true, true, false, false, true, true, false, MeshUtility.UV_MAP));
        }
        if (AppWindow.random.nextBoolean()) {
            y = (deskLegHeight * 0.5f) + (deskLegWidth * 0.5f);
            standMesh.combine(MeshUtility.createCube("stand", x, (x + deskLegWidth), y, (y + deskLegWidth), (minZ + deskLegWidth), (maxZ - deskLegWidth), true, true, false, false, true, true, false, MeshUtility.UV_MAP));
        }
        if (AppWindow.random.nextBoolean()) {
            y = deskLegHeight - (deskLegWidth + (deskLegWidth * 0.5f));
            standMesh.combine(MeshUtility.createCube("stand", x, (x + deskLegWidth), y, (y + deskLegWidth), (minZ + deskLegWidth), (maxZ - deskLegWidth), true, true, false, false, true, true, false, MeshUtility.UV_MAP));
        }
    }

    @Override
    public void buildMeshes() {
        float y, monitorWidth;
        float standWid, standHalfWid, standHigh;
        RagPoint rotAngle;

        addBitmap("monitor", new String[]{"Monitor"});
        addBitmap("case", new String[]{"Metal", "MetalPlank"});

        // monitor
        standWid = MapBuilder.SEGMENT_SIZE * (0.051f + AppWindow.random.nextFloat(0.05f));
        standHalfWid = standWid * 0.5f;
        standHigh = MapBuilder.SEGMENT_SIZE * (0.1f + AppWindow.random.nextFloat(0.1f));
        monitorWidth = MapBuilder.SEGMENT_SIZE * (0.6f + AppWindow.random.nextFloat(0.4f));

        y = 0.0f;

        rotAngle = new RagPoint(0.0f, (160.0f + AppWindow.random.nextFloat(40.0f)), 0.0f);
        scene.rootNode.meshes.add(MeshUtility.createCubeRotated("case", -standHalfWid, standHalfWid, y, (y + standHigh), -standHalfWid, standHalfWid, rotAngle, true, true, true, true, false, false, false, MeshUtility.UV_MAP));

        // the monitor
        y += standHigh;
        scene.rootNode.meshes.add(MeshUtility.createCubeRotated("monitor", -monitorWidth, monitorWidth, y, (y + ((monitorWidth * 6) / 9)), -standHalfWid, standHalfWid, rotAngle, false, false, true, false, false, false, false, MeshUtility.UV_WHOLE));
        scene.rootNode.meshes.add(MeshUtility.createCubeRotated("case", -monitorWidth, monitorWidth, y, (y + ((monitorWidth * 6) / 9)), -standHalfWid, standHalfWid, rotAngle, true, true, false, true, true, true, false, MeshUtility.UV_MAP));
    }
}
