package com.klinksoftware.rag.map;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.bitmaps.BitmapBase;
import com.klinksoftware.rag.mesh.*;
import com.klinksoftware.rag.utility.*;
import java.util.ArrayList;
import java.util.HashMap;

public class MapStorage {

    private float shelfHeight, shelfLegWidth, xShelfMargin, zShelfMargin;
    private MapBuilder mapBuilder;
    private ArrayList<MapRoom> rooms;
    private MeshList meshList;
    private HashMap<String, BitmapBase> bitmaps;

    public MapStorage(MapBuilder mapBuilder, ArrayList<MapRoom> rooms, MeshList meshList, HashMap<String, BitmapBase> bitmaps) {
        this.mapBuilder = mapBuilder;
        this.rooms = rooms;
        this.meshList = meshList;
        this.bitmaps = bitmaps;

        shelfHeight = MapBuilder.SEGMENT_SIZE * (0.3f + AppWindow.random.nextFloat(0.35f));
        shelfLegWidth = MapBuilder.SEGMENT_SIZE * (0.03f + AppWindow.random.nextFloat(0.05f));
        xShelfMargin = MapBuilder.SEGMENT_SIZE * (0.025f + AppWindow.random.nextFloat(0.05f));
        zShelfMargin = MapBuilder.SEGMENT_SIZE * (0.025f + AppWindow.random.nextFloat(0.05f));
    }

        //
        // boxes
        //

    private void addBoxes(MapRoom room, int roomNumber, int x, float by, int z) {
        int stackLevel, stackCount;
        float dx, dy, dz, boxSize, boxSizeReduction, boxHalfSize;
        String name;
        RagPoint rotAngle;
        Mesh mesh, mesh2;

        BitmapBase.mapBitmapLoader(bitmaps, "box", new String[]{"Storage"});

            // box size

        dx = ((room.x + x) * MapBuilder.SEGMENT_SIZE) + (MapBuilder.SEGMENT_SIZE * 0.5f);
        dy = 0;
        dz = ((room.z + z) * MapBuilder.SEGMENT_SIZE) + (MapBuilder.SEGMENT_SIZE * 0.5f);

        boxSize = (MapBuilder.SEGMENT_SIZE * 0.2f) + (AppWindow.random.nextFloat() * (MapBuilder.SEGMENT_SIZE * 0.2f));
        boxSizeReduction = 0.9f + AppWindow.random.nextFloat(0.1f);

        rotAngle=new RagPoint(0.0f,0.0f,0.0f);

            // stacks of boxes

        stackCount=1+AppWindow.random.nextInt(3);

            // the stacks

        mesh = null;
        name = "storage_" + Integer.toString(roomNumber) + "_" + Integer.toString(x) + "x" + Integer.toString(z);

        for (stackLevel=0;stackLevel!=stackCount;stackLevel++) {
            boxHalfSize = boxSize * 0.5f;
            rotAngle.setFromValues(0.0f,(-10.0f+(AppWindow.random.nextFloat()*20.0f)),0.0f);
            mesh2 = MeshUtility.createCubeRotated("box", (dx - boxHalfSize), (dx + boxHalfSize), (by + dy), ((by + dy) + boxSize), (dz - boxHalfSize), (dz + boxHalfSize), rotAngle, true, true, true, true, true, (stackLevel != 0), false, MeshUtility.UV_WHOLE);

            if (mesh==null) {
                mesh=mesh2;
            }
            else {
                mesh.combine(mesh2);
            }

                // go up one level

            dy += boxSize;
            if ((dy + boxSize) > MapBuilder.SEGMENT_SIZE) {
                break;
            }

            boxSize *= boxSizeReduction;
        }

        meshList.add(mesh);
    }

