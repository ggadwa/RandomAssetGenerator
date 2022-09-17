package com.klinksoftware.rag.scene;

import com.klinksoftware.rag.utility.RagPoint;
import java.util.ArrayList;

// this is a node in the scene, equivalent to what you see in
// a gltf file, has list of child nodes and list of child meshes,
// the meshes become primitives in the gltf
public class Node {
    public int index;
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
