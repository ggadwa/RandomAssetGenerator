package com.klinksoftware.rag;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.text.*;
import javax.swing.*;
import javax.swing.text.*;

public class AppWindow implements WindowListener
{
    public static final int WINDOW_WIDTH=1000;
    public static final int WINDOW_HEIGHT=600;
    public static final int TOOLBAR_HEIGHT=38;
    public static final int HEADER_HEIGHT=22;
    public static final int SETTINGS_WIDTH=250;
    
    private static final int TOOL_BUTTON_RUN=0;
    
    public static final int UI_TYPE_MAP=0;
    public static final int UI_TYPE_MODEL=1;
    public static final int UI_TYPE_BITMAPS=2;
    
    private JFrame frame;
    private JPanel settingsPanel,spacerPanel;
    private JToolBar toolBar;
    private JButton runButton;
    private JLabel typeLabel,nameLabel;
    private JTextField nameField;
    private JComboBox typeComboBox;
    private GradientLabel settingsLabel,logLabel;
    private JScrollPane logScroll;
    private JTextArea log;
    
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
    
    public int getSelectedType()
    {
        return(typeComboBox.getSelectedIndex());
    }
    
    public String getName()
    {
        return(nameField.getText());
    }
    
    public void writeLog(String str)
    {
        log.append(str);
        log.append("\n");
    }
    
    public void enableRunButton(boolean enabled)
    {
        runButton.setEnabled(enabled);
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
        URL iconURL;
        Image image;
        
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
        toolBar.setMaximumSize(new Dimension(Integer.MAX_VALUE,TOOLBAR_HEIGHT));
        
        toolBar.add(Box.createHorizontalGlue());
        runButton=addToolButton("tool_run",TOOL_BUTTON_RUN,"Run");
        frame.add(toolBar,new GridBagConstraints(0,0,3,1,1.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
        
            // the settings
            
        settingsLabel=new GradientLabel("Settings",new Color(196,196,255),new Color(128,128,255),false);
        frame.add(settingsLabel,new GridBagConstraints(0,1,1,1,0.4,0.0,GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
        
        settingsPanel=new JPanel(new GridBagLayout());
        settingsPanel.setBackground(Color.WHITE);
        frame.add(settingsPanel,new GridBagConstraints(0,2,1,1,0.4,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
        
        typeLabel=new JLabel("Type:");
        typeLabel.setHorizontalAlignment(JLabel.RIGHT);
        settingsPanel.add(typeLabel,new GridBagConstraints(0,0,1,1,0.2,0.0,GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(2,2,2,2),0,0));
        
        typeComboBox=new JComboBox(new String[]{"Map","Model","Bitmaps"});
        settingsPanel.add(typeComboBox,new GridBagConstraints(1,0,1,1,0.8,0.0,GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(2,2,2,2),0,0));
        
        nameLabel=new JLabel("Name:");
        nameLabel.setHorizontalAlignment(JLabel.RIGHT);       
        settingsPanel.add(nameLabel,new GridBagConstraints(0,1,1,1,0.2,0.0,GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(2,2,2,2),0,0));
        
        nameField=new JTextField("test");
        settingsPanel.add(nameField,new GridBagConstraints(1,1,1,1,0.8,0.0,GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(2,2,2,2),0,0));

        spacerPanel=new JPanel();
        spacerPanel.setBackground(Color.WHITE);
        settingsPanel.add(spacerPanel,new GridBagConstraints(0,2,1,1,0.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));

            // log
        
        logLabel=new GradientLabel("Log",new Color(196,196,255),new Color(128,128,255),true);
        frame.add(logLabel,new GridBagConstraints(2,1,1,1,0.6,0.0,GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));

        log=new JTextArea();
        log.setFont(new Font("Courier New",Font.PLAIN,14));
        log.setText("application started\n");
        log.setEditable(false);
        
        logScroll=new JScrollPane(log); 
        logScroll.setBorder(BorderFactory.createMatteBorder(0,1,0,0,Color.BLACK));
        logScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        logScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        frame.add(logScroll,new GridBagConstraints(2,2,1,2,0.6,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
        
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
