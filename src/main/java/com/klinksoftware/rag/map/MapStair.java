package com.klinksoftware.rag.map;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.mesh.*;
import java.util.ArrayList;

public class MapStair {
    private MeshList meshList;
    private ArrayList<MapRoom> rooms;

    public MapStair(MeshList meshList, ArrayList<MapRoom> rooms) {
        this.meshList = meshList;
        this.rooms = rooms;
    }

    private void addSideWall(MapRoom room, int roomNumber, boolean upper, int dir, float sx, float sy, float sz) {
        float ty;

        ty = (sy + MapBuilder.SEGMENT_SIZE) + MapBuilder.FLOOR_HEIGHT;

        switch (dir) {
            case MeshMapUtility.STAIR_DIR_POS_Z:
                if (AppWindow.random.nextBoolean()) {
                    meshList.add(MeshUtility.createCube((upper ? "wall_main" : "wall_lower"), (sx - MapBuilder.FLOOR_HEIGHT), sx, sy, ty, sz, (sz + (MapBuilder.SEGMENT_SIZE * 2)), true, true, true, true, true, false, false, MeshUtility.UV_MAP));
                }
                if (AppWindow.random.nextBoolean()) {
                    meshList.add(MeshUtility.createCube((upper ? "wall_main" : "wall_lower"), (sx + MapBuilder.SEGMENT_SIZE), ((sx + MapBuilder.SEGMENT_SIZE) + MapBuilder.FLOOR_HEIGHT), sy, ty, sz, (sz + (MapBuilder.SEGMENT_SIZE * 2)), true, true, true, true, true, false, false, MeshUtility.UV_MAP));
                }
                break;
            case MeshMapUtility.STAIR_DIR_NEG_Z:
                if (AppWindow.random.nextBoolean()) {
                    meshList.add(MeshUtility.createCube((upper ? "wall_main" : "wall_lower"), (sx - MapBuilder.FLOOR_HEIGHT), sx, sy, ty, (sz + MapBuilder.SEGMENT_SIZE), (sz - MapBuilder.SEGMENT_SIZE), true, true, true, true, true, false, false, MeshUtility.UV_MAP));
                }
                if (AppWindow.random.nextBoolean()) {
                    meshList.add(MeshUtility.createCube((upper ? "wall_main" : "wall_lower"), (sx + MapBuilder.SEGMENT_SIZE), ((sx + MapBuilder.SEGMENT_SIZE) + MapBuilder.FLOOR_HEIGHT), sy, ty, (sz + MapBuilder.SEGMENT_SIZE), (sz - MapBuilder.SEGMENT_SIZE), true, true, true, true, true, false, false, MeshUtility.UV_MAP));
                }
                break;
            case MeshMapUtility.STAIR_DIR_POS_X:
                if (AppWindow.random.nextBoolean()) {
                    meshList.add(MeshUtility.createCube((upper ? "wall_main" : "wall_lower"), sx, (sx + (MapBuilder.SEGMENT_SIZE * 2)), sy, ty, (sz - MapBuilder.FLOOR_HEIGHT), sz, true, true, true, true, true, false, false, MeshUtility.UV_MAP));
                }
                if (AppWindow.random.nextBoolean()) {
                    meshList.add(MeshUtility.createCube((upper ? "wall_main" : "wall_lower"), sx, (sx + (MapBuilder.SEGMENT_SIZE * 2)), sy, ty, (sz + MapBuilder.SEGMENT_SIZE), ((sz + MapBuilder.SEGMENT_SIZE) + MapBuilder.FLOOR_HEIGHT), true, true, true, true, true, false, false, MeshUtility.UV_MAP));
                }
                break;
            case MeshMapUtility.STAIR_DIR_NEG_X:
                if (AppWindow.random.nextBoolean()) {
                    meshList.add(MeshUtility.createCube((upper ? "wall_main" : "wall_lower"), (sx + MapBuilder.SEGMENT_SIZE), (sx - MapBuilder.SEGMENT_SIZE), sy, ty, (sz - MapBuilder.FLOOR_HEIGHT), sz, true, true, true, true, true, false, false, MeshUtility.UV_MAP));
                }
                if (AppWindow.random.nextBoolean()) {
                    meshList.add(MeshUtility.createCube((upper ? "wall_main" : "wall_lower"), (sx + MapBuilder.SEGMENT_SIZE), (sx - MapBuilder.SEGMENT_SIZE), sy, ty, (sz + MapBuilder.SEGMENT_SIZE), ((sz + MapBuilder.SEGMENT_SIZE) + MapBuilder.FLOOR_HEIGHT), true, true, true, true, true, false, false, MeshUtility.UV_MAP));
                }
                break;
        }
    }

    public void build(MapRoom room, int roomNumber, boolean upper) {
        int x, z, x2, z2, dir;
        float sx, sy, sz;
        boolean smallStairHole;
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

        // mark off the grid
        smallStairHole = AppWindow.random.nextBoolean();

        switch (dir) {
            case MeshMapUtility.STAIR_DIR_POS_Z:
                for (z2 = (z + 1); z2 >= (smallStairHole ? z : 0); z2--) {
                    room.setPlatformGrid(x, z2);
                    room.setBlockedGrid(x, z2);
                }
                break;
            case MeshMapUtility.STAIR_DIR_NEG_Z:
                for (z2 = (z - 1); z2 <= (smallStairHole ? z : (room.piece.sizeZ - 1)); z2++) {
                    room.setPlatformGrid(x, z2);
                    room.setBlockedGrid(x, z2);
                }
                break;
            case MeshMapUtility.STAIR_DIR_POS_X:
                for (x2 = (x + 1); x2 >= (smallStairHole ? x : 0); x2--) {
                    room.setPlatformGrid(x2, z);
                    room.setBlockedGrid(x2, z);
                }
                break;
            case MeshMapUtility.STAIR_DIR_NEG_X:
                for (x2 = (x - 1); x2 <= (smallStairHole ? x : (room.piece.sizeX - 1)); x2++) {
                    room.setPlatformGrid(x2, z);
                    room.setBlockedGrid(x2, z);
                }
                break;
        }

        // make the stairs
        sx = (room.x + x) * MapBuilder.SEGMENT_SIZE;
        sz = (room.z + z) * MapBuilder.SEGMENT_SIZE;
        if (upper) {
            sy = 0;
        } else {
            sy = -(MapBuilder.SEGMENT_SIZE + (MapBuilder.FLOOR_HEIGHT * 2.0f));
        }

        name = "stair_" + Integer.toString(roomNumber);
        MeshMapUtility.buildStairs(meshList, room, name, sx, sy, sz, dir, 1.0f, true);

        // random sides
        addSideWall(room, roomNumber, upper, dir, sx, sy, sz);

        // save for platform calcs later
        room.stairDir = dir;
        room.stairX = x;
        room.stairZ = z;
    }
}
