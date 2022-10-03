package com.klinksoftware.rag;

import com.klinksoftware.rag.soundview.SoundView;
import com.klinksoftware.rag.walkview.WalkThread;
import com.klinksoftware.rag.walkview.WalkView;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.util.Random;
import java.util.concurrent.Semaphore;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import org.lwjgl.opengl.awt.GLData;

public class AppWindow extends JFrame {

    public static final int WINDOW_WIDTH = 1250;
    public static final int WINDOW_HEIGHT = 700;
    public static final int TOOLBAR_HEIGHT = 38;
    public static final int HEADER_HEIGHT = 22;
    public static final int SETTING_WIDTH = 250;

    public Semaphore glTerminate;
    public Semaphore glTerminated;

    public GradientLabel walkLabel;
    private GradientLabel settingsLabel;
    private JTabbedPane settingsTab;
    private JPanel displayPanel;
    private CardLayout displayPanelCardLayout;
    private SpinnerPane spinnerPane;

    public static Random random;
    public static WalkView walkView;
    public static SoundView soundView;
    public static SettingsMap settingsMap;
    public static SettingsProp settingsProp;
    public static SettingsTexture settingsTexture;
    public static SettingsSound settingsSound;
    public static ToolBar toolBar;

    private Thread renderThread;

    public AppWindow(Image image) {
        super();

        glTerminate = new Semaphore(0);
        glTerminated = new Semaphore(0);

        // setup
        setTitle("Random Asset Generator");
        setIconImage(image);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setMinimumSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

        setLayout(new GridBagLayout());

        // toolbar
        toolBar = new ToolBar();
        add(toolBar, new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        // walk view label
        walkLabel = new GradientLabel("Asset", new Color(196, 196, 255), new Color(128, 128, 255), false);
        add(walkLabel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        // card layout for display
        displayPanelCardLayout = new CardLayout();

        displayPanel = new JPanel();
        displayPanel.setLayout(displayPanelCardLayout);
        add(displayPanel, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        // walk view
        GLData glData = new GLData();
        //glData.majorVersion = 3;
        //glData.minorVersion = 3;
        //glData.profile = GLData.Profile.COMPATIBILITY;
        //glData.profile = GLData.Profile.CORE;
        //glData.forwardCompatible = true;
        glData.samples = 4;
        glData.swapInterval = 0;

        walkView = new WalkView(glData);
        displayPanel.add("walkView", walkView);

        // sound display
        soundView = new SoundView();
        displayPanel.add("soundView", soundView);

        // settings tabs
        settingsLabel = new GradientLabel("Settings", new Color(196, 196, 255), new Color(128, 128, 255), true);
        add(settingsLabel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        settingsTab = new JTabbedPane();
        settingsTab.setPreferredSize(new Dimension(SETTING_WIDTH, Integer.MAX_VALUE));
        settingsTab.setMinimumSize(new Dimension(SETTING_WIDTH, Integer.MAX_VALUE));
        settingsTab.setMaximumSize(new Dimension(SETTING_WIDTH, Integer.MAX_VALUE));
        settingsTab.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.black));
        add(settingsTab, new GridBagConstraints(1, 2, 1, 1, 0.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        // tabs
        settingsMap = new SettingsMap(this);
        settingsTab.addTab("Map", settingsMap);

        settingsProp = new SettingsProp(this);
        settingsTab.addTab("Prop", settingsProp);

        settingsTexture = new SettingsTexture(this);
        settingsTab.addTab("Texture", settingsTexture);

        settingsSound = new SettingsSound(this);
        settingsTab.addTab("Sound", settingsSound);

        // the spinner
        spinnerPane = new SpinnerPane();
        setGlassPane(spinnerPane);

        // quit handler for some OSes
        try {
            Desktop.getDesktop().setQuitHandler((event, response) -> dispose());
        } catch (Exception e) {
        }

        // show the window
        setLocationRelativeTo(null);
        setVisible(true);

        // create the random
        // this will be seeded when we start a build
        random = new Random(0);

        // and the main rendering thread
        renderThread = new Thread(new WalkThread(this));
        renderThread.start();
    }

    // utility
    public void startBuild() {
        settingsTab.setEnabled(false);
        settingsMap.enableAll(false);
        settingsProp.enableAll(false);
        settingsTexture.enableAll(false);
        settingsSound.enableAll(false);

        spinnerPane.start();
    }

    public void stopBuild() {
        settingsTab.setEnabled(true);
        settingsMap.enableAll(true);
        settingsProp.enableAll(true);
        settingsTexture.enableAll(true);
        settingsSound.enableAll(true);

        spinnerPane.stop();
    }

    public void switchView(String name) {
        displayPanelCardLayout.show(displayPanel, name);
    }

    // we use this to make sure the gl stuff is cleaned up
    // before the application exits
    @Override
    public void dispose() {
        glTerminate.release();

        try {
            glTerminated.acquire();
        } catch (InterruptedException e) {
        }

        super.dispose();
    }
}
