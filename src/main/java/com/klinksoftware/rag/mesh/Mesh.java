package com.klinksoftware.rag.mesh;

import java.util.*;

public class Mesh
{
    public String               name,bitmapName;
    public ArrayList<Integer>   vertexArray,indexArray;
    public ArrayList<Float>     normalArray,tangentArray,uvArray;

    public Mesh(String name,String bitmapName,ArrayList<Integer> vertexArray,ArrayList<Float> normalArray,ArrayList<Float> tangentArray,ArrayList<Float> uvArray,ArrayList<Integer> indexArray)
    {
        this.name=name;
        this.bitmapName=bitmapName;
        this.vertexArray=vertexArray;
        this.normalArray=normalArray;
        this.tangentArray=tangentArray;
        this.uvArray=uvArray;
        this.indexArray=indexArray;
    }
}
