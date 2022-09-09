package com.klinksoftware.rag.scene;

import com.klinksoftware.rag.bitmaps.BitmapBase;
import com.klinksoftware.rag.utility.MeshUtility;
import com.klinksoftware.rag.utility.RagPoint;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

// this is our internal representation of a gltf file
// root node is created automatically
public class Scene {

    private int nodeIndex, meshIndex;

    public boolean skyBox, skinned;
    public Node rootNode;
    public HashMap<String, BitmapBase> bitmaps;
    public Animation animation;

    public Scene() {
        rootNode = new Node("root", new RagPoint(0.0f, 0.0f, 0.0f));
        bitmaps = new HashMap<>();
        animation = new Animation(this);

        skyBox = false;
        skinned = false;
    }

    // convience routine to get all nodes from the tree
    private void getAllNodesRecursive(Node node, ArrayList<Node> nodes) {
        nodes.add(node);

        for (Node childNode : node.childNodes) {
            getAllNodesRecursive(childNode, nodes);
        }
    }

    private ArrayList<Node> getAllNodes() {
        ArrayList<Node> nodes = new ArrayList<>();

        getAllNodesRecursive(rootNode, nodes);
        return (nodes);
    }

    // convience routine to get all meshes from the tree
    private void getAllMeshesRecursive(Node node, ArrayList<Mesh> meshes) {
        meshes.addAll(node.meshes);

        for (Node childNode : node.childNodes) {
            getAllMeshesRecursive(childNode, meshes);
        }
    }

    private ArrayList<Mesh> getAllMeshes() {
        ArrayList<Mesh> meshes = new ArrayList<>();

        getAllMeshesRecursive(rootNode, meshes);
        return (meshes);
    }

    // gets the min max of all vertexes in the scene
    public void getMixMaxVertex(RagPoint minPnt, RagPoint maxPnt) {
        RagPoint meshMin, meshMax;

        meshMin = new RagPoint(0.0f, 0.0f, 0.0f);
        meshMax = new RagPoint(0.0f, 0.0f, 0.0f);

        for (Mesh mesh : getAllMeshes()) {
            mesh.getMinMaxVertex(meshMin, meshMax);
            minPnt.setIfMin(meshMin);
            maxPnt.setIfMax(meshMax);
        }
    }

    // vertex randomizers
    public void randomizeVertexes(float percentMove, float moveFactor) {
        ArrayList<Mesh> meshes = getAllMeshes();

        for (Mesh mesh : meshes) {
            mesh.randomizeVertexes(percentMove, moveFactor, meshes);
        }
    }

    public void randomizeWallVertexesFromCenter(float percentMove, float moveFactor, RagPoint centerPnt) {
        ArrayList<Mesh> meshes = getAllMeshes();

        for (Mesh mesh : meshes) {
            mesh.randomizeWallVertexesFromCenter(percentMove, moveFactor, centerPnt, meshes);
        }
    }

    public void randomizeFloorVertexes(float percentMove, float moveFactor) {
        ArrayList<Mesh> meshes = getAllMeshes();

        for (Mesh mesh : meshes) {
            mesh.randomizeFloorVertexes(percentMove, moveFactor, meshes);
        }
    }

    // some scenes are built with absolute vertexes and need to be
    // converted to relative (to the node) vertexes
    public void shiftAbsoluteMeshesToNodeRelativeMeshesRecursive(Node node, RagPoint pnt) {
        RagPoint nextPnt;

        nextPnt = pnt.copy();
        nextPnt.addPoint(node.pnt);

        for (Mesh mesh : node.meshes) {
            mesh.makeVertexesRelativeToPoint(nextPnt);
        }

        for (Node childNode : node.childNodes) {
            shiftAbsoluteMeshesToNodeRelativeMeshesRecursive(childNode, nextPnt);
        }
    }

