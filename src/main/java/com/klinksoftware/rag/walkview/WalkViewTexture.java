package com.klinksoftware.rag.walkview;

import com.klinksoftware.rag.bitmaps.BitmapBase;
import java.nio.ByteBuffer;
import static org.lwjgl.opengl.ARBFramebufferObject.glGenerateMipmap;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import org.lwjgl.system.MemoryUtil;

public class WalkViewTexture {
    public int colorTextureId;
    public int normalTextureId;
    public int metallicRoughnessTextureId;
    public int emissiveTextureId;
    public boolean hasAlpha;

    private static int loadTexture(int textureSize, boolean hasAlpha, byte[] textureData) {
        int textureId;
        ByteBuffer bitmapBuf;

        if (textureData == null) {
            return (-1);
        }

        bitmapBuf = MemoryUtil.memAlloc((textureSize * (hasAlpha ? 4 : 3)) * textureSize);
        bitmapBuf.put(textureData).flip();

        textureId = glGenTextures();
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureId);
        glTexImage2D(GL_TEXTURE_2D, 0, (hasAlpha ? GL_RGBA : GL_RGB), textureSize, textureSize, 0, (hasAlpha ? GL_RGBA : GL_RGB), GL_UNSIGNED_BYTE, bitmapBuf);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glGenerateMipmap(GL_TEXTURE_2D);

        MemoryUtil.memFree(bitmapBuf);

        return (textureId);
    }

    public static WalkViewTexture createTexture(BitmapBase bitmapBase) {
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

        texture.hasAlpha = bitmapBase.hasAlpha();

        return (texture);
    }

    public void deleteTexture() {
        if (colorTextureId != -1) {
            glDeleteBuffers(colorTextureId);
        }
        if (normalTextureId != -1) {
            glDeleteBuffers(normalTextureId);
        }
        if (metallicRoughnessTextureId != -1) {
            glDeleteBuffers(metallicRoughnessTextureId);
        }
        if (emissiveTextureId != -1) {
            glDeleteBuffers(emissiveTextureId);
        }
    }
}
