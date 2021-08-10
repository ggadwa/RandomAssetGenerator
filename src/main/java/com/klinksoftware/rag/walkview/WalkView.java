package com.klinksoftware.rag.walkview;

import com.klinksoftware.rag.utility.*;
import com.klinksoftware.rag.*;
import com.klinksoftware.rag.bitmaps.*;
import com.klinksoftware.rag.mesh.*;
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

    private static final float RAG_NEAR_Z=500.0f;
    private static final float RAG_FAR_Z=500000.0f;
    private static final long RAG_PAINT_TICK=33;
    
    private int wid, high;
    private int vertexShaderId, fragmentShaderId, programId;
    private int vertexPositionAttribute,vertexUVAttribute;
    private int perspectiveUniformId,viewUniformId;
    private int textureId;
    private long nextPaintTick;
    private float aspectRatio;
    private MeshList meshList;
    private RagPoint eyePoint,cameraPoint,cameraAngle,lookAtUpVector;
    private RagMatrix4f perspectiveMatrix, viewMatrix, eyeRotMatrix, eyeRotMatrix2;

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
        eyeRotMatrix=new RagMatrix4f();
        eyeRotMatrix2=new RagMatrix4f();
        
            // no mesh loaded
            
        meshList=null;
        
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
        
            // testing!  load up a texture
            
            BitmapBase bitmapBase;
            GeneratorMain.random=new Random(Calendar.getInstance().getTimeInMillis()); // hack!
            
            bitmapBase=new BitmapBrick();
        bitmapBase.generate(BitmapBrick.VARIATION_NONE,null,"brick");
        int textureSize=bitmapBase.getTextureSize();
        boolean hasAlpha=bitmapBase.hasAlpha();
        
        

        ByteBuffer buffer = MemoryUtil.memAlloc((textureSize*(hasAlpha?4:3))* textureSize);
        buffer.put(bitmapBase.getColorDataAsBytes()).flip();


	textureId = glGenTextures(); //Generate texture ID
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureId); //Bind texture ID
        glTexImage2D(GL_TEXTURE_2D, 0, (hasAlpha?GL_RGBA:GL_RGB), textureSize, textureSize, 0, (hasAlpha?GL_RGBA:GL_RGB), GL_UNSIGNED_BYTE, buffer);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glGenerateMipmap(GL_TEXTURE_2D);

        glBindTexture(GL_TEXTURE_2D, 0);
        
        MemoryUtil.memFree(buffer);
        
        
                float[] vertices = new float[]{
     -50.0f,  -50.0f, 2000.0f,
    -50.0f, 50.0f, 2000.0f,
     50.0f, 50.0f, 2000.0f,
     
     -50.0f,  -50.0f, 2000.0f,
    50.0f, -50.0f, 2000.0f,
     50.0f, 50.0f, 2000.0f,

     -1000.0f,  -1000.0f, 2500.0f,
    -1000.0f, 1000.0f, 2500.0f,
     1000.0f, 1000.0f, 2500.0f,
     
     -1000.0f,  -1000.0f, 2500.0f,
    1000.0f, -1000.0f, 2500.0f,
     1000.0f, 1000.0f, 2500.0f
};

                
    float[] uvs=new float[] {
        0.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 1.0f,
        0.0f, 0.0f,
        1.0f, 0.0f,
        1.0f, 1.0f,
        0.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 1.0f,
        0.0f, 0.0f,
        1.0f, 0.0f,
        1.0f, 1.0f
    };
    
    int[] indexes=new int[] {0,1,2,3,4,5,6,7,8,9,10,11};
    
        Mesh mesh=new Mesh("test","bitmap",vertices,null,uvs,indexes);
        meshList=new MeshList();
        meshList.add(mesh);

        for (n=0;n!=meshList.count();n++) {
            stageMesh(meshList.get(n));
        }

    
    
        // enable everything we need to draw
        glUseProgram(programId);
        glUniform1i(glGetUniformLocation(programId,"baseTex"),0);   // this is always texture slot 0
        glEnableVertexAttribArray(vertexPositionAttribute);
        glEnableVertexAttribArray(vertexUVAttribute);
        glUseProgram(0);

        // redraw timing
        nextPaintTick=System.currentTimeMillis();
    }
    
    private void stageMesh(Mesh mesh) {
        FloatBuffer buf;
        
        // vertexes
        buf = MemoryUtil.memAllocFloat(mesh.vertexes.length);
        buf.put(mesh.vertexes).flip();

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
    }
    
    public void shutdown()
    {
        if (vertexShaderId!=-1) glDeleteShader(vertexShaderId);
        if (fragmentShaderId!=-1) glDeleteShader(fragmentShaderId);
        if (programId!=-1) glDeleteProgram(programId);
    }

    @Override
    public void paintGL() {
        int n,nMesh;
        long tick;
        Mesh mesh;
        
        // redraw timing
        tick=System.currentTimeMillis();
        if (nextPaintTick>tick) return;
        
        while (nextPaintTick>tick) {
            nextPaintTick+=RAG_PAINT_TICK;
        }
        
            // clear
            
        glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
        
            // start the program and setup
            // the drawing matrixes
        
        glUseProgram(programId);

        try ( MemoryStack stack = stackPush()) {
            cameraPoint.setFromValues(0.0f,0.0f,0.0f);

            eyePoint.setFromValues(0,0,-RAG_NEAR_Z);
            eyeRotMatrix.setTranslationFromPoint(cameraPoint);
            eyeRotMatrix2.setRotationFromYAngle(cameraAngle.y);
            eyeRotMatrix.multiply(eyeRotMatrix2);
            eyeRotMatrix2.setRotationFromXAngle(cameraAngle.x);
            eyeRotMatrix.multiply(eyeRotMatrix2);
            this.eyePoint.matrixMultiply(eyeRotMatrix);

            perspectiveMatrix.setPerspectiveMatrix(55.0f,aspectRatio,RAG_NEAR_Z,RAG_FAR_Z);
            viewMatrix.setLookAtMatrix(eyePoint,cameraPoint,lookAtUpVector);

            FloatBuffer perspectiveBuffer = stack.mallocFloat(16);
            perspectiveBuffer.put(perspectiveMatrix.data).flip();
            glUniformMatrix4fv(perspectiveUniformId, false, perspectiveBuffer);

            FloatBuffer viewBuffer = stack.mallocFloat(16);
            viewBuffer.put(viewMatrix.data).flip();
            glUniformMatrix4fv(viewUniformId, false, viewBuffer);
        }

        cameraAngle.y+=0.01f;
        if (cameraAngle.y>=360.0f) cameraAngle.y=0.0f;
    
            // draw all the meshes

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D,textureId);
        
        
        
        
        nMesh=meshList.count();
        
        for (n=0;n!=nMesh;n++) {
            mesh=meshList.get(n);




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

}
