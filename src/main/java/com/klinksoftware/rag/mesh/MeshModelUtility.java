package com.klinksoftware.rag.mesh;

import static com.klinksoftware.rag.mesh.MeshMapUtility.floatArrayListToFloat;
import static com.klinksoftware.rag.mesh.MeshMapUtility.intArrayListToInt;
import com.klinksoftware.rag.skeleton.*;
import com.klinksoftware.rag.utility.*;

import java.util.*;

public class MeshModelUtility
{
    private static final int CYLINDER_ACROSS_SURFACE_COUNT = 3;
    private static final int CYLINDER_AROUND_SURFACE_COUNT = 24;

        //
        // build a cylinder around a limb
        //
    public static Mesh buildCylinderAroundLimb(String name, String bitmapName, int meshType, int axis, RagPoint meshScale, RagPoint botPnt, float botRadius, RagPoint topPnt, float topRadius) {
        int n, k, rowIdx, row2Idx, rowStartIdx, row2StartIdx, vIdx, vStartIdx, vOrigStartIdx;
        float ang, angAdd, acrossPos, acrossAdd;
        float tx, ty, tz;
        float rd, rAdd, rad;
        ArrayList<Float> vertexArray, normalArray, uvArray;
        ArrayList<Integer> indexArray;
        int[] indexes;
        float[] vertexes, normals, tangents, uvs;
        RagPoint normal, centerPnt;

        // allocate arrays
        vertexArray = new ArrayList<>();
        normalArray = new ArrayList<>();
        uvArray = new ArrayList<>();
        indexArray = new ArrayList<>();

        normal = new RagPoint(0.0f, 0.0f, 0.0f);
        centerPnt = new RagPoint(0.0f, 0.0f, 0.0f);

        // cylinder setup
        angAdd = 360.0f / (float) CYLINDER_AROUND_SURFACE_COUNT;

        switch (axis) {
            case Limb.LIMB_AXIS_X:
                acrossAdd = (topPnt.x - botPnt.x) / (float) CYLINDER_ACROSS_SURFACE_COUNT;
                acrossPos = botPnt.x;
                break;
            case Limb.LIMB_AXIS_Y:
                acrossAdd = (topPnt.y - botPnt.y) / (float) CYLINDER_ACROSS_SURFACE_COUNT;
                acrossPos = botPnt.y;
                break;
            default: // Z
                acrossAdd = (topPnt.z - botPnt.z) / (float) CYLINDER_ACROSS_SURFACE_COUNT;
                acrossPos = botPnt.z;
                break;
        }

        rad = botRadius;
        rAdd = (topRadius - botRadius) / (float) CYLINDER_ACROSS_SURFACE_COUNT;

        // the cylinder vertexes
        for (k = 0; k != (CYLINDER_ACROSS_SURFACE_COUNT + 1); k++) {

            // move center point along line
            centerPnt.x = botPnt.x + (((topPnt.x - botPnt.x) * (float) k) / (float) CYLINDER_ACROSS_SURFACE_COUNT);
            centerPnt.y = botPnt.y + (((topPnt.y - botPnt.y) * (float) k) / (float) CYLINDER_ACROSS_SURFACE_COUNT);
            centerPnt.z = botPnt.z + (((topPnt.z - botPnt.z) * (float) k) / (float) CYLINDER_ACROSS_SURFACE_COUNT);

            ang = 0.0f;

            for (n = 0; n != CYLINDER_AROUND_SURFACE_COUNT; n++) {

                // force last segment to wrap
                if (n == (CYLINDER_AROUND_SURFACE_COUNT - 1)) {
                    ang = 0.0f;
                }

                rd = ang * ((float) Math.PI / 180.0f);

                switch (axis) {
                    case Limb.LIMB_AXIS_X:
                        tx = acrossPos;
                        ty = centerPnt.y + (((rad * (float) Math.sin(rd)) + (rad * (float) Math.cos(rd))) * meshScale.y);
                        tz = centerPnt.z + (((rad * (float) Math.cos(rd)) - (rad * (float) Math.sin(rd))) * meshScale.z);
                        break;
                    case Limb.LIMB_AXIS_Y:
                        tx = centerPnt.x + (((rad * (float) Math.sin(rd)) + (rad * (float) Math.cos(rd))) * meshScale.x);
                        ty = acrossPos;
                        tz = centerPnt.z + (((rad * (float) Math.cos(rd)) - (rad * (float) Math.sin(rd))) * meshScale.z);
                        break;
                    default: // Z
                        tx = centerPnt.x + (((rad * (float) Math.sin(rd)) + (rad * (float) Math.cos(rd))) * meshScale.x);
                        ty = centerPnt.y + (((rad * (float) Math.cos(rd)) - (rad * (float) Math.sin(rd))) * meshScale.y);
                        tz = acrossPos;
                        break;
                }

                vertexArray.addAll(Arrays.asList(tx, ty, tz));
                normal.setFromValues((tx - centerPnt.x), (ty - centerPnt.y), (tz - centerPnt.z));
                normal.normalize();
                normalArray.addAll(Arrays.asList(normal.x, normal.y, normal.z));
                uvArray.addAll(Arrays.asList((ang / 360.0f), ((float) k / (float) CYLINDER_ACROSS_SURFACE_COUNT)));

                ang += angAdd;
            }

            rad += rAdd;
            acrossPos += acrossAdd;
        }

        // the cylinder triangles
        for (k = 0; k != CYLINDER_ACROSS_SURFACE_COUNT; k++) {

            rowIdx = rowStartIdx = k * CYLINDER_AROUND_SURFACE_COUNT;
            row2Idx = row2StartIdx = rowIdx + CYLINDER_AROUND_SURFACE_COUNT;

            for (n = 0; n != CYLINDER_AROUND_SURFACE_COUNT; n++) {

                if (n == (CYLINDER_AROUND_SURFACE_COUNT - 1)) {
                    indexArray.add(rowIdx);
                    indexArray.add(rowStartIdx);
                    indexArray.add(row2Idx);
                    indexArray.add(rowStartIdx);
                    indexArray.add(row2StartIdx);
                    indexArray.add(row2Idx);
                } else {
                    indexArray.add(rowIdx);
                    indexArray.add(rowIdx + 1);
                    indexArray.add(row2Idx);
                    indexArray.add(rowIdx + 1);
                    indexArray.add(row2Idx + 1);
                    indexArray.add(row2Idx);
                }

                rowIdx++;
                row2Idx++;
            }
        }

        // top close
        if ((meshType == Limb.MESH_TYPE_CYLINDER_CLOSE_ALL) || (meshType == Limb.MESH_TYPE_CYLINDER_CLOSE_TOP)) {
            vIdx = vertexArray.size() / 3;
            vStartIdx = vOrigStartIdx = CYLINDER_AROUND_SURFACE_COUNT * (CYLINDER_ACROSS_SURFACE_COUNT + 0);

            // middle vertex
            vertexArray.addAll(Arrays.asList(topPnt.x, topPnt.y, topPnt.z));
            normal.setFromValues((topPnt.x - botPnt.x), (topPnt.y - botPnt.y), (topPnt.z - botPnt.z));
            normal.normalize();
            normalArray.addAll(Arrays.asList(normal.x, normal.y, normal.z));
            uvArray.addAll(Arrays.asList(0.5f, 0.5f));

            for (n = 0; n != (CYLINDER_AROUND_SURFACE_COUNT - 1); n++) {
                indexArray.add(vStartIdx);
                indexArray.add((n == (CYLINDER_AROUND_SURFACE_COUNT - 2)) ? vOrigStartIdx : (vStartIdx + 1));
                indexArray.add(vIdx);

                vStartIdx++;
            }
        }

        if ((meshType == Limb.MESH_TYPE_CYLINDER_CLOSE_ALL) || (meshType == Limb.MESH_TYPE_CYLINDER_CLOSE_BOTTOM)) {
            vIdx = vertexArray.size() / 3;
            vStartIdx = 0;

            // middle vertex
            vertexArray.addAll(Arrays.asList(botPnt.x, botPnt.y, botPnt.z));
            normal.setFromValues((botPnt.x - topPnt.x), (botPnt.y - topPnt.y), (botPnt.z - topPnt.z));
            normal.normalize();
            normalArray.addAll(Arrays.asList(normal.x, normal.y, normal.z));
            uvArray.addAll(Arrays.asList(0.5f, 0.5f));

            for (n = 0; n != (CYLINDER_AROUND_SURFACE_COUNT - 1); n++) {
                indexArray.add(vStartIdx);
                indexArray.add((n == (CYLINDER_AROUND_SURFACE_COUNT - 2)) ? 0 : (vStartIdx + 1));
                indexArray.add(vIdx);

                vStartIdx++;
            }
        }

        // create the mesh
        vertexes = floatArrayListToFloat(vertexArray);
        normals = floatArrayListToFloat(normalArray);
        uvs = floatArrayListToFloat(uvArray);
        indexes = intArrayListToInt(indexArray);
        tangents = MeshMapUtility.buildTangents(vertexes, uvs, indexes);

        return (new Mesh(name, bitmapName, vertexes, normals, tangents, uvs, indexes));
    }

