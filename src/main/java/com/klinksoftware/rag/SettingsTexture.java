package com.klinksoftware.rag;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.uiworker.BitmapBuildWorker;
import com.klinksoftware.rag.uiworker.BitmapExportWorker;
import javax.swing.JButton;
import javax.swing.JComboBox;

public class SettingsTexture extends SettingsBase {

    private static final int BUTTON_GENERATE_TEXTURE = 0;
    private static final int BUTTON_EXPORT_TEXTURE = 1;
    private static final String[] TEXTURE_ITEMS = {
        "Brick", "Computer", "Concrete", "Control Panel",
        "Glass", "Ground", "Liquid", "Metal", "Monitor",
        "Mosaic", "Skin", "Stone", "Tile", "Wood"};

    private JButton generateTextureButton;
    private JComboBox textureTypeCombo;

    public SettingsTexture(AppWindow appWindow) {
        super(appWindow);

        int y;

        setLayout(null);

        y = 0;

        generateTextureButton = addButton(y, "Generate Texture", BUTTON_GENERATE_TEXTURE);
        y += (ROW_HEIGHT + ROW_GAP);

        textureTypeCombo = addComboBox(y, "Texture Type", TEXTURE_ITEMS, 0);
        y += (ROW_HEIGHT + ROW_GAP);

        generateTextureButton = addButton(y, "Export Texture", BUTTON_EXPORT_TEXTURE);
    }

    @Override
    public void buttonClick(int id) {
        switch (id) {
            case BUTTON_GENERATE_TEXTURE:
                (new BitmapBuildWorker(appWindow, TEXTURE_ITEMS[textureTypeCombo.getSelectedIndex()])).execute();
                return;
            case BUTTON_EXPORT_TEXTURE:
                (new BitmapExportWorker(appWindow)).execute();
                return;
        }
    }
}