    public void shiftAbsoluteMeshesToNodeRelativeMeshes() {
        shiftAbsoluteMeshesToNodeRelativeMeshesRecursive(rootNode, new RagPoint(0.0f, 0.0f, 0.0f));
    }

    // setup all the gl buffers for meshes in the node tree
    public void setupGLBuffersForAllMeshesRecursive(Node node) {
        for (Mesh mesh : node.meshes) {
            mesh.setupGLBuffers(node);
        }

        for (Node childNode : node.childNodes) {
            setupGLBuffersForAllMeshesRecursive(childNode);
        }
    }

    public void setupGLBuffersForAllMeshes() {
        setupGLBuffersForAllMeshesRecursive(rootNode);
    }

    // release all the gl buffers for meshes in the node tree
    public void releaseGLBuffersForAllMeshes() {
        for (Mesh mesh : getAllMeshes()) {
            mesh.releaseGLBuffers();
        }
    }

    // creates a scene that is a simple cube
    public void makeSceneSimpleCube(BitmapBase bitmap) {
        int idx;
        ArrayList<Float> vertexArray, uvArray;
        ArrayList<Integer> indexArray;
        int[] indexes;
        float[] vertexes, normals, tangents, uvs;

        // the single bitmap
        bitmaps.put("bitmap", bitmap);

        // allocate proper buffers
        vertexArray = new ArrayList<>();
        uvArray = new ArrayList<>();
        indexArray = new ArrayList<>();

        // box parts
        idx = 0;

        // left
        vertexArray.addAll(Arrays.asList(-1.0f, 1.0f, -1.0f));
        vertexArray.addAll(Arrays.asList(-1.0f, -1.0f, -1.0f));
        vertexArray.addAll(Arrays.asList(-1.0f, -1.0f, 1.0f));
        vertexArray.addAll(Arrays.asList(-1.0f, 1.0f, 1.0f));
        uvArray.addAll(Arrays.asList(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f));
        indexArray.addAll(Arrays.asList(idx, (idx + 1), (idx + 2), idx, (idx + 2), (idx + 3)));
        idx += 4;

        // right
        vertexArray.addAll(Arrays.asList(1.0f, 1.0f, -1.0f));
        vertexArray.addAll(Arrays.asList(1.0f, -1.0f, -1.0f));
        vertexArray.addAll(Arrays.asList(1.0f, -1.0f, 1.0f));
        vertexArray.addAll(Arrays.asList(1.0f, 1.0f, 1.0f));
        uvArray.addAll(Arrays.asList(1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f));
        indexArray.addAll(Arrays.asList(idx, (idx + 1), (idx + 2), idx, (idx + 2), (idx + 3)));
        idx += 4;

        // front
        vertexArray.addAll(Arrays.asList(-1.0f, 1.0f, -1.0f));
        vertexArray.addAll(Arrays.asList(-1.0f, -1.0f, -1.0f));
        vertexArray.addAll(Arrays.asList(1.0f, -1.0f, -1.0f));
        vertexArray.addAll(Arrays.asList(1.0f, 1.0f, -1.0f));
        uvArray.addAll(Arrays.asList(1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f));
        indexArray.addAll(Arrays.asList(idx, (idx + 1), (idx + 2), idx, (idx + 2), (idx + 3)));
        idx += 4;

        // back
        vertexArray.addAll(Arrays.asList(-1.0f, 1.0f, 1.0f));
        vertexArray.addAll(Arrays.asList(-1.0f, -1.0f, 1.0f));
        vertexArray.addAll(Arrays.asList(1.0f, -1.0f, 1.0f));
        vertexArray.addAll(Arrays.asList(1.0f, 1.0f, 1.0f));
        uvArray.addAll(Arrays.asList(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f));
        indexArray.addAll(Arrays.asList(idx, (idx + 1), (idx + 2), idx, (idx + 2), (idx + 3)));
        idx += 4;

        // top
        vertexArray.addAll(Arrays.asList(-1.0f, 1.0f, 1.0f));
        vertexArray.addAll(Arrays.asList(-1.0f, 1.0f, -1.0f));
        vertexArray.addAll(Arrays.asList(1.0f, 1.0f, -1.0f));
        vertexArray.addAll(Arrays.asList(1.0f, 1.0f, 1.0f));
        uvArray.addAll(Arrays.asList(0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f));
        indexArray.addAll(Arrays.asList(idx, (idx + 1), (idx + 2), idx, (idx + 2), (idx + 3)));
        idx += 4;

        // bottom
        vertexArray.addAll(Arrays.asList(-1.0f, -1.0f, 1.0f));
        vertexArray.addAll(Arrays.asList(-1.0f, -1.0f, -1.0f));
        vertexArray.addAll(Arrays.asList(1.0f, -1.0f, -1.0f));
        vertexArray.addAll(Arrays.asList(1.0f, -1.0f, 1.0f));
        uvArray.addAll(Arrays.asList(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f));
        indexArray.addAll(Arrays.asList(idx, (idx + 1), (idx + 2), idx, (idx + 2), (idx + 3)));

        // vertexes and indexes to arrays
        vertexes = MeshUtility.floatArrayListToFloat(vertexArray);
        indexes = MeshUtility.intArrayListToInt(indexArray);

        // create the mesh
        normals = MeshUtility.buildNormals(vertexes, indexes, new RagPoint(0.0f, 0.0f, 0.0f), false);
        uvs = MeshUtility.floatArrayListToFloat(uvArray);
        tangents = MeshUtility.buildTangents(vertexes, uvs, indexes);

        rootNode.meshes.clear();
        rootNode.meshes.add(new Mesh("cube", "bitmap", vertexes, normals, tangents, uvs, indexes));
    }

