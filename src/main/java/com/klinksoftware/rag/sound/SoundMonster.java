package com.klinksoftware.rag.sound;

import com.klinksoftware.rag.AppWindow;

public class SoundMonster extends SoundBase {

    public SoundMonster() {
        super();

        waveMillis = 1500 + AppWindow.random.nextInt(1500);
    }

    @Override
    public void generateInternal() {
        int n, frameCount, wavCount;
        float dataPos, frequency, frequencyMin, frequencyMax;
        float rumbleFrequencyMin, rumbleFrequencyMax;
        float[] mixData;

        frameCount = getFrameCount();
        mixData = new float[frameCount];

        frequencyMin = 50.0f + AppWindow.random.nextFloat(300.0f);
        frequencyMax = frequencyMin + AppWindow.random.nextFloat(300.0f);

        rumbleFrequencyMin = 50.0f + AppWindow.random.nextFloat(50.0f);
        rumbleFrequencyMax = rumbleFrequencyMin + AppWindow.random.nextFloat(200.0f);


        // add in a saw wave to change the timber a bit
        createSineWave(waveData, 0, frameCount, 40);
        createSawToothWave(mixData, 0, frameCount, 60);
        mixWave(waveData, mixData, 0.5f, 0, frameCount);

        createSawToothWave(mixData, 0, frameCount, (rumbleFrequencyMin + (AppWindow.random.nextFloat() * (rumbleFrequencyMax - rumbleFrequencyMin))));
        mixWave(waveData, mixData, 0.5f, 0, frameCount);

            // normalize it, randomly clip, low pass, and delay it

        normalize(waveData);
        lowPassFilter(waveData, 0, frameCount, AppWindow.random.nextFloat());

        //delay(waveData, 0, frameCount, (int) (frameCount * 0.1f), (AppWindow.random.nextFloat(0.5f)));

            // finally normalize and fade

        normalize(waveData);
        fade(waveData, 0.1f, 0.2f);
    }

}
