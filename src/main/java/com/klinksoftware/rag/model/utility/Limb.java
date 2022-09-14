package com.klinksoftware.rag.model.utility;

import com.klinksoftware.rag.scene.Node;
import com.klinksoftware.rag.utility.*;

public class Limb
{
    public static final int MESH_TYPE_CYLINDER = 0;
    public static final int MESH_TYPE_CYLINDER_CLOSE_TOP = 1;
    public static final int MESH_TYPE_CYLINDER_CLOSE_BOTTOM = 2;
    public static final int MESH_TYPE_CYLINDER_CLOSE_ALL = 3;
    public static final int MESH_TYPE_GLOBE = 4;

    public static final int LIMB_AXIS_X = 0;
    public static final int LIMB_AXIS_Y=1;
    public static final int LIMB_AXIS_Z=2;

    public int uvMapType, meshType, axis;
    public float radius1, radius2;
    public String name, bitmapName;
    public RagPoint scale, globeRadius, globeRotAngle;
    public Node node1, node2;

    // cylinder types
    public Limb(String name, String bitmapName, int meshType, int axis, RagPoint scale, Node node1, float radius1, Node node2, float radius2) {
        this.name = name;
        this.bitmapName = bitmapName;
        this.meshType = meshType;
        this.axis=axis;
        this.scale = scale;
        this.node1 = node1;
        this.radius1 = radius1;
        this.node2 = node2;
        this.radius2 = radius2;
        this.globeRadius = null;
        this.globeRotAngle = null;
    }

    // globe types
    public Limb(String name, String bitmapName, int meshType, Node node1, RagPoint globeRadius, RagPoint globalRotAngle) {
        this.name = name;
        this.bitmapName = bitmapName;
        this.meshType = meshType;
        this.axis = axis;
        this.scale = scale;
        this.node1 = node1;
        this.globeRadius = globeRadius.copy();
        this.globeRotAngle = (globeRotAngle == null) ? null : globeRotAngle.copy();
        this.node2 = null;
        this.radius2 = 0.0f;
    }
}
