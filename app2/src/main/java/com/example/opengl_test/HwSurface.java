package com.example.opengl_test;

import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.util.Log;
import android.view.Surface;

import java.nio.IntBuffer;

public class HwSurface implements SurfaceTexture.OnFrameAvailableListener {

    private static final String TAG = HwSurface.class.getSimpleName();

    private static EGLContext unityContext = EGL14.EGL_NO_CONTEXT;
    private static EGLDisplay unityDisplay = EGL14.EGL_NO_DISPLAY;
    private static EGLSurface unityDrawSurface = EGL14.EGL_NO_SURFACE;
    private static EGLSurface unityReadSurface = EGL14.EGL_NO_SURFACE;

    private int mTextureId;
    private Surface mSurface;
    private SurfaceTexture mSurfaceTexture;
    private boolean mNewFrameAvailable = false;

    public int glCreateExternalTexture() {
        int[] texId = new int[1];
        GLES20.glGenTextures(1, IntBuffer.wrap(texId));
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texId[0]);
//        GLES20.glTexParameteri(
//                GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
//        GLES20.glTexParameteri(
//                GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
//        GLES20.glTexParameteri(
//                GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
//        GLES20.glTexParameteri(
//                GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        //GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);

//        ShaderUtil.checkGlError(TAG, "glCreateExternalTexture");
        return texId[0];
    }

    public int getTextureId() {
        return mTextureId;
    }

    public void createSurface(int width, int height, int texId) {
        unityContext = EGL14.eglGetCurrentContext();
        unityDisplay = EGL14.eglGetCurrentDisplay();
        unityDrawSurface = EGL14.eglGetCurrentSurface(EGL14.EGL_DRAW);
        unityReadSurface = EGL14.eglGetCurrentSurface(EGL14.EGL_READ);

        if (unityContext == EGL14.EGL_NO_CONTEXT) {
            Log.e(TAG, "UnityEGLContext is invalid -> Most probably wrong thread");
        }

        EGL14.eglMakeCurrent(unityDisplay, unityDrawSurface, unityReadSurface, unityContext);

        //mTextureId = glCreateExternalTexture();

        mTextureId = texId;
        if (mTextureId == 0) {
            mTextureId = glCreateExternalTexture();
        }

        Log.d(TAG, "textureid: " + mTextureId);

        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureId);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        mSurfaceTexture = new SurfaceTexture(mTextureId);
        mSurfaceTexture.setDefaultBufferSize(width, height);
        mSurfaceTexture.setOnFrameAvailableListener(this);

        mSurface = new Surface(mSurfaceTexture);

        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);

    }

    public Surface getSurface() {
        return mSurface;
    }

    public void updateTexImage() {
        if (mNewFrameAvailable) {
            if (!Thread.currentThread().getName().equals("UnityMain"))
                Log.e(TAG, "Not called from render thread and hence update texture will fail");
            Log.d(TAG, "updateTexImage");
            mSurfaceTexture.updateTexImage();
            mNewFrameAvailable = false;
        }
    }

    public long getTimestamp() {
        return mSurfaceTexture.getTimestamp();
    }

    public float[] getTransformMatrix() {
        float[] textureTransform = new float[16];
        mSurfaceTexture.getTransformMatrix(textureTransform);
        return textureTransform;
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        mNewFrameAvailable = true;
    }
}