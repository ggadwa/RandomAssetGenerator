package com.klinksoftware.rag.uiworker;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.sound.SoundBase;
import java.util.*;
import javax.swing.*;

public class SoundBuildWorker extends SwingWorker<Integer, Void> {

    private AppWindow appWindow;
    private String soundName;

    public static SoundBase generatedSound = null;

    public SoundBuildWorker(AppWindow appWindow, String soundName) {
        this.appWindow = appWindow;
        this.soundName = soundName;
    }

    @Override
    protected Integer doInBackground() throws Exception {
        long seed;
        SoundBase sound;

        appWindow.enableSettings(false);

        // get a random seed and generate the sound
        seed = Calendar.getInstance().getTimeInMillis();
        AppWindow.random.setSeed(seed);

        try {
            sound = (SoundBase) (Class.forName("com.klinksoftware.rag.sound.Sound" + soundName.replace(" ", ""))).getConstructor(null).newInstance(null);
            sound.generate();
        } catch (Exception e) {
            e.printStackTrace();
            return (0);
        }

        generatedSound = sound;

        return (0);
    }

    @Override
    protected void done() {
        appWindow.enableSettings(true);
    }
}
