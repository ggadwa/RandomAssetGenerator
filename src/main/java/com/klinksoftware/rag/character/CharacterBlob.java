package com.klinksoftware.rag.character;

import com.klinksoftware.rag.character.utility.SkeletonBuilder;
import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.character.utility.AnimationBuilder;
import com.klinksoftware.rag.character.utility.CharacterBase;
import com.klinksoftware.rag.character.utility.CharacterInterface;

@CharacterInterface
public class CharacterBlob extends CharacterBase {

    @Override
    public void buildMeshes() {
        SkeletonBuilder skeletonBuilder;

        if (organic) {
            scene.bitmapGroup.add("body", new String[]{"Fur", "Organic", "Scale"});
            scene.bitmapGroup.add("leg", new String[]{"Fur", "Organic", "Scale"});
            scene.bitmapGroup.add("arm", new String[]{"Fur", "Organic", "Scale"});
            scene.bitmapGroup.add("hand", new String[]{"Fur", "Organic", "Scale"});
            scene.bitmapGroup.add("head", new String[]{"Fur", "Organic", "Scale"});
        } else {
            scene.bitmapGroup.add("body", new String[]{"Metal", "MetalCorrugated", "MetalPlank", "MetalPlate"});
            scene.bitmapGroup.add("leg", new String[]{"Metal", "MetalCorrugated", "MetalPlank", "MetalPlate"});
            scene.bitmapGroup.add("arm", new String[]{"Metal", "MetalCorrugated", "MetalPlank", "MetalPlate"});
            scene.bitmapGroup.add("hand", new String[]{"Metal", "MetalCorrugated", "MetalPlank", "MetalPlate"});
            scene.bitmapGroup.add("head", new String[]{"Metal", "MetalCorrugated", "MetalPlank", "MetalPlate"});
        }

        // build the skeleton
        skeletonBuilder = new SkeletonBuilder(scene, SkeletonBuilder.MODEL_TYPE_BLOB, bilateral, organic);
        skeletonBuilder.build();

        // build the meshes around the limbs
        wrapLimbs(skeletonBuilder.limbs);

        // snap vertexes
        //scene.snapVertexes();

        // any randomization
        if ((organic) && (roughness != 0.0f)) {
            scene.randomizeVertexes(roughness, (0.025f + AppWindow.random.nextFloat(0.05f)));
        }

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
