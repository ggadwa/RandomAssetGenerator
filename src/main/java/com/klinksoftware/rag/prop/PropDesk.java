package com.klinksoftware.rag.prop;

import com.klinksoftware.rag.prop.utility.PropBase;
import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.map.utility.MapBase;
import com.klinksoftware.rag.scene.Mesh;
import com.klinksoftware.rag.utility.MeshUtility;
import com.klinksoftware.rag.prop.utility.PropInterface;

@PropInterface
public class PropDesk extends PropBase {

    private Mesh deskMesh, standMesh, drawerMesh;

    private void addSideStand(float minX, float maxX, float by, float ty, float minZ, float maxZ, int drawerCount, float drawerMargin) {
        int n;
        float y, high;
        Mesh mesh;

        // the stand
        mesh = MeshUtility.createCube("stand", minX, maxX, by, ty, minZ, maxZ, true, true, true, true, false, true, false, MeshUtility.UV_MAP);
        if (standMesh == null) {
            standMesh = mesh;
        } else {
            standMesh.combine(mesh);
        }

        // drawers
        high = ((ty - by) - drawerMargin) / drawerCount;

        y = by + drawerMargin;

        for (n = 0; n != drawerCount; n++) {
            mesh = MeshUtility.createCube("drawer", (minX + drawerMargin), (maxX - drawerMargin), y, (y + (high - drawerMargin)), maxZ, (maxZ + drawerMargin), true, true, false, true, false, true, false, MeshUtility.UV_MAP);
            if (drawerMesh == null) {
                drawerMesh = mesh;
            } else {
                drawerMesh.combine(mesh);
            }
            y += high;
        }
    }

    private void addSideLegs(float x, float deskLegWidth, float deskLegHeight, float minZ, float maxZ, boolean square, boolean cross1, boolean cross2, boolean cross3) {
        float y;
        Mesh mesh;

        // legs
        mesh = MeshUtility.createBar((x + (deskLegWidth / 2)), 0, (minZ + (deskLegWidth / 2)), (deskLegWidth / 2), deskLegHeight, MeshUtility.AXIS_Y, square);
        if (standMesh == null) {
            standMesh = mesh;
        } else {
            standMesh.combine(mesh);
        }

        standMesh.combine(MeshUtility.createBar((x + (deskLegWidth / 2)), 0, (maxZ - (deskLegWidth / 2)), (deskLegWidth / 2), deskLegHeight, MeshUtility.AXIS_Y, square));

        // crossbars
        if (cross1) {
            y = deskLegWidth + (deskLegWidth * 0.5f);
            standMesh.combine(MeshUtility.createBar((x + (deskLegWidth / 2)), y, (minZ + (deskLegWidth / 2)), (deskLegWidth / 2), ((maxZ - minZ) - deskLegWidth), MeshUtility.AXIS_Z, square));
        }
        if (cross2) {
            y = (deskLegHeight * 0.5f) + (deskLegWidth * 0.5f);
            standMesh.combine(MeshUtility.createBar((x + (deskLegWidth / 2)), y, (minZ + (deskLegWidth / 2)), (deskLegWidth / 2), ((maxZ - minZ) - deskLegWidth), MeshUtility.AXIS_Z, square));
        }
        if (cross3) {
            y = deskLegHeight - (deskLegWidth + (deskLegWidth * 0.5f));
            standMesh.combine(MeshUtility.createBar((x + (deskLegWidth / 2)), y, (minZ + (deskLegWidth / 2)), (deskLegWidth / 2), ((maxZ - minZ) - deskLegWidth), MeshUtility.AXIS_Z, square));
        }
    }

    @Override
    public void buildMeshes() {
        int drawerCount;
        float drawerHigh, drawerMargin;
        float deskLength, deskDepth, deskTopMargin, deskTopHeight;
        float deskLegWidth, deskLegHeight, deskStandOffset;
        float deskHalfLength, deskHalfDepth;
        boolean square, cross1, cross2, cross3;

        scene.bitmapGroup.add("desk", new String[]{"BitmapMetal", "BitmapMetalPlank", "BitmapWoodPanel"});
        scene.bitmapGroup.add("stand", new String[]{"BitmapMetal", "BitmapMetalPlank", "BitmapWoodPanel"});
        scene.bitmapGroup.add("drawer", new String[]{"BitmapMetal", "BitmapWoodPanel"});

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

        drawerCount = 2 + AppWindow.random.nextInt(2);
        drawerMargin = deskLegHeight * (0.02f + AppWindow.random.nextFloat(0.01f));
        square = AppWindow.random.nextBoolean();
        cross1 = AppWindow.random.nextBoolean();
        cross2 = AppWindow.random.nextBoolean();
        cross3 = AppWindow.random.nextBoolean();

        if (AppWindow.random.nextBoolean()) {
            addSideStand(-(deskHalfLength - deskTopMargin), -(deskHalfLength * deskStandOffset), 0, deskLegHeight, -(deskHalfDepth - deskTopMargin), (deskHalfDepth - deskTopMargin), drawerCount, drawerMargin);
        } else {
            addSideLegs(-(deskHalfLength - deskTopMargin), deskLegWidth, deskLegHeight, -(deskHalfDepth - deskTopMargin), (deskHalfDepth - deskTopMargin), square, cross1, cross2, cross3);
        }
        if (AppWindow.random.nextBoolean()) {
            addSideStand((deskHalfLength * deskStandOffset), (deskHalfLength - deskTopMargin), 0, deskLegHeight, -(deskHalfDepth - deskTopMargin), (deskHalfDepth - deskTopMargin), drawerCount, drawerMargin);
        } else {
            addSideLegs((deskHalfLength - (deskTopMargin + deskLegWidth)), deskLegWidth, deskLegHeight, -(deskHalfDepth - deskTopMargin), (deskHalfDepth - deskTopMargin), square, cross1, cross2, cross3);
        }

        if (AppWindow.random.nextBoolean()) {
            drawerHigh = deskLegHeight * (0.1f + AppWindow.random.nextFloat(0.1f));
            addSideStand(-(deskHalfLength * deskStandOffset), (deskHalfLength * deskStandOffset), (deskLegHeight - drawerHigh), deskLegHeight, -(deskHalfDepth - deskTopMargin), (deskHalfDepth - deskTopMargin), 1, drawerMargin);
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
