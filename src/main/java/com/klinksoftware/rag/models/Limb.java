package com.klinksoftware.rag.models;

import com.klinksoftware.rag.scene.Node;
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
    public String name, bitmapName;
    public RagPoint scale;
    public Node node1, node2;

    public Limb(String name, String bitmapName, int meshType, int axis, RagPoint scale, Node node1, Node node2) {
        this.name = name;
        this.bitmapName = bitmapName;
        this.meshType = meshType;
        this.axis=axis;
        this.scale = scale;
        this.node1 = node1;
        this.node2 = node2;
    }
}
