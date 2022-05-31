package com.klinksoftware.rag.mesh;

import com.klinksoftware.rag.map.MapPiece;
import com.klinksoftware.rag.map.MapRoom;
import com.klinksoftware.rag.*;
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

    public static final int UV_WHOLE=0;
    public static final int UV_BOX=1;
    public static final int UV_MAP=2;

    public static final int CYLINDER_SIDE_COUNT = 16;

        //
        // build UVs for vertex lists
        //

    public static float[] buildUVs(float[] vertexes,float[] normals,float uvScale)
    {
        int                 n,k,nVertex,offset,minIntX,minIntY;
        float               x,y,ang;
        float[]             uvs;
        RagPoint            v,normal,mapUp;

        v=new RagPoint(0.0f,0.0f,0.0f);
        normal=new RagPoint(0.0f,0.0f,0.0f);

        nVertex=vertexes.length/3;

        uvs=new float[nVertex*2];

            // determine floor/wall like by
            // the dot product of the normal
            // and an up vector

        mapUp=new RagPoint(0.0f,1.0f,0.0f);

            // run through the vertices
            // remember, both this and normals
            // are packed arrays

        for (n=0;n!=nVertex;n++) {

            offset=n*3;
            v.x=vertexes[offset];
            v.y=vertexes[offset+1];
            v.z=vertexes[offset+2];

            normal.x=normals[offset];
            normal.y=normals[offset+1];
            normal.z=normals[offset+2];

            ang=mapUp.dot(normal);

                // wall like
                // use longest of x/z coordinates + Y coordinates of vertex

            if (Math.abs(ang)<=0.4f) {
                if (Math.abs(normal.x)<Math.abs(normal.z)) {
                    x=v.x;
                }
                else {
                    x=v.z;
                }
                y=v.y;
            }

                // floor/ceiling like
                // use x/z coordinates of vertex

            else {
                x=v.x;
                y=v.z;
            }

            offset=n*2;
            uvs[offset]=x*uvScale;
            uvs[offset+1]=1.0f-(y*uvScale);
        }

            // reduce all the UVs to
            // their minimum integers

        minIntX=(int)(uvs[0]);
        minIntY=(int)(uvs[1]);

        for (n=1;n!=nVertex;n++) {
            offset=n*2;

            k=(int)(uvs[offset]);
            if (k<minIntX) minIntX=k;
            k=(int)(uvs[offset+1]);
            if (k<minIntY) minIntY=k;
        }

        for (n=0;n!=nVertex;n++) {
            offset=n*2;
            uvs[offset]-=(float)minIntX;
            uvs[offset+1]-=(float)minIntY;
        }

        return(uvs);
    }

        //
        // build normals
        //

    public static float[] buildNormals(float[] vertexes,int[] indexes,RagPoint meshCenter,boolean normalsIn)
    {
        int             n,nTrig,trigIdx,offset;
        float[]         normals;
        boolean         flip;
        RagPoint        trigCenter,v0,v1,v2,
                        faceVct,p10,p20,normal;

        normals=new float[vertexes.length];

        trigCenter=new RagPoint(0.0f,0.0f,0.0f);
        faceVct=new RagPoint(0.0f,0.0f,0.0f);

            // generate normals by the trigs
            // sometimes we will end up overwriting
            // but it depends on the mesh to have
            // constant shared vertices against
            // triangle normals

        v0=new RagPoint(0.0f,0.0f,0.0f);
        v1=new RagPoint(0.0f,0.0f,0.0f);
        v2=new RagPoint(0.0f,0.0f,0.0f);
        p10=new RagPoint(0.0f,0.0f,0.0f);
        p20=new RagPoint(0.0f,0.0f,0.0f);
        normal=new RagPoint(0.0f,0.0f,0.0f);

        nTrig=indexes.length/3;

        for (n=0;n!=nTrig;n++) {

                // get the vertex indexes and
                // the vertexes for the trig

            trigIdx=n*3;

            offset=indexes[trigIdx]*3;
            v0.x=vertexes[offset];
            v0.y=vertexes[offset+1];
            v0.z=vertexes[offset+2];

            offset=indexes[trigIdx+1]*3;
            v1.x=vertexes[offset];
            v1.y=vertexes[offset+1];
            v1.z=vertexes[offset+2];

            offset=indexes[trigIdx+2]*3;
            v2.x=vertexes[offset];
            v2.y=vertexes[offset+1];
            v2.z=vertexes[offset+2];

                // create vectors and calculate the normal
                // by the cross product

            p10.x=v1.x-v0.x;
            p10.y=v1.y-v0.y;
            p10.z=v1.z-v0.z;
            p20.x=v2.x-v0.x;
            p20.y=v2.y-v0.y;
            p20.z=v2.z-v0.z;

            normal.x=(p10.y*p20.z)-(p10.z*p20.y);
            normal.y=(p10.z*p20.x)-(p10.x*p20.z);
            normal.z=(p10.x*p20.y)-(p10.y*p20.x);

            normal.normalize();

                // determine if we need to flip
                // we can use the dot product to tell
                // us if the normal is pointing
                // more towards the center or more
                // away from it

            trigCenter.x=((v0.x+v1.x+v2.x)*0.33f);
            trigCenter.y=((v0.y+v1.y+v2.y)*0.33f);
            trigCenter.z=((v0.z+v1.z+v2.z)*0.33f);

            faceVct.x=trigCenter.x-meshCenter.x;
            faceVct.y=trigCenter.y-meshCenter.y;
            faceVct.z=trigCenter.z-meshCenter.z;

            flip=(normal.dot(faceVct)>0.0f);
            if (!normalsIn) flip=!flip;

            if (flip) normal.scale(-1.0f);

                // and set the mesh normal
                // to all vertexes in this trig

            offset=indexes[trigIdx]*3;
            normals[offset]=normal.x;
            normals[offset+1]=normal.y;
            normals[offset+2]=normal.z;

            offset=indexes[trigIdx+1]*3;
            normals[offset]=normal.x;
            normals[offset+1]=normal.y;
            normals[offset+2]=normal.z;

            offset=indexes[trigIdx+2]*3;
            normals[offset]=normal.x;
            normals[offset+1]=normal.y;
            normals[offset+2]=normal.z;
        }

        return(normals);
    }

    public static float[] buildNormalsSimple(float[] vertexes,float x,float y,float z)
    {
        int             n,nVertex;
        float[]         normals;

        normals=new float[vertexes.length];

        nVertex=vertexes.length;

        for (n=0;n<nVertex;n+=3) {
            normals[n]=x;
            normals[n+1]=y;
            normals[n+2]=z;
        }

        return(normals);
    }

        //
        // build tangents
        //

    public static float[] buildTangents(float[] vertexes,float[] uvs,int[] indexes) {
        int n,nTrig,trigIdx,vIdx,uvIdx;
        float u10,u20,v10,v20,denom;
        float[] tangents;
        RagPoint v0,v1,v2,uv0,uv1,uv2,p10,p20,vLeft,vRight,vNum,tangent;

        tangents=new float[vertexes.length];

            // generate tangents by the trigs
            // sometimes we will end up overwriting
            // but it depends on the mesh to have
            // constant shared vertexes against
            // triangle tangents

        v0=new RagPoint(0.0f,0.0f,0.0f);
        v1=new RagPoint(0.0f,0.0f,0.0f);
        v2=new RagPoint(0.0f,0.0f,0.0f);
        uv0=new RagPoint(0.0f,0.0f,0.0f);
        uv1=new RagPoint(0.0f,0.0f,0.0f);
        uv2=new RagPoint(0.0f,0.0f,0.0f);
        p10=new RagPoint(0.0f,0.0f,0.0f);
        p20=new RagPoint(0.0f,0.0f,0.0f);
        vLeft=new RagPoint(0.0f,0.0f,0.0f);
        vRight=new RagPoint(0.0f,0.0f,0.0f);
        vNum=new RagPoint(0.0f,0.0f,0.0f);
        tangent=new RagPoint(0.0f,0.0f,0.0f);

        nTrig=indexes.length/3;

        for (n=0;n!=nTrig;n++) {

                // get the vertex indexes and
                // the vertexes for the trig

            trigIdx=n*3;

            vIdx=indexes[trigIdx]*3;
            v0.setFromValues(vertexes[vIdx],vertexes[vIdx+1],vertexes[vIdx+2]);
            vIdx=indexes[trigIdx+1]*3;
            v1.setFromValues(vertexes[vIdx],vertexes[vIdx+1],vertexes[vIdx+2]);
            vIdx=indexes[trigIdx+2]*3;
            v2.setFromValues(vertexes[vIdx],vertexes[vIdx+1],vertexes[vIdx+2]);

            uvIdx=indexes[trigIdx]*2;
            uv0.setFromValues(uvs[uvIdx],uvs[uvIdx+1],0.0f);
            uvIdx=indexes[trigIdx+1]*2;
            uv1.setFromValues(uvs[uvIdx],uvs[uvIdx+1],0.0f);
            uvIdx=indexes[trigIdx+2]*2;
            uv2.setFromValues(uvs[uvIdx],uvs[uvIdx+1],0.0f);

                // create vectors

            p10.setFromSubPoint(v1,v0);
            p20.setFromSubPoint(v2,v0);

                // get the UV scalars (u1-u0), (u2-u0), (v1-v0), (v2-v0)

            u10=uv1.x-uv0.x;        // x component
            u20=uv2.x-uv0.x;
            v10=uv1.y-uv0.y;        // y component
            v20=uv2.y-uv0.y;

                // calculate the tangent
                // (v20xp10)-(v10xp20) / (u10*v20)-(v10*u20)

            vLeft.setFromScale(p10,v20);
            vRight.setFromScale(p20,v10);
            vNum.setFromSubPoint(vLeft,vRight);

            denom=(u10*v20)-(v10*u20);
            if (denom!=0.0f) denom=1.0f/denom;
            tangent.setFromScale(vNum,denom);
            tangent.normalize();

                // and set the mesh tangent
                // to all vertexes in this trig

            vIdx=indexes[trigIdx]*3;
            tangents[vIdx]=tangent.x;
            tangents[vIdx+1]=tangent.y;
            tangents[vIdx+2]=tangent.z;
            vIdx=indexes[trigIdx+1]*3;
            tangents[vIdx]=tangent.x;
            tangents[vIdx+1]=tangent.y;
            tangents[vIdx+2]=tangent.z;
            vIdx=indexes[trigIdx+2]*3;
            tangents[vIdx]=tangent.x;
            tangents[vIdx+1]=tangent.y;
            tangents[vIdx+2]=tangent.z;
        }

        return(tangents);
    }

        //
        // mesh utilities
        //

    public static float[] floatArrayListToFloat(ArrayList<Float> list)
    {
        int         n,len;
        float[]     arr;

        len=list.size();

        arr=new float[len];

        for (n=0;n!=len;n++) {
            arr[n]=list.get(n);
        }

        return(arr);
    }

    public static int[] intArrayListToInt(ArrayList<Integer> list)
    {
        int         n,len;
        int[]       arr;

        len=list.size();

        arr=new int[len];

        for (n=0;n!=len;n++) {
            arr[n]=list.get(n);
        }

        return(arr);
    }

    public static int addTrigToIndexes(ArrayList<Integer> indexArray,int trigIdx)
    {
        indexArray.addAll(Arrays.asList(trigIdx,(trigIdx+1),(trigIdx+2)));
        return(trigIdx+3);
    }

    public static int addQuadToIndexes(ArrayList<Integer> indexArray,int trigIdx)
    {
        indexArray.addAll(Arrays.asList(trigIdx,(trigIdx+1),(trigIdx+2),trigIdx,(trigIdx+2),(trigIdx+3)));
        return(trigIdx+4);
    }

        //
        // room pieces
        //

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
                if (floor) {
                    return;
                }
                py = (MapBuilder.SEGMENT_SIZE * 2) + (MapBuilder.FLOOR_HEIGHT * 3);
                break;
            case MapRoom.ROOM_STORY_LOWER_EXTENSION:
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
                if (piece.floorGrid[(z * piece.sizeX) + x] != 1) {
                    //    continue;
                }

                px = (room.x + x) * MapBuilder.SEGMENT_SIZE;
                vertexArray.addAll(Arrays.asList(px,py,pz));
                vertexArray.addAll(Arrays.asList((px+MapBuilder.SEGMENT_SIZE),py,pz));
                vertexArray.addAll(Arrays.asList((px+MapBuilder.SEGMENT_SIZE),py,(pz+MapBuilder.SEGMENT_SIZE)));
                vertexArray.addAll(Arrays.asList(px,py,(pz+MapBuilder.SEGMENT_SIZE)));

                trigIdx = addQuadToIndexes(indexArray, trigIdx);
            }
        }

        if (trigIdx==0) return;

        vertexes=floatArrayListToFloat(vertexArray);
        indexes=intArrayListToInt(indexArray);
        normals=MeshMapUtility.buildNormalsSimple(vertexes,0.0f,(floor?1.0f:-1.0f),0.0f);
        uvs=MeshMapUtility.buildUVs(vertexes,normals,(1.0f/MapBuilder.SEGMENT_SIZE));
        tangents=MeshMapUtility.buildTangents(vertexes,uvs,indexes);

        if (floor) {
            meshList.add(new Mesh(("floor_" + Integer.toString(roomNumber)), (room.story == MapRoom.ROOM_STORY_MAIN ? "floor" : "floor_lower"), vertexes, normals, tangents, uvs, indexes));
        } else {
            meshList.add(new Mesh(("ceiling_" + Integer.toString(roomNumber)), (room.story == MapRoom.ROOM_STORY_MAIN ? "ceiling" : "ceiling_upper"), vertexes, normals, tangents, uvs, indexes));
        }
    }

    public static void buildRoomWalls(MeshList meshList, MapRoom room, RagPoint centerPnt, int roomNumber) {
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
                y = (MapBuilder.SEGMENT_SIZE + (MapBuilder.FLOOR_HEIGHT * 2));
                break;
            case MapRoom.ROOM_STORY_LOWER:
            case MapRoom.ROOM_STORY_LOWER_EXTENSION:
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
            trigIdx = addQuadToIndexes(indexArray, trigIdx);

            // small top wall (all versions have this, for doorway overheads)
            vertexArray.addAll(Arrays.asList(((piece.vertexes[n][0] + room.x) * MapBuilder.SEGMENT_SIZE), ((y + MapBuilder.SEGMENT_SIZE) + MapBuilder.FLOOR_HEIGHT), ((piece.vertexes[n][1] + room.z) * MapBuilder.SEGMENT_SIZE)));
            vertexArray.addAll(Arrays.asList(((piece.vertexes[n2][0] + room.x) * MapBuilder.SEGMENT_SIZE), ((y + MapBuilder.SEGMENT_SIZE) + MapBuilder.FLOOR_HEIGHT), ((piece.vertexes[n2][1] + room.z) * MapBuilder.SEGMENT_SIZE)));
            vertexArray.addAll(Arrays.asList(((piece.vertexes[n2][0] + room.x) * MapBuilder.SEGMENT_SIZE), (y + MapBuilder.SEGMENT_SIZE), ((piece.vertexes[n2][1] + room.z) * MapBuilder.SEGMENT_SIZE)));
            vertexArray.addAll(Arrays.asList(((piece.vertexes[n][0] + room.x) * MapBuilder.SEGMENT_SIZE), (y + MapBuilder.SEGMENT_SIZE), ((piece.vertexes[n][1] + room.z) * MapBuilder.SEGMENT_SIZE)));
            trigIdx = addQuadToIndexes(indexArray, trigIdx);
        }

        if (trigIdx != 0) {
            vertexes = floatArrayListToFloat(vertexArray);
            indexes = intArrayListToInt(indexArray);
            normals = MeshMapUtility.buildNormals(vertexes, indexes, centerPnt, true);
            uvs = MeshMapUtility.buildUVs(vertexes, normals, (1.0f / MapBuilder.SEGMENT_SIZE));
            tangents = MeshMapUtility.buildTangents(vertexes, uvs, indexes);

            switch (room.story) {
                case MapRoom.ROOM_STORY_MAIN:
                    meshList.add(new Mesh(("wall_main_" + Integer.toString(roomNumber)), "wall_main", vertexes, normals, tangents, uvs, indexes));
                    break;
                case MapRoom.ROOM_STORY_UPPER:
                case MapRoom.ROOM_STORY_UPPER_EXTENSION:
                    meshList.add(new Mesh(("wall_upper_" + Integer.toString(roomNumber)), "wall_upper", vertexes, normals, tangents, uvs, indexes));
                    break;
                case MapRoom.ROOM_STORY_LOWER:
                case MapRoom.ROOM_STORY_LOWER_EXTENSION:
                    meshList.add(new Mesh(("wall_lower_" + Integer.toString(roomNumber)), "wall_lower", vertexes, normals, tangents, uvs, indexes));
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
            if (room.story == MapRoom.ROOM_STORY_UPPER_EXTENSION) {
                vertexArray.addAll(Arrays.asList(((piece.vertexes[n][0] + room.x) * MapBuilder.SEGMENT_SIZE), (y - MapBuilder.FLOOR_HEIGHT), ((piece.vertexes[n][1] + room.z) * MapBuilder.SEGMENT_SIZE)));
                vertexArray.addAll(Arrays.asList(((piece.vertexes[n2][0] + room.x) * MapBuilder.SEGMENT_SIZE), (y - MapBuilder.FLOOR_HEIGHT), ((piece.vertexes[n2][1] + room.z) * MapBuilder.SEGMENT_SIZE)));
                vertexArray.addAll(Arrays.asList(((piece.vertexes[n2][0] + room.x) * MapBuilder.SEGMENT_SIZE), y, ((piece.vertexes[n2][1] + room.z) * MapBuilder.SEGMENT_SIZE)));
                vertexArray.addAll(Arrays.asList(((piece.vertexes[n][0] + room.x) * MapBuilder.SEGMENT_SIZE), y, ((piece.vertexes[n][1] + room.z) * MapBuilder.SEGMENT_SIZE)));
                trigIdx = addQuadToIndexes(indexArray, trigIdx);
            }

            // extra small top wall for lower rooms
            if (room.story == MapRoom.ROOM_STORY_LOWER_EXTENSION) {
                vertexArray.addAll(Arrays.asList(((piece.vertexes[n][0] + room.x) * MapBuilder.SEGMENT_SIZE), ((y + MapBuilder.SEGMENT_SIZE) + (MapBuilder.FLOOR_HEIGHT * 2)), ((piece.vertexes[n][1] + room.z) * MapBuilder.SEGMENT_SIZE)));
                vertexArray.addAll(Arrays.asList(((piece.vertexes[n2][0] + room.x) * MapBuilder.SEGMENT_SIZE), ((y + MapBuilder.SEGMENT_SIZE) + (MapBuilder.FLOOR_HEIGHT * 2)), ((piece.vertexes[n2][1] + room.z) * MapBuilder.SEGMENT_SIZE)));
                vertexArray.addAll(Arrays.asList(((piece.vertexes[n2][0] + room.x) * MapBuilder.SEGMENT_SIZE), ((y + MapBuilder.SEGMENT_SIZE) + MapBuilder.FLOOR_HEIGHT), ((piece.vertexes[n2][1] + room.z) * MapBuilder.SEGMENT_SIZE)));
                vertexArray.addAll(Arrays.asList(((piece.vertexes[n][0] + room.x) * MapBuilder.SEGMENT_SIZE), ((y + MapBuilder.SEGMENT_SIZE) + MapBuilder.FLOOR_HEIGHT), ((piece.vertexes[n][1] + room.z) * MapBuilder.SEGMENT_SIZE)));
                trigIdx = addQuadToIndexes(indexArray, trigIdx);
            }
        }

        if (trigIdx != 0) {
            vertexes = floatArrayListToFloat(vertexArray);
            indexes = intArrayListToInt(indexArray);
            normals = MeshMapUtility.buildNormals(vertexes, indexes, centerPnt, true);
            uvs = MeshMapUtility.buildUVs(vertexes, normals, (1.0f / MapBuilder.SEGMENT_SIZE));
            tangents = MeshMapUtility.buildTangents(vertexes, uvs, indexes);

            switch (room.story) {
                case MapRoom.ROOM_STORY_UPPER_EXTENSION:
                    meshList.add(new Mesh(("platform_upper_" + Integer.toString(roomNumber)), "platform", vertexes, normals, tangents, uvs, indexes));
                    break;
                case MapRoom.ROOM_STORY_LOWER_EXTENSION:
                    meshList.add(new Mesh(("platform_lower_" + Integer.toString(roomNumber)), "platform", vertexes, normals, tangents, uvs, indexes));
                    break;
            }
        }
    }

        //
        // staircases
        //

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
            trigIdx=addQuadToIndexes(indexArray,trigIdx);

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

            trigIdx=addQuadToIndexes(indexArray,trigIdx);

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

                trigIdx=addQuadToIndexes(indexArray,trigIdx);
                trigIdx=addQuadToIndexes(indexArray,trigIdx);
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

            trigIdx=addQuadToIndexes(indexArray,trigIdx);
        }

            // create the mesh

        vertexes=floatArrayListToFloat(vertexArray);
        indexes=intArrayListToInt(indexArray);
        normals=MeshMapUtility.buildNormals(vertexes,indexes,centerPnt,false);
        uvs = MeshMapUtility.buildUVs(vertexes, normals, (1.0f / MapBuilder.SEGMENT_SIZE));
        tangents = MeshMapUtility.buildTangents(vertexes, uvs, indexes);

        meshList.add(new Mesh(name, "platform", vertexes, normals, tangents, uvs, indexes));
    }

        //
        // cubes
        //

    public static Mesh createCubeRotated(MapRoom room,String name,String bitmapName,float xMin,float xMax,float yMin,float yMax,float zMin,float zMax,RagPoint rotAngle,boolean left,boolean right,boolean front,boolean back,boolean top,boolean bottom,boolean normalsIn,int uvMode)
    {
        int                 n,idx;
        ArrayList<Float>    vertexArray,uvArray;
        ArrayList<Integer>  indexArray;
        int[]               indexes;
        float[]             vertexes,normals,tangents,uvs;
        RagPoint            centerPnt,rotPnt;

            // allocate proper buffers

        vertexArray=new ArrayList<>();
        uvArray=new ArrayList<>();
        indexArray=new ArrayList<>();

            // box parts

        idx=0;

            // left

        if (left) {
            vertexArray.addAll(Arrays.asList(xMin,yMax,zMin));
            vertexArray.addAll(Arrays.asList(xMin,yMin,zMin));
            vertexArray.addAll(Arrays.asList(xMin,yMin,zMax));
            vertexArray.addAll(Arrays.asList(xMin,yMax,zMax));

            switch (uvMode) {
                case MeshMapUtility.UV_WHOLE:
                    uvArray.addAll(Arrays.asList(0.0f,0.0f,0.0f,1.0f,1.0f,1.0f,1.0f,0.0f));
                    break;
                case MeshMapUtility.UV_BOX:
                    uvArray.addAll(Arrays.asList(0.0f,0.0f,0.0f,0.499f,0.499f,0.499f,0.499f,0.0f));
                    break;
            }

            indexArray.addAll(Arrays.asList(idx,(idx+1),(idx+2),idx,(idx+2),(idx+3)));
            idx+=4;
        }

             // right

        if (right) {
            vertexArray.addAll(Arrays.asList(xMax,yMax,zMin));
            vertexArray.addAll(Arrays.asList(xMax,yMin,zMin));
            vertexArray.addAll(Arrays.asList(xMax,yMin,zMax));
            vertexArray.addAll(Arrays.asList(xMax,yMax,zMax));

            switch (uvMode) {
                case MeshMapUtility.UV_WHOLE:
                    uvArray.addAll(Arrays.asList(1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f));
                    break;
                case MeshMapUtility.UV_BOX:
                    uvArray.addAll(Arrays.asList(0.0f,0.499f,0.0f,0.0f,0.499f,0.0f,0.499f,0.499f));
                    break;
            }

            indexArray.addAll(Arrays.asList(idx,(idx+1),(idx+2),idx,(idx+2),(idx+3)));
            idx+=4;
        }

            // front

        if (front) {
            vertexArray.addAll(Arrays.asList(xMin,yMax,zMin));
            vertexArray.addAll(Arrays.asList(xMin,yMin,zMin));
            vertexArray.addAll(Arrays.asList(xMax,yMin,zMin));
            vertexArray.addAll(Arrays.asList(xMax,yMax,zMin));

            switch (uvMode) {
                case MeshMapUtility.UV_WHOLE:
                    uvArray.addAll(Arrays.asList(1.0f,0.0f,1.0f,1.0f,0.0f,1.0f,0.0f,0.0f));
                    break;
                case MeshMapUtility.UV_BOX:
                    uvArray.addAll(Arrays.asList(1.0f,0.0f,1.0f,0.499f,0.5f,0.499f,0.5f,0.0f));
                    break;
            }

            indexArray.addAll(Arrays.asList(idx,(idx+1),(idx+2),idx,(idx+2),(idx+3)));
            idx+=4;
        }

            // back

        if (back) {
            vertexArray.addAll(Arrays.asList(xMin,yMax,zMax));
            vertexArray.addAll(Arrays.asList(xMin,yMin,zMax));
            vertexArray.addAll(Arrays.asList(xMax,yMin,zMax));
            vertexArray.addAll(Arrays.asList(xMax,yMax,zMax));

            switch (uvMode) {
                case MeshMapUtility.UV_WHOLE:
                    uvArray.addAll(Arrays.asList(0.0f,0.0f,0.0f,1.0f,1.0f,1.0f,1.0f,0.0f));
                    break;
                case MeshMapUtility.UV_BOX:
                    uvArray.addAll(Arrays.asList(0.5f,0.0f,0.5f,0.499f,1.0f,0.499f,1.0f,0.0f));
                    break;
            }

            indexArray.addAll(Arrays.asList(idx,(idx+1),(idx+2),idx,(idx+2),(idx+3)));
            idx+=4;
        }

            // top

        if (top) {
            vertexArray.addAll(Arrays.asList(xMin,yMax,zMax));
            vertexArray.addAll(Arrays.asList(xMin,yMax,zMin));
            vertexArray.addAll(Arrays.asList(xMax,yMax,zMin));
            vertexArray.addAll(Arrays.asList(xMax,yMax,zMax));

            switch (uvMode) {
                case MeshMapUtility.UV_WHOLE:
                    uvArray.addAll(Arrays.asList(0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f));
                    break;
                case MeshMapUtility.UV_BOX:
                    uvArray.addAll(Arrays.asList(0.0f,0.499f,0.0f,1.0f,0.499f,1.0f,0.499f,0.499f));
                    break;
            }

            indexArray.addAll(Arrays.asList(idx,(idx+1),(idx+2),idx,(idx+2),(idx+3)));
            idx+=4;
        }

            // bottom

        if (bottom) {
            vertexArray.addAll(Arrays.asList(xMin,yMin,zMax));
            vertexArray.addAll(Arrays.asList(xMin,yMin,zMin));
            vertexArray.addAll(Arrays.asList(xMax,yMin,zMin));
            vertexArray.addAll(Arrays.asList(xMax,yMin,zMax));

            switch (uvMode) {
                case MeshMapUtility.UV_WHOLE:
                    uvArray.addAll(Arrays.asList(0.0f,0.0f,0.0f,1.0f,1.0f,1.0f,1.0f,0.0f));
                    break;
                case MeshMapUtility.UV_BOX:
                    uvArray.addAll(Arrays.asList(0.0f,0.499f,0.0f,1.0f,0.499f,1.0f,0.499f,0.499f));
                    break;
            }

            indexArray.addAll(Arrays.asList(idx,(idx+1),(idx+2),idx,(idx+2),(idx+3)));
            idx+=4;
        }

            // vertexes and indexes to arrays

        vertexes=floatArrayListToFloat(vertexArray);
        indexes=intArrayListToInt(indexArray);

            // rotate

        centerPnt=new RagPoint(((xMin+xMax)*0.5f),((yMin+yMax)*0.5f),((zMin+zMax)*0.5f));

        if (rotAngle!=null) {
            rotPnt=new RagPoint(0.0f,0.0f,0.0f);

            for (n=0;n<vertexes.length;n+=3) {
                rotPnt.setFromValues(vertexes[n],vertexes[n+1],vertexes[n+2]);
                rotPnt.rotateAroundPoint(centerPnt,rotAngle);
                vertexes[n]=rotPnt.x;
                vertexes[n+1]=rotPnt.y;
                vertexes[n+2]=rotPnt.z;
            }
        }

            // create the mesh

        normals = MeshMapUtility.buildNormals(vertexes, indexes, centerPnt, normalsIn);
        if (uvMode==MeshMapUtility.UV_MAP) {
            uvs=MeshMapUtility.buildUVs(vertexes,normals,(1.0f/MapBuilder.SEGMENT_SIZE));
        }
        else {
            uvs=floatArrayListToFloat(uvArray);
        }
        tangents=MeshMapUtility.buildTangents(vertexes,uvs,indexes);

        return(new Mesh(name,bitmapName,vertexes,normals,tangents,uvs,indexes));
    }

    public static Mesh createCube(MapRoom room,String name,String bitmapName,float xMin,float xMax,float yMin,float yMax,float zMin,float zMax,boolean left,boolean right,boolean front,boolean back,boolean top,boolean bottom,boolean normalsIn,int uvMode)
    {
        return(createCubeRotated(room,name,bitmapName,xMin,xMax,yMin,yMax,zMin,zMax,null,left,right,front,back,top,bottom,normalsIn,uvMode));
    }

        //
        // cylinders
        //

    public static float[] createCylinderSegmentList(int segmentCount, int segmentExtra) {
        int n, segCount;
        float[] segments;

        segCount=segmentCount+AppWindow.random.nextInt(segmentExtra);
        segments=new float[segCount+2];

        segments[0] = 1.0f; // top always biggest

        for (n=0;n!=segCount;n++) {
            segments[n + 1] = 0.8f + (AppWindow.random.nextFloat() * 0.2f);
        }

        segments[segCount + 1] = 1.0f; // and bottom

        return(segments);
    }

    public static Mesh createCylinder(MapRoom room,String name,String bitmapName,RagPoint centerPnt,float ty,float by,float[] segments,float radius,boolean addTop,boolean addBot)
    {
        int                 n,k,t,iIdx,vStartIdx,segCount;
        float               ang,ang2,angAdd,y,segTy,segBy,yAdd,
                            botRad,topRad,u1,u2,rd,
                            tx,tz,bx,bz,tx2,tz2,bx2,bz2;
        ArrayList<Float>    vertexArray,normalArray,uvArray;
        ArrayList<Integer>  indexArray;
        int[]               indexes;
        float[]             vertexes,normals,tangents,uvs;
        RagPoint            normal;

            // allocate arrays

        vertexArray=new ArrayList<>();
        normalArray=new ArrayList<>();
        uvArray=new ArrayList<>();
        indexArray=new ArrayList<>();

        normal=new RagPoint(0.0f,0.0f,0.0f);

            // make the cylinder

        iIdx=0;
        segCount=segments.length-1;

        angAdd=360.0f/(float)CYLINDER_SIDE_COUNT;
        yAdd=(ty-by)/(float)segCount;

        segBy=by;
        segTy=by+yAdd;

        botRad=segments[0]*radius;

        for (k=0;k!=segCount;k++) {

                // new radius

            topRad=segments[k+1]*radius;

                // cyliner faces

            ang=0.0f;

            for (n=0;n!=CYLINDER_SIDE_COUNT;n++) {
                ang2=ang+angAdd;

                    // the two Us

                u1=(ang*(float)segCount)/360.0f;
                u2=(ang2*(float)segCount)/360.0f;

                    // force last segment to wrap

                if (n==(CYLINDER_SIDE_COUNT-1)) ang2=0.0f;

                rd=ang*((float)Math.PI/180.0f);
                tx=centerPnt.x+((topRad*(float)Math.sin(rd))+(topRad*(float)Math.cos(rd)));
                tz=centerPnt.z+((topRad*(float)Math.cos(rd))-(topRad*(float)Math.sin(rd)));

                bx=centerPnt.x+((botRad*(float)Math.sin(rd))+(botRad*(float)Math.cos(rd)));
                bz=centerPnt.z+((botRad*(float)Math.cos(rd))-(botRad*(float)Math.sin(rd)));

                rd=ang2*((float)Math.PI/180.0f);
                tx2=centerPnt.x+((topRad*(float)Math.sin(rd))+(topRad*(float)Math.cos(rd)));
                tz2=centerPnt.z+((topRad*(float)Math.cos(rd))-(topRad*(float)Math.sin(rd)));

                bx2=centerPnt.x+((botRad*(float)Math.sin(rd))+(botRad*(float)Math.cos(rd)));
                bz2=centerPnt.z+((botRad*(float)Math.cos(rd))-(botRad*(float)Math.sin(rd)));

                    // the points

                vStartIdx=vertexArray.size();

                vertexArray.addAll(Arrays.asList(tx,segTy,tz));
                uvArray.addAll(Arrays.asList(u1,0.0f));
                indexArray.add(iIdx++);

                vertexArray.addAll(Arrays.asList(tx2,segTy,tz2));
                uvArray.addAll(Arrays.asList(u2,0.0f));
                indexArray.add(iIdx++);

                vertexArray.addAll(Arrays.asList(bx,segBy,bz));
                uvArray.addAll(Arrays.asList(u1,1.0f));
                indexArray.add(iIdx++);

                vertexArray.addAll(Arrays.asList(tx2,segTy,tz2));
                uvArray.addAll(Arrays.asList(u2,0.0f));
                indexArray.add(iIdx++);

                vertexArray.addAll(Arrays.asList(bx2,segBy,bz2));
                uvArray.addAll(Arrays.asList(u2,1.0f));
                indexArray.add(iIdx++);

                vertexArray.addAll(Arrays.asList(bx,segBy,bz));
                uvArray.addAll(Arrays.asList(u1,1.0f));
                indexArray.add(iIdx++);

                    // the normals

                y=(segTy+segBy)*0.5f;

                for (t=0;t!=6;t++) {
                    normal.x=vertexArray.get(vStartIdx++)-centerPnt.x;
                    normal.y=(vertexArray.get(vStartIdx++)-y)*0.25f;     // reduce the normal here so cylinders don't have heavy lighting
                    normal.z=vertexArray.get(vStartIdx++)-centerPnt.z;
                    normal.normalize();
                    normalArray.addAll(Arrays.asList(normal.x,normal.y,normal.z));
                }

                ang=ang2;
            }

            botRad=topRad;

            segBy=segTy;
            segTy=segBy+yAdd;
        }

            // top and bottom triangles

        if (addTop) {
            vStartIdx=vertexArray.size()/3;

            ang=0.0f;
            topRad=segments[0]*radius;

            for (n=0;n!=CYLINDER_SIDE_COUNT;n++) {
                rd=ang*((float)Math.PI/180.0f);

                u1=((float)Math.sin(rd)*0.5f)+0.5f;
                u2=((float)Math.cos(rd)*0.5f)+0.5f;

                tx=centerPnt.x+((topRad*(float)Math.sin(rd))+(topRad*(float)Math.cos(rd)));
                tz=centerPnt.z+((topRad*(float)Math.cos(rd))-(topRad*(float)Math.sin(rd)));

                    // the points

                vertexArray.addAll(Arrays.asList(tx,ty,tz));
                uvArray.addAll(Arrays.asList(u1,u2));
                normalArray.addAll(Arrays.asList(0.0f,1.0f,0.0f));

                ang+=angAdd;
            }

            for (n=0;n!=(CYLINDER_SIDE_COUNT-2);n++) {
                indexArray.add(vStartIdx);
                indexArray.add(vStartIdx+(n+1));
                indexArray.add(vStartIdx+(n+2));
            }
        }

        if (addBot) {
            vStartIdx=vertexArray.size()/3;

            ang=0.0f;
            botRad=segments[segments.length-1]*radius;

            for (n=0;n!=CYLINDER_SIDE_COUNT;n++) {
                rd=ang*((float)Math.PI/180.0f);

                u1=((float)Math.sin(rd)*0.5f)+0.5f;
                u2=((float)Math.cos(rd)*0.5f)+0.5f;

                bx=centerPnt.x+((botRad*(float)Math.sin(rd))+(botRad*(float)Math.cos(rd)));
                bz=centerPnt.z+((botRad*(float)Math.cos(rd))-(botRad*(float)Math.sin(rd)));

                    // the points

                vertexArray.addAll(Arrays.asList(bx,by,bz));
                uvArray.addAll(Arrays.asList(u1,u2));
                normalArray.addAll(Arrays.asList(0.0f,-1.0f,0.0f));

                ang+=angAdd;
            }

            for (n=0;n!=(CYLINDER_SIDE_COUNT-2);n++) {
                indexArray.add(vStartIdx);
                indexArray.add(vStartIdx+(n+1));
                indexArray.add(vStartIdx+(n+2));
            }
        }

            // create the mesh

        vertexes=floatArrayListToFloat(vertexArray);
        normals=floatArrayListToFloat(normalArray);
        uvs=floatArrayListToFloat(uvArray);
        indexes=intArrayListToInt(indexArray);
        tangents=MeshMapUtility.buildTangents(vertexes,uvs,indexes);

        return(new Mesh(name,bitmapName,vertexes,normals,tangents,uvs,indexes));
    }

    public static Mesh createMeshCylinderSimple(MapRoom room,String name,String bitmapName,RagPoint centerPnt,float ty,float by,float radius,boolean addTop,boolean addBot)
    {
        return(createCylinder(room,name,bitmapName,centerPnt,ty,by,new float[]{1.0f,1.0f},radius,addTop,addBot));
    }
}
