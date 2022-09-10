package com.klinksoftware.rag.models;

@ModelInterface
public class ModelRobot extends ModelBase {

    @Override
    public void buildMeshes() {
        SkeletonBuilder skeletonBuilder;

        addBitmap("body", new String[]{"Metal", "MetalCorrugated", "MetalPlank", "MetalPlate"});
        addBitmap("leg", new String[]{"Metal", "MetalCorrugated", "MetalPlank", "MetalPlate"});
        addBitmap("foot", new String[]{"Metal", "MetalCorrugated", "MetalPlank", "MetalPlate"});
        addBitmap("arm", new String[]{"Metal", "MetalCorrugated", "MetalPlank", "MetalPlate"});
        addBitmap("hand", new String[]{"Metal", "MetalCorrugated", "MetalPlank", "MetalPlate"});
        addBitmap("head", new String[]{"Metal", "MetalCorrugated", "MetalPlank", "MetalPlate"});

        // build the skeleton
        skeletonBuilder = new SkeletonBuilder(scene);
        skeletonBuilder.build(SkeletonBuilder.MODEL_TYPE_ROBOT, bilateral);

        // build the meshes around the limbs
        wrapLimbs(skeletonBuilder.limbs, false);

        // snap vertexes
        //scene.snapVertexes();

        // has animations
        scene.skinned = true;
    }

    @Override
    public void buildAnimations() {

    }

}
