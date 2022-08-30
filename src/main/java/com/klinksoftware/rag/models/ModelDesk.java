package com.klinksoftware.rag.models;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.map.MapBuilder;
import com.klinksoftware.rag.mesh.Mesh;
import com.klinksoftware.rag.mesh.MeshUtility;
import com.klinksoftware.rag.utility.RagPoint;

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
    public void buildInternal() {
        float deskLength, deskDepth, deskTopMargin, deskTopHeight;
        float deskLegWidth, deskLegHeight, deskStandOffset, monitorWidth;
        float deskHalfLength, deskHalfDepth;
        float y, standWid, standHalfWid, standHigh;
        RagPoint rotAngle;

        addBitmap("monitor", new String[]{"Monitor"});
        addBitmap("desk", new String[]{"Metal", "MetalPlank", "WoodPanel"});
        addBitmap("stand", new String[]{"Metal", "MetalPlank", "WoodPanel"});
        addBitmap("drawer", new String[]{"Metal", "WoodPanel"});

        // sizes
        deskLength = MapBuilder.SEGMENT_SIZE * (0.6f + AppWindow.random.nextFloat(0.4f));
        deskHalfLength = deskLength * 0.5f;
        deskStandOffset = 0.3f + AppWindow.random.nextFloat(0.2f);
        deskDepth = deskLength * (0.3f + AppWindow.random.nextFloat(0.3f));
        deskHalfDepth = deskDepth * 0.5f;
        deskTopMargin = deskDepth * (0.05f + AppWindow.random.nextFloat(0.05f));
        deskTopHeight = MapBuilder.FLOOR_HEIGHT * (0.1f + AppWindow.random.nextFloat(0.2f));
        deskLegHeight = MapBuilder.SEGMENT_SIZE * (0.2f + AppWindow.random.nextFloat(0.2f));
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

        meshList.add(deskMesh);
        if (standMesh != null) {
            meshList.add(standMesh);
        }
        if (drawerMesh != null) {
            meshList.add(drawerMesh);
        }

        // monitor

        standWid = deskDepth * 0.05f;
        standHalfWid = standWid * 0.5f;
        standHigh = deskDepth * 0.1f;
        monitorWidth = deskDepth * 0.7f;

        y = (deskLegHeight + deskTopHeight);

        rotAngle = new RagPoint(0.0f, (160.0f + AppWindow.random.nextFloat(40.0f)), 0.0f);
        meshList.add(MeshUtility.createCubeRotated("desk", -standHalfWid, standHalfWid, y, (y + standHigh), -standHalfWid, standHalfWid, rotAngle, true, true, true, true, false, false, false, MeshUtility.UV_MAP));

        // the monitor
        y += standHigh;
        meshList.add(MeshUtility.createCubeRotated("monitor", -monitorWidth, monitorWidth, y, (y + ((deskDepth * 6) / 9)), -standHalfWid, standHalfWid, rotAngle, false, false, true, false, false, false, false, MeshUtility.UV_WHOLE));
        meshList.add(MeshUtility.createCubeRotated("stand", -monitorWidth, monitorWidth, y, (y + ((deskDepth * 6) / 9)), -standHalfWid, standHalfWid, rotAngle, true, true, false, true, true, true, false, MeshUtility.UV_MAP));

        // now build a fake skeleton for the glTF
        skeleton = meshList.rebuildMapMeshesWithSkeleton();
    }
}
