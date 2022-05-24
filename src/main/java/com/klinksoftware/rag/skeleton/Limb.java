package com.klinksoftware.rag.skeleton;

import com.klinksoftware.rag.utility.*;

public class Limb
{
    public static final int MESH_TYPE_CYLINDER = 0;
    public static final int MESH_TYPE_CYLINDER_CLOSE_TOP = 1;
    public static final int MESH_TYPE_CYLINDER_CLOSE_BOTTOM = 2;
    public static final int MESH_TYPE_CYLINDER_CLOSE_ALL = 3;

    public static final int LIMB_AXIS_X = 0;
    public static final int LIMB_AXIS_Y=1;
    public static final int LIMB_AXIS_Z=2;

    public int uvMapType, meshType, axis;
    public int bone1Idx, bone2Idx;
    public float uOffset, vOffset, uSize, vSize;
    public String name, bitmapName;
    public RagPoint scale;

    public Limb(String name, int meshType, int axis, RagPoint scale, float uOffset, float vOffset, float uSize, float vSize, int bone1Idx, int bone2Idx) {
        this.name = name;
        this.meshType = meshType;
        this.axis=axis;
        this.scale = scale;
        this.uOffset = uOffset;
        this.vOffset = vOffset;
        this.uSize = uSize;
        this.vSize = vSize;
        this.bone1Idx = bone1Idx;
        this.bone2Idx = bone2Idx;
    }
}
