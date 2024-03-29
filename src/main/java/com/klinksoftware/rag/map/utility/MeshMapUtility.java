package com.klinksoftware.rag.map.utility;

import com.klinksoftware.rag.map.utility.MapPiece;
import com.klinksoftware.rag.map.utility.MapRoom;
import com.klinksoftware.rag.scene.Mesh;
import com.klinksoftware.rag.utility.MeshUtility;
import com.klinksoftware.rag.utility.RagPoint;

import java.util.*;

public class MeshMapUtility
{
    public static final int STAIR_DIR_POS_Z = 0;
    public static final int STAIR_DIR_NEG_Z=1;
    public static final int STAIR_DIR_POS_X=2;
    public static final int STAIR_DIR_NEG_X=3;

    // room floor and ceilings
    public static void buildRoomFloorCeiling(MapRoom room, RagPoint centerPnt, int roomNumber, boolean floor, boolean textureSameAsWall) {
        int n, k, trigIdx, idx;
        float py;
        ArrayList<Float> vertexArray;
        ArrayList<Integer> indexArray;
        int[] indexes;
        float[] vertexes, normals, tangents, uvs;
        MapPiece piece;

        piece = room.piece;

        py = 0.0f;

        switch (room.story) {
            case MapRoom.ROOM_STORY_MAIN:
                if ((floor) && (room.hasLowerExtension)) {
                    return;
                }
                if ((!floor) && (room.hasUpperExtension)) {
                    return;
                }
                py = floor ? 0.0f : (MapBase.SEGMENT_SIZE + MapBase.FLOOR_HEIGHT);
                break;
            case MapRoom.ROOM_STORY_UPPER:
                py = floor ? (MapBase.SEGMENT_SIZE + (MapBase.FLOOR_HEIGHT * 2)) : ((MapBase.SEGMENT_SIZE * 2) + (MapBase.FLOOR_HEIGHT * 3));
                break;
            case MapRoom.ROOM_STORY_LOWER:
                py = floor ? -(MapBase.SEGMENT_SIZE + (MapBase.FLOOR_HEIGHT * 2)) : -(MapBase.FLOOR_HEIGHT * 1);
                break;
            case MapRoom.ROOM_STORY_UPPER_EXTENSION:
            case MapRoom.ROOM_STORY_TALL_EXTENSION:
                if (floor) {
                    return;
                }
                py = (MapBase.SEGMENT_SIZE * 2) + (MapBase.FLOOR_HEIGHT * 3);
                break;
            case MapRoom.ROOM_STORY_LOWER_EXTENSION:
                if (!floor) {
                    return;
                }
                py = -(MapBase.SEGMENT_SIZE + (MapBase.FLOOR_HEIGHT * 2));
                break;
            case MapRoom.ROOM_STORY_SUNKEN_EXTENSION:
                if (!floor) {
                    return;
                }
                py = -(MapBase.SUNKEN_HEIGHT + (MapBase.FLOOR_HEIGHT * 2));
                break;
        }

        vertexArray = new ArrayList<>();
        indexArray=new ArrayList<>();

        trigIdx = 0;
        idx = 0;

        for (n = 0; n != (piece.floorQuads.length / 4); n++) {
            for (k = 0; k != 4; k++) {
                vertexArray.addAll(Arrays.asList(((room.x + piece.floorQuads[idx][0]) * MapBase.SEGMENT_SIZE), py, ((room.z + piece.floorQuads[idx][1]) * MapBase.SEGMENT_SIZE)));
                idx++;
            }

            trigIdx = MeshUtility.addQuadToIndexes(indexArray, trigIdx);
        }

        idx = 0;

        for (n = 0; n != (piece.floorTrigs.length / 3); n++) {
            for (k = 0; k != 3; k++) {
                vertexArray.addAll(Arrays.asList(((room.x + piece.floorTrigs[idx][0]) * MapBase.SEGMENT_SIZE), py, ((room.z + piece.floorTrigs[idx][1]) * MapBase.SEGMENT_SIZE)));
                idx++;
            }

            trigIdx = MeshUtility.addTrigToIndexes(indexArray, trigIdx);
        }

        if (trigIdx == 0) {
            return;
        }

        vertexes = MeshUtility.floatArrayListToFloat(vertexArray);
        indexes = MeshUtility.intArrayListToInt(indexArray);
        normals = MeshUtility.buildNormalsSimple(vertexes, 0.0f, (floor ? 1.0f : -1.0f), 0.0f);
        uvs = MeshUtility.buildUVs(vertexes, normals, (1.0f / MapBase.SEGMENT_SIZE));
        tangents = MeshUtility.buildTangents(vertexes, uvs, indexes);

        if (floor) {
            room.node.addMesh(new Mesh(("floor_" + Integer.toString(roomNumber)), (room.story == MapRoom.ROOM_STORY_MAIN ? "floor" : "floor_lower"), vertexes, normals, tangents, uvs, indexes));
        } else {
            if (textureSameAsWall) {
                room.node.addMesh(new Mesh(("ceiling_" + Integer.toString(roomNumber)), "wall_main", vertexes, normals, tangents, uvs, indexes));
            } else {
                room.node.addMesh(new Mesh(("ceiling_" + Integer.toString(roomNumber)), (room.story == MapRoom.ROOM_STORY_MAIN ? "ceiling" : "ceiling_upper"), vertexes, normals, tangents, uvs, indexes));
            }
        }
    }

