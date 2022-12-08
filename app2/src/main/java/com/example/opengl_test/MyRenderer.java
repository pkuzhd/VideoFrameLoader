package com.example.opengl_test;

import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glBindTexture;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLES32;
import android.opengl.GLSurfaceView;

import com.example.gettexture.GetTexture;
import com.netvideo.libvideo.VideoFrameLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyRenderer implements GLSurfaceView.Renderer {
    float[] triangleCoords = {
            0.0f, 1.00f, 0.0f, // top
            -1.00f, -1.00f, 0.0f, // bottom left
            1.00f, -1.00f, 0.0f  // bottom right
    };

    float[] color = {1.0f, 0f, 0f, 1.0f}; //red
    private FloatBuffer vertexBuffer;
    private int mProgram;
    private int mScreenProgram;

    private int cameraFBO = -1;
    private int cameraTexture = -1;
    private int cameraDepth = -1;
    private int accumulateFBO = -1;
    private int accumulateTexture = -1;
    private int VAO = -1;
    private int VBO = -1;
    private int EBO = -1;
    private int VAO2 = -1;
    private int VBO2 = -1;
    private int EBO2 = -1;
    private int VAO3 = -1;
    private int VBO3 = -1;
    private int EBO3 = -1;
    private int mTexture = -1;

    private int[] textures = new int[5];
    private String filename = "rtmp://222.29.111.196:1935/live/rgb";
    private VideoFrameLoader video = null;

    public void glCreateExternalTexture() {
        int width = 2000;
        int height = 1600;
        int[] tmp = new int[1];


        if (cameraFBO == -1) {
            GLES32.glGenFramebuffers(1, IntBuffer.wrap(tmp));
            cameraFBO = tmp[0];
            GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, cameraFBO);

            GLES32.glGenTextures(1, IntBuffer.wrap(tmp));
            cameraTexture = tmp[0];
            GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, cameraTexture);
            GLES32.glTexImage2D(
                    GLES32.GL_TEXTURE_2D,
                    0, // level
                    GLES32.GL_RGBA,
                    width,
                    height,
                    0, // border must be 0
                    GLES32.GL_RGBA,
                    GLES32.GL_UNSIGNED_BYTE,
                    null); // no pixel data
            GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MAG_FILTER, GLES32.GL_NEAREST);
            GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MIN_FILTER, GLES32.GL_NEAREST);

            GLES32.glFramebufferTexture2D(GLES32.GL_FRAMEBUFFER,
                    GLES32.GL_COLOR_ATTACHMENT0, GLES32.GL_TEXTURE_2D, cameraTexture, 0);

            GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, 0);
            GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, 0);


            GLES32.glGenRenderbuffers(1, IntBuffer.wrap(tmp));
            cameraDepth = tmp[0];
            GLES32.glBindRenderbuffer(GLES32.GL_RENDERBUFFER, cameraDepth);
            GLES32.glRenderbufferStorage(GLES32.GL_RENDERBUFFER, GLES32.GL_DEPTH_COMPONENT, width, height);
            GLES32.glBindRenderbuffer(GLES32.GL_RENDERBUFFER, 0);

            GLES32.glFramebufferRenderbuffer(GLES32.GL_FRAMEBUFFER,
                    GLES32.GL_DEPTH_ATTACHMENT, GLES32.GL_RENDERBUFFER, cameraDepth);

            GLES32.glGenFramebuffers(1, IntBuffer.wrap(tmp));
            accumulateFBO = tmp[0];
            GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, accumulateFBO);

            GLES32.glGenTextures(1, IntBuffer.wrap(tmp));
            accumulateTexture = tmp[0];
            GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, accumulateTexture);
            GLES32.glTexImage2D(
                    GLES32.GL_TEXTURE_2D,
                    0, // level
                    GLES32.GL_RGBA32F,
                    width,
                    height,
                    0, // border must be 0
                    GLES32.GL_RGBA,
                    GLES32.GL_BYTE,
                    null); // no pixel data
            GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MAG_FILTER, GLES32.GL_NEAREST);
            GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MIN_FILTER, GLES32.GL_NEAREST);
            GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, 0);
            GLES32.glFramebufferTexture2D(GLES32.GL_FRAMEBUFFER,
                    GLES32.GL_COLOR_ATTACHMENT0, GLES32.GL_TEXTURE_2D, accumulateTexture, 0);
            GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, 0);

            float[] vertices = {
                    1.00f, 1.00f, 0.0f, // top right
                    1.00f, -1.00f, 0.0f,  // bottom right
                    -1.00f, -1.00f, 0.0f, // bottom left
                    -1.00f, 1.00f, 0.0f, // top left
            };
            float[] vertices2 = {
                    1.0f, 1.0f, 0.0f, // top right
                    1.0f, -1.0f, 0.0f,  // bottom right
                    -1.0f, -1.0f, 0.0f, // bottom left
                    -1.0f, 1.0f, 0.0f, // top left
            };

            int[] indices = {
                    0, 1, 3,
                    1, 2, 3
            };

            {
                GLES32.glGenVertexArrays(1, IntBuffer.wrap(tmp));
                VAO = tmp[0];
                GLES32.glGenBuffers(1, IntBuffer.wrap(tmp));
                VBO = tmp[0];
                GLES32.glGenBuffers(1, IntBuffer.wrap(tmp));
                EBO = tmp[0];
                GLES32.glBindVertexArray(VAO);
                GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, VBO);
                GLES32.glBufferData(GLES32.GL_ARRAY_BUFFER,
                        vertices.length * 4, BufferUtil.fBuffer(vertices), GLES32.GL_STATIC_DRAW);
                GLES32.glBindBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER, EBO);
                GLES32.glBufferData(GLES32.GL_ELEMENT_ARRAY_BUFFER,
                        indices.length * 4, BufferUtil.iBuffer(indices), GLES32.GL_STATIC_DRAW);

                GLES32.glVertexAttribPointer(0,
                        3, GLES32.GL_FLOAT, false, 3 * 4, 0);
                GLES32.glEnableVertexAttribArray(0);

                GLES32.glBindVertexArray(0);
