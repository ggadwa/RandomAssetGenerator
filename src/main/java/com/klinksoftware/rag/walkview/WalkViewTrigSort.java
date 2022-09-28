package com.klinksoftware.rag.walkview;

import com.klinksoftware.rag.scene.Mesh;
import com.klinksoftware.rag.scene.Node;
import com.klinksoftware.rag.utility.RagPoint;

public class WalkViewTrigSort {

    private static final int MAX_TRIG = 1000;

    public int trigCount;
    public SortedTrig[] trigArray;

    private RagPoint trigCenter;

    public WalkViewTrigSort() {
        int n;

        // pre-allocates
        trigArray = new SortedTrig[MAX_TRIG];

        for (n = 0; n != MAX_TRIG; n++) {
            trigArray[n] = new SortedTrig();
        }

        trigCenter = new RagPoint(0.0f, 0.0f, 0.0f);
    }

    public void clearTrigs() {
        trigCount = 0;
    }

    public void addTrigsFromMesh(Node node, Mesh mesh, RagPoint cameraPnt) {
        int n, k, indexTrigCount, idx;
        float dist;

        indexTrigCount = mesh.indexes.length / 3;

        for (n = 0; n != indexTrigCount; n++) {
            if (trigCount >= MAX_TRIG) {
                return;
            }

            // get trig center
            idx = mesh.indexes[n] * 3;
            trigCenter.setFromValues(mesh.vertexes[idx], mesh.vertexes[idx + 1], mesh.vertexes[idx + 2]);
            idx = mesh.indexes[n + 1] * 3;
            trigCenter.addFromValues(mesh.vertexes[idx], mesh.vertexes[idx + 1], mesh.vertexes[idx + 2]);
            idx = mesh.indexes[n + 2] * 3;
            trigCenter.addFromValues(mesh.vertexes[idx], mesh.vertexes[idx + 1], mesh.vertexes[idx + 2]);

            trigCenter.scale(0.33f);

            // distance to center
            dist = trigCenter.distance(cameraPnt);

            // sort it in
            idx = -1;

            for (k = 0; k != trigCount; k++) {
                if (dist < trigArray[k].distance) {
                    idx = k;
                    break;
                }
            }

            idx = -1;

            // add to list
            if (idx == -1) {
                trigArray[trigCount].node = node;
                trigArray[trigCount].mesh = mesh;
                trigArray[trigCount].trigIdx = n;
                trigArray[trigCount].distance = dist;
            } else {
                for (k = (trigCount - 1); k >= idx; k--) {
                    trigArray[k + 1] = trigArray[k];
                }

                trigArray[idx].node = node;
                trigArray[idx].mesh = mesh;
                trigArray[idx].trigIdx = n;
                trigArray[idx].distance = dist;
            }

            trigCount++;
        }
    }

    public class SortedTrig {

        public Node node;
        public Mesh mesh;
        public int trigIdx;
        public float distance;
    }

}
