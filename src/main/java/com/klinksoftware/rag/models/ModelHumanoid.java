package com.klinksoftware.rag.models;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.skeleton.SkeletonBuilder;

@ModelInterface
public class ModelHumanoid extends ModelBase {

    @Override
    public void buildInternal() {
        addBitmap("bitmap", new String[]{"Monster"});

        // build the skeleton
        skeleton = (new SkeletonBuilder()).build(SkeletonBuilder.MODEL_TYPE_HUMANOID);

        // build the meshes around the limbs
        wrapLimbs(true);

        // any randomization
        if (AppWindow.random.nextBoolean()) {
            meshList.randomizeVertexes((0.6f + AppWindow.random.nextFloat(1.0f)), (0.025f + AppWindow.random.nextFloat(0.05f)));
        }

        // skeletons and meshes are created with absolute
        // points, we need to change this to relative before
        // saving the model
        meshList.rebuildModelMeshWithSkeleton(skeleton);
    }
}