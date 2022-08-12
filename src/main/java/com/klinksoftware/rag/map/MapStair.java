package com.klinksoftware.rag.map;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.mesh.*;
import com.klinksoftware.rag.utility.RagBound;
import java.util.ArrayList;

public class MapStair {

    public static final int STAIR_PLATFORM_STEP_COUNT = 10;
    public static final int STAIR_SUNKEN_STEP_COUNT = 5;

    private MeshList meshList;
    private ArrayList<MapRoom> rooms;

    public MapStair(MeshList meshList, ArrayList<MapRoom> rooms) {
        this.meshList = meshList;
        this.rooms = rooms;
    }

    // stair sides
    private void addSideWallFull(MapRoom room, String bitmapName, int dir, float x, float sy, float z) {
        float sx, ty, sz;

        sx = (x + room.x) * MapBuilder.SEGMENT_SIZE;
        ty = (sy + MapBuilder.SEGMENT_SIZE) + MapBuilder.FLOOR_HEIGHT;
        sz = (z + room.z) * MapBuilder.SEGMENT_SIZE;

        switch (dir) {
            case MeshMapUtility.STAIR_DIR_POS_Z:
                meshList.add(MeshUtility.createCube(bitmapName, (sx - MapBuilder.FLOOR_HEIGHT), sx, sy, ty, sz, (sz + (MapBuilder.SEGMENT_SIZE * 2)), true, true, true, true, true, false, false, MeshUtility.UV_MAP));
                meshList.add(MeshUtility.createCube(bitmapName, (sx + MapBuilder.SEGMENT_SIZE), ((sx + MapBuilder.SEGMENT_SIZE) + MapBuilder.FLOOR_HEIGHT), sy, ty, sz, (sz + (MapBuilder.SEGMENT_SIZE * 2)), true, true, true, true, true, false, false, MeshUtility.UV_MAP));
                break;
            case MeshMapUtility.STAIR_DIR_NEG_Z:
                meshList.add(MeshUtility.createCube(bitmapName, (sx - MapBuilder.FLOOR_HEIGHT), sx, sy, ty, (sz + MapBuilder.SEGMENT_SIZE), (sz - MapBuilder.SEGMENT_SIZE), true, true, true, true, true, false, false, MeshUtility.UV_MAP));
                meshList.add(MeshUtility.createCube(bitmapName, (sx + MapBuilder.SEGMENT_SIZE), ((sx + MapBuilder.SEGMENT_SIZE) + MapBuilder.FLOOR_HEIGHT), sy, ty, (sz + MapBuilder.SEGMENT_SIZE), (sz - MapBuilder.SEGMENT_SIZE), true, true, true, true, true, false, false, MeshUtility.UV_MAP));
                break;
            case MeshMapUtility.STAIR_DIR_POS_X:
                meshList.add(MeshUtility.createCube(bitmapName, sx, (sx + (MapBuilder.SEGMENT_SIZE * 2)), sy, ty, (sz - MapBuilder.FLOOR_HEIGHT), sz, true, true, true, true, true, false, false, MeshUtility.UV_MAP));
                meshList.add(MeshUtility.createCube(bitmapName, sx, (sx + (MapBuilder.SEGMENT_SIZE * 2)), sy, ty, (sz + MapBuilder.SEGMENT_SIZE), ((sz + MapBuilder.SEGMENT_SIZE) + MapBuilder.FLOOR_HEIGHT), true, true, true, true, true, false, false, MeshUtility.UV_MAP));
                break;
            case MeshMapUtility.STAIR_DIR_NEG_X:
                meshList.add(MeshUtility.createCube(bitmapName, (sx + MapBuilder.SEGMENT_SIZE), (sx - MapBuilder.SEGMENT_SIZE), sy, ty, (sz - MapBuilder.FLOOR_HEIGHT), sz, true, true, true, true, true, false, false, MeshUtility.UV_MAP));
                meshList.add(MeshUtility.createCube(bitmapName, (sx + MapBuilder.SEGMENT_SIZE), (sx - MapBuilder.SEGMENT_SIZE), sy, ty, (sz + MapBuilder.SEGMENT_SIZE), ((sz + MapBuilder.SEGMENT_SIZE) + MapBuilder.FLOOR_HEIGHT), true, true, true, true, true, false, false, MeshUtility.UV_MAP));
                break;
        }
    }