    // room indoor walls
    public static void buildRoomIndoorWalls(MapRoom room, RagPoint centerPnt, int roomNumber) {
        int n, n2, trigIdx, vertexCount;
        float y, y2;
        int[] indexes;
        float[] vertexes, normals, tangents, uvs;
        ArrayList<Float> vertexArray;
        ArrayList<Integer> indexArray;
        MapPiece piece;

        piece=room.piece;
        vertexCount = piece.wallLines.length;

        // regular walls
        vertexArray = new ArrayList<>();
        indexArray = new ArrayList<>();

        trigIdx = 0;
        vertexCount = piece.wallLines.length;

        switch (room.story) {
            case MapRoom.ROOM_STORY_UPPER:
            case MapRoom.ROOM_STORY_UPPER_EXTENSION:
            case MapRoom.ROOM_STORY_TALL_EXTENSION:
                y = (MapBase.SEGMENT_SIZE + (MapBase.FLOOR_HEIGHT * 2));
                y2 = y + MapBase.SEGMENT_SIZE;
                break;
            case MapRoom.ROOM_STORY_LOWER:
            case MapRoom.ROOM_STORY_LOWER_EXTENSION:
                y = -(MapBase.SEGMENT_SIZE + (MapBase.FLOOR_HEIGHT * 2));
                y2 = y + MapBase.SEGMENT_SIZE;
                break;
            case MapRoom.ROOM_STORY_SUNKEN_EXTENSION:
                y = -(MapBase.SUNKEN_HEIGHT + (MapBase.FLOOR_HEIGHT * 2));
                y2 = y + MapBase.SUNKEN_HEIGHT;
                break;
            default:
                y = 0.0f;
                y2 = y + MapBase.SEGMENT_SIZE;
                break;
        }

        for (n = 0; n != vertexCount; n++) {

            n2 = n + 1;
            if (n2 == vertexCount) {
                n2 = 0;
            }

            if (room.getWallHideType(n) == MapRoom.WALL_HIDE_NONE) {
                vertexArray.addAll(Arrays.asList(((piece.wallLines[n][0] + room.x) * MapBase.SEGMENT_SIZE), y2, ((piece.wallLines[n][1] + room.z) * MapBase.SEGMENT_SIZE)));
                vertexArray.addAll(Arrays.asList(((piece.wallLines[n2][0] + room.x) * MapBase.SEGMENT_SIZE), y2, ((piece.wallLines[n2][1] + room.z) * MapBase.SEGMENT_SIZE)));
                vertexArray.addAll(Arrays.asList(((piece.wallLines[n2][0] + room.x) * MapBase.SEGMENT_SIZE), y, ((piece.wallLines[n2][1] + room.z) * MapBase.SEGMENT_SIZE)));
                vertexArray.addAll(Arrays.asList(((piece.wallLines[n][0] + room.x) * MapBase.SEGMENT_SIZE), y, ((piece.wallLines[n][1] + room.z) * MapBase.SEGMENT_SIZE)));
                trigIdx = MeshUtility.addQuadToIndexes(indexArray, trigIdx);
            }

            // small top wall (all versions have this, for doorway overheads)
            if (room.getWallHideType(n) != MapRoom.WALL_HIDE_FULL) {
                vertexArray.addAll(Arrays.asList(((piece.wallLines[n][0] + room.x) * MapBase.SEGMENT_SIZE), (y2 + MapBase.FLOOR_HEIGHT), ((piece.wallLines[n][1] + room.z) * MapBase.SEGMENT_SIZE)));
                vertexArray.addAll(Arrays.asList(((piece.wallLines[n2][0] + room.x) * MapBase.SEGMENT_SIZE), (y2 + MapBase.FLOOR_HEIGHT), ((piece.wallLines[n2][1] + room.z) * MapBase.SEGMENT_SIZE)));
                vertexArray.addAll(Arrays.asList(((piece.wallLines[n2][0] + room.x) * MapBase.SEGMENT_SIZE), y2, ((piece.wallLines[n2][1] + room.z) * MapBase.SEGMENT_SIZE)));
                vertexArray.addAll(Arrays.asList(((piece.wallLines[n][0] + room.x) * MapBase.SEGMENT_SIZE), y2, ((piece.wallLines[n][1] + room.z) * MapBase.SEGMENT_SIZE)));
                trigIdx = MeshUtility.addQuadToIndexes(indexArray, trigIdx);
            }
        }

        if (trigIdx != 0) {
            vertexes = MeshUtility.floatArrayListToFloat(vertexArray);
            indexes = MeshUtility.intArrayListToInt(indexArray);
            normals = MeshUtility.buildNormals(vertexes, indexes, centerPnt, true);
            uvs = MeshUtility.buildUVs(vertexes, normals, (1.0f / MapBase.SEGMENT_SIZE));
            tangents = MeshUtility.buildTangents(vertexes, uvs, indexes);

            switch (room.story) {
                case MapRoom.ROOM_STORY_MAIN:
                    room.node.addMesh(new Mesh(("wall_main_" + Integer.toString(roomNumber)), "wall_main", vertexes, normals, tangents, uvs, indexes));
                    break;
                case MapRoom.ROOM_STORY_UPPER:
                case MapRoom.ROOM_STORY_UPPER_EXTENSION:
                    room.node.addMesh(new Mesh(("wall_upper_" + Integer.toString(roomNumber)), "wall_upper", vertexes, normals, tangents, uvs, indexes));
                    break;
                case MapRoom.ROOM_STORY_TALL_EXTENSION:
                    room.node.addMesh(new Mesh(("wall_tall_" + Integer.toString(roomNumber)), "wall_main", vertexes, normals, tangents, uvs, indexes));
                    break;
                case MapRoom.ROOM_STORY_LOWER:
                case MapRoom.ROOM_STORY_LOWER_EXTENSION:
                    room.node.addMesh(new Mesh(("wall_lower_" + Integer.toString(roomNumber)), "wall_lower", vertexes, normals, tangents, uvs, indexes));
                    break;
                case MapRoom.ROOM_STORY_SUNKEN_EXTENSION:
                    room.node.addMesh(new Mesh(("wall_sunken_" + Integer.toString(roomNumber)), "wall_main", vertexes, normals, tangents, uvs, indexes));
                    break;
            }
        }

        // extension walls
        vertexArray = new ArrayList<>();
        indexArray = new ArrayList<>();

        trigIdx = 0;

        for (n = 0; n != vertexCount; n++) {

            n2 = n + 1;
            if (n2 == vertexCount) {
                n2 = 0;
            }

            if (room.getWallHideType(n) == MapRoom.WALL_HIDE_FULL) {
                continue;
            }

            // extra small bottom wall for upper extension rooms
            if ((room.story == MapRoom.ROOM_STORY_UPPER_EXTENSION) || (room.story == MapRoom.ROOM_STORY_TALL_EXTENSION)) {
                vertexArray.addAll(Arrays.asList(((piece.wallLines[n][0] + room.x) * MapBase.SEGMENT_SIZE), (y - MapBase.FLOOR_HEIGHT), ((piece.wallLines[n][1] + room.z) * MapBase.SEGMENT_SIZE)));
                vertexArray.addAll(Arrays.asList(((piece.wallLines[n2][0] + room.x) * MapBase.SEGMENT_SIZE), (y - MapBase.FLOOR_HEIGHT), ((piece.wallLines[n2][1] + room.z) * MapBase.SEGMENT_SIZE)));
                vertexArray.addAll(Arrays.asList(((piece.wallLines[n2][0] + room.x) * MapBase.SEGMENT_SIZE), y, ((piece.wallLines[n2][1] + room.z) * MapBase.SEGMENT_SIZE)));
                vertexArray.addAll(Arrays.asList(((piece.wallLines[n][0] + room.x) * MapBase.SEGMENT_SIZE), y, ((piece.wallLines[n][1] + room.z) * MapBase.SEGMENT_SIZE)));
                trigIdx = MeshUtility.addQuadToIndexes(indexArray, trigIdx);
            }

            // extra small top wall for lower rooms
            if ((room.story == MapRoom.ROOM_STORY_LOWER_EXTENSION) || (room.story == MapRoom.ROOM_STORY_SUNKEN_EXTENSION)) {
                vertexArray.addAll(Arrays.asList(((piece.wallLines[n][0] + room.x) * MapBase.SEGMENT_SIZE), (y2 + (MapBase.FLOOR_HEIGHT * 2)), ((piece.wallLines[n][1] + room.z) * MapBase.SEGMENT_SIZE)));
                vertexArray.addAll(Arrays.asList(((piece.wallLines[n2][0] + room.x) * MapBase.SEGMENT_SIZE), (y2 + (MapBase.FLOOR_HEIGHT * 2)), ((piece.wallLines[n2][1] + room.z) * MapBase.SEGMENT_SIZE)));
                vertexArray.addAll(Arrays.asList(((piece.wallLines[n2][0] + room.x) * MapBase.SEGMENT_SIZE), (y2 + MapBase.FLOOR_HEIGHT), ((piece.wallLines[n2][1] + room.z) * MapBase.SEGMENT_SIZE)));
                vertexArray.addAll(Arrays.asList(((piece.wallLines[n][0] + room.x) * MapBase.SEGMENT_SIZE), (y2 + MapBase.FLOOR_HEIGHT), ((piece.wallLines[n][1] + room.z) * MapBase.SEGMENT_SIZE)));
                trigIdx = MeshUtility.addQuadToIndexes(indexArray, trigIdx);
            }
        }

        if (trigIdx != 0) {
            vertexes = MeshUtility.floatArrayListToFloat(vertexArray);
            indexes = MeshUtility.intArrayListToInt(indexArray);
            normals = MeshUtility.buildNormals(vertexes, indexes, centerPnt, true);
            uvs = MeshUtility.buildUVs(vertexes, normals, (1.0f / MapBase.SEGMENT_SIZE));
            tangents = MeshUtility.buildTangents(vertexes, uvs, indexes);

            switch (room.story) {
                case MapRoom.ROOM_STORY_UPPER_EXTENSION:
                    room.node.addMesh(new Mesh(("platform_upper_" + Integer.toString(roomNumber)), "platform", vertexes, normals, tangents, uvs, indexes));
                    break;
                case MapRoom.ROOM_STORY_TALL_EXTENSION:
                    room.node.addMesh(new Mesh(("platform_upper_" + Integer.toString(roomNumber)), "wall_main", vertexes, normals, tangents, uvs, indexes));
                    break;
                case MapRoom.ROOM_STORY_LOWER_EXTENSION:
                    room.node.addMesh(new Mesh(("platform_lower_" + Integer.toString(roomNumber)), "platform", vertexes, normals, tangents, uvs, indexes));
                    break;
                case MapRoom.ROOM_STORY_SUNKEN_EXTENSION:
                    room.node.addMesh(new Mesh(("platform_lower_" + Integer.toString(roomNumber)), "wall_main", vertexes, normals, tangents, uvs, indexes));
                    break;
            }
        }
    }

