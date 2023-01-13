package com.klinksoftware.rag.map.utility;

import com.klinksoftware.rag.utility.MeshUtility;
import com.klinksoftware.rag.scene.Mesh;
import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.utility.RagPoint;

import java.util.*;

public class MapPlatform {
    public static final int RAIL_HALF_WALL = 0;
    public static final int RAIL_QUARTER_WALL = 1;
    public static final int RAIL_RAIL_SQUARE = 2;
    public static final int RAIL_RAIL_ROUND = 3;
    public static final int RAIL_RAIL_SQUARE_WALL = 4;
    public static final int RAIL_RAIL_ROUND_WALL = 5;

    public static final int RAIL_AROUND_COUNT = 8;

    private ArrayList<MapRoom> rooms;

    public MapPlatform(ArrayList<MapRoom> rooms) {
        this.rooms = rooms;
    }

        //
        // check for walls on platform segments
        //

    private boolean hasNegXWall(MapRoom room, int x, int z) {
        if (x == 0) {
            return (true);
        }
        return (!room.getPlatformGrid((x - 1), z));
    }

    private boolean hasPosXWall(MapRoom room, int x, int z) {
        if (x >= (room.piece.sizeX - 1)) {
            return (true);
        }
        return (!room.getPlatformGrid((x + 1), z));
    }

    private boolean hasNegZWall(MapRoom room, int x, int z) {
        if (z == 0) {
            return (true);
        }
        return (!room.getPlatformGrid(x, (z - 1)));
    }

    private boolean hasPosZWall(MapRoom room, int x, int z) {
        if (z >= (room.piece.sizeZ - 1)) {
            return (false);
        }
        return (!room.getPlatformGrid(x, (z + 1)));
    }

