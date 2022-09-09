package com.klinksoftware.rag.models;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.bitmaps.BitmapBase;
import com.klinksoftware.rag.export.Export;
import com.klinksoftware.rag.scene.Mesh;
import com.klinksoftware.rag.scene.Scene;
import com.klinksoftware.rag.skeleton.Limb;
import com.klinksoftware.rag.utility.RagPoint;
import java.util.ArrayList;

public class ModelBase {

    public Scene scene;

    public int textureSize;
    public boolean bilateral;
    public float roughness;

    // limb wrapping (for humanoid, etc models)
    public void wrapLimbs(ArrayList<Limb> limbs, boolean organic) {
        int n, meshIdx;
        Limb limb;
        Mesh mesh;

        // wrap all the limbs with meshes
        for (n = 0; n != limbs.size(); n++) {
            limb = limbs.get(n);

            // wrap the mesh
            //mesh = MeshModelUtility.buildMeshAroundBoneLimb(skeleton, limb, organic);

            // add mesh and attach to bone
            //meshIdx = meshList.add(mesh);
            //skeleton.setBoneMeshIndex(limb.bone1Idx, meshIdx);
        }
    }

    // bitmaps
    public void addBitmap(String name, String[] bitmapList) {
        int len;
        String bitmapName;
        BitmapBase bitmap;

        len = bitmapList.length;
        bitmapName = bitmapList[AppWindow.random.nextInt(len)];

        try {
            bitmap = (BitmapBase) (Class.forName("com.klinksoftware.rag.bitmaps.Bitmap" + bitmapName.replace(" ", ""))).getConstructor(int.class).newInstance(textureSize);
            bitmap.generate();
            scene.bitmaps.put(name, bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // export model
    public void writeToFile(String path) {
        try {
            (new Export()).export(scene, path, "model");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // overrides
    public float getCameraDistance() {
        return (8.0f);
    }

    public float getCameraLightDistance() {
        return (2.0f);
    }

    public float getCameraRotateX() {
        return (0.0f);
    }

    public float getCameraRotateY() {
        return (0.0f);
    }

    public float getCameraOffsetY() {
        RagPoint min, max;

        min = new RagPoint(0.0f, 0.0f, 0.0f);
        max = new RagPoint(0.0f, 0.0f, 0.0f);
        scene.getMixMaxVertex(min, max);

        return ((max.y - min.y) / 2.0f);
    }

    public void buildMeshes() {
    }

    public void buildAnimations() {
    }

    // build a model
    public void build(int textureSize, boolean bilateral, float roughness) {
        this.textureSize = textureSize;
        this.bilateral = bilateral;
        this.roughness = roughness;

        scene = new Scene();

        // run the internal mesh build
        buildMeshes();

        // models are build with absolute vertexes, we
        // need to adjust these to be relative to nodes
        scene.shiftAbsoluteMeshesToNodeRelativeMeshes();

        // need to build unique indexes for all nodes
        // and meshes, which is how they refer to each other in
        // the gltf
        scene.createNodeAndMeshIndexes();

        if (scene.skinned) {
            // create joints and weights for animation
            // has to happen after node indexes are created
            scene.createJointsAndWeights();

            // default no animation joints
            //skeleton.animation.createJointMatrixesFromBones();
            // run the internal animation build
            buildAnimations();
        }
    }
}
