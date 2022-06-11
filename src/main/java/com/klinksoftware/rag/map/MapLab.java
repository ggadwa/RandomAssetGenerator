package com.klinksoftware.rag.map;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.bitmaps.BitmapBase;
import com.klinksoftware.rag.bitmaps.BitmapGlass;
import com.klinksoftware.rag.bitmaps.BitmapLiquid;
import com.klinksoftware.rag.bitmaps.BitmapMetal;
import com.klinksoftware.rag.mesh.*;
import com.klinksoftware.rag.utility.*;
import java.util.ArrayList;
import java.util.HashMap;

public class MapLab {
    private float tableHeight, tableLegWidth, xTableMargin, zTableMargin;
    private float tubeRadius, tubeHeight, tubeCapRadius, tubeTopCapHeight, tubeBotCapHeight;
    private float barrelRingRadius, barrelSectionRadius, barrelRingHeight, barrelSectionHeight;
    private MapBuilder mapBuilder;
    private ArrayList<MapRoom> rooms;
    private MeshList meshList;
    private HashMap<String, BitmapBase> bitmaps;

    public MapLab(MapBuilder mapBuilder, ArrayList<MapRoom> rooms, MeshList meshList, HashMap<String, BitmapBase> bitmaps) {
        this.mapBuilder = mapBuilder;
        this.rooms = rooms;
        this.meshList = meshList;
        this.bitmaps = bitmaps;

        tableHeight = MapBuilder.SEGMENT_SIZE * (0.2f + AppWindow.random.nextFloat(0.15f));
        tableLegWidth = MapBuilder.SEGMENT_SIZE * (0.03f + AppWindow.random.nextFloat(0.05f));
        xTableMargin = MapBuilder.SEGMENT_SIZE * (0.025f + AppWindow.random.nextFloat(0.05f));
        zTableMargin = MapBuilder.SEGMENT_SIZE * (0.025f + AppWindow.random.nextFloat(0.05f));

        tubeCapRadius = (MapBuilder.SEGMENT_SIZE * (0.4f + AppWindow.random.nextFloat(0.2f))) * 0.5f;
        tubeRadius = tubeCapRadius * (0.7f + AppWindow.random.nextFloat(0.2f));
        tubeHeight = (MapBuilder.SEGMENT_SIZE - MapBuilder.FLOOR_HEIGHT) * (0.7f + AppWindow.random.nextFloat(0.3f));
        tubeTopCapHeight = tubeHeight * (0.15f + AppWindow.random.nextFloat(0.2f));
        tubeBotCapHeight = tubeHeight * (0.15f + AppWindow.random.nextFloat(0.2f));

        barrelSectionRadius = (MapBuilder.SEGMENT_SIZE * (0.2f + AppWindow.random.nextFloat(0.2f))) * 0.5f;
        barrelRingRadius = barrelSectionRadius + (MapBuilder.SEGMENT_SIZE * 0.02f);
        barrelRingHeight = MapBuilder.SEGMENT_SIZE * (0.02f + AppWindow.random.nextFloat(0.02f));
        barrelSectionHeight = MapBuilder.SEGMENT_SIZE * (0.15f + AppWindow.random.nextFloat(0.1f));
    }

