package com.klinksoftware.rag;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.uiworker.BitmapBuildWorker;
import com.klinksoftware.rag.uiworker.BitmapExportWorker;
import javax.swing.JButton;
import javax.swing.JList;

public class SettingsTexture extends SettingsBase {

    private static final int BUTTON_GENERATE_TEXTURE = 0;
    private static final int BUTTON_EXPORT_TEXTURE = 1;

    private static final String[] TEXTURE_ITEMS = {
        "Brick", "Computer", "Concrete", "Control Panel", "Dirt",
        "Fur", "Geometric", "Glass", "Grass", "Liquid", "Metal",
        "Metal Box", "Monitor", "Monster", "Mosaic", "Organic",
        "Pipe", "Plaster", "Robot", "Scale", "Stone", "Test",
        "Tile", "Wood", "Wood Box"};

    private JButton generateTextureButton, exportTextureButton;
    private JList textureTypeList;

    public SettingsTexture(AppWindow appWindow) {
        super(appWindow);

        int y;

        setLayout(null);

        y = 0;

        generateTextureButton = addButton(y, "Generate Texture", BUTTON_GENERATE_TEXTURE);
        y += (ROW_HEIGHT + ROW_GAP);

        textureTypeList = addList(y, "Texture Type", TEXTURE_ITEMS, 0);
        y += (ROW_LIST_HEIGHT + ROW_GAP);

        exportTextureButton = addButton(y, "Export Texture", BUTTON_EXPORT_TEXTURE);
    }

    @Override
    public void buttonClick(int id) {
        switch (id) {
            case BUTTON_GENERATE_TEXTURE:
                (new BitmapBuildWorker(appWindow, TEXTURE_ITEMS[textureTypeList.getSelectedIndex()])).execute();
                return;
            case BUTTON_EXPORT_TEXTURE:
                (new BitmapExportWorker(appWindow)).execute();
                return;
        }
    }
}
