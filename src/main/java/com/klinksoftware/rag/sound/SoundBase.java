package com.klinksoftware.rag.sound;

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
    protected void createSineWave(float[] data, int frameStart, int frameEnd, float hzFrequency) {
        int n;
        float rd, rdAdd;

        rd = 0.0f;
        rdAdd = ((float) Math.PI * (hzFrequency * 2.0f)) / SAMPLE_RATE;

        for (n = frameStart; n < frameEnd; n++) {
            data[n] = (float) Math.sin(rd);
            rd += rdAdd;
        }
    }

    protected void createSineMultipleWaves(float[] data, ArrayList<SineWaveChunk> chunkList)    {
        int n, idx, chunkFrameLen;
        int chunkLen, frameCount;
        float rd, chunkSineSize;
        SineWaveChunk chunk;

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

        for (n = 0; n != frameCount; n++) {
            data[n] = (float) Math.sin(rd);

            if ((n != 0) && (n == chunkList.get(idx + 1).frame)) {
                if (idx<(chunkLen-2)) idx++;
                chunkFrameLen = chunkList.get(idx + 1).frame - chunkList.get(idx).frame;
                chunkSineSize = chunkList.get(idx + 1).sineAdd - chunkList.get(idx).sineAdd;
            }

            rd += (chunkList.get(idx).sineAdd + ((chunkSineSize * (float) (n - chunkList.get(idx).frame)) / (float) chunkFrameLen));
        }
    }

    protected void createSquareWave(float[] data, int frameStart, int frameEnd, float hzFrequency)    {
        int n;
        float rd, rdAdd;

        rd = 0.0f;
        rdAdd = ((float) Math.PI * (hzFrequency * 2.0f)) / SAMPLE_RATE;

        for (n=frameStart;n<frameEnd;n++) {
            data[n] = (float) Math.signum(Math.sin(rd));
            rd+=rdAdd;
        }
    }

    protected void createTriangleWave(float[] data, int frameStart, int frameEnd, float hzFrequency)    {
        int n;
        float period;

        period = SAMPLE_RATE / hzFrequency;

        for (n=frameStart;n<frameEnd;n++) {
            data[n] = (Math.abs(((n / period) - (float) Math.floor(((float) n / period) + 0.5f)) * 2.0f) * 2.0f) - 1.0f;
        }
    }

    protected void createSawToothWave(float[] data, int frameStart, int frameEnd, float hzFrequency)    {
        int n;
        float period;

        period = SAMPLE_RATE / hzFrequency;

        for (n=frameStart;n<frameEnd;n++) {
            data[n] = (((float) n / period) - (float) Math.floor(((float) n / period) + 0.5f)) * 2.0f;
        }
    }

    //
    // effects
    //

    protected void mixWhiteNoise(float[] data, int frameStart, int frameEnd, float minAmp, float maxAmp, float range) {
        int n;
        float doubleRange = range * 2.0f;

        for (n = frameStart; n < frameEnd; n++) {
            if ((data[n] >= minAmp) && (data[n] <= maxAmp)) {
                data[n] += (AppWindow.random.nextFloat() * doubleRange) - range;
            }
        }
    }

    protected void lowPassFilter(float[] data, int frameStart, int frameEnd, float factor)    {
        int n;
        float inverseFactor;

        inverseFactor = 1.0f - factor;

        if (frameStart<1) frameStart=1;

        for (n=frameStart;n<frameEnd;n++) {
            data[n]+=(factor*data[n])+(inverseFactor*data[n-1]);
        }
    }

    protected void delay(float[] data, int frameStart, int frameEnd, int delayOffset, float mix)    {
        int n, frameCount, fadeFrameIndex, delayIdx;
        float fadeFactor, mixFactor;

        frameCount = getFrameCount();
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

    protected void clip(float[] data, int frameStart, int frameEnd, float min, float max)    {
        int n;

        for (n=frameStart;n<frameEnd;n++) {
            if (data[n]<min) {
                data[n]=min;
                continue;
            }
            if (data[n]>max) data[n]=max;
        }
    }

    protected void scale(float[] data, int frameStart, int frameEnd, float factor)    {
        int n;

        for (n=frameStart;n<frameEnd;n++) {
            data[n]*=factor;
        }
    }

    protected void normalize(float[] data)    {
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

    protected void fade(float[] data, float fadeIn, float fadeOut)    {
        int n, fadeStart, frameCount;
        float fadeLen;

        frameCount = getFrameCount();

            // fade in

        if (fadeIn != 0.0f) {
            fadeLen = (float) frameCount * fadeIn;

            for (n=0;n<fadeLen;n++) {
                data[n] *= ((float) n / fadeLen);
            }
        }

            // fade out

        if (fadeOut != 0.0f) {
            fadeLen = (float) frameCount * fadeOut;
            fadeStart = frameCount - (int) fadeLen;

            for (n=fadeStart;n<frameCount;n++) {
                data[n] *= (1.0f - (((float) n - fadeStart) / fadeLen));
            }
        }
    }

    //
    // mixing
    //

    protected void mixWave(float[] data, float[] mixData, float mixVolume, int frameStart, int frameEnd) {
        int n;
        float dataVolume;

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
        createSineWave(waveData, 0, waveData.length, 440.0f);
    }

    public void generate() {
        // setup the wave
        waveData = new float[getFrameCount()];

        // run the internal generator
        generateInternal();

        // play the sound
        play();
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
