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
    
    private void addBoneAbsolutePointRecurse(int boneIdx,RagPoint pnt)
    {
        int         n;
        
        pnt.addPoint(bones.get(boneIdx).pnt);
        
        for (n=0;n!=bones.size();n++) {
            if (n==boneIdx) continue;
            
            if (bones.get(n).children.contains(boneIdx)) {
                addBoneAbsolutePointRecurse(n,pnt);
                break;
            }
        }
    }
    
    public RagPoint getBoneAbsolutePoint(int boneIdx)
    {
        RagPoint        pnt;
        Bone            bone;
        
        pnt=new RagPoint(0.0f,0.0f,0.0f);
        addBoneAbsolutePointRecurse(boneIdx,pnt);
        
        return(pnt);
    }
    
    public void setBoneMeshIndex(int boneIdx,int meshIdx)
    {
        bones.get(boneIdx).meshIdx=meshIdx;
    }
    
    public int addLimb(String name,int limbType,int axis,boolean flipped,int acrossSurfaceCount,int aroundSurfaceCount,RagPoint scale,int[] boneIndexes)
    {
        int         idx;
        String      bitmapName;
        
        switch (limbType) {
            case Limb.LIMB_TYPE_BODY:
                bitmapName="body";
                break;
            case Limb.LIMB_TYPE_HEAD:
                bitmapName="head";
                break;
            default:
                bitmapName="limb";
                break;
        }
        
        idx=limbs.size();
        limbs.add(new Limb(name,bitmapName,limbType,axis,flipped,acrossSurfaceCount,aroundSurfaceCount,scale,boneIndexes));
        
        return(idx);
    }
}
