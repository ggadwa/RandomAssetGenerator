package com.klinksoftware.rag;

import com.klinksoftware.rag.walkview.WalkView;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import javax.swing.*;
import org.lwjgl.opengl.awt.*;

public class AppWindow implements WindowListener {

    public static final int WINDOW_WIDTH = 1000;
    public static final int WINDOW_HEIGHT = 600;
    public static final int TOOLBAR_HEIGHT = 38;
    public static final int HEADER_HEIGHT = 22;

    private static final int TOOL_BUTTON_RUN = 0;

    public static final int UI_TYPE_MAP = 0;
    public static final int UI_TYPE_MODEL = 1;
    public static final int UI_TYPE_BITMAPS = 2;

    private JFrame frame;
    private JPanel settingsPanel, spacerPanel;
    private JToolBar toolBar;
    private JButton runButton;
    private JLabel typeLabel, nameLabel;
    private JTextField nameField;
    private JComboBox typeComboBox;
    private GradientLabel settingsLabel, walkLabel;
    private WalkView walkView;

    //
    // window events
    //
    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        RandomAssetGenerator.stop();
        walkView.shutdown();
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    //
    // getters
    //
    public int getSelectedType() {
        return (typeComboBox.getSelectedIndex());
    }

    public String getName() {
        return (nameField.getText());
    }

    public void enableRunButton(boolean enabled) {
        runButton.setEnabled(enabled);
    }

    //
    // toolbar
    //
    private void toolBarClick(int buttonId) {
        switch (buttonId) {
            case TOOL_BUTTON_RUN:
                (new GeneratorRun(this)).execute();
                break;
        }
    }

    private JButton addToolButton(String iconName, int buttonId, String toolTipText) {
        URL iconURL;
        JButton button;

        iconURL = getClass().getResource("/Graphics/" + iconName + ".png");

        button = new JButton();
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setFocusable(false);
        button.setIcon(new ImageIcon(iconURL));
        button.setToolTipText(toolTipText);
        button.addActionListener(e -> toolBarClick(buttonId));

        toolBar.add(button);

        return (button);
    }

    //
    // start and stop main window
    //
    public void start() {
        URL iconURL;
        Image image;
        GLData glData;
        Runnable glLoop;

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        // window icon
        iconURL = getClass().getResource("/Graphics/icon.png");
        image = new ImageIcon(iconURL).getImage();

        // the quit menu event and doc icon
        // this is only handled on some OSes, so we just ignore if
        // it errors out
        try {
            Desktop.getDesktop().setQuitHandler((event, response) -> RandomAssetGenerator.stop());
            Taskbar.getTaskbar().setIconImage(image);
        } catch (Exception e) {
        }

        // create the window
        frame = new JFrame();

        frame.setTitle("Random Asset Generator");
        frame.setIconImage(image);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setMinimumSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

        frame.setLayout(new GridBagLayout());

        // toolbar
        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, TOOLBAR_HEIGHT));

        toolBar.add(Box.createHorizontalGlue());
        runButton = addToolButton("tool_run", TOOL_BUTTON_RUN, "Run");
        frame.add(toolBar, new GridBagConstraints(0, 0, 3, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        // the settings
        settingsLabel = new GradientLabel("Settings", new Color(196, 196, 255), new Color(128, 128, 255), false);
        frame.add(settingsLabel, new GridBagConstraints(0, 1, 1, 1, 0.2, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        settingsPanel = new JPanel(new GridBagLayout());
        settingsPanel.setBackground(Color.WHITE);
        frame.add(settingsPanel, new GridBagConstraints(0, 2, 1, 1, 0.2, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        typeLabel = new JLabel("Type:");
        typeLabel.setHorizontalAlignment(JLabel.RIGHT);
        settingsPanel.add(typeLabel, new GridBagConstraints(0, 0, 1, 1, 0.2, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));

        typeComboBox = new JComboBox(new String[]{"Map", "Model", "Bitmaps"});
        settingsPanel.add(typeComboBox, new GridBagConstraints(1, 0, 1, 1, 0.8, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));

        nameLabel = new JLabel("Name:");
        nameLabel.setHorizontalAlignment(JLabel.RIGHT);
        settingsPanel.add(nameLabel, new GridBagConstraints(0, 1, 1, 1, 0.2, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));

        nameField = new JTextField("test");
        settingsPanel.add(nameField, new GridBagConstraints(1, 1, 1, 1, 0.8, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));

        spacerPanel = new JPanel();
        spacerPanel.setBackground(Color.WHITE);
        settingsPanel.add(spacerPanel, new GridBagConstraints(0, 2, 1, 1, 0.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        // walk view
        walkLabel = new GradientLabel("Walk Through", new Color(196, 196, 255), new Color(128, 128, 255), true);
        frame.add(walkLabel, new GridBagConstraints(2, 1, 1, 1, 0.8, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        glData = new GLData();
        glData.samples = 4;
        glData.swapInterval = 0;
        walkView = new WalkView(glData);

        frame.add(walkView, new GridBagConstraints(2, 2, 1, 2, 0.8, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        // all the event listeners
        frame.addWindowListener(this);

        // show the window
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // a loop to constantly render the walk view
        glLoop = new Runnable() {
            @Override
            public void run() {
                if (walkView.isValid()) {
                    walkView.render();
                    SwingUtilities.invokeLater(this);
                }
            }
        };

        SwingUtilities.invokeLater(glLoop);
    }

    public void stop() {
        // dispose window

        frame.dispose();
    }
}
