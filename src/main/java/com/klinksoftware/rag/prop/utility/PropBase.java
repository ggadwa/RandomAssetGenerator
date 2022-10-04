package com.klinksoftware.rag.prop.utility;

import com.klinksoftware.rag.export.Export;
import com.klinksoftware.rag.scene.Scene;
import com.klinksoftware.rag.utility.RagPoint;

public class PropBase {

    public Scene scene;
    public int textureSize;

    // export model
    public void writeToFile(String path) {
        try {
            (new Export()).export(scene, path, "prop");
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
    public void build(int textureSize) {
        this.textureSize = textureSize;

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

        // generate the bitmaps
        scene.bitmapGroup.generateAll();
    }
}
