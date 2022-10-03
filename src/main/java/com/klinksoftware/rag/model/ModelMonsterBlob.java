package com.klinksoftware.rag.model;

import com.klinksoftware.rag.model.utility.ModelInterface;
import com.klinksoftware.rag.model.utility.ModelBase;
import com.klinksoftware.rag.model.utility.SkeletonBuilder;
import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.model.utility.AnimationBuilder;

@ModelInterface
public class ModelMonsterBlob extends ModelBase {

    @Override
    public void buildMeshes() {
        SkeletonBuilder skeletonBuilder;

        scene.bitmapGroup.add("body", new String[]{"Fur", "Organic", "Scale"});
        scene.bitmapGroup.add("leg", new String[]{"Fur", "Organic", "Scale"});
        scene.bitmapGroup.add("arm", new String[]{"Fur", "Organic", "Scale"});
        scene.bitmapGroup.add("hand", new String[]{"Fur", "Organic", "Scale"});
        scene.bitmapGroup.add("head", new String[]{"Fur", "Organic", "Scale"});

        // build the skeleton
        skeletonBuilder = new SkeletonBuilder(scene, SkeletonBuilder.MODEL_TYPE_BLOB, bilateral);
        skeletonBuilder.build();

        // build the meshes around the limbs
        wrapLimbs(skeletonBuilder.limbs, true);

        // snap vertexes
        //scene.snapVertexes();

        // any randomization
        if (roughness != 0.0f) {
            scene.randomizeVertexes(roughness, (0.025f + AppWindow.random.nextFloat(0.05f)));
        }

        // has animations
        scene.skinned = true;
    }

    @Override
    public void buildAnimations() {
        AnimationBuilder animationBuilder;

        animationBuilder = new AnimationBuilder(scene);
        animationBuilder.build();
    }

}
