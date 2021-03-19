package com.klinksoftware.rag.model;

import com.klinksoftware.rag.bitmaps.*;
import com.klinksoftware.rag.*;
import com.klinksoftware.rag.export.*;
import com.klinksoftware.rag.mesh.*;
import com.klinksoftware.rag.skeleton.*;
import com.klinksoftware.rag.utility.*;

import java.util.*;

public class ModelHumanoidBuilder
{
    private String                  basePath;
    private Skeleton                skeleton;
    private MeshList                meshList;
    private BitmapGenerator         mapBitmapList;
    
    public ModelHumanoidBuilder(String basePath)
    {
        this.basePath=basePath;
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
        String              modelName;
        
            // always use a single body bitmap
        
        mapBitmapList=new BitmapGenerator(basePath);
        mapBitmapList.generateBody();
        mapBitmapList.generateLimb();
        mapBitmapList.generateHead();
        
            // some settings
         
        modelName=(String)GeneratorMain.settings.get("name");
        
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
            (new Export()).export(skeleton,meshList,basePath,modelName);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
