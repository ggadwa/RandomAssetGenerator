package com.klinksoftware.rag.model.utility;

import com.klinksoftware.rag.model.utility.Limb;
import com.klinksoftware.rag.utility.MeshUtility;
import com.klinksoftware.rag.scene.Mesh;
import com.klinksoftware.rag.scene.Scene;
import com.klinksoftware.rag.utility.*;
import com.klinksoftware.rag.utility.MeshUtility;
import com.klinksoftware.rag.utility.RagPoint;

import java.util.*;

public class MeshModelUtility
{
    private static final int CYLINDER_ORGANIC_ACROSS_SURFACE_COUNT = 3;
    private static final int CYLINDER_ORGANIC_AROUND_SURFACE_COUNT = 24;
    private static final int CYLINDER_MECHANICAL_ACROSS_SURFACE_COUNT = 1;
    private static final int CYLINDER_MECHANICAL_AROUND_SURFACE_COUNT = 4;

    // build a cylinder around a limb
    public static Mesh buildCylinderAroundLimb(String name, String bitmapName, int meshType, int axis, RagPoint meshScale, RagPoint botPnt, float botRadius, RagPoint topPnt, float topRadius, int aroundCount, int acrossCount) {
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
            u = 0.0f;
            uAdd = 1.0f / (float) (aroundCount + 1);

            // we need to dupliacte the seam so the uvs work
            for (n = 0; n != (aroundCount + 1); n++) {

                if (n == aroundCount) {
                    ang = 0.0f;
                }

                // this is here to make square cylinders face forward
                rd = (ang - 45.0f) * ((float) Math.PI / 180.0f);

                switch (axis) {
                    case Limb.LIMB_AXIS_X:
                        tx = acrossPos;
                        ty = centerPnt.y + ((rad * (float) Math.cos(rd)) * meshScale.y);
                        tz = centerPnt.z + ((rad * (float) Math.sin(rd)) * meshScale.z);
                        break;
                    case Limb.LIMB_AXIS_Y:
                        tx = centerPnt.x + ((rad * (float) Math.cos(rd)) * meshScale.x);
                        ty = acrossPos;
                        tz = centerPnt.z + ((rad * (float) Math.sin(rd)) * meshScale.z);
                        break;
                    default: // Z
                        tx = centerPnt.x + ((rad * (float) Math.cos(rd)) * meshScale.x);
                        ty = centerPnt.y + ((rad * (float) Math.sin(rd)) * meshScale.y);
                        tz = acrossPos;
                        break;
                }

                vertexArray.addAll(Arrays.asList(tx, ty, tz));
                normal.setFromValues((tx - centerPnt.x), (ty - centerPnt.y), (tz - centerPnt.z));
                normal.normalize();
                normalArray.addAll(Arrays.asList(normal.x, normal.y, normal.z));
                uvArray.addAll(Arrays.asList(u, ((float) k / (float) acrossCount)));

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
            uvArray.addAll(Arrays.asList(0.5f, 0.0f));

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
            uvArray.addAll(Arrays.asList(0.5f, 1.0f));

            for (n = 0; n != aroundCount; n++) {
                indexArray.add(vStartIdx);
                indexArray.add(vStartIdx + 1);
                indexArray.add(vIdx);

                vStartIdx++;
            }
        }

        // create the mesh
        vertexes = MeshUtility.floatArrayListToFloat(vertexArray);
        normals = MeshUtility.floatArrayListToFloat(normalArray);
        uvs = MeshUtility.floatArrayListToFloat(uvArray);
        indexes = MeshUtility.intArrayListToInt(indexArray);
        tangents = MeshUtility.buildTangents(vertexes, uvs, indexes);

        return (new Mesh(name, bitmapName, vertexes, normals, tangents, uvs, indexes));
    }

    // rebuild the normal by finding the nearest node point
    // and tracing the normal from there
    private static void rebuildNormals(Mesh mesh, RagPoint pnt1, RagPoint pnt2) {
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

            // find closest point
            d = pnt1.distance(pnt);
            d2 = pnt2.distance(pnt);

            // get normal
            pnt.subPoint((d < d2) ? pnt1 : pnt2);
            pnt.normalize();

            mesh.normals[vIdx]=pnt.x;
            mesh.normals[vIdx+1]=pnt.y;
            mesh.normals[vIdx+2]=pnt.z;
        }
    }

    // build mesh around limb
    public static Mesh buildMeshAroundNodeLimb(Scene scene, Limb limb, boolean organic) {
        RagPoint absPnt1, absPnt2;
        Mesh mesh;

        // globe limbs
        if (limb.meshType == Limb.MESH_TYPE_GLOBE) {
            if (organic) {
                mesh = MeshUtility.createGlobe(limb.name, limb.bitmapName, limb.node1.getAbsolutePoint(), limb.globeRadius, limb.globeRotAngle);
            } else {
                mesh = MeshUtility.createCubeSimple(limb.name, limb.bitmapName, limb.node1.getAbsolutePoint(), limb.globeRadius);
            }

            mesh.clipFloorVertexes();

            return (mesh);
        }

        // cylinder limbs
        absPnt1 = limb.node1.getAbsolutePoint();
        absPnt2 = limb.node2.getAbsolutePoint();

        // build the cylinder around the nodes
        if (organic) {
            mesh = buildCylinderAroundLimb(limb.name, limb.bitmapName, limb.meshType, limb.axis, limb.scale, absPnt1, limb.radius1, absPnt2, limb.radius2, CYLINDER_ORGANIC_AROUND_SURFACE_COUNT, CYLINDER_ORGANIC_ACROSS_SURFACE_COUNT);
        } else {
            mesh = buildCylinderAroundLimb(limb.name, limb.bitmapName, limb.meshType, limb.axis, limb.scale, absPnt1, limb.radius1, absPnt2, limb.radius2, CYLINDER_MECHANICAL_AROUND_SURFACE_COUNT, CYLINDER_MECHANICAL_ACROSS_SURFACE_COUNT);
        }

        rebuildNormals(mesh, absPnt1, absPnt2);
        mesh.clipFloorVertexes();

        return (mesh);
    }

}
