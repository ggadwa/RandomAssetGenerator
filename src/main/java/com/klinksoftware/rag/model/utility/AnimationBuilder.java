package com.klinksoftware.rag.model.utility;

import com.klinksoftware.rag.scene.Joint;
import com.klinksoftware.rag.scene.Scene;
import com.klinksoftware.rag.utility.RagPoint;

public class AnimationBuilder {

    public Scene scene;

    public AnimationBuilder(Scene scene) {
        this.scene = scene;
    }

    public void buildArm(int armIndex, float flip) {
        Joint joint;

        joint = scene.animation.findJointForNodeName("shoulder_" + Integer.toString(armIndex));
        if (joint == null) {
            return;
        }

        joint.addSamples(0.5f, new RagPoint(0.0f, 0.0f, (70.0f * flip)), new RagPoint(0.0f, (-15.0f * flip), (60.0f * flip)));
        joint.addSamples(0.5f, new RagPoint(0.0f, (-15.0f * flip), (60.0f * flip)), new RagPoint(0.0f, 0.0f, (70.0f * flip)));
        joint.addSamples(0.5f, new RagPoint(0.0f, 0.0f, (70.0f * flip)), new RagPoint(0.0f, (15.0f * flip), (60.0f * flip)));
        joint.addSamples(0.5f, new RagPoint(0.0f, (15.0f * flip), (60.0f * flip)), new RagPoint(0.0f, 0.0f, (70.0f * flip)));
    }

    public void build() {
        int n;
        Joint joint;

        joint = scene.animation.findJointForNodeName("hip_0");
        joint.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(-45.0f, 0.0f, 0.0f));
        joint.addSamples(0.5f, new RagPoint(-45.0f, 0.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));
        joint.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(45.0f, 0.0f, 0.0f));
        joint.addSamples(0.5f, new RagPoint(45.0f, 0.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));

        joint = scene.animation.findJointForNodeName("knee_0");
        joint.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(25.0f, 0.0f, 0.0f));
        joint.addSamples(0.5f, new RagPoint(25.0f, 0.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));
        joint.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(40.0f, 0.0f, 0.0f));
        joint.addSamples(0.5f, new RagPoint(40.0f, 0.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));

        joint = scene.animation.findJointForNodeName("ankle_0");
        joint.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(25.0f, 0.0f, 0.0f));
        joint.addSamples(0.5f, new RagPoint(25.0f, 0.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));
        joint.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(10.0f, 0.0f, 0.0f));
        joint.addSamples(0.5f, new RagPoint(10.0f, 0.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));

        joint = scene.animation.findJointForNodeName("hip_1");
        joint.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(45.0f, 0.0f, 0.0f));
        joint.addSamples(0.5f, new RagPoint(45.0f, 0.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));
        joint.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(-45.0f, 0.0f, 0.0f));
        joint.addSamples(0.5f, new RagPoint(-45.0f, 0.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));

        joint = scene.animation.findJointForNodeName("knee_1");
        joint.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(40.0f, 0.0f, 0.0f));
        joint.addSamples(0.5f, new RagPoint(40.0f, 0.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));
        joint.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(25.0f, 0.0f, 0.0f));
        joint.addSamples(0.5f, new RagPoint(25.0f, 0.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));

        joint = scene.animation.findJointForNodeName("ankle_1");
        joint.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(10.0f, 0.0f, 0.0f));
        joint.addSamples(0.5f, new RagPoint(10.0f, 0.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));
        joint.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(25.0f, 0.0f, 0.0f));
        joint.addSamples(0.5f, new RagPoint(25.0f, 0.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));

        joint = scene.animation.findJointForNodeName("torso_shoulder");
        joint.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(0.0f, 15.0f, 0.0f));
        joint.addSamples(0.5f, new RagPoint(0.0f, 15.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));
        joint.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(0.0f, -15.0f, 0.0f));
        joint.addSamples(0.5f, new RagPoint(0.0f, -15.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));

        for (n = 0; n != 4; n++) {
            buildArm(n, (((n % 2) == 0) ? -1.0f : 1.0f));
        }
    }
}
