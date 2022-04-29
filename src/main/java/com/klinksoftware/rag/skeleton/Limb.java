package com.klinksoftware.rag.skeleton;

import com.klinksoftware.rag.utility.*;

public class Limb
{

    public static final int MESH_TYPE_GLOBE = 0;
    public static final int MESH_TYPE_CYLINDER = 1;

    public static final int LIMB_AXIS_X = 0;
    public static final int LIMB_AXIS_Y=1;
    public static final int LIMB_AXIS_Z=2;

    public int meshType, axis, acrossSurfaceCount, aroundSurfaceCount;
    public int bone1Idx, bone2Idx;
    public String name, bitmapName;
    public RagPoint scale;

    public Limb(String name, String bitmapName, int meshType, int axis, int acrossSurfaceCount, int aroundSurfaceCount, RagPoint scale, int bone1Idx, int bone2Idx) {
        this.name=name;
        this.bitmapName = bitmapName;
        this.meshType = meshType;
        this.axis=axis;
        this.acrossSurfaceCount = acrossSurfaceCount;
        this.aroundSurfaceCount=aroundSurfaceCount;
        this.scale=scale;
        this.bone1Idx = bone1Idx;
        this.bone2Idx = bone2Idx;
    }

    //public int getRandomBoneIndex()
    //{
    //    return(boneIndexes[AppWindow.random.nextInt(boneIndexes.length)]);
    //}
}
