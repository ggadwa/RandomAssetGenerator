package com.klinksoftware.rag.model;

import com.klinksoftware.rag.model.utility.ModelInterface;
import com.klinksoftware.rag.model.utility.ModelBase;
import com.klinksoftware.rag.model.utility.SkeletonBuilder;

@ModelInterface
public class ModelMonsterRobot extends ModelBase {

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
        skeletonBuilder = new SkeletonBuilder(scene, SkeletonBuilder.MODEL_TYPE_ROBOT, bilateral);
        skeletonBuilder.build();

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
