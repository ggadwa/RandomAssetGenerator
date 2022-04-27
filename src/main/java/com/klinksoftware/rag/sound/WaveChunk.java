package com.klinksoftware.rag.sound;

public class WaveChunk {
    public int frame;
    public float timePercentage, frequency, amplitude, sineAdd, period;

    public WaveChunk(float timePercentage, float frequency, float amplitude) {
        this.timePercentage = timePercentage;
        this.frequency = frequency;
        this.amplitude = amplitude;

        frame = 0;
        sineAdd = 0.0f;
        period = 0.0f;
    }
}
