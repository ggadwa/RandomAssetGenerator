package com.klinksoftware.rag.scene;

import com.klinksoftware.rag.utility.RagMatrix4f;
import com.klinksoftware.rag.utility.RagPoint;
import com.klinksoftware.rag.utility.RagQuaternion;
import java.util.ArrayList;

public class Animation {
    public static final int JOINT_COUNT = 128;

    public Scene scene;
    public ArrayList<RagMatrix4f> jointMatrixes, inverseBindMatrixes;
    public ArrayList<AnimationChannel> channels;

    private RagMatrix4f rotMatrix;

    public Animation(Scene scene) {
        this.scene = scene;

        inverseBindMatrixes = null;
        jointMatrixes = null;

        channels = null;

        rotMatrix = new RagMatrix4f(); // pre-allocate
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
            channels.add(new AnimationChannel(scene.getNodeForIndex(n)));
        }
    }

    public AnimationChannel findChannelByNodeName(String name) {
        return (channels.get(scene.findNodeByName(name).index));
    }

    public void buildJointMatrixesForAnimationRecurse(Node node, RagPoint pnt, RagQuaternion rotQuat, long tick) {
        RagPoint nextPnt, nodePnt;
        RagQuaternion nextRotQuat;
        AnimationChannel channel;

        // get channel for this node
        channel = channels.get(node.index);

        // we need to rotate the node offset (from the parent)
        // by the commulative rotational offsets
        nodePnt = node.pnt.copy();

        rotMatrix.setIdentity();
        rotMatrix.setRotationFromQuaternion(rotQuat);
        nodePnt.matrixMultiply(rotMatrix);

        // now add it in to the last offset
        nextPnt = pnt.copy();
        nextPnt.addPoint(nodePnt);

        // add in the next rotation
        nextRotQuat = rotQuat.copy();
        nextRotQuat.multiply(channel.getRotateQuaternionForTick(tick));

        // calculate the joint pose matrix from the commulative
        // offsets and rotations up to here
        channel.setPoseMatrix(nextPnt, nextRotQuat);

        // pass this on to the children
        for (Node childNode : node.childNodes) {
            buildJointMatrixesForAnimationRecurse(childNode, nextPnt, nextRotQuat, tick);
        }
    }

    public ArrayList<RagMatrix4f> buildJointMatrixesForAnimation(long tick) {
        int n, nodeCount;
        RagPoint pnt;
        RagQuaternion rotQuat;

        // recurse through the nodes to build out all the pose matrixes
        // we only do rotations so we just need to push the rotation
        // quaternion around, and we start with the identity
        pnt = new RagPoint(0.0f, 0.0f, 0.0f); // root starts at 0,0,0
        rotQuat = new RagQuaternion();
        buildJointMatrixesForAnimationRecurse(scene.rootNode, pnt, rotQuat, tick);

        // and now return all the calculated joint matrixes
        // for the pose and inverse binds
        nodeCount = scene.getNodeCount();

        for (n = 0; n != nodeCount; n++) {
            jointMatrixes.get(n).setFromMultiple(channels.get(n).poseMatrix, inverseBindMatrixes.get(n));
        }

        return (jointMatrixes);
    }

}
