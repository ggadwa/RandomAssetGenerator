package com.klinksoftware.rag;

import javax.swing.*;

public class GeneratorRun extends SwingWorker<String,Void>
{
    private JButton         runButton;
    private JTabbedPane     tab;
    private JTextArea       mapTextArea,modelTextArea;
    
    public GeneratorRun(JButton runButton,JTabbedPane tab,JTextArea mapTextArea,JTextArea modelTextArea)
    {
        this.runButton=runButton;
        this.tab=tab;
        this.mapTextArea=mapTextArea;
        this.modelTextArea=modelTextArea;
    }
    
    @Override
    protected String doInBackground() throws Exception
    {
        runButton.setEnabled(false);
        
        switch (tab.getSelectedIndex()) {
            case 0:
                GeneratorMain.runMap(mapTextArea.getText());
                break;
            case 1:
                GeneratorMain.runModel(modelTextArea.getText());
                break;
        }

        return("");
    }
    
    @Override
    protected void done()
    {
        runButton.setEnabled(true);
    }    
}
