package com.klinksoftware.rag.uiworker;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.sound.SoundBase;
import com.klinksoftware.rag.sound.SoundExplosion;
import com.klinksoftware.rag.sound.SoundGunFire;
import com.klinksoftware.rag.sound.SoundMonster;
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

        switch (soundName) {
            case "Gun Fire":
                sound = new SoundGunFire();
                break;
            case "Explosion":
                sound = new SoundExplosion();
                break;
            case "Monster":
                sound = new SoundMonster();
                break;
            default:
                sound = new SoundBase();
                break;
        }

        sound.generate();
        generatedSound = sound;

        return (0);
    }

    @Override
    protected void done() {
        appWindow.enableSettings(true);
    }
}
