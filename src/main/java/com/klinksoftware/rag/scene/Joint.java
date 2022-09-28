package com.klinksoftware.rag.scene;

import com.klinksoftware.rag.utility.RagMatrix4f;
import com.klinksoftware.rag.utility.RagPoint;
import com.klinksoftware.rag.utility.RagQuaternion;
import java.util.ArrayList;

public class Joint {

    public static final float ANIMATION_FPS = 60.0f;

    public Node node;
    public RagMatrix4f rotMatrix, poseMatrix, jointMatrix, inverseBindMatrix;
    public ArrayList<ChannelSample> samples;

    public Joint(Node node) {
        this.node = node;

        samples = new ArrayList<>();

        poseMatrix = new RagMatrix4f();
        rotMatrix = new RagMatrix4f();
        jointMatrix = new RagMatrix4f();
        inverseBindMatrix = new RagMatrix4f();
    }

    // the inverse bind matrix puts a vertex back into
    // the local space of the joint, so the inverse bind
    // matrix is just the negative translate from the absolute
    // position of the joint's node
    public void createInverseBindMatrix() {
        inverseBindMatrix = new RagMatrix4f();
        inverseBindMatrix.setNegativeTranslationFromPoint(node.getAbsolutePoint());
    }

    public void addSamples(float secs, RagPoint fromRot, RagPoint toRot) {
        int n, sampleCount;
        float f, lastSec;
        RagPoint rot;
        RagQuaternion quat;

        quat = new RagQuaternion();

        sampleCount = (int) (secs * ANIMATION_FPS);
        rot = fromRot.copy();

        if (samples.isEmpty()) {
            lastSec = 0.0f;
        } else {
            lastSec = samples.get(samples.size() - 1).globalTimeSecond;
        }

        for (n = 0; n != sampleCount; n++) {
            f = (float) n / (float) sampleCount;
            rot.tween(fromRot, toRot, f);
            quat.setIdentity();
            quat.setFromPoint(rot);
            samples.add(new ChannelSample((lastSec + (f * secs)), quat));
        }
    }

    public float getLastSampleSec() {
        return (samples.isEmpty() ? 0.0f : samples.get(samples.size() - 1).globalTimeSecond);
    }

    public RagQuaternion getRotateQuaternionForTick(long tick) {
        int n, sampleIdx;
        long lastMilliSec;
        float loopSecond;

        // no sample, then identity rotation
        if (samples.isEmpty()) {
            return (new RagQuaternion());
        }

        // tick within the animation loop
        lastMilliSec = (long) (samples.get(samples.size() - 1).globalTimeSecond * 1000.0f);
        loopSecond = ((float) (tick % lastMilliSec)) / 1000.0f;

        // find closest sample
        sampleIdx = 0;
        for (n = 0; n != samples.size(); n++) {
            if (loopSecond < samples.get(n).globalTimeSecond) {
                break;
            }
            sampleIdx = n;
        }

        return (samples.get(sampleIdx).rotation);
    }

    public void setPoseMatrix(RagPoint pnt, RagQuaternion rotQuat) {
        poseMatrix.setIdentity();
        poseMatrix.setTranslationFromPoint(pnt);
        rotMatrix.setRotationFromQuaternion(rotQuat);
        poseMatrix.multiply(rotMatrix);
    }

    // the joint matrix is just the pose matrix * the inverse bind matrix
    // this is what eventually gets passed to the shader
    public void buildJointMatrix() {
        jointMatrix.setFromMultiply(poseMatrix, inverseBindMatrix);
    }
}
