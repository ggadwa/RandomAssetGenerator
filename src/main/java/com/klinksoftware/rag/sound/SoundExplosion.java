package com.klinksoftware.rag.sound;

import com.klinksoftware.rag.AppWindow;

public class SoundExplosion extends SoundBase {

    public SoundExplosion() {
        super();

        waveMillis = 2000 + AppWindow.random.nextInt(2000);
    }

    @Override
    public void generateInternal() {
        int frameCount, bangPosition;
        float[] mixData;

        frameCount = getFrameCount();
        mixData = new float[frameCount];

        bangPosition = (int) (frameCount * (0.1f + (AppWindow.random.nextFloat(0.25f))));

            // original tone

        createSineWave(waveData, 0, frameCount, 30);
        createSineWave(mixData, 0, frameCount, 55);
        mixWave(waveData, mixData, 0.5f, 0, frameCount);
        mixWhiteNoise(waveData, 0, frameCount, 0.0f, 1.0f, 0.25f);
        lowPassFilter(waveData, 0, frameCount, 0.15f);

            // this part of the clip is the 'bang'
            // part of the exposion, so clip that
            // and then scale the rest to match

        normalize(waveData);
        clip(waveData, 0, bangPosition, -0.5f, 0.5f);
        clip(waveData, bangPosition, frameCount, -0.9f, 0.9f);
        scale(waveData, bangPosition, frameCount, 0.7f);

            // now normalize and fade the start/finish

        normalize(waveData);
        fade(waveData, 0.1f, 0.75f);
    }

}
