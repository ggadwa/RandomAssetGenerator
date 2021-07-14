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
        
        switch (appWindow.getSelectedType()) {
            case AppWindow.UI_TYPE_MAP:
                GeneratorMain.runMap(appWindow);
                break;
            case AppWindow.UI_TYPE_MODEL:
                GeneratorMain.runModel(appWindow);
                break;
            case AppWindow.UI_TYPE_BITMAPS:
                GeneratorMain.runBitmaps(appWindow);
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
