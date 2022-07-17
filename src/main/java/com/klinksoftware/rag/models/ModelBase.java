package com.klinksoftware.rag.models;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.bitmaps.BitmapBase;
import com.klinksoftware.rag.export.Export;
import com.klinksoftware.rag.mesh.Mesh;
import com.klinksoftware.rag.mesh.MeshList;
import com.klinksoftware.rag.mesh.MeshModelUtility;
import com.klinksoftware.rag.skeleton.Limb;
import com.klinksoftware.rag.skeleton.Skeleton;
import com.klinksoftware.rag.utility.RagPoint;
import java.util.HashMap;

public class ModelBase {

    public Skeleton skeleton;
    public MeshList meshList;
    public HashMap<String, BitmapBase> bitmaps;

    public boolean bilateral;
    public float roughness;

    // limb wrapping (for humanoid, etc models)
    public void wrapLimbs(boolean organic) {
        int n, meshIdx;
        Limb limb;
        Mesh mesh;

        // wrap all the limbs with meshes
        for (n = 0; n != skeleton.limbs.size(); n++) {
            limb = skeleton.limbs.get(n);

            // wrap the mesh
            mesh = MeshModelUtility.buildMeshAroundBoneLimb(skeleton, limb, organic);

            // add mesh and attach to bone
            meshIdx = meshList.add(mesh);
            skeleton.setBoneMeshIndex(limb.bone1Idx, meshIdx);
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
            bitmap = (BitmapBase) (Class.forName("com.klinksoftware.rag.bitmaps.Bitmap" + bitmapName.replace(" ", ""))).getConstructor().newInstance();
            bitmap.generate();
            bitmaps.put(name, bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // export model
    public void writeToFile(String path) {
        try {
            (new Export()).export(meshList, skeleton, bitmaps, path, "model");
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
        meshList.getMixMaxVertex(min, max);

        return ((max.y - min.y) / 2.0f);
    }

    public void buildInternal() {
    }

    // build a model
    public void build(boolean bilateral, float roughness) {
        this.bilateral = bilateral;
        this.roughness = roughness;

        meshList = new MeshList();
        bitmaps = new HashMap<>();

        // run the internal build
        buildInternal();
    }
}
