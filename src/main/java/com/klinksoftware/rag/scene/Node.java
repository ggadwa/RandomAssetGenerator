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
    public Node parentNode;
    public RagPoint pnt;
    public ArrayList<Node> childNodes;
    public ArrayList<Mesh> meshes;

    public Node(String name, RagPoint pnt) {
        this.name = name;
        this.pnt = pnt.copy();

        parentNode = null;
        childNodes = new ArrayList<>();
        meshes = new ArrayList<>();

        limbRadius = 0.0f; // for limb building in models
    }

    // this version is for the skeleton builder, which attaches
    // a radius for limb building
    public Node(String name, float limbRadius, RagPoint pnt) {
        this.name = name;
        this.pnt = pnt.copy();

        childNodes = new ArrayList<>();
        meshes = new ArrayList<>();

        this.limbRadius = limbRadius;
    }

    public void addChild(Node node) {
        node.parentNode = this;
        childNodes.add(node);
    }

    public void clearMeshes() {
        meshes.clear();
    }

    public void addMesh(Mesh mesh) {
        meshes.add(mesh);
    }

    public RagPoint getAbsolutePoint() {
        RagPoint absPnt;
        Node node;

        absPnt = new RagPoint(0.0f, 0.0f, 0.0f);
        node = this;

        while (true) {
            absPnt.addPoint(node.pnt);
            node = node.parentNode;
            if (node == null) {
                return (absPnt);
            }
        }
    }

}
