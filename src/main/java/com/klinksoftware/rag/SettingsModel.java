package com.klinksoftware.rag;

import javax.swing.JButton;

public class SettingsModel extends SettingsBase {

    private static final int BUTTON_GENERATE_MODEL = 0;

    private JButton generateModelButton;

    public SettingsModel() {
        int y;

        setLayout(null);

        y = 0;

        generateModelButton = addButton(y, "Generate Model", BUTTON_GENERATE_MODEL);
        y += (ROW_HEIGHT + ROW_GAP);
    }

    @Override
    public void buttonClick(int id) {
        System.out.println(id);
    }
}