    // rooms outdoor walls
    public static void buildRoomOutdoorWalls(MapRoom room, RagPoint centerPnt, int roomNumber) {
        int n, n2, trigIdx, vertexCount;
        int[] indexes;
        float[] vertexes, normals, tangents, uvs;
        ArrayList<Float> vertexArray;
        ArrayList<Integer> indexArray;
        MapPiece piece;

        piece = room.piece;
        vertexCount = piece.wallLines.length;

        // regular walls
        vertexArray = new ArrayList<>();
        indexArray = new ArrayList<>();

        trigIdx = 0;
        vertexCount = piece.wallLines.length;

        for (n = 0; n != vertexCount; n++) {

            n2 = n + 1;
            if (n2 == vertexCount) {
                n2 = 0;
            }

            if (room.getWallHideType(n) == MapRoom.WALL_HIDE_FULL) {
                continue;
            }

            vertexArray.addAll(Arrays.asList(((piece.wallLines[n][0] + room.x) * MapBase.SEGMENT_SIZE), (MapBase.SEGMENT_SIZE * 2.0f), ((piece.wallLines[n][1] + room.z) * MapBase.SEGMENT_SIZE)));
            vertexArray.addAll(Arrays.asList(((piece.wallLines[n2][0] + room.x) * MapBase.SEGMENT_SIZE), (MapBase.SEGMENT_SIZE * 2.0f), ((piece.wallLines[n2][1] + room.z) * MapBase.SEGMENT_SIZE)));
            vertexArray.addAll(Arrays.asList(((piece.wallLines[n2][0] + room.x) * MapBase.SEGMENT_SIZE), 0.0f, ((piece.wallLines[n2][1] + room.z) * MapBase.SEGMENT_SIZE)));
            vertexArray.addAll(Arrays.asList(((piece.wallLines[n][0] + room.x) * MapBase.SEGMENT_SIZE), 0.0f, ((piece.wallLines[n][1] + room.z) * MapBase.SEGMENT_SIZE)));
            trigIdx = MeshUtility.addQuadToIndexes(indexArray, trigIdx);
        }

        if (trigIdx != 0) {
            vertexes = MeshUtility.floatArrayListToFloat(vertexArray);
            indexes = MeshUtility.intArrayListToInt(indexArray);
            normals = MeshUtility.buildNormals(vertexes, indexes, centerPnt, true);
            uvs = MeshUtility.buildUVs(vertexes, normals, (1.0f / MapBase.SEGMENT_SIZE));
            tangents = MeshUtility.buildTangents(vertexes, uvs, indexes);

            room.node.addMesh(new Mesh(("wall_main_" + Integer.toString(roomNumber)), "wall_main", vertexes, normals, tangents, uvs, indexes));
        }
    }

