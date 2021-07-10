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
            case AppWindow.UI_TAB_MAP:
                GeneratorMain.runMap(appWindow.getMapText());
                break;
            case AppWindow.UI_TAB_MODEL:
                GeneratorMain.runModel(appWindow.getModelText());
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
