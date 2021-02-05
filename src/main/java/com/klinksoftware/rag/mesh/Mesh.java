package com.klinksoftware.rag.mesh;

import java.util.*;

public class Mesh
{
    public String       name,bitmapName;
    public int[]        vertexes,indexes;
    public float[]      normals,tangents,uvs;

    public Mesh(String name,String bitmapName,int[] vertexes,float[] normals,float[] tangents,float[] uvs,int[] indexes)
    {
        this.name=name;
        this.bitmapName=bitmapName;
        this.vertexes=vertexes;
        this.normals=normals;
        this.tangents=tangents;
        this.uvs=uvs;
        this.indexes=indexes;
    }
}