    // staircases
    public static void buildStairs(MapRoom room, String name, float x, float y, float z, int dir, int stepCount, float stepWidth, float stepTotalHeight, float stepSize, boolean sides, boolean back) {
        int n, trigIdx;
        float sx, sx2, sy, sz, sz2, stepHigh;
        ArrayList<Float> vertexArray;
        ArrayList<Integer> indexArray;
        int[] indexes;
        float[] vertexes, normals, tangents, uvs;
        RagPoint centerPnt;

        // add in room ofsets
        x = (x + room.x) * MapBase.SEGMENT_SIZE;
        z = (z + room.z) * MapBase.SEGMENT_SIZE;

        stepHigh = stepTotalHeight / (float) stepCount;

        centerPnt=null;

        // allocate proper buffers
        vertexArray=new ArrayList<>();
        indexArray=new ArrayList<>();

        // initial locations
        sx=sz=0.0f;
        sx2=sz2=0.0f;

        switch (dir) {
            case STAIR_DIR_POS_Z:
            case STAIR_DIR_NEG_Z:
                sx=x;
                sx2=sx+(MapBase.SEGMENT_SIZE*stepWidth);
                centerPnt = new RagPoint((x + (MapBase.SEGMENT_SIZE * 0.5f)), y, (z + MapBase.SEGMENT_SIZE));
                break;
            case STAIR_DIR_POS_X:
            case STAIR_DIR_NEG_X:
                sz=z;
                sz2=sz+(MapBase.SEGMENT_SIZE*stepWidth);
                centerPnt = new RagPoint((x + MapBase.SEGMENT_SIZE), y, (z + (MapBase.SEGMENT_SIZE * 0.5f)));
                break;
        }

        // the steps
        trigIdx=0;

        sy=y+stepHigh;

        for (n = 0; n != stepCount; n++) {

                // step top

            switch (dir) {
                case STAIR_DIR_POS_Z:
                    sz=z+(n*stepSize);
                    sz2=sz+stepSize;
                    break;
                case STAIR_DIR_NEG_Z:
                    sz=(z+MapBase.SEGMENT_SIZE)-(n*stepSize);
                    sz2=sz-stepSize;
                    break;
                case STAIR_DIR_POS_X:
                    sx=x+(n*stepSize);
                    sx2=sx+stepSize;
                    break;
                case STAIR_DIR_NEG_X:
                    sx=(x+MapBase.SEGMENT_SIZE)-(n*stepSize);
                    sx2=sx-stepSize;
                    break;
            }

            vertexArray.addAll(Arrays.asList(sx,sy,sz,sx2,sy,sz,sx2,sy,sz2,sx,sy,sz2));
            trigIdx = MeshUtility.addQuadToIndexes(indexArray, trigIdx);

            // step front
            switch (dir) {
                case STAIR_DIR_POS_Z:
                case STAIR_DIR_NEG_Z:
                    vertexArray.addAll(Arrays.asList(sx,sy,sz,sx2,sy,sz,sx2,(sy-stepHigh),sz,sx,(sy-stepHigh),sz));
                    break;
                case STAIR_DIR_POS_X:
                case STAIR_DIR_NEG_X:
                    vertexArray.addAll(Arrays.asList(sx,sy,sz,sx,sy,sz2,sx,(sy-stepHigh),sz2,sx,(sy-stepHigh),sz));
                    break;
            }

            trigIdx = MeshUtility.addQuadToIndexes(indexArray, trigIdx);

            // step sides
            if (sides) {
                switch (dir) {
                    case STAIR_DIR_POS_Z:
                    case STAIR_DIR_NEG_Z:
                        vertexArray.addAll(Arrays.asList(sx,sy,sz,sx,sy,sz2,sx,y,sz2,sx,y,sz));
                        vertexArray.addAll(Arrays.asList(sx2,sy,sz,sx2,sy,sz2,sx2,y,sz2,sx2,y,sz));
                        break;
                    case STAIR_DIR_POS_X:
                    case STAIR_DIR_NEG_X:
                        vertexArray.addAll(Arrays.asList(sx,sy,sz,sx2,sy,sz,sx2,y,sz,sx,y,sz));
                        vertexArray.addAll(Arrays.asList(sx,sy,sz2,sx2,sy,sz2,sx2,y,sz2,sx,y,sz2));
                        break;
                }

                trigIdx = MeshUtility.addQuadToIndexes(indexArray, trigIdx);
                trigIdx = MeshUtility.addQuadToIndexes(indexArray, trigIdx);
            }

            sy+=stepHigh;
        }

        // step back
        if (back) {
            sy = y + (MapBase.SEGMENT_SIZE + MapBase.FLOOR_HEIGHT);

            switch (dir) {
                case STAIR_DIR_POS_Z:
                    sx = x + (((float) stepCount) * stepWidth);
                    sz=z+(MapBase.SEGMENT_SIZE*2.0f);
                    vertexArray.addAll(Arrays.asList(x,y,sz,sx,y,sz,sx,sy,sz,x,sy,sz));
                    break;
                case STAIR_DIR_NEG_Z:
                    sx = x + (((float) stepCount) * stepWidth);
                    sz=z-MapBase.SEGMENT_SIZE;
                    vertexArray.addAll(Arrays.asList(x,y,sz,sx,y,sz,sx,sy,sz,x,sy,sz));
                    break;
                case STAIR_DIR_POS_X:
                    sx=x+(MapBase.SEGMENT_SIZE*2.0f);
                    sz = z + (((float) stepCount) * stepWidth);
                    vertexArray.addAll(Arrays.asList(sx,y,z,sx,y,sz,sx,sy,sz,sx,sy,z));
                    break;
                case STAIR_DIR_NEG_X:
                    sx=x-MapBase.SEGMENT_SIZE;
                    sz = z + (((float) stepCount) * stepWidth);
                    vertexArray.addAll(Arrays.asList(sx,y,z,sx,y,sz,sx,sy,sz,sx,sy,z));
                    break;
            }

            trigIdx = MeshUtility.addQuadToIndexes(indexArray, trigIdx);
        }

        // create the mesh
        vertexes = MeshUtility.floatArrayListToFloat(vertexArray);
        indexes = MeshUtility.intArrayListToInt(indexArray);
        normals = MeshUtility.buildNormals(vertexes, indexes, centerPnt, false);
        uvs = MeshUtility.buildUVs(vertexes, normals, (1.0f / MapBase.SEGMENT_SIZE));
        tangents = MeshUtility.buildTangents(vertexes, uvs, indexes);

        room.node.addMesh(new Mesh(name, "stair", vertexes, normals, tangents, uvs, indexes));
    }