    private void addSideWallHalf(MapRoom room, String bitmapName, int dir, float x, float sy, float z) {
        float sx, ty, ty2, sz;

        sx = (x + room.x) * MapBuilder.SEGMENT_SIZE;
        ty = (sy + MapBuilder.SEGMENT_SIZE) + MapBuilder.FLOOR_HEIGHT;
        ty2 = sy + (MapBuilder.SEGMENT_SIZE * 0.5f);
        sz = (z + room.z) * MapBuilder.SEGMENT_SIZE;

        switch (dir) {
            case MeshMapUtility.STAIR_DIR_POS_Z:
                meshList.add(MeshUtility.createCube(bitmapName, (sx - MapBuilder.FLOOR_HEIGHT), sx, sy, ty, (sz + MapBuilder.SEGMENT_SIZE), (sz + (MapBuilder.SEGMENT_SIZE * 2)), true, true, true, true, true, false, false, MeshUtility.UV_MAP));
                meshList.add(MeshUtility.createCube(bitmapName, (sx - MapBuilder.FLOOR_HEIGHT), sx, sy, ty2, sz, (sz + MapBuilder.SEGMENT_SIZE), true, true, true, false, true, false, false, MeshUtility.UV_MAP));
                meshList.add(MeshUtility.createCube(bitmapName, (sx + MapBuilder.SEGMENT_SIZE), ((sx + MapBuilder.SEGMENT_SIZE) + MapBuilder.FLOOR_HEIGHT), sy, ty, (sz + MapBuilder.SEGMENT_SIZE), (sz + (MapBuilder.SEGMENT_SIZE * 2)), true, true, true, true, true, false, false, MeshUtility.UV_MAP));
                meshList.add(MeshUtility.createCube(bitmapName, (sx + MapBuilder.SEGMENT_SIZE), ((sx + MapBuilder.SEGMENT_SIZE) + MapBuilder.FLOOR_HEIGHT), sy, ty2, sz, (sz + MapBuilder.SEGMENT_SIZE), true, true, true, false, true, false, false, MeshUtility.UV_MAP));
                break;
            case MeshMapUtility.STAIR_DIR_NEG_Z:
                meshList.add(MeshUtility.createCube(bitmapName, (sx - MapBuilder.FLOOR_HEIGHT), sx, sy, ty, sz, (sz - MapBuilder.SEGMENT_SIZE), true, true, true, true, true, false, false, MeshUtility.UV_MAP));
                meshList.add(MeshUtility.createCube(bitmapName, (sx - MapBuilder.FLOOR_HEIGHT), sx, sy, ty2, sz, (sz + MapBuilder.SEGMENT_SIZE), true, true, false, true, true, false, false, MeshUtility.UV_MAP));
                meshList.add(MeshUtility.createCube(bitmapName, (sx + MapBuilder.SEGMENT_SIZE), ((sx + MapBuilder.SEGMENT_SIZE) + MapBuilder.FLOOR_HEIGHT), sy, ty, sz, (sz - MapBuilder.SEGMENT_SIZE), true, true, true, true, true, false, false, MeshUtility.UV_MAP));
                meshList.add(MeshUtility.createCube(bitmapName, (sx + MapBuilder.SEGMENT_SIZE), ((sx + MapBuilder.SEGMENT_SIZE) + MapBuilder.FLOOR_HEIGHT), sy, ty2, sz, (sz + MapBuilder.SEGMENT_SIZE), true, true, false, true, true, false, false, MeshUtility.UV_MAP));
                break;
            case MeshMapUtility.STAIR_DIR_POS_X:
                meshList.add(MeshUtility.createCube(bitmapName, (sx + MapBuilder.SEGMENT_SIZE), (sx + (MapBuilder.SEGMENT_SIZE * 2)), sy, ty, (sz - MapBuilder.FLOOR_HEIGHT), sz, true, true, true, true, true, false, false, MeshUtility.UV_MAP));
                meshList.add(MeshUtility.createCube(bitmapName, (sx + MapBuilder.SEGMENT_SIZE), sx, sy, ty2, (sz - MapBuilder.FLOOR_HEIGHT), sz, false, true, true, true, true, false, false, MeshUtility.UV_MAP));
                meshList.add(MeshUtility.createCube(bitmapName, (sx + MapBuilder.SEGMENT_SIZE), (sx + (MapBuilder.SEGMENT_SIZE * 2)), sy, ty, (sz + MapBuilder.SEGMENT_SIZE), ((sz + MapBuilder.SEGMENT_SIZE) + MapBuilder.FLOOR_HEIGHT), true, true, true, true, true, false, false, MeshUtility.UV_MAP));
                meshList.add(MeshUtility.createCube(bitmapName, sx, (sx + MapBuilder.SEGMENT_SIZE), sy, ty2, (sz + MapBuilder.SEGMENT_SIZE), ((sz + MapBuilder.SEGMENT_SIZE) + MapBuilder.FLOOR_HEIGHT), true, false, true, true, true, false, false, MeshUtility.UV_MAP));
                break;
            case MeshMapUtility.STAIR_DIR_NEG_X:
                meshList.add(MeshUtility.createCube(bitmapName, sx, (sx - MapBuilder.SEGMENT_SIZE), sy, ty, (sz - MapBuilder.FLOOR_HEIGHT), sz, true, true, true, true, true, false, false, MeshUtility.UV_MAP));
                meshList.add(MeshUtility.createCube(bitmapName, (sx + MapBuilder.SEGMENT_SIZE), sx, sy, ty2, (sz - MapBuilder.FLOOR_HEIGHT), sz, true, false, true, true, true, false, false, MeshUtility.UV_MAP));
                meshList.add(MeshUtility.createCube(bitmapName, sx, (sx - MapBuilder.SEGMENT_SIZE), sy, ty, (sz + MapBuilder.SEGMENT_SIZE), ((sz + MapBuilder.SEGMENT_SIZE) + MapBuilder.FLOOR_HEIGHT), true, true, true, true, true, false, false, MeshUtility.UV_MAP));
                meshList.add(MeshUtility.createCube(bitmapName, sx, (sx + MapBuilder.SEGMENT_SIZE), sy, ty2, (sz + MapBuilder.SEGMENT_SIZE), ((sz + MapBuilder.SEGMENT_SIZE) + MapBuilder.FLOOR_HEIGHT), false, true, true, true, true, false, false, MeshUtility.UV_MAP));
                break;
        }
    }

