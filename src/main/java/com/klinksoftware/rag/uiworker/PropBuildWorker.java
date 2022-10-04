package com.klinksoftware.rag.uiworker;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.prop.utility.PropBase;
import java.util.*;
import javax.swing.*;

public class PropBuildWorker extends SwingWorker<Integer, Void> {
    private AppWindow appWindow;
    private int textureSize;
    private String propName;

    public static PropBase generatedProp = null;

    public PropBuildWorker(AppWindow appWindow, String propName, int textureSize) {
        this.appWindow = appWindow;
        this.propName = propName;
        this.textureSize = textureSize;
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
        System.out.println("seed=" + seed);

        // run the prop builder
        try {
            prop = (PropBase) (Class.forName("com.klinksoftware.rag.prop.Prop" + propName.replace(" ", ""))).getConstructor().newInstance();
            prop.build(textureSize);
        } catch (Exception e) {
            e.printStackTrace();
            return (0);
        }

        generatedProp = prop;

        // reset toolbar
        AppWindow.toolBar.reset(false);

        // and set the walk view
        AppWindow.walkView.setCameraCenterRotate(prop.getCameraDistance(), prop.getCameraRotateX(), prop.getCameraRotateY(), prop.getCameraOffsetY(), prop.getCameraLightDistance());
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
