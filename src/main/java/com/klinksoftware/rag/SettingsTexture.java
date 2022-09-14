package com.klinksoftware.rag;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.uiworker.BitmapBuildWorker;
import com.klinksoftware.rag.uiworker.BitmapExportWorker;
import javax.swing.JButton;
import javax.swing.JList;
import com.klinksoftware.rag.bitmap.utility.BitmapInterface;
import javax.swing.JComboBox;

public class SettingsTexture extends SettingsBase {

    private static final int BUTTON_GENERATE_TEXTURE = 0;
    private static final int BUTTON_EXPORT_TEXTURE = 1;

    public static final String[] TEXTURE_SIZE = {"512", "1024", "2048", "4096"};

    private JButton generateTextureButton, exportTextureButton;
    private JComboBox textureSizeCombo;
    private JList textureTypeList;

    public SettingsTexture(AppWindow appWindow) {
        super(appWindow);

        int y;

        setLayout(null);

        y = 0;

        generateTextureButton = addButton(y, "Generate Texture", BUTTON_GENERATE_TEXTURE);
        y += (ROW_HEIGHT + ROW_GAP);

        textureTypeList = addList(y, "Texture Type", getAnnotationClasses("com.klinksoftware.rag.bitmap", "bitmap", BitmapInterface.class), 0);
        y += (ROW_LIST_HEIGHT + ROW_GAP);

        textureSizeCombo = addComboBox(y, "Texture Size", TEXTURE_SIZE, 0);
        y += (ROW_HEIGHT + ROW_GAP);

        exportTextureButton = addButton(y, "Export Texture", BUTTON_EXPORT_TEXTURE);
    }

    @Override
    public void buttonClick(int id) {
        switch (id) {
            case BUTTON_GENERATE_TEXTURE:
                (new BitmapBuildWorker(appWindow, getIntFromStringCombo(textureSizeCombo), (String) textureTypeList.getModel().getElementAt(textureTypeList.getSelectedIndex()))).execute();
                return;
            case BUTTON_EXPORT_TEXTURE:
                (new BitmapExportWorker(appWindow)).execute();
                return;
        }
    }
}
