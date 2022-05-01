package com.klinksoftware.rag.skeleton;

import com.klinksoftware.rag.utility.*;

public class Limb
{
    public static final int MESH_TYPE_CYLINDER = 0;
    public static final int MESH_TYPE_CYLINDER_CLOSE_TOP = 1;
    public static final int MESH_TYPE_CYLINDER_CLOSE_BOTTOM = 2;
    public static final int MESH_TYPE_CYLINDER_CLOSE_ALL = 3;

    public static final int UV_MAP_TYPE_BODY = 0;
    public static final int UV_MAP_TYPE_ARM = 1;
    public static final int UV_MAP_TYPE_HAND = 2;
    public static final int UV_MAP_TYPE_LEG = 3;
    public static final int UV_MAP_TYPE_FOOT = 4;
    public static final int UV_MAP_TYPE_NECK = 5;
    public static final int UV_MAP_TYPE_HEAD = 6;
    public static final int UV_MAP_TYPE_WHIP = 7;

    public static final int LIMB_AXIS_X = 0;
    public static final int LIMB_AXIS_Y=1;
    public static final int LIMB_AXIS_Z=2;

    public int uvMapType, meshType, axis;
    public int bone1Idx, bone2Idx;
    public String name, bitmapName;
    public RagPoint scale;

    public Limb(String name, int uvMapType, int meshType, int axis, RagPoint scale, int bone1Idx, int bone2Idx) {
        this.name = name;
        this.uvMapType = uvMapType;
        this.meshType = meshType;
        this.axis=axis;
        this.scale = scale;
        this.bone1Idx = bone1Idx;
        this.bone2Idx = bone2Idx;
    }

    //public int getRandomBoneIndex()
    //{
    //    return(boneIndexes[AppWindow.random.nextInt(boneIndexes.length)]);
    //}
}
