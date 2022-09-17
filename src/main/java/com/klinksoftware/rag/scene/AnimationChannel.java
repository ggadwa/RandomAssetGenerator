package com.klinksoftware.rag.scene;

import com.klinksoftware.rag.utility.RagPoint;

public class AnimationChannel {

    public static final int TOTAL_ANIMATION_MSEC = 1000;

    public float[] globalTime;
    public RagPoint[] rotation;

    // this contains the global time (in gltf, the input of the sampler)
    // and just a rotation (in gltf, the output of the sampler)
    public AnimationChannel() {
        globalTime = null;
        rotation = null;
    }
}
