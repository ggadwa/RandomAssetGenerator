package com.klinksoftware.rag.model.utility;

import com.klinksoftware.rag.scene.Joint;
import com.klinksoftware.rag.scene.Scene;
import com.klinksoftware.rag.utility.RagPoint;

public class AnimationBuilder {

    public Scene scene;

    public AnimationBuilder(Scene scene) {
        this.scene = scene;
    }

    public void buildLeg(int legIndex, boolean flip) {
        Joint joint;

        joint = scene.animation.findJointForNodeName("hip_" + Integer.toString(legIndex));
        if (joint == null) {
            return;
        }

        if (flip) {
            joint.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(-45.0f, 0.0f, 0.0f));
            joint.addSamples(0.5f, new RagPoint(-45.0f, 0.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));
            joint.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(45.0f, 0.0f, 0.0f));
            joint.addSamples(0.5f, new RagPoint(45.0f, 0.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));
        } else {
            joint.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(45.0f, 0.0f, 0.0f));
            joint.addSamples(0.5f, new RagPoint(45.0f, 0.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));
            joint.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(-45.0f, 0.0f, 0.0f));
            joint.addSamples(0.5f, new RagPoint(-45.0f, 0.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));
        }

        joint = scene.animation.findJointForNodeName("knee_" + Integer.toString(legIndex));
        if (flip) {
            joint.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(25.0f, 0.0f, 0.0f));
            joint.addSamples(0.5f, new RagPoint(25.0f, 0.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));
            joint.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(40.0f, 0.0f, 0.0f));
            joint.addSamples(0.5f, new RagPoint(40.0f, 0.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));
        } else {
            joint.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(40.0f, 0.0f, 0.0f));
            joint.addSamples(0.5f, new RagPoint(40.0f, 0.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));
            joint.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(25.0f, 0.0f, 0.0f));
            joint.addSamples(0.5f, new RagPoint(25.0f, 0.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));
        }

        joint = scene.animation.findJointForNodeName("ankle_" + Integer.toString(legIndex));
        if (flip) {
            joint.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(25.0f, 0.0f, 0.0f));
            joint.addSamples(0.5f, new RagPoint(25.0f, 0.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));
            joint.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(10.0f, 0.0f, 0.0f));
            joint.addSamples(0.5f, new RagPoint(10.0f, 0.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));
        } else {
            joint.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(25.0f, 0.0f, 0.0f));
            joint.addSamples(0.5f, new RagPoint(25.0f, 0.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));
            joint.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(10.0f, 0.0f, 0.0f));
            joint.addSamples(0.5f, new RagPoint(10.0f, 0.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));
        }
    }

    public void buildArm(int armIndex, boolean flip) {
        Joint joint;

        joint = scene.animation.findJointForNodeName("shoulder_" + Integer.toString(armIndex));
        if (joint == null) {
            return;
        }

        if (flip) {
            joint.addSamples(0.5f, new RagPoint(0.0f, 0.0f, -70.0f), new RagPoint(0.0f, 15.0f, -60.0f));
            joint.addSamples(0.5f, new RagPoint(0.0f, 15.0f, -60.0f), new RagPoint(0.0f, 0.0f, -70.0f));
            joint.addSamples(0.5f, new RagPoint(0.0f, 0.0f, -70.0f), new RagPoint(0.0f, -15.0f, -60.0f));
            joint.addSamples(0.5f, new RagPoint(0.0f, -15.0f, -60.0f), new RagPoint(0.0f, 0.0f, -70.0f));
        } else {
            joint.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 70.0f), new RagPoint(0.0f, -15.0f, 60.0f));
            joint.addSamples(0.5f, new RagPoint(0.0f, -15.0f, 60.0f), new RagPoint(0.0f, 0.0f, 70.0f));
            joint.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 70.0f), new RagPoint(0.0f, 15.0f, 60.0f));
            joint.addSamples(0.5f, new RagPoint(0.0f, 15.0f, 60.0f), new RagPoint(0.0f, 0.0f, 70.0f));
        }

        joint = scene.animation.findJointForNodeName("elbow_" + Integer.toString(armIndex));
        if (joint == null) {
            return;
        }

        if (flip) {
            joint.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(0.0f, -20.0f, 0.0f));
            joint.addSamples(0.5f, new RagPoint(0.0f, -20.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));
            joint.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(0.0f, 20.0f, 0.0f));
            joint.addSamples(0.5f, new RagPoint(0.0f, 20.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));
        } else {
            joint.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(0.0f, 20.0f, 0.0f));
            joint.addSamples(0.5f, new RagPoint(0.0f, 20.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));
            joint.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(0.0f, -20.0f, 0.0f));
            joint.addSamples(0.5f, new RagPoint(0.0f, -20.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));
        }
    }

    public void build() {
        int n;
        Joint joint;

        joint = scene.animation.findJointForNodeName("torso_shoulder");
        joint.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(0.0f, 15.0f, 0.0f));
        joint.addSamples(0.5f, new RagPoint(0.0f, 15.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));
        joint.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(0.0f, -15.0f, 0.0f));
        joint.addSamples(0.5f, new RagPoint(0.0f, -15.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));

        for (n = 0; n != 4; n++) {
            buildLeg(n, ((n % 2) == 0));
        }

        for (n = 0; n != 4; n++) {
            buildArm(n, ((n % 2) == 0));
        }
    }
}
