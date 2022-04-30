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

    public int findBoneIndexforMeshIndex(int meshIdx)
    {
        int         n;

        for (n=0;n!=bones.size();n++) {
            if (bones.get(n).meshIdx==meshIdx) return(n);
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
                return;
            }
        }
    }

    public RagPoint getBoneAbsolutePoint(int boneIdx)
    {
        RagPoint pnt;

        pnt=new RagPoint(0.0f,0.0f,0.0f);
        addBoneAbsolutePointRecurse(boneIdx,pnt);

        return(pnt);
    }

    public void setBoneMeshIndex(int boneIdx,int meshIdx)
    {
        bones.get(boneIdx).meshIdx=meshIdx;
    }

    public int addLimb(String name, String bitmapName, int meshType, int axis, RagPoint scale, int bone1Idx, int bone2Idx) {
        int         idx;

        idx = limbs.size();
        limbs.add(new Limb(name, bitmapName, meshType, axis, scale, bone1Idx, bone2Idx));

        return(idx);
    }
}
