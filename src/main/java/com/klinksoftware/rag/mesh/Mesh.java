package com.klinksoftware.rag.mesh;

import com.klinksoftware.rag.utility.*;
import java.nio.*;

public class Mesh
{
    public boolean hasEmissive;
    public String name,bitmapName;
    public int[] indexes;
    public float[] vertexes,normals,uvs;
    
    public int vboVertexId,vboNormalId,vboTangentId,vboUVId;
    public IntBuffer indexBuf;

    public Mesh(String name,String bitmapName,float[] vertexes,float[] normals,float[] uvs,int[] indexes)
    {
        this.name=name;
        this.bitmapName=bitmapName;
        this.vertexes=vertexes;
        this.normals=normals;
        this.uvs=uvs;
        this.indexes=indexes;
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
    
    public void getCenterPoint(RagPoint center)
    {
        int         n;
        float       f;
        
        center.x=0;
        center.y=0;
        center.z=0;
        
        for (n=0;n<vertexes.length;n+=3) {
            center.x+=vertexes[n];
            center.y+=vertexes[n+1];
            center.z+=vertexes[n+2];
        }
        
        f=((float)vertexes.length)/3.0f;
        
        center.x=center.x/f;
        center.y=center.y/f;
        center.z=center.z/f;
    }
    
    public void makeVertexesRelativeToPoint(RagPoint pnt)
    {
        int         n;
        
        for (n=0;n<vertexes.length;n+=3) {
            vertexes[n]-=pnt.x;
            vertexes[n+1]-=pnt.y;
            vertexes[n+2]-=pnt.z;
        }
    }
    
    public void transformUVs(float uAdd,float vAdd,float uReduce,float vReduce)
    {
        int         n;
        
        for (n=0;n<uvs.length;n+=2) {
            uvs[n]=(uvs[n]*uReduce)+uAdd;
            uvs[n+1]=(uvs[n+1]*uReduce)+uAdd;
        }
    }
    
    public void combine(Mesh mesh)
    {
        int         n,idx,indexOffset;
        int[]       indexes2;
        float[]     vertexes2,normals2,uvs2;
        
            // combine the arrays
        
        vertexes2=new float[vertexes.length+mesh.vertexes.length];
        System.arraycopy(vertexes,0,vertexes2,0,vertexes.length);
        System.arraycopy(mesh.vertexes,0,vertexes2,vertexes.length,mesh.vertexes.length);
        
        normals2=new float[normals.length+mesh.normals.length];
        System.arraycopy(normals,0,normals2,0,normals.length);
        System.arraycopy(mesh.normals,0,normals2,normals.length,mesh.normals.length);

        uvs2=new float[uvs.length+mesh.uvs.length];
        System.arraycopy(uvs,0,uvs2,0,uvs.length);
        System.arraycopy(mesh.uvs,0,uvs2,uvs.length,mesh.uvs.length);

            // new indexes need to be offset from
            // this meshes vertexes
            
        indexes2=new int[indexes.length+mesh.indexes.length];
        System.arraycopy(indexes,0,indexes2,0,indexes.length);
        
        idx=indexes.length;
        indexOffset=vertexes.length/3;
        
        for (n=0;n!=mesh.indexes.length;n++) {
            indexes2[idx++]=mesh.indexes[n]+indexOffset;
        }
        
            // and move over new arrays
            
        vertexes=vertexes2;
        normals=normals2;
        uvs=uvs2;
        indexes=indexes2;
    }
}
