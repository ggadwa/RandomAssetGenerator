package com.klinksoftware.rag.sound;

@SoundInterface
public class SoundMonster extends SoundBase {

    public SoundMonster() {
        super();

        waveMillis = 1000;
        //waveMillis = 1500 + AppWindow.random.nextInt(1500);
    }

    @Override
    public void generateInternal() {
        int n, frameCount;
        float frequencyMin, frequencyMax;
        float rumbleFrequencyMin, rumbleFrequencyMax;
        float[] mixData;

        frameCount = getFrameCount();
        mixData = new float[frameCount];

        createSquareWave(waveData, new int[]{200, 50, 100, 100}, 0.5f);
        //createWave(waveData, WAVE_TYPE_SQUARE, createSimpleWaveChunks(200, 0.8f));
        //createWave(mixData, WAVE_TYPE_SINE, createSimpleWaveChunks(400, 0.5f));
        //mixWave(waveData, mixData, 0.5f, 0.0f, 1.0f);
/*

        frequencyMin = 50.0f + AppWindow.random.nextFloat(300.0f);
        frequencyMax = frequencyMin + AppWindow.random.nextFloat(300.0f);

        rumbleFrequencyMin = 50.0f + AppWindow.random.nextFloat(50.0f);
        rumbleFrequencyMax = rumbleFrequencyMin + AppWindow.random.nextFloat(200.0f);

        // add in a saw wave to change the timber a bit
        createWave(waveData, WAVE_TYPE_SINE, createSimpleWaveChunks((frequencyMin + AppWindow.random.nextFloat(frequencyMax - frequencyMin)), 0.8f));
        createWave(mixData, WAVE_TYPE_SINE, createSimpleWaveChunks((frequencyMin + AppWindow.random.nextFloat(frequencyMax - frequencyMin)), 0.85f));
        mixWave(waveData, mixData, 0.5f, 0.0f, 1.0f);

        createWave(mixData, WAVE_TYPE_SINE, createSimpleWaveChunks((rumbleFrequencyMin + AppWindow.random.nextFloat(rumbleFrequencyMax - rumbleFrequencyMin)), 0.8f));
        mixWave(waveData, mixData, 0.5f, 0.0f, 1.0f);

            // normalize it, randomly clip, low pass, and delay it

        normalize(waveData);
        lowPassFilter(waveData, 0.0f, 1.0f, AppWindow.random.nextFloat());

        //delay(waveData, 0, frameCount, (int) (frameCount * 0.1f), (AppWindow.random.nextFloat(0.5f)));

            // finally normalize and fade

        normalize(waveData);
        fade(waveData, 0.1f, 0.2f);
         */
    }

}