    //
    // tables
    //
    private void addTable(MapRoom room, int roomNumber, int x, float by, int z) {
        float dx, dz, tableXMin, tableXMax, tableZMin, tableZMax;
        String tableName;
        Mesh mesh;
        BitmapBase bitmap;

        // bitmap
        if (!bitmaps.containsKey("accessory")) {
            bitmap = new BitmapMetal();
            bitmap.generate();
            bitmaps.put("accessory", bitmap);
        }

        // some preset bounds
        dx = (room.x + x) * MapBuilder.SEGMENT_SIZE;
        dz = (room.z + z) * MapBuilder.SEGMENT_SIZE;

        tableXMin = dx + xTableMargin;
        tableXMax = (dx + MapBuilder.SEGMENT_SIZE) - xTableMargin;
        tableZMin = dz + zTableMargin;
        tableZMax = (dz + MapBuilder.SEGMENT_SIZE) - zTableMargin;

        // the stacked shelves
        tableName = "table_" + Integer.toString(roomNumber) + "_" + Integer.toString(x) + "x" + Integer.toString(z);

        mesh = MeshMapUtility.createCube(room, tableName, "accessory", tableXMin, (tableXMin + tableLegWidth), by, (by + tableHeight), tableZMin, (tableZMin + tableLegWidth), true, true, true, true, false, false, false, MeshMapUtility.UV_MAP);
        mesh.combine(MeshMapUtility.createCube(room, tableName, "accessory", tableXMin, (tableXMin + tableLegWidth), by, (by + tableHeight), (tableZMax - tableLegWidth), tableZMax, true, true, true, true, false, false, false, MeshMapUtility.UV_MAP));
        mesh.combine(MeshMapUtility.createCube(room, tableName, "accessory", (tableXMax - tableLegWidth), tableXMax, by, (by + tableHeight), tableZMin, (tableZMin + tableLegWidth), true, true, true, true, false, false, false, MeshMapUtility.UV_MAP));
        mesh.combine(MeshMapUtility.createCube(room, tableName, "accessory", (tableXMax - tableLegWidth), tableXMax, by, (by + tableHeight), (tableZMax - tableLegWidth), tableZMax, true, true, true, true, false, false, false, MeshMapUtility.UV_MAP));

        // the table
        mesh.combine(MeshMapUtility.createCube(room, tableName, "accessory", tableXMin, tableXMax, (by + tableHeight), ((by + tableHeight) + tableLegWidth), tableZMin, tableZMax, true, true, true, true, true, true, false, MeshMapUtility.UV_MAP));

        meshList.add(mesh);
    }

    //
    // tubes
    //
    public void addTube(MapRoom room, int roomNumber, int x, float by, int z) {
        float dx, dz;
        float yBotCapBy, yBotCapTy, yTopCapBy, yTopCapTy, y;
        String name;
        RagPoint centerPnt;
        Mesh mesh, mesh2;
        BitmapBase bitmap;

        // bitmap
        if (!bitmaps.containsKey("glass")) {
            bitmap = new BitmapGlass();
            bitmap.generate();
            bitmaps.put("glass", bitmap);
        }
        if (!bitmaps.containsKey("liquid")) {
            bitmap = new BitmapLiquid();
            bitmap.generate();
            bitmaps.put("liquid", bitmap);
        }
        if (!bitmaps.containsKey("accessory")) {
            bitmap = new BitmapMetal();
            bitmap.generate();
            bitmaps.put("accessory", bitmap);
        }

        name = "tube_" + Integer.toString(roomNumber) + "_" + Integer.toString(x) + "x" + Integer.toString(z);

        // tube center
        dx = ((room.x + x) * MapBuilder.SEGMENT_SIZE) + (MapBuilder.SEGMENT_SIZE * 0.5f);
        dz = ((room.z + z) * MapBuilder.SEGMENT_SIZE) + (MapBuilder.SEGMENT_SIZE * 0.5f);

        centerPnt = new RagPoint(dx, (by + (tubeHeight * 0.5f)), dz);

        // the top and bottom caps
        yBotCapBy = by;
        yBotCapTy = yBotCapBy + tubeBotCapHeight;

        yTopCapBy = yBotCapTy + (tubeHeight - (tubeBotCapHeight + tubeTopCapHeight));
        yTopCapTy = yTopCapBy + tubeTopCapHeight;

        mesh = MeshMapUtility.createMeshCylinderSimple(room, (name + "_top"), "accessory", 16, centerPnt, yBotCapTy, yBotCapBy, tubeCapRadius, true, false);
        mesh2 = MeshMapUtility.createMeshCylinderSimple(room, (name + "_top"), "accessory", 16, centerPnt, yTopCapTy, yTopCapBy, tubeCapRadius, true, true);
        mesh.combine(mesh2);
        meshList.add(mesh);

        // the tube
        meshList.add(MeshMapUtility.createMeshCylinderSimple(room, (name + "_glass"), "glass", 16, centerPnt, yTopCapBy, yBotCapTy, tubeRadius, false, false));

        // the liquid in the tube
        y = yBotCapTy + (AppWindow.random.nextFloat() * (yTopCapBy - yBotCapTy));
        meshList.add(MeshMapUtility.createMeshCylinderSimple(room, (name + "_liquid"), "liquid", 16, centerPnt, y, yBotCapTy, (tubeRadius * 0.98f), true, false));
    }