    //
        // no vertexes can extend past floor, everything
        // there is flat
        //

    private static void clipFloorVertexes(Mesh mesh)
    {
        int         n,vIdx,nVertex;

        nVertex=mesh.vertexes.length/3;

        for (n=0;n!=nVertex;n++) {
            vIdx=n*3;
            if (mesh.vertexes[vIdx+1]<0) mesh.vertexes[vIdx+1]=0.0f;
        }
    }

        //
        // rebuild the normal by finding the nearest bone
        // and tracing the normal from there
        //

    private static void rebuildNormals(Mesh mesh, Bone bone1, Bone bone2)    {
        int n, vIdx, nVertex;
        float d, d2;
        RagPoint    pnt;

        nVertex=mesh.vertexes.length/3;

        pnt=new RagPoint(0.0f,0.0f,0.0f);

        for (n=0;n!=nVertex;n++) {
            vIdx=n*3;

            pnt.x=mesh.vertexes[vIdx];
            pnt.y=mesh.vertexes[vIdx+1];
            pnt.z=mesh.vertexes[vIdx+2];

                // find closest bone
            d = bone1.pnt.distance(pnt);
            d2 = bone2.pnt.distance(pnt);

                // get normal

            pnt.subPoint((d < d2) ? bone1.pnt : bone2.pnt);
            pnt.normalize();

            mesh.normals[vIdx]=pnt.x;
            mesh.normals[vIdx+1]=pnt.y;
            mesh.normals[vIdx+2]=pnt.z;
        }
    }

        //
        // build mesh around limb
        //

    public static Mesh buildMeshAroundBoneLimb(Skeleton skeleton,Limb limb)
    {
        Bone bone1, bone2;
        Mesh mesh;

        bone1 = skeleton.bones.get(limb.bone1Idx);
        bone2 = skeleton.bones.get(limb.bone2Idx);

        // build the cylinder around the bones
        // todo -- different uv mapping here
        mesh = buildCylinderAroundLimb(limb.name, "bitmap", limb.meshType, limb.axis, limb.scale, bone1.pnt, bone1.radius, bone2.pnt, bone2.radius);

        clipFloorVertexes(mesh);
        rebuildNormals(mesh, bone1, bone2);

        return(mesh);
    }

}
