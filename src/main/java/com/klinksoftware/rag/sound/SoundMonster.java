package com.klinksoftware.rag.sound;

import com.klinksoftware.rag.sound.utility.SoundInterface;
import com.klinksoftware.rag.sound.utility.SoundBase;
import com.klinksoftware.rag.AppWindow;

@SoundInterface
public class SoundMonster extends SoundBase {

    public SoundMonster() {
        super();

        waveMillis = 1000 + AppWindow.random.nextInt(1000);
    }

    @Override
    public void generateInternal() {
        int n, frameCount, chunkCount, toneCount;
        int offset, toneFrameCount;
        int randomCount;
        float toneFactor, amplitute;
        float[] mixData;
        int[] chunks;

        // start with a randomized square wave
        frameCount = getFrameCount();

        chunkCount = 5 + AppWindow.random.nextInt(10);

        chunks = new int[chunkCount];
        for (n = 0; n != chunkCount; n++) {
            chunks[n] = 20 + AppWindow.random.nextInt(200);
        }

        toneCount = 5 + AppWindow.random.nextInt(20);
        toneFrameCount = frameCount / toneCount;

        offset = 0;
        toneFactor = 1.0f;
        amplitute = 0.5f;

        for (n = 0; n != toneCount; n++) {
            createSquareWave(waveData, chunks, offset, (offset + toneFrameCount), toneFactor, amplitute);

            offset += toneFrameCount;
            if (offset >= frameCount) {
                break;
            }

            toneFactor += (AppWindow.random.nextFloat(0.05f) - 0.025f);
            toneFactor = Math.min(4.0f, Math.max(toneFactor, 0.25f));
            amplitute += (AppWindow.random.nextFloat(0.1f) - 0.05f);
            amplitute = Math.min(0.8f, Math.max(amplitute, 0.3f));
        }

        // randomize it a bit
        randomCount = (frameCount / 10) + AppWindow.random.nextInt(frameCount / 10);

        for (n = 0; n != randomCount; n++) {
            offset = AppWindow.random.nextInt(frameCount);
            mixRandomAmplituteSpike(waveData, offset, (0.05f + AppWindow.random.nextFloat(0.15f)), 0.025f);
        }

        // smooth it out a bit
        if (AppWindow.random.nextBoolean()) {
            mixData = new float[frameCount];
            createSineWave(mixData, createSimpleWaveChunks((100 + AppWindow.random.nextInt(400)), 0.5f));
            mixWave(waveData, mixData, 0, frameCount, 0.2f);
        }

        // finalize
        lowPassFilter(waveData, 0, frameCount, AppWindow.random.nextFloat());

        if (AppWindow.random.nextBoolean()) {
            delay(waveData, 0, frameCount, (int) ((float) frameCount * 0.1f), (AppWindow.random.nextFloat(0.5f)));
        }

        normalize(waveData, 0, frameCount, 0.5f);
        fade(waveData, (1000 + AppWindow.random.nextInt(2000)), (frameCount - (2000 + AppWindow.random.nextInt(3000))));
    }

}
