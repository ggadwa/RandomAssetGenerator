package com.klinksoftware.rag;

import static com.klinksoftware.rag.SettingsBase.ROW_GAP;
import static com.klinksoftware.rag.SettingsBase.ROW_HEIGHT;
import com.klinksoftware.rag.map.utility.MapInterface;
import com.klinksoftware.rag.uiworker.MapBuildWorker;
import com.klinksoftware.rag.uiworker.MapExportWorker;
import javax.swing.JButton;
import javax.swing.JList;

public class SettingsMap extends SettingsBase {

    private static final int BUTTON_GENERATE_MAP = 0;
    private static final int BUTTON_EXPORT_MAP = 1;

    public static final int MAP_TYPE_INDOOR = 0;
    public static final int MAP_TYPE_OUTDOOR = 1;
    private static final String[] MAP_TYPE = {"Indoor", "Outdoor"};

    private JButton generateMapButton, exportMapButton;
    private JList<String> mapTypeList;

    public SettingsMap(AppWindow appWindow) {
        super(appWindow);

        int y;

        setLayout(null);

        y = 0;

        generateMapButton = addButton(y, "Generate Map", BUTTON_GENERATE_MAP);
        y += (ROW_HEIGHT + ROW_GAP);

        mapTypeList = addList(y, "Map Type", getAnnotationClasses("com.klinksoftware.rag.map", "Map", MapInterface.class), 0);
        y += (ROW_LIST_HEIGHT + ROW_GAP);

        exportMapButton = addButton(y, "Export Map", BUTTON_EXPORT_MAP);
    }

    @Override
    public void buttonClick(int id) {
        switch (id) {
            case BUTTON_GENERATE_MAP:
                (new MapBuildWorker(
                        appWindow,
                        demangleDisplayNameForClass(mapTypeList, "Map")
                )).execute();
                return;
            case BUTTON_EXPORT_MAP:
                (new MapExportWorker(appWindow)).execute();
                return;
        }
    }
}
