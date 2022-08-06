package com.klinksoftware.rag.collision;

import com.klinksoftware.rag.map.MapBuilder;
import com.klinksoftware.rag.mesh.Mesh;
import com.klinksoftware.rag.mesh.MeshList;
import com.klinksoftware.rag.skeleton.Skeleton;
import com.klinksoftware.rag.utility.RagPoint;
import java.util.ArrayList;

public class Collision {

    private static final float COLLISION_WIDTH = 1.4f;

    private ArrayList<CollisionTrig> wallTrigs;
    private ArrayList<CollisionTrig> floorTrigs;
    private ArrayList<RagPoint> wallCollideSpokeVcts;

    private RagPoint slidePnt, fallPnt, fallVct, hitPnt;

    public Collision() {
        int ang;
        RagPoint vct;

        // wall and floor trigs
        wallTrigs = new ArrayList<>();
        floorTrigs = new ArrayList<>();

        // xz collision spokes
        wallCollideSpokeVcts = new ArrayList<>();

        for (ang = 0; ang != 360; ang += 10) {
            vct = new RagPoint(0.0f, 0.0f, COLLISION_WIDTH);
            vct.rotateY(ang);
            wallCollideSpokeVcts.add(vct);
        }

        // some preallocates
        slidePnt = new RagPoint(0.0f, 0.0f, 0.0f);
        fallPnt = new RagPoint(0.0f, 0.0f, 0.0f);
        fallVct = new RagPoint(0.0f, 0.0f, 0.0f);
        hitPnt = new RagPoint(0.0f, 0.0f, 0.0f);
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
            } else {
                wallTrigs.add(new CollisionTrig(v0, v1, v2, normal));
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

    public boolean collideWithWall(RagPoint rayPnt) {
        for (CollisionTrig trig : wallTrigs) {
            for (RagPoint rayVct : wallCollideSpokeVcts) {
                if (trig.rayOverlapBounds(rayPnt, rayVct)) {
                    if (trig.rayTrace(rayPnt, rayVct, hitPnt)) {
                        return (true);
                    }
                }
            }
        }

        return (false);
    }

    public void slideWithWall(RagPoint rayPnt, RagPoint moveVct) {
        boolean xMove;

        // check regular move
        slidePnt.setFromPoint(rayPnt);
        slidePnt.x += moveVct.x;
        slidePnt.z += moveVct.z;

        if (!collideWithWall(rayPnt)) {
            rayPnt.x += moveVct.x;
            rayPnt.z += moveVct.z;
            return;
        }

        // slide first on X
        xMove = false;

        slidePnt.setFromPoint(rayPnt);
        slidePnt.x += moveVct.x;

        if (!collideWithWall(slidePnt)) {
            rayPnt.x += moveVct.x;
            xMove = true;
        }

        // now z
        slidePnt.setFromPoint(rayPnt);
        slidePnt.z += moveVct.z;

        if (!collideWithWall(slidePnt)) {
            rayPnt.z += moveVct.z;
        }

        // one more x try
        if (!xMove) {
            slidePnt.setFromPoint(rayPnt);
            slidePnt.x += moveVct.x;

            if (!collideWithWall(slidePnt)) {
                rayPnt.x += moveVct.x;
            }
        }
    }

    public void collideWithFloor(RagPoint pnt) {
        // we go two floor height above, and check one below
        fallPnt.setFromValues(pnt.x, (pnt.y + (MapBuilder.FLOOR_HEIGHT * 2.0f)), pnt.z);
        fallVct.setFromValues(0.0f, -(MapBuilder.FLOOR_HEIGHT * 3.0f), 0.0f);

        for (CollisionTrig trig : floorTrigs) {
            if (trig.rayOverlapBounds(fallPnt, fallVct)) {
                if (trig.rayTrace(fallPnt, fallVct, hitPnt)) {
                    if (hitPnt.y > pnt.y) {
                        pnt.y = hitPnt.y;
                    }
                }
            }
        }
    }
}
