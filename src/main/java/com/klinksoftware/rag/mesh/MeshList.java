package com.klinksoftware.rag.mesh;

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
    
}
