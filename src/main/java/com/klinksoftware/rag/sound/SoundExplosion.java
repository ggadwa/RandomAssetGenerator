package com.klinksoftware.rag.sound;

import com.klinksoftware.rag.AppWindow;

@SoundInterface
public class SoundExplosion extends SoundBase {

    public SoundExplosion() {
        super();

        waveMillis = 2000 + AppWindow.random.nextInt(2000);
    }

    @Override
    public void generateInternal() {
        int frameCount, bangIdx, fadeIdx;
        float bangFrequency, wrapFrequency;
        float[] mixData;

        frameCount = getFrameCount();
        mixData = new float[frameCount];

        bangIdx = 1000 + AppWindow.random.nextInt(1000);
        fadeIdx = frameCount - (2000 + AppWindow.random.nextInt(2000));

        bangFrequency = 10.0f + AppWindow.random.nextFloat(20.0f);
        wrapFrequency = 5.0f + AppWindow.random.nextFloat(20.0f);

        //white noises
        createWhiteNoise(waveData, 0.8f);

        createSquareWhiteNoise(mixData, bangFrequency, 0.8f);
        mixWave(waveData, mixData, bangIdx, fadeIdx, 0.5f);
        createWhiteNoise(mixData, 0.8f);
        mixWave(waveData, mixData, bangIdx, fadeIdx, 0.5f);

        createWhiteNoise(mixData, 0.8f);
        mixWave(waveData, mixData, fadeIdx, frameCount, 0.5f);

        // fade the sections
        fade(waveData, bangIdx, fadeIdx);

        // tones in wave
        createWave(mixData, WAVE_TYPE_SINE, createSimpleWaveChunks(wrapFrequency, 0.8f));
        mixWave(waveData, mixData, 0, bangIdx, (0.3f + AppWindow.random.nextFloat(0.2f)));

        createWave(mixData, WAVE_TYPE_SINE, createSimpleWaveChunks((bangFrequency + 1.0f), 0.8f));
        mixWave(waveData, mixData, bangIdx, fadeIdx, (0.3f + AppWindow.random.nextFloat(0.2f)));

        createWave(mixData, WAVE_TYPE_SINE, createSimpleWaveChunks(wrapFrequency, 0.8f));
        mixWave(waveData, mixData, fadeIdx, frameCount, (0.3f + AppWindow.random.nextFloat(0.2f)));

        // low pass and clip
        lowPassFilter(waveData, 0, frameCount, (0.05f + AppWindow.random.nextFloat(0.1f)));
        clip(waveData, bangIdx, fadeIdx, -(0.95f + AppWindow.random.nextFloat(0.5f)), (0.95f + AppWindow.random.nextFloat(0.5f)));

        // one more fade
        normalize(waveData, 0, frameCount, 0.5f);
        fade(waveData, bangIdx, fadeIdx);
    }

}
