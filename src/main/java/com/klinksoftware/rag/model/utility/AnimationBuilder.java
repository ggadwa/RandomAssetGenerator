package com.klinksoftware.rag.model.utility;

import com.klinksoftware.rag.scene.Joint;
import com.klinksoftware.rag.scene.Scene;
import com.klinksoftware.rag.utility.RagPoint;

public class AnimationBuilder {

    public Scene scene;

    public AnimationBuilder(Scene scene) {
        this.scene = scene;
    }

    public void build() {
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
    }
}
