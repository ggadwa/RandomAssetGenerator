package com.klinksoftware.rag.sound;

import com.klinksoftware.rag.AppWindow;

@SoundInterface
public class SoundBang extends SoundBase {

    public SoundBang() {
        super();

        waveMillis = 200 + AppWindow.random.nextInt(200);
    }

    @Override
    public void generateInternal() {
        createWhiteNoise(waveData, 0.8f);
        lowPassFilter(waveData, 0.0f, 1.0f, (0.05f + AppWindow.random.nextFloat(0.1f)));
        clip(waveData, 0.0f, 1.0f, -(0.5f + AppWindow.random.nextFloat(0.2f)), (0.5f + AppWindow.random.nextFloat(0.2f)));
        normalize(waveData);
        fade(waveData, (0.05f + AppWindow.random.nextFloat(0.1f)), (0.4f + AppWindow.random.nextFloat(0.2f)));
    }

}
