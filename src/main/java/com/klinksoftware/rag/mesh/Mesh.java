package com.klinksoftware.rag.mesh;

import com.klinksoftware.rag.utility.*;

import java.util.*;

public class Mesh
{
    public boolean      hasEmissive;
    public String       name,bitmapName;
    public int[]        indexes;
    public float[]      vertexes,normals,uvs;

    public Mesh(String name,String bitmapName,float[] vertexes,float[] normals,float[] uvs,int[] indexes,boolean hasEmissive)
    {
        this.name=name;
        this.bitmapName=bitmapName;
        this.vertexes=vertexes;
        this.normals=normals;
        this.uvs=uvs;
        this.indexes=indexes;
        this.hasEmissive=hasEmissive;
    }
    
    public void getMinMaxVertex(RagPoint min,RagPoint max)
    {
        int         n;
        
        min.x=max.x=vertexes[0];
        min.y=max.y=vertexes[1];
        min.z=max.z=vertexes[2];
        
        for (n=0;n<vertexes.length;n+=3) {
            if (vertexes[n]<min.x) min.x=vertexes[n];
            if (vertexes[n]>max.x) max.x=vertexes[n];
            if (vertexes[n+1]<min.y) min.y=vertexes[n+1];
            if (vertexes[n+1]>max.y) max.y=vertexes[n+1];
            if (vertexes[n+2]<min.z) min.z=vertexes[n+2];
            if (vertexes[n+2]>max.z) max.z=vertexes[n+2];
        }
    }
}
