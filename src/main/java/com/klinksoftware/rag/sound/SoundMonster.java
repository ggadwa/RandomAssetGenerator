package com.klinksoftware.rag.sound;

import com.klinksoftware.rag.AppWindow;
import java.util.ArrayList;

public class SoundMonster extends SoundBase {

    public SoundMonster() {
        super();

        waveMillis = 500 + AppWindow.random.nextInt(1000);
    }

    @Override
    public void generateInternal() {
        int n, frameCount, wavCount;
        float dataPos, frequency, frequencyMin, frequencyMax;
        float rumbleFrequencyMin, rumbleFrequencyMax;
        float[] mixData;
        ArrayList<SineWaveChunk> chunkList;

        frameCount = getFrameCount();
        mixData = new float[frameCount];

        frequencyMin = 50.0f + AppWindow.random.nextFloat(300.0f);
        frequencyMax = frequencyMin + AppWindow.random.nextFloat(300.0f);

        rumbleFrequencyMin = 50.0f + AppWindow.random.nextFloat(50.0f);
        rumbleFrequencyMax = rumbleFrequencyMin + AppWindow.random.nextFloat(200.0f);

        wavCount = 1 + AppWindow.random.nextInt(3);
        chunkList = new ArrayList<>();

        for (n = 0; n != wavCount; n++) {
            dataPos = 0.0f;
            frequency = frequencyMin + (AppWindow.random.nextFloat() * (frequencyMax - frequencyMin));

            chunkList.clear();

            while (true) {
                if (dataPos > 1.0f) {
                    dataPos = 1.0f;
                }

                chunkList.add(new SineWaveChunk(dataPos, frequency));

                if (dataPos == 1.0f) {
                    break;
                }
                dataPos += AppWindow.random.nextFloat(0.2f);

                frequency += (100.0f - (AppWindow.random.nextFloat(200.0f)));
                if (frequency<frequencyMin) frequency=frequencyMin;
                if (frequency>frequencyMax) frequency=frequencyMax;
            }

            if (n == 0) {
                createSineMultipleWaves(waveData, chunkList);
            }
            else {
                createSineMultipleWaves(mixData, chunkList);
                mixWave(waveData, mixData, (0.4f + AppWindow.random.nextFloat(0.2f)), 0, frameCount);
            }
        }

            // add in a saw wave to change the timber a bit

        createSawToothWave(mixData, 0, frameCount, (rumbleFrequencyMin + (AppWindow.random.nextFloat() * (rumbleFrequencyMax - rumbleFrequencyMin))));
        mixWave(waveData, mixData, 0.05f, 0, frameCount);

            // normalize it, randomly clip, low pass, and delay it

        normalize(waveData);
        clip(waveData, 0, frameCount, (-1.0f + (AppWindow.random.nextFloat(0.2f))), (1.0f - (AppWindow.random.nextFloat(0.2f))));
        lowPassFilter(waveData, 0, frameCount, AppWindow.random.nextFloat());

        delay(waveData, 0, frameCount, (int) (frameCount * 0.1f), (AppWindow.random.nextFloat(0.5f)));

            // finally normalize and fade

        normalize(waveData);
        fade(waveData, 0.1f, 0.2f);
    }

}
