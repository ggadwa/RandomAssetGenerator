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
    public static Mesh buildCylinderAroundLimb(String name, String bitmapName, int meshType, int axis, RagPoint meshScale, RagPoint topPnt, float topRadius, RagPoint botPnt, float botRadius) {
        int n, k, iIdx, vStartIdx, vIdx;
        float ang, ang2, angAdd, acrossTop, acrossBot, acrossAdd;
        float u1, u2, v1, v2;
        float tx, ty, tz, tx2, ty2, tz2, bx, by, bz, bx2, by2, bz2;
        float rd, rd2, rAdd, botRad, topRad;
        ArrayList<Float> vertexArray, normalArray, uvArray;
        ArrayList<Integer> indexArray;
        int[] indexes;
        float[] vertexes, normals, tangents, uvs;
        RagPoint normal, centerPntTop, centerPntBot;

        // allocate arrays
        vertexArray = new ArrayList<>();
        normalArray = new ArrayList<>();
        uvArray = new ArrayList<>();
        indexArray = new ArrayList<>();

        normal = new RagPoint(0.0f, 0.0f, 0.0f);
        centerPntTop = new RagPoint(0.0f, 0.0f, 0.0f);
        centerPntBot = new RagPoint(0.0f, 0.0f, 0.0f);

        // cylinder setup
        angAdd = 360.0f / (float) CYLINDER_AROUND_SURFACE_COUNT;

        switch (axis) {
            case Limb.LIMB_AXIS_X:
                acrossAdd = (topPnt.x - botPnt.x) / (float) CYLINDER_ACROSS_SURFACE_COUNT;
                acrossBot = botPnt.x;
                acrossTop = botPnt.x + acrossAdd;
                break;
            case Limb.LIMB_AXIS_Y:
                acrossAdd = (topPnt.y - botPnt.y) / (float) CYLINDER_ACROSS_SURFACE_COUNT;
                acrossBot = botPnt.y;
                acrossTop = botPnt.y + acrossAdd;
                break;
            default: // Z
                acrossAdd = (topPnt.z - botPnt.z) / (float) CYLINDER_ACROSS_SURFACE_COUNT;
                acrossBot = botPnt.z;
                acrossTop = botPnt.z + acrossAdd;
                break;
        }

        rAdd = (topRadius - botRadius) / (float) CYLINDER_ACROSS_SURFACE_COUNT;
        botRad = botRadius;
        topRad = botRad + rAdd;

        // make the cylinder
        iIdx = 0;

        for (k = 0; k != CYLINDER_ACROSS_SURFACE_COUNT; k++) {

            // move center point along line
            centerPntBot.x = botPnt.x + (((topPnt.x - botPnt.x) * (float) k) / (float) CYLINDER_ACROSS_SURFACE_COUNT);
            centerPntBot.y = botPnt.y + (((topPnt.y - botPnt.y) * (float) k) / (float) CYLINDER_ACROSS_SURFACE_COUNT);
            centerPntBot.z = botPnt.z + (((topPnt.z - botPnt.z) * (float) k) / (float) CYLINDER_ACROSS_SURFACE_COUNT);

            centerPntTop.x = botPnt.x + (((topPnt.x - botPnt.x) * (float) (k + 1)) / (float) CYLINDER_ACROSS_SURFACE_COUNT);
            centerPntTop.y = botPnt.y + (((topPnt.y - botPnt.y) * (float) (k + 1)) / (float) CYLINDER_ACROSS_SURFACE_COUNT);
            centerPntTop.z = botPnt.z + (((topPnt.z - botPnt.z) * (float) (k + 1)) / (float) CYLINDER_ACROSS_SURFACE_COUNT);

            // the vs
            v1 = (float) k / (float) CYLINDER_ACROSS_SURFACE_COUNT;
            v2 = (k == (CYLINDER_ACROSS_SURFACE_COUNT - 1)) ? 1.0f : ((float) (k + 1) / (float) CYLINDER_ACROSS_SURFACE_COUNT);

            // cyliner faces
            ang = 0.0f;

            for (n = 0; n != CYLINDER_AROUND_SURFACE_COUNT; n++) {
                ang2 = ang + angAdd;

                // the two Us
                u1 = ang / 360.0f;
                u2 = ang2 / 360.0f;

                // force last segment to wrap
                if (n == (CYLINDER_AROUND_SURFACE_COUNT - 1)) {
                    ang2 = 0.0f;
                }

                rd = ang * ((float) Math.PI / 180.0f);
                rd2 = ang2 * ((float) Math.PI / 180.0f);

                switch (axis) {
                    case Limb.LIMB_AXIS_X:
                        tx = acrossTop;
                        ty = centerPntTop.y + (((topRad * (float) Math.sin(rd)) + (topRad * (float) Math.cos(rd))) * meshScale.y);
                        tz = centerPntTop.z + (((topRad * (float) Math.cos(rd)) - (topRad * (float) Math.sin(rd))) * meshScale.z);
                        bx = acrossBot;
                        by = centerPntBot.y + (((botRad * (float) Math.sin(rd)) + (botRad * (float) Math.cos(rd))) * meshScale.y);
                        bz = centerPntBot.z + (((botRad * (float) Math.cos(rd)) - (botRad * (float) Math.sin(rd))) * meshScale.z);
                        tx2 = acrossTop;
                        ty2 = centerPntTop.y + (((topRad * (float) Math.sin(rd2)) + (topRad * (float) Math.cos(rd2))) * meshScale.y);
                        tz2 = centerPntTop.z + (((topRad * (float) Math.cos(rd2)) - (topRad * (float) Math.sin(rd2))) * meshScale.z);
                        bx2 = acrossBot;
                        by2 = centerPntBot.y + (((botRad * (float) Math.sin(rd2)) + (botRad * (float) Math.cos(rd2))) * meshScale.y);
                        bz2 = centerPntBot.z + (((botRad * (float) Math.cos(rd2)) - (botRad * (float) Math.sin(rd2))) * meshScale.z);
                        break;
                    case Limb.LIMB_AXIS_Y:
                        tx = centerPntTop.x + (((topRad * (float) Math.sin(rd)) + (topRad * (float) Math.cos(rd))) * meshScale.x);
                        ty = acrossTop;
                        tz = centerPntTop.z + (((topRad * (float) Math.cos(rd)) - (topRad * (float) Math.sin(rd))) * meshScale.z);
                        bx = centerPntBot.x + (((botRad * (float) Math.sin(rd)) + (botRad * (float) Math.cos(rd))) * meshScale.x);
                        by = acrossBot;
                        bz = centerPntBot.z + (((botRad * (float) Math.cos(rd)) - (botRad * (float) Math.sin(rd))) * meshScale.z);
                        tx2 = centerPntTop.x + (((topRad * (float) Math.sin(rd2)) + (topRad * (float) Math.cos(rd2))) * meshScale.x);
                        ty2 = acrossTop;
                        tz2 = centerPntTop.z + (((topRad * (float) Math.cos(rd2)) - (topRad * (float) Math.sin(rd2))) * meshScale.z);
                        bx2 = centerPntBot.x + (((botRad * (float) Math.sin(rd2)) + (botRad * (float) Math.cos(rd2))) * meshScale.x);
                        by2 = acrossBot;
                        bz2 = centerPntBot.z + (((botRad * (float) Math.cos(rd2)) - (botRad * (float) Math.sin(rd2))) * meshScale.z);
                        break;
                    default: // Z
                        tx = centerPntTop.x + (((topRad * (float) Math.sin(rd)) + (topRad * (float) Math.cos(rd))) * meshScale.x);
                        ty = centerPntTop.y + (((topRad * (float) Math.cos(rd)) - (topRad * (float) Math.sin(rd))) * meshScale.y);
                        tz = acrossTop;
                        bx = centerPntBot.x + (((botRad * (float) Math.sin(rd)) + (botRad * (float) Math.cos(rd))) * meshScale.x);
                        by = centerPntBot.y + (((botRad * (float) Math.cos(rd)) - (botRad * (float) Math.sin(rd))) * meshScale.y);
                        bz = acrossBot;
                        tx2 = centerPntTop.x + (((topRad * (float) Math.sin(rd2)) + (topRad * (float) Math.cos(rd2))) * meshScale.x);
                        ty2 = centerPntTop.y + (((topRad * (float) Math.cos(rd2)) - (topRad * (float) Math.sin(rd2))) * meshScale.y);
                        tz2 = acrossTop;
                        bx2 = centerPntBot.x + (((botRad * (float) Math.sin(rd2)) + (botRad * (float) Math.cos(rd2))) * meshScale.x);
                        by2 = centerPntBot.y + (((botRad * (float) Math.cos(rd2)) - (botRad * (float) Math.sin(rd2))) * meshScale.y);
                        bz2 = acrossBot;
                        break;
                }

                // the points
                vertexArray.addAll(Arrays.asList(tx, ty, tz));
                normal.setFromValues((tx - centerPntTop.x), (ty - centerPntTop.y), (tz - centerPntTop.z));
                normal.normalize();
                normalArray.addAll(Arrays.asList(normal.x, normal.y, normal.z));
                uvArray.addAll(Arrays.asList(u1, v1));
                indexArray.add(iIdx++);

                vertexArray.addAll(Arrays.asList(tx2, ty2, tz2));
                normal.setFromValues((tx2 - centerPntTop.x), (ty2 - centerPntTop.y), (tz2 - centerPntTop.z));
                normal.normalize();
                normalArray.addAll(Arrays.asList(normal.x, normal.y, normal.z));
                uvArray.addAll(Arrays.asList(u2, v1));
                indexArray.add(iIdx++);

                vertexArray.addAll(Arrays.asList(bx, by, bz));
                normal.setFromValues((bx - centerPntBot.x), (by - centerPntBot.y), (bz - centerPntBot.z));
                normal.normalize();
                normalArray.addAll(Arrays.asList(normal.x, normal.y, normal.z));
                uvArray.addAll(Arrays.asList(u1, v2));
                indexArray.add(iIdx++);

                vertexArray.addAll(Arrays.asList(tx2, ty2, tz2));
                normal.setFromValues((tx2 - centerPntTop.x), (ty2 - centerPntTop.y), (tz2 - centerPntTop.z));
                normal.normalize();
                normalArray.addAll(Arrays.asList(normal.x, normal.y, normal.z));
                uvArray.addAll(Arrays.asList(u2, v1));
                indexArray.add(iIdx++);

                vertexArray.addAll(Arrays.asList(bx2, by2, bz2));
                normal.setFromValues((bx2 - centerPntBot.x), (by2 - centerPntBot.y), (bz2 - centerPntBot.z));
                normal.normalize();
                normalArray.addAll(Arrays.asList(normal.x, normal.y, normal.z));
                uvArray.addAll(Arrays.asList(u2, v2));
                indexArray.add(iIdx++);

                vertexArray.addAll(Arrays.asList(bx, by, bz));
                normal.setFromValues((bx - centerPntBot.x), (by - centerPntBot.y), (bz - centerPntBot.z));
                normal.normalize();
                normalArray.addAll(Arrays.asList(normal.x, normal.y, normal.z));
                uvArray.addAll(Arrays.asList(u1, v2));
                indexArray.add(iIdx++);

                ang = ang2;
            }

            botRad = topRad;
            topRad = botRad + rAdd;

            acrossBot = acrossTop;
            acrossTop = acrossBot + acrossAdd;
        }

        // top close
        if ((meshType == Limb.MESH_TYPE_CYLINDER_CLOSE_ALL) || (meshType == Limb.MESH_TYPE_CYLINDER_CLOSE_TOP)) {
            vIdx = vertexArray.size() / 3;
            vStartIdx = vIdx - (CYLINDER_AROUND_SURFACE_COUNT * 6);

            // middle vertex
            vertexArray.addAll(Arrays.asList(topPnt.x, topPnt.y, topPnt.z));
            normal.setFromValues((topPnt.x - botPnt.x), (topPnt.y - botPnt.y), (topPnt.z - botPnt.z));
            normal.normalize();
            normalArray.addAll(Arrays.asList(normal.x, normal.y, normal.z));
            uvArray.addAll(Arrays.asList(0.5f, 0.5f));

            for (n = 0; n != (CYLINDER_AROUND_SURFACE_COUNT - 2); n++) {
                indexArray.add(vStartIdx);
                indexArray.add(vStartIdx + 1);
                indexArray.add(vIdx);

                vStartIdx += 6;   // there's 6 vertexes to each two points on the edge
            }

            /*
            vStartIdx = vertexArray.size() / 3;

            ang = 0.0f;

            for (n = 0; n != CYLINDER_AROUND_SURFACE_COUNT; n++) {
                rd = ang * ((float) Math.PI / 180.0f);

                u1 = ((float) Math.sin(rd) * 0.5f) + 0.5f;
                u2 = ((float) Math.cos(rd) * 0.5f) + 0.5f;

                switch (axis) {
                    case Limb.LIMB_AXIS_X:
                        tx = topPnt.x;
                        ty = topPnt.x + ((topRad * (float) Math.sin(rd)) + (topRad * (float) Math.cos(rd)));
                        tz = topPnt.z + ((topRad * (float) Math.cos(rd)) - (topRad * (float) Math.sin(rd)));
                    case Limb.LIMB_AXIS_Y:
                        tx = topPnt.x + ((topRad * (float) Math.sin(rd)) + (topRad * (float) Math.cos(rd)));
                        ty = topPnt.y;
                        tz = topPnt.z + ((topRad * (float) Math.cos(rd)) - (topRad * (float) Math.sin(rd)));
                        break;
                    default: // Z
                        tx = topPnt.x + ((topRad * (float) Math.sin(rd)) + (topRad * (float) Math.cos(rd)));
                        ty = topPnt.z + ((topRad * (float) Math.cos(rd)) - (topRad * (float) Math.sin(rd)));
                        tz = topPnt.z;
                        break;
                }

                // the points
                vertexArray.addAll(Arrays.asList(tx, ty, tz));
                normal.setFromValues((tx - topPnt.x), (ty - topPnt.y), (tz - topPnt.z));
                normal.normalize();
                normalArray.addAll(Arrays.asList(normal.x, normal.y, normal.z));
                uvArray.addAll(Arrays.asList(u1, u2));

                ang += angAdd;
            }

            for (n = 0; n != (CYLINDER_AROUND_SURFACE_COUNT - 2); n++) {
                indexArray.add(vStartIdx);
                indexArray.add(vStartIdx + (n + 1));
                indexArray.add(vStartIdx + (n + 2));
            }
             */
        }

        if ((meshType == Limb.MESH_TYPE_CYLINDER_CLOSE_ALL) || (meshType == Limb.MESH_TYPE_CYLINDER_CLOSE_BOTTOM)) {
            /*
            vStartIdx = vertexArray.size() / 3;

            ang = 0.0f;

            for (n = 0; n != CYLINDER_AROUND_SURFACE_COUNT; n++) {
                rd = ang * ((float) Math.PI / 180.0f);

                u1 = ((float) Math.sin(rd) * 0.5f) + 0.5f;
                u2 = ((float) Math.cos(rd) * 0.5f) + 0.5f;

                switch (axis) {
                    case Limb.LIMB_AXIS_X:
                        bx = botPnt.x;
                        by = botPnt.x + ((botRad * (float) Math.sin(rd)) + (botRad * (float) Math.cos(rd)));
                        bz = botPnt.z + ((botRad * (float) Math.cos(rd)) - (botRad * (float) Math.sin(rd)));
                    case Limb.LIMB_AXIS_Y:
                        bx = botPnt.x + ((botRad * (float) Math.sin(rd)) + (botRad * (float) Math.cos(rd)));
                        by = botPnt.y;
                        bz = botPnt.z + ((botRad * (float) Math.cos(rd)) - (botRad * (float) Math.sin(rd)));
                    default: // Z
                        bx = botPnt.x + ((botRad * (float) Math.sin(rd)) + (botRad * (float) Math.cos(rd)));
                        by = botPnt.z + ((botRad * (float) Math.cos(rd)) - (botRad * (float) Math.sin(rd)));
                        bz = botPnt.z;
                        break;
                }
                // the points

                vertexArray.addAll(Arrays.asList(bx, by, bz));
                normal.setFromValues((bx - botPnt.x), (by - botPnt.y), (bz - botPnt.z));
                normal.normalize();
                normalArray.addAll(Arrays.asList(normal.x, normal.y, normal.z));
                uvArray.addAll(Arrays.asList(u1, u2));

                ang += angAdd;
            }

            for (n = 0; n != (CYLINDER_AROUND_SURFACE_COUNT - 2); n++) {
                indexArray.add(vStartIdx);
                indexArray.add(vStartIdx + (n + 1));
                indexArray.add(vStartIdx + (n + 2));
            }
             */
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
