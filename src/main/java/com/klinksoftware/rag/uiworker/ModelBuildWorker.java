package com.klinksoftware.rag.uiworker;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.models.ModelBase;
import java.util.*;
import javax.swing.*;

public class ModelBuildWorker extends SwingWorker<Integer,Void>
{
    private AppWindow appWindow;
    private String modelName;

    public static ModelBase generatedModel = null;

    public ModelBuildWorker(AppWindow appWindow, String modelName) {
        this.appWindow = appWindow;
        this.modelName = modelName;
    }

    @Override
    protected Integer doInBackground() throws Exception {
        long seed;
        ModelBase model;

        appWindow.enableSettings(false);

            // set the seed and base path for model
            // and make directories if necessary

        seed=Calendar.getInstance().getTimeInMillis();
        AppWindow.random.setSeed(seed);
        //AppWindow.random.setSeed(1655414438748L);

        // run the model builder
        try {
            model = (ModelBase) (Class.forName("com.klinksoftware.rag.models.Model" + modelName.replace(" ", ""))).getConstructor().newInstance();
            model.build();
        } catch (Exception e) {
            e.printStackTrace();
            return (0);
        }

        generatedModel = model;

        // and set the walk view
        AppWindow.walkView.setCameraCenterRotate(8.0f, 0.0f, (model.skeleton.standing ? 0.0f : 90.0f), (model.skeleton.standing ? 4.5f : 4.5f), 2.0f);
        AppWindow.walkView.setIncommingMeshList(model.meshList, model.skeleton, model.bitmaps);

        appWindow.walkLabel.setGeneratedTitle("Model", seed);
        appWindow.switchView("walkView");

        return(0);
    }

    @Override
    protected void done() {
        appWindow.enableSettings(true);
    }
}
