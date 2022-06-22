package com.klinksoftware.rag.map;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.mesh.*;

import java.util.*;

public class MapPlatform {
    public static final int PLATFORM_DIR_POS_Z=0;
    public static final int PLATFORM_DIR_NEG_Z=1;
    public static final int PLATFORM_DIR_POS_X=2;
    public static final int PLATFORM_DIR_NEG_X=3;

    public static final int FLAG_NONE=0;
    public static final int FLAG_STEPS=1;
    public static final int FLAG_PLATFORM=2;
    public static final int FLAG_WALL=3;

    private MeshList meshList;
    private ArrayList<MapRoom> rooms;

    public MapPlatform(MeshList meshList, ArrayList<MapRoom> rooms) {
        this.meshList = meshList;
        this.rooms = rooms;
    }

        //
        // check for walls on platform segments
        //

    private boolean hasNegXWall(MapRoom room, int x, int z) {
        if (x == 0) {
            return (true);
        }
        return (room.getPlatformGrid((x - 1), z));
    }

    private boolean hasPosXWall(MapRoom room, int x, int z) {
        if (x >= (room.piece.sizeX - 1)) {
            return (true);
        }
        return (room.getPlatformGrid((x + 1), z));
    }

    private boolean hasNegZWall(MapRoom room, int x, int z) {
        if (z == 0) {
            return (true);
        }
        return (room.getPlatformGrid(x, (z - 1)));
    }

    private boolean hasPosZWall(MapRoom room, int x, int z) {
        if (z >= (room.piece.sizeZ - 1)) {
            return (false);
        }
        return (room.getPlatformGrid(x, (z + 1)));
    }

    private void knockOutPlatformSides(MapRoom room, MapRoom platformRoom) {
        int x, z, k;

        // can open up the side of the stairs facing
        // away from the steps
        switch (room.stairDir) {
            case MeshMapUtility.STAIR_DIR_POS_Z:
                k = platformRoom.touchesNegativeZ(rooms) ? 1 : 0;
                for (z = k; z <= (room.stairZ + 1); z++) {
                    room.setPlatformGridAcrossZ(z, true);
                }
                if ((platformRoom.touchesNegativeX(rooms)) || (AppWindow.random.nextBoolean())) {
                    room.setPlatformGridAcrossX(0, false);
                }
                if ((platformRoom.touchesPositiveX(rooms)) || (AppWindow.random.nextBoolean())) {
                    room.setPlatformGridAcrossX((room.piece.sizeX - 1), false);
                }
                break;
            case MeshMapUtility.STAIR_DIR_NEG_Z:
                k = platformRoom.touchesPositiveZ(rooms) ? (room.piece.sizeZ - 2) : (room.piece.sizeZ - 1);
                for (z = k; z >= (room.stairZ - 1); z--) {
                    room.setPlatformGridAcrossZ(z, true);
                }
                if ((platformRoom.touchesNegativeX(rooms)) || (AppWindow.random.nextBoolean())) {
                    room.setPlatformGridAcrossX(0, false);
                }
                if ((platformRoom.touchesPositiveX(rooms)) || (AppWindow.random.nextBoolean())) {
                    room.setPlatformGridAcrossX((room.piece.sizeX - 1), false);
                }
                break;
            case MeshMapUtility.STAIR_DIR_POS_X:
                k = platformRoom.touchesNegativeX(rooms) ? 1 : 0;
                for (x = k; x <= (room.stairX + 1); x++) {
                    room.setPlatformGridAcrossX(x, true);
                }
                if ((platformRoom.touchesNegativeZ(rooms)) || (AppWindow.random.nextBoolean())) {
                    room.setPlatformGridAcrossZ(0, false);
                }
                if ((platformRoom.touchesPositiveZ(rooms)) || (AppWindow.random.nextBoolean())) {
                    room.setPlatformGridAcrossZ((room.piece.sizeZ - 1), false);
                }
                break;
            case MeshMapUtility.STAIR_DIR_NEG_X:
                k = platformRoom.touchesPositiveX(rooms) ? (room.piece.sizeX - 2) : (room.piece.sizeX - 1);
                for (x = k; x >= (room.stairX - 1); x--) {
                    room.setPlatformGridAcrossX(x, true);
                }
                room.setPlatformGridAcrossZ(0, false);
                room.setPlatformGridAcrossZ((room.piece.sizeZ - 1), false);
                if ((platformRoom.touchesNegativeZ(rooms)) || (AppWindow.random.nextBoolean())) {
                    room.setPlatformGridAcrossZ(0, false);
                }
                if ((platformRoom.touchesPositiveZ(rooms)) || (AppWindow.random.nextBoolean())) {
                    room.setPlatformGridAcrossZ((room.piece.sizeZ - 1), false);
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
                if (room.getPlatformGrid(x, z)) {
                    continue;
                }

                    // create the segments
                ty = y + MapBuilder.FLOOR_HEIGHT;
                by = y;

                negX = (room.x + x) * MapBuilder.SEGMENT_SIZE;
                posX = (room.x + (x + 1)) * MapBuilder.SEGMENT_SIZE;
                negZ = (room.z + z) * MapBuilder.SEGMENT_SIZE;
                posZ = (room.z + (z + 1)) * MapBuilder.SEGMENT_SIZE;

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

        vertexes = MeshUtility.floatArrayListToFloat(vertexArray);
        normals = MeshUtility.floatArrayListToFloat(normalArray);
        indexes = MeshUtility.intArrayListToInt(indexArray);
        uvs = MeshUtility.buildUVs(vertexes, normals, (1.0f / MapBuilder.SEGMENT_SIZE));
        tangents = MeshUtility.buildTangents(vertexes, uvs, indexes);

        meshList.add(new Mesh(("platform_" + Integer.toString(roomNumber)), "platform", vertexes, normals, tangents, uvs, indexes));
    }

        //
        // second story mainline
        //

    public void build(MapRoom room, int roomNumber, boolean upper) {
        float y;

        if (upper) {
            y = MapBuilder.SEGMENT_SIZE + MapBuilder.FLOOR_HEIGHT;
        } else {
            y = -MapBuilder.FLOOR_HEIGHT;
        }

        if (AppWindow.random.nextBoolean()) {
            knockOutPlatformSides(room, (upper ? room : room.extendedFromRoom));
        }
        addPlatforms(room, roomNumber, y);
    }
}
