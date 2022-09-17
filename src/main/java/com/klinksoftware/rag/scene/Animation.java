package com.klinksoftware.rag.scene;

import com.klinksoftware.rag.utility.RagMatrix4f;
import java.util.ArrayList;

public class Animation {
    public static final int JOINT_COUNT = 128;

    public Scene scene;
    public ArrayList<RagMatrix4f> jointMatrixes, inverseBindMatrixes;
    public ArrayList<AnimationChannel> channels;

    private RagMatrix4f poseMatrix;

    public Animation(Scene scene) {
        this.scene = scene;

        inverseBindMatrixes = null;
        jointMatrixes = null;

        channels = null;

        poseMatrix = new RagMatrix4f(); // pre-allocate
    }

    public void createInverseBindMatrixForNodes() {
        int n, nodeCount;
        Node node;
        RagMatrix4f inverseBindMatrix;

        // create the inverse bind matrixes for nodes
        nodeCount = scene.getNodeCount();

        inverseBindMatrixes = new ArrayList<>();

        for (n = 0; n != nodeCount; n++) {
            node = scene.getNodeForIndex(n);

            // the inverse bind matrix puts a vertex back into
            // the local space of the joint, note that in models
            // all vertexes are absolute from the root node, this is so we
            // can attach any vertex to any node, so the inverse bind
            // matrix is just the negative translate from the absolute
            // position of the node
            inverseBindMatrix = new RagMatrix4f();
            inverseBindMatrix.setNegativeTranslationFromPoint(node.getAbsolutePoint());
            inverseBindMatrixes.add(inverseBindMatrix);
        }
    }

    public void setupJointMatrixesForAnimation() {
        int n;

        // we make enough for the joint list max even if we
        // don't have the nodes because we pass that list to the vert
        // shader
        jointMatrixes = new ArrayList<>();
        for (n = 0; n != JOINT_COUNT; n++) {
            jointMatrixes.add(new RagMatrix4f());
        }
    }

    public void setupChannelsForAnimation() {
        int n, nodeCount;

        nodeCount = scene.getNodeCount();

        channels = new ArrayList<>();
        for (n = 0; n != nodeCount; n++) {
            channels.add(new AnimationChannel());
        }
    }

    public ArrayList<RagMatrix4f> buildJointMatrixesForAnimation(long tick) {
        int n, nodeCount;
        Node node;

        nodeCount = scene.getNodeCount();

        tick = tick / 100;
        System.out.println("cont=" + (float) (tick % 360));

        int testJointIdx = scene.findNodeByName("head_0").index;

        for (n = 0; n != nodeCount; n++) {
            node = scene.getNodeForIndex(n);

            poseMatrix.setIdentity();
            poseMatrix.setTranslationFromPoint(node.getAbsolutePoint());
            if (n == testJointIdx) {
                RagMatrix4f rotMatrix = new RagMatrix4f();
                rotMatrix.setRotationFromYAngle((float) (tick % 360));
                poseMatrix.multiply(rotMatrix);
            } else {
                poseMatrix.setTranslationFromPoint(node.getAbsolutePoint());
            }

            jointMatrixes.get(n).setFromMultiple(poseMatrix, inverseBindMatrixes.get(n));
        }

        return (jointMatrixes);
    }

}