    //
    // big box
    //
    private void addBigBox(MapRoom room, int roomNumber, int x, float by, int z) {
        float dx, dz, boxWidth, boxHeight;
        String name;
        RagPoint rotAngle;

        BitmapBase.mapBitmapLoader(bitmaps, "box", new String[]{"Storage"});

        dx = ((room.x + x) * MapBuilder.SEGMENT_SIZE) + (MapBuilder.SEGMENT_SIZE * 0.5f);
        dz = ((room.z + z) * MapBuilder.SEGMENT_SIZE) + (MapBuilder.SEGMENT_SIZE * 0.5f);

        boxWidth = (MapBuilder.SEGMENT_SIZE * (0.4f + AppWindow.random.nextFloat(0.4f))) * 0.5f;
        boxHeight = MapBuilder.SEGMENT_SIZE * (0.7f + AppWindow.random.nextFloat(0.3f));

        name = "storage_" + Integer.toString(roomNumber) + "_" + Integer.toString(x) + "x" + Integer.toString(z);
        rotAngle = new RagPoint(0.0f, (AppWindow.random.nextFloat(30.0f) - 15.0f), 0.0f);
        meshList.add(MeshUtility.createCubeRotated("box", (dx - boxWidth), (dx + boxWidth), by, (by + boxHeight), (dz - boxWidth), (dz + boxWidth), rotAngle, true, true, true, true, true, true, false, MeshUtility.UV_WHOLE));
    }

