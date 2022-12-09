package com.klinksoftware.rag;

import com.klinksoftware.rag.sound.utility.SoundInterface;
import com.klinksoftware.rag.uiworker.SoundBuildWorker;
import com.klinksoftware.rag.uiworker.SoundExportWorker;
import javax.swing.JButton;
import javax.swing.JList;

public class SettingsSound extends SettingsBase {

    private static final int BUTTON_GENERATE_SOUND = 0;
    private static final int BUTTON_EXPORT_SOUND = 1;

    private static final String[] SOUND_ITEMS = {"Alien", "Bang", "Explosion", "Monster"};

    private JButton generateSoundButton, exportSoundButton;
    private JList<String> soundTypeList;

    public SettingsSound(AppWindow appWindow) {
        super(appWindow);

        int y;

        setLayout(null);

        y = 0;

        generateSoundButton = addButton(y, "Generate Sound", BUTTON_GENERATE_SOUND);
        y += (ROW_HEIGHT + ROW_GAP);

        soundTypeList = addList(y, "Sound Type", getAnnotationClasses("com.klinksoftware.rag.sound", "Sound", SoundInterface.class), 0);
        y += (ROW_LIST_HEIGHT + ROW_GAP);

        exportSoundButton = addButton(y, "Export Sound", BUTTON_EXPORT_SOUND);
    }

    @Override
    public void buttonClick(int id) {
        switch (id) {
            case BUTTON_GENERATE_SOUND:
                (new SoundBuildWorker(
                        appWindow,
                        demangleDisplayNameForClass(soundTypeList, "Sound")
                )).execute();
                return;
            case BUTTON_EXPORT_SOUND:
                (new SoundExportWorker(appWindow)).execute();
                return;
        }
    }
}
