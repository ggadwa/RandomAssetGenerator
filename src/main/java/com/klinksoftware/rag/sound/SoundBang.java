package com.klinksoftware.rag.sound;

import com.klinksoftware.rag.AppWindow;

public class SoundBang extends SoundBase {

    public SoundBang() {
        super();

        waveMillis = 200 + AppWindow.random.nextInt(200);
    }

    @Override
    public void generateInternal() {
        int frameCount;

        frameCount = getFrameCount();

        createWhiteNoise(waveData, 0.8f);
        lowPassFilter(waveData, 0, frameCount, 0.1f);
        clip(waveData, 0, frameCount, -0.7f, 0.7f);
        fade(waveData, 0.1f, 0.5f);
    }

}
