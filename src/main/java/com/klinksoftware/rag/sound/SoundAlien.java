package com.klinksoftware.rag.sound;

import com.klinksoftware.rag.AppWindow;
import java.util.ArrayList;

@SoundInterface
public class SoundAlien extends SoundBase {

    public SoundAlien() {
        super();

        waveMillis = 1500 + AppWindow.random.nextInt(1500);
    }

    @Override
    public void generateInternal() {
        int n, frameCount, wavCount;
        float dataPos, frequency, frequencyMin, frequencyMax;
        float rumbleFrequencyMin, rumbleFrequencyMax;
        float[] mixData;
        ArrayList<WaveChunk> chunkList;

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

                chunkList.add(new WaveChunk(dataPos, frequency, 0.8f));

                if (dataPos == 1.0f) {
                    break;
                }
                dataPos += AppWindow.random.nextFloat(0.2f);

                frequency += (100.0f - (AppWindow.random.nextFloat(200.0f)));
                if (frequency < frequencyMin) {
                    frequency = frequencyMin;
                }
                if (frequency > frequencyMax) {
                    frequency = frequencyMax;
                }
            }

            if (n == 0) {
                createWave(waveData, WAVE_TYPE_SINE, chunkList);
            } else {
                createWave(mixData, WAVE_TYPE_SINE, chunkList);
                mixWave(waveData, mixData, 0, frameCount, (0.4f + AppWindow.random.nextFloat(0.2f)));
            }
        }

        normalize(waveData, 0, frameCount, 1.0f);

        // normalize it, randomly clip, low pass, and delay it
        clip(waveData, 0, frameCount, (-1.0f + (AppWindow.random.nextFloat(0.2f))), (1.0f - (AppWindow.random.nextFloat(0.2f))));
        lowPassFilter(waveData, 0, frameCount, AppWindow.random.nextFloat());

        delay(waveData, 0, frameCount, (int) (frameCount * 0.1f), (AppWindow.random.nextFloat(0.5f)));

        // finally normalize and fade
        normalize(waveData, 0, frameCount, 0.5f);
        fade(waveData, (1000 + AppWindow.random.nextInt(2000)), (frameCount - (2000 + AppWindow.random.nextInt(3000))));
    }

}
