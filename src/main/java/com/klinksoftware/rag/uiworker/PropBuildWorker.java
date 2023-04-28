package com.klinksoftware.rag.uiworker;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.prop.utility.PropBase;
import java.util.*;
import javax.swing.*;

public class PropBuildWorker extends SwingWorker<Integer, Void> {
    private AppWindow appWindow;
    private String propName;

    public static PropBase generatedProp = null;

    public PropBuildWorker(AppWindow appWindow, String propName) {
        this.appWindow = appWindow;
        this.propName = propName;
    }

    @Override
    protected Integer doInBackground() throws Exception {
        long seed;
        PropBase prop;

        appWindow.startBuild();

        // set the seed for prop
        seed = Calendar.getInstance().getTimeInMillis();
        //seed = 1663387204703L;
        AppWindow.random.setSeed(seed);
        //System.out.println("seed=" + seed);

        // run the prop builder
        try {
            prop = (PropBase) (Class.forName("com.klinksoftware.rag.prop." + propName.replace(" ", ""))).getConstructor().newInstance();
            prop.build();
        } catch (Exception e) {
            e.printStackTrace();
            return (0);
        }

        generatedProp = prop;

        // reset toolbar
        AppWindow.toolBar.reset(false);

        // and set the walk view
        AppWindow.walkView.setCameraCenterRotate(prop.getCameraDistance(), prop.getCameraAngle(), prop.getCameraOffsetY(), prop.getCameraFixedLightPoint(), 20, 90, 20);
        AppWindow.walkView.setIncommingScene(prop.scene);

        appWindow.walkLabel.setGeneratedTitle("Model", seed);
        appWindow.switchView("walkView");

        return(0);
    }

    @Override
    protected void done() {
        appWindow.stopBuild();
    }
}
