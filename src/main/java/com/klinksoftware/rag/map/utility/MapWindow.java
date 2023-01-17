package com.klinksoftware.rag.map.utility;

import com.klinksoftware.rag.utility.MeshUtility;

public class MapWindow {
    public static final int WINDOW_DIR_Z = 0;
    public static final int WINDOW_DIR_X = 1;

    private float x, z;
    private float depth, width, height;
    private int direction;

    public MapWindow(float x, float z, int direction, float depth, float width, float height) {
        this.x = x;
        this.z = z;
        this.depth = depth;
        this.width = width;
        this.height = height;
        this.direction = direction;
    }

    public void build(MapRoom room, int roomNumber) {
        float sx, sz, ty, by;

        // corner position of window
        sx = (x + room.x) * MapBase.SEGMENT_SIZE;
        sz = (z + room.z) * MapBase.SEGMENT_SIZE;

        switch (room.story) {
            case MapRoom.ROOM_STORY_UPPER:
            case MapRoom.ROOM_STORY_UPPER_EXTENSION:
            case MapRoom.ROOM_STORY_TALL_EXTENSION:
                by = (MapBase.SEGMENT_SIZE + (MapBase.FLOOR_HEIGHT * 2));
                ty = by + MapBase.SEGMENT_SIZE;
                break;
            case MapRoom.ROOM_STORY_LOWER:
            case MapRoom.ROOM_STORY_LOWER_EXTENSION:
                by = -(MapBase.SEGMENT_SIZE + (MapBase.FLOOR_HEIGHT * 2));
                ty = by + MapBase.SEGMENT_SIZE;
                break;
            case MapRoom.ROOM_STORY_SUNKEN_EXTENSION:
                by = -(MapBase.SUNKEN_HEIGHT + (MapBase.FLOOR_HEIGHT + MapBase.FLOOR_HEIGHT));
                ty = by + MapBase.SUNKEN_HEIGHT;
                break;
            default:
                by = 0.0f;
                ty = by + MapBase.SEGMENT_SIZE;
                break;
        }

        // window boxes

        switch (direction) {
            case WINDOW_DIR_Z:
                room.node.addMesh(MeshUtility.createCube("platform", sx, (sx + width), by, ty, (sz - depth), (sz + depth), true, true, true, true, true, true, false, MeshUtility.UV_MAP));
                room.node.addMesh(MeshUtility.createCube("platform", ((sx + MapBase.SEGMENT_SIZE) - width), (sx + MapBase.SEGMENT_SIZE), by, ty, (sz - depth), (sz + depth), true, true, true, true, true, true, false, MeshUtility.UV_MAP));
                room.node.addMesh(MeshUtility.createCube("platform", (sx + width), ((sx + MapBase.SEGMENT_SIZE) - width), (ty - height), ty, (sz - depth), (sz + depth), true, true, true, true, true, true, false, MeshUtility.UV_MAP));
                room.node.addMesh(MeshUtility.createCube("platform", (sx + width), ((sx + MapBase.SEGMENT_SIZE) - width), by, (by + height), (sz - depth), (sz + depth), true, true, true, true, true, true, false, MeshUtility.UV_MAP));
                break;
            case WINDOW_DIR_X:
                room.node.addMesh(MeshUtility.createCube("platform", (sx - depth), (sx + depth), by, ty, sz, (sz + width), true, true, true, true, true, true, false, MeshUtility.UV_MAP));
                room.node.addMesh(MeshUtility.createCube("platform", (sx - depth), (sx + depth), by, ty, ((sz + MapBase.SEGMENT_SIZE) - width), (sz + MapBase.SEGMENT_SIZE), true, true, true, true, true, true, false, MeshUtility.UV_MAP));
                room.node.addMesh(MeshUtility.createCube("platform", (sx - depth), (sx + depth), (ty - height), ty, (sz + width), ((sz + MapBase.SEGMENT_SIZE) - width), true, true, true, true, true, true, false, MeshUtility.UV_MAP));
                room.node.addMesh(MeshUtility.createCube("platform", (sx - depth), (sx + depth), by, (by + height), (sz + width), ((sz + MapBase.SEGMENT_SIZE) - width), true, true, true, true, true, true, false, MeshUtility.UV_MAP));
                break;
        }

    }

}
