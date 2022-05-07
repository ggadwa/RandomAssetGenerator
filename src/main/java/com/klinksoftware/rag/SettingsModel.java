package com.klinksoftware.rag;

import com.klinksoftware.rag.uiworker.ModelBuildWorker;
import com.klinksoftware.rag.uiworker.ModelExportWorker;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

public class SettingsModel extends SettingsBase {

    private static final int BUTTON_GENERATE_MODEL = 0;
    private static final int BUTTON_EXPORT_MODEL = 1;

    public static final int MODEL_TYPE_HUMANOID = 0;
    public static final int MODEL_TYPE_ANIMAL = 1;
    public static final int MODEL_TYPE_BLOB = 2;
    public static final int MODEL_TYPE_ROBOT = 3;
    private static final String[] MODEL_TYPE = {"Humanoid", "Animal", "Blob", "Robot"};

    private JButton generateModelButton, exportModelButton;
    private JCheckBox thinCheckBox, bilateralCheckBox, roughCheckBox;
    private JComboBox modelTypeCombo;

    public SettingsModel(AppWindow appWindow) {
        super(appWindow);

        int y;

        setLayout(null);

        y = 0;

        generateModelButton = addButton(y, "Generate Model", BUTTON_GENERATE_MODEL);
        y += (ROW_HEIGHT + ROW_GAP);

        modelTypeCombo = addComboBox(y, "Model Type", MODEL_TYPE, 0);
        y += (ROW_HEIGHT + ROW_GAP);

        thinCheckBox = addCheckBox(y, "Thin", true);
        y += (ROW_HEIGHT + ROW_GAP);

        bilateralCheckBox = addCheckBox(y, "Bilateral", true);
        y += (ROW_HEIGHT + ROW_GAP);

        roughCheckBox = addCheckBox(y, "Rough", true);
        y += (ROW_HEIGHT + ROW_GAP);

        exportModelButton = addButton(y, "Export Model", BUTTON_EXPORT_MODEL);
    }

    @Override
    public void buttonClick(int id) {
        switch (id) {
            case BUTTON_GENERATE_MODEL:
                (new ModelBuildWorker(appWindow, modelTypeCombo.getSelectedIndex(), thinCheckBox.isSelected(), bilateralCheckBox.isSelected(), roughCheckBox.isSelected())).execute();
                return;
            case BUTTON_EXPORT_MODEL:
                (new ModelExportWorker(appWindow)).execute();
                return;
        }
    }
}