    //
    // barrels
    //
    public void addBarrel(MapRoom room, int roomNumber, int x, float by, int z) {
        float dx, dz;
        String name;
        RagPoint centerPnt;
        Mesh mesh;
        BitmapBase bitmap;

        // bitmap
        if (!bitmaps.containsKey("barrel")) {
            bitmap = new BitmapMetal();
            bitmap.generate();
            bitmaps.put("barrel", bitmap);
        }

        name = "barrel_" + Integer.toString(roomNumber) + "_" + Integer.toString(x) + "x" + Integer.toString(z);

        // tube center
        dx = ((room.x + x) * MapBuilder.SEGMENT_SIZE) + (MapBuilder.SEGMENT_SIZE * 0.5f);
        dz = ((room.z + z) * MapBuilder.SEGMENT_SIZE) + (MapBuilder.SEGMENT_SIZE * 0.5f);

        //barrelRadius,barrelRingHeight,barrelSectionHeight
        // barrel parts
        centerPnt = new RagPoint(dx, (by + (barrelRingHeight * 0.5f)), dz);
        mesh = MeshMapUtility.createMeshCylinderSimple(room, name, "barrel", 16, centerPnt, by, (by + barrelRingHeight), barrelRingRadius, false, true);

        by += barrelRingHeight;
        centerPnt = new RagPoint(dx, (by + (barrelSectionHeight * 0.5f)), dz);
        mesh.combine(MeshMapUtility.createMeshCylinderSimple(room, name, "barrel", 16, centerPnt, by, (by + barrelSectionHeight), barrelSectionRadius, false, false));

        by += barrelSectionHeight;
        centerPnt = new RagPoint(dx, (by + (barrelRingHeight * 0.5f)), dz);
        mesh.combine(MeshMapUtility.createMeshCylinderSimple(room, name, "barrel", 16, centerPnt, by, (by + barrelRingHeight), barrelRingRadius, true, true));

        by += barrelRingHeight;
        centerPnt = new RagPoint(dx, (by + (barrelSectionHeight * 0.5f)), dz);
        mesh.combine(MeshMapUtility.createMeshCylinderSimple(room, name, "barrel", 16, centerPnt, by, (by + barrelSectionHeight), barrelSectionRadius, false, false));

        by += barrelSectionHeight;
        centerPnt = new RagPoint(dx, (by + (barrelRingHeight * 0.5f)), dz);
        mesh.combine(MeshMapUtility.createMeshCylinderSimple(room, name, "barrel", 16, centerPnt, by, (by + barrelRingHeight), barrelRingRadius, true, true));

        meshList.add(mesh);
    }

    //
    // lab build
    //
    public void build(MapRoom room, int roomNumber, float by, float decorations) {
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
                        addTube(room, roomNumber, x, by, z);
                        break;
                    case 1:
                        addTable(room, roomNumber, x, by, z);
                        break;
                    case 2:
                        addBarrel(room, roomNumber, x, by, z);
                        break;
                }

                room.setBlockedGrid(x, z);
            }
        }
    }

}
