package com.klinksoftware.rag;

import javax.swing.JButton;
import javax.swing.JComboBox;

public class SettingsTexture extends SettingsBase {

    private static final int BUTTON_GENERATE_TEXTURE = 0;
    private static final String[] TEXTURE_ITEMS = {"Brick", "Computer", "Concrete", "Glass", "Ground", "Liquid", "Metal", "Mosaic", "Skin", "Stone", "Tile", "Wood"};

    private JButton generateTextureButton;
    private JComboBox textureTypeCombo;

    public SettingsTexture() {
        int y;

        setLayout(null);

        y = 0;

        generateTextureButton = addButton(y, "Generate Texture", BUTTON_GENERATE_TEXTURE);
        y += (ROW_HEIGHT + ROW_GAP);

        textureTypeCombo = addComboBox(y, "Texture Type", TEXTURE_ITEMS, 0);
    }

    @Override
    public void buttonClick(int id) {
        System.out.println(id);
    }
}
