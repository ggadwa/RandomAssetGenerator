package com.klinksoftware.rag.sound;

import com.klinksoftware.rag.sound.utility.SoundInterface;
import com.klinksoftware.rag.sound.utility.SoundBase;
import com.klinksoftware.rag.AppWindow;

@SoundInterface
public class SoundBang extends SoundBase {

    public SoundBang() {
        super();

        waveMillis = 100 + AppWindow.random.nextInt(200);
    }

    @Override
    public void generateInternal() {
        int frameCount;

        frameCount = waveData.length;

        createWhiteNoise(waveData, 0.8f);
        lowPassFilter(waveData, 0, frameCount, (0.05f + AppWindow.random.nextFloat(0.1f)));
        clip(waveData, 0, frameCount, -(0.5f + AppWindow.random.nextFloat(0.2f)), (0.5f + AppWindow.random.nextFloat(0.2f)));
        normalize(waveData, 0, frameCount, 0.5f);
        fade(waveData, (200 + AppWindow.random.nextInt(200)), (frameCount - (1000 + AppWindow.random.nextInt(2000))));
    }

}
