package com.klinksoftware.rag.scene;

import com.klinksoftware.rag.utility.MeshUtility;
import com.klinksoftware.rag.utility.RagPoint;
import java.util.ArrayList;
import java.util.Arrays;

// this is our internal representation of a gltf file
// root node is created automatically
public class Scene {

    private int nodeIndex;

    public boolean skyBox, skinned;
    public Node rootNode;
    public BitmapGroup bitmapGroup;
    public Animation animation;

    public Scene() {
        nodeIndex = 0;

        rootNode = new Node("root", nodeIndex++, new RagPoint(0.0f, 0.0f, 0.0f));
        bitmapGroup = new BitmapGroup();
        animation = new Animation(this);

        skyBox = false;
        skinned = false;
    }

    // add a node
    public Node addChildNode(Node parentNode, String name, RagPoint pnt) {
        Node node;

        node = new Node(name, nodeIndex++, pnt);
        parentNode.addChild(node);

        return (node);
    }

    public Node addChildNodeAndJoint(Node parentNode, String name, RagPoint pnt) {
        Node node;

        node = addChildNode(parentNode, name, pnt);
        animation.joints.add(new Joint(node));
        return (node);
    }

    // convience routine to get all nodes from the tree
    private void getAllNodesRecursive(Node node, ArrayList<Node> nodes) {
        nodes.add(node);

        for (Node childNode : node.childNodes) {
            getAllNodesRecursive(childNode, nodes);
        }
    }

    public ArrayList<Node> getAllNodes() {
        ArrayList<Node> nodes = new ArrayList<>();

        getAllNodesRecursive(rootNode, nodes);
        return (nodes);
    }

    public Node findNodeByName(String name) {
        for (Node node : getAllNodes()) {
            if (node.name.equalsIgnoreCase(name)) {
                return (node);
            }
        }
        return (null);
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

    // gets the min max of all absolute absolute meshes in the scene
    // this should only be called when the meshes are absolute
    public void getAbsoluteMixMaxVertexForAbsoluteVertexes(RagPoint minPnt, RagPoint maxPnt) {
        RagPoint meshMin, meshMax;

        meshMin = new RagPoint(0.0f, 0.0f, 0.0f);
        meshMax = new RagPoint(0.0f, 0.0f, 0.0f);

        for (Mesh mesh : getAllMeshes()) {
            mesh.getMinMaxVertex(meshMin, meshMax);
            minPnt.setIfMin(meshMin);
            maxPnt.setIfMax(meshMax);
        }
    }

    // gets the min max of all relative meshes in the scene
    // this should only be called when meshes have been made relative
    public void getAbsoluteMixMaxVertexForRelativeVertexes(RagPoint minPnt, RagPoint maxPnt) {
        RagPoint meshMin, meshMax, absPnt;

        meshMin = new RagPoint(0.0f, 0.0f, 0.0f);
        meshMax = new RagPoint(0.0f, 0.0f, 0.0f);

        for (Node node : getAllNodes()) {
            absPnt = node.getAbsolutePoint();

            for (Mesh mesh : node.meshes) {
                mesh.getMinMaxVertex(meshMin, meshMax);
                meshMin.addPoint(absPnt);
                meshMax.addPoint(absPnt);

                minPnt.setIfMin(meshMin);
                maxPnt.setIfMax(meshMax);
            }
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

    // we use this to shift the absolute vertexes to be relative to the
    // node the mesh is attached to.  We use this for maps
    public void shiftAbsoluteMeshesToNodeRelativeMeshes() {
        shiftAbsoluteMeshesToNodeRelativeMeshesRecursive(rootNode, new RagPoint(0.0f, 0.0f, 0.0f));
    }

    // setup all the gl buffers for meshes in the node tree
    public void setupGLBuffersForAllMeshesRecursive(Node node) {
        for (Mesh mesh : node.meshes) {
            mesh.setupGLBuffers(node, skinned);
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
    public void makeSceneSimpleCube() {
        int idx;
        ArrayList<Float> vertexArray, uvArray;
        ArrayList<Integer> indexArray;
        int[] indexes;
        float[] vertexes, normals, tangents, uvs;

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

        // single bitmap needs to be named "bitmap"
        rootNode.clearMeshes();
        rootNode.addMesh(new Mesh("cube", "bitmap", vertexes, normals, tangents, uvs, indexes));
    }

    // find nearest node, only works on items where
    // the vertexes are absolute and the absolute node
    // points have been calculated
    public Node findNearestNode(RagPoint pnt) {
        float dist, currentDist;
        Node currentNode;

        currentNode = null;
        currentDist = 0.0f;

        for (Node node : getAllNodes()) {
            // always ignore root node
            if (node == rootNode) {
                continue;
            }

            // get nearest node
            dist = node.getAbsolutePoint().distance(pnt);
            if (currentNode == null) {
                currentDist = dist;
                currentNode = node;
            } else {
                if (dist < currentDist) {
                    currentDist = dist;
                    currentNode = node;
                }
            }
        }

        return ((currentNode == null) ? rootNode : currentNode);
    }

    public void createMeshIndexes() {
        int meshIndex = 0;

        for (Mesh mesh : getAllMeshes()) {
            mesh.index = meshIndex++;
        }
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

    // setup the node matrixes for drawing, in regular drawing
    // the node matrix is just the absolute point for the node,
    // but when skinned, since the vertexes for skin is a single
    // unit with absolute vertexes, we just use the identity
    public void setupNodeModelMatrixes() {
        for (Node node : getAllNodes()) {
            if (!skinned) {
                node.modelMatrix.setTranslationFromPoint(node.getAbsolutePoint());
            } else {
                node.modelMatrix.setIdentity();
            }
        }
    }

}
