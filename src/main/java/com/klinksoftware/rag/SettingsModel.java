package com.klinksoftware.rag;

import com.klinksoftware.rag.uiworker.ModelBuildWorker;
import com.klinksoftware.rag.uiworker.ModelExportWorker;
import javax.swing.JButton;

public class SettingsModel extends SettingsBase {

    private static final int BUTTON_GENERATE_MODEL = 0;
    private static final int BUTTON_EXPORT_MODEL = 1;

    private JButton generateModelButton, exportModelButton;

    public SettingsModel(AppWindow appWindow) {
        super(appWindow);

        int y;

        setLayout(null);

        y = 0;

        generateModelButton = addButton(y, "Generate Model", BUTTON_GENERATE_MODEL);
        y += (ROW_HEIGHT + ROW_GAP);

        exportModelButton = addButton(y, "Export Model", BUTTON_EXPORT_MODEL);
        y += (ROW_HEIGHT + ROW_GAP);
    }

    @Override
    public void buttonClick(int id) {
        switch (id) {
            case BUTTON_GENERATE_MODEL:
                (new ModelBuildWorker(appWindow)).execute();
                return;
            case BUTTON_EXPORT_MODEL:
                (new ModelExportWorker(appWindow)).execute();
                return;
        }
    }
}
