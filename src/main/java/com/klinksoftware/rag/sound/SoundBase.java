package com.klinksoftware.rag.sound;

import com.klinksoftware.rag.AppWindow;
import java.io.*;
import java.nio.*;
import java.util.ArrayList;
import javax.sound.sampled.*;

public class SoundBase {

    public static final float SAMPLE_RATE = 44100.0f;

    public static final int WAVE_TYPE_SINE = 0;
    public static final int WAVE_TYPE_SQUARE = 1;
    public static final int WAVE_TYPE_SAWTOOTH = 2;

    protected int waveMillis;
    protected float[] waveData;

    public SoundBase() {
        // will be reset in children classes

        waveMillis = 500;
        waveData = null;
    }

    protected int getFrameCount() {
        return ((int) (SAMPLE_RATE * (((float) waveMillis) / 1000.0f)));
    }

    //
    // waves
    //
    protected void createWave(float[] data, int waveType, ArrayList<WaveChunk> chunkList) {
        int n, idx, chunkFrameLen;
        int chunkLen, frameCount;
        float rd, period;
        float chunkSineSize, chunkPeriodSize, chunkAmplitudeSize;
        WaveChunk chunk;

        chunkLen = chunkList.size();
        frameCount = getFrameCount();

            // convert the chunks from % of position to
            // absolute frames and the frequency to
            // the proper sin

        for (n = 0; n != chunkLen; n++) {
            chunk = chunkList.get(n);
            chunk.frame = (int) (chunk.timePercentage * (float) frameCount);
            chunk.sineAdd = ((float) Math.PI * (chunk.frequency * 2.0f)) / SAMPLE_RATE;
            chunk.period = SAMPLE_RATE / chunk.frequency;
        }

            // run through all the sin waves

        idx=0;
        rd = 0.0f;

        chunkFrameLen = chunkList.get(1).frame - chunkList.get(0).frame;
        chunkSineSize = chunkList.get(1).sineAdd - chunkList.get(0).sineAdd;
        chunkPeriodSize = chunkList.get(1).period - chunkList.get(0).period;
        chunkAmplitudeSize = chunkList.get(1).amplitude - chunkList.get(0).amplitude;

        for (n = 0; n != frameCount; n++) {

            // the wave
            switch (waveType) {
                case WAVE_TYPE_SINE:
                    data[n] = (float) Math.sin(rd);
                    break;
                case WAVE_TYPE_SQUARE:
                    data[n] = (float) Math.signum(Math.sin(rd));
                    break;
                case WAVE_TYPE_SAWTOOTH:
                    period = (chunkList.get(idx).period + ((chunkPeriodSize * (float) (n - chunkList.get(idx).frame)) / (float) chunkFrameLen));
                    data[n] = (((float) n / period) - (float) Math.floor(((float) n / period) + 0.5f)) * 2.0f;
                    break;
            }

            // amplitude
            data[n] *= (chunkList.get(idx).amplitude + ((chunkAmplitudeSize * (float) (n - chunkList.get(idx).frame)) / (float) chunkFrameLen));

            if ((n != 0) && (n == chunkList.get(idx + 1).frame)) {
                if (idx<(chunkLen-2)) idx++;
                chunkFrameLen = chunkList.get(idx + 1).frame - chunkList.get(idx).frame;
                chunkSineSize = chunkList.get(idx + 1).sineAdd - chunkList.get(idx).sineAdd;
                chunkPeriodSize = chunkList.get(idx + 1).period - chunkList.get(idx).period;
                chunkAmplitudeSize = chunkList.get(idx + 1).amplitude - chunkList.get(idx).amplitude;
            }

            rd += (chunkList.get(idx).sineAdd + ((chunkSineSize * (float) (n - chunkList.get(idx).frame)) / (float) chunkFrameLen));
        }
    }

    protected void createSquareWave(float[] data, int[] chunkLens, float amplitude) {
        int n, idx, currentCount;
        int chunkLen, frameCount;

        chunkLen = chunkLens.length;
        frameCount = getFrameCount();

        idx = 0;
        currentCount = chunkLens[idx];

        for (n = 0; n != frameCount; n++) {

            // the wave
            data[n] = (((idx & 0x1) == 0x0) ? (-1.0f) : 1.0f) * amplitude;

            // next chunk
            currentCount--;
            if (currentCount <= 0) {
                idx++;
                if (idx >= chunkLen) {
                    idx = 0;
                }
                currentCount = chunkLens[idx];
            }
        }
    }

