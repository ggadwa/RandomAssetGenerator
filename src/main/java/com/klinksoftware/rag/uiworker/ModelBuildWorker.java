package com.klinksoftware.rag.uiworker;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.model.utility.ModelBase;
import java.util.*;
import javax.swing.*;

public class ModelBuildWorker extends SwingWorker<Integer,Void>
{
    private AppWindow appWindow;
    private int textureSize;
    private String modelName;
    private boolean bilateral;
    private float roughness;

    public static ModelBase generatedModel = null;

    public ModelBuildWorker(AppWindow appWindow, String modelName, int textureSize, boolean bilateral, float roughness) {
        this.appWindow = appWindow;
        this.modelName = modelName;
        this.textureSize = textureSize;
        this.bilateral = bilateral;
        this.roughness = roughness;
    }

    @Override
    protected Integer doInBackground() throws Exception {
        long seed;
        ModelBase model;

        appWindow.startBuild();

        // set the seed for model
        seed = Calendar.getInstance().getTimeInMillis();
        seed = 1663387204703L;
        AppWindow.random.setSeed(seed);
        System.out.println("seed=" + seed);

        // run the model builder
        try {
            model = (ModelBase) (Class.forName("com.klinksoftware.rag.model.Model" + modelName.replace(" ", ""))).getConstructor().newInstance();
            model.build(textureSize, bilateral, roughness);
        } catch (Exception e) {
            e.printStackTrace();
            return (0);
        }

        generatedModel = model;

        // and set the walk view
        AppWindow.walkView.setCameraCenterRotate(model.getCameraDistance(), model.getCameraRotateX(), model.getCameraRotateY(), model.getCameraOffsetY(), model.getCameraLightDistance());
        AppWindow.walkView.setIncommingScene(model.scene);

        appWindow.walkLabel.setGeneratedTitle("Model", seed);
        appWindow.switchView("walkView");

        return(0);
    }

    @Override
    protected void done() {
        appWindow.stopBuild();
    }
}
