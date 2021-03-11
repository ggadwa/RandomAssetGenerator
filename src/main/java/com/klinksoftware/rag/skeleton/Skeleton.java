package com.klinksoftware.rag.skeleton;

import com.klinksoftware.rag.utility.*;

import java.util.*;

public class Skeleton
{
    public ArrayList<Bone>      bones;
    public ArrayList<Limb>      limbs;
    
    public Skeleton()
    {
        bones=new ArrayList<>();
        bones.add(new Bone("root",-1,1.0f,new RagPoint(0.0f,0.0f,0.0f)));     // first bone is always root
        
        limbs=new ArrayList<>();
    }
    
    public int addChildBone(int toBoneIndex,String name,int meshIdx,float gravityLockDistance,RagPoint pnt)
    {
        int         idx;
        
        idx=bones.size();
        bones.add(new Bone(name,meshIdx,gravityLockDistance,pnt));
        
        bones.get(toBoneIndex).addChild(idx);
        
        return(idx);
    }
    
    public int findBoneIndex(String name)
    {
        int         n;
        
        for (n=0;n!=bones.size();n++) {
            if (bones.get(n).name.equals(name)) return(n);
        }
        
        return(-1);
    }
    
    public int addLimb(int limbType,int axis,boolean flipped,int acrossSurfaceCount,int aroundSurfaceCount,boolean randomize,RagPoint scaleMin,RagPoint scaleMax,int[] boneIndexes)
    {
        int         idx;
        
        idx=limbs.size();
        limbs.add(new Limb(limbType,axis,flipped,acrossSurfaceCount,aroundSurfaceCount,randomize,scaleMin,scaleMax,boneIndexes));
        
        return(idx);
    }
}