    protected ArrayList<WaveChunk> createSimpleWaveChunks(float hzFrequency, float amplitude) {
        ArrayList<WaveChunk> chunkList;

        chunkList = new ArrayList<>();
        chunkList.add(new WaveChunk(0.0f, hzFrequency, amplitude));
        chunkList.add(new WaveChunk(1.0f, hzFrequency, amplitude));

        return (chunkList);
    }

    protected void createWhiteNoise(float[] data, float range) {
        int n, frameCount;
        float doubleRange;

        doubleRange = range * 2.0f;
        frameCount = getFrameCount();

        for (n = 0; n != frameCount; n++) {
            data[n] = (AppWindow.random.nextFloat(doubleRange) - range);
        }
    }

    protected void createSquareWhiteNoise(float[] data, float hzFrequency, float range) {
        int n, frameCount;
        float rd, rdAdd, doubleRange;

        doubleRange = range * 2.0f;
        frameCount = getFrameCount();

        rd = 0.0f;
        rdAdd = ((float) Math.PI * (hzFrequency * 2.0f)) / SAMPLE_RATE;

        for (n = 0; n != frameCount; n++) {
            data[n] = ((float) Math.signum(Math.sin(rd)) * range) + (AppWindow.random.nextFloat(doubleRange) - range);
            rd += rdAdd;
        }
    }

    //
    // effects
    //
    protected void lowPassFilter(float[] data, float startPos, float endPos, float factor) {
        int n, frameStart, frameEnd, frameCount;
        float inverseFactor;

        frameCount = getFrameCount();
        frameStart = (int) ((float) frameCount * startPos);
        if (frameStart < 0) {
            frameStart = 0;
        }
        frameEnd = (int) ((float) frameCount * endPos);
        if (frameEnd > frameCount) {
            frameEnd = frameCount;
        }

        inverseFactor = 1.0f - factor;

        if (frameStart<1) frameStart=1;

        for (n=frameStart;n<frameEnd;n++) {
            data[n] += (factor * data[n]) + (inverseFactor * data[n - 1]);
        }
    }

    protected void delay(float[] data, float startPos, float endPos, int delayOffset, float mix) {
        int n, frameStart, frameEnd, frameCount, fadeFrameIndex, delayIdx;
        float fadeFactor, mixFactor;

        frameCount = getFrameCount();
        frameStart = (int) ((float) frameCount * startPos);
        if (frameStart < 0) {
            frameStart = 0;
        }
        frameEnd = (int) ((float) frameCount * endPos);
        if (frameEnd > frameCount) {
            frameEnd = frameCount;
        }

        fadeFrameIndex = frameStart + (int) ((float) (frameEnd - frameStart) * 0.1f);
        delayIdx = frameEnd + delayOffset;

        mixFactor = mix;
        fadeFactor = mix / (float) (fadeFrameIndex - frameStart);

        for (n=frameEnd;n>=frameStart;n--) {

                // the delay

            if (delayIdx < frameCount) {
                data[delayIdx] = (data[delayIdx] * (1.0f - mixFactor)) + (data[n] * mixFactor);
            }
            delayIdx--;

                // ramp down the mix if we are
                // past the fade start (we run the delay
                // backwards so the mix doesn't interfere
                // with the previous delay.)

            if (n<fadeFrameIndex) {
                mixFactor-=fadeFactor;
                if (mixFactor<=0.0) break;
            }
        }
    }

    protected void clip(float[] data, float startPos, float endPos, float min, float max) {
        int n, frameStart, frameEnd, frameCount;

        frameCount = getFrameCount();
        frameStart = (int) ((float) frameCount * startPos);
        if (frameStart < 0) {
            frameStart = 0;
        }
        frameEnd = (int) ((float) frameCount * endPos);
        if (frameEnd > frameCount) {
            frameEnd = frameCount;
        }

        for (n=frameStart;n<frameEnd;n++) {
            if (data[n]<min) {
                data[n]=min;
                continue;
            }
            if (data[n]>max) data[n]=max;
        }
    }

