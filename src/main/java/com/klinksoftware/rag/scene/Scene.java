package com.klinksoftware.rag.scene;

import com.klinksoftware.rag.bitmap.utility.BitmapBase;
import com.klinksoftware.rag.utility.MeshUtility;
import com.klinksoftware.rag.utility.RagPoint;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import org.lwjgl.system.MemoryUtil;
import static org.lwjgl.system.MemoryUtil.memFree;

// this is our internal representation of a gltf file
// root node is created automatically
public class Scene {

    private int nodeIndex, meshIndex;

    public int vboVertexId;
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

    // we use this to shift the absolute vertexes to stay absolute but
    // be attached only to the root node.  We do this so skinned animations can
    // work with all nodes
    public void attachAllAbsoluteMeshesToRootNode() {
        ArrayList<Mesh> meshes;

        // get this first otherwise it gets cleared
        meshes = getAllMeshes();

        // clear all nodes first
        for (Node node : getAllNodes()) {
            node.clearMeshes();
        }

        // now attach all meshes to root node
        for (Mesh mesh : meshes) {
            rootNode.addMesh(mesh);
        }
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

    // create joints and weights for each vertexes
    public void createJointsAndWeights() {
        for (Mesh mesh : getAllMeshes()) {
            mesh.createJointsAndWeights(this);
        }
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

    // gl buffers for drawing skeletons
    public int setupGLBuffersForSkeletonDrawing() {
        int nodeCount, lineCount;
        RagPoint absPnt, absPnt2;
        FloatBuffer vertexBuf;
        ArrayList<Node> nodes;

        nodes = getAllNodes();
        nodeCount = nodes.size();

        // count the # of lines we will need
        lineCount = 0;

        for (Node node : nodes) {
            lineCount += node.childNodes.size();
        }

        // memory for vertexes
        vertexBuf = MemoryUtil.memAllocFloat(((lineCount * 2) + nodeCount) * 3);

        // vertexes for lines
        for (Node node : nodes) {
            absPnt = node.getAbsolutePoint();

            for (Node node2 : node.childNodes) {
                vertexBuf.put(absPnt.x).put(absPnt.y).put(absPnt.z);

                absPnt2 = node2.getAbsolutePoint();
                vertexBuf.put(absPnt2.x).put(absPnt2.y).put(absPnt2.z);
            }
        }

        // vertexes for bones
        for (Node node : nodes) {
            absPnt = node.getAbsolutePoint();
            vertexBuf.put(absPnt.x).put(absPnt.y).put(absPnt.z);
        }

        // put it in gl buffer
        vertexBuf.flip();

        vboVertexId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboVertexId);
        glBufferData(GL_ARRAY_BUFFER, vertexBuf, GL_STATIC_DRAW);
        memFree(vertexBuf);

        return (lineCount);
    }

    // release buffers for opengl drawing
    public void releaseGLBuffersForSkeletonDrawing() {
        glDeleteBuffers(vboVertexId);
    }
}
