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
        
        /*
        
        genMesh=new GenMeshMonsterClass(this.view,model,modelBitmap);
        genMesh.build();

        */
        


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
