package com.klinksoftware.rag.walkview;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.ARBVertexArrayObject.*;
import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import org.lwjgl.opengl.awt.*;
import org.lwjgl.system.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.stb.STBImage.*;


public class WalkView extends AWTGLCanvas {

    private int vertexShaderId, fragmentShaderId, programId;
    private int vaoId,vboId;

    public WalkView(GLData glData) {
        super(glData);
    }

    @Override
    public void initGL() {
        String vertexSource, fragmentSource;
        String errorStr;
        
        System.out.println("OpenGL version: " + effective.majorVersion + "." + effective.minorVersion + " (Profile: " + effective.profile + ")");
        
        createCapabilities();
        glClearColor(0.9f, 0.9f, 0.9f, 1);
        //glEnable(GL_DEPTH_TEST);
        
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
/*
            BufferedImage img = null;
try {
    img = ImageIO.read(new File("output"+File.separator+"bitmap_test2"+File.separator+"textures"+File.separator+"brick_color.png"));
} catch (IOException e) {
    e.printStackTrace();
    return;
}
            
        ByteBuffer buffer = BufferUtils.createByteBuffer(img.getWidth() * img.getHeight() * 3);
        
        for(int y = 0; y < img.getHeight(); y++){
            for(int x = 0; x < img.getWidth(); x++){
                int pixel = pixels[y * img.getWidth() + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
                buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
                buffer.put((byte) (pixel & 0xFF));               // Blue component
                buffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
            }
        }

        buffer.flip(); //FOR THE LOVE OF GOD DO NOT FORGET THIS

        // You now have a ByteBuffer filled with the color data of each pixel.
        // Now just create a texture ID and bind it. Then you can load it using 
        // whatever OpenGL method you want, for example:

		int textureID = glGenTextures(); //Generate texture ID
        glBindTexture(GL_TEXTURE_2D, textureID); //Bind texture ID
        
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, img.getWidth(), img.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
 */
/*
            
                    int texID = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, texID);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        int format;
        if (comp == 3) {
            if ((w & 3) != 0) {
                glPixelStorei(GL_UNPACK_ALIGNMENT, 2 - (w & 1));
            }
            format = GL_RGB;
        } else {
            premultiplyAlpha();

            glEnable(GL_BLEND);
            glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

            format = GL_RGBA;
        }

        glTexImage2D(GL_TEXTURE_2D, 0, format, w, h, 0, format, GL_UNSIGNED_BYTE, image);
*/
/*
        float[] vertices = new float[]{
     0.0f,  0.5f, 0.0f,
    -0.5f, -0.5f, 0.0f,
     0.5f, -0.5f, 0.0f
};
 */       
        
        
                float[] vertices = new float[]{
     50.0f,  50.0f, 0.0f,
    50.0f, 100.0f, 0.0f,
     100.0f, 100.0f, 0.0f
};
                
        FloatBuffer verticesBuffer = MemoryUtil.memAllocFloat(vertices.length);
verticesBuffer.put(vertices).flip();


vaoId = glGenVertexArrays();
glBindVertexArray(vaoId);

vboId = glGenBuffers();
glBindBuffer(GL_ARRAY_BUFFER, vboId);
glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
memFree(verticesBuffer);

glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
// Unbind the VBO
glBindBuffer(GL_ARRAY_BUFFER, 0);

// Unbind the VAO
glBindVertexArray(0);

if (verticesBuffer != null) {
    MemoryUtil.memFree(verticesBuffer);
}

    }
    
    public void shutdown()
    {
        if (vertexShaderId!=-1) glDeleteShader(vertexShaderId);
        if (fragmentShaderId!=-1) glDeleteShader(fragmentShaderId);
        if (programId!=-1) glDeleteProgram(programId);
    }

    @Override
    public void paintGL() {
        int w = getWidth();
        int h = getHeight();
        float aspect = (float) w / h;
        double now = System.currentTimeMillis() * 0.001;
        float width = (float) Math.abs(Math.sin(now * 0.3));
        glClear(GL_COLOR_BUFFER_BIT);
        glViewport(0, 0, w, h);
        
        RagMatrix4f m=new RagMatrix4f();
        m.setOrthoMatrix(w, h, 0.0f, 100.0f);

       int orthoId=glGetUniformLocation(programId,"orthoMatrix");
       
       FloatBuffer orthoBuffer = MemoryUtil.memAllocFloat(16);
        orthoBuffer.put(m.getData()).flip();
        
        
        
        
glUseProgram(programId);

glUniformMatrix4fv(orthoId, false, orthoBuffer);

    // Bind to the VAO
    glBindVertexArray(vaoId);
    glEnableVertexAttribArray(0);

    // Draw the vertices
    glDrawArrays(GL_TRIANGLES, 0, 3);

    // Restore state
    glDisableVertexAttribArray(0);
    glBindVertexArray(0);

  glUseProgram(0);      
        
        MemoryUtil.memFree(orthoBuffer);
        
       /*
        

        glBegin(GL_QUADS);
        glColor3f(0.4f, 0.6f, 0.8f);
        glVertex2f(-0.75f * width / aspect, 0.0f);
        glVertex2f(0, -0.75f);
        glVertex2f(+0.75f * width / aspect, 0);
        glVertex2f(0, +0.75f);
        glEnd();
*/
        swapBuffers();
    }

}
