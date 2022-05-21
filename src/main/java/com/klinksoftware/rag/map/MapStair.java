package com.klinksoftware.rag.map;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.mesh.*;

public class MapStair {
    private int roomNumber;
    private MeshList meshList;
    private MapRoom room;

    public MapStair(MeshList meshList, MapRoom room, int roomNumber) {
        this.meshList = meshList;
        this.room = room;
        this.roomNumber = roomNumber;
    }

    public void build(boolean upper) {
        int x, z, dir;
        float sx, sy, sz;
        String name;

        // possible directions
        x = 0;
        z = 0;
        dir = AppWindow.random.nextInt(4);

        switch (dir) {
            case MeshMapUtility.STAIR_DIR_POS_Z:
                x = 1 + AppWindow.random.nextInt(room.piece.sizeX - 3);
                z = 1 + AppWindow.random.nextInt(room.piece.sizeZ - 4);
                break;
            case MeshMapUtility.STAIR_DIR_NEG_Z:
                x = 1 + AppWindow.random.nextInt(room.piece.sizeX - 3);
                z = 2 + AppWindow.random.nextInt(room.piece.sizeZ - 4);
                break;
            case MeshMapUtility.STAIR_DIR_POS_X:
                x = 1 + AppWindow.random.nextInt(room.piece.sizeX - 4);
                z = 1 + AppWindow.random.nextInt(room.piece.sizeZ - 3);
                break;
            case MeshMapUtility.STAIR_DIR_NEG_X:
                x = 2 + AppWindow.random.nextInt(room.piece.sizeX - 4);
                z = 1 + AppWindow.random.nextInt(room.piece.sizeZ - 3);
                break;
        }

        // possition
        sx = (room.x + x) * MapBuilder.SEGMENT_SIZE;
        sz = (room.z + z) * MapBuilder.SEGMENT_SIZE;
        if (upper) {
            sy = 0;
        } else {
            sy = -(MapBuilder.SEGMENT_SIZE + (MapBuilder.FLOOR_HEIGHT * 2.0f));
        }

        dir = MeshMapUtility.STAIR_DIR_POS_Z;

        // mark off the grid
        switch (dir)
        {
            case MeshMapUtility.STAIR_DIR_POS_Z:
                room.setPlatformGrid(x, z);
                room.setPlatformGrid(x, (z + 1));
                break;
            case MeshMapUtility.STAIR_DIR_NEG_Z:
                room.setPlatformGrid(x, z);
                room.setPlatformGrid(x, (z - 1));
                break;
            case MeshMapUtility.STAIR_DIR_POS_X:
                room.setPlatformGrid(x, z);
                room.setPlatformGrid((x + 1), z);
                break;
            case MeshMapUtility.STAIR_DIR_NEG_X:
                room.setPlatformGrid(x, z);
                room.setPlatformGrid((x - 1), z);
                break;
        }

        // make the stairs
        name = "stair_" + Integer.toString(roomNumber);
        MeshMapUtility.buildStairs(meshList, room, name, sx, sy, sz, dir, 1.0f, true);
    }
}
