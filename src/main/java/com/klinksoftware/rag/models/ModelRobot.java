package com.klinksoftware.rag.models;

import com.klinksoftware.rag.skeleton.Skeleton;
import com.klinksoftware.rag.skeleton.SkeletonBuilder;

@ModelInterface
public class ModelRobot extends ModelBase {

    public Skeleton skeleton;

    @Override
    public void buildMeshes() {
        addBitmap("body", new String[]{"Metal", "MetalCorrugated", "MetalPlank", "MetalPlate"});
        addBitmap("leg", new String[]{"Metal", "MetalCorrugated", "MetalPlank", "MetalPlate"});
        addBitmap("foot", new String[]{"Metal", "MetalCorrugated", "MetalPlank", "MetalPlate"});
        addBitmap("arm", new String[]{"Metal", "MetalCorrugated", "MetalPlank", "MetalPlate"});
        addBitmap("hand", new String[]{"Metal", "MetalCorrugated", "MetalPlank", "MetalPlate"});
        addBitmap("head", new String[]{"Metal", "MetalCorrugated", "MetalPlank", "MetalPlate"});

        // build the skeleton
        skeleton = (new SkeletonBuilder()).build(SkeletonBuilder.MODEL_TYPE_ROBOT, bilateral);

        // build the meshes around the limbs
        wrapLimbs(skeleton.limbs, false);

        // has animations
        scene.skinned = true;
    }

    @Override
    public void buildAnimations() {

    }

}
