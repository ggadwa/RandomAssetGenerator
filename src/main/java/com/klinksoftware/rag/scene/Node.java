package com.klinksoftware.rag.scene;

import com.klinksoftware.rag.utility.RagPoint;
import java.util.ArrayList;

// this is a node in the scene, equivalent to what you see in
// a gltf file, has list of child nodes and list of child meshes,
// the meshes become primitives in the gltf
//
// there is some extra non-gltf data in these nodes used in the
// construction of some items
public class Node {

    public int index;
    public float limbRadius;
    public String name;
    public RagPoint pnt, absolutePnt;
    public ArrayList<Node> childNodes;
    public ArrayList<Mesh> meshes;

    public Node(String name, RagPoint pnt) {
        this.name = name;
        this.pnt = pnt.copy();

        childNodes = new ArrayList<>();
        meshes = new ArrayList<>();

        absolutePnt = null; // for animation calcs
        limbRadius = 0.0f; // for limb building in models
    }

    // this version is for the skeleton builder, which attaches
    // a radius for limb building
    public Node(String name, float limbRadius, RagPoint pnt) {
        this.name = name;
        this.pnt = pnt.copy();

        childNodes = new ArrayList<>();
        meshes = new ArrayList<>();

        absolutePnt = null;
        limbRadius = limbRadius;
    }

}
