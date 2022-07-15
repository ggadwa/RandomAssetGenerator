package com.klinksoftware.rag.mesh;

import com.klinksoftware.rag.map.MapPiece;
import com.klinksoftware.rag.map.MapRoom;
import com.klinksoftware.rag.map.MapBuilder;
import com.klinksoftware.rag.utility.*;

import java.util.*;

public class MeshMapUtility
{
    public static final int STAIR_STEP_COUNT=10;

    public static final int STAIR_DIR_POS_Z=0;
    public static final int STAIR_DIR_NEG_Z=1;
    public static final int STAIR_DIR_POS_X=2;
    public static final int STAIR_DIR_NEG_X=3;

    // room floor and ceilings
    public static void buildRoomFloorCeiling(MeshList meshList, MapRoom room, RagPoint centerPnt, int roomNumber, boolean floor) {
        int x, z, trigIdx;
        float px, py, pz;
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
                py = floor ? 0.0f : (MapBuilder.SEGMENT_SIZE + MapBuilder.FLOOR_HEIGHT);
                break;
            case MapRoom.ROOM_STORY_UPPER:
                py = floor ? (MapBuilder.SEGMENT_SIZE + (MapBuilder.FLOOR_HEIGHT * 2)) : ((MapBuilder.SEGMENT_SIZE * 2) + (MapBuilder.FLOOR_HEIGHT * 3));
                break;
            case MapRoom.ROOM_STORY_LOWER:
                py = floor ? -(MapBuilder.SEGMENT_SIZE + (MapBuilder.FLOOR_HEIGHT * 2)) : -(MapBuilder.FLOOR_HEIGHT * 1);
                break;
            case MapRoom.ROOM_STORY_UPPER_EXTENSION:
            case MapRoom.ROOM_STORY_TALL_EXTENSION:
                if (floor) {
                    return;
                }
                py = (MapBuilder.SEGMENT_SIZE * 2) + (MapBuilder.FLOOR_HEIGHT * 3);
                break;
            case MapRoom.ROOM_STORY_LOWER_EXTENSION:
            case MapRoom.ROOM_STORY_SUNKEN_EXTENSION:
                if (!floor) {
                    return;
                }
                py = -(MapBuilder.SEGMENT_SIZE + (MapBuilder.FLOOR_HEIGHT * 2));
                break;
        }

        vertexArray = new ArrayList<>();
        indexArray=new ArrayList<>();

        trigIdx=0;

        for (z=0;z!=piece.sizeZ;z++) {
            pz=(room.z+z)*MapBuilder.SEGMENT_SIZE;
            for (x=0;x!=piece.sizeX;x++) {
                if (piece.floorGrid[(z * piece.sizeX) + x] == 0) {
                    continue;
                }

                px = (room.x + x) * MapBuilder.SEGMENT_SIZE;
                vertexArray.addAll(Arrays.asList(px,py,pz));
                vertexArray.addAll(Arrays.asList((px+MapBuilder.SEGMENT_SIZE),py,pz));
                vertexArray.addAll(Arrays.asList((px+MapBuilder.SEGMENT_SIZE),py,(pz+MapBuilder.SEGMENT_SIZE)));
                vertexArray.addAll(Arrays.asList(px,py,(pz+MapBuilder.SEGMENT_SIZE)));

                trigIdx = MeshUtility.addQuadToIndexes(indexArray, trigIdx);
            }
        }

        if (trigIdx==0) return;

        vertexes = MeshUtility.floatArrayListToFloat(vertexArray);
        indexes = MeshUtility.intArrayListToInt(indexArray);
        normals = MeshUtility.buildNormalsSimple(vertexes, 0.0f, (floor ? 1.0f : -1.0f), 0.0f);
        uvs = MeshUtility.buildUVs(vertexes, normals, (1.0f / MapBuilder.SEGMENT_SIZE));
        tangents = MeshUtility.buildTangents(vertexes, uvs, indexes);

