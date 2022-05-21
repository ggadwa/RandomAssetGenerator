package com.klinksoftware.rag;

import com.klinksoftware.rag.walkview.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.Random;
import javax.swing.*;
import org.lwjgl.opengl.awt.*;

public class AppWindow implements WindowListener {

    public static final int WINDOW_WIDTH = 1250;
    public static final int WINDOW_HEIGHT = 600;
    public static final int TOOLBAR_HEIGHT = 38;
    public static final int HEADER_HEIGHT = 22;
    public static final int SETTING_WIDTH = 250;

    private static final int TOOL_BUTTON_MAP = 0;
    private static final int TOOL_BUTTON_MODEL = 1;
    private static final int TOOL_BUTTON_BITMAP = 2;

    public static final int UI_TYPE_MAP = 0;
    public static final int UI_TYPE_MODEL = 1;
    public static final int UI_TYPE_BITMAPS = 2;

    public JFrame frame;
    private JButton buildMapButton, buildModelButton, buildBitmapButton;
    private GradientLabel walkLabel, settingsLabel;
    private JTabbedPane settingsTab;

    public static Random random;
    public static WalkView walkView;
    public static SettingsMap settingsMap;
    public static SettingsModel settingsModel;
    public static SettingsTexture settingsTexture;
    public static SettingsSound settingsSound;

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
    // settings utility
    //
    public void enableSettings(boolean enable) {
        settingsTab.setEnabled(enable);
        settingsMap.enableAll(enable);
        settingsModel.enableAll(enable);
        settingsTexture.enableAll(enable);
        settingsSound.enableAll(enable);
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

        // walk view
        walkLabel = new GradientLabel("Walk Through", new Color(196, 196, 255), new Color(128, 128, 255), false);
        frame.add(walkLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        glData = new GLData();
        glData.samples = 4;
        glData.swapInterval = 0;
        walkView = new WalkView(glData);
        walkView.setFocusable(true);

        frame.add(walkView, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        // settings tabs
        settingsLabel = new GradientLabel("Settings", new Color(196, 196, 255), new Color(128, 128, 255), true);
        frame.add(settingsLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        settingsTab = new JTabbedPane();
        settingsTab.setPreferredSize(new Dimension(SETTING_WIDTH, Integer.MAX_VALUE));
        settingsTab.setMinimumSize(new Dimension(SETTING_WIDTH, Integer.MAX_VALUE));
        settingsTab.setMaximumSize(new Dimension(SETTING_WIDTH, Integer.MAX_VALUE));
        settingsTab.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.black));
        frame.add(settingsTab, new GridBagConstraints(1, 1, 1, 1, 0.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        // map pane
        settingsMap = new SettingsMap(this);
        settingsTab.addTab("Map", settingsMap);

        settingsModel = new SettingsModel(this);
        settingsTab.addTab("Model", settingsModel);

        settingsTexture = new SettingsTexture(this);
        settingsTab.addTab("Texture", settingsTexture);

        settingsSound = new SettingsSound(this);
        settingsTab.addTab("Sound", settingsSound);

        // all the event listeners
        frame.addWindowListener(this);

        // events in canvas
        walkView.addMouseMotionListener(
            new MouseMotionListener() {
                @Override
                public void mouseMoved(MouseEvent e) {
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    walkView.mouseDrag(e.getX(),e.getY());
                }
            });

        walkView.addMouseListener(
            new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    walkView.mousePressed(e.getButton(),e.getX(),e.getY());
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    walkView.mouseRelease(e.getButton());
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                }

                @Override
                public void mouseExited(MouseEvent e) {
                }
            });

        walkView.addKeyListener(
                new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                }

                @Override
                    public void keyPressed(KeyEvent e) {
                    walkView.keyPress(e.getKeyCode());
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    walkView.keyRelease(e.getKeyCode());
                }
            });

        // show the window
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // create the random
        // this will be seeded when we start a build
        random=new Random(0);

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
        frame.dispose();
    }

}
