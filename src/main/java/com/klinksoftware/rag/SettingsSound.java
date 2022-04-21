package com.klinksoftware.rag;

import com.klinksoftware.rag.uiworker.SoundBuildWorker;
import com.klinksoftware.rag.uiworker.SoundExportWorker;
import javax.swing.JButton;
import javax.swing.JComboBox;

public class SettingsSound extends SettingsBase {

    private static final int BUTTON_GENERATE_SOUND = 0;
    private static final int BUTTON_EXPORT_SOUND = 1;
    private static final String[] SOUND_ITEMS = {"Gun Fire", "Explosion", "Monster Wake Up", "Monster Hurt", "Monster Die"};

    private JButton generateSoundButton;
    private JComboBox soundTypeCombo;

    public SettingsSound(AppWindow appWindow) {
        super(appWindow);

        int y;

        setLayout(null);

        y = 0;

        generateSoundButton = addButton(y, "Generate Sound", BUTTON_GENERATE_SOUND);
        y += (ROW_HEIGHT + ROW_GAP);

        soundTypeCombo = addComboBox(y, "Sound Type", SOUND_ITEMS, 0);
        y += (ROW_HEIGHT + ROW_GAP);

        generateSoundButton = addButton(y, "Export Sound", BUTTON_EXPORT_SOUND);
    }

    @Override
    public void buttonClick(int id) {
        switch (id) {
            case BUTTON_GENERATE_SOUND:
                (new SoundBuildWorker(appWindow, SOUND_ITEMS[soundTypeCombo.getSelectedIndex()])).execute();
                return;
            case BUTTON_EXPORT_SOUND:
                (new SoundExportWorker(appWindow)).execute();
                return;
        }
    }
}
