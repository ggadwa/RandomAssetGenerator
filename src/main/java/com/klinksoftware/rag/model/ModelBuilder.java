package com.klinksoftware.rag.model;

import com.klinksoftware.rag.bitmaps.*;
import com.klinksoftware.rag.export.Export;
import com.klinksoftware.rag.mesh.*;
import com.klinksoftware.rag.skeleton.*;
import com.klinksoftware.rag.utility.*;

import java.util.*;

public class ModelBuilder
{
    public Skeleton skeleton;
    public MeshList meshList;
    public HashMap<String, BitmapBase> bitmaps;

    public ModelBuilder() {
    }

        //
        // wrap the limbs with a mesh
        //

    public void wrapLimbs()
    {
        int         n,boneIdx,meshIdx;
        Limb        limb;
        Mesh        mesh;
        RagPoint    bonePnt;

            // wrap all the limbs
            // with meshes

        for (n=0;n!=skeleton.limbs.size();n++) {
            limb=skeleton.limbs.get(n);

                // mesh roots to first bone in list

            boneIdx=limb.boneIndexes[0];

                // wrap the mesh

            mesh=MeshModelUtility.buildMeshAroundBoneLimb(skeleton,limb);

                // add mesh and attach to bone

            meshIdx=meshList.add(mesh);
            skeleton.setBoneMeshIndex(boneIdx,meshIdx);
        }
    }

    //
    // required textures
    //
    private void buildRequiredTextures() {
        BitmapBase bitmapBase;

        // body
        bitmapBase = new BitmapSkin();
        bitmapBase.generate();
        bitmaps.put("body", bitmapBase);

        // limbs
        bitmapBase = new BitmapSkin();
        bitmapBase.generate();
        bitmaps.put("limb", bitmapBase);

        // head
        bitmapBase = new BitmapSkin();
        bitmapBase.generate();
        bitmaps.put("head", bitmapBase);
    }

        //
        // build a model
        //

    public void build()
    {
        bitmaps = new HashMap<>();

            // always use a single body bitmap

        buildRequiredTextures();

            // build the skeleton

        skeleton=(new SkeletonBuilder()).build();

            // build the meshes around the limbs

        meshList=new MeshList();
        wrapLimbs();

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
