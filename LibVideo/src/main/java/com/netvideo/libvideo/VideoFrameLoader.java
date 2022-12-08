package com.netvideo.libvideo;

import android.opengl.GLES32;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_LUMINANCE;
import static android.opengl.GLES20.GL_NO_ERROR;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glGetError;
import static android.opengl.GLES20.glTexImage2D;
import static android.opengl.GLES20.glTexParameteri;

public class VideoFrameLoader {
    static {
        System.loadLibrary("video");
    }

    public static final int TEXTURE_Y = 0;
    public static final int TEXTURE_U = 1;
    public static final int TEXTURE_V = 2;
    public static final int TEXTURE_DEPTH = 3;
    public static final int TEXTURE_MASK = 4;

    private int type = 1;
    private long nativePtr;
    private int[] textureId;

    private native long nInit(String videoFilename, String depthFilename);

    private native long nInit2(String videoFilename, String depthFilename);

    private native void nRun(long ptr);

    private native int nUpdate(long ptr);

    private native int nUpdate2(long ptr, int type);

    private native int nGetInt(long ptr, String name, int idx);

    private native int nGetByteBuffer(long ptr, int idx, ByteBuffer buffer);

    private native int nGetTextureID(long ptr, int textureType);

    private native double nGetNextPTS(long ptr);

    private native double nGetLastPTS(long ptr);

    public VideoFrameLoader(String videoFilename, String depthFilename) {
        this.nativePtr = nInit(videoFilename, depthFilename);
    }

    public VideoFrameLoader(String videoFilename, String depthFilename, int type) {
        this.type = type;
        if (type == 1)
            this.nativePtr = nInit(videoFilename, depthFilename);
        else if (type == 2) {
            this.nativePtr = nInit2(videoFilename, depthFilename);
            textureId = new int[5];
            glGenTextures(5, IntBuffer.wrap(textureId));
            int error = glGetError();
            if (error != GL_NO_ERROR) {
                Log.e("GLES", "[zhd] opengl error: " + error);
            }
            if (textureId[0] == 0) {
                Log.e("GLES", "Could not generate a new OpenGL texture object");
            }

            for (int i = 0; i < 5; ++i) {
                glBindTexture(GL_TEXTURE_2D, textureId[i]);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
                glBindTexture(GL_TEXTURE_2D, 0);
            }
        }
    }

    public void run() {
        nRun(nativePtr);
    }

    public int getTextureId(int textureType) {
        if (type == 1) {
            return nGetTextureID(nativePtr, textureType);
        } else if (type == 2) {
            return textureId[textureType];
        }
        return -1;
    }

    public double getNextPTS() {
        return nGetNextPTS(nativePtr);
    }

    public double getLastPTS() {
        return nGetLastPTS(nativePtr);
    }

    public int update() {
        if (type == 1) {
            return nUpdate(nativePtr);
        } else if (type == 2) {
            int ret = nUpdate2(nativePtr, 0);
            if (ret == 0)
                return 0;

            for (int i = 0; i < 3; ++i) {
                int idx = i;
                int width = nGetInt(nativePtr, "width", idx);
                int height = nGetInt(nativePtr, "height", idx);

                ByteBuffer data = ByteBuffer.allocateDirect(width * height);
                nGetByteBuffer(nativePtr, idx, data);
                data.flip();

                glBindTexture(GL_TEXTURE_2D, textureId[idx]);

                glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE,
                        width, height, 0, GL_LUMINANCE, GL_UNSIGNED_BYTE, data);
                glGenerateMipmap(GL_TEXTURE_2D);
                glBindTexture(GL_TEXTURE_2D, 0);
            }

            for (int i = 0; i < 2; ++i) {
                int idx = 3 + i;
                int width = nGetInt(nativePtr, "width", idx);
                int height = nGetInt(nativePtr, "height", idx);

                ByteBuffer data = ByteBuffer.allocateDirect(width * height);
                nGetByteBuffer(nativePtr, idx, data);
                data.flip();

                glBindTexture(GL_TEXTURE_2D, textureId[idx]);

                glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE,
                        width, height, 0, GL_LUMINANCE, GL_UNSIGNED_BYTE, data);
                glGenerateMipmap(GL_TEXTURE_2D);
                glBindTexture(GL_TEXTURE_2D, 0);
            }

            nUpdate2(nativePtr, 1);
            return 1;
        }
        return -1;
    }
}