//                GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, 0);
//                GLES32.glBindBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER, 0);
            }

            {
                GLES32.glGenVertexArrays(1, IntBuffer.wrap(tmp));
                VAO2 = tmp[0];
                GLES32.glGenBuffers(1, IntBuffer.wrap(tmp));
                VBO2 = tmp[0];
                GLES32.glGenBuffers(1, IntBuffer.wrap(tmp));
                EBO2 = tmp[0];
                GLES32.glBindVertexArray(VAO2);
                GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, VBO2);
                GLES32.glBufferData(GLES32.GL_ARRAY_BUFFER,
                        vertices.length * 4, BufferUtil.fBuffer(vertices2), GLES32.GL_STATIC_DRAW);
                GLES32.glBindBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER, EBO2);
                GLES32.glBufferData(GLES32.GL_ELEMENT_ARRAY_BUFFER,
                        indices.length * 4, BufferUtil.iBuffer(indices), GLES32.GL_STATIC_DRAW);

                GLES32.glVertexAttribPointer(0, 3,
                        GLES32.GL_FLOAT, false, 3 * 4, 0);
                GLES32.glEnableVertexAttribArray(0);

                GLES32.glBindVertexArray(0);
//                GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, 0);
//                GLES32.glBindBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER, 0);
            }
        }
//        GLES32.glTexParameteri(
//                GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES32.GL_TEXTURE_MIN_FILTER, GLES32.GL_NEAREST);
//        GLES32.glTexParameteri(
//                GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES32.GL_TEXTURE_MAG_FILTER, GLES32.GL_NEAREST);
//        GLES32.glTexParameteri(
//                GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES32.GL_TEXTURE_WRAP_S, GLES32.GL_CLAMP_TO_EDGE);
//        GLES32.glTexParameteri(
//                GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES32.GL_TEXTURE_WRAP_T, GLES32.GL_CLAMP_TO_EDGE);

        //GLES32.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);

//        ShaderUtil.checkGlError(TAG, "glCreateExternalTexture");

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        video = new VideoFrameLoader(filename, filename, 2);
        for (int i = 0; i < 5; ++i) {
            textures[i] = video.getTextureId(i);
        }
        video.run();


        //将背景设置为灰色
        GLES32.glClearColor(1.00f, 1.00f, 1.00f, 1.0f);
        //将坐标数据转换为FloatBuffer，用以传入给OpenGL ES程序
        vertexBuffer = BufferUtil.fBuffer(triangleCoords);

        String vertexShaderCode = MainActivity.readAssertResource("tri.vert");
        String fragmentShaderCode = MainActivity.readAssertResource("tri.frag");

        int vertexShader = loadShader(GLES32.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES32.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES32.glCreateProgram();
        GLES32.glAttachShader(mProgram, vertexShader);
        GLES32.glAttachShader(mProgram, fragmentShader);
        GLES32.glLinkProgram(mProgram);

        mScreenProgram = GLES32.glCreateProgram();
        GLES32.glAttachShader(mScreenProgram,
                loadShader(GLES32.GL_VERTEX_SHADER,
                        MainActivity.readAssertResource("screen.vert")));
        GLES32.glAttachShader(mScreenProgram,
                loadShader(GLES32.GL_FRAGMENT_SHADER,
                        MainActivity.readAssertResource("screen.frag")));
        GLES32.glLinkProgram(mScreenProgram);

        glCreateExternalTexture();
        mTexture = MainActivity.loadBitmapTexture(R.mipmap.test);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES32.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        int mPositionHandle;

        // render triangle
        {
            GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, cameraFBO);
//            GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, 0);
            GLES32.glClearColor(1.00f, 1.00f, 1.00f, 1.0f);
            GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT);

            // 将程序加入到OpenGLES2.0环境(加载)
            GLES32.glEnable(GLES32.GL_BLEND);
            GLES32.glDisable(GLES32.GL_DEPTH);

            GLES32.glUseProgram(mProgram);

            mPositionHandle = GLES32.glGetAttribLocation(mProgram, "vPosition");
            GLES32.glBindVertexArray(VAO);
