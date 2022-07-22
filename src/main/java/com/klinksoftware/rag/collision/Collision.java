package com.klinksoftware.rag.collision;

import com.klinksoftware.rag.map.MapBuilder;
import com.klinksoftware.rag.mesh.Mesh;
import com.klinksoftware.rag.mesh.MeshList;
import com.klinksoftware.rag.skeleton.Skeleton;
import com.klinksoftware.rag.utility.RagPoint;
import java.util.ArrayList;

public class Collision {

    private ArrayList<CollisionTrig> floorTrigs;

    public Collision() {
        floorTrigs = new ArrayList<>();
    }

    private void addMeshTrigsToCollision(MeshList meshList, Skeleton skeleton, int meshIdx) {
        int n, nTrig, trigIdx, boneIdx, offset;
        RagPoint v0, v1, v2, normal, bonePoint;
        Mesh mesh;

        mesh = meshList.get(meshIdx);

        // get offset bone
        boneIdx = skeleton.findBoneIndexforMeshIndex(meshIdx);
        if (boneIdx == -1) {
            bonePoint = new RagPoint(0.0f, 0.0f, 0.0f);
        } else {
            bonePoint = skeleton.getBoneAbsolutePoint(boneIdx);
        }

        // create the trigs
        nTrig = mesh.indexes.length / 3;

        for (n = 0; n != nTrig; n++) {

            // get vertex and normals
            trigIdx = n * 3;

            offset = mesh.indexes[trigIdx] * 3;
            v0 = new RagPoint((mesh.vertexes[offset] + bonePoint.x), (mesh.vertexes[offset + 1] + bonePoint.y), (mesh.vertexes[offset + 2] + bonePoint.z));
            normal = new RagPoint(mesh.normals[offset], mesh.normals[offset + 1], mesh.normals[offset + 2]);

            offset = mesh.indexes[trigIdx + 1] * 3;
            v1 = new RagPoint((mesh.vertexes[offset] + bonePoint.x), (mesh.vertexes[offset + 1] + bonePoint.y), (mesh.vertexes[offset + 2] + bonePoint.z));

            offset = mesh.indexes[trigIdx + 2] * 3;
            v2 = new RagPoint((mesh.vertexes[offset] + bonePoint.x), (mesh.vertexes[offset + 1] + bonePoint.y), (mesh.vertexes[offset + 2] + bonePoint.z));

            // is this a floor?
            if (normal.y > 0.75f) {
                floorTrigs.add(new CollisionTrig(v0, v1, v2, normal));
            }
        }
    }

    public void buildFromMeshList(MeshList meshList, Skeleton skeleton) {
        int n, nMesh;

        floorTrigs.clear();

        nMesh = meshList.count();
        for (n = 0; n != nMesh; n++) {
            addMeshTrigsToCollision(meshList, skeleton, n);
        }
    }

    public void collideWithFloor(RagPoint pnt) {
        RagPoint rayPnt, rayVct, hitPnt;

        // we go two floor height above, and check one below
        rayPnt = new RagPoint(pnt.x, (pnt.y + (MapBuilder.FLOOR_HEIGHT * 2.0f)), pnt.z);
        rayVct = new RagPoint(0.0f, -(MapBuilder.FLOOR_HEIGHT * 3.0f), 0.0f);
        hitPnt = new RagPoint(0.0f, 0.0f, 0.0f);

        for (CollisionTrig trig : floorTrigs) {
            if (trig.rayOverlapBounds(rayPnt, rayVct)) {
                if (trig.rayTrace(rayPnt, rayVct, hitPnt)) {
                    if (hitPnt.y > pnt.y) {
                        pnt.y = hitPnt.y;
                    }
                }
            }
        }
    }
}
