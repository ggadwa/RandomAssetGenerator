package com.klinksoftware.rag.character;

import com.klinksoftware.rag.character.utility.SkeletonBuilder;
import com.klinksoftware.rag.character.utility.AnimationBuilder;
import com.klinksoftware.rag.character.utility.CharacterBase;
import com.klinksoftware.rag.character.utility.CharacterInterface;

@CharacterInterface
public class CharacterRobot extends CharacterBase {

    @Override
    public void buildMeshes() {
        SkeletonBuilder skeletonBuilder;

        scene.bitmapGroup.add("body", new String[]{"BitmapMetal", "BitmapMetalCorrugated", "BitmapMetalPlank", "BitmapMetalPlate"});
        scene.bitmapGroup.add("leg", new String[]{"BitmapMetal", "BitmapMetalCorrugated", "BitmapMetalPlank", "BitmapMetalPlate"});
        scene.bitmapGroup.add("foot", new String[]{"BitmapMetal", "BitmapMetalCorrugated", "BitmapMetalPlank", "BitmapMetalPlate"});
        scene.bitmapGroup.add("arm", new String[]{"BitmapMetal", "BitmapMetalCorrugated", "BitmapMetalPlank", "BitmapMetalPlate"});
        scene.bitmapGroup.add("hand", new String[]{"BitmapMetal", "BitmapMetalCorrugated", "BitmapMetalPlank", "BitmapMetalPlate"});
        scene.bitmapGroup.add("head", new String[]{"BitmapMetal", "BitmapMetalCorrugated", "BitmapMetalPlank", "BitmapMetalPlate"});

        // build the skeleton
        skeletonBuilder = new SkeletonBuilder(scene, SkeletonBuilder.MODEL_TYPE_HUMANOID, true, false);
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