        //
        // shelves
        //
    private Mesh addShelfBox(MapRoom room, Mesh boxMesh, float bx, float by, float bz, float boxSize, int boxCount, String boxName) {
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

    private void addShelf(MapRoom room, int roomNumber, int x, float by, int z) {
        int stackLevel, stackCount;
        float dx, dz, bx, bz, origBy, tableXMin, tableXMax, tableZMin, tableZMax, boxSize;
        String boxName, shelfName;
        Mesh shelfMesh, boxMesh, mesh2;

        BitmapBase.mapBitmapLoader(bitmaps, "accessory", new String[]{"Metal", "Tile", "Wood"});
        BitmapBase.mapBitmapLoader(bitmaps, "box", new String[]{"Storage"});

        // height and width
        stackCount = 2 + AppWindow.random.nextInt(3);

        // some preset bounds
        dx = (room.x + x) * MapBuilder.SEGMENT_SIZE;
        dz = (room.z + z) * MapBuilder.SEGMENT_SIZE;

        origBy = by;

        tableXMin = dx + xShelfMargin;
        tableXMax = (dx + MapBuilder.SEGMENT_SIZE) - xShelfMargin;
        tableZMin = dz + zShelfMargin;
        tableZMax = (dz + MapBuilder.SEGMENT_SIZE) - zShelfMargin;

            // the stacked shelves

        shelfMesh=null;
        boxMesh = null;

        boxName = "storage_" + Integer.toString(roomNumber) + "_" + Integer.toString(x) + "x" + Integer.toString(z);
        shelfName = "shelf_" + Integer.toString(roomNumber) + "_" + Integer.toString(x) + "x" + Integer.toString(z);

        for (stackLevel=0;stackLevel!=stackCount;stackLevel++) {

            // the table
            mesh2 = MeshUtility.createCube("accessory", tableXMin, tableXMax, by, (by + shelfLegWidth), tableZMin, tableZMax, true, true, true, true, true, true, false, MeshUtility.UV_MAP);
            if (shelfMesh==null) {
                shelfMesh=mesh2;
            }
            else {
                shelfMesh.combine(mesh2);
            }

            // legs
            if (stackLevel != 0) {
                mesh2 = MeshUtility.createCube("accessory", tableXMin, (tableXMin + shelfLegWidth), (by - shelfHeight), by, tableZMin, (tableZMin + shelfLegWidth), true, true, true, true, false, false, false, MeshUtility.UV_MAP);
                shelfMesh.combine(mesh2);

                mesh2 = MeshUtility.createCube("accessory", tableXMin, (tableXMin + shelfLegWidth), (by - shelfHeight), by, (tableZMax - shelfLegWidth), tableZMax, true, true, true, true, false, false, false, MeshUtility.UV_MAP);
                shelfMesh.combine(mesh2);

                mesh2 = MeshUtility.createCube("accessory", (tableXMax - shelfLegWidth), tableXMax, (by - shelfHeight), by, tableZMin, (tableZMin + shelfLegWidth), true, true, true, true, false, false, false, MeshUtility.UV_MAP);
                shelfMesh.combine(mesh2);

                mesh2 = MeshUtility.createCube("accessory", (tableXMax - shelfLegWidth), tableXMax, (by - shelfHeight), by, (tableZMax - shelfLegWidth), tableZMax, true, true, true, true, false, false, false, MeshUtility.UV_MAP);
                shelfMesh.combine(mesh2);
            }

            // items on shelf
            // one big item
            if (AppWindow.random.nextBoolean()) {
                boxSize = shelfHeight * (0.5f + (AppWindow.random.nextFloat(0.25f)));
                bx = dx + (MapBuilder.SEGMENT_SIZE * 0.5f);
                bz = dz + (MapBuilder.SEGMENT_SIZE * 0.5f);

                boxMesh = addShelfBox(room, boxMesh, bx, by, bz, boxSize, 1, boxName);
            } // up to 4 small items
            else {
                if (AppWindow.random.nextBoolean()) {
                    boxSize = shelfHeight * (0.15f + (AppWindow.random.nextFloat(0.2f)));
                    bx = dx + (MapBuilder.SEGMENT_SIZE * 0.25f);
                    bz = dz + (MapBuilder.SEGMENT_SIZE * 0.25f);
                    boxMesh = addShelfBox(room, boxMesh, bx, by, bz, boxSize, (AppWindow.random.nextBoolean() ? 2 : 1), boxName);
                }
                if (AppWindow.random.nextBoolean()) {
                    boxSize = shelfHeight * (0.15f + (AppWindow.random.nextFloat(0.2f)));
                    bx = dx + (MapBuilder.SEGMENT_SIZE * 0.75f);
                    bz = dz + (MapBuilder.SEGMENT_SIZE * 0.25f);
                    boxMesh = addShelfBox(room, boxMesh, bx, by, bz, boxSize, (AppWindow.random.nextBoolean() ? 2 : 1), boxName);
                }
                if (AppWindow.random.nextBoolean()) {
                    boxSize = shelfHeight * (0.15f + (AppWindow.random.nextFloat(0.2f)));
                    bx = dx + (MapBuilder.SEGMENT_SIZE * 0.25f);
                    bz = dz + (MapBuilder.SEGMENT_SIZE * 0.75f);
                    boxMesh = addShelfBox(room, boxMesh, bx, by, bz, boxSize, (AppWindow.random.nextBoolean() ? 2 : 1), boxName);
                }
                if (AppWindow.random.nextBoolean()) {
                    boxSize = shelfHeight * (0.15f + (AppWindow.random.nextFloat(0.2f)));
                    bx = dx + (MapBuilder.SEGMENT_SIZE * 0.75f);
                    bz = dz + (MapBuilder.SEGMENT_SIZE * 0.75f);
                    boxMesh = addShelfBox(room, boxMesh, bx, by, bz, boxSize, (AppWindow.random.nextBoolean() ? 2 : 1), boxName);
                }
            }

            // go up one level
            by += (shelfHeight + shelfLegWidth);
            if ((by - origBy) > (MapBuilder.SEGMENT_SIZE + MapBuilder.FLOOR_HEIGHT)) {
                break;
            }
        }

        if (shelfMesh!=null) meshList.add(shelfMesh);
        if (boxMesh != null) {
            meshList.add(boxMesh);
        }
    }

        //
        // storage
        //

    public void build(MapRoom room, int roomNumber, float by, float decorations) {
        /*
        int x, z;

        for (z = 0; z != room.piece.sizeZ; z++) {
            for (x = 0; x != room.piece.sizeX; x++) {
                if (!mapBuilder.isGoodStructureDecorationPosition(rooms, room, x, z)) {
                    continue;
                }
                if (AppWindow.random.nextFloat() > decorations) {
                    continue;
                }

                switch (AppWindow.random.nextInt(3)) {
                    case 0:
                        addBoxes(room, roomNumber, x, by, z);
                        break;
                    case 1:
                        addBigBox(room, roomNumber, x, by, z);
                        break;
                    case 2:
                        addShelf(room, roomNumber, x, by, z);
                        break;
                }

                room.setBlockedGrid(x, z);
            }
        }
         */
    }

}
