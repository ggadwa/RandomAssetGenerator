package com.klinksoftware.rag.model;

import com.klinksoftware.rag.bitmaps.*;
import com.klinksoftware.rag.*;
import com.klinksoftware.rag.export.*;
import com.klinksoftware.rag.mesh.*;
import com.klinksoftware.rag.utility.*;

import java.util.*;

public class ModelHumanoidBuilder
{
    private String                  basePath;
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
        float               height;
        String              modelName;
        
            // always use a single body bitmap
        
        mapBitmapList=new BitmapGenerator(basePath);
         mapBitmapList.generateBody();
        
            // some settings
         
        modelName=(String)GeneratorMain.settings.get("name");
        height=(float)((double)GeneratorMain.settings.get("height"));       
        
            // no meshes or bones
            
        meshList=new MeshList();
        
        /*
                    // mesh and skeleton
            
        genSkeleton=new GenSkeletonMonsterClass(this.view,model,sizeFactor);
        genSkeleton.build();
        
        genMesh=new GenMeshMonsterClass(this.view,model,modelBitmap);
        genMesh.build();

        */
        

        /*
            // write out the model
        
        try {
            (new Export()).export(meshList,basePath,modelName);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
*/
    }
}
