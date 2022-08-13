package com.klinksoftware.rag.walkview;

import com.klinksoftware.rag.utility.*;
import com.klinksoftware.rag.bitmaps.*;
import com.klinksoftware.rag.mesh.*;
import com.klinksoftware.rag.skeleton.Skeleton;
import java.awt.Cursor;
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

    public static final int WV_DISPLAY_RENDER = 0;
    public static final int WV_DISPLAY_COLOR = 1;
    public static final int WV_DISPLAY_NORMAL = 2;
    public static final int WV_DISPLAY_METALLIC_ROUGHNESS = 3;
    public static final int WV_DISPLAY_EMISSIVE = 4;

    private static final float RAG_NEAR_Z=1.0f;
    private static final float RAG_FAR_Z = 1000.0f;
    private static final float RAG_FOV=55.0f;
    private static final long RAG_PAINT_TICK = 33;
    private static final float RAG_LIGHT_LOW_INTENSITY = 100.0f;
    private static final float RAG_LIGHT_HIGH_INTENSITY = 500.0f;

    public int wid, high;
    private int vertexShaderId,fragmentShaderId,programId;
    private int vertexPositionAttribute,vertexUVAttribute, vertexNormalAttribute, vertexTangentAttribute;
    private int perspectiveMatrixUniformId, viewMatrixUniformId, normalMatrixUniformId, lightPositionIntensityUniformId;
    private int displayTypeUniformId, highlightedUniformId, hasEmissiveUniformId, emissiveFactorUniformId;
    private int displayType;
    private long nextPaintTick;
    private float aspectRatio;
    public float cameraRotateDistance;
    private float cameraRotateOffsetY;
    private float currentLightIntensity;
    private boolean cameraCenterRotate, noClear;
    private MeshList meshList, incommingMeshList;
    private Skeleton incommingSkeleton;
    private HashMap<String, BitmapBase> incommingBitmaps;
    public RagPoint cameraAngle;
    public RagPoint eyePoint, cameraPoint, lightEyePoint, lookAtUpVector, fixedLightPoint;
    private RagMatrix4f perspectiveMatrix,viewMatrix,rotMatrix,rotMatrix2;
    private RagMatrix3f normalMatrix;
    private float[] clipPlane;
    private RagPlane frustumLeftPlane, frustumRightPlane, frustumTopPlane, frustumBottomPlane, frustumNearPlane, frustumFarPlane;
    private HashMap<String, WalkViewTexture> textures;
    public WalkViewPhysics physics;
    public WalkViewTrigSort trigSort;

    public WalkView(GLData glData) {
        super(glData);

        setFocusable(true);

        // physics
        physics = new WalkViewPhysics(this);

        // trig sorter of transperent trigs
        trigSort = new WalkViewTrigSort();

        // mouse events
        WalkViewMouseMotionListener mouseMotionListener = new WalkViewMouseMotionListener(this);
        addMouseMotionListener(mouseMotionListener);
        addMouseListener(new WalkViewMouseListener(mouseMotionListener));

        // keyboard events
        addKeyListener(new WalkViewKeyListener(this, mouseMotionListener));

        // move cursor
        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
    }

    // start up and shutdown
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
        rotMatrix2 = new RagMatrix4f();

        clipPlane = new float[16];
        frustumLeftPlane = new RagPlane(0.0f, 0.0f, 0.0f, 0.0f);
        frustumRightPlane = new RagPlane(0.0f, 0.0f, 0.0f, 0.0f);
        frustumTopPlane = new RagPlane(0.0f, 0.0f, 0.0f, 0.0f);
        frustumBottomPlane = new RagPlane(0.0f, 0.0f, 0.0f, 0.0f);
        frustumNearPlane = new RagPlane(0.0f, 0.0f, 0.0f, 0.0f);
        frustumFarPlane = new RagPlane(0.0f, 0.0f, 0.0f, 0.0f);

            // no mesh loaded

        meshList = null;
        textures = null;

        incommingMeshList=null;
        incommingSkeleton=null;
        incommingBitmaps = null;

        displayType = WV_DISPLAY_RENDER;

        noClear = false;

        cameraCenterRotate = false;
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
        normalMatrixUniformId = glGetUniformLocation(programId, "normalMatrix");

        displayTypeUniformId = glGetUniformLocation(programId, "displayType");

        lightPositionIntensityUniformId = glGetUniformLocation(programId, "lightPositionIntensity");

        highlightedUniformId = glGetUniformLocation(programId, "highlighted");

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

    @Override
    public void disposeCanvas() {
    }

    public void doDisposeCanvas() {
        if (vertexShaderId != -1) {
            glDeleteShader(vertexShaderId);
        }
        if (fragmentShaderId != -1) {
            glDeleteShader(fragmentShaderId);
        }
        if (programId != -1) {
            glDeleteProgram(programId);
        }

        super.disposeCanvas();
    }

    //
    // display settings
    //
    public void setLightIntensity(boolean selected) {
        currentLightIntensity = selected ? RAG_LIGHT_HIGH_INTENSITY : RAG_LIGHT_LOW_INTENSITY;
    }

    public void setFlyMode(boolean selected) {
        physics.flyMode = selected;
    }

    public void setDisplayType(int displayType) {
        this.displayType = displayType;
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

        // setup the bounds for culling
        mesh.setGlobalBounds(bonePoint);
    }

    private void releaseMesh(Mesh mesh) {
        glDeleteBuffers(mesh.vboVertexId);
        glDeleteBuffers(mesh.vboUVId);
        glDeleteBuffers(mesh.vboNormalId);
        glDeleteBuffers(mesh.vboTangentId);
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

    public void setCameraWalkView(float x, float y, float z, float feetOffsetY) {
        cameraPoint.setFromValues(x, y, z);
        cameraAngle.setFromValues(0.0f, 0.0f, 0.0f);
        physics.cameraFeetOffsetY = feetOffsetY;
        cameraCenterRotate = false;
        fixedLightPoint = null;
    }

    public void setCameraCenterRotate(float dist, float rotateX, float rotateY, float offsetY, float lightDistance) {
        cameraRotateDistance=dist;
        cameraRotateOffsetY = offsetY;
        cameraAngle.setFromValues(rotateX, rotateY, 0.0f);
        physics.cameraFeetOffsetY = 0.0f;
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

    private WalkViewTexture bufferTexture(BitmapBase bitmapBase) {
        WalkViewTexture texture;

        texture = new WalkViewTexture();
        texture.colorTextureId = loadTexture(bitmapBase.getTextureSize(), bitmapBase.hasAlpha(), bitmapBase.getColorDataAsBytes());

        if (!bitmapBase.hasNormal()) {
            texture.normalTextureId = -1;
        } else {
            texture.normalTextureId = loadTexture(bitmapBase.getTextureSize(), false, bitmapBase.getNormalDataAsBytes());
        }

        if (!bitmapBase.hasMetallicRoughness()) {
            texture.metallicRoughnessTextureId = -1;
        } else {
            texture.metallicRoughnessTextureId = loadTexture(bitmapBase.getTextureSize(), false, bitmapBase.getMetallicRoughnessDataAsBytes());
        }

        if (!bitmapBase.hasEmissive()) {
            texture.emissiveTextureId = -1;
        } else {
            texture.emissiveTextureId = loadTexture(bitmapBase.getTextureSize(), false, bitmapBase.getEmissiveDataAsBytes());
        }

        return (texture);
    }

    private void deleteTexture(WalkViewTexture texture) {
        if (texture.colorTextureId != -1) {
            glDeleteBuffers(texture.colorTextureId);
        }
        if (texture.normalTextureId != -1) {
            glDeleteBuffers(texture.normalTextureId);
        }
        if (texture.metallicRoughnessTextureId != -1) {
            glDeleteBuffers(texture.metallicRoughnessTextureId);
        }
        if (texture.emissiveTextureId != -1) {
            glDeleteBuffers(texture.emissiveTextureId);
        }
    }

    //
    // stage the mesh list
    // this gets triggered during a draw operation when an incomming mesh
    // list has been set
    //

    private void stageMeshList() {
        int n, nMesh, boneIdx;
        Mesh mesh;
        RagPoint tempPnt;

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
                deleteTexture(texture2);
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
            if (!textures.containsKey(mesh.bitmapName)) {
                textures.put(mesh.bitmapName, bufferTexture(incommingBitmaps.get(mesh.bitmapName)));
            }

            mesh.hasAlpha = incommingBitmaps.get(mesh.bitmapName).hasAlpha();
        }

        // unbind any textures
        glBindTexture(GL_TEXTURE_2D, 0);

        // set noClear
        noClear = incommingMeshList.hasSkyBox();

        // setup the collision data
        physics.setupCollision(incommingMeshList, incommingSkeleton);

        // switch to new mesh list and clear incomming
        meshList = incommingMeshList;
        incommingMeshList=null;
        incommingSkeleton=null;
        incommingBitmaps = null;
    }

    //
    // drawing camera/eye setup
    //

    private void setupCameraWalkView() {
        eyePoint.setFromValues(0, 0, -RAG_NEAR_Z);
        rotMatrix.setTranslationFromPoint(cameraPoint);
        rotMatrix2.setRotationFromYAngle(cameraAngle.y);
        rotMatrix.multiply(rotMatrix2);
        rotMatrix2.setRotationFromXAngle(cameraAngle.x);
        rotMatrix.multiply(rotMatrix2);
        eyePoint.matrixMultiply(rotMatrix);
    }

    private void setupCameraCenterRotate() {
        cameraPoint.setFromValues(0.0f, cameraRotateOffsetY, 0.0f);
        eyePoint.setFromValues(0,0,cameraRotateDistance);
        rotMatrix.setTranslationFromPoint(cameraPoint);
        rotMatrix2.setRotationFromYAngle(cameraAngle.y);
        rotMatrix.multiply(rotMatrix2);
        rotMatrix2.setRotationFromXAngle(cameraAngle.x);
        rotMatrix.multiply(rotMatrix2);
        eyePoint.matrixMultiply(rotMatrix);
    }

    private void setupCullingFrustum() {

        // combine the matrixes
        // to build the frustum
        // ABCD planes equations
        clipPlane[0] = (viewMatrix.data[0] * perspectiveMatrix.data[0]) + (viewMatrix.data[1] * perspectiveMatrix.data[4]) + (viewMatrix.data[2] * perspectiveMatrix.data[8]) + (viewMatrix.data[3] * perspectiveMatrix.data[12]);
        clipPlane[1] = (viewMatrix.data[0] * perspectiveMatrix.data[1]) + (viewMatrix.data[1] * perspectiveMatrix.data[5]) + (viewMatrix.data[2] * perspectiveMatrix.data[9]) + (viewMatrix.data[3] * perspectiveMatrix.data[13]);
        clipPlane[2] = (viewMatrix.data[0] * perspectiveMatrix.data[2]) + (viewMatrix.data[1] * perspectiveMatrix.data[6]) + (viewMatrix.data[2] * perspectiveMatrix.data[10]) + (viewMatrix.data[3] * perspectiveMatrix.data[14]);
        clipPlane[3] = (viewMatrix.data[0] * perspectiveMatrix.data[3]) + (viewMatrix.data[1] * perspectiveMatrix.data[7]) + (viewMatrix.data[2] * perspectiveMatrix.data[11]) + (viewMatrix.data[3] * perspectiveMatrix.data[15]);

        clipPlane[4] = (viewMatrix.data[4] * perspectiveMatrix.data[0]) + (viewMatrix.data[5] * perspectiveMatrix.data[4]) + (viewMatrix.data[6] * perspectiveMatrix.data[8]) + (viewMatrix.data[7] * perspectiveMatrix.data[12]);
        clipPlane[5] = (viewMatrix.data[4] * perspectiveMatrix.data[1]) + (viewMatrix.data[5] * perspectiveMatrix.data[5]) + (viewMatrix.data[6] * perspectiveMatrix.data[9]) + (viewMatrix.data[7] * perspectiveMatrix.data[13]);
        clipPlane[6] = (viewMatrix.data[4] * perspectiveMatrix.data[2]) + (viewMatrix.data[5] * perspectiveMatrix.data[6]) + (viewMatrix.data[6] * perspectiveMatrix.data[10]) + (viewMatrix.data[7] * perspectiveMatrix.data[14]);
        clipPlane[7] = (viewMatrix.data[4] * perspectiveMatrix.data[3]) + (viewMatrix.data[5] * perspectiveMatrix.data[7]) + (viewMatrix.data[6] * perspectiveMatrix.data[11]) + (viewMatrix.data[7] * perspectiveMatrix.data[15]);

        clipPlane[8] = (viewMatrix.data[8] * perspectiveMatrix.data[0]) + (viewMatrix.data[9] * perspectiveMatrix.data[4]) + (viewMatrix.data[10] * perspectiveMatrix.data[8]) + (viewMatrix.data[11] * perspectiveMatrix.data[12]);
        clipPlane[9] = (viewMatrix.data[8] * perspectiveMatrix.data[1]) + (viewMatrix.data[9] * perspectiveMatrix.data[5]) + (viewMatrix.data[10] * perspectiveMatrix.data[9]) + (viewMatrix.data[11] * perspectiveMatrix.data[13]);
        clipPlane[10] = (viewMatrix.data[8] * perspectiveMatrix.data[2]) + (viewMatrix.data[9] * perspectiveMatrix.data[6]) + (viewMatrix.data[10] * perspectiveMatrix.data[10]) + (viewMatrix.data[11] * perspectiveMatrix.data[14]);
        clipPlane[11] = (viewMatrix.data[8] * perspectiveMatrix.data[3]) + (viewMatrix.data[9] * perspectiveMatrix.data[7]) + (viewMatrix.data[10] * perspectiveMatrix.data[11]) + (viewMatrix.data[11] * perspectiveMatrix.data[15]);

        clipPlane[12] = (viewMatrix.data[12] * perspectiveMatrix.data[0]) + (viewMatrix.data[13] * perspectiveMatrix.data[4]) + (viewMatrix.data[14] * perspectiveMatrix.data[8]) + (viewMatrix.data[15] * perspectiveMatrix.data[12]);
        clipPlane[13] = (viewMatrix.data[12] * perspectiveMatrix.data[1]) + (viewMatrix.data[13] * perspectiveMatrix.data[5]) + (viewMatrix.data[14] * perspectiveMatrix.data[9]) + (viewMatrix.data[15] * perspectiveMatrix.data[13]);
        clipPlane[14] = (viewMatrix.data[12] * perspectiveMatrix.data[2]) + (viewMatrix.data[13] * perspectiveMatrix.data[6]) + (viewMatrix.data[14] * perspectiveMatrix.data[10]) + (viewMatrix.data[15] * perspectiveMatrix.data[14]);
        clipPlane[15] = (viewMatrix.data[12] * perspectiveMatrix.data[3]) + (viewMatrix.data[13] * perspectiveMatrix.data[7]) + (viewMatrix.data[14] * perspectiveMatrix.data[11]) + (viewMatrix.data[15] * perspectiveMatrix.data[15]);

        // left plane
        frustumLeftPlane.a = clipPlane[3] + clipPlane[0];
        frustumLeftPlane.b = clipPlane[7] + clipPlane[4];
        frustumLeftPlane.c = clipPlane[11] + clipPlane[8];
        frustumLeftPlane.d = clipPlane[15] + clipPlane[12];
        frustumLeftPlane.normalize();

        // right plane
        frustumRightPlane.a = clipPlane[3] - clipPlane[0];
        frustumRightPlane.b = clipPlane[7] - clipPlane[4];
        frustumRightPlane.c = clipPlane[11] - clipPlane[8];
        frustumRightPlane.d = clipPlane[15] - clipPlane[12];
        frustumRightPlane.normalize();

        // top plane
        frustumTopPlane.a = clipPlane[3] - clipPlane[1];
        frustumTopPlane.b = clipPlane[7] - clipPlane[5];
        frustumTopPlane.c = clipPlane[11] - clipPlane[9];
        frustumTopPlane.d = clipPlane[15] - clipPlane[13];
        frustumTopPlane.normalize();

        // bottom plane
        frustumBottomPlane.a = clipPlane[3] + clipPlane[1];
        frustumBottomPlane.b = clipPlane[7] + clipPlane[5];
        frustumBottomPlane.c = clipPlane[11] + clipPlane[9];
        frustumBottomPlane.d = clipPlane[15] + clipPlane[13];
        frustumBottomPlane.normalize();

        // near plane
        frustumNearPlane.a = clipPlane[3] + clipPlane[2];
        frustumNearPlane.b = clipPlane[7] + clipPlane[6];
        frustumNearPlane.c = clipPlane[11] + clipPlane[10];
        frustumNearPlane.d = clipPlane[15] + clipPlane[14];
        frustumNearPlane.normalize();

        // far plane
        frustumFarPlane.a = clipPlane[3] - clipPlane[2];
        frustumFarPlane.b = clipPlane[7] - clipPlane[6];
        frustumFarPlane.c = clipPlane[11] - clipPlane[10];
        frustumFarPlane.d = clipPlane[15] - clipPlane[14];
        frustumFarPlane.normalize();
    }

    private boolean boundBoxInFrustum(RagBound xBound, RagBound yBound, RagBound zBound)    {
            // check if outside the plane, if it is,
            // then it's considered outside the bounds

        if (!frustumLeftPlane.boundBoxOutsidePlane(xBound, yBound, zBound)) {
            return (false);
        }
        if (!frustumRightPlane.boundBoxOutsidePlane(xBound, yBound, zBound)) {
            return (false);
        }
        if (!frustumTopPlane.boundBoxOutsidePlane(xBound, yBound, zBound)) {
            return (false);
        }
        if (!frustumBottomPlane.boundBoxOutsidePlane(xBound, yBound, zBound)) {
            return (false);
        }
        if (!frustumNearPlane.boundBoxOutsidePlane(xBound, yBound, zBound)) {
            return (false);
        }
        if (!frustumFarPlane.boundBoxOutsidePlane(xBound, yBound, zBound)) {
            return (false);
        }

            // otherwise considered within the frustum planes

        return(true);
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
    // cosine glow
    //
    private float getEmissiveFactor(long tick) {
        float f;

        tick = tick % 2000;
        f = (float) Math.cos((2.0 * Math.PI) * ((double) tick / 2000.0));
        return ((f * 0.4f) + 0.4f);
    }

    //
    // drawing utilities
    //
    private void switchTexture(WalkViewTexture texture, long tick) {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture.colorTextureId);
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, texture.normalTextureId);
        glActiveTexture(GL_TEXTURE2);
        glBindTexture(GL_TEXTURE_2D, texture.metallicRoughnessTextureId);

        glUniform1i(highlightedUniformId, (texture.normalTextureId != -1) ? 0 : 1);

        if (texture.emissiveTextureId == -1) {
            glUniform1i(hasEmissiveUniformId, 0);
            glUniform1f(emissiveFactorUniformId, 0.0f);
        } else {
            glUniform1i(hasEmissiveUniformId, 1);
            glUniform1f(emissiveFactorUniformId, getEmissiveFactor(tick));
            glActiveTexture(GL_TEXTURE3);
            glBindTexture(GL_TEXTURE_2D, texture.emissiveTextureId);
        }
    }

    private void setupMeshBuffers(Mesh mesh) {
        glBindBuffer(GL_ARRAY_BUFFER, mesh.vboVertexId);
        glVertexAttribPointer(vertexPositionAttribute, 3, GL_FLOAT, false, 0, 0);

        glBindBuffer(GL_ARRAY_BUFFER, mesh.vboUVId);
        glVertexAttribPointer(vertexUVAttribute, 2, GL_FLOAT, false, 0, 0);

        glBindBuffer(GL_ARRAY_BUFFER, mesh.vboNormalId);
        glVertexAttribPointer(vertexNormalAttribute, 3, GL_FLOAT, false, 0, 0);

        glBindBuffer(GL_ARRAY_BUFFER, mesh.vboTangentId);
        glVertexAttribPointer(vertexTangentAttribute, 3, GL_FLOAT, false, 0, 0);
    }

    //
    // draw the scene
    //

    @Override
    public void paintGL() {
        int n, nMesh, trigIndexIdx;
        long tick;
        FloatBuffer buf;
        IntBuffer intBuf;
        Mesh mesh, curMesh;
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

        // physics
        physics.run(cameraCenterRotate);

        // set the camera
        if (!cameraCenterRotate) {
            setupCameraWalkView();
        }
        else {
            setupCameraCenterRotate();
        }

        // clear
        glClear(noClear ? GL_DEPTH_BUFFER_BIT : GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

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
            glUniform4fv(lightPositionIntensityUniformId, buf);

            glUniform1i(displayTypeUniformId, displayType);
        }

        // setup culling frustum
        setupCullingFrustum();

        // draw the opaque meshes
        curTexture=null;

        nMesh = meshList.count();

        for (n = 0; n != nMesh; n++) {
            mesh = meshList.get(n);
            if (mesh.hasAlpha) {
                continue;
            }

            // culled?
            if (!boundBoxInFrustum(mesh.xBound, mesh.yBound, mesh.zBound)) {
                continue;
            }

            // new texture?
            texture = textures.get(mesh.bitmapName);
            if (texture!=curTexture) {
                curTexture = texture;
                switchTexture(texture, tick);
            }

            // draw the mesh
            setupMeshBuffers(mesh);

            intBuf = MemoryUtil.memAllocInt(mesh.indexes.length);
            intBuf.put(mesh.indexes).flip();

            glDrawElements(GL_TRIANGLES, intBuf);

            memFree(intBuf);
        }

        // add transparent trigs to sortable array
        trigSort.clearTrigs();

        for (n = 0; n != nMesh; n++) {
            mesh = meshList.get(n);
            if (!mesh.hasAlpha) {
                continue;
            }

            // culled?
            if (!boundBoxInFrustum(mesh.xBound, mesh.yBound, mesh.zBound)) {
                continue;
            }

            // add to sort array
            trigSort.addTrigsFromMesh(meshList, cameraPoint, n);
        }

        // draw the transparent meshes
        curTexture = null;
        curMesh = null;

        glEnable(GL_BLEND);
        glDepthMask(false);

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        for (n = 0; n != trigSort.trigCount; n++) {
            mesh = meshList.get(trigSort.trigArray[n].meshIdx);

            // new mesh?
            if (curMesh != mesh) {
                curMesh = mesh;
                setupMeshBuffers(mesh);

                // new texture?
                texture = textures.get(mesh.bitmapName);
                if (texture != curTexture) {
                    curTexture = texture;
                    switchTexture(texture, tick);
                }
            }

            // draw the trig
            trigIndexIdx = trigSort.trigArray[n].trigIdx * 3;

            intBuf = MemoryUtil.memAllocInt(3);
            intBuf.put(mesh.indexes[trigIndexIdx++]).put(mesh.indexes[trigIndexIdx++]).put(mesh.indexes[trigIndexIdx++]).flip();

            glDrawElements(GL_TRIANGLES, intBuf);

            memFree(intBuf);
        }

        glDepthMask(true);
        glDisable(GL_BLEND);

        // shutdown drawing
        glBindBuffer(GL_ARRAY_BUFFER,0);
        glBindTexture(GL_TEXTURE_2D,0);
        glUseProgram(0);

        // swap the buffers
        swapBuffers();
    }

}
