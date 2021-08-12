package com.klinksoftware.rag.skeleton;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;

import java.util.*;

public class Limb
{
    public static final int LIMB_TYPE_BODY=0;
    public static final int LIMB_TYPE_HEAD=1;
    public static final int LIMB_TYPE_ARM=2;
    public static final int LIMB_TYPE_HAND=3;
    public static final int LIMB_TYPE_FINGER=4;
    public static final int LIMB_TYPE_LEG=5;
    public static final int LIMB_TYPE_FOOT=6;
    public static final int LIMB_TYPE_TOE=7;
    public static final int LIMB_TYPE_WHIP=8;

    public static final int LIMB_AXIS_X=0;
    public static final int LIMB_AXIS_Y=1;
    public static final int LIMB_AXIS_Z=2;
    
    public int                  limbType,axis,
                                acrossSurfaceCount,aroundSurfaceCount;
    public boolean              flipped,randomize;
    public String               name,bitmapName;
    public RagPoint             scale;
    public int[]                boneIndexes;
    
    public Limb(String name,String bitmapName,int limbType,int axis,boolean flipped,int acrossSurfaceCount,int aroundSurfaceCount,RagPoint scale,int[] boneIndexes)
    {
        this.name=name;
        this.bitmapName=bitmapName;
        this.limbType=limbType;
        this.axis=axis;
        this.flipped=flipped;
        this.acrossSurfaceCount=acrossSurfaceCount;
        this.aroundSurfaceCount=aroundSurfaceCount;
        this.scale=scale;
        this.boneIndexes=boneIndexes;
    }
    
    public int getRandomBoneIndex()
    {
        return(boneIndexes[AppWindow.random.nextInt(boneIndexes.length)]);
    }
}
