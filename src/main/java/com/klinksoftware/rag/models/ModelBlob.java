package com.klinksoftware.rag.models;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.skeleton.Skeleton;
import com.klinksoftware.rag.skeleton.SkeletonBuilder;

@ModelInterface
public class ModelBlob extends ModelBase {

    public Skeleton skeleton;

    @Override
    public void buildMeshes() {
        addBitmap("body", new String[]{"Fur", "Organic", "Scale"});
        addBitmap("leg", new String[]{"Fur", "Organic", "Scale"});
        addBitmap("arm", new String[]{"Fur", "Organic", "Scale"});
        addBitmap("hand", new String[]{"Fur", "Organic", "Scale"});
        addBitmap("head", new String[]{"Fur", "Organic", "Scale"});

        // build the skeleton
        skeleton = (new SkeletonBuilder()).build(SkeletonBuilder.MODEL_TYPE_BLOB, bilateral);

        // build the meshes around the limbs
        wrapLimbs(skeleton.limbs, true);

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
