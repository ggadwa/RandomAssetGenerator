package com.klinksoftware.rag;

import com.klinksoftware.rag.uiworker.MapBuildWorker;
import com.klinksoftware.rag.uiworker.MapExportWorker;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JSlider;

public class SettingsMap extends SettingsBase {

    private static final int BUTTON_GENERATE_MAP = 0;
    private static final int BUTTON_EXPORT_MAP = 1;

    public static final int MAP_TYPE_INDOOR = 0;
    public static final int MAP_TYPE_OUTDOOR = 1;
    private static final String[] MAP_TYPE = {"Indoor", "Outdoor"};

    private JButton generateMapButton, exportMapButton;
    private JSlider mainFloorSizeSlider, upperFloorSizeSlider, lowerFloorSizeSlider, compactSlider;
    private JCheckBox complexCheckBox;
    private JComboBox mapTypeCombo;

    public SettingsMap(AppWindow appWindow) {
        super(appWindow);

        int y;

        setLayout(null);

        y = 0;

        generateMapButton = addButton(y, "Generate Map", BUTTON_GENERATE_MAP);
        y += (ROW_HEIGHT + ROW_GAP);

        mapTypeCombo = addComboBox(y, "Model Type", MAP_TYPE, 0);
        y += (ROW_HEIGHT + ROW_GAP);

        mainFloorSizeSlider = addSlider(y, "Main Floor Size", 0.6f);
        y += (ROW_HEIGHT + ROW_GAP);

        upperFloorSizeSlider = addSlider(y, "Upper Floor Size", 0.2f);
        y += (ROW_HEIGHT + ROW_GAP);

        lowerFloorSizeSlider = addSlider(y, "Lower Floor Size", 0.2f);
        y += (ROW_HEIGHT + ROW_GAP);

        compactSlider = addSlider(y, "Compact", 0.6f);
        y += (ROW_HEIGHT + ROW_GAP);

        complexCheckBox = addCheckBox(y, "Complex", true);
        y += (ROW_HEIGHT + ROW_GAP);

        exportMapButton = addButton(y, "Export Map", BUTTON_EXPORT_MAP);
    }

    @Override
    public void buttonClick(int id) {
        switch (id) {
            case BUTTON_GENERATE_MAP:
                (new MapBuildWorker(
                        appWindow,
                        mapTypeCombo.getSelectedIndex(),
                        ((float) mainFloorSizeSlider.getValue() / 100.0f),
                        ((float) upperFloorSizeSlider.getValue() / 100.0f),
                        ((float) lowerFloorSizeSlider.getValue() / 100.0f),
                        ((float) compactSlider.getValue() / 100.0f),
                        complexCheckBox.isSelected()
                )).execute();
                return;
            case BUTTON_EXPORT_MAP:
                (new MapExportWorker(appWindow)).execute();
                return;
        }
    }
}
