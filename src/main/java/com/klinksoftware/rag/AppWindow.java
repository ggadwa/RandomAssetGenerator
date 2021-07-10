package com.klinksoftware.rag;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import javax.swing.*;

public class AppWindow implements WindowListener
{
    public static final int         WINDOW_WIDTH=1000;
    public static final int         WINDOW_HEIGHT=600;
    public static final int         TOOLBAR_HEIGHT=38;
    
    private static final int        TOOL_BUTTON_RUN=0;
    
    public static final int         UI_TAB_MAP=0;
    public static final int         UI_TAB_MODEL=1;
    public static final int         UI_TAB_BITMAPS=2;
    
    private JFrame          frame;
    private JToolBar        toolBar;
    private JButton         runButton;
    private JTabbedPane     tab;
    private JScrollPane     mapTextScroll,
                            modelTextScroll,bitmapTextScroll;
    private JTextArea       mapIndoorTextArea,mapOutdoorTextArea,mapTrackTextArea,
                            modelHumanoidTextArea,bitmapTextArea;
    
        //
        // window events
        //
    
    @Override
    public void windowOpened(WindowEvent e)
    { 
    }

    @Override
    public void windowClosing(WindowEvent e)
    {
        RandomAssetGenerator.stop();
    }

    @Override
    public void windowClosed(WindowEvent e)
    {
    }

    @Override
    public void windowIconified(WindowEvent e)
    {
    }

    @Override
    public void windowDeiconified(WindowEvent e)
    {
    }

    @Override
    public void windowActivated(WindowEvent e)
    {
    }

    @Override
    public void windowDeactivated(WindowEvent e)
    {
    }
    
        //
        // getters
        //
    
    public void enableRunButton(boolean enabled)
    {
        runButton.setEnabled(enabled);
    }
    
    public int getSelectedTab()
    {
        return(tab.getSelectedIndex());
    }
    
    public String getMapText()
    {
        return(mapIndoorTextArea.getText());
    }
    
    public String getModelText()
    {
        return(modelHumanoidTextArea.getText());
    }
    
    public String getBitmapText()
    {
        return(bitmapTextArea.getText());
    }
    
        //
        // toolbar
        //

    private void toolBarClick(int buttonId)
    {
        switch (buttonId) {
            case TOOL_BUTTON_RUN:
                (new GeneratorRun(this)).execute();
                break;
        }
    }
    
    private JButton addToolButton(String iconName,int buttonId,String toolTipText)
    {
        URL                 iconURL;
        JButton             button;
        
        iconURL=getClass().getResource("/Graphics/"+iconName+".png");
        
        button=new JButton();
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setFocusable(false);
        button.setIcon(new ImageIcon(iconURL));
        button.setToolTipText(toolTipText);
        button.addActionListener(e->toolBarClick(buttonId));
        
        toolBar.add(button);
        
        return(button);
    }

        //
        // start and stop main window
        //
    
