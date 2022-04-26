package com.klinksoftware.rag.sound;

import com.klinksoftware.rag.AppWindow;

public class SoundBang extends SoundBase {

    public SoundBang() {
        super();

        waveMillis = 50 + AppWindow.random.nextInt(100);
    }

    @Override
    public void generateInternal() {
        int n, frameCount, mixCount;
        float[] mixData;

        frameCount = getFrameCount();
        mixData = new float[frameCount];

        // start with a square wave that has white noise at peaks
        createSquareWave(waveData, 0, frameCount, (170.0f + AppWindow.random.nextFloat(60.0f)));
        mixWhiteNoise(waveData, 0, frameCount, 0.0f, 0.1f, (0.1f + AppWindow.random.nextFloat(0.2f)));
        mixWhiteNoise(waveData, 0, frameCount, -1.0f, -0.9f, (0.1f + AppWindow.random.nextFloat(0.2f)));

        /*
        createSineWave(waveData, 0, frameCount, (20.0f + AppWindow.random.nextFloat(80.0f)));

        mixCount = 1 + AppWindow.random.nextInt(3);
        for (n = 0; n != mixCount; n++) {
            createSineWave(mixData, 0, frameCount, (20.0f + AppWindow.random.nextFloat(80.0f)));
            mixWave(waveData, mixData, 0.5f, 0, frameCount);
        }

        mixWhiteNoise(waveData, 0, frameCount, (0.05f + AppWindow.random.nextFloat(0.1f)));

        scale(waveData, 0, frameCount, 2.0f);
        //clip(waveData, 0, frameCount, -0.8f, 0.2f);
*/
        normalize(waveData);
        fade(waveData, 0.0f, (0.1f + AppWindow.random.nextFloat(0.2f)));
    }

}
