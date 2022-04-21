package com.klinksoftware.rag.uiworker;

import com.klinksoftware.rag.AppWindow;
import java.io.*;
import javax.swing.*;

public class SoundExportWorker extends SwingWorker<Integer, Void> {

    private AppWindow appWindow;

    public SoundExportWorker(AppWindow appWindow) {
        this.appWindow = appWindow;
    }

    @Override
    protected Integer doInBackground() throws Exception {
        File file;
        JFileChooser fileChooser;

        // skip if nothing generated
        if (SoundBuildWorker.generatedSound == null) {
            JOptionPane.showMessageDialog(appWindow.frame, "Generate a sound first.");
            return (0);
        }

        appWindow.enableSettings(false);

        // pick folder to save too
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showSaveDialog(appWindow.frame) == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
            SoundBuildWorker.generatedSound.writeToFile(file.getAbsolutePath());
        }

        return (0);
    }

    @Override
    protected void done() {
        appWindow.enableSettings(true);
    }
}
