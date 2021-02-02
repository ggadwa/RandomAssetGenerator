package com.klinksoftware.rag.mesh;

import java.util.*;

public class MeshList
{
    private ArrayList<Mesh>         meshes;
    
    public MeshList()
    {
        meshes=new ArrayList<>();
    }
    
    public void addMesh(Mesh mesh)
    {
        meshes.add(mesh);
    }
    
    public Mesh getMesh(int index)
    {
        return(meshes.get(index));
    }
}
