package com.klinksoftware.rag.sound.utility;

import com.klinksoftware.rag.sound.utility.WaveChunk;
import com.klinksoftware.rag.AppWindow;
import java.io.*;
import java.nio.*;
import java.util.ArrayList;
import javax.sound.sampled.*;

public class SoundBase {

    public static final float SAMPLE_RATE = 44100.0f;

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
    protected void createSineWave(float[] data, ArrayList<WaveChunk> chunkList) {
        int n, idx, chunkFrameLen;
        int chunkLen, frameCount;
        float rd;
        float chunkSineSize, chunkAmplitudeSize;
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
        }

            // run through all the sin waves

        idx=0;
        rd = 0.0f;

        chunkFrameLen = chunkList.get(1).frame - chunkList.get(0).frame;
        chunkSineSize = chunkList.get(1).sineAdd - chunkList.get(0).sineAdd;
        chunkAmplitudeSize = chunkList.get(1).amplitude - chunkList.get(0).amplitude;

        for (n = 0; n != frameCount; n++) {

            // the wave
            data[n] = (float) Math.sin(rd);

            // amplitude
            data[n] *= (chunkList.get(idx).amplitude + ((chunkAmplitudeSize * (float) (n - chunkList.get(idx).frame)) / (float) chunkFrameLen));

            if ((n != 0) && (n == chunkList.get(idx + 1).frame)) {
                if (idx<(chunkLen-2)) idx++;
                chunkFrameLen = chunkList.get(idx + 1).frame - chunkList.get(idx).frame;
                chunkSineSize = chunkList.get(idx + 1).sineAdd - chunkList.get(idx).sineAdd;
                chunkAmplitudeSize = chunkList.get(idx + 1).amplitude - chunkList.get(idx).amplitude;
            }

            rd += (chunkList.get(idx).sineAdd + ((chunkSineSize * (float) (n - chunkList.get(idx).frame)) / (float) chunkFrameLen));
        }
    }

    protected void createSquareWave(float[] data, int[] chunkLens, int startIdx, int endIdx, float factor, float amplitude) {
        int n, idx, currentCount;
        int chunkLen, frameCount;

        chunkLen = chunkLens.length;
        frameCount = getFrameCount();
        endIdx = Math.min(endIdx, frameCount);

        idx = 0;
        currentCount = (int) ((float) chunkLens[idx] * factor);

        for (n = startIdx; n < endIdx; n++) {

            // the wave
            data[n] = (((idx & 0x1) == 0x0) ? (-1.0f) : 1.0f) * amplitude;

            // next chunk
            currentCount--;
            if (currentCount <= 0) {
                idx++;
                if (idx >= chunkLen) {
                    idx = 0;
                }
                currentCount = (int) ((float) chunkLens[idx] * factor);
            }
        }
    }

    protected void createSawWave(float[] data, int startIdx, int endIdx, float frequency, float amplitude) {
        int n;
        float period;

        period = SAMPLE_RATE / frequency;

        // run through all the sin waves
        for (n = startIdx; n < endIdx; n++) {
            data[n] = (((float) n / period) - (float) Math.floor(((float) n / period) + 0.5f)) * -2.0f;
            data[n] *= amplitude;
        }
    }

    protected ArrayList<WaveChunk> createSimpleWaveChunks(float hzFrequency, float amplitude) {
        ArrayList<WaveChunk> chunkList;

        chunkList = new ArrayList<>();
        chunkList.add(new WaveChunk(0.0f, hzFrequency, amplitude));
        chunkList.add(new WaveChunk(1.0f, hzFrequency, amplitude));

        return (chunkList);
    }

    //
    // white noise
    //
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
    // randomizers
    //
    protected void mixRandomAmplituteSpike(float[] data, int idx, float size, float reduceSize) {
        int lftIdx, rgtIdx, frameCount;
        float fSize;

        frameCount = data.length;

        if (data[idx] < 0.0f) {
            data[idx] = Math.min(0.0f, (data[idx] + size));

            lftIdx = idx - 1;
            fSize = size;
            while ((lftIdx > 0) && (fSize > 0.05f)) {
                fSize *= AppWindow.random.nextFloat(reduceSize);
                data[lftIdx] = Math.min(0.0f, (data[lftIdx] + fSize));
                lftIdx--;
            }

            rgtIdx = idx + 1;
            fSize = size;
            while ((rgtIdx < frameCount) && (fSize > 0.05f)) {
                fSize *= AppWindow.random.nextFloat(reduceSize);
                data[rgtIdx] = Math.min(0.0f, (data[rgtIdx] + fSize));
                rgtIdx++;
            }

        } else {
            data[idx] = Math.max(0.0f, (data[idx] - size));

            lftIdx = idx - 1;
            fSize = size;
            while ((lftIdx > 0) && (fSize > 0.05f)) {
                fSize *= AppWindow.random.nextFloat(reduceSize);
                data[lftIdx] = Math.max(0.0f, (data[lftIdx] - fSize));
                lftIdx--;
            }

            rgtIdx = idx + 1;
            fSize = size;
            while ((rgtIdx < frameCount) && (fSize > 0.05f)) {
                fSize *= AppWindow.random.nextFloat(reduceSize);
                data[rgtIdx] = Math.max(0.0f, (data[rgtIdx] - fSize));
                rgtIdx++;
            }
        }
    }

    //
    // effects
    //
    protected void lowPassFilter(float[] data, int startIdx, int endIdx, float factor) {
        int n;
        float inverseFactor;

        inverseFactor = 1.0f - factor;

        if (startIdx < 1) {
            startIdx = 1;
        }

        for (n = startIdx; n < endIdx; n++) {
            data[n] += (factor * data[n]) + (inverseFactor * data[n - 1]);
        }
    }

    protected void delay(float[] data, int startIdx, int endIdx, int delayOffset, float mix) {
        int n, frameCount, fadeFrameIndex, delayIdx;
        float fadeFactor, mixFactor;

        frameCount = getFrameCount();

        fadeFrameIndex = startIdx + (int) ((float) (endIdx - startIdx) * 0.1f);
        delayIdx = endIdx + delayOffset;

        mixFactor = mix;
        fadeFactor = mix / (float) (fadeFrameIndex - startIdx);

        for (n = endIdx; n >= startIdx; n--) {

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

    protected void clip(float[] data, int startIdx, int endIdx, float min, float max) {
        int n;

        for (n = startIdx; n < endIdx; n++) {
            if (data[n]<min) {
                data[n]=min;
                continue;
            }
            if (data[n]>max) data[n]=max;
        }
    }

    protected void scale(float[] data, int startIdx, int endIdx, float factor) {
        int n;

        for (n = startIdx; n < endIdx; n++) {
            data[n]*=factor;
        }
    }

    protected void normalize(float[] data, int startIdx, int endIdx, float maxAmplitute) {
        int n;
        float f, max;

            // get max value

        max = 0.0f;

        for (n = startIdx; n != endIdx; n++) {
            f = Math.abs(data[n]);
            if (f > max) {
                max = f;
            }
        }

        if (max == 0.0f) {
            return;
        }

        // normalize
        f = (1.0f / max) * maxAmplitute;

        for (n = startIdx; n != endIdx; n++) {
            data[n] *= f;
        }
    }

    protected void fade(float[] data, int fadeInIdx, int fadeOutIdx) {
        int n, frameCount, fadeLen;

        frameCount = getFrameCount();

            // fade in

        if (fadeInIdx != 0) {
            for (n = 0; n < fadeInIdx; n++) {
                data[n] *= ((float) n / (float) fadeInIdx);
            }
        }

            // fade out

        if (fadeOutIdx != 0) {
            fadeLen = frameCount - fadeOutIdx;

            for (n = fadeOutIdx; n < frameCount; n++) {
                data[n] *= (1.0f - (((float) (n - fadeOutIdx)) / (float) fadeLen));
            }
        }
    }

    //
    // mixing
    //

    protected void mixWave(float[] data, float[] mixData, int startIdx, int endIdx, float mixVolume) {
        int n;
        float dataVolume;

        dataVolume = 1.0f - mixVolume;

        for (n = startIdx; n < endIdx; n++) {
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
        createSineWave(waveData, createSimpleWaveChunks(440.0f, 0.8f));
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
