package com.klinksoftware.rag.uiworker;

import com.klinksoftware.rag.AppWindow;
import java.io.*;
import javax.swing.*;

public class CharacterExportWorker extends SwingWorker<Integer, Void> {

    private AppWindow appWindow;

    public CharacterExportWorker(AppWindow appWindow) {
        this.appWindow = appWindow;
    }

    @Override
    protected Integer doInBackground() throws Exception {
        File file;
        JFileChooser fileChooser;

        // skip if nothing generated
        if (CharacterBuildWorker.generatedCharacter == null) {
            JOptionPane.showMessageDialog(appWindow, "Generate a character first.");
            return (0);
        }

        appWindow.startBuild();

        // pick folder to save too
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showSaveDialog(appWindow) == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
            CharacterBuildWorker.generatedCharacter.writeToFile(file.getAbsolutePath());
        }

        return (0);
    }

    @Override
    protected void done() {
        appWindow.stopBuild();
    }
}
