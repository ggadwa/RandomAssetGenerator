package com.klinksoftware.rag.sound;

public class SineWaveChunk {
    public int frame;
    public float timePercentage, frequency, sineAdd;

    public SineWaveChunk(float timePercentage, float frequency) {
        this.timePercentage = timePercentage;
        this.frequency = frequency;

        frame = 0;
        sineAdd = 0.0f;
    }
}
