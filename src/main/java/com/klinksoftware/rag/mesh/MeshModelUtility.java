package com.klinksoftware.rag.mesh;

import com.klinksoftware.rag.SettingsModel;
import static com.klinksoftware.rag.mesh.MeshMapUtility.floatArrayListToFloat;
import static com.klinksoftware.rag.mesh.MeshMapUtility.intArrayListToInt;
import com.klinksoftware.rag.skeleton.*;
import com.klinksoftware.rag.utility.*;

import java.util.*;

public class MeshModelUtility
{
    private static final int CYLINDER_ORGANIC_ACROSS_SURFACE_COUNT = 3;
    private static final int CYLINDER_ORGANIC_AROUND_SURFACE_COUNT = 24;
    private static final int CYLINDER_MECHANICAL_ACROSS_SURFACE_COUNT = 1;
    private static final int CYLINDER_MECHANICAL_AROUND_SURFACE_COUNT = 4;

        //
        // build a cylinder around a limb
        //
    public static Mesh buildCylinderAroundLimb(String name, String bitmapName, int meshType, int axis, RagPoint meshScale, RagPoint botPnt, float botRadius, RagPoint topPnt, float topRadius, int aroundCount, int acrossCount, float uOffset, float vOffset, float uSize, float vSize) {
        int n, k, rowIdx, row2Idx, vIdx, vStartIdx;
        float ang, angAdd, acrossPos, acrossAdd;
        float tx, ty, tz;
        float rd, rAdd, rad, u, uAdd;
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
        angAdd = 360.0f / (float) aroundCount;

        switch (axis) {
            case Limb.LIMB_AXIS_X:
                acrossAdd = (topPnt.x - botPnt.x) / (float) acrossCount;
                acrossPos = botPnt.x;
                break;
            case Limb.LIMB_AXIS_Y:
                acrossAdd = (topPnt.y - botPnt.y) / (float) acrossCount;
                acrossPos = botPnt.y;
                break;
            default: // Z
                acrossAdd = (topPnt.z - botPnt.z) / (float) acrossCount;
                acrossPos = botPnt.z;
                break;
        }

        rad = botRadius;
        rAdd = (topRadius - botRadius) / (float) acrossCount;

        // the cylinder vertexes
        for (k = 0; k != (acrossCount + 1); k++) {

            // move center point along line
            centerPnt.x = botPnt.x + (((topPnt.x - botPnt.x) * (float) k) / (float) acrossCount);
            centerPnt.y = botPnt.y + (((topPnt.y - botPnt.y) * (float) k) / (float) acrossCount);
            centerPnt.z = botPnt.z + (((topPnt.z - botPnt.z) * (float) k) / (float) acrossCount);

            ang = 0.0f;
            u = uOffset;
            uAdd = uSize / (float) (aroundCount + 1);

            // we need to dupliacte the seam so the uvs work
            for (n = 0; n != (aroundCount + 1); n++) {

                if (n == aroundCount) {
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
                uvArray.addAll(Arrays.asList(u, ((((float) k / (float) acrossCount)) * vSize) + vOffset));

                ang += angAdd;
                u += uAdd;
            }

            rad += rAdd;
            acrossPos += acrossAdd;
        }

        // the cylinder triangles
        for (k = 0; k != acrossCount; k++) {

            rowIdx = k * (aroundCount + 1);
            row2Idx = rowIdx + (aroundCount + 1);

            for (n = 0; n != aroundCount; n++) {
                indexArray.add(rowIdx);
                indexArray.add(rowIdx + 1);
                indexArray.add(row2Idx);
                indexArray.add(rowIdx + 1);
                indexArray.add(row2Idx + 1);
                indexArray.add(row2Idx);

                rowIdx++;
                row2Idx++;
            }
        }

        // top close
        if ((meshType == Limb.MESH_TYPE_CYLINDER_CLOSE_ALL) || (meshType == Limb.MESH_TYPE_CYLINDER_CLOSE_TOP)) {
            vIdx = vertexArray.size() / 3;
            vStartIdx = (aroundCount + 1) * acrossCount;

            // middle vertex
            vertexArray.addAll(Arrays.asList(topPnt.x, topPnt.y, topPnt.z));
            normal.setFromValues((topPnt.x - botPnt.x), (topPnt.y - botPnt.y), (topPnt.z - botPnt.z));
            normal.normalize();
            normalArray.addAll(Arrays.asList(normal.x, normal.y, normal.z));
            uvArray.addAll(Arrays.asList((uOffset + (uSize * 0.5f)), vOffset));

            for (n = 0; n != aroundCount; n++) {
                indexArray.add(vStartIdx);
                indexArray.add(vStartIdx + 1);
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
            uvArray.addAll(Arrays.asList((uOffset + (uSize * 0.5f)), (vOffset + vSize)));

            for (n = 0; n != aroundCount; n++) {
                indexArray.add(vStartIdx);
                indexArray.add(vStartIdx + 1);
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
        // rebuild the normal by finding the nearest bone
        // and tracing the normal from there
        //

    private static void rebuildNormals(Mesh mesh, Bone bone1, Bone bone2)    {
        int n, vIdx, nVertex;
        float d, d2;
        RagPoint pnt;

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

    public static Mesh buildMeshAroundBoneLimb(Skeleton skeleton, int modelType, Limb limb) {
        Bone bone1, bone2;
        Mesh mesh;

        bone1 = skeleton.bones.get(limb.bone1Idx);
        bone2 = skeleton.bones.get(limb.bone2Idx);

        // build the cylinder around the bones
        if (modelType != SettingsModel.MODEL_TYPE_ROBOT) {
            mesh = buildCylinderAroundLimb(limb.name, "bitmap", limb.meshType, limb.axis, limb.scale, bone1.pnt, bone1.radius, bone2.pnt, bone2.radius, CYLINDER_ORGANIC_AROUND_SURFACE_COUNT, CYLINDER_ORGANIC_ACROSS_SURFACE_COUNT, limb.uOffset, limb.vOffset, limb.uSize, limb.vSize);
        } else {
            mesh = buildCylinderAroundLimb(limb.name, "bitmap", limb.meshType, limb.axis, limb.scale, bone1.pnt, bone1.radius, bone2.pnt, bone2.radius, CYLINDER_MECHANICAL_AROUND_SURFACE_COUNT, CYLINDER_MECHANICAL_ACROSS_SURFACE_COUNT, limb.uOffset, limb.vOffset, limb.uSize, limb.vSize);
        }

        rebuildNormals(mesh, bone1, bone2);
        mesh.clipFloorVertexes();

        return(mesh);
    }

}
