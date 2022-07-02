package com.klinksoftware.rag.models;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.map.MapBuilder;
import com.klinksoftware.rag.mesh.Mesh;
import com.klinksoftware.rag.mesh.MeshUtility;
import com.klinksoftware.rag.utility.RagPoint;

@ModelInterface
public class ModelShelf extends ModelBase {

    @Override
    public float getCameraRotateX() {
        return (-15.0f);
    }

    @Override
    public float getCameraRotateY() {
        return (130.0f);
    }

    @Override
    public float getCameraDistance() {
        return (14.0f);
    }

    private Mesh addShelfBox(Mesh boxMesh, float bx, float by, float bz, float boxSize, int boxCount, float shelfLegWidth) {
        int n;
        RagPoint rotAngle;
        Mesh mesh2;

        for (n = 0; n != boxCount; n++) {
            rotAngle = new RagPoint(0.0f, (AppWindow.random.nextFloat(30.0f) - 15.0f), 0.0f);
            mesh2 = MeshUtility.createCubeRotated("box", (bx - boxSize), (bx + boxSize), (by + shelfLegWidth), ((by + shelfLegWidth) + boxSize), (bz - boxSize), (bz + boxSize), rotAngle, true, true, true, true, true, true, false, MeshUtility.UV_WHOLE);
            if (boxMesh == null) {
                boxMesh = mesh2;
            } else {
                boxMesh.combine(mesh2);
            }
            by += boxSize;
        }

        return (boxMesh);
    }

    @Override
    public void buildInternal() {
        int n, stackCount;
        float shelfLegWidth, shelfHeight, xShelfWidth, zShelfWidth;
        float y, bx, bz, shelfXMin, shelfXMax, shelfZMin, shelfZMax, boxSize;
        boolean hasBack;
        Mesh shelfMesh, boxMesh, mesh2;

        addBitmap("shelf", new String[]{"Metal", "MetalPlank", "Wood"});
        addBitmap("box", new String[]{"Storage"});

        shelfHeight = MapBuilder.SEGMENT_SIZE * (0.3f + AppWindow.random.nextFloat(0.35f));
        shelfLegWidth = MapBuilder.SEGMENT_SIZE * (0.03f + AppWindow.random.nextFloat(0.05f));
        xShelfWidth = MapBuilder.SEGMENT_SIZE * (0.9f + AppWindow.random.nextFloat(0.7f));
        zShelfWidth = MapBuilder.SEGMENT_SIZE * (0.9f + AppWindow.random.nextFloat(0.3f));

        hasBack = AppWindow.random.nextBoolean();

        // height and width
        stackCount = 2 + AppWindow.random.nextInt(3);

        // some preset bounds
        shelfXMin = -(xShelfWidth / 2.0f);
        shelfXMax = xShelfWidth / 2.0f;
        shelfZMin = -(zShelfWidth / 2.0f);
        shelfZMax = zShelfWidth / 2.0f;

        // the stacked shelves
        shelfMesh = null;
        boxMesh = null;

        y = 0.0f;

        for (n = 0; n != stackCount; n++) {

            // the table
            mesh2 = MeshUtility.createCube("shelf", shelfXMin, shelfXMax, y, (y + shelfLegWidth), shelfZMin, shelfZMax, true, true, true, true, true, true, false, MeshUtility.UV_MAP);
            if (shelfMesh == null) {
                shelfMesh = mesh2;
            } else {
                shelfMesh.combine(mesh2);
            }

            // legs
            if (n != 0) {
                mesh2 = MeshUtility.createCube("shelf", shelfXMin, (shelfXMin + shelfLegWidth), (y - shelfHeight), y, shelfZMin, (shelfZMin + shelfLegWidth), true, true, true, true, false, false, false, MeshUtility.UV_MAP);
                shelfMesh.combine(mesh2);

                mesh2 = MeshUtility.createCube("shelf", (shelfXMax - shelfLegWidth), shelfXMax, (y - shelfHeight), y, shelfZMin, (shelfZMin + shelfLegWidth), true, true, true, true, false, false, false, MeshUtility.UV_MAP);
                shelfMesh.combine(mesh2);

                if (!hasBack) {
                    mesh2 = MeshUtility.createCube("shelf", shelfXMin, (shelfXMin + shelfLegWidth), (y - shelfHeight), y, (shelfZMax - shelfLegWidth), shelfZMax, true, true, true, true, false, false, false, MeshUtility.UV_MAP);
                    shelfMesh.combine(mesh2);

                    mesh2 = MeshUtility.createCube("shelf", (shelfXMax - shelfLegWidth), shelfXMax, (y - shelfHeight), y, (shelfZMax - shelfLegWidth), shelfZMax, true, true, true, true, false, false, false, MeshUtility.UV_MAP);
                    shelfMesh.combine(mesh2);
                }
            }

            // items on shelf
            // one big item
            if (AppWindow.random.nextBoolean()) {
                boxSize = shelfHeight * (0.5f + (AppWindow.random.nextFloat(0.25f)));
                boxMesh = addShelfBox(boxMesh, 0, y, 0, boxSize, 1, shelfLegWidth);
            } // up to 4 small items
            else {
                if (AppWindow.random.nextBoolean()) {
                    boxSize = shelfHeight * (0.15f + (AppWindow.random.nextFloat(0.2f)));
                    bx = -(xShelfWidth * 0.25f);
                    bz = -(zShelfWidth * 0.25f);
                    boxMesh = addShelfBox(boxMesh, bx, y, bz, boxSize, (AppWindow.random.nextBoolean() ? 2 : 1), shelfLegWidth);
                }
                if (AppWindow.random.nextBoolean()) {
                    boxSize = shelfHeight * (0.15f + (AppWindow.random.nextFloat(0.2f)));
                    bx = -(xShelfWidth * 0.25f);
                    bz = zShelfWidth * 0.25f;
                    boxMesh = addShelfBox(boxMesh, bx, y, bz, boxSize, (AppWindow.random.nextBoolean() ? 2 : 1), shelfLegWidth);
                }
                if (AppWindow.random.nextBoolean()) {
                    boxSize = shelfHeight * (0.15f + (AppWindow.random.nextFloat(0.2f)));
                    bx = xShelfWidth * 0.25f;
                    bz = -(zShelfWidth * 0.25f);
                    boxMesh = addShelfBox(boxMesh, bx, y, bz, boxSize, (AppWindow.random.nextBoolean() ? 2 : 1), shelfLegWidth);
                }
                if (AppWindow.random.nextBoolean()) {
                    boxSize = shelfHeight * (0.15f + (AppWindow.random.nextFloat(0.2f)));
                    bx = xShelfWidth * 0.25f;
                    bz = zShelfWidth * 0.25f;
                    boxMesh = addShelfBox(boxMesh, bx, y, bz, boxSize, (AppWindow.random.nextBoolean() ? 2 : 1), shelfLegWidth);
                }
            }

            // go up one level
            y += (shelfHeight + shelfLegWidth);
        }

        // back
        if (hasBack) {
            mesh2 = MeshUtility.createCube("shelf", shelfXMin, shelfXMax, 0, (y - (shelfHeight + shelfLegWidth)), (shelfZMax - shelfLegWidth), shelfZMax, true, true, true, true, false, false, false, MeshUtility.UV_MAP);
            shelfMesh.combine(mesh2);
        }

        if (shelfMesh != null) {
            meshList.add(shelfMesh);
        }
        if (boxMesh != null) {
            meshList.add(boxMesh);
        }

        // now build a fake skeleton for the glTF
        skeleton = meshList.rebuildMapMeshesWithSkeleton();
    }
}
