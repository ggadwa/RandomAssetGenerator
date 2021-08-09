package com.klinksoftware.rag.walkview;

import com.klinksoftware.rag.utility.*;
import com.klinksoftware.rag.GeneratorMain;
import com.klinksoftware.rag.bitmaps.*;
import java.nio.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import static org.lwjgl.opengl.ARBFramebufferObject.*;
import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import org.lwjgl.opengl.awt.*;
import org.lwjgl.system.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.*;


public class WalkView extends AWTGLCanvas {

    private int vertexShaderId, fragmentShaderId, programId;
    private int vertexVBOId,uvVBOId;
    private int vertexPositionAttribute,vertexUVAttribute;
    private int orthoUniformId,perspectiveUniformId,viewUniformId;
    private int textureId;
    private RagPoint eyePoint,cameraPoint,lookAtUpVector;
    private RagMatrix4f perspectiveMatrix, viewMatrix;

    public WalkView(GLData glData) {
        super(glData);
    }

    @Override
    public void initGL() {
        String vertexSource, fragmentSource;
        String errorStr;
        
            // some pre-allocates
            
        eyePoint=new RagPoint(0.0f,0.0f,0.0f);
        cameraPoint=new RagPoint(0.0f,0.0f,0.0f);
        lookAtUpVector=new RagPoint(0.0f,-1.0f,0.0f);
        
        perspectiveMatrix=new RagMatrix4f();
        viewMatrix=new RagMatrix4f();
        
        createCapabilities();
        glClearColor(0.9f, 0.9f, 0.9f, 1);
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
        
        /*
                float[] vertices = new float[]{
     50.0f,  50.0f, 0.0f,
    50.0f, 350.0f, 0.0f,
     350.0f, 350.0f, 0.0f,
     
     50.0f,  50.0f, 0.0f,
    350.0f, 50.0f, 0.0f,
     350.0f, 350.0f, 0.0f,

     160.0f,  160.0f, 0.0f,
    160.0f, 460.0f, 0.0f,
     460.0f, 460.0f, 0.0f,
     
     160.0f,  160.0f, 0.0f,
    460.0f, 160.0f, 0.0f,
     460.0f, 460.0f, 0.0f
};
    */
        
                float[] vertices = new float[]{
     -50.0f,  -50.0f, 100.0f,
    -50.0f, 50.0f, 100.0f,
     50.0f, 50.0f, 100.0f,
     
     -50.0f,  -50.0f, 100.0f,
    50.0f, -50.0f, 100.0f,
     50.0f, 50.0f, 100.0f,

     -1000.0f,  -1000.0f, 2000.0f,
    -1000.0f, 1000.0f, 2000.0f,
     1000.0f, 1000.0f, 2000.0f,
     
     -1000.0f,  -1000.0f, 2000.0f,
    1000.0f, -1000.0f, 2000.0f,
     1000.0f, 1000.0f, 2000.0f
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
                
        FloatBuffer verticesBuffer = MemoryUtil.memAllocFloat(vertices.length);
    verticesBuffer.put(vertices).flip();

        FloatBuffer uvsBuffer = MemoryUtil.memAllocFloat(uvs.length);
    uvsBuffer.put(uvs).flip();

        // uniforms and attributes
        
    orthoUniformId=glGetUniformLocation(programId,"orthoMatrix");
    perspectiveUniformId=glGetUniformLocation(programId,"perspectiveMatrix");
    viewUniformId=glGetUniformLocation(programId,"viewMatrix");

    vertexPositionAttribute=glGetAttribLocation(programId,"vertexPosition");
    vertexUVAttribute=glGetAttribLocation(programId,"vertexUV");
    

    vertexVBOId = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, vertexVBOId);
    glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
    memFree(verticesBuffer);
    //glBindBuffer(GL_ARRAY_BUFFER, 0);

    uvVBOId = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, uvVBOId);
    glBufferData(GL_ARRAY_BUFFER, uvsBuffer, GL_STATIC_DRAW);
    memFree(uvsBuffer);
    glBindBuffer(GL_ARRAY_BUFFER, 0);


if (verticesBuffer != null) {
    MemoryUtil.memFree(verticesBuffer);
}
MemoryUtil.memFree(uvsBuffer);

// textures never change
glUseProgram(programId);
glUniform1i(glGetUniformLocation(programId,"baseTex"),0);   // this is always texture slot 0
glUseProgram(0);
    }
    
    public void shutdown()
    {
        if (vertexShaderId!=-1) glDeleteShader(vertexShaderId);
        if (fragmentShaderId!=-1) glDeleteShader(fragmentShaderId);
        if (programId!=-1) glDeleteProgram(programId);
    }

    @Override
    public void paintGL() {
        
        // 30 fps here
        
        int w = getWidth();
        int h = getHeight();
        float aspect = (float) w / h;
        double now = System.currentTimeMillis() * 0.001;
        float width = (float) Math.abs(Math.sin(now * 0.3));
        glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
        glViewport(0, 0, w, h);
        
        
        /*
        

        
        
                this.core.perspectiveMatrix.setPerspectiveMatrix(this.camera.glFOV,(this.core.canvas.width/this.core.canvas.height),this.camera.glNearZ,this.camera.glFarZ);

            // the eye point is -this.camera.glNearZ behind
            // the player

        this.eyePos.setFromValues(0,0,-this.camera.glNearZ);
        this.eyeRotMatrix.setTranslationFromPoint(this.camera.position);
        this.eyeRotMatrix2.setRotationFromYAngle(this.camera.angle.y);
        this.eyeRotMatrix.multiply(this.eyeRotMatrix2);
        this.eyeRotMatrix2.setRotationFromXAngle(this.camera.angle.x);
        this.eyeRotMatrix.multiply(this.eyeRotMatrix2);
        this.eyePos.matrixMultiply(this.eyeRotMatrix);
        
        this.runCameraShake();

            // setup the look at

        this.core.viewMatrix.setLookAtMatrix(this.eyePos,this.camera.position,this.lookAtUpVector);

        */
        
        
        
        
    glUseProgram(programId);

    try ( MemoryStack stack = stackPush()) {
        RagMatrix4f m=new RagMatrix4f();
        m.setOrthoMatrix(w, h, 0.0f, 100.0f);

        FloatBuffer orthoBuffer = stack.mallocFloat(16);
        orthoBuffer.put(m.getData()).flip();
        glUniformMatrix4fv(orthoUniformId, false, orthoBuffer);
        
        eyePoint.setFromValues(0.0f, 0.0f, -500.0f);
        cameraPoint.setFromValues(0.0f,0.0f,0.0f);
        
        perspectiveMatrix.setPerspectiveMatrix(55.0f,((float)w/(float)h),500.0f,500000.0f);
        viewMatrix.setLookAtMatrix(eyePoint,cameraPoint,lookAtUpVector);
        
        FloatBuffer perspectiveBuffer = stack.mallocFloat(16);
        perspectiveBuffer.put(perspectiveMatrix.getData()).flip();
        glUniformMatrix4fv(perspectiveUniformId, false, perspectiveBuffer);
        
        FloatBuffer viewBuffer = stack.mallocFloat(16);
        viewBuffer.put(viewMatrix.getData()).flip();
        glUniformMatrix4fv(viewUniformId, false, viewBuffer);
    }


    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D,textureId);


    
    glBindBuffer(GL_ARRAY_BUFFER, vertexVBOId);
    glVertexAttribPointer(vertexPositionAttribute, 3, GL_FLOAT, false, 0, 0);
    glEnableVertexAttribArray(vertexPositionAttribute);
    glBindBuffer(GL_ARRAY_BUFFER, vertexVBOId);
    
    glBindBuffer(GL_ARRAY_BUFFER, uvVBOId);
    glVertexAttribPointer(vertexUVAttribute, 2, GL_FLOAT, false, 0, 0);
    glEnableVertexAttribArray(vertexUVAttribute);


    glDrawArrays(GL_TRIANGLES, 0, 12);

    
    glBindTexture(GL_TEXTURE_2D,0);
    
    glBindBuffer(GL_ARRAY_BUFFER,0);

    glUseProgram(0);      
        

        swapBuffers();
    }

}
