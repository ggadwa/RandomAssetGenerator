package com.klinksoftware.rag.model.utility;

import com.klinksoftware.rag.scene.AnimationChannel;
import com.klinksoftware.rag.scene.Scene;
import com.klinksoftware.rag.utility.RagPoint;

public class AnimationBuilder {

    public Scene scene;

    public AnimationBuilder(Scene scene) {
        this.scene = scene;
    }

    public void build() {
        AnimationChannel channel;

        channel = scene.animation.findChannelByNodeName("hip_0");
        channel.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(-45.0f, 0.0f, 0.0f));
        channel.addSamples(0.5f, new RagPoint(-45.0f, 0.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));
        channel.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(45.0f, 0.0f, 0.0f));
        channel.addSamples(0.5f, new RagPoint(45.0f, 0.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));

        channel = scene.animation.findChannelByNodeName("knee_0");
        channel.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(25.0f, 0.0f, 0.0f));
        channel.addSamples(0.5f, new RagPoint(25.0f, 0.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));
        channel.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(40.0f, 0.0f, 0.0f));
        channel.addSamples(0.5f, new RagPoint(40.0f, 0.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));

        channel = scene.animation.findChannelByNodeName("ankle_0");
        channel.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(25.0f, 0.0f, 0.0f));
        channel.addSamples(0.5f, new RagPoint(25.0f, 0.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));
        channel.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(10.0f, 0.0f, 0.0f));
        channel.addSamples(0.5f, new RagPoint(10.0f, 0.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));

        channel = scene.animation.findChannelByNodeName("hip_1");
        channel.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(45.0f, 0.0f, 0.0f));
        channel.addSamples(0.5f, new RagPoint(45.0f, 0.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));
        channel.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(-45.0f, 0.0f, 0.0f));
        channel.addSamples(0.5f, new RagPoint(-45.0f, 0.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));

        channel = scene.animation.findChannelByNodeName("knee_1");
        channel.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(40.0f, 0.0f, 0.0f));
        channel.addSamples(0.5f, new RagPoint(40.0f, 0.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));
        channel.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(25.0f, 0.0f, 0.0f));
        channel.addSamples(0.5f, new RagPoint(25.0f, 0.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));

        channel = scene.animation.findChannelByNodeName("ankle_1");
        channel.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(10.0f, 0.0f, 0.0f));
        channel.addSamples(0.5f, new RagPoint(10.0f, 0.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));
        channel.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(25.0f, 0.0f, 0.0f));
        channel.addSamples(0.5f, new RagPoint(25.0f, 0.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));

        channel = scene.animation.findChannelByNodeName("torso_shoulder");
        channel.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(0.0f, 15.0f, 0.0f));
        channel.addSamples(0.5f, new RagPoint(0.0f, 15.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));
        channel.addSamples(0.5f, new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(0.0f, -15.0f, 0.0f));
        channel.addSamples(0.5f, new RagPoint(0.0f, -15.0f, 0.0f), new RagPoint(0.0f, 0.0f, 0.0f));

    }
}
