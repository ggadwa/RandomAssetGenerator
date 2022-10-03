package com.klinksoftware.rag.uiworker;

import com.klinksoftware.rag.AppWindow;
import java.io.*;
import javax.swing.*;

public class PropExportWorker extends SwingWorker<Integer, Void> {

    private AppWindow appWindow;

    public PropExportWorker(AppWindow appWindow) {
        this.appWindow = appWindow;
    }

    @Override
    protected Integer doInBackground() throws Exception {
        File file;
        JFileChooser fileChooser;

        // skip if nothing generated
        if (PropBuildWorker.generatedProp == null) {
            JOptionPane.showMessageDialog(appWindow, "Generate a prop first.");
            return (0);
        }

        appWindow.startBuild();

        // pick folder to save too
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showSaveDialog(appWindow) == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
            PropBuildWorker.generatedProp.writeToFile(file.getAbsolutePath());
        }

        return (0);
    }

    @Override
    protected void done() {
        appWindow.stopBuild();
    }
}
