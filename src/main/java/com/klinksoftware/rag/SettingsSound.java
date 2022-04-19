package com.klinksoftware.rag;

import javax.swing.JButton;
import javax.swing.JComboBox;

public class SettingsSound extends SettingsBase {

    private static final int BUTTON_GENERATE_SOUND = 0;
    private static final String[] SOUND_ITEMS = {"x"};

    private JButton generateSoundButton;
    private JComboBox soundTypeCombo;

    public SettingsSound() {
        int y;

        setLayout(null);

        y = 0;

        generateSoundButton = addButton(y, "Generate Sound", BUTTON_GENERATE_SOUND);
        y += (ROW_HEIGHT + ROW_GAP);

        soundTypeCombo = addComboBox(y, "Sound Type", SOUND_ITEMS, 0);
    }

    @Override
    public void buttonClick(int id) {
        System.out.println(id);
    }
}
