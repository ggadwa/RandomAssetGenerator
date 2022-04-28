package com.klinksoftware.rag.sound;

import com.klinksoftware.rag.AppWindow;

public class SoundExplosion extends SoundBase {

    public SoundExplosion() {
        super();

        waveMillis = 2000 + AppWindow.random.nextInt(2000);
    }

    @Override
    public void generateInternal() {
        int frameCount;
        float bangPos, fadePos, bangFrequency, wrapFrequency;
        float[] mixData;

        frameCount = getFrameCount();
        mixData = new float[frameCount];

        bangPos = 0.1f + (AppWindow.random.nextFloat(0.25f));
        fadePos = bangPos + (0.1f + AppWindow.random.nextFloat(0.4f));

        bangFrequency = 10.0f + AppWindow.random.nextFloat(20.0f);
        wrapFrequency = 5.0f + AppWindow.random.nextFloat(20.0f);

        //white noises
        createWhiteNoise(waveData, 0.8f);

        createSquareWhiteNoise(mixData, bangFrequency, 0.8f);
        mixWave(waveData, mixData, 1.0f, bangPos, fadePos);
        createWhiteNoise(mixData, 0.8f);
        mixWave(waveData, mixData, 0.5f, bangPos, fadePos);

        createWhiteNoise(mixData, 0.8f);
        mixWave(waveData, mixData, 1.0f, fadePos, 1.0f);

        // fade the sections
        fade(waveData, bangPos, fadePos);

        // tones in wave
        createWave(mixData, WAVE_TYPE_SINE, createSimpleWaveChunks(wrapFrequency, 0.5f));
        mixWave(waveData, mixData, (0.3f + AppWindow.random.nextFloat(0.2f)), 0.0f, bangPos);

        createWave(mixData, WAVE_TYPE_SINE, createSimpleWaveChunks((bangFrequency + 1.0f), 0.5f));
        mixWave(waveData, mixData, (0.3f + AppWindow.random.nextFloat(0.2f)), bangPos, fadePos);

        createWave(mixData, WAVE_TYPE_SINE, createSimpleWaveChunks(wrapFrequency, 0.5f));
        mixWave(waveData, mixData, (0.3f + AppWindow.random.nextFloat(0.2f)), fadePos, 1.0f);

        // low pass and clip
        lowPassFilter(waveData, 0.0f, 1.0f, (0.05f + AppWindow.random.nextFloat(0.1f)));
        clip(waveData, bangPos, fadePos, -(0.95f + AppWindow.random.nextFloat(0.5f)), (0.95f + AppWindow.random.nextFloat(0.5f)));

        // one more fade
        fade(waveData, bangPos, fadePos);
    }

}
