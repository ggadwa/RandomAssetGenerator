package com.klinksoftware.rag.model.utility;

import com.klinksoftware.rag.model.utility.Limb;
import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.bitmap.utility.BitmapBase;
import com.klinksoftware.rag.export.Export;
import com.klinksoftware.rag.scene.Scene;
import com.klinksoftware.rag.model.utility.MeshModelUtility;
import com.klinksoftware.rag.utility.RagPoint;
import java.util.ArrayList;

public class ModelBase {

    public Scene scene;

    public int textureSize;
    public boolean bilateral;
    public float roughness;

    // bitmaps
    public void addBitmap(String name, String[] bitmapList) {
        int len;
        String bitmapName;
        BitmapBase bitmap;

        len = bitmapList.length;
        bitmapName = bitmapList[AppWindow.random.nextInt(len)];

        try {
            bitmap = (BitmapBase) (Class.forName("com.klinksoftware.rag.bitmap.Bitmap" + bitmapName.replace(" ", ""))).getConstructor(int.class).newInstance(textureSize);
            bitmap.generate();
            scene.bitmaps.put(name, bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // limb wrapping (for humanoid, etc models)
    // builds a mesh around two nodes and adds the mesh to first node of limb
    public void wrapLimbs(ArrayList<Limb> limbs, boolean organic) {
        for (Limb limb : limbs) {
            limb.node1.addMesh(MeshModelUtility.buildMeshAroundNodeLimb(scene, limb, organic));
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
        scene.getAbsoluteMixMaxVertexForRelativeVertexes(min, max);

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
            scene.animation.createJointMatrixComponentsFromNodes();

            // run the internal animation build
            buildAnimations();
        }
    }
}
