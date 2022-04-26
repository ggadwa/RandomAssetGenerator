package com.klinksoftware.rag.uiworker;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.model.*;
import java.util.*;
import javax.swing.*;

public class ModelBuildWorker extends SwingWorker<Integer,Void>
{
    private AppWindow appWindow;

    public static ModelBuilder generatedModel = null;

    public ModelBuildWorker(AppWindow appWindow) {
        this.appWindow=appWindow;
    }

    @Override
    protected Integer doInBackground() throws Exception {
        long seed;
        ModelBuilder modelBuilder;

        appWindow.enableSettings(false);

            // set the seed and base path for model
            // and make directories if necessary

        seed=Calendar.getInstance().getTimeInMillis();
        AppWindow.random.setSeed(seed);

            // run the model builder

        try {
            modelBuilder = new ModelBuilder();
            modelBuilder.build();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return (0);
        }

        generatedModel = modelBuilder;

        // and set the walk view
        AppWindow.walkView.setCameraCenterRotate(8.0f, 0.0f, 0.0f, 3.5f, 2.0f);
        AppWindow.walkView.setIncommingMeshList(modelBuilder.meshList, modelBuilder.skeleton, modelBuilder.bitmaps);

        return(0);
    }

    @Override
    protected void done() {
        appWindow.enableSettings(true);
    }
}
