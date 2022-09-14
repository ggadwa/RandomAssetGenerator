package com.klinksoftware.rag.model;

import com.klinksoftware.rag.model.utility.ModelInterface;
import com.klinksoftware.rag.model.utility.ModelBase;
import com.klinksoftware.rag.model.utility.SkeletonBuilder;
import com.klinksoftware.rag.AppWindow;

@ModelInterface
public class ModelMonsterBlob extends ModelBase {

    @Override
    public void buildMeshes() {
        SkeletonBuilder skeletonBuilder;

        addBitmap("body", new String[]{"Fur", "Organic", "Scale"});
        addBitmap("leg", new String[]{"Fur", "Organic", "Scale"});
        addBitmap("arm", new String[]{"Fur", "Organic", "Scale"});
        addBitmap("hand", new String[]{"Fur", "Organic", "Scale"});
        addBitmap("head", new String[]{"Fur", "Organic", "Scale"});

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

    }

}
