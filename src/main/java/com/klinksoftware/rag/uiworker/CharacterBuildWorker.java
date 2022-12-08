package com.klinksoftware.rag.uiworker;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.character.utility.CharacterBase;
import java.util.*;
import javax.swing.*;

public class CharacterBuildWorker extends SwingWorker<Integer, Void> {
    private AppWindow appWindow;
    private String propName;
    private boolean bilateral, organic;
    private float roughness;

    public static CharacterBase generatedCharacter = null;

    public CharacterBuildWorker(AppWindow appWindow, String propName, boolean bilateral, boolean organic, float roughness) {
        this.appWindow = appWindow;
        this.propName = propName;
        this.bilateral = bilateral;
        this.organic = organic;
        this.roughness = roughness;
    }

    @Override
    protected Integer doInBackground() throws Exception {
        long seed;
        CharacterBase character;

        appWindow.startBuild();

        // set the seed for prop
        seed = Calendar.getInstance().getTimeInMillis();
        //seed = 1663387204703L;
        AppWindow.random.setSeed(seed);
        System.out.println("seed=" + seed);

        // run the prop builder
        try {
            character = (CharacterBase) (Class.forName("com.klinksoftware.rag.character.Character" + propName.replace(" ", ""))).getConstructor().newInstance();
            character.build(bilateral, organic, roughness);
        } catch (Exception e) {
            e.printStackTrace();
            return (0);
        }

        generatedCharacter = character;

        // reset toolbar
        AppWindow.toolBar.reset(true);

        // and set the walk view
        AppWindow.walkView.setCameraCenterRotate(character.getCameraDistance(), character.getCameraRotateX(), character.getCameraRotateY(), character.getCameraOffsetY(), character.getCameraLightDistance());
        AppWindow.walkView.setIncommingScene(character.scene);

        appWindow.walkLabel.setGeneratedTitle("Model", seed);
        appWindow.switchView("walkView");

        return(0);
    }

    @Override
    protected void done() {
        appWindow.stopBuild();
    }
}
