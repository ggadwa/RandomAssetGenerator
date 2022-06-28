package com.klinksoftware.rag.uiworker;

import com.klinksoftware.rag.AppWindow;
import java.io.*;
import javax.swing.*;

public class ModelExportWorker extends SwingWorker<Integer, Void> {

    private AppWindow appWindow;

    public ModelExportWorker(AppWindow appWindow) {
        this.appWindow = appWindow;
    }

    @Override
    protected Integer doInBackground() throws Exception {
        File file;
        JFileChooser fileChooser;

        // skip if nothing generated
        if (ModelBuildWorker.generatedModel == null) {
            JOptionPane.showMessageDialog(appWindow, "Generate a model first.");
            return (0);
        }

        appWindow.enableSettings(false);

        // pick folder to save too
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showSaveDialog(appWindow) == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
            ModelBuildWorker.generatedModel.writeToFile(file.getAbsolutePath());
        }

        return (0);
    }

    @Override
    protected void done() {
        appWindow.enableSettings(true);
    }
}
