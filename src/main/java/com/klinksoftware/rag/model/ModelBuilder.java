package com.klinksoftware.rag.model;

import com.klinksoftware.rag.SettingsModel;
import com.klinksoftware.rag.bitmaps.*;
import com.klinksoftware.rag.export.Export;
import com.klinksoftware.rag.mesh.*;
import com.klinksoftware.rag.skeleton.*;

import java.util.*;

public class ModelBuilder
{

    public Skeleton skeleton;
    public MeshList meshList;
    public HashMap<String, BitmapBase> bitmaps;

        //
        // wrap the limbs with a mesh
        //

    public void wrapLimbs(int modelType) {
        int n, meshIdx;
        Limb limb;
        Mesh mesh;

        // wrap all the limbs
            // with meshes

        for (n=0;n!=skeleton.limbs.size();n++) {
            limb=skeleton.limbs.get(n);

                // wrap the mesh

            mesh = MeshModelUtility.buildMeshAroundBoneLimb(skeleton, modelType, limb);

                // add mesh and attach to bone

            meshIdx=meshList.add(mesh);
            skeleton.setBoneMeshIndex(limb.bone1Idx, meshIdx);
        }
    }

    // build a model
    public void build(int modelType, boolean thin, boolean bilaterial) {
        BitmapBase bitmapBase;

        // for now just the monster texture
        bitmaps = new HashMap<>();
        if (modelType != SettingsModel.MODEL_TYPE_ROBOT) {
            bitmapBase = new BitmapMonster();
        } else {
            bitmapBase = new BitmapMetal();
        }
        bitmapBase.generate();
        bitmaps.put("bitmap", bitmapBase);

            // build the skeleton

        skeleton = (new SkeletonBuilder()).build(modelType, thin, bilaterial);

            // build the meshes around the limbs

        meshList=new MeshList();
        wrapLimbs(modelType);

            // skeletons and meshes are created with absolute
            // points, we need to change this to relative before
            // saving the model

        meshList.rebuildModelMeshWithSkeleton(skeleton);
    }

    public void writeToFile(String path) {
        try {
            (new Export()).export(meshList, skeleton, bitmaps, path, "model");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
