package com.klinksoftware.rag.uiworker;

import com.klinksoftware.rag.AppWindow;
import javax.swing.*;

public class SoundPlayAgainWorker extends SwingWorker<Integer, Void> {

    private AppWindow appWindow;

    public SoundPlayAgainWorker(AppWindow appWindow) {
        this.appWindow = appWindow;
    }

    @Override
    protected Integer doInBackground() throws Exception {

        // skip if nothing generated
        if (SoundBuildWorker.generatedSound == null) {
            JOptionPane.showMessageDialog(appWindow.frame, "Generate a sound first.");
            return (0);
        }

        appWindow.enableSettings(false);

        // play it
        SoundBuildWorker.generatedSound.play();

        return (0);
    }

    @Override
    protected void done() {
        appWindow.enableSettings(true);
    }
}