    private void addSideWallRamp(MapRoom room, String bitmapName, int dir, float x, float sy, float z) {
        float sx, ty, sz, stepHigh;

        sx = (x + room.x) * MapBuilder.SEGMENT_SIZE;
        sz = (z + room.z) * MapBuilder.SEGMENT_SIZE;

        stepHigh = (MapBuilder.SEGMENT_SIZE + MapBuilder.FLOOR_HEIGHT) / STAIR_PLATFORM_STEP_COUNT;
        ty = sy + stepHigh;

        switch (dir) {
            case MeshMapUtility.STAIR_DIR_POS_Z:
                meshList.add(MeshUtility.createRamp(bitmapName, (sx - MapBuilder.FLOOR_HEIGHT), ty, sz, MeshUtility.RAMP_DIR_POS_Z, MapBuilder.FLOOR_HEIGHT, (MapBuilder.SEGMENT_SIZE + MapBuilder.FLOOR_HEIGHT), (MapBuilder.SEGMENT_SIZE * 2), true));
                meshList.add(MeshUtility.createCube(bitmapName, (sx - MapBuilder.FLOOR_HEIGHT), sx, sy, ty, sz, (sz + (MapBuilder.SEGMENT_SIZE * 2)), true, false, true, true, false, false, false, MeshUtility.UV_MAP));
                meshList.add(MeshUtility.createRamp(bitmapName, (sx + MapBuilder.SEGMENT_SIZE), ty, sz, MeshUtility.RAMP_DIR_POS_Z, MapBuilder.FLOOR_HEIGHT, (MapBuilder.SEGMENT_SIZE + MapBuilder.FLOOR_HEIGHT), (MapBuilder.SEGMENT_SIZE * 2), true));
                meshList.add(MeshUtility.createCube(bitmapName, (sx + MapBuilder.SEGMENT_SIZE), ((sx + MapBuilder.SEGMENT_SIZE) + MapBuilder.FLOOR_HEIGHT), sy, ty, sz, (sz + (MapBuilder.SEGMENT_SIZE * 2)), false, true, true, true, false, false, false, MeshUtility.UV_MAP));
                break;
            case MeshMapUtility.STAIR_DIR_NEG_Z:
                meshList.add(MeshUtility.createRamp(bitmapName, (sx - MapBuilder.FLOOR_HEIGHT), ty, (sz - MapBuilder.SEGMENT_SIZE), MeshUtility.RAMP_DIR_NEG_Z, MapBuilder.FLOOR_HEIGHT, (MapBuilder.SEGMENT_SIZE + MapBuilder.FLOOR_HEIGHT), (MapBuilder.SEGMENT_SIZE * 2), true));
                meshList.add(MeshUtility.createCube(bitmapName, (sx - MapBuilder.FLOOR_HEIGHT), sx, sy, ty, (sz + MapBuilder.SEGMENT_SIZE), (sz - MapBuilder.SEGMENT_SIZE), true, false, true, true, false, false, false, MeshUtility.UV_MAP));
                meshList.add(MeshUtility.createRamp(bitmapName, (sx + MapBuilder.SEGMENT_SIZE), ty, (sz - MapBuilder.SEGMENT_SIZE), MeshUtility.RAMP_DIR_NEG_Z, MapBuilder.FLOOR_HEIGHT, (MapBuilder.SEGMENT_SIZE + MapBuilder.FLOOR_HEIGHT), (MapBuilder.SEGMENT_SIZE * 2), true));
                meshList.add(MeshUtility.createCube(bitmapName, (sx + MapBuilder.SEGMENT_SIZE), ((sx + MapBuilder.SEGMENT_SIZE) + MapBuilder.FLOOR_HEIGHT), sy, ty, (sz + MapBuilder.SEGMENT_SIZE), (sz - MapBuilder.SEGMENT_SIZE), false, true, true, true, false, false, false, MeshUtility.UV_MAP));
                break;
            case MeshMapUtility.STAIR_DIR_POS_X:
                meshList.add(MeshUtility.createRamp(bitmapName, sx, ty, (sz - MapBuilder.FLOOR_HEIGHT), MeshUtility.RAMP_DIR_POS_X, MapBuilder.FLOOR_HEIGHT, (MapBuilder.SEGMENT_SIZE + MapBuilder.FLOOR_HEIGHT), (MapBuilder.SEGMENT_SIZE * 2), true));
                meshList.add(MeshUtility.createCube(bitmapName, sx, (sx + (MapBuilder.SEGMENT_SIZE * 2)), sy, ty, (sz - MapBuilder.FLOOR_HEIGHT), sz, true, true, true, false, false, false, false, MeshUtility.UV_MAP));
                meshList.add(MeshUtility.createRamp(bitmapName, sx, ty, (sz + MapBuilder.SEGMENT_SIZE), MeshUtility.RAMP_DIR_POS_X, MapBuilder.FLOOR_HEIGHT, (MapBuilder.SEGMENT_SIZE + MapBuilder.FLOOR_HEIGHT), (MapBuilder.SEGMENT_SIZE * 2), true));
                meshList.add(MeshUtility.createCube(bitmapName, sx, (sx + (MapBuilder.SEGMENT_SIZE * 2)), sy, ty, (sz + MapBuilder.SEGMENT_SIZE), ((sz + MapBuilder.SEGMENT_SIZE) + MapBuilder.FLOOR_HEIGHT), true, true, false, true, false, false, false, MeshUtility.UV_MAP));
                break;
            case MeshMapUtility.STAIR_DIR_NEG_X:
                meshList.add(MeshUtility.createRamp(bitmapName, (sx - MapBuilder.SEGMENT_SIZE), ty, (sz - MapBuilder.FLOOR_HEIGHT), MeshUtility.RAMP_DIR_NEG_X, MapBuilder.FLOOR_HEIGHT, (MapBuilder.SEGMENT_SIZE + MapBuilder.FLOOR_HEIGHT), (MapBuilder.SEGMENT_SIZE * 2), true));
                meshList.add(MeshUtility.createCube(bitmapName, (sx + MapBuilder.SEGMENT_SIZE), (sx - MapBuilder.SEGMENT_SIZE), sy, ty, (sz - MapBuilder.FLOOR_HEIGHT), sz, true, true, true, true, true, false, false, MeshUtility.UV_MAP));
                meshList.add(MeshUtility.createRamp(bitmapName, (sx - MapBuilder.SEGMENT_SIZE), ty, (sz + MapBuilder.SEGMENT_SIZE), MeshUtility.RAMP_DIR_NEG_X, MapBuilder.FLOOR_HEIGHT, (MapBuilder.SEGMENT_SIZE + MapBuilder.FLOOR_HEIGHT), (MapBuilder.SEGMENT_SIZE * 2), true));
                meshList.add(MeshUtility.createCube(bitmapName, (sx + MapBuilder.SEGMENT_SIZE), (sx - MapBuilder.SEGMENT_SIZE), sy, ty, (sz + MapBuilder.SEGMENT_SIZE), ((sz + MapBuilder.SEGMENT_SIZE) + MapBuilder.FLOOR_HEIGHT), true, true, true, true, true, false, false, MeshUtility.UV_MAP));
                break;
        }
    }

