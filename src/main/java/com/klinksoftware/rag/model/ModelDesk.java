package com.klinksoftware.rag.model;

import com.klinksoftware.rag.model.utility.ModelInterface;
import com.klinksoftware.rag.model.utility.ModelBase;
import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.map.utility.MapBase;
import com.klinksoftware.rag.scene.Mesh;
import com.klinksoftware.rag.utility.MeshUtility;

@ModelInterface
public class ModelDesk extends ModelBase {

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
        float deskLength, deskDepth, deskTopMargin, deskTopHeight;
        float deskLegWidth, deskLegHeight, deskStandOffset;
        float deskHalfLength, deskHalfDepth;

        scene.bitmapGroup.add("desk", new String[]{"Metal", "MetalPlank", "WoodPanel"});
        scene.bitmapGroup.add("stand", new String[]{"Metal", "MetalPlank", "WoodPanel"});
        scene.bitmapGroup.add("drawer", new String[]{"Metal", "WoodPanel"});

        // sizes
        deskLength = MapBase.SEGMENT_SIZE * (0.6f + AppWindow.random.nextFloat(0.4f));
        deskHalfLength = deskLength * 0.5f;
        deskStandOffset = 0.3f + AppWindow.random.nextFloat(0.2f);
        deskDepth = deskLength * (0.3f + AppWindow.random.nextFloat(0.3f));
        deskHalfDepth = deskDepth * 0.5f;
        deskTopMargin = deskDepth * (0.05f + AppWindow.random.nextFloat(0.05f));
        deskTopHeight = MapBase.FLOOR_HEIGHT * (0.1f + AppWindow.random.nextFloat(0.2f));
        deskLegHeight = MapBase.SEGMENT_SIZE * (0.2f + AppWindow.random.nextFloat(0.2f));
        deskLegWidth = deskDepth * (0.05f + AppWindow.random.nextFloat(0.05f));

        // the desk
        deskMesh = MeshUtility.createCube("desk", -deskHalfLength, deskHalfLength, deskLegHeight, (deskLegHeight + deskTopHeight), -deskHalfDepth, deskHalfDepth, true, true, true, true, true, true, false, MeshUtility.UV_MAP);

        drawerMesh = null;
        standMesh = null;

        if (AppWindow.random.nextBoolean()) {
            addSideStand(-(deskHalfLength - deskTopMargin), -(deskHalfLength * deskStandOffset), deskLegHeight, -(deskHalfDepth - deskTopMargin), (deskHalfDepth - deskTopMargin));
        } else {
            addSideLegs(-(deskHalfLength - deskTopMargin), deskLegWidth, deskLegHeight, -(deskHalfDepth - deskTopMargin), (deskHalfDepth - deskTopMargin));
        }
        if (AppWindow.random.nextBoolean()) {
            addSideStand((deskHalfLength * deskStandOffset), (deskHalfLength - deskTopMargin), deskLegHeight, -(deskHalfDepth - deskTopMargin), (deskHalfDepth - deskTopMargin));
        } else {
            addSideLegs((deskHalfLength - (deskTopMargin + deskLegWidth)), deskLegWidth, deskLegHeight, -(deskHalfDepth - deskTopMargin), (deskHalfDepth - deskTopMargin));
        }

        if (AppWindow.random.nextBoolean()) {
            deskMesh.combine(MeshUtility.createCube("desk", -(deskHalfLength - deskTopMargin), (deskHalfLength - deskTopMargin), 0.0f, deskLegHeight, -deskHalfDepth, -(deskHalfDepth - deskTopMargin), true, true, true, true, false, true, false, MeshUtility.UV_MAP));
        }

        scene.rootNode.addMesh(deskMesh);
        if (standMesh != null) {
            scene.rootNode.addMesh(standMesh);
        }
        if (drawerMesh != null) {
            scene.rootNode.addMesh(drawerMesh);
        }
    }
}
