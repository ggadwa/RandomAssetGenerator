package com.klinksoftware.rag.models;

import com.klinksoftware.rag.skeleton.SkeletonBuilder;

@ModelInterface
public class ModelRobot extends ModelBase {

    @Override
    public void buildInternal() {
        addBitmap("bitmap", new String[]{"Robot"});

        // build the skeleton
        skeleton = (new SkeletonBuilder()).build(SkeletonBuilder.MODEL_TYPE_ROBOT);

        // build the meshes around the limbs
        wrapLimbs(false);

        // skeletons and meshes are created with absolute
        // points, we need to change this to relative before
        // saving the model
        meshList.rebuildModelMeshWithSkeleton(skeleton);
    }
}