    // platform railings
    private void addPlatformRailingSegment(MapRoom room, int railType, float railRadius, float railOffset, int x, int z, float y) {
        float sx, ty, ty2, my, by, sz;

        by = y + MapBase.FLOOR_HEIGHT;
        ty = by + (MapBase.SEGMENT_SIZE * 0.5f);
        ty2 = (railType == RAIL_RAIL_ROUND) ? ty : (ty - (railRadius * 0.5f));
        my = by + (MapBase.SEGMENT_SIZE * 0.25f);

        sx = (float) (x + room.x) * MapBase.SEGMENT_SIZE;
        sz = (float) (z + room.z) * MapBase.SEGMENT_SIZE;

        if ((x > 0) && (!room.getPlatformGrid((x - 1), z)) && (!(((x - 2) == room.stairX) && (z == room.stairZ) && (room.stairDir == MeshMapUtility.STAIR_DIR_POS_X)))) {
            switch (railType) {
                case RAIL_HALF_WALL:
                    room.node.addMesh(MeshUtility.createCube("railing", sx, (sx + MapBase.FLOOR_HEIGHT), by, ty, sz, (sz + MapBase.SEGMENT_SIZE), true, true, true, true, true, false, false, MeshUtility.UV_MAP));
                    break;
                case RAIL_QUARTER_WALL:
                    room.node.addMesh(MeshUtility.createCube("railing", sx, (sx + MapBase.FLOOR_HEIGHT), by, my, sz, (sz + MapBase.SEGMENT_SIZE), true, true, true, true, true, false, false, MeshUtility.UV_MAP));
                    break;
                case RAIL_RAIL_SQUARE:
                    sx += railRadius;
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_Z, new RagPoint(sx, ty, sz), new RagPoint(sx, ty, (sz + MapBase.SEGMENT_SIZE)), railRadius, 4));
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_Y, new RagPoint(sx, ty2, (sz + railOffset)), new RagPoint(sx, by, (sz + railOffset)), railRadius, 4));
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_Y, new RagPoint(sx, ty2, ((sz + MapBase.SEGMENT_SIZE) - railOffset)), new RagPoint(sx, by, ((sz + MapBase.SEGMENT_SIZE) + railOffset)), railRadius, 4));
                    break;
                case RAIL_RAIL_ROUND:
                    sx += railRadius;
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_Z, new RagPoint(sx, ty, sz), new RagPoint(sx, ty, (sz + MapBase.SEGMENT_SIZE)), railRadius, RAIL_AROUND_COUNT));
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_Y, new RagPoint(sx, ty2, (sz + railOffset)), new RagPoint(sx, by, (sz + railOffset)), railRadius, RAIL_AROUND_COUNT));
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_Y, new RagPoint(sx, ty2, ((sz + MapBase.SEGMENT_SIZE) - railOffset)), new RagPoint(sx, by, ((sz + MapBase.SEGMENT_SIZE) + railOffset)), railRadius, RAIL_AROUND_COUNT));
                    break;
                case RAIL_RAIL_SQUARE_WALL:
                    room.node.addMesh(MeshUtility.createCube("railing", sx, (sx + MapBase.FLOOR_HEIGHT), by, my, sz, (sz + MapBase.SEGMENT_SIZE), true, true, true, true, true, false, false, MeshUtility.UV_MAP));
                    sx += railRadius;
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_Z, new RagPoint(sx, ty, sz), new RagPoint(sx, ty, (sz + MapBase.SEGMENT_SIZE)), railRadius, 4));
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_Y, new RagPoint(sx, ty2, (sz + railOffset)), new RagPoint(sx, by, (sz + railOffset)), railRadius, 4));
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_Y, new RagPoint(sx, ty2, ((sz + MapBase.SEGMENT_SIZE) - railOffset)), new RagPoint(sx, by, ((sz + MapBase.SEGMENT_SIZE) + railOffset)), railRadius, 4));
                    break;
                case RAIL_RAIL_ROUND_WALL:
                    room.node.addMesh(MeshUtility.createCube("railing", sx, (sx + MapBase.FLOOR_HEIGHT), by, my, sz, (sz + MapBase.SEGMENT_SIZE), true, true, true, true, true, false, false, MeshUtility.UV_MAP));
                    sx += railRadius;
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_Z, new RagPoint(sx, ty, sz), new RagPoint(sx, ty, (sz + MapBase.SEGMENT_SIZE)), railRadius, RAIL_AROUND_COUNT));
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_Y, new RagPoint(sx, ty2, (sz + railOffset)), new RagPoint(sx, by, (sz + railOffset)), railRadius, RAIL_AROUND_COUNT));
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_Y, new RagPoint(sx, ty2, ((sz + MapBase.SEGMENT_SIZE) - railOffset)), new RagPoint(sx, by, ((sz + MapBase.SEGMENT_SIZE) + railOffset)), railRadius, RAIL_AROUND_COUNT));
                    break;
            }
        }
        if ((x < (room.piece.sizeX - 1)) && (!room.getPlatformGrid((x + 1), z)) && (!(((x + 2) == room.stairX) && (z == room.stairZ) && (room.stairDir == MeshMapUtility.STAIR_DIR_NEG_X)))) {
            switch (railType) {
                case RAIL_HALF_WALL:
                    room.node.addMesh(MeshUtility.createCube("railing", ((sx + MapBase.SEGMENT_SIZE) - MapBase.FLOOR_HEIGHT), (sx + MapBase.SEGMENT_SIZE), by, ty, sz, (sz + MapBase.SEGMENT_SIZE), true, true, true, true, true, false, false, MeshUtility.UV_MAP));
                    break;
                case RAIL_QUARTER_WALL:
                    room.node.addMesh(MeshUtility.createCube("railing", ((sx + MapBase.SEGMENT_SIZE) - MapBase.FLOOR_HEIGHT), (sx + MapBase.SEGMENT_SIZE), by, my, sz, (sz + MapBase.SEGMENT_SIZE), true, true, true, true, true, false, false, MeshUtility.UV_MAP));
                    break;
                case RAIL_RAIL_SQUARE:
                    sx -= railRadius;
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_Z, new RagPoint((sx + MapBase.SEGMENT_SIZE), ty, sz), new RagPoint((sx + MapBase.SEGMENT_SIZE), ty, (sz + MapBase.SEGMENT_SIZE)), railRadius, 4));
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_Y, new RagPoint((sx + MapBase.SEGMENT_SIZE), ty2, (sz + railOffset)), new RagPoint((sx + MapBase.SEGMENT_SIZE), by, (sz + railOffset)), railRadius, 4));
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_Y, new RagPoint((sx + MapBase.SEGMENT_SIZE), ty2, ((sz + MapBase.SEGMENT_SIZE) - railOffset)), new RagPoint((sx + MapBase.SEGMENT_SIZE), by, ((sz + MapBase.SEGMENT_SIZE) + railOffset)), railRadius, 4));
                    break;
                case RAIL_RAIL_ROUND:
                    sx -= railRadius;
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_Z, new RagPoint((sx + MapBase.SEGMENT_SIZE), ty, sz), new RagPoint((sx + MapBase.SEGMENT_SIZE), ty, (sz + MapBase.SEGMENT_SIZE)), railRadius, RAIL_AROUND_COUNT));
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_Y, new RagPoint((sx + MapBase.SEGMENT_SIZE), ty2, (sz + railOffset)), new RagPoint((sx + MapBase.SEGMENT_SIZE), by, (sz + railOffset)), railRadius, RAIL_AROUND_COUNT));
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_Y, new RagPoint((sx + MapBase.SEGMENT_SIZE), ty2, ((sz + MapBase.SEGMENT_SIZE) - railOffset)), new RagPoint((sx + MapBase.SEGMENT_SIZE), by, ((sz + MapBase.SEGMENT_SIZE) + railOffset)), railRadius, RAIL_AROUND_COUNT));
                    break;
                case RAIL_RAIL_SQUARE_WALL:
                    room.node.addMesh(MeshUtility.createCube("railing", ((sx + MapBase.SEGMENT_SIZE) - MapBase.FLOOR_HEIGHT), (sx + MapBase.SEGMENT_SIZE), by, my, sz, (sz + MapBase.SEGMENT_SIZE), true, true, true, true, true, false, false, MeshUtility.UV_MAP));
                    sx -= railRadius;
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_Z, new RagPoint((sx + MapBase.SEGMENT_SIZE), ty, sz), new RagPoint((sx + MapBase.SEGMENT_SIZE), ty, (sz + MapBase.SEGMENT_SIZE)), railRadius, 4));
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_Y, new RagPoint((sx + MapBase.SEGMENT_SIZE), ty2, (sz + railOffset)), new RagPoint((sx + MapBase.SEGMENT_SIZE), by, (sz + railOffset)), railRadius, 4));
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_Y, new RagPoint((sx + MapBase.SEGMENT_SIZE), ty2, ((sz + MapBase.SEGMENT_SIZE) - railOffset)), new RagPoint((sx + MapBase.SEGMENT_SIZE), by, ((sz + MapBase.SEGMENT_SIZE) + railOffset)), railRadius, 4));
                    break;
                case RAIL_RAIL_ROUND_WALL:
                    room.node.addMesh(MeshUtility.createCube("railing", ((sx + MapBase.SEGMENT_SIZE) - MapBase.FLOOR_HEIGHT), (sx + MapBase.SEGMENT_SIZE), by, my, sz, (sz + MapBase.SEGMENT_SIZE), true, true, true, true, true, false, false, MeshUtility.UV_MAP));
                    sx -= railRadius;
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_Z, new RagPoint((sx + MapBase.SEGMENT_SIZE), ty, sz), new RagPoint((sx + MapBase.SEGMENT_SIZE), ty, (sz + MapBase.SEGMENT_SIZE)), railRadius, RAIL_AROUND_COUNT));
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_Y, new RagPoint((sx + MapBase.SEGMENT_SIZE), ty2, (sz + railOffset)), new RagPoint((sx + MapBase.SEGMENT_SIZE), by, (sz + railOffset)), railRadius, RAIL_AROUND_COUNT));
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_Y, new RagPoint((sx + MapBase.SEGMENT_SIZE), ty2, ((sz + MapBase.SEGMENT_SIZE) - railOffset)), new RagPoint((sx + MapBase.SEGMENT_SIZE), by, ((sz + MapBase.SEGMENT_SIZE) + railOffset)), railRadius, RAIL_AROUND_COUNT));
                    break;
            }
        }
        if ((z > 0) && (!room.getPlatformGrid(x, (z - 1)) && (!((x == room.stairX) && ((z - 2) == room.stairZ) && (room.stairDir == MeshMapUtility.STAIR_DIR_POS_Z))))) {
            switch (railType) {
                case RAIL_HALF_WALL:
                    room.node.addMesh(MeshUtility.createCube("railing", sx, (sx + MapBase.SEGMENT_SIZE), by, ty, sz, (sz + MapBase.FLOOR_HEIGHT), true, true, true, true, true, false, false, MeshUtility.UV_MAP));
                    break;
                case RAIL_QUARTER_WALL:
                    room.node.addMesh(MeshUtility.createCube("railing", sx, (sx + MapBase.SEGMENT_SIZE), by, my, sz, (sz + MapBase.FLOOR_HEIGHT), true, true, true, true, true, false, false, MeshUtility.UV_MAP));
                    break;
                case RAIL_RAIL_SQUARE:
                    sz += railRadius;
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_X, new RagPoint(sx, ty, sz), new RagPoint((sx + MapBase.SEGMENT_SIZE), ty, sz), railRadius, 4));
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_Y, new RagPoint((sx + railOffset), ty2, sz), new RagPoint((sx + railOffset), by, sz), railRadius, 4));
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_Y, new RagPoint(((sx + MapBase.SEGMENT_SIZE) - railOffset), ty2, sz), new RagPoint(((sz + MapBase.SEGMENT_SIZE) + railOffset), by, sz), railRadius, 4));
                    break;
                case RAIL_RAIL_ROUND:
                    sz += railRadius;
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_X, new RagPoint(sx, ty, sz), new RagPoint((sx + MapBase.SEGMENT_SIZE), ty, sz), railRadius, RAIL_AROUND_COUNT));
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_Y, new RagPoint((sx + railOffset), ty2, sz), new RagPoint((sx + railOffset), by, sz), railRadius, RAIL_AROUND_COUNT));
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_Y, new RagPoint(((sx + MapBase.SEGMENT_SIZE) - railOffset), ty2, sz), new RagPoint(((sz + MapBase.SEGMENT_SIZE) + railOffset), by, sz), railRadius, RAIL_AROUND_COUNT));
                    room.node.addMesh(MeshUtility.createCube("railing", sx, (sx + MapBase.SEGMENT_SIZE), by, my, sz, (sz + MapBase.FLOOR_HEIGHT), true, true, true, true, true, false, false, MeshUtility.UV_MAP));
                    break;
                case RAIL_RAIL_SQUARE_WALL:
                    room.node.addMesh(MeshUtility.createCube("railing", sx, (sx + MapBase.SEGMENT_SIZE), by, my, sz, (sz + MapBase.FLOOR_HEIGHT), true, true, true, true, true, false, false, MeshUtility.UV_MAP));
                    sz += railRadius;
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_X, new RagPoint(sx, ty, sz), new RagPoint((sx + MapBase.SEGMENT_SIZE), ty, sz), railRadius, 4));
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_Y, new RagPoint((sx + railOffset), ty2, sz), new RagPoint((sx + railOffset), by, sz), railRadius, 4));
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_Y, new RagPoint(((sx + MapBase.SEGMENT_SIZE) - railOffset), ty2, sz), new RagPoint(((sz + MapBase.SEGMENT_SIZE) + railOffset), by, sz), railRadius, 4));
                    break;
                case RAIL_RAIL_ROUND_WALL:
                    room.node.addMesh(MeshUtility.createCube("railing", sx, (sx + MapBase.SEGMENT_SIZE), by, my, sz, (sz + MapBase.FLOOR_HEIGHT), true, true, true, true, true, false, false, MeshUtility.UV_MAP));
                    sz += railRadius;
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_X, new RagPoint(sx, ty, sz), new RagPoint((sx + MapBase.SEGMENT_SIZE), ty, sz), railRadius, RAIL_AROUND_COUNT));
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_Y, new RagPoint((sx + railOffset), ty2, sz), new RagPoint((sx + railOffset), by, sz), railRadius, RAIL_AROUND_COUNT));
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_Y, new RagPoint(((sx + MapBase.SEGMENT_SIZE) - railOffset), ty2, sz), new RagPoint(((sz + MapBase.SEGMENT_SIZE) + railOffset), by, sz), railRadius, RAIL_AROUND_COUNT));
                    break;
            }
        }
        if ((z < (room.piece.sizeZ - 1)) && (!room.getPlatformGrid(x, (z + 1))) && (!((x == room.stairX) && ((z + 2) == room.stairZ) && (room.stairDir == MeshMapUtility.STAIR_DIR_NEG_Z)))) {
            switch (railType) {
                case RAIL_HALF_WALL:
                    room.node.addMesh(MeshUtility.createCube("railing", sx, (sx + MapBase.SEGMENT_SIZE), by, ty, ((sz + MapBase.SEGMENT_SIZE) - MapBase.FLOOR_HEIGHT), (sz + MapBase.SEGMENT_SIZE), true, true, true, true, true, false, false, MeshUtility.UV_MAP));
                    break;
                case RAIL_QUARTER_WALL:
                    room.node.addMesh(MeshUtility.createCube("railing", sx, (sx + MapBase.SEGMENT_SIZE), by, my, ((sz + MapBase.SEGMENT_SIZE) - MapBase.FLOOR_HEIGHT), (sz + MapBase.SEGMENT_SIZE), true, true, true, true, true, false, false, MeshUtility.UV_MAP));
                    break;
                case RAIL_RAIL_SQUARE:
                    sz -= railRadius;
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_X, new RagPoint(sx, ty, (sz + MapBase.SEGMENT_SIZE)), new RagPoint((sx + MapBase.SEGMENT_SIZE), ty, (sz + MapBase.SEGMENT_SIZE)), railRadius, 4));
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_Y, new RagPoint((sx + railOffset), ty2, (sz + MapBase.SEGMENT_SIZE)), new RagPoint((sx + railOffset), by, (sz + MapBase.SEGMENT_SIZE)), railRadius, 4));
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_Y, new RagPoint(((sx + MapBase.SEGMENT_SIZE) - railOffset), ty2, (sz + MapBase.SEGMENT_SIZE)), new RagPoint(((sz + MapBase.SEGMENT_SIZE) + railOffset), by, (sz + MapBase.SEGMENT_SIZE)), railRadius, 4));
                    break;
                case RAIL_RAIL_ROUND:
                    sz -= railRadius;
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_X, new RagPoint(sx, ty, (sz + MapBase.SEGMENT_SIZE)), new RagPoint((sx + MapBase.SEGMENT_SIZE), ty, (sz + MapBase.SEGMENT_SIZE)), railRadius, RAIL_AROUND_COUNT));
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_Y, new RagPoint((sx + railOffset), ty2, (sz + MapBase.SEGMENT_SIZE)), new RagPoint((sx + railOffset), by, (sz + MapBase.SEGMENT_SIZE)), railRadius, RAIL_AROUND_COUNT));
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_Y, new RagPoint(((sx + MapBase.SEGMENT_SIZE) - railOffset), ty2, (sz + MapBase.SEGMENT_SIZE)), new RagPoint(((sz + MapBase.SEGMENT_SIZE) + railOffset), by, (sz + MapBase.SEGMENT_SIZE)), railRadius, RAIL_AROUND_COUNT));
                    break;
                case RAIL_RAIL_SQUARE_WALL:
                    room.node.addMesh(MeshUtility.createCube("railing", sx, (sx + MapBase.SEGMENT_SIZE), by, my, ((sz + MapBase.SEGMENT_SIZE) - MapBase.FLOOR_HEIGHT), (sz + MapBase.SEGMENT_SIZE), true, true, true, true, true, false, false, MeshUtility.UV_MAP));
                    sz -= railRadius;
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_X, new RagPoint(sx, ty, (sz + MapBase.SEGMENT_SIZE)), new RagPoint((sx + MapBase.SEGMENT_SIZE), ty, (sz + MapBase.SEGMENT_SIZE)), railRadius, 4));
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_Y, new RagPoint((sx + railOffset), ty2, (sz + MapBase.SEGMENT_SIZE)), new RagPoint((sx + railOffset), by, (sz + MapBase.SEGMENT_SIZE)), railRadius, 4));
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_Y, new RagPoint(((sx + MapBase.SEGMENT_SIZE) - railOffset), ty2, (sz + MapBase.SEGMENT_SIZE)), new RagPoint(((sz + MapBase.SEGMENT_SIZE) + railOffset), by, (sz + MapBase.SEGMENT_SIZE)), railRadius, 4));
                    break;
                case RAIL_RAIL_ROUND_WALL:
                    room.node.addMesh(MeshUtility.createCube("railing", sx, (sx + MapBase.SEGMENT_SIZE), by, my, ((sz + MapBase.SEGMENT_SIZE) - MapBase.FLOOR_HEIGHT), (sz + MapBase.SEGMENT_SIZE), true, true, true, true, true, false, false, MeshUtility.UV_MAP));
                    sz -= railRadius;
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_X, new RagPoint(sx, ty, (sz + MapBase.SEGMENT_SIZE)), new RagPoint((sx + MapBase.SEGMENT_SIZE), ty, (sz + MapBase.SEGMENT_SIZE)), railRadius, RAIL_AROUND_COUNT));
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_Y, new RagPoint((sx + railOffset), ty2, (sz + MapBase.SEGMENT_SIZE)), new RagPoint((sx + railOffset), by, (sz + MapBase.SEGMENT_SIZE)), railRadius, RAIL_AROUND_COUNT));
                    room.node.addMesh(MeshUtility.createCylinderAroundAxis("rail", "railing", MeshUtility.AXIS_Y, new RagPoint(((sx + MapBase.SEGMENT_SIZE) - railOffset), ty2, (sz + MapBase.SEGMENT_SIZE)), new RagPoint(((sz + MapBase.SEGMENT_SIZE) + railOffset), by, (sz + MapBase.SEGMENT_SIZE)), railRadius, RAIL_AROUND_COUNT));
                    break;
            }
        }
    }

    private void addPlatformRailing(MapRoom room, float y) {
        int x, z;
        int railType;
        float railRadius, railOffset;

        railType = AppWindow.random.nextInt(6);
        railRadius = 0.15f + AppWindow.random.nextFloat(0.3f);
        railOffset = 0.25f + AppWindow.random.nextFloat(0.4f);

        for (z = 0; z != room.piece.sizeZ; z++) {
            for (x = 0; x != room.piece.sizeX; x++) {
                if (room.getPlatformGrid(x, z)) {
                    addPlatformRailingSegment(room, railType, railRadius, railOffset, x, z, y);
                }
            }
        }
    }

    // platform segments
    private void knockOutPlatformSides(MapRoom room, MapRoom platformRoom) {
        int x, z, k;

        // can open up the side of the stairs facing
        // away from the steps
        switch (room.stairDir) {
            case MeshMapUtility.STAIR_DIR_POS_Z:
                k = platformRoom.touchesNegativeZ(rooms) ? 1 : 0;
                for (z = k; z <= (room.stairZ + 1); z++) {
                    room.setPlatformGridAcrossZ(z, false);
                }
                if ((platformRoom.touchesNegativeX(rooms)) || (AppWindow.random.nextBoolean())) {
                    room.setPlatformGridAcrossX(0, true);
                }
                if ((platformRoom.touchesPositiveX(rooms)) || (AppWindow.random.nextBoolean())) {
                    room.setPlatformGridAcrossX((room.piece.sizeX - 1), true);
                }
                break;
            case MeshMapUtility.STAIR_DIR_NEG_Z:
                k = platformRoom.touchesPositiveZ(rooms) ? (room.piece.sizeZ - 2) : (room.piece.sizeZ - 1);
                for (z = k; z >= (room.stairZ - 1); z--) {
                    room.setPlatformGridAcrossZ(z, false);
                }
                if ((platformRoom.touchesNegativeX(rooms)) || (AppWindow.random.nextBoolean())) {
                    room.setPlatformGridAcrossX(0, true);
                }
                if ((platformRoom.touchesPositiveX(rooms)) || (AppWindow.random.nextBoolean())) {
                    room.setPlatformGridAcrossX((room.piece.sizeX - 1), true);
                }
                break;
            case MeshMapUtility.STAIR_DIR_POS_X:
                k = platformRoom.touchesNegativeX(rooms) ? 1 : 0;
                for (x = k; x <= (room.stairX + 1); x++) {
                    room.setPlatformGridAcrossX(x, false);
                }
                if ((platformRoom.touchesNegativeZ(rooms)) || (AppWindow.random.nextBoolean())) {
                    room.setPlatformGridAcrossZ(0, true);
                }
                if ((platformRoom.touchesPositiveZ(rooms)) || (AppWindow.random.nextBoolean())) {
                    room.setPlatformGridAcrossZ((room.piece.sizeZ - 1), true);
                }
                break;
            case MeshMapUtility.STAIR_DIR_NEG_X:
                k = platformRoom.touchesPositiveX(rooms) ? (room.piece.sizeX - 2) : (room.piece.sizeX - 1);
                for (x = k; x >= (room.stairX - 1); x--) {
                    room.setPlatformGridAcrossX(x, false);
                }
                if ((platformRoom.touchesNegativeZ(rooms)) || (AppWindow.random.nextBoolean())) {
                    room.setPlatformGridAcrossZ(0, true);
                }
                if ((platformRoom.touchesPositiveZ(rooms)) || (AppWindow.random.nextBoolean())) {
                    room.setPlatformGridAcrossZ((room.piece.sizeZ - 1), true);
                }
                break;
        }
    }

    private void addPlatforms(MapRoom room, int roomNumber, float y) {
        int x, z, trigIdx;
        float ty, by, negX, posX, negZ, posZ;
        ArrayList<Float> vertexArray, normalArray;
        ArrayList<Integer> indexArray;
        float[] vertexes, normals, uvs, tangents;
        int[] indexes;

        // setup the buffers
        vertexArray=new ArrayList<>();
        normalArray=new ArrayList<>();
        indexArray=new ArrayList<>();

        // make the segments
        trigIdx = 0;

        for (z = 0; z != room.piece.sizeZ; z++) {
            for (x = 0; x != room.piece.sizeX; x++) {
                if (!room.getPlatformGrid(x, z)) {
                    continue;
                }

                    // create the segments
                ty = y + MapBase.FLOOR_HEIGHT;
                by = y;

                negX = (room.x + x) * MapBase.SEGMENT_SIZE;
                posX = (room.x + (x + 1)) * MapBase.SEGMENT_SIZE;
                negZ = (room.z + z) * MapBase.SEGMENT_SIZE;
                posZ = (room.z + (z + 1)) * MapBase.SEGMENT_SIZE;

                if (hasNegXWall(room, x, z)) {
                    vertexArray.addAll(Arrays.asList(negX,ty,negZ,negX,ty,posZ,negX,by,posZ,negX,by,negZ));
                    normalArray.addAll(Arrays.asList(-1.0f,0.0f,0.0f,-1.0f,0.0f,0.0f,-1.0f,0.0f,0.0f,-1.0f,0.0f,0.0f));
                    trigIdx = MeshUtility.addQuadToIndexes(indexArray, trigIdx);
                }

                if (hasPosXWall(room, x, z)) {
                    vertexArray.addAll(Arrays.asList(posX,ty,negZ,posX,ty,posZ,posX,by,posZ,posX,by,negZ));
                    normalArray.addAll(Arrays.asList(1.0f,0.0f,0.0f,1.0f,0.0f,0.0f,1.0f,0.0f,0.0f,1.0f,0.0f,0.0f));
                    trigIdx = MeshUtility.addQuadToIndexes(indexArray, trigIdx);
                }

                // always draw the top
                vertexArray.addAll(Arrays.asList(negX,ty,negZ,negX,ty,posZ,posX,ty,posZ,posX,ty,negZ));
                normalArray.addAll(Arrays.asList(0.0f,1.0f,0.0f,0.0f,1.0f,0.0f,0.0f,1.0f,0.0f,0.0f,1.0f,0.0f));
                trigIdx = MeshUtility.addQuadToIndexes(indexArray, trigIdx);

                vertexArray.addAll(Arrays.asList(negX, by, negZ, negX, by, posZ, posX, by, posZ, posX, by, negZ));
                normalArray.addAll(Arrays.asList(0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f));
                trigIdx = MeshUtility.addQuadToIndexes(indexArray, trigIdx);

                if (hasNegZWall(room, x, z)) {
                    vertexArray.addAll(Arrays.asList(negX,ty,negZ,posX,ty,negZ,posX,by,negZ,negX,by,negZ));
                    normalArray.addAll(Arrays.asList(0.0f,0.0f,-1.0f,0.0f,0.0f,-1.0f,0.0f,0.0f,-1.0f,0.0f,0.0f,-1.0f));
                    trigIdx = MeshUtility.addQuadToIndexes(indexArray, trigIdx);
                }

                if (hasPosZWall(room, x, z)) {
                    vertexArray.addAll(Arrays.asList(negX,ty,posZ,posX,ty,posZ,posX,by,posZ,negX,by,posZ));
                    normalArray.addAll(Arrays.asList(0.0f,0.0f,1.0f,0.0f,0.0f,1.0f,0.0f,0.0f,1.0f,0.0f,0.0f,1.0f));
                    trigIdx = MeshUtility.addQuadToIndexes(indexArray, trigIdx);
                }
            }
        }

        if (vertexArray.isEmpty()) {
            return;
        }

        vertexes = MeshUtility.floatArrayListToFloat(vertexArray);
        normals = MeshUtility.floatArrayListToFloat(normalArray);
        indexes = MeshUtility.intArrayListToInt(indexArray);
        uvs = MeshUtility.buildUVs(vertexes, normals, (1.0f / MapBase.SEGMENT_SIZE));
        tangents = MeshUtility.buildTangents(vertexes, uvs, indexes);

        room.node.addMesh(new Mesh(("platform_" + Integer.toString(roomNumber)), "platform", vertexes, normals, tangents, uvs, indexes));
    }

    // build platforms
    public void build(MapRoom room, int roomNumber, boolean upper) {
        float y;

        if (upper) {
            y = MapBase.SEGMENT_SIZE + MapBase.FLOOR_HEIGHT;
        } else {
            y = -MapBase.FLOOR_HEIGHT;
        }

        if (AppWindow.random.nextBoolean()) {
            knockOutPlatformSides(room, (upper ? room : room.extendedFromRoom));
        }

        addPlatforms(room, roomNumber, y);
        addPlatformRailing(room, y);
    }
}
