package com.klinksoftware.rag.map;

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

    private int roomNumber;
    private MeshList meshList;
    private MapRoom room;

    public MapPlatform(MeshList meshList, MapRoom room, int roomNumber) {
        this.meshList=meshList;
        this.room=room;
        this.roomNumber = roomNumber;
    }

        //
        // check for walls on platform segments
        //

    private boolean hasNegXWall(int x, int z) {
        if (x == 0) {
            return (true);
        }
        return (room.getPlatformGrid((x - 1), z));
    }

    private boolean hasPosXWall(int x, int z) {
        if (x >= (this.room.piece.sizeX - 1)) {
            return (true);
        }
        return (room.getPlatformGrid((x + 1), z));
    }

    private boolean hasNegZWall(int x, int z) {
        if (z == 0) {
            return (true);
        }
        return (room.getPlatformGrid(x, (z - 1)));
    }

    private boolean hasPosZWall(int x, int z) {
        if (z >= (this.room.piece.sizeZ - 1)) {
            return (false);
        }
        return (room.getPlatformGrid(x, (z + 1)));
    }

    private void setupRandomPlatforms(int startX,int startZ,int storyIdx)
    {
        /*
        int         x,z,gx,gz,sx,sz,
                    dir,orgDir;
        boolean     wallStop;

        gx=startX;
        gz=startZ;

            // start the random wander of segments

        while (true) {

                // next random direction

            dir=AppWindow.random.nextInt(4);
            orgDir=dir;

                // find open direction

            sx=sz=0;
            wallStop=false;

            while (true) {

                switch (dir) {
                    case PLATFORM_DIR_POS_Z:
                        sx=gx;
                        sz=gz+1;
                        break;
                    case PLATFORM_DIR_NEG_Z:
                        sx=gx;
                        sz=gz-1;
                        break;
                    case PLATFORM_DIR_POS_X:
                        sx=gx+1;
                        sz=gz;
                        break;
                    case PLATFORM_DIR_NEG_X:
                        sx=gx-1;
                        sz=gz;
                        break;
                }

                if ((room.getGrid(storyIdx,sx,sz)!=this.FLAG_NONE) || (sx<0) || (sx>=room.piece.size.x) || (sz<0) || (sz>=room.piece.size.z)) {
                    dir++;
                    if (dir==4) dir=0;
                    if (dir==orgDir) {
                        wallStop=true;
                        break;
                    }
                }
                else {
                    break;
                }
            }

            if (wallStop) break;

                // add grid spot

            room.setGrid(storyIdx,sx,sz,FLAG_PLATFORM);

            gx=sx;
            gz=sz;
        }

            // randomly make stripes where stories
            // become solid walls instead of floating blocks
            // only do it on first story

        if (storyIdx==1) {
            for (x=1;x<(room.piece.size.x-1);x++) {
                if (AppWindow.random.nextFloat()<0.2f) {
                    for (z=1;z<(room.piece.size.z-1);z++) {
                        if (room.getGrid(storyIdx,x,z)==FLAG_PLATFORM) room.setGrid(storyIdx,x,z,FLAG_WALL);
                    }
                }
            }

            for (z=1;z<(room.piece.size.z-1);z++) {
                if (AppWindow.random.nextFloat()<0.2f) {
                    for (x=1;x<(room.piece.size.x-1);x++) {
                        if (room.getGrid(storyIdx,x,z)==FLAG_PLATFORM) room.setGrid(storyIdx,x,z,FLAG_WALL);
                    }
                }
            }
        }
*/
    }

    private void addPlatforms(float y) {
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

                if (hasNegXWall(x, z)) {
                    vertexArray.addAll(Arrays.asList(negX,ty,negZ,negX,ty,posZ,negX,by,posZ,negX,by,negZ));
                    normalArray.addAll(Arrays.asList(-1.0f,0.0f,0.0f,-1.0f,0.0f,0.0f,-1.0f,0.0f,0.0f,-1.0f,0.0f,0.0f));
                    trigIdx=MeshMapUtility.addQuadToIndexes(indexArray,trigIdx);
                }

                if (hasPosXWall(x, z)) {
                    vertexArray.addAll(Arrays.asList(posX,ty,negZ,posX,ty,posZ,posX,by,posZ,posX,by,negZ));
                    normalArray.addAll(Arrays.asList(1.0f,0.0f,0.0f,1.0f,0.0f,0.0f,1.0f,0.0f,0.0f,1.0f,0.0f,0.0f));
                    trigIdx=MeshMapUtility.addQuadToIndexes(indexArray,trigIdx);
                }

                    // always draw the top

                vertexArray.addAll(Arrays.asList(negX,ty,negZ,negX,ty,posZ,posX,ty,posZ,posX,ty,negZ));
                normalArray.addAll(Arrays.asList(0.0f,1.0f,0.0f,0.0f,1.0f,0.0f,0.0f,1.0f,0.0f,0.0f,1.0f,0.0f));
                trigIdx=MeshMapUtility.addQuadToIndexes(indexArray,trigIdx);

                vertexArray.addAll(Arrays.asList(negX, by, negZ, negX, by, posZ, posX, by, posZ, posX, by, negZ));
                normalArray.addAll(Arrays.asList(0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f));
                trigIdx = MeshMapUtility.addQuadToIndexes(indexArray, trigIdx);

                if (hasNegZWall(x, z)) {
                    vertexArray.addAll(Arrays.asList(negX,ty,negZ,posX,ty,negZ,posX,by,negZ,negX,by,negZ));
                    normalArray.addAll(Arrays.asList(0.0f,0.0f,-1.0f,0.0f,0.0f,-1.0f,0.0f,0.0f,-1.0f,0.0f,0.0f,-1.0f));
                    trigIdx=MeshMapUtility.addQuadToIndexes(indexArray,trigIdx);
                }

                if (hasPosZWall(x, z)) {
                    vertexArray.addAll(Arrays.asList(negX,ty,posZ,posX,ty,posZ,posX,by,posZ,negX,by,posZ));
                    normalArray.addAll(Arrays.asList(0.0f,0.0f,1.0f,0.0f,0.0f,1.0f,0.0f,0.0f,1.0f,0.0f,0.0f,1.0f));
                    trigIdx=MeshMapUtility.addQuadToIndexes(indexArray,trigIdx);
                }
            }
        }

        vertexes=MeshMapUtility.floatArrayListToFloat(vertexArray);
        normals=MeshMapUtility.floatArrayListToFloat(normalArray);
        indexes=MeshMapUtility.intArrayListToInt(indexArray);
        uvs = MeshMapUtility.buildUVs(vertexes, normals, (1.0f / MapBuilder.SEGMENT_SIZE));
        tangents = MeshMapUtility.buildTangents(vertexes, uvs, indexes);

        meshList.add(new Mesh(("platform_" + Integer.toString(roomNumber)), "platform", vertexes, normals, tangents, uvs, indexes));
    }

        //
        // second story mainline
        //

    public void build(boolean upper) {
        float y;

        if (upper) {
            y = MapBuilder.SEGMENT_SIZE + MapBuilder.FLOOR_HEIGHT;
        } else {
            y = -MapBuilder.FLOOR_HEIGHT;
        }

        //    setupRandomPlatforms(x,z,1);
        addPlatforms(y);
    }
}
