package com.klinksoftware.rag;

import javax.swing.*;

public class GeneratorRun extends SwingWorker<String,Void>
{
    private AppWindow           appWindow;
    
    public GeneratorRun(AppWindow appWindow)
    {
        this.appWindow=appWindow;
    }
    
    @Override
    protected String doInBackground() throws Exception
    {
        appWindow.enableRunButton(false);
        
        switch (appWindow.getSelectedTab()) {
            case AppWindow.UI_TAB_MAP_INDOOR:
                GeneratorMain.runMapIndoor(appWindow.getMapIndoorText());
                break;
            case AppWindow.UI_TAB_MAP_OUTDOOR:
            case AppWindow.UI_TAB_MAP_TRACK:
                break;
            case AppWindow.UI_TAB_MODEL_HUMANOID:
                GeneratorMain.runModelHumanoid(appWindow.getHumanoidModelText());
                break;
            case AppWindow.UI_TAB_BITMAPS:
                GeneratorMain.runBitmaps(appWindow.getBitmapText());
                break;
        }

        return("");
    }
    
    @Override
    protected void done()
    {
        appWindow.enableRunButton(true);
    }    
}