    private void addSides(MapRoom room, boolean upper, int dir, float x, float sy, float z) {
        String bitmapName;

        bitmapName = (upper ? "wall_main" : "wall_lower");

        switch (AppWindow.random.nextInt(4)) {
            case 0:
                addSideWallFull(room, bitmapName, dir, x, sy, z);
                break;
            case 1:
                addSideWallHalf(room, bitmapName, dir, x, sy, z);
                break;
            case 2:
                addSideWallRamp(room, bitmapName, dir, x, sy, z);
                break;
        }
    }

    // stairs for platforms
    public void buildPlatformStair(MapRoom room, int roomNumber, boolean upper) {
        int x, z, x2, z2, dir;
        float y;
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
                }
                break;
            case MeshMapUtility.STAIR_DIR_NEG_Z:
                for (z2 = (z - 1); z2 <= (smallStairHole ? z : (room.piece.sizeZ - 1)); z2++) {
                    room.setPlatformGrid(x, z2);
                }
                break;
            case MeshMapUtility.STAIR_DIR_POS_X:
                for (x2 = (x + 1); x2 >= (smallStairHole ? x : 0); x2--) {
                    room.setPlatformGrid(x2, z);
                }
                break;
            case MeshMapUtility.STAIR_DIR_NEG_X:
                for (x2 = (x - 1); x2 <= (smallStairHole ? x : (room.piece.sizeX - 1)); x2++) {
                    room.setPlatformGrid(x2, z);
                }
                break;
        }

        // make the stairs
        if (upper) {
            y = 0.0f;
        } else {
            y = -(MapBuilder.SEGMENT_SIZE + (MapBuilder.FLOOR_HEIGHT * 2.0f));
        }

        name = "stair_" + Integer.toString(roomNumber);
        MeshMapUtility.buildStairs(meshList, room, name, x, y, z, dir, STAIR_PLATFORM_STEP_COUNT, 1.0f, (MapBuilder.SEGMENT_SIZE + MapBuilder.FLOOR_HEIGHT), 2.0f, true, true);

        // random sides
        addSides(room, upper, dir, x, y, z);

        // save for platform calcs later
        room.stairDir = dir;
        room.stairX = x;
        room.stairZ = z;
    }

    // stairs for sunken rooms
    public void buildSunkenStairs(MapRoom room, int roomNumber) {
        int n, roomCount;
        float y, high;
        boolean ramp;
        String name;
        RagBound touchBound;
        MapRoom checkRoom;

        roomCount = rooms.size();
        high = MapBuilder.SUNKEN_HEIGHT + (MapBuilder.FLOOR_HEIGHT * 2);
        y = -(MapBuilder.SUNKEN_HEIGHT + (MapBuilder.FLOOR_HEIGHT * 2));

        // find all the rooms connected to this one
        for (n = 0; n != roomCount; n++) {

            // skip self
            if (n == roomNumber) {
                continue;
            }

            // skip any room we are extending from
            checkRoom = rooms.get(n);
            if (checkRoom == room.extendedFromRoom) {
                continue;
            }

            // stairs or ramps
            ramp = AppWindow.random.nextBoolean();

            // build stairs to any touching room
            if (room.touchesNegativeX(checkRoom)) {
                touchBound = room.getTouchWallRange(checkRoom, false);
                if (touchBound == null) {
                    continue;
                }
                name = "sunk_stair_nx_" + Integer.toString(roomNumber);
                if (!ramp) {
                    MeshMapUtility.buildStairs(meshList, room, name, 0.0f, y, touchBound.min, MeshMapUtility.STAIR_DIR_NEG_X, STAIR_SUNKEN_STEP_COUNT, (touchBound.getSize() + 1.0f), high, 2.0f, true, false);
                } else {
                    MeshMapUtility.buildRamp(meshList, room, name, 0.0f, y, touchBound.min, MeshMapUtility.STAIR_DIR_NEG_X, (touchBound.getSize() + 1.0f), high, 1.0f);
                }
                continue;
            }
            if (room.touchesPositiveX(checkRoom)) {
                touchBound = room.getTouchWallRange(checkRoom, false);
                if (touchBound == null) {
                    continue;
                }
                name = "sunk_stair_px_" + Integer.toString(roomNumber);
                if (!ramp) {
                    MeshMapUtility.buildStairs(meshList, room, name, (room.piece.sizeX - 1.0f), y, touchBound.min, MeshMapUtility.STAIR_DIR_POS_X, STAIR_SUNKEN_STEP_COUNT, (touchBound.getSize() + 1.0f), high, 2.0f, true, false);
                } else {
                    MeshMapUtility.buildRamp(meshList, room, name, (room.piece.sizeX - 1.0f), y, touchBound.min, MeshMapUtility.STAIR_DIR_POS_X, (touchBound.getSize() + 1.0f), high, 1.0f);
                }
                continue;
            }
            if (room.touchesNegativeZ(checkRoom)) {
                touchBound = room.getTouchWallRange(checkRoom, true);
                if (touchBound == null) {
                    continue;
                }
                name = "sunk_stair_pz_" + Integer.toString(roomNumber);
                if (!ramp) {
                    MeshMapUtility.buildStairs(meshList, room, name, touchBound.min, y, 0.0f, MeshMapUtility.STAIR_DIR_NEG_Z, STAIR_SUNKEN_STEP_COUNT, (touchBound.getSize() + 1.0f), high, 2.0f, true, false);
                } else {
                    MeshMapUtility.buildRamp(meshList, room, name, touchBound.min, y, 0.0f, MeshMapUtility.STAIR_DIR_NEG_Z, (touchBound.getSize() + 1.0f), high, 1.0f);
                }
                continue;
            }
            if (room.touchesPositiveZ(checkRoom)) {
                touchBound = room.getTouchWallRange(checkRoom, true);
                if (touchBound == null) {
                    continue;
                }
                name = "sunk_stair_nz_" + Integer.toString(roomNumber);
                if (!ramp) {
                    MeshMapUtility.buildStairs(meshList, room, name, touchBound.min, y, (room.piece.sizeZ - 1.0f), MeshMapUtility.STAIR_DIR_POS_Z, STAIR_SUNKEN_STEP_COUNT, (touchBound.getSize() + 1.0f), high, 2.0f, true, false);
                } else {
                    MeshMapUtility.buildRamp(meshList, room, name, touchBound.min, y, (room.piece.sizeZ - 1.0f), MeshMapUtility.STAIR_DIR_POS_Z, (touchBound.getSize() + 1.0f), high, 1.0f);
                }
                continue;
            }

        }
    }

}
