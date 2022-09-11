package com.klinksoftware.rag.scene;

import com.klinksoftware.rag.utility.RagMatrix4f;
import java.util.ArrayList;

public class Animation {

    public Scene scene;
    public ArrayList<RagMatrix4f> jointMatrixes, inverseBindMatrixes;

    public Animation(Scene scene) {
        this.scene = scene;

        jointMatrixes = null;
        inverseBindMatrixes = null;
    }

    public void createJointMatrixComponentsFromNodes() {
        int n, nodeCount;
        Node node;
        RagMatrix4f inverseBindMatrix;

        // create the needed matrixes to do the animation
        nodeCount = scene.getNodeCount();

        inverseBindMatrixes = new ArrayList<>();

        for (n = 0; n != nodeCount; n++) {
            node = scene.getNodeForIndex(n);

            // the inverse bind matrix puts a vertex back into
            // the local space of the joint
            inverseBindMatrix = new RagMatrix4f();
            inverseBindMatrix.setNegativeTranslationFromPoint(node.getAbsolutePoint());
            inverseBindMatrixes.add(inverseBindMatrix);

        }
    }
}
