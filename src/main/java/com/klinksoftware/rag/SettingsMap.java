package com.klinksoftware.rag;

import com.klinksoftware.rag.uiworker.MapBuildWorker;
import com.klinksoftware.rag.uiworker.MapExportWorker;
import javax.swing.JButton;
import javax.swing.JCheckBox;

public class SettingsMap extends SettingsBase {

    private static final int BUTTON_GENERATE_MAP = 0;
    private static final int BUTTON_EXPORT_MAP = 1;

    private JButton generateMapButton, exportMapButton;
    private JCheckBox upperFloorCheckBox, lowerFloorCheckBox, decorationsCheckBox;

    public SettingsMap(AppWindow appWindow) {
        super(appWindow);

        int y;

        setLayout(null);

        y = 0;

        generateMapButton = addButton(y, "Generate Map", BUTTON_GENERATE_MAP);
        y += (ROW_HEIGHT + ROW_GAP);

        upperFloorCheckBox = addCheckBox(y, "Upper Floor", true);
        y += (ROW_HEIGHT + ROW_GAP);

        lowerFloorCheckBox = addCheckBox(y, "Lower Floor", true);
        y += (ROW_HEIGHT + ROW_GAP);

        decorationsCheckBox = addCheckBox(y, "Decorations", false);
        y += (ROW_HEIGHT + ROW_GAP);

        exportMapButton = addButton(y, "Export Map", BUTTON_EXPORT_MAP);
    }

    @Override
    public void buttonClick(int id) {
        switch (id) {
            case BUTTON_GENERATE_MAP:
                (new MapBuildWorker(appWindow, upperFloorCheckBox.isSelected(), lowerFloorCheckBox.isSelected(), decorationsCheckBox.isSelected())).execute();
                return;
            case BUTTON_EXPORT_MAP:
                (new MapExportWorker(appWindow)).execute();
                return;
        }
    }
}
