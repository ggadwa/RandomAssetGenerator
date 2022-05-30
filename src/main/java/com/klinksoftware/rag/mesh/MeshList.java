package com.klinksoftware.rag.mesh;

import com.klinksoftware.rag.skeleton.*;
import com.klinksoftware.rag.utility.*;

import java.util.*;

public class MeshList
{
    private ArrayList<Mesh>         meshes;

    public MeshList()
    {
        meshes=new ArrayList<>();
    }

    public int add(Mesh mesh)
    {
        meshes.add(mesh);
        return(meshes.size()-1);
    }

    public Mesh get(int index)
    {
        return(meshes.get(index));
    }

    public int count()
    {
        return(meshes.size());
    }

    public Skeleton rebuildMapMeshesWithSkeleton()
    {
        int             n;
        Mesh            mesh;
        Skeleton        skeleton;
        RagPoint        center;

            // this is used to turn boneless maps
            // into a simple skeleton with a single
            // root node

        skeleton=new Skeleton();        // node 0 will be root

        center=new RagPoint(0.0f,0.0f,0.0f);

        for (n=0;n!=meshes.size();n++) {
            mesh=meshes.get(n);

            mesh.getCenterPoint(center);
            skeleton.addChildBone(0,mesh.name,n,1.0f,center);
            mesh.makeVertexesRelativeToPoint(center);
        }

        return(skeleton);
    }

    private void rebuildModelMeshWithSkeletonRecurse(Skeleton skeleton,Bone bone,RagPoint offsetPnt)
    {
        int         n;
        RagPoint    nextOffsetPnt;

            // everything is absolute now so just
            // move the mesh to where the bone will be relative

        if (bone.meshIdx!=-1) meshes.get(bone.meshIdx).makeVertexesRelativeToPoint(bone.pnt);

            // now move the bone and recurse

        nextOffsetPnt=new RagPoint(bone.pnt.x,bone.pnt.y,bone.pnt.z);

        if (offsetPnt!=null) {
            bone.pnt.x-=offsetPnt.x;
            bone.pnt.y-=offsetPnt.y;
            bone.pnt.z-=offsetPnt.z;
        }

        for (n=0;n!=bone.children.size();n++) {
            rebuildModelMeshWithSkeletonRecurse(skeleton,skeleton.bones.get(bone.children.get(n)),nextOffsetPnt);
        }
    }

    public void rebuildModelMeshWithSkeleton(Skeleton skeleton)
    {
            // when constructing the bones are
            // all absolute, this makes everything relative

        rebuildModelMeshWithSkeletonRecurse(skeleton,skeleton.bones.get(0),null);
    }

    public void makeListSimpleCube(String bitmapName) {
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
        vertexes = MeshMapUtility.floatArrayListToFloat(vertexArray);
        indexes = MeshMapUtility.intArrayListToInt(indexArray);

        // create the mesh
        normals = MeshMapUtility.buildNormals(vertexes, indexes, new RagPoint(0.0f, 0.0f, 0.0f), false);
        uvs = MeshMapUtility.floatArrayListToFloat(uvArray);
        tangents = MeshMapUtility.buildTangents(vertexes, uvs, indexes);

        meshes.clear();
        meshes.add(new Mesh("cube", bitmapName, vertexes, normals, tangents, uvs, indexes));
    }

    public void randomizeVertexes(float percentMove, float moveFactor) {
        for (Mesh mesh : meshes) {
            mesh.randomizeVertexes(percentMove, moveFactor, meshes);
        }
    }

}
