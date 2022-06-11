package com.klinksoftware.rag;

import com.klinksoftware.rag.uiworker.MapBuildWorker;
import com.klinksoftware.rag.uiworker.MapExportWorker;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JSlider;

public class SettingsMap extends SettingsBase {

    private static final int BUTTON_GENERATE_MAP = 0;
    private static final int BUTTON_EXPORT_MAP = 1;

    private JButton generateMapButton, exportMapButton;
    private JSlider sizeSlider, compactSlider, decorationSlider;
    private JCheckBox complexCheckBox, upperFloorCheckBox, lowerFloorCheckBox, structureCheckBox;

    public SettingsMap(AppWindow appWindow) {
        super(appWindow);

        int y;

        setLayout(null);

        y = 0;

        generateMapButton = addButton(y, "Generate Map", BUTTON_GENERATE_MAP);
        y += (ROW_HEIGHT + ROW_GAP);

        sizeSlider = addSlider(y, "Size", 0.6f);
        y += (ROW_HEIGHT + ROW_GAP);

        compactSlider = addSlider(y, "Compact", 0.6f);
        y += (ROW_HEIGHT + ROW_GAP);

        complexCheckBox = addCheckBox(y, "Complex", true);
        y += (ROW_HEIGHT + ROW_GAP);

        upperFloorCheckBox = addCheckBox(y, "Upper Floor", true);
        y += (ROW_HEIGHT + ROW_GAP);

        lowerFloorCheckBox = addCheckBox(y, "Lower Floor", true);
        y += (ROW_HEIGHT + ROW_GAP);

        structureCheckBox = addCheckBox(y, "Structure", true);
        y += (ROW_HEIGHT + ROW_GAP);

        decorationSlider = addSlider(y, "Decorations", 0.3f);
        y += (ROW_HEIGHT + ROW_GAP);

        exportMapButton = addButton(y, "Export Map", BUTTON_EXPORT_MAP);
    }

    @Override
    public void buttonClick(int id) {
        switch (id) {
            case BUTTON_GENERATE_MAP:
                (new MapBuildWorker(
                        appWindow,
                        ((float) sizeSlider.getValue() / 100.0f),
                        ((float) compactSlider.getValue() / 100.0f),
                        complexCheckBox.isSelected(),
                        upperFloorCheckBox.isSelected(),
                        lowerFloorCheckBox.isSelected(),
                        structureCheckBox.isSelected(),
                        ((float) decorationSlider.getValue() / 100.0f)
                )).execute();
                return;
            case BUTTON_EXPORT_MAP:
                (new MapExportWorker(appWindow)).execute();
                return;
        }
    }
}
