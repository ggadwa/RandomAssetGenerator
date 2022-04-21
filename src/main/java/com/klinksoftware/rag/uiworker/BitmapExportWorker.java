package com.klinksoftware.rag.uiworker;

import com.klinksoftware.rag.AppWindow;
import java.io.*;
import javax.swing.*;

public class BitmapExportWorker extends SwingWorker<Integer, Void> {

    private AppWindow appWindow;

    public BitmapExportWorker(AppWindow appWindow) {
        this.appWindow = appWindow;
    }

    @Override
    protected Integer doInBackground() throws Exception {
        long seed;
        String buildName, basePath;
        File file;
        JFileChooser fileChooser;

        // skip if nothing generated
        if (BitmapBuildWorker.generatedBitmap == null) {
            JOptionPane.showMessageDialog(appWindow.frame, "Generate a texture first.");
            return (0);
        }

        appWindow.enableSettings(false);

        // pick folder to save too
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showSaveDialog(appWindow.frame) == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
            BitmapBuildWorker.generatedBitmap.writeToFile(file.getAbsolutePath());
        }

        return (0);
    }

    @Override
    protected void done() {
        appWindow.enableSettings(true);
    }
}
