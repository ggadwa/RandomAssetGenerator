package com.klinksoftware.rag.model;

import com.klinksoftware.rag.model.utility.AnimationBuilder;
import com.klinksoftware.rag.model.utility.ModelInterface;
import com.klinksoftware.rag.model.utility.ModelBase;
import com.klinksoftware.rag.model.utility.SkeletonBuilder;

@ModelInterface
public class ModelMonsterRobot extends ModelBase {

    @Override
    public void buildMeshes() {
        SkeletonBuilder skeletonBuilder;

        scene.bitmapGroup.add("body", new String[]{"Metal", "MetalCorrugated", "MetalPlank", "MetalPlate"});
        scene.bitmapGroup.add("leg", new String[]{"Metal", "MetalCorrugated", "MetalPlank", "MetalPlate"});
        scene.bitmapGroup.add("foot", new String[]{"Metal", "MetalCorrugated", "MetalPlank", "MetalPlate"});
        scene.bitmapGroup.add("arm", new String[]{"Metal", "MetalCorrugated", "MetalPlank", "MetalPlate"});
        scene.bitmapGroup.add("hand", new String[]{"Metal", "MetalCorrugated", "MetalPlank", "MetalPlate"});
        scene.bitmapGroup.add("head", new String[]{"Metal", "MetalCorrugated", "MetalPlank", "MetalPlate"});

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
        AnimationBuilder animationBuilder;

        animationBuilder = new AnimationBuilder(scene);
        animationBuilder.build();
    }

}