//            GLES32.glEnableVertexAttribArray(mPositionHandle);

            GLES32.glUniform4fv(GLES32.glGetUniformLocation(mProgram, "vColor"), 1, color, 0);
//            GLES32.glDrawArrays(GLES32.GL_TRIANGLES, 1, 3);
            GLES32.glDrawElements(GLES32.GL_TRIANGLES, 6, GLES32.GL_UNSIGNED_INT, 0);
            GLES32.glUseProgram(0);
        }

        {
//            GetTexture.draw();

//            GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, GetTexture.getFrameId2());
////            GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, 0);
//            GLES32.glClearColor(1.00f, 1.00f, 1.00f, 1.0f);
//            GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT);
//
//            // 将程序加入到OpenGLES2.0环境(加载)
//            GLES32.glEnable(GLES32.GL_BLEND);
//            GLES32.glDisable(GLES32.GL_DEPTH);
//
//            GLES32.glUseProgram(GetTexture.mProgram);
//
//            GLES32.glBindVertexArray(GetTexture.VAO);
//
//            GLES32.glDrawElements(GLES32.GL_TRIANGLES, 6, GLES32.GL_UNSIGNED_INT, 0);
//            GLES32.glUseProgram(0);
        }

//        video.update();
        video.update();
        {
            GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, 0);
//            GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, cameraFBO);

            GLES32.glClearColor(0.0f, 1.00f, 0.0f, 1.0f);
            GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT);

            GLES32.glUseProgram(mScreenProgram);

            mPositionHandle = GLES32.glGetAttribLocation(mScreenProgram, "vPosition");
            GLES32.glBindVertexArray(VAO2);
//            GLES32.glEnableVertexAttribArray(mPositionHandle);

            GLES32.glUniform1i(GLES32.glGetUniformLocation(mScreenProgram, "yTexture"), 0);
            GLES32.glUniform1i(GLES32.glGetUniformLocation(mScreenProgram, "uTexture"), 1);
            GLES32.glUniform1i(GLES32.glGetUniformLocation(mScreenProgram, "vTexture"), 2);
            GLES32.glUniform1i(GLES32.glGetUniformLocation(mScreenProgram, "depthTexture"), 3);
            GLES32.glUniform1i(GLES32.glGetUniformLocation(mScreenProgram, "maskTexture"), 4);
            for (int i = 0; i < 5; ++i) {
                GLES32.glActiveTexture(GLES32.GL_TEXTURE0 + i);
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textures[i]);
            }

//            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTexture);
//            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, cameraTexture);
//            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, GetTexture.getTexId());

            GLES32.glDrawElements(GLES32.GL_TRIANGLES, 6, GLES32.GL_UNSIGNED_INT, 0);

        }
        // end


////        GLES32.glEnableVertexAttribArray(mPositionHandle);
//
////        FloatBuffer vertexBuffer_2 = BufferUtil.fBuffer(triangleCoords2);
////
////        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, 0);
////        GLES32.glUseProgram(mScreenProgram);
////        GLES32.glBindVertexArray(VAO);
////        GLES32.glDrawArrays(GLES32.GL_TRIANGLES, 0, vertexCount);
//////        GLES32.glDrawElements(GLES32.GL_TRIANGLES, 6, GLES32.GL_INT, 0);
////        GLES32.glBindVertexArray(0);
//        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, accumulateFBO);
//        GLES32.glClearColor(0.0f, 1.00f, 0.0f, 1.0f);
//        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT);
//
//        GLES32.glUseProgram(mScreenProgram);
//        mPositionHandle = GLES32.glGetAttribLocation(mScreenProgram, "vPosition");
//        GLES32.glEnableVertexAttribArray(mPositionHandle);
////        GLES32.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
////                GLES32.GL_FLOAT, false,
////                vertexStride, vertexBuffer);
////        GLES32.glUniform1i(GLES32.glGetAttribLocation(mScreenProgram, "screenTexture"), 0);
////        GLES32.glBindVertexArray(VAO);
////        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, cameraTexture);
//        GLES32.glDrawArrays(GLES32.GL_TRIANGLES, 0, 3);
////        GLES32.glBindVertexArray(0);
//
//        GLES32.glDisableVertexAttribArray(mPositionHandle);
    }

    public int loadShader(int type, String shaderCode) {
        // 根据type创建顶点着色器或者片元着色器
        int shader = GLES32.glCreateShader(type);
        // 将资源加入到着色器中，并编译
        GLES32.glShaderSource(shader, shaderCode);
        GLES32.glCompileShader(shader);
        return shader;
    }
}
