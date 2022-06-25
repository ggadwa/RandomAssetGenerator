package com.klinksoftware.rag.mesh;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.utility.*;
import java.nio.*;
import java.util.ArrayList;

public class Mesh
{
    public boolean hasEmissive;
    public String name,bitmapName;
    public int[] indexes;
    public float[] vertexes, normals, tangents, uvs;
    public int vboVertexId,vboNormalId,vboTangentId,vboUVId;
    public IntBuffer indexBuf;
    public RagBound xBound, yBound, zBound;
    public boolean hasAlpha;

    public Mesh(String name,String bitmapName,float[] vertexes,float[] normals,float[] tangents,float[] uvs,int[] indexes)
    {
        this.name=name;
        this.bitmapName=bitmapName;
        this.vertexes=vertexes;
        this.normals=normals;
        this.tangents=tangents;
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

    public void setGlobalBounds(RagPoint offsetPnt) {
        int n;

        xBound = new RagBound((vertexes[0] + offsetPnt.x), (vertexes[0] + offsetPnt.x));
        yBound = new RagBound((vertexes[1] + offsetPnt.y), (vertexes[1] + offsetPnt.y));
        zBound = new RagBound((vertexes[2] + offsetPnt.z), (vertexes[2] + offsetPnt.z));

        for (n = 0; n < vertexes.length; n += 3) {
            xBound.adjust(vertexes[n] + offsetPnt.x);
            yBound.adjust(vertexes[n + 1] + offsetPnt.y);
            zBound.adjust(vertexes[n + 2] + offsetPnt.z);
        }
    }

    public void makeVertexesRelativeToPoint(RagPoint pnt) {
        int n;

        for (n=0;n<vertexes.length;n+=3) {
            vertexes[n]-=pnt.x;
            vertexes[n+1]-=pnt.y;
            vertexes[n+2]-=pnt.z;
        }
    }

    public void transformUVs(float uAdd, float vAdd, float uReduce, float vReduce) {
        int n;

        for (n=0;n<uvs.length;n+=2) {
            uvs[n] = (uvs[n] * uReduce) + uAdd;
            uvs[n + 1] = (uvs[n + 1] * vReduce) + vAdd;
        }
    }

    public void combine(Mesh mesh) {
        int n, idx, indexOffset;
        int[] indexes2;
        float[] vertexes2, uvs2, normals2, tangents2;

        // combine the arrays
        vertexes2=new float[vertexes.length+mesh.vertexes.length];
        System.arraycopy(vertexes,0,vertexes2,0,vertexes.length);
        System.arraycopy(mesh.vertexes, 0, vertexes2, vertexes.length, mesh.vertexes.length);

        uvs2 = new float[uvs.length + mesh.uvs.length];
        System.arraycopy(uvs, 0, uvs2, 0, uvs.length);
        System.arraycopy(mesh.uvs, 0, uvs2, uvs.length, mesh.uvs.length);

        normals2=new float[normals.length+mesh.normals.length];
        System.arraycopy(normals,0,normals2,0,normals.length);
        System.arraycopy(mesh.normals,0,normals2,normals.length,mesh.normals.length);

        tangents2 = new float[tangents.length + mesh.tangents.length];
        System.arraycopy(tangents, 0, tangents2, 0, tangents.length);
        System.arraycopy(mesh.tangents, 0, tangents2, tangents.length, mesh.tangents.length);

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
        vertexes = vertexes2;
        uvs = uvs2;
        normals = normals2;
        tangents = tangents2;
        indexes=indexes2;
    }

    public void clipFloorVertexes() {
        int n, vIdx, nVertex;

        vIdx = 0;
        nVertex = vertexes.length / 3;

        for (n = 0; n != nVertex; n++) {
            if (vertexes[vIdx + 1] < 0.0f) {
                vertexes[vIdx + 1] = 0.0f;
            }
            vIdx += 3;
        }
    }

    public void randomizeVertexes(float percentMove, float moveFactor, ArrayList<Mesh> meshes) {
        int n, k, vIdx, nVertex, nCheckVertex;
        RagPoint normal, vertex, origVertex, checkVertex;

        normal = new RagPoint(0.0f, 0.0f, 0.0f);
        vertex = new RagPoint(0.0f, 0.0f, 0.0f);
        origVertex = new RagPoint(0.0f, 0.0f, 0.0f);
        checkVertex = new RagPoint(0.0f, 0.0f, 0.0f);

        nVertex = vertexes.length / 3;

        for (n = 0; n != nVertex; n++) {
            if (AppWindow.random.nextFloat() > percentMove) {
                continue;
            }

            vIdx = n * 3;

            // remember the original to compare
            origVertex.setFromValues(vertexes[vIdx], vertexes[vIdx + 1], vertexes[vIdx + 2]);

            // randomly move down normal
            normal.setFromValues(normals[vIdx], normals[vIdx + 1], normals[vIdx + 2]);
            normal.scale(AppWindow.random.nextFloat(moveFactor * 2.0f) - moveFactor);

            vertex.setFromPoint(origVertex);
            vertex.addPoint(normal);
            vertexes[vIdx] = vertex.x;
            vertexes[vIdx + 1] = vertex.y;
            vertexes[vIdx + 2] = vertex.z;

            // check for equal vertexes to move
            for (Mesh mesh : meshes) {
                nCheckVertex = mesh.vertexes.length / 3;

                vIdx = 0;

                for (k = 0; k != nCheckVertex; k++) {
                    if ((mesh == this) && (k == n)) {
                        continue;
                    }
                    checkVertex.setFromValues(mesh.vertexes[vIdx], mesh.vertexes[vIdx + 1], mesh.vertexes[vIdx + 2]);
                    if (checkVertex.isCloseEqual(origVertex)) {
                        mesh.vertexes[vIdx] = vertex.x;
                        mesh.vertexes[vIdx + 1] = vertex.y;
                        mesh.vertexes[vIdx + 2] = vertex.z;
                    }
                    vIdx += 3;
                }
            }
        }
    }

    public void randomizeWallVertexesFromCenter(float percentMove, float moveFactor, RagPoint centerPnt, ArrayList<Mesh> meshes) {
        int n, k, vIdx, nVertex, nCheckVertex;
        RagPoint normal, vertex, origVertex, checkVertex;

        normal = new RagPoint(0.0f, 0.0f, 0.0f);
        vertex = new RagPoint(0.0f, 0.0f, 0.0f);
        origVertex = new RagPoint(0.0f, 0.0f, 0.0f);
        checkVertex = new RagPoint(0.0f, 0.0f, 0.0f);

        nVertex = vertexes.length / 3;

        for (n = 0; n != nVertex; n++) {
            if (AppWindow.random.nextFloat() > percentMove) {
                continue;
            }

            vIdx = n * 3;

            // if normals face up, then it's not a wall, skip it
            if (normals[vIdx + 1] != 0.0f) {
                continue;
            }

            // remember the original to compare
            origVertex.setFromValues(vertexes[vIdx], vertexes[vIdx + 1], vertexes[vIdx + 2]);

            // move away from center
            normal.setFromSubPoint(origVertex, centerPnt);
            normal.normalize();
            normal.scale(AppWindow.random.nextFloat(moveFactor));   // always move outwards

            vertex.setFromPoint(origVertex);
            vertex.addPoint(normal);
            vertexes[vIdx] = vertex.x;
            vertexes[vIdx + 1] = vertex.y;
            vertexes[vIdx + 2] = vertex.z;

            // check for equal vertexes to move, we ignore the Y so we
            // catch all parts of the wall segment at once
            for (Mesh mesh : meshes) {
                nCheckVertex = mesh.vertexes.length / 3;

                vIdx = 0;

                for (k = 0; k != nCheckVertex; k++) {
                    if ((mesh == this) && (k == n)) {
                        continue;
                    }
                    checkVertex.setFromValues(mesh.vertexes[vIdx], mesh.vertexes[vIdx + 1], mesh.vertexes[vIdx + 2]);
                    if (checkVertex.isCloseEqualIgnoreY(origVertex)) {
                        mesh.vertexes[vIdx] = vertex.x;
                        mesh.vertexes[vIdx + 2] = vertex.z;
                    }
                    vIdx += 3;
                }
            }
        }
    }

    public void randomizeFloorVertexes(float percentMove, float moveFactor, ArrayList<Mesh> meshes) {
        int n, k, vIdx, nVertex, nCheckVertex;
        float y;
        RagPoint origVertex, checkVertex;

        origVertex = new RagPoint(0.0f, 0.0f, 0.0f);
        checkVertex = new RagPoint(0.0f, 0.0f, 0.0f);

        nVertex = vertexes.length / 3;

        for (n = 0; n != nVertex; n++) {
            if (AppWindow.random.nextFloat() > percentMove) {
                continue;
            }

            vIdx = n * 3;

            // skip anything not a floor
            if (normals[vIdx + 1] == 0.0f) {
                continue;
            }

            // remember the original to compare
            origVertex.setFromValues(vertexes[vIdx], vertexes[vIdx + 1], vertexes[vIdx + 2]);

            // move up randomly
            y = vertexes[vIdx + 1] + (AppWindow.random.nextFloat(moveFactor));
            vertexes[vIdx + 1] = y;

            // check for equal vertexes to move
            for (Mesh mesh : meshes) {
                nCheckVertex = mesh.vertexes.length / 3;

                vIdx = 0;

                for (k = 0; k != nCheckVertex; k++) {
                    if ((mesh == this) && (k == n)) {
                        continue;
                    }
                    checkVertex.setFromValues(mesh.vertexes[vIdx], mesh.vertexes[vIdx + 1], mesh.vertexes[vIdx + 2]);
                    if (checkVertex.isCloseEqual(origVertex)) {
                        mesh.vertexes[vIdx + 1] = y;
                    }
                    vIdx += 3;
                }
            }
        }
    }
}
