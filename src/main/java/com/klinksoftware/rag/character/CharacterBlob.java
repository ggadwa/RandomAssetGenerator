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

        scene.bitmapGroup.add("body", new String[]{"BitmapFur", "BitmapOrganic", "BitmapScale"});
        scene.bitmapGroup.add("leg", new String[]{"BitmapFur", "BitmapOrganic", "BitmapScale"});
        scene.bitmapGroup.add("arm", new String[]{"BitmapFur", "BitmapOrganic", "BitmapScale"});
        scene.bitmapGroup.add("hand", new String[]{"BitmapFur", "BitmapOrganic", "BitmapScale"});
        scene.bitmapGroup.add("head", new String[]{"BitmapFur", "BitmapOrganic", "BitmapScale"});

        // build the skeleton
        skeletonBuilder = new SkeletonBuilder(scene, SkeletonBuilder.MODEL_TYPE_BLOB, false, true);
        skeletonBuilder.build();

        // build the meshes around the limbs
        wrapLimbs(skeletonBuilder.limbs, true);

        // snap vertexes
        //scene.snapVertexes();

        // any randomization
        scene.randomizeVertexes((0.2f + AppWindow.random.nextFloat(0.3f)), (0.035f + AppWindow.random.nextFloat(0.06f)));

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
