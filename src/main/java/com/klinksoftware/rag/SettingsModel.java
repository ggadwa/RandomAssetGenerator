package com.klinksoftware.rag;

import com.klinksoftware.rag.models.ModelInterface;
import com.klinksoftware.rag.uiworker.ModelBuildWorker;
import com.klinksoftware.rag.uiworker.ModelExportWorker;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JSlider;

public class SettingsModel extends SettingsBase {

    private static final int BUTTON_GENERATE_MODEL = 0;
    private static final int BUTTON_EXPORT_MODEL = 1;

    private JButton generateModelButton, exportModelButton;
    private JList modelTypeList;
    private JSlider roughnessSlider;
    private JCheckBox bilateralCheckBox;

    public SettingsModel(AppWindow appWindow) {
        super(appWindow);

        int y;

        setLayout(null);

        y = 0;

        generateModelButton = addButton(y, "Generate Model", BUTTON_GENERATE_MODEL);
        y += (ROW_HEIGHT + ROW_GAP);

        modelTypeList = addList(y, "Model Type", getAnnotationClasses("com.klinksoftware.rag.models", "model", ModelInterface.class), 0);
        y += (ROW_LIST_HEIGHT + ROW_GAP);

        bilateralCheckBox = addCheckBox(y, "Bilateral", true);
        y += (ROW_HEIGHT + ROW_GAP);

        roughnessSlider = addSlider(y, "Roughness", 0.2f);
        y += (ROW_HEIGHT + ROW_GAP);

        exportModelButton = addButton(y, "Export Model", BUTTON_EXPORT_MODEL);
    }

    @Override
    public void buttonClick(int id) {
        switch (id) {
            case BUTTON_GENERATE_MODEL:
                (new ModelBuildWorker(
                        appWindow,
                        (String) modelTypeList.getModel().getElementAt(modelTypeList.getSelectedIndex()),
                        bilateralCheckBox.isSelected(),
                        ((float) roughnessSlider.getValue() / 100.0f)
                )).execute();
                return;
            case BUTTON_EXPORT_MODEL:
                (new ModelExportWorker(appWindow)).execute();
                return;
        }
    }
}
