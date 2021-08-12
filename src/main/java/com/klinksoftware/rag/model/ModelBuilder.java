package com.klinksoftware.rag.model;

import com.klinksoftware.rag.bitmaps.*;
import com.klinksoftware.rag.*;
import com.klinksoftware.rag.export.*;
import com.klinksoftware.rag.mesh.*;
import com.klinksoftware.rag.skeleton.*;
import com.klinksoftware.rag.utility.*;

import java.util.*;

public class ModelBuilder
{
    private String name;
    private Skeleton skeleton;
    private MeshList meshList;
    private BitmapGenerator mapBitmapList;
    
    public ModelBuilder(String name) {
        this.name=name;
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
        // build a model
        //
        
    public void build()
    {
            // always use a single body bitmap
        
        mapBitmapList=new BitmapGenerator(name);
        mapBitmapList.generateBody();
        mapBitmapList.generateLimb();
        mapBitmapList.generateHead();
        
            // build the skeleton
            
        skeleton=(new SkeletonBuilder()).build();
        
            // build the meshes around the limbs
            
        meshList=new MeshList();
        wrapLimbs();
        
            // skeletons and meshes are created with absolute
            // points, we need to change this to relative before
            // saving the model
            
        meshList.rebuildModelMeshWithSkeleton(skeleton);

            // write out the model
        
        try {
            (new Export()).export(skeleton,meshList,name);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
            // and set the walk view
            
        AppWindow.walkView.setIncommingMeshList(meshList,skeleton);
    }
}
