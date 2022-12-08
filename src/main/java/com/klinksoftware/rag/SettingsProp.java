package com.klinksoftware.rag;

import com.klinksoftware.rag.uiworker.PropBuildWorker;
import com.klinksoftware.rag.uiworker.PropExportWorker;
import javax.swing.JButton;
import javax.swing.JList;
import com.klinksoftware.rag.prop.utility.PropInterface;

public class SettingsProp extends SettingsBase {

    private static final int BUTTON_GENERATE_PROP = 0;
    private static final int BUTTON_EXPORT_PROP = 1;

    private JButton generatePropButton, exportPropButton;
    private JList propTypeList;

    public SettingsProp(AppWindow appWindow) {
        super(appWindow);

        int y;

        setLayout(null);

        y = 0;

        generatePropButton = addButton(y, "Generate Prop", BUTTON_GENERATE_PROP);
        y += (ROW_HEIGHT + ROW_GAP);

        propTypeList = addList(y, "Prop Type", getAnnotationClasses("com.klinksoftware.rag.prop", "prop", PropInterface.class), 0);
        y += (ROW_LIST_HEIGHT + ROW_GAP);

        exportPropButton = addButton(y, "Export Prop", BUTTON_EXPORT_PROP);
    }

    @Override
    public void buttonClick(int id) {
        switch (id) {
            case BUTTON_GENERATE_PROP:
                (new PropBuildWorker(
                        appWindow,
                        (String) propTypeList.getModel().getElementAt(propTypeList.getSelectedIndex())
                )).execute();
                return;
            case BUTTON_EXPORT_PROP:
                (new PropExportWorker(appWindow)).execute();
                return;
        }
    }
}