    // create joints and weights for each vertexes
    // we need to create a flat list of nodes here so we can reach them
    // by index, which is what is in the joints x/y/z/w floats
    public void createJointsAndWeights() {
        //    for (Mesh mesh : meshes) {
        //        mesh.createJointsAndWeights(skeleton);
        //    }
    }

    // traverse the nodes to build an absolute position for
    // each node, normally used to make default animation joint matrixes
    private void createNodesAbsolutePositionRecursive(Node node, RagPoint pnt) {
        RagPoint nextPnt;

        nextPnt = pnt.copy();
        nextPnt.addPoint(node.pnt);

        node.absolutePnt = nextPnt.copy();

        for (Node childNode : node.childNodes) {
            createNodesAbsolutePositionRecursive(childNode, nextPnt);
        }
    }

    public void createNodesAbsolutePosition() {
        RagPoint pnt;

        pnt = new RagPoint(0.0f, 0.0f, 0.0f);

        createNodesAbsolutePositionRecursive(rootNode, pnt);
    }


    // traverse the tree to give every node and every mesh a index
    // gltf's refer to nodes and meshes this way
    private void createNodeAndMeshIndexesRecursive(Node node) {
        node.index = nodeIndex++;

        for (Mesh mesh : node.meshes) {
            mesh.index = meshIndex++;
        }

        for (Node childNode : node.childNodes) {
            createNodeAndMeshIndexesRecursive(childNode);
        }
    }

    public void createNodeAndMeshIndexes() {
        nodeIndex = 0;
        meshIndex = 0;

        createNodeAndMeshIndexesRecursive(rootNode);
    }

    public int getNodeCount() {
        return (getAllNodes().size());
    }

    public Node getNodeForIndex(int index) {
        for (Node node : getAllNodes()) {
            if (node.index == index) {
                return (node);
            }
        }

        throw new RuntimeException("Something is wrong with node list");
    }

    public int getMeshCount() {
        return (getAllMeshes().size());
    }

    public Mesh getMeshForIndex(int index) {
        for (Mesh mesh : getAllMeshes()) {
            if (mesh.index == index) {
                return (mesh);
            }
        }

        throw new RuntimeException("Something is wrong with mesh list");
    }

}