    // ramp
    public static void buildRamp(MapRoom room, String name, float x, float y, float z, int dir, float rampWidth, float rampHeight, float rampLength) {
        // add in room ofsets
        x = (x + room.x) * MapBase.SEGMENT_SIZE;
        z = (z + room.z) * MapBase.SEGMENT_SIZE;

        switch (dir) {
            case STAIR_DIR_POS_Z:
                room.node.addMesh(MeshUtility.createRamp("stair", x, y, z, MeshUtility.RAMP_DIR_POS_Z, (MapBase.SEGMENT_SIZE * rampWidth), rampHeight, (MapBase.SEGMENT_SIZE * rampLength), false));
                break;
            case STAIR_DIR_NEG_Z:
                room.node.addMesh(MeshUtility.createRamp("stair", x, y, z, MeshUtility.RAMP_DIR_NEG_Z, (MapBase.SEGMENT_SIZE * rampWidth), rampHeight, (MapBase.SEGMENT_SIZE * rampLength), false));
                break;
            case STAIR_DIR_POS_X:
                room.node.addMesh(MeshUtility.createRamp("stair", x, y, z, MeshUtility.RAMP_DIR_POS_X, (MapBase.SEGMENT_SIZE * rampWidth), rampHeight, (MapBase.SEGMENT_SIZE * rampLength), false));
                break;
            case STAIR_DIR_NEG_X:
                room.node.addMesh(MeshUtility.createRamp("stair", x, y, z, MeshUtility.RAMP_DIR_NEG_X, (MapBase.SEGMENT_SIZE * rampWidth), rampHeight, (MapBase.SEGMENT_SIZE * rampLength), false));
                break;
        }
    }

}
