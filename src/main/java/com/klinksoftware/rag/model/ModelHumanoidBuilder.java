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
        int         n,meshIdx;
        Limb        limb;
        RagPoint    fullBodyScale;
        Mesh        mesh;
        
            // random body scaling
            
        fullBodyScale=new RagPoint(1.0f,(1.0f-(GeneratorMain.random.nextFloat()*0.3f)),(1.0f-(GeneratorMain.random.nextFloat()*0.2f)));
        
            // wrap all the limbs
            // with meshes
            
        for (n=0;n!=skeleton.limbs.size();n++) {
            limb=skeleton.limbs.get(n);

            mesh=MeshModelUtility.buildMeshAroundBoneLimb(skeleton,limb,"body");
            if (limb.randomize) MeshModelUtility.randomScaleVertexToBones(mesh);

                // add to model mesh
                // and set to first bone in set
                
            //meshIdx=meshList.add(mesh);
            //skeleton.setBoneMeshIndex(limb.boneIndexes[0],meshIdx);
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
        
            // some settings
         
        modelName=(String)GeneratorMain.settings.get("name");
        //height=(float)((double)GeneratorMain.settings.get("height"));
        
            // build the skeleton
            
        skeleton=(new SkeletonBuilder()).build();
        
            // build the meshes around the limbs
            
        meshList=new MeshList();
        wrapLimbs();

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
