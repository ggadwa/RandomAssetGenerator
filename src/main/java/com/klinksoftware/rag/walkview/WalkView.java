package com.klinksoftware.rag.walkview;

import com.klinksoftware.rag.utility.*;
import com.klinksoftware.rag.*;
import com.klinksoftware.rag.bitmaps.*;
import com.klinksoftware.rag.map.MapBuilder;
import com.klinksoftware.rag.mesh.*;
import com.klinksoftware.rag.skeleton.Skeleton;
import java.nio.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import static org.lwjgl.opengl.ARBFramebufferObject.*;
import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import org.lwjgl.opengl.awt.*;
import org.lwjgl.system.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class WalkView extends AWTGLCanvas {

    private static final float RAG_NEAR_Z=1.0f;
    private static final float RAG_FAR_Z=500.0f;
    private static final float RAG_FOV=55.0f;
    private static final float RAG_MOVE_SPEED=0.01f;
    private static final long RAG_PAINT_TICK=33;
    
    private int wid, high, lastMouseX, lastMouseY;
    private int vertexShaderId, fragmentShaderId, programId;
    private int vertexPositionAttribute,vertexUVAttribute;
    private int perspectiveUniformId,viewUniformId;
    private long nextPaintTick;
    private float aspectRatio;
    private float moveX,moveY,moveZ;
    private MeshList meshList, incommingMeshList;
    private Skeleton incommingSkeleton;
    private BitmapGenerator incommingBitmapGenerator;
    private RagPoint eyePoint,cameraPoint,cameraAngle,lookAtUpVector, movePoint;
    private RagMatrix4f perspectiveMatrix, viewMatrix, rotMatrix, rotMatrix2;
    private HashMap<String,Integer> bitmaps;

    public WalkView(GLData glData) {
        super(glData);
    }

    @Override
    public void initGL() {
        int n;
        String vertexSource, fragmentSource;
        String errorStr;
        
            // screen sizes
            
        wid=getWidth();
        high=getHeight();
        aspectRatio=(float)wid/(float)high;
        
            // some pre-allocates
            
        eyePoint=new RagPoint(0.0f,0.0f,0.0f);
        cameraPoint=new RagPoint(0.0f,0.0f,0.0f);
        cameraAngle=new RagPoint(0.0f,0.0f,0.0f);
        lookAtUpVector=new RagPoint(0.0f,-1.0f,0.0f);
        
        perspectiveMatrix=new RagMatrix4f();
        viewMatrix=new RagMatrix4f();
        rotMatrix=new RagMatrix4f();
        rotMatrix2=new RagMatrix4f();
        
            // no mesh loaded
            
        meshList=null;
        incommingMeshList=null;
        incommingSkeleton=null;
        incommingBitmapGenerator=null;
        bitmaps=null;
        
            // no dragging
            
        lastMouseX=-1;
        lastMouseY=-1;
        moveX=0;
        moveY=0;
        moveZ=0;
        movePoint=new RagPoint(0.0f,0.0f,0.0f);
        
            // start opengl
        
        createCapabilities();
        glViewport(0,0,wid,high);
        glClearColor(0.9f, 0.9f, 0.9f, 1.0f);
        glEnable(GL_DEPTH_TEST);
        
            // no shader yet
            
        vertexShaderId=-1;
        fragmentShaderId=-1;
        programId=-1;

            // get the shader sources
        
        try {
            vertexSource=Files.readString(Paths.get(getClass().getClassLoader().getResource("shaders/interface.vert").toURI()), StandardCharsets.US_ASCII);
            fragmentSource=Files.readString(Paths.get(getClass().getClassLoader().getResource("shaders/interface.frag").toURI()), StandardCharsets.US_ASCII);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        
            // compile the shader
        
        try {
            vertexShaderId=glCreateShader(GL_VERTEX_SHADER);
            glShaderSource(vertexShaderId, vertexSource);
            glCompileShader(vertexShaderId);
            if (glGetShaderi(vertexShaderId, GL_COMPILE_STATUS)==GL_FALSE) {
                errorStr=glGetShaderInfoLog(vertexShaderId,glGetShaderi(vertexShaderId, GL_INFO_LOG_LENGTH));
                glDeleteShader(vertexShaderId);
                throw new Exception("vertex shader compile failed: "+errorStr);
            }
        
            fragmentShaderId=glCreateShader(GL_FRAGMENT_SHADER);
            glShaderSource(fragmentShaderId, fragmentSource);
            glCompileShader(fragmentShaderId);
            if (glGetShaderi(fragmentShaderId, GL_COMPILE_STATUS)==GL_FALSE) {
                errorStr=glGetShaderInfoLog(fragmentShaderId,glGetShaderi(fragmentShaderId, GL_INFO_LOG_LENGTH));
                glDeleteShader(vertexShaderId);
                glDeleteShader(fragmentShaderId);
                throw new Exception("fragment shader compile failed: "+errorStr);
            }
            
            programId = glCreateProgram();
            glAttachShader(programId, vertexShaderId);
            glAttachShader(programId, fragmentShaderId);
            glLinkProgram(programId);
            if (glGetProgrami(programId,GL_LINK_STATUS)==GL_FALSE) {
                errorStr=glGetProgramInfoLog(programId,glGetShaderi(programId, GL_INFO_LOG_LENGTH));
                glDeleteShader(vertexShaderId);
                glDeleteShader(fragmentShaderId);
                glDeleteProgram(programId);
                throw new Exception("program link failed: "+errorStr);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // get locations of uniforms and attributes
        perspectiveUniformId=glGetUniformLocation(programId,"perspectiveMatrix");
        viewUniformId=glGetUniformLocation(programId,"viewMatrix");

        vertexPositionAttribute=glGetAttribLocation(programId,"vertexPosition");
        vertexUVAttribute=glGetAttribLocation(programId,"vertexUV");
    
        // enable everything we need to draw
        glUseProgram(programId);
        glUniform1i(glGetUniformLocation(programId,"baseTex"),0);   // this is always texture slot 0
        glEnableVertexAttribArray(vertexPositionAttribute);
        glEnableVertexAttribArray(vertexUVAttribute);
        glUseProgram(0);

        // redraw timing
        nextPaintTick=System.currentTimeMillis();
    }
    
    private void stageMesh(Mesh mesh,RagPoint bonePoint) {
        int n;
        FloatBuffer buf;
        
        // vertexes
        buf = MemoryUtil.memAllocFloat(mesh.vertexes.length);
        
        for (n=0;n<mesh.vertexes.length;n+=3) {
            buf.put(mesh.vertexes[n]+bonePoint.x);
            buf.put(mesh.vertexes[n+1]+bonePoint.y);
            buf.put(mesh.vertexes[n+2]+bonePoint.z);
        }
        
        buf.flip();

        mesh.vboVertexId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, mesh.vboVertexId);
        glBufferData(GL_ARRAY_BUFFER, buf, GL_STATIC_DRAW);
        memFree(buf);

        // uvs
        buf = MemoryUtil.memAllocFloat(mesh.uvs.length);
        buf.put(mesh.uvs).flip();

        mesh.vboUVId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, mesh.vboUVId);
        glBufferData(GL_ARRAY_BUFFER, buf, GL_STATIC_DRAW);
        memFree(buf);
        
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        // indexes
        mesh.indexBuf=MemoryUtil.memAllocInt(mesh.indexes.length);
        mesh.indexBuf.put(mesh.indexes).flip();
    }
    
    private void releaseMesh(Mesh mesh) {
        glDeleteBuffers(mesh.vboVertexId);
        glDeleteBuffers(mesh.vboUVId);
        
        memFree(mesh.indexBuf);
    }
    
    public void setIncommingMeshList(MeshList incommingMeshList,Skeleton incommingSkeleton,BitmapGenerator incommingBitmapGenerator) {
        // we have to setup an incomming list so we can load
        // the new list on a draw call so the opengl context is set
        this.incommingMeshList=incommingMeshList;
        this.incommingSkeleton=incommingSkeleton;
        this.incommingBitmapGenerator=incommingBitmapGenerator;
    }
    
    public void setCameraPoint(float x,float y,float z) {
        cameraPoint.setFromValues(x,y,z);
    }
    
    private void stageMeshList() {
        int n,nMesh,boneIdx,textureSize,textureId;
        boolean hasAlpha;
        String bitmapName;
        Mesh mesh;
        BitmapBase bitmapBase;
        RagPoint tempPnt;
        ByteBuffer bitmapBuf;
        
        if (incommingMeshList==null) return;
        
            // remove old mesh list
        
        if (meshList!=null) {
            nMesh=meshList.count();

            for (n=0;n!=nMesh;n++) {
                releaseMesh(meshList.get(n));
            }
        }
        
            // remove old bitmaps
            
        if (bitmaps!=null) {
            for (int id:bitmaps.values()) {
                glDeleteBuffers(id);
            }
        }
        
            // setup the new mesh
            
        tempPnt=new RagPoint(0.0f,0.0f,0.0f);
        bitmaps=new HashMap<>();
            
        nMesh=incommingMeshList.count();

        for (n=0;n!=nMesh;n++) {
            
                // the mesh
                
            mesh=incommingMeshList.get(n);
            boneIdx=incommingSkeleton.findBoneIndex(mesh.name);
            if (boneIdx==-1) {
                stageMesh(mesh,tempPnt);
            }
            else {
                stageMesh(mesh,incommingSkeleton.bones.get(boneIdx).pnt);
            }
            
                // the bitmap
                
            bitmapName=mesh.bitmapName;
            
            if (!bitmaps.containsKey(bitmapName)) {
                bitmapBase=incommingBitmapGenerator.bitmaps.get(bitmapName);
            
                textureSize=bitmapBase.getTextureSize();
                hasAlpha=bitmapBase.hasAlpha();

                bitmapBuf = MemoryUtil.memAlloc((textureSize*(hasAlpha?4:3))* textureSize);
                bitmapBuf.put(bitmapBase.getColorDataAsBytes()).flip();

                textureId = glGenTextures();
                glActiveTexture(GL_TEXTURE0);
                glBindTexture(GL_TEXTURE_2D, textureId);
                glTexImage2D(GL_TEXTURE_2D, 0, (hasAlpha?GL_RGBA:GL_RGB), textureSize, textureSize, 0, (hasAlpha?GL_RGBA:GL_RGB), GL_UNSIGNED_BYTE, bitmapBuf);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
                glGenerateMipmap(GL_TEXTURE_2D);

                MemoryUtil.memFree(bitmapBuf);
                
                bitmaps.put(bitmapName, textureId);
            }
        }

            // unbind any textures
            
        glBindTexture(GL_TEXTURE_2D, 0);
        
            // switch to new mesh list
            // and clear incomming
            
        this.meshList=incommingMeshList;
        incommingMeshList=null;
        incommingSkeleton=null;
        incommingBitmapGenerator=null;
    }
    
    public void shutdown()
    {
        if (vertexShaderId!=-1) glDeleteShader(vertexShaderId);
        if (fragmentShaderId!=-1) glDeleteShader(fragmentShaderId);
        if (programId!=-1) glDeleteProgram(programId);
    }

    @Override
    public void paintGL() {
        int n,nMesh,curTextureId,textureId;
        long tick;
        Mesh mesh;
        
        // do we have an incomming meshlist?
        stageMeshList();
        
        // no meshes
        if (meshList==null) return;
        
        // redraw timing
        tick=System.currentTimeMillis();
        if (nextPaintTick>tick) return;
        
        while (nextPaintTick>tick) {
            nextPaintTick+=RAG_PAINT_TICK;
        }
        
            // movement
            
        if ((moveX!=0.0f) || (moveZ!=0.0f)) {
            movePoint.setFromValues(moveX,0,moveZ);
            rotMatrix.setRotationFromYAngle(cameraAngle.y);
            rotMatrix2.setRotationFromXAngle(cameraAngle.x);
            rotMatrix.multiply(rotMatrix2);
            movePoint.matrixMultiply(rotMatrix);
            
            cameraPoint.addPoint(movePoint);
        }
        
        if (moveY!=0.0f) cameraPoint.y+=moveY;
        
            // clear
            
        glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
        
            // start the program and setup
            // the drawing matrixes
        
        glUseProgram(programId);

        try ( MemoryStack stack = stackPush()) {
            eyePoint.setFromValues(0,0,-RAG_NEAR_Z);
            rotMatrix.setTranslationFromPoint(cameraPoint);
            rotMatrix2.setRotationFromYAngle(cameraAngle.y);
            rotMatrix.multiply(rotMatrix2);
            rotMatrix2.setRotationFromXAngle(cameraAngle.x);
            rotMatrix.multiply(rotMatrix2);
            eyePoint.matrixMultiply(rotMatrix);

            perspectiveMatrix.setPerspectiveMatrix(RAG_FOV,aspectRatio,RAG_NEAR_Z,RAG_FAR_Z);
            viewMatrix.setLookAtMatrix(eyePoint,cameraPoint,lookAtUpVector);

            FloatBuffer perspectiveBuffer = stack.mallocFloat(16);
            perspectiveBuffer.put(perspectiveMatrix.data).flip();
            glUniformMatrix4fv(perspectiveUniformId, false, perspectiveBuffer);

            FloatBuffer viewBuffer = stack.mallocFloat(16);
            viewBuffer.put(viewMatrix.data).flip();
            glUniformMatrix4fv(viewUniformId, false, viewBuffer);
        }
        
            // draw all the meshes

        curTextureId=-1;
        
        nMesh=meshList.count();
        
        for (n=0;n!=nMesh;n++) {
            mesh=meshList.get(n);
            
                // new texture?
                
            textureId=bitmaps.get(mesh.bitmapName);
            if (textureId!=curTextureId) {
                curTextureId=textureId;
                
                glActiveTexture(GL_TEXTURE0);
                glBindTexture(GL_TEXTURE_2D,textureId);
            }

                // draw the mesh
                
            glBindBuffer(GL_ARRAY_BUFFER, mesh.vboVertexId);
            glVertexAttribPointer(vertexPositionAttribute, 3, GL_FLOAT, false, 0, 0);

            glBindBuffer(GL_ARRAY_BUFFER, mesh.vboUVId);
            glVertexAttribPointer(vertexUVAttribute, 2, GL_FLOAT, false, 0, 0);

            glDrawElements(GL_TRIANGLES, mesh.indexBuf);
        }

            // shutdown drawing
            
        glBindBuffer(GL_ARRAY_BUFFER,0);
        glBindTexture(GL_TEXTURE_2D,0);
        glUseProgram(0);      
        
            // swap the buffers
            
        swapBuffers();
    }
    
    //
    // events
    //
    
    public void mousePressed(int button,int x,int y) {
        lastMouseX=x;
        lastMouseY=y;
    }
    
    public void mouseRelease(int button) {
    }
    
    public void mouseDrag(int x,int y) {
        if (lastMouseX!=x) {
            cameraAngle.y-=((float)(x-lastMouseX)*0.5f);
            if (cameraAngle.y<0) cameraAngle.y=360.0f+cameraAngle.y;
            if (cameraAngle.y>=360) cameraAngle.y=cameraAngle.y-360.0f;
            lastMouseX=x;
        }
        
        if (lastMouseY!=y) {
            cameraAngle.x+=((float)(y-lastMouseY)*0.2f);
            if (cameraAngle.x<-89.0f) cameraAngle.x=-89.0f;
            if (cameraAngle.x>89.0f) cameraAngle.x=89.0f;
            lastMouseY=y;
        }
    }
    
    public void keyPress(char key) {
        if (key=='w') moveZ=RAG_MOVE_SPEED;
        if (key=='s') moveZ=-RAG_MOVE_SPEED;
        if (key=='a') moveX=RAG_MOVE_SPEED;
        if (key=='d') moveX=-RAG_MOVE_SPEED;
        if (key=='q') moveY=RAG_MOVE_SPEED;
        if (key=='e') moveY=-RAG_MOVE_SPEED;
    }
    
    public void keyRelease(char key) {
        if ((key=='w') || (key=='s')) moveZ=0.0f;
        if ((key=='a') || (key=='d')) moveX=0.0f;
        if ((key=='q') || (key=='e')) moveY=0.0f;
    }
}
