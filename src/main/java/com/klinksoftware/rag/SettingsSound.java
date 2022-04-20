package com.klinksoftware.rag;

import javax.swing.JButton;
import javax.swing.JComboBox;

public class SettingsSound extends SettingsBase {

    private static final int BUTTON_GENERATE_SOUND = 0;
    private static final int BUTTON_SAVE_SOUND = 1;
    private static final String[] SOUND_ITEMS = {"Gun Fire", "Explosion", "Monster Wake Up", "Monster Hurt", "Monster Die"};

    private JButton generateSoundButton;
    private JComboBox soundTypeCombo;

    public SettingsSound() {
        int y;

        setLayout(null);

        y = 0;

        generateSoundButton = addButton(y, "Generate Sound", BUTTON_GENERATE_SOUND);
        y += (ROW_HEIGHT + ROW_GAP);

        soundTypeCombo = addComboBox(y, "Sound Type", SOUND_ITEMS, 0);
        y += (ROW_HEIGHT + ROW_GAP);

        generateSoundButton = addButton(y, "Save Sound", BUTTON_SAVE_SOUND);
    }

    @Override
    public void buttonClick(int id) {
        System.out.println(id);
    }
}
