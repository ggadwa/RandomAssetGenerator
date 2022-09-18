package com.klinksoftware.rag.scene;

import com.klinksoftware.rag.utility.RagMatrix4f;
import com.klinksoftware.rag.utility.RagQuaternion;
import java.util.ArrayList;

public class AnimationChannel {
    public static final int TOTAL_ANIMATION_MSEC = 1000;

    public Node node;
    public RagMatrix4f rotMatrix;
    public ArrayList<AnimationChannelSample> samples;

    public AnimationChannel(Node node) {
        RagQuaternion quat;

        this.node = node;
        rotMatrix = new RagMatrix4f(); // pre-allocate

        samples = new ArrayList<>();

        // every channel has a single default 0 animation
        quat = new RagQuaternion();
        samples.add(new AnimationChannelSample(0.0f, quat));
    }

    public void testX() {
        int n;
        float sec;
        RagQuaternion quat;

        quat = new RagQuaternion();
        samples.clear();

        for (n = 0; n != 100; n++) {
            sec = ((float) (n * 10)) / 1000.0f;
            quat.setIdentity();
            quat.setFromEuler(((float) n * 3.6f), 0.0f, 0.0f);
            samples.add(new AnimationChannelSample(sec, quat));
        }
    }

    public void testY() {
        int n;
        float sec;
        RagQuaternion quat;

        quat = new RagQuaternion();
        samples.clear();

        for (n = 0; n != 100; n++) {
            sec = ((float) (n * 10)) / 1000.0f;
            quat.setIdentity();
            quat.setFromEuler(0.0f, ((float) n * 3.6f), 0.0f);
            samples.add(new AnimationChannelSample(sec, quat));
        }
    }

    public void calculatePoseMatrixForTick(RagMatrix4f poseMatrix, long tick) {
        int n, sampleIdx;
        float loopSecond;

        // the default pose is just the node translation
        poseMatrix.setIdentity();
        poseMatrix.setTranslationFromPoint(node.getAbsolutePoint());

        // if no samples, then it's just the default
        if (samples.isEmpty()) {
            return;
        }

        // tick within the animation loop
        loopSecond = ((float) (tick % (long) TOTAL_ANIMATION_MSEC)) / 1000.0f;

        // find closest sample
        sampleIdx = 0;
        for (n = 0; n != samples.size(); n++) {
            if (loopSecond < samples.get(n).globalTimeSecond) {
                break;
            }
            sampleIdx = n;
        }

        // build pose from sample
        rotMatrix.setRotationFromQuaternion(samples.get(sampleIdx).rotation);
        poseMatrix.multiply(rotMatrix);
    }
}
