package com.klinksoftware.rag.model.utility;

import com.klinksoftware.rag.model.utility.Limb;
import com.klinksoftware.rag.export.Export;
import com.klinksoftware.rag.scene.Scene;
import com.klinksoftware.rag.model.utility.MeshModelUtility;
import com.klinksoftware.rag.scene.Mesh;
import com.klinksoftware.rag.utility.RagPoint;
import java.util.ArrayList;

public class ModelBase {

    public Scene scene;

    public int textureSize;
    public boolean bilateral;
    public float roughness;

    // limb wrapping (for humanoid, etc models)
    // builds a mesh around two nodes and adds the mesh to first node of limb
    // we remember the nodes here to help attach joints and weights later
    public void wrapLimbs(ArrayList<Limb> limbs, boolean organic) {
        Mesh mesh;

        for (Limb limb : limbs) {
            mesh = MeshModelUtility.buildMeshAroundNodeLimb(scene, limb, organic);
            limb.node1.addMesh(mesh);
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
    // note: models are build with absolute vertexes, but unlike maps,
    // we leave the vertexes absolute and just attach them to
    public void build(int textureSize, boolean bilateral, float roughness) {
        this.textureSize = textureSize;
        this.bilateral = bilateral;
        this.roughness = roughness;

        scene = new Scene(textureSize);

        // run the internal mesh build
        buildMeshes();

        // models are build with absolute vertexes, we
        // need to adjust these to be relative to nodes
        scene.shiftAbsoluteMeshesToNodeRelativeMeshes();

        // need to build unique indexes for the meshes,
        // which is how they refer to each other in
        // the gltf
        scene.createMeshIndexes();

        if (scene.skinned) {
            // run the internal animation build, we add
            // any required joints here
            buildAnimations();

            // the inversebind matrixes for nodes
            scene.animation.createInverseBindMatrixForJoints();
        }

        // generate the bitmaps
        scene.bitmapGroup.generateAll();
    }
}
