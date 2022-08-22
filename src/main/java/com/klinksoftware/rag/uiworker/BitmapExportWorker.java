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
        File file;
        JFileChooser fileChooser;

        // skip if nothing generated
        if (BitmapBuildWorker.generatedBitmap == null) {
            JOptionPane.showMessageDialog(appWindow, "Generate a texture first.");
            return (0);
        }

        appWindow.startBuild();

        // pick folder to save too
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showSaveDialog(appWindow) == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
            BitmapBuildWorker.generatedBitmap.writeToFile(file.getAbsolutePath(), null);
        }

        return (0);
    }

    @Override
    protected void done() {
        appWindow.stopBuild();
    }
}
