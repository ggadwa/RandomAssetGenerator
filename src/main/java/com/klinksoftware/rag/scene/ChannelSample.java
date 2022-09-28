package com.klinksoftware.rag.scene;

import com.klinksoftware.rag.utility.RagQuaternion;

// this contains the global time (in gltf, the input of the sampler)
// and just a rotation (in gltf, the output of the sampler)
public class ChannelSample {

    public float globalTimeSecond;
    public RagQuaternion rotation;

    public ChannelSample(float globalTimeSecond, RagQuaternion rotation) {
        this.globalTimeSecond = globalTimeSecond;
        this.rotation = rotation.copy();
    }
}
