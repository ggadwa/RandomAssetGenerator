package com.klinksoftware.rag.models;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.skeleton.SkeletonBuilder;

@ModelInterface
public class ModelHumanoid extends ModelBase {

    @Override
    public void buildInternal() {
        addBitmap("body", new String[]{"Fur", "Organic", "Scale"});
        addBitmap("leg", new String[]{"Fur", "Organic", "Scale"});
        addBitmap("foot", new String[]{"Fur", "Organic", "Scale"});
        addBitmap("arm", new String[]{"Fur", "Organic", "Scale"});
        addBitmap("hand", new String[]{"Fur", "Organic", "Scale"});
        addBitmap("head", new String[]{"Fur", "Organic", "Scale"});

        // build the skeleton
        skeleton = (new SkeletonBuilder()).build(SkeletonBuilder.MODEL_TYPE_HUMANOID, bilateral);

        // build the meshes around the limbs
        wrapLimbs(true);

        // any randomization
        if (roughness != 0.0f) {
            meshList.randomizeVertexes(roughness, (0.025f + AppWindow.random.nextFloat(0.05f)));
        }

        // skeletons and meshes are created with absolute
        // points, we need to change this to relative before
        // saving the model
        meshList.rebuildModelMeshWithSkeleton(skeleton);
    }
}
