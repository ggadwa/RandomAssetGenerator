package com.klinksoftware.rag.scene;

import com.klinksoftware.rag.utility.RagMatrix4f;
import com.klinksoftware.rag.utility.RagPoint;
import com.klinksoftware.rag.utility.RagQuaternion;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import org.lwjgl.system.MemoryUtil;
import static org.lwjgl.system.MemoryUtil.memFree;

public class Animation {
    public static final int JOINT_COUNT = 128;

    public Scene scene;
    public int vboSkeletonVertexId;
    public ArrayList<Joint> joints;

    private RagMatrix4f rotMatrix;

    public Animation(Scene scene) {
        this.scene = scene;

        joints = new ArrayList<>();
        rotMatrix = new RagMatrix4f(); // pre-allocate
    }

    public void createJointsForAnimatedNodes() {
        for (Node node : scene.getAllNodes()) {
            if (node != scene.rootNode) {
                joints.add(new Joint(node));
            }
        }
    }

    public Joint findJointForNode(Node node) {
        for (Joint joint : joints) {
            if (joint.node == node) {
                return (joint);
            }
        }

        return (null); // here it is possible to have a node witout a joint
    }

    public Joint findJointForNodeName(String nodeName) {
        for (Joint joint : joints) {
            if (joint.node.name.equals(nodeName)) {
                return (joint);
            }
        }

        return (null);
    }

    public int findJointIndexForNodeIndex(int index) {
        int n, jointCount;

        jointCount = joints.size();

        for (n = 0; n != jointCount; n++) {
            if (joints.get(n).node.index == index) {
                return (n);
            }
        }

        throw new RuntimeException("Joint setup is missing joint: " + index);
    }

    public float getAnimationLengthInSec() {
        float sec;

        sec = 0.0f;

        for (Joint joint : joints) {
            sec = Math.max(sec, joint.getLastSampleSec());
        }

        return (sec);
    }

    public void createInverseBindMatrixForJoints() {
        for (Joint joint : joints) {
            joint.createInverseBindMatrix();
        }
    }

    public void buildJointPoseMatrixesForAnimationRecurse(Node node, RagPoint pnt, RagQuaternion rotQuat, long tick) {
        RagPoint nextPnt, nodePnt;
        RagQuaternion nextRotQuat;
        Joint joint;

        joint = findJointForNode(node);

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
        if (joint != null) {
            nextRotQuat.multiply(joint.getRotateQuaternionForTick(tick));
        }

        // calculate the joint pose matrix from the commulative
        // offsets and rotations up to here
        if (joint != null) {
            joint.setPoseMatrix(nextPnt, nextRotQuat);
        }

        // pass this on to the children
        for (Node childNode : node.childNodes) {
            buildJointPoseMatrixesForAnimationRecurse(childNode, nextPnt, nextRotQuat, tick);
        }
    }

    public void buildJointMatrixesForAnimation(long tick) {
        RagPoint pnt;
        RagQuaternion rotQuat;

        // recurse through the nodes to build out all the pose matrixes
        // for the joints -- we only do rotations so we just need to push the rotation
        // quaternion around, and we start with the identity
        pnt = new RagPoint(0.0f, 0.0f, 0.0f); // root starts at 0,0,0
        rotQuat = new RagQuaternion();
        buildJointPoseMatrixesForAnimationRecurse(scene.rootNode, pnt, rotQuat, tick);

        // build all the joint matrixes which is the post matrix * inverse bind matrix
        for (Joint joint : joints) {
            joint.buildJointMatrix();
        }
    }

    // gl routines for drawing skeletons
    public void setupGLBuffersForSkeletonDrawing() {
        vboSkeletonVertexId = glGenBuffers();
    }

    public void setupNodeSkeletonPntRecurse(Node node, RagPoint pnt, RagQuaternion rotQuat, long tick) {
        RagPoint nextPnt, nodePnt;
        RagQuaternion nextRotQuat;
        Joint joint;

        // get joint for this node
        joint = findJointForNode(node);

        // we need to rotate the node offset (from the parent)
        // by the commulative rotational offsets
        nodePnt = node.pnt.copy();

        rotMatrix.setIdentity();
        rotMatrix.setRotationFromQuaternion(rotQuat);
        nodePnt.matrixMultiply(rotMatrix);

        // now add it in to the last offset
        nextPnt = pnt.copy();
        nextPnt.addPoint(nodePnt);

        // the animated skeleton absolute value
        node.skeletonPnt.setFromPoint(nextPnt);

        // add in the next rotation
        nextRotQuat = rotQuat.copy();
        if (joint != null) {
            nextRotQuat.multiply(joint.getRotateQuaternionForTick(tick));
        }

        // pass this on to the children
        for (Node childNode : node.childNodes) {
            setupNodeSkeletonPntRecurse(childNode, nextPnt, nextRotQuat, tick);
        }
    }

    public int updateGLBuffersForSkeletonDrawing(long tick) {
        int nodeCount, lineCount;
        RagPoint pnt;
        FloatBuffer vertexBuf;
        ArrayList<Node> nodes;
        RagQuaternion rotQuat;

        // recurse through the nodes to get the matrix at
        // each node part
        pnt = new RagPoint(0.0f, 0.0f, 0.0f); // root starts at 0,0,0
        rotQuat = new RagQuaternion();
        setupNodeSkeletonPntRecurse(scene.rootNode, pnt, rotQuat, tick);

        // count the # of lines we will need
        lineCount = 0;

        nodes = scene.getAllNodes();
        nodeCount = nodes.size();

        for (Node node : nodes) {
            lineCount += node.childNodes.size();
        }

        // memory for vertexes
        vertexBuf = MemoryUtil.memAllocFloat(((lineCount * 2) + nodeCount) * 3);

        // vertexes for lines
        for (Node node : nodes) {
            for (Node node2 : node.childNodes) {
                vertexBuf.put(node.skeletonPnt.x).put(node.skeletonPnt.y).put(node.skeletonPnt.z);
                vertexBuf.put(node2.skeletonPnt.x).put(node2.skeletonPnt.y).put(node2.skeletonPnt.z);
            }
        }

        // vertexes for bones
        for (Node node : nodes) {
            vertexBuf.put(node.skeletonPnt.x).put(node.skeletonPnt.y).put(node.skeletonPnt.z);
        }

        // put it in gl buffer
        vertexBuf.flip();
        vboSkeletonVertexId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboSkeletonVertexId);
        glBufferData(GL_ARRAY_BUFFER, vertexBuf, GL_DYNAMIC_DRAW);
        memFree(vertexBuf);

        return (lineCount);
    }

    public void releaseGLBuffersForSkeletonDrawing() {
        glDeleteBuffers(vboSkeletonVertexId);
    }

}
