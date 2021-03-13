package com.klinksoftware.rag.mesh;

import com.klinksoftware.rag.skeleton.*;
import com.klinksoftware.rag.utility.*;

import java.util.*;

public class MeshList
{
    private ArrayList<Mesh>         meshes;
    
    public MeshList()
    {
        meshes=new ArrayList<>();
    }
    
    public int add(Mesh mesh)
    {
        meshes.add(mesh);
        return(meshes.size()-1);
    }
    
    public Mesh get(int index)
    {
        return(meshes.get(index));
    }
    
    public int count()
    {
        return(meshes.size());
    }
    
    public Skeleton rebuildMapMeshesWithSkeleton()
    {
        int             n;
        Mesh            mesh;
        Skeleton        skeleton;
        RagPoint        center;
        
            // this is used to turn boneless maps
            // into a simple skeleton with a single
            // root node
        
        skeleton=new Skeleton();        // node 0 will be root
        
        center=new RagPoint(0.0f,0.0f,0.0f);
        
        for (n=0;n!=meshes.size();n++) {
            mesh=meshes.get(n);
            
            mesh.getCenterPoint(center);
            skeleton.addChildBone(0,mesh.name,n,1.0f,center);
            mesh.makeVertexesRelativeToPoint(center);
        }
        
        return(skeleton);
    }
    
    private void rebuildModelMeshWithSkeletonRecurse(Skeleton skeleton,Bone bone,RagPoint offsetPnt)
    {
        int         n;
        RagPoint    nextOffsetPnt;
        
        nextOffsetPnt=new RagPoint(bone.pnt.x,bone.pnt.y,bone.pnt.z);
        
        if (offsetPnt!=null) {
            bone.pnt.x-=offsetPnt.x;
            bone.pnt.y-=offsetPnt.y;
            bone.pnt.z-=offsetPnt.z;
        }
        
        if (bone.meshIdx!=-1) meshes.get(bone.meshIdx).makeVertexesRelativeToPoint(bone.pnt);
        
        for (n=0;n!=bone.children.size();n++) {
            rebuildModelMeshWithSkeletonRecurse(skeleton,skeleton.bones.get(bone.children.get(n)),nextOffsetPnt);
        }
    }
    
    public void rebuildModelMeshWithSkeleton(Skeleton skeleton)
    {
            // when constructing the bones are
            // all absolute, this makes everything relative
        
        rebuildModelMeshWithSkeletonRecurse(skeleton,skeleton.bones.get(0),null);
    }
    
}
