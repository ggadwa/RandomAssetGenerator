/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.klinksoftware.rag;

import javax.swing.JButton;
import javax.swing.JCheckBox;

public class SettingsMap extends SettingsBase {

    private static final int BUTTON_GENERATE_MAP = 0;
    private static final int BUTTON_REGENERATE_TEXTURES = 1;
    private static final int BUTTON_SAVE_MAP = 2;

    private JButton generateMapButton, regenerateTexturesButton;
    private JCheckBox upperFloorCheckBox, lowerFloorCheckBox, decorationsCheckBox;

    public SettingsMap() {
        int y;

        setLayout(null);

        y = 0;

        generateMapButton = addButton(y, "Generate Map", BUTTON_GENERATE_MAP);
        y += (ROW_HEIGHT + ROW_GAP);

        regenerateTexturesButton = addButton(y, "Regenerate Textures", BUTTON_REGENERATE_TEXTURES);
        y += (ROW_HEIGHT + ROW_GAP);

        upperFloorCheckBox = addCheckBox(y, "Upper Floor", true);
        y += (ROW_HEIGHT + ROW_GAP);

        lowerFloorCheckBox = addCheckBox(y, "Lower Floor", true);
        y += (ROW_HEIGHT + ROW_GAP);

        decorationsCheckBox = addCheckBox(y, "Decorations", false);
        y += (ROW_HEIGHT + ROW_GAP);

        generateMapButton = addButton(y, "Save Map", BUTTON_SAVE_MAP);
    }

    @Override
    public void buttonClick(int id) {
        System.out.println(id);
    }
}