    protected void scale(float[] data, float startPos, float endPos, float factor) {
        int n, frameStart, frameEnd, frameCount;

        frameCount = getFrameCount();
        frameStart = (int) ((float) frameCount * startPos);
        if (frameStart < 0) {
            frameStart = 0;
        }
        frameEnd = (int) ((float) frameCount * endPos);
        if (frameEnd > frameCount) {
            frameEnd = frameCount;
        }

        for (n=frameStart;n<frameEnd;n++) {
            data[n]*=factor;
        }
    }

    protected void normalize(float[] data) {
        int n;
        float f, max;
        int frameCount;

        frameCount = getFrameCount();

            // get max value

        max = 0.0f;

        for (n = 0; n != frameCount; n++) {
            f = Math.abs(data[n]);
            if (f > max) {
                max = f;
            }
        }

        if (max == 0.0f) {
            return;
        }

            // normalize

        f = 1.0f / max;

        for (n = 0; n != frameCount; n++) {
            data[n]*=f;
        }
    }

    protected void fade(float[] data, float fadeInPos, float fadeOutPos) {
        int n, fadeStart, frameCount, fadeLen;

        frameCount = getFrameCount();

            // fade in

        if (fadeInPos != 0.0f) {
            fadeLen = (int) ((float) frameCount * fadeInPos);
            if (fadeLen > frameCount) {
                fadeLen = frameCount;
            }

            for (n=0;n<fadeLen;n++) {
                data[n] *= ((float) n / (float) fadeLen);
            }
        }

            // fade out

        if (fadeOutPos != 0.0f) {
            fadeLen = (int) ((float) frameCount * fadeOutPos);
            if (fadeLen > frameCount) {
                fadeLen = frameCount;
            }
            fadeStart = frameCount - fadeLen;

            for (n=fadeStart;n<frameCount;n++) {
                data[n] *= (1.0f - (((float) (n - fadeStart)) / (float) fadeLen));
            }
        }
    }

    //
    // mixing
    //

    protected void mixWave(float[] data, float[] mixData, float mixVolume, float startPos, float endPos) {
        int n, frameStart, frameEnd, frameCount;
        float dataVolume;

        frameCount = getFrameCount();
        frameStart = (int) ((float) frameCount * startPos);
        if (frameStart < 0) {
            frameStart = 0;
        }
        frameEnd = (int) ((float) frameCount * endPos);
        if (frameEnd > frameCount) {
            frameEnd = frameCount;
        }

        dataVolume = 1.0f - mixVolume;

        for (n = frameStart; n < frameEnd; n++) {
            data[n] = (data[n] * dataVolume) + (mixData[n] * mixVolume);
        }
    }

    //
    // audio utilities
    //
    public AudioInputStream createAudioStream() {
        int n, waveSize;
        float f;
        byte[] byteBuf;
        short[] shortBuf;
        AudioFormat format;

        waveSize = waveData.length;

        // conver to shorts
        shortBuf = new short[waveSize];

        for (n = 0; n != waveSize; n++) {
            f = waveData[n];
            if (f < -1.0f) {
                f = -1.0f;
            }
            if (f > 1.0f) {
                f = 1.0f;
            }
            shortBuf[n] = (short) (f * Short.MAX_VALUE);
        }

        // convert to bytes
        byteBuf = new byte[waveSize * 2];
        ByteBuffer.wrap(byteBuf).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(shortBuf);

        // to audio stream
        format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
        return (new AudioInputStream(new ByteArrayInputStream(byteBuf), format, waveSize));
    }

    public void play() {
        Clip clip;
        AudioInputStream stream;

        stream = createAudioStream();

        try {
            clip = AudioSystem.getClip();
            clip.open(stream);
            clip.start();

            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                stream.close();
            } catch (Exception e) {
            }
        }
    }

    protected void generateInternal() {
        createWave(waveData, WAVE_TYPE_SINE, createSimpleWaveChunks(440.0f, 0.8f));
    }

    public void generate() {
        // setup the wave
        waveData = new float[getFrameCount()];

        // run the internal generator
        generateInternal();
    }

    public float[] getWaveData() {
        return (waveData);
    }

    public void writeToFile(String path) {
        String name;
        File file;
        AudioInputStream stream;

        name = this.getClass().getSimpleName().substring(5).toLowerCase();
        file = new File(path + File.separator + name + ".wav");

        try {
            stream = createAudioStream();
            AudioSystem.write(stream, AudioFileFormat.Type.WAVE, new FileOutputStream(file));
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
