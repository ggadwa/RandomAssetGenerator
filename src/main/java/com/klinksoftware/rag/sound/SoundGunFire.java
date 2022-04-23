package com.klinksoftware.rag.sound;

import com.klinksoftware.rag.AppWindow;

public class SoundGunFire extends SoundBase {

    public SoundGunFire() {
        super();

        waveMillis = 50 + AppWindow.random.nextInt(100);
    }

    @Override
    public void generateInternal() {
        int n, frameCount, mixCount;
        float[] mixData;

        frameCount = getFrameCount();
        mixData = new float[frameCount];

        createSineWave(waveData, 0, frameCount, (20.0f + AppWindow.random.nextFloat(80.0f)));

        mixCount = 1 + AppWindow.random.nextInt(3);
        for (n = 0; n != mixCount; n++) {
            createSineWave(mixData, 0, frameCount, (20.0f + AppWindow.random.nextFloat(80.0f)));
            mixWave(waveData, mixData, 0.5f, 0, frameCount);
        }

        mixWhiteNoise(waveData, 0, frameCount, (0.05f + AppWindow.random.nextFloat(0.1f)));

        normalize(waveData);
        clip(waveData, 0, frameCount, -0.8f, 0.2f);

        normalize(waveData);
        fade(waveData, 0.0f, (0.1f + AppWindow.random.nextFloat(0.2f)));
    }

}
