package com.klinksoftware.rag;

import com.klinksoftware.rag.character.utility.CharacterInterface;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JSlider;
import com.klinksoftware.rag.uiworker.CharacterBuildWorker;
import com.klinksoftware.rag.uiworker.CharacterExportWorker;

public class SettingsCharacter extends SettingsBase {

    private static final int BUTTON_GENERATE_CHARACTER = 0;
    private static final int BUTTON_EXPORT_CHARACTER = 1;

    private JButton generateCharacterButton, exportCharacterButton;
    private JList characterTypeList;
    private JSlider roughnessSlider;
    private JCheckBox bilateralCheckBox, organicCheckBox;
    private JComboBox textureSizeCombo;

    public SettingsCharacter(AppWindow appWindow) {
        super(appWindow);

        int y;

        setLayout(null);

        y = 0;

        generateCharacterButton = addButton(y, "Generate Character", BUTTON_GENERATE_CHARACTER);
        y += (ROW_HEIGHT + ROW_GAP);

        characterTypeList = addList(y, "Character Type", getAnnotationClasses("com.klinksoftware.rag.character", "character", CharacterInterface.class), 0);
        y += (ROW_LIST_HEIGHT + ROW_GAP);

        textureSizeCombo = addComboBox(y, "Texture Size", SettingsTexture.TEXTURE_SIZE, 1);
        y += (ROW_HEIGHT + ROW_GAP);

        bilateralCheckBox = addCheckBox(y, "Bilateral", true);
        y += (ROW_HEIGHT + ROW_GAP);

        organicCheckBox = addCheckBox(y, "Organic", true);
        y += (ROW_HEIGHT + ROW_GAP);

        roughnessSlider = addSlider(y, "Roughness", 0.2f);
        y += (ROW_HEIGHT + ROW_GAP);

        exportCharacterButton = addButton(y, "Export Character", BUTTON_EXPORT_CHARACTER);
    }

    @Override
    public void buttonClick(int id) {
        switch (id) {
            case BUTTON_GENERATE_CHARACTER:
                (new CharacterBuildWorker(
                        appWindow,
                        (String) characterTypeList.getModel().getElementAt(characterTypeList.getSelectedIndex()),
                        getIntFromStringCombo(textureSizeCombo),
                        bilateralCheckBox.isSelected(),
                        organicCheckBox.isSelected(),
                        ((float) roughnessSlider.getValue() / 100.0f)
                )).execute();
                return;
            case BUTTON_EXPORT_CHARACTER:
                (new CharacterExportWorker(appWindow)).execute();
                return;
        }
    }
}
