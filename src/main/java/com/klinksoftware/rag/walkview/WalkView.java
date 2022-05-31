package com.klinksoftware.rag.walkview;

import com.klinksoftware.rag.utility.*;
import com.klinksoftware.rag.bitmaps.*;
import com.klinksoftware.rag.mesh.*;
import com.klinksoftware.rag.skeleton.Skeleton;
import java.awt.event.KeyEvent;
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
    private static final float RAG_MOVE_SPEED = 0.01f;
    private static final float RAG_SPEED_MULTIPLIER = 3.0f;
    private static final long RAG_PAINT_TICK=33;
    private static final float RAG_LIGHT_LOW_INTENSITY = 100.0f;
    private static final float RAG_LIGHT_HIGH_INTENSITY = 500.0f;

    private int wid,high,lastMouseX,lastMouseY;
    private int vertexShaderId,fragmentShaderId,programId;
    private int vertexPositionAttribute,vertexUVAttribute, vertexNormalAttribute, vertexTangentAttribute;
    private int perspectiveMatrixUniformId, viewMatrixUniformId, normalMatrixUniformId, lightPositionIntensityUniformId;
    private int hasEmissiveUniformId, emissiveFactorUniformId;
    private long nextPaintTick;
    private float aspectRatio;
    private float moveX, moveY, moveZ, speedMultiplier;
    private float cameraRotateDistance, cameraRotateOffsetY;
    private float currentLightIntensity;
    private boolean cameraCenterRotate;
    private MeshList meshList, incommingMeshList;
    private Skeleton incommingSkeleton;
    private HashMap<String, BitmapBase> incommingBitmaps;
    private RagPoint eyePoint, cameraPoint, cameraAngle, lightEyePoint, lookAtUpVector, movePoint, fixedLightPoint;
    private RagMatrix4f perspectiveMatrix,viewMatrix,rotMatrix,rotMatrix2;
    private RagMatrix3f normalMatrix;
    private HashMap<String, WalkViewTexture> textures;

    public WalkView(GLData glData) {
        super(glData);
    }

    @Override
    public void initGL() {
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
        lightEyePoint = new RagPoint(0.0f, 0.0f, 0.0f);
        lookAtUpVector=new RagPoint(0.0f,-1.0f,0.0f);

        perspectiveMatrix=new RagMatrix4f();
        viewMatrix=new RagMatrix4f();
        normalMatrix=new RagMatrix3f();
        rotMatrix=new RagMatrix4f();
        rotMatrix2=new RagMatrix4f();

            // no mesh loaded

        meshList = null;
        textures = null;

        incommingMeshList=null;
        incommingSkeleton=null;
        incommingBitmaps = null;

            // no dragging

        lastMouseX=-1;
        lastMouseY=-1;
        moveX=0;
        moveY=0;
        moveZ=0;
        movePoint = new RagPoint(0.0f, 0.0f, 0.0f);
        speedMultiplier = 1.0f;

        cameraCenterRotate=false;
        cameraRotateDistance=0.0f;
        cameraRotateOffsetY = 0.0f;
        fixedLightPoint = null;

        currentLightIntensity = RAG_LIGHT_LOW_INTENSITY;

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
            vertexSource=Files.readString(Paths.get(getClass().getClassLoader().getResource("shaders/view.vert").toURI()), StandardCharsets.US_ASCII);
            fragmentSource=Files.readString(Paths.get(getClass().getClassLoader().getResource("shaders/view.frag").toURI()), StandardCharsets.US_ASCII);
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
        perspectiveMatrixUniformId=glGetUniformLocation(programId,"perspectiveMatrix");
        viewMatrixUniformId=glGetUniformLocation(programId,"viewMatrix");
        normalMatrixUniformId=glGetUniformLocation(programId,"normalMatrix");

        lightPositionIntensityUniformId = glGetUniformLocation(programId, "lightPositionIntensity");

        hasEmissiveUniformId = glGetUniformLocation(programId, "hasEmissive");
        emissiveFactorUniformId = glGetUniformLocation(programId, "emissiveFactor");

        vertexPositionAttribute=glGetAttribLocation(programId,"vertexPosition");
        vertexUVAttribute=glGetAttribLocation(programId,"vertexUV");
        vertexNormalAttribute=glGetAttribLocation(programId,"vertexNormal");
        vertexTangentAttribute=glGetAttribLocation(programId,"vertexTangent");

        // enable everything we need to draw
        glUseProgram(programId);
        glUniform1i(glGetUniformLocation(programId,"baseTex"),0);   // this is always texture slot 0
        glUniform1i(glGetUniformLocation(programId,"normalTex"),1);   // this is always texture slot 1
        glUniform1i(glGetUniformLocation(programId, "metallicRoughnessTex"), 2);   // this is always texture slot 2
        glUniform1i(glGetUniformLocation(programId, "emissiveTex"), 3);   // this is always texture slot 3
        glEnableVertexAttribArray(vertexPositionAttribute);
        glEnableVertexAttribArray(vertexUVAttribute);
        glEnableVertexAttribArray(vertexNormalAttribute);
        glEnableVertexAttribArray(vertexTangentAttribute);
        glUseProgram(0);

        // redraw timing
        nextPaintTick=System.currentTimeMillis();
    }

    //
    // add and remove meshes from view
    //

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

        // normals
        buf = MemoryUtil.memAllocFloat(mesh.normals.length);
        buf.put(mesh.normals).flip();

        mesh.vboNormalId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, mesh.vboNormalId);
        glBufferData(GL_ARRAY_BUFFER, buf, GL_STATIC_DRAW);
        memFree(buf);

        // tangents
        buf = MemoryUtil.memAllocFloat(mesh.tangents.length);
        buf.put(mesh.tangents).flip();

        mesh.vboTangentId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, mesh.vboTangentId);
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
        glDeleteBuffers(mesh.vboNormalId);
        glDeleteBuffers(mesh.vboTangentId);

        memFree(mesh.indexBuf);
    }

    //
    // setup meshes in view
    // we do this as an incomming list so we can actually
    // have the correct context as this gets triggered during a draw
    //

    public void setIncommingMeshList(MeshList incommingMeshList, Skeleton incommingSkeleton, HashMap<String, BitmapBase> incommingBitmaps) {
        this.incommingMeshList=incommingMeshList;
        this.incommingSkeleton=incommingSkeleton;
        this.incommingBitmaps = incommingBitmaps;
    }

    //
    // set the view cameras
    //

    public void setCameraWalkView(float x,float y,float z) {
        cameraPoint.setFromValues(x, y, z);
        cameraAngle.setFromValues(0.0f, 0.0f, 0.0f);
        cameraCenterRotate = false;
        fixedLightPoint = null;
    }

    public void setCameraCenterRotate(float dist, float rotateX, float rotateY, float offsetY, float lightDistance) {
        cameraRotateDistance=dist;
        cameraRotateOffsetY = offsetY;
        cameraAngle.setFromValues(rotateX, rotateY, 0.0f);
        cameraCenterRotate = true;
        fixedLightPoint = new RagPoint(-1.5f, offsetY, lightDistance);
    }

    //
    // textures
    //

    private int loadTexture(int textureSize, boolean hasAlpha, byte[] textureData) {
        int textureId;
        ByteBuffer bitmapBuf;

        if (textureData==null) return(-1);

        bitmapBuf = MemoryUtil.memAlloc((textureSize * (hasAlpha ? 4 : 3)) * textureSize);
        bitmapBuf.put(textureData).flip();

        textureId=glGenTextures();
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D,textureId);
        glTexImage2D(GL_TEXTURE_2D,0,(hasAlpha?GL_RGBA:GL_RGB),textureSize,textureSize,0,(hasAlpha?GL_RGBA:GL_RGB),GL_UNSIGNED_BYTE,bitmapBuf);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR_MIPMAP_LINEAR);
        glGenerateMipmap(GL_TEXTURE_2D);

        MemoryUtil.memFree(bitmapBuf);

        return(textureId);
    }

    //
    // stage the mesh list
    // this gets triggered during a draw operation when an incomming mesh
    // list has been set
    //

    private void stageMeshList() {
        int n, nMesh, boneIdx;
        String bitmapName;
        Mesh mesh;
        BitmapBase bitmapBase;
        RagPoint tempPnt;
        WalkViewTexture texture;

        if (incommingMeshList==null) return;

            // remove old mesh list

        if (meshList!=null) {
            nMesh=meshList.count();

            for (n=0;n!=nMesh;n++) {
                releaseMesh(meshList.get(n));
            }
        }

        // remove old textures

        if (textures != null) {
            for (WalkViewTexture texture2 : textures.values()) {
                if (texture2.colorTextureId!=-1) glDeleteBuffers(texture2.colorTextureId);
                if (texture2.normalTextureId!=-1) glDeleteBuffers(texture2.normalTextureId);
                if (texture2.metallicRoughnessTextureId != -1) {
                    glDeleteBuffers(texture2.metallicRoughnessTextureId);
                }
                if (texture2.emissiveTextureId != -1) {
                    glDeleteBuffers(texture2.emissiveTextureId);
                }
            }
        }

            // setup the new mesh

        tempPnt=new RagPoint(0.0f,0.0f,0.0f);
        textures = new HashMap<>();

        nMesh=incommingMeshList.count();

        for (n=0;n!=nMesh;n++) {

                // the mesh

            mesh=incommingMeshList.get(n);
            boneIdx=incommingSkeleton.findBoneIndexforMeshIndex(n);
            if (boneIdx==-1) {
                stageMesh(mesh,tempPnt);
            }
            else {
                stageMesh(mesh,incommingSkeleton.getBoneAbsolutePoint(boneIdx));
            }

                // the bitmap

            bitmapName=mesh.bitmapName;

            if (!textures.containsKey(bitmapName)) {
                bitmapBase = incommingBitmaps.get(bitmapName);

                texture = new WalkViewTexture();
                texture.colorTextureId=loadTexture(bitmapBase.getTextureSize(),bitmapBase.hasAlpha(),bitmapBase.getColorDataAsBytes());
                texture.normalTextureId=loadTexture(bitmapBase.getTextureSize(),false,bitmapBase.getNormalDataAsBytes());
                texture.metallicRoughnessTextureId = loadTexture(bitmapBase.getTextureSize(), false, bitmapBase.getMetallicRoughnessDataAsBytes());

                if (!bitmapBase.hasEmissive()) {
                    texture.emissiveTextureId = -1;
                } else {
                    texture.emissiveTextureId = loadTexture(bitmapBase.getTextureSize(), false, bitmapBase.getEmissiveDataAsBytes());
                }

                textures.put(bitmapName, texture);
            }
        }

            // unbind any textures

        glBindTexture(GL_TEXTURE_2D, 0);

            // switch to new mesh list
            // and clear incomming

        this.meshList=incommingMeshList;
        incommingMeshList=null;
        incommingSkeleton=null;
        incommingBitmaps = null;
    }

    public void shutdown() {
        if (vertexShaderId!=-1) glDeleteShader(vertexShaderId);
        if (fragmentShaderId!=-1) glDeleteShader(fragmentShaderId);
        if (programId!=-1) glDeleteProgram(programId);
    }

    //
    // drawing camera/eye setup
    //

    private void setupCameraWalkView() {
        // any movement

        if ((moveX != 0.0f) || (moveZ != 0.0f)) {
            movePoint.setFromValues((moveX * speedMultiplier), 0, (moveZ * speedMultiplier));
            rotMatrix.setRotationFromYAngle(cameraAngle.y);
            rotMatrix2.setRotationFromXAngle(cameraAngle.x);
            rotMatrix.multiply(rotMatrix2);
            movePoint.matrixMultiply(rotMatrix);

            cameraPoint.addPoint(movePoint);
        }

        if (moveY != 0.0f) {
            cameraPoint.y += (moveY * speedMultiplier);
        }

            // setup the eye point

        eyePoint.setFromValues(0,0,-RAG_NEAR_Z);
        rotMatrix.setTranslationFromPoint(cameraPoint);
        rotMatrix2.setRotationFromYAngle(cameraAngle.y);
        rotMatrix.multiply(rotMatrix2);
        rotMatrix2.setRotationFromXAngle(cameraAngle.x);
        rotMatrix.multiply(rotMatrix2);
        eyePoint.matrixMultiply(rotMatrix);
    }

    private void setupCameraCenterRotate() {
            // any movement

       cameraRotateDistance-=moveZ;

            // camera at center

        cameraPoint.setFromValues(0.0f,cameraRotateOffsetY,0.0f);

        eyePoint.setFromValues(0,0,cameraRotateDistance);
        rotMatrix.setTranslationFromPoint(cameraPoint);
        rotMatrix2.setRotationFromYAngle(cameraAngle.y);
        rotMatrix.multiply(rotMatrix2);
        rotMatrix2.setRotationFromXAngle(cameraAngle.x);
        rotMatrix.multiply(rotMatrix2);
        eyePoint.matrixMultiply(rotMatrix);
    }

    //
    // convert point to eye cordinates
    //

    private void convertToEyeCoordinates(RagPoint pnt, RagPoint eyePnt) {
        eyePnt.x=(pnt.x*viewMatrix.data[0])+(pnt.y*viewMatrix.data[4])+(pnt.z*viewMatrix.data[8])+viewMatrix.data[12];
        eyePnt.y=(pnt.x*viewMatrix.data[1])+(pnt.y*viewMatrix.data[5])+(pnt.z*viewMatrix.data[9])+viewMatrix.data[13];
        eyePnt.z=(pnt.x*viewMatrix.data[2])+(pnt.y*viewMatrix.data[6])+(pnt.z*viewMatrix.data[10])+viewMatrix.data[14];
    }

    //
    // cos glow
    //
    private float getEmissiveFactor(long tick) {
        float f;

        tick = tick % 2000;
        f = (float) Math.cos((2.0 * Math.PI) * ((double) tick / 2000.0));
        return ((f * 0.4f) + 0.4f);
    }

    //
    // draw the scene
    //

    @Override
    public void paintGL() {
        int n,nMesh;
        long tick;
        FloatBuffer buf;
        Mesh mesh;
        WalkViewTexture texture,curTexture;

        // do we have an incomming meshlist?
        stageMeshList();

        // redraw timing
        tick=System.currentTimeMillis();
        if (nextPaintTick>tick) return;

        while (nextPaintTick>tick) {
            nextPaintTick+=RAG_PAINT_TICK;
        }

        // no meshes, just black
        if (meshList == null) {
            glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);
            swapBuffers();
            return;
        }

            // set the camera

        if (!cameraCenterRotate) {
            setupCameraWalkView();
        }
        else {
            setupCameraCenterRotate();
        }

            // clear

        glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);

            // start the program and setup
            // the drawing matrixes

        glUseProgram(programId);

        try (MemoryStack stack = stackPush()) {
            perspectiveMatrix.setPerspectiveMatrix(RAG_FOV,aspectRatio,RAG_NEAR_Z,RAG_FAR_Z);
            viewMatrix.setLookAtMatrix(eyePoint,cameraPoint,lookAtUpVector);
            normalMatrix.setInvertTransposeFromMat4(viewMatrix);

            buf=stack.mallocFloat(16);
            buf.put(perspectiveMatrix.data).flip();
            glUniformMatrix4fv(perspectiveMatrixUniformId,false,buf);

            buf=stack.mallocFloat(16);
            buf.put(viewMatrix.data).flip();
            glUniformMatrix4fv(viewMatrixUniformId,false,buf);

            buf=stack.mallocFloat(12);
            buf.put(normalMatrix.data).flip();
            glUniformMatrix3fv(normalMatrixUniformId,false,buf);

            if (fixedLightPoint != null) {   // lights need to be in eye coordinates
                convertToEyeCoordinates(fixedLightPoint, lightEyePoint);
            } else {
                convertToEyeCoordinates(cameraPoint, lightEyePoint);
            }
            buf=stack.mallocFloat(4);
            buf.put(lightEyePoint.x).put(lightEyePoint.y).put(lightEyePoint.z).put(currentLightIntensity).flip();
            glUniform4fv(lightPositionIntensityUniformId,buf);
        }

            // draw all the meshes

        curTexture=null;

        nMesh=meshList.count();

        for (n=0;n!=nMesh;n++) {
            mesh=meshList.get(n);

                // new texture?

            texture = textures.get(mesh.bitmapName);
            if (texture!=curTexture) {
                curTexture=texture;

                glActiveTexture(GL_TEXTURE0);
                glBindTexture(GL_TEXTURE_2D,texture.colorTextureId);
                glActiveTexture(GL_TEXTURE1);
                glBindTexture(GL_TEXTURE_2D,texture.normalTextureId);
                glActiveTexture(GL_TEXTURE2);
                glBindTexture(GL_TEXTURE_2D, texture.metallicRoughnessTextureId);
                if (texture.emissiveTextureId == -1) {
                    glUniform1i(hasEmissiveUniformId, 1);
                    glUniform1f(emissiveFactorUniformId, 0.0f);
                } else {
                    glUniform1i(hasEmissiveUniformId, 1);
                    glUniform1f(emissiveFactorUniformId, getEmissiveFactor(tick));
                    glActiveTexture(GL_TEXTURE3);
                    glBindTexture(GL_TEXTURE_2D, texture.emissiveTextureId);
                }
            }

                // draw the mesh

            glBindBuffer(GL_ARRAY_BUFFER, mesh.vboVertexId);
            glVertexAttribPointer(vertexPositionAttribute, 3, GL_FLOAT, false, 0, 0);

            glBindBuffer(GL_ARRAY_BUFFER, mesh.vboUVId);
            glVertexAttribPointer(vertexUVAttribute, 2, GL_FLOAT, false, 0, 0);

            glBindBuffer(GL_ARRAY_BUFFER, mesh.vboNormalId);
            glVertexAttribPointer(vertexNormalAttribute, 3, GL_FLOAT, false, 0, 0);

            glBindBuffer(GL_ARRAY_BUFFER, mesh.vboTangentId);
            glVertexAttribPointer(vertexTangentAttribute, 3, GL_FLOAT, false, 0, 0);

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

    public void keyPress(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_SHIFT:
                speedMultiplier = RAG_SPEED_MULTIPLIER;
                return;
            case KeyEvent.VK_W:
                moveZ = RAG_MOVE_SPEED;
                return;
            case KeyEvent.VK_S:
                moveZ = -RAG_MOVE_SPEED;
                return;
            case KeyEvent.VK_A:
                moveX = RAG_MOVE_SPEED;
                return;
            case KeyEvent.VK_D:
                moveX = -RAG_MOVE_SPEED;
                return;
            case KeyEvent.VK_Q:
                moveY = RAG_MOVE_SPEED;
                return;
            case KeyEvent.VK_E:
                moveY = -RAG_MOVE_SPEED;
                return;
        }
    }

    public void keyRelease(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_SHIFT:
                speedMultiplier = 1.0f;
                return;
            case KeyEvent.VK_W:
            case KeyEvent.VK_S:
                moveZ = 0.0f;
                return;
            case KeyEvent.VK_A:
            case KeyEvent.VK_D:
                moveX = 0.0f;
                return;
            case KeyEvent.VK_Q:
            case KeyEvent.VK_E:
                moveY = 0.0f;
                return;
            case KeyEvent.VK_T:
                currentLightIntensity = (currentLightIntensity == RAG_LIGHT_LOW_INTENSITY) ? RAG_LIGHT_HIGH_INTENSITY : RAG_LIGHT_LOW_INTENSITY;
                break;
        }
    }
}
