package com.klinksoftware.rag.collision;

import com.klinksoftware.rag.map.utility.MapBase;
import com.klinksoftware.rag.scene.Mesh;
import com.klinksoftware.rag.scene.Node;
import com.klinksoftware.rag.scene.Scene;
import com.klinksoftware.rag.utility.RagPoint;
import java.util.ArrayList;

public class Collision {

    private static final float COLLISION_WIDTH = 1.6f;
    private static final float COLLISION_HEIGHT = 5.0f;
    private static final float COLLISION_HEIGHT_COUNT = 8;

    private ArrayList<CollisionTrig> wallTrigs;
    private ArrayList<CollisionTrig> floorTrigs;
    private ArrayList<RagPoint> wallCollideSpokeVcts;

    private RagPoint slidePnt, highPnt, fallPnt, fallVct, hitPnt;

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
        highPnt = new RagPoint(0.0f, 0.0f, 0.0f);
        fallPnt = new RagPoint(0.0f, 0.0f, 0.0f);
        fallVct = new RagPoint(0.0f, 0.0f, 0.0f);
        hitPnt = new RagPoint(0.0f, 0.0f, 0.0f);
    }

    private void addMeshTrigsToCollision(Mesh mesh, RagPoint nodePnt) {
        int n, nTrig, trigIdx, offset;
        RagPoint v0, v1, v2, normal;

        // create the trigs
        nTrig = mesh.indexes.length / 3;

        for (n = 0; n != nTrig; n++) {

            // get vertex and normals
            trigIdx = n * 3;

            offset = mesh.indexes[trigIdx] * 3;
            v0 = new RagPoint((mesh.vertexes[offset] + nodePnt.x), (mesh.vertexes[offset + 1] + nodePnt.y), (mesh.vertexes[offset + 2] + nodePnt.z));
            normal = new RagPoint(mesh.normals[offset], mesh.normals[offset + 1], mesh.normals[offset + 2]);

            offset = mesh.indexes[trigIdx + 1] * 3;
            v1 = new RagPoint((mesh.vertexes[offset] + nodePnt.x), (mesh.vertexes[offset + 1] + nodePnt.y), (mesh.vertexes[offset + 2] + nodePnt.z));

            offset = mesh.indexes[trigIdx + 2] * 3;
            v2 = new RagPoint((mesh.vertexes[offset] + nodePnt.x), (mesh.vertexes[offset + 1] + nodePnt.y), (mesh.vertexes[offset + 2] + nodePnt.z));

            // is this a floor?
            if (normal.y > 0.75f) {
                floorTrigs.add(new CollisionTrig(v0, v1, v2, normal));
            } else {
                wallTrigs.add(new CollisionTrig(v0, v1, v2, normal));
            }
        }
    }

    public void buildFromSceneRecursive(Node node, RagPoint pnt) {
        RagPoint nextPnt;

        nextPnt = pnt.copy();
        nextPnt.addPoint(node.pnt);

        for (Mesh mesh : node.meshes) {
            addMeshTrigsToCollision(mesh, nextPnt);
        }

        for (Node childNode : node.childNodes) {
            buildFromSceneRecursive(childNode, nextPnt);
        }
    }

    public void buildFromScene(Scene scene) {
        floorTrigs.clear();
        wallTrigs.clear();

        buildFromSceneRecursive(scene.rootNode, new RagPoint(0.0f, 0.0f, 0.0f));
    }

    public boolean collideSpokesWithWall(RagPoint rayPnt) {
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

    public boolean collideWithWall(RagPoint rayPnt) {
        int n;
        float yAdd;

        yAdd = COLLISION_HEIGHT / (float) COLLISION_HEIGHT_COUNT;
        highPnt.setFromPoint(rayPnt);

        for (n = 0; n != COLLISION_HEIGHT_COUNT; n++) {
            if (collideSpokesWithWall(highPnt)) {
                return (true);
            }
            highPnt.y += yAdd;
        }

        return (false);
    }

    public void slideWithWall(RagPoint rayPnt, RagPoint moveVct) {
        boolean xMove;

        // check regular move
        slidePnt.setFromPoint(rayPnt);
        slidePnt.x += moveVct.x;
        slidePnt.z += moveVct.z;

        if (!collideWithWall(slidePnt)) {
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
        fallPnt.setFromValues(pnt.x, (pnt.y + (MapBase.FLOOR_HEIGHT * 2.0f)), pnt.z);
        fallVct.setFromValues(0.0f, -(MapBase.FLOOR_HEIGHT * 3.0f), 0.0f);

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