    public void start()
    {
        URL                 iconURL;
        Image               image;
        GridBagConstraints  gbc;
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {}      // really nothing to do about this, you just get the default "metal" appearance
      
            // window icon
            
        iconURL=getClass().getResource("/Graphics/icon.png");
        image=new ImageIcon(iconURL).getImage();
        
            // the quit menu event and doc icon
            // this is only handled on some OSes, so we just ignore if
            // it errors out
        
        try {
            Desktop.getDesktop().setQuitHandler((event,response) -> RandomAssetGenerator.stop());
            Taskbar.getTaskbar().setIconImage(image);
        }
        catch (Exception e) {}
        
            // create the window
        
        frame=new JFrame();
        
        frame.setTitle("Random Asset Generator");      
        frame.setIconImage(image);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(WINDOW_WIDTH,WINDOW_HEIGHT);
        frame.setMinimumSize(new Dimension(WINDOW_WIDTH,WINDOW_HEIGHT));
        
        frame.setLayout(new GridBagLayout());
        
            // toolbar
            
        toolBar=new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setPreferredSize(new Dimension(Integer.MAX_VALUE,TOOLBAR_HEIGHT));
        toolBar.setMinimumSize(new Dimension(Integer.MAX_VALUE,TOOLBAR_HEIGHT));
        toolBar.setMaximumSize(new Dimension(Integer.MAX_VALUE,TOOLBAR_HEIGHT));
        
        toolBar.add(Box.createHorizontalGlue());
        runButton=addToolButton("tool_run",TOOL_BUTTON_RUN,"Run");
        
        gbc=new GridBagConstraints();
        gbc.fill=GridBagConstraints.BOTH;
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.weightx=1.0;
        gbc.weighty=0.0;
        frame.add(toolBar,gbc);
        
            // tabs
            
        tab=new JTabbedPane();
        tab.setFont(new Font("Arial",Font.PLAIN,18));
        tab.setPreferredSize(new Dimension(Integer.MAX_VALUE,100));
        tab.setMinimumSize(new Dimension(Integer.MAX_VALUE,100));
        tab.setMaximumSize(new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE));
        
            // map
            
        mapIndoorTextArea=new JTextArea();
        mapIndoorTextArea.setFont(new Font("Courier New",Font.PLAIN,14));
        mapIndoorTextArea.setText(GeneratorMain.getSettingJson("map_indoor"));
        
        mapTextScroll=new JScrollPane(mapIndoorTextArea); 
        mapTextScroll.setBorder(BorderFactory.createEmptyBorder());
        mapTextScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        mapTextScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        mapTextScroll.setPreferredSize(new Dimension(Integer.MAX_VALUE,100));
        mapTextScroll.setMinimumSize(new Dimension(Integer.MAX_VALUE,100));
        mapTextScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE));
        
        tab.addTab("Map",null,mapTextScroll,"Map Creation Settings");
        
            // model json
            
        modelHumanoidTextArea=new JTextArea();
        modelHumanoidTextArea.setFont(new Font("Courier New",Font.PLAIN,14));
        modelHumanoidTextArea.setText(GeneratorMain.getSettingJson("model_humanoid"));
        
        modelTextScroll=new JScrollPane(modelHumanoidTextArea); 
        modelTextScroll.setBorder(BorderFactory.createEmptyBorder());
        modelTextScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        modelTextScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        modelTextScroll.setPreferredSize(new Dimension(Integer.MAX_VALUE,100));
        modelTextScroll.setMinimumSize(new Dimension(Integer.MAX_VALUE,100));
        modelTextScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE));
        
        tab.addTab("Model",null,modelTextScroll,"Model Creation Settings");
        
            // generic bitmap generator
            
        bitmapTextArea=new JTextArea();
        bitmapTextArea.setFont(new Font("Courier New",Font.PLAIN,14));
        bitmapTextArea.setText(GeneratorMain.getSettingJson("bitmaps"));
        
        bitmapTextScroll=new JScrollPane(bitmapTextArea); 
        bitmapTextScroll.setBorder(BorderFactory.createEmptyBorder());
        bitmapTextScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        bitmapTextScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        bitmapTextScroll.setPreferredSize(new Dimension(Integer.MAX_VALUE,100));
        bitmapTextScroll.setMinimumSize(new Dimension(Integer.MAX_VALUE,100));
        bitmapTextScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE));
            
        tab.addTab("Bitmaps",null,bitmapTextScroll,"Generate All Bitmaps");

        gbc=new GridBagConstraints();
        gbc.fill=GridBagConstraints.BOTH;
        gbc.gridx=0;
        gbc.gridy=1;
        gbc.weightx=1.0;
        gbc.weighty=1.0;
        frame.add(tab,gbc);

            // all the event listeners
            
        frame.addWindowListener(this);
        
            // show the window
            
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    public void stop()
    {
            // dispose window
            
        frame.dispose();
    }
}
