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
        bones.add(new Bone("root",-1,new RagPoint(0.0f,0.0f,0.0f)));     // first bone is always root
        
        limbs=new ArrayList<>();
    }
    
    public int addChildBone(int toBoneIndex,String name,int meshIdx,RagPoint pnt)
    {
        int         idx;
        
        idx=bones.size();
        bones.add(new Bone(name,meshIdx,pnt));
        
        bones.get(toBoneIndex).addChild(idx);
        
        return(idx);
    }
}
