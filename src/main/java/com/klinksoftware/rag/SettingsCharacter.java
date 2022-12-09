package com.klinksoftware.rag;

import com.klinksoftware.rag.character.utility.CharacterInterface;
import javax.swing.JButton;
import javax.swing.JList;
import com.klinksoftware.rag.uiworker.CharacterBuildWorker;
import com.klinksoftware.rag.uiworker.CharacterExportWorker;

public class SettingsCharacter extends SettingsBase {

    private static final int BUTTON_GENERATE_CHARACTER = 0;
    private static final int BUTTON_EXPORT_CHARACTER = 1;

    private JButton generateCharacterButton, exportCharacterButton;
    private JList<String> characterTypeList;

    public SettingsCharacter(AppWindow appWindow) {
        super(appWindow);

        int y;

        setLayout(null);

        y = 0;

        generateCharacterButton = addButton(y, "Generate Character", BUTTON_GENERATE_CHARACTER);
        y += (ROW_HEIGHT + ROW_GAP);

        characterTypeList = addList(y, "Character Type", getAnnotationClasses("com.klinksoftware.rag.character", "Character", CharacterInterface.class), 0);
        y += (ROW_LIST_HEIGHT + ROW_GAP);

        exportCharacterButton = addButton(y, "Export Character", BUTTON_EXPORT_CHARACTER);
    }

    @Override
    public void buttonClick(int id) {
        switch (id) {
            case BUTTON_GENERATE_CHARACTER:
                (new CharacterBuildWorker(
                        appWindow,
                        demangleDisplayNameForClass(characterTypeList, "Character")
                )).execute();
                return;
            case BUTTON_EXPORT_CHARACTER:
                (new CharacterExportWorker(appWindow)).execute();
                return;
        }
    }
}
