package com.klinksoftware.rag.skeleton;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

import java.util.*;

public class Limb
{
    public final int LIMB_TYPE_BODY=0;
    public final int LIMB_TYPE_NECK=1;
    public final int LIMB_TYPE_HEAD=2;
    public final int LIMB_TYPE_JAW=3;
    public final int LIMB_TYPE_ARM=4;
    public final int LIMB_TYPE_HAND=5;
    public final int LIMB_TYPE_FINGER=6;
    public final int LIMB_TYPE_LEG=7;
    public final int LIMB_TYPE_FOOT=8;
    public final int LIMB_TYPE_TOE=9;
    public final int LIMB_TYPE_WHIP=10;

    public final int LIMB_AXIS_X=0;
    public final int LIMB_AXIS_Y=1;
    public final int LIMB_AXIS_Z=2;
    
    public int                  limbType,axis,
                                acrossSurfaceCount,aroundSurfaceCount;
    public boolean              flipped,randomize;
    public RagPoint             scaleMin,scaleMax;
    public ArrayList<Integer>   boneIndexes;
    
    public Limb(int limbType,int axis,boolean flipped,int acrossSurfaceCount,int aroundSurfaceCount,boolean randomize,RagPoint scaleMin,RagPoint scaleMax,ArrayList<Integer> boneIndexes)
    {
        this.limbType=limbType;
        this.axis=axis;
        this.flipped=flipped;
        this.acrossSurfaceCount=acrossSurfaceCount;
        this.aroundSurfaceCount=aroundSurfaceCount;
        this.randomize=randomize;
        this.scaleMin=scaleMin;
        this.scaleMax=scaleMax;
        this.boneIndexes=boneIndexes;
    }
    
    public int getRandomBoneIndex()
    {
        return(boneIndexes.get(GeneratorMain.random.nextInt(boneIndexes.size())));
    }
}