        if (floor) {
            meshList.add(new Mesh(("floor_" + Integer.toString(roomNumber)), (room.story == MapRoom.ROOM_STORY_MAIN ? "floor" : "floor_lower"), vertexes, normals, tangents, uvs, indexes));
        } else {
            meshList.add(new Mesh(("ceiling_" + Integer.toString(roomNumber)), (room.story == MapRoom.ROOM_STORY_MAIN ? "ceiling" : "ceiling_upper"), vertexes, normals, tangents, uvs, indexes));
        }
    }

    // room indoor walls
    public static void buildRoomIndoorWalls(MeshList meshList, MapRoom room, RagPoint centerPnt, int roomNumber) {
        int n, n2, trigIdx, vertexCount;
        float y;
        int[] indexes;
        float[] vertexes, normals, tangents, uvs;
        ArrayList<Float> vertexArray;
        ArrayList<Integer> indexArray;
        MapPiece piece;

        piece=room.piece;
        vertexCount=piece.vertexes.length;

        // regular walls
        vertexArray = new ArrayList<>();
        indexArray = new ArrayList<>();

        trigIdx = 0;
        vertexCount = piece.vertexes.length;

        y = 0.0f;

        switch (room.story) {
            case MapRoom.ROOM_STORY_UPPER:
            case MapRoom.ROOM_STORY_UPPER_EXTENSION:
            case MapRoom.ROOM_STORY_TALL_EXTENSION:
                y = (MapBuilder.SEGMENT_SIZE + (MapBuilder.FLOOR_HEIGHT * 2));
                break;
            case MapRoom.ROOM_STORY_LOWER:
            case MapRoom.ROOM_STORY_LOWER_EXTENSION:
            case MapRoom.ROOM_STORY_SUNKEN_EXTENSION:
                y = -(MapBuilder.SEGMENT_SIZE + (MapBuilder.FLOOR_HEIGHT * 2));
                break;
        }

        for (n = 0; n != vertexCount; n++) {

            n2 = n + 1;
            if (n2 == vertexCount) {
                n2 = 0;
            }

            if (room.isWallHidden(n)) {
                continue;
            }

            vertexArray.addAll(Arrays.asList(((piece.vertexes[n][0] + room.x) * MapBuilder.SEGMENT_SIZE), (y + MapBuilder.SEGMENT_SIZE), ((piece.vertexes[n][1] + room.z) * MapBuilder.SEGMENT_SIZE)));
            vertexArray.addAll(Arrays.asList(((piece.vertexes[n2][0] + room.x) * MapBuilder.SEGMENT_SIZE), (y + MapBuilder.SEGMENT_SIZE), ((piece.vertexes[n2][1] + room.z) * MapBuilder.SEGMENT_SIZE)));
            vertexArray.addAll(Arrays.asList(((piece.vertexes[n2][0] + room.x) * MapBuilder.SEGMENT_SIZE), y, ((piece.vertexes[n2][1] + room.z) * MapBuilder.SEGMENT_SIZE)));
            vertexArray.addAll(Arrays.asList(((piece.vertexes[n][0] + room.x) * MapBuilder.SEGMENT_SIZE), y, ((piece.vertexes[n][1] + room.z) * MapBuilder.SEGMENT_SIZE)));
            trigIdx = MeshUtility.addQuadToIndexes(indexArray, trigIdx);

            // small top wall (all versions have this, for doorway overheads)
            vertexArray.addAll(Arrays.asList(((piece.vertexes[n][0] + room.x) * MapBuilder.SEGMENT_SIZE), ((y + MapBuilder.SEGMENT_SIZE) + MapBuilder.FLOOR_HEIGHT), ((piece.vertexes[n][1] + room.z) * MapBuilder.SEGMENT_SIZE)));
            vertexArray.addAll(Arrays.asList(((piece.vertexes[n2][0] + room.x) * MapBuilder.SEGMENT_SIZE), ((y + MapBuilder.SEGMENT_SIZE) + MapBuilder.FLOOR_HEIGHT), ((piece.vertexes[n2][1] + room.z) * MapBuilder.SEGMENT_SIZE)));
            vertexArray.addAll(Arrays.asList(((piece.vertexes[n2][0] + room.x) * MapBuilder.SEGMENT_SIZE), (y + MapBuilder.SEGMENT_SIZE), ((piece.vertexes[n2][1] + room.z) * MapBuilder.SEGMENT_SIZE)));
            vertexArray.addAll(Arrays.asList(((piece.vertexes[n][0] + room.x) * MapBuilder.SEGMENT_SIZE), (y + MapBuilder.SEGMENT_SIZE), ((piece.vertexes[n][1] + room.z) * MapBuilder.SEGMENT_SIZE)));
            trigIdx = MeshUtility.addQuadToIndexes(indexArray, trigIdx);
        }

        if (trigIdx != 0) {
            vertexes = MeshUtility.floatArrayListToFloat(vertexArray);
            indexes = MeshUtility.intArrayListToInt(indexArray);
            normals = MeshUtility.buildNormals(vertexes, indexes, centerPnt, true);
            uvs = MeshUtility.buildUVs(vertexes, normals, (1.0f / MapBuilder.SEGMENT_SIZE));
            tangents = MeshUtility.buildTangents(vertexes, uvs, indexes);

            switch (room.story) {
                case MapRoom.ROOM_STORY_MAIN:
                    meshList.add(new Mesh(("wall_main_" + Integer.toString(roomNumber)), "wall_main", vertexes, normals, tangents, uvs, indexes));
                    break;
                case MapRoom.ROOM_STORY_UPPER:
                case MapRoom.ROOM_STORY_UPPER_EXTENSION:
                    meshList.add(new Mesh(("wall_upper_" + Integer.toString(roomNumber)), "wall_upper", vertexes, normals, tangents, uvs, indexes));
                    break;
                case MapRoom.ROOM_STORY_TALL_EXTENSION:
                    meshList.add(new Mesh(("wall_tall_" + Integer.toString(roomNumber)), "wall_main", vertexes, normals, tangents, uvs, indexes));
                    break;
                case MapRoom.ROOM_STORY_LOWER:
                case MapRoom.ROOM_STORY_LOWER_EXTENSION:
                    meshList.add(new Mesh(("wall_lower_" + Integer.toString(roomNumber)), "wall_lower", vertexes, normals, tangents, uvs, indexes));
                    break;
                case MapRoom.ROOM_STORY_SUNKEN_EXTENSION:
                    meshList.add(new Mesh(("wall_sunken_" + Integer.toString(roomNumber)), "wall_main", vertexes, normals, tangents, uvs, indexes));
                    break;
            }
        }

        // platform walls
        vertexArray = new ArrayList<>();
        indexArray = new ArrayList<>();

        trigIdx = 0;

        for (n = 0; n != vertexCount; n++) {

            n2 = n + 1;
            if (n2 == vertexCount) {
                n2 = 0;
            }

            if (room.isWallHidden(n)) {
                continue;
            }

            // extra small bottom wall for upper extension rooms
            if ((room.story == MapRoom.ROOM_STORY_UPPER_EXTENSION) || (room.story == MapRoom.ROOM_STORY_TALL_EXTENSION)) {
                vertexArray.addAll(Arrays.asList(((piece.vertexes[n][0] + room.x) * MapBuilder.SEGMENT_SIZE), (y - MapBuilder.FLOOR_HEIGHT), ((piece.vertexes[n][1] + room.z) * MapBuilder.SEGMENT_SIZE)));
                vertexArray.addAll(Arrays.asList(((piece.vertexes[n2][0] + room.x) * MapBuilder.SEGMENT_SIZE), (y - MapBuilder.FLOOR_HEIGHT), ((piece.vertexes[n2][1] + room.z) * MapBuilder.SEGMENT_SIZE)));
                vertexArray.addAll(Arrays.asList(((piece.vertexes[n2][0] + room.x) * MapBuilder.SEGMENT_SIZE), y, ((piece.vertexes[n2][1] + room.z) * MapBuilder.SEGMENT_SIZE)));
                vertexArray.addAll(Arrays.asList(((piece.vertexes[n][0] + room.x) * MapBuilder.SEGMENT_SIZE), y, ((piece.vertexes[n][1] + room.z) * MapBuilder.SEGMENT_SIZE)));
                trigIdx = MeshUtility.addQuadToIndexes(indexArray, trigIdx);
            }

            // extra small top wall for lower rooms
            if ((room.story == MapRoom.ROOM_STORY_LOWER_EXTENSION) || (room.story == MapRoom.ROOM_STORY_SUNKEN_EXTENSION)) {
                vertexArray.addAll(Arrays.asList(((piece.vertexes[n][0] + room.x) * MapBuilder.SEGMENT_SIZE), ((y + MapBuilder.SEGMENT_SIZE) + (MapBuilder.FLOOR_HEIGHT * 2)), ((piece.vertexes[n][1] + room.z) * MapBuilder.SEGMENT_SIZE)));
                vertexArray.addAll(Arrays.asList(((piece.vertexes[n2][0] + room.x) * MapBuilder.SEGMENT_SIZE), ((y + MapBuilder.SEGMENT_SIZE) + (MapBuilder.FLOOR_HEIGHT * 2)), ((piece.vertexes[n2][1] + room.z) * MapBuilder.SEGMENT_SIZE)));
                vertexArray.addAll(Arrays.asList(((piece.vertexes[n2][0] + room.x) * MapBuilder.SEGMENT_SIZE), ((y + MapBuilder.SEGMENT_SIZE) + MapBuilder.FLOOR_HEIGHT), ((piece.vertexes[n2][1] + room.z) * MapBuilder.SEGMENT_SIZE)));
                vertexArray.addAll(Arrays.asList(((piece.vertexes[n][0] + room.x) * MapBuilder.SEGMENT_SIZE), ((y + MapBuilder.SEGMENT_SIZE) + MapBuilder.FLOOR_HEIGHT), ((piece.vertexes[n][1] + room.z) * MapBuilder.SEGMENT_SIZE)));
                trigIdx = MeshUtility.addQuadToIndexes(indexArray, trigIdx);
            }
        }

        if (trigIdx != 0) {
            vertexes = MeshUtility.floatArrayListToFloat(vertexArray);
            indexes = MeshUtility.intArrayListToInt(indexArray);
            normals = MeshUtility.buildNormals(vertexes, indexes, centerPnt, true);
            uvs = MeshUtility.buildUVs(vertexes, normals, (1.0f / MapBuilder.SEGMENT_SIZE));
            tangents = MeshUtility.buildTangents(vertexes, uvs, indexes);

            switch (room.story) {
                case MapRoom.ROOM_STORY_UPPER_EXTENSION:
                    meshList.add(new Mesh(("platform_upper_" + Integer.toString(roomNumber)), "platform", vertexes, normals, tangents, uvs, indexes));
                    break;
                case MapRoom.ROOM_STORY_TALL_EXTENSION:
                    meshList.add(new Mesh(("platform_upper_" + Integer.toString(roomNumber)), "wall_main", vertexes, normals, tangents, uvs, indexes));
                    break;
                case MapRoom.ROOM_STORY_LOWER_EXTENSION:
                    meshList.add(new Mesh(("platform_lower_" + Integer.toString(roomNumber)), "platform", vertexes, normals, tangents, uvs, indexes));
                    break;
                case MapRoom.ROOM_STORY_SUNKEN_EXTENSION:
                    meshList.add(new Mesh(("platform_lower_" + Integer.toString(roomNumber)), "wall_main", vertexes, normals, tangents, uvs, indexes));
                    break;
            }
        }
    }

    // rooms outdoor walls
    public static void buildRoomOutdoorWalls(MeshList meshList, MapRoom room, RagPoint centerPnt, int roomNumber) {
        int n, n2, trigIdx, vertexCount;
        int[] indexes;
        float[] vertexes, normals, tangents, uvs;
        ArrayList<Float> vertexArray;
        ArrayList<Integer> indexArray;
        MapPiece piece;

        piece = room.piece;
        vertexCount = piece.vertexes.length;

        // regular walls
        vertexArray = new ArrayList<>();
        indexArray = new ArrayList<>();

        trigIdx = 0;
        vertexCount = piece.vertexes.length;

        for (n = 0; n != vertexCount; n++) {

            n2 = n + 1;
            if (n2 == vertexCount) {
                n2 = 0;
            }

            if (room.isWallHidden(n)) {
                continue;
            }

            vertexArray.addAll(Arrays.asList(((piece.vertexes[n][0] + room.x) * MapBuilder.SEGMENT_SIZE), (MapBuilder.SEGMENT_SIZE * 2.0f), ((piece.vertexes[n][1] + room.z) * MapBuilder.SEGMENT_SIZE)));
            vertexArray.addAll(Arrays.asList(((piece.vertexes[n2][0] + room.x) * MapBuilder.SEGMENT_SIZE), (MapBuilder.SEGMENT_SIZE * 2.0f), ((piece.vertexes[n2][1] + room.z) * MapBuilder.SEGMENT_SIZE)));
            vertexArray.addAll(Arrays.asList(((piece.vertexes[n2][0] + room.x) * MapBuilder.SEGMENT_SIZE), 0.0f, ((piece.vertexes[n2][1] + room.z) * MapBuilder.SEGMENT_SIZE)));
            vertexArray.addAll(Arrays.asList(((piece.vertexes[n][0] + room.x) * MapBuilder.SEGMENT_SIZE), 0.0f, ((piece.vertexes[n][1] + room.z) * MapBuilder.SEGMENT_SIZE)));
            trigIdx = MeshUtility.addQuadToIndexes(indexArray, trigIdx);
        }

        if (trigIdx != 0) {
            vertexes = MeshUtility.floatArrayListToFloat(vertexArray);
            indexes = MeshUtility.intArrayListToInt(indexArray);
            normals = MeshUtility.buildNormals(vertexes, indexes, centerPnt, true);
            uvs = MeshUtility.buildUVs(vertexes, normals, (1.0f / MapBuilder.SEGMENT_SIZE));
            tangents = MeshUtility.buildTangents(vertexes, uvs, indexes);

            meshList.add(new Mesh(("wall_main_" + Integer.toString(roomNumber)), "wall_main", vertexes, normals, tangents, uvs, indexes));
        }
    }

    // staircases
    public static void buildStairs(MeshList meshList, MapRoom room, String name, float x, float y, float z, int dir, float stepWidth, boolean sides) {
        int n, trigIdx;
        float sx, sx2, sy, sz, sz2, stepSize, stepHigh;
        ArrayList<Float> vertexArray;
        ArrayList<Integer> indexArray;
        int[] indexes;
        float[] vertexes, normals, tangents, uvs;
        RagPoint centerPnt;

            // step sizes

        stepSize=(MapBuilder.SEGMENT_SIZE*2.0f)/(float)STAIR_STEP_COUNT;
        stepHigh = (MapBuilder.SEGMENT_SIZE + MapBuilder.FLOOR_HEIGHT) / (float) STAIR_STEP_COUNT;

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
                sx2=sx+(MapBuilder.SEGMENT_SIZE*stepWidth);
                centerPnt = new RagPoint((x + (MapBuilder.SEGMENT_SIZE * 0.5f)), y, (z + MapBuilder.SEGMENT_SIZE));
                break;
            case STAIR_DIR_POS_X:
            case STAIR_DIR_NEG_X:
                sz=z;
                sz2=sz+(MapBuilder.SEGMENT_SIZE*stepWidth);
                centerPnt = new RagPoint((x + MapBuilder.SEGMENT_SIZE), y, (z + (MapBuilder.SEGMENT_SIZE * 0.5f)));
                break;
        }

            // the steps

        trigIdx=0;

        sy=y+stepHigh;

        for (n=0;n!=STAIR_STEP_COUNT;n++) {

                // step top

            switch (dir) {
                case STAIR_DIR_POS_Z:
                    sz=z+(n*stepSize);
                    sz2=sz+stepSize;
                    break;
                case STAIR_DIR_NEG_Z:
                    sz=(z+MapBuilder.SEGMENT_SIZE)-(n*stepSize);
                    sz2=sz-stepSize;
                    break;
                case STAIR_DIR_POS_X:
                    sx=x+(n*stepSize);
                    sx2=sx+stepSize;
                    break;
                case STAIR_DIR_NEG_X:
                    sx=(x+MapBuilder.SEGMENT_SIZE)-(n*stepSize);
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

        if (sides) {
            sy = y + (MapBuilder.SEGMENT_SIZE + MapBuilder.FLOOR_HEIGHT);

            switch (dir) {
                case STAIR_DIR_POS_Z:
                    sx=x+(MapBuilder.SEGMENT_SIZE*stepWidth);
                    sz=z+(MapBuilder.SEGMENT_SIZE*2.0f);
                    vertexArray.addAll(Arrays.asList(x,y,sz,sx,y,sz,sx,sy,sz,x,sy,sz));
                    break;
                case STAIR_DIR_NEG_Z:
                    sx=x+(MapBuilder.SEGMENT_SIZE*stepWidth);
                    sz=z-MapBuilder.SEGMENT_SIZE;
                    vertexArray.addAll(Arrays.asList(x,y,sz,sx,y,sz,sx,sy,sz,x,sy,sz));
                    break;
                case STAIR_DIR_POS_X:
                    sx=x+(MapBuilder.SEGMENT_SIZE*2.0f);
                    sz=z+(MapBuilder.SEGMENT_SIZE*stepWidth);
                    vertexArray.addAll(Arrays.asList(sx,y,z,sx,y,sz,sx,sy,sz,sx,sy,z));
                    break;
                case STAIR_DIR_NEG_X:
                    sx=x-MapBuilder.SEGMENT_SIZE;
                    sz=z+(MapBuilder.SEGMENT_SIZE*stepWidth);
                    vertexArray.addAll(Arrays.asList(sx,y,z,sx,y,sz,sx,sy,sz,sx,sy,z));
                    break;
            }

            trigIdx = MeshUtility.addQuadToIndexes(indexArray, trigIdx);
        }

            // create the mesh

        vertexes = MeshUtility.floatArrayListToFloat(vertexArray);
        indexes = MeshUtility.intArrayListToInt(indexArray);
        normals = MeshUtility.buildNormals(vertexes, indexes, centerPnt, false);
        uvs = MeshUtility.buildUVs(vertexes, normals, (1.0f / MapBuilder.SEGMENT_SIZE));
        tangents = MeshUtility.buildTangents(vertexes, uvs, indexes);

        meshList.add(new Mesh(name, "platform", vertexes, normals, tangents, uvs, indexes));
    }

}
