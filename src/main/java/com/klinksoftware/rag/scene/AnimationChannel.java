package com.klinksoftware.rag.scene;

import com.klinksoftware.rag.utility.RagMatrix4f;
import com.klinksoftware.rag.utility.RagPoint;
import com.klinksoftware.rag.utility.RagQuaternion;
import java.util.ArrayList;

public class AnimationChannel {

    public static final float ANIMATION_FPS = 60.0f;

    public Node node;
    public RagMatrix4f rotMatrix, poseMatrix;
    public ArrayList<AnimationChannelSample> samples;

    public AnimationChannel(Node node) {
        RagQuaternion quat;

        this.node = node;
        rotMatrix = new RagMatrix4f(); // pre-allocate

        samples = new ArrayList<>();

        // the calculated pose matrix for this node
        poseMatrix = new RagMatrix4f();
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
            samples.add(new AnimationChannelSample((lastSec + (f * secs)), quat));
        }
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
}
