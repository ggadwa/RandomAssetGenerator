package com.klinksoftware.rag.models;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.bitmaps.BitmapBase;
import com.klinksoftware.rag.export.Export;
import com.klinksoftware.rag.mesh.Mesh;
import com.klinksoftware.rag.mesh.MeshList;
import com.klinksoftware.rag.mesh.MeshModelUtility;
import com.klinksoftware.rag.skeleton.Limb;
import com.klinksoftware.rag.skeleton.Skeleton;
import java.util.HashMap;

public class ModelBase {

    public Skeleton skeleton;
    public MeshList meshList;
    public HashMap<String, BitmapBase> bitmaps;

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

    // override
    public void buildInternal() {
    }

    // build a model
    public void build() {
        meshList = new MeshList();
        bitmaps = new HashMap<>();

        // run the internal build
        buildInternal();
    }
}
