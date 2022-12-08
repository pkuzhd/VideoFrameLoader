package com.example.gettexture;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLES32;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class GetTexture {
    public static final String TAG = "getTexture";

    public static int texId = -1;
    public static int frameId2 = -1;
    public static int texId2 = -1;
    public static int mProgram = -1;
    public static int VAO = -1;


    public static int get1024() {
        return 1024;
    }


    public static int glCreateExternalTexture() {
        int[] texId = new int[1];
        GLES32.glGenTextures(1, IntBuffer.wrap(texId));
        int error = GLES32.glGetError();
        if (error != GLES32.GL_NO_ERROR) {
            Log.e(TAG, "[zhd] opengl error" + Integer.toHexString(error));
            throw new RuntimeException("[zhd] opengl error" + Integer.toHexString(error));
        }
        GLES32.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texId[0]);
//        GLES20.glTexParameteri(
//                GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
//        GLES20.glTexParameteri(
//                GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
//        GLES20.glTexParameteri(
//                GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
//        GLES20.glTexParameteri(
//                GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        //GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        return texId[0];
    }

    public static void update1() {

        byte[] tmp = new byte[120000];
        for (int i = 0; i < 100; ++i) {
            for (int j = 0; j < 100; ++j) {
                tmp[i * 200 * 3 + j * 3] = (byte) 0x00;
                tmp[i * 200 * 3 + j * 3 + 1] = (byte) 0xff;
                tmp[i * 200 * 3 + j * 3 + 2] = (byte) 0x00;
            }
        }

        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, texId);
        GLES32.glTexImage2D(GLES32.GL_TEXTURE_2D, 0, GLES32.GL_RGB,
                200, 200, 0, GLES32.GL_RGB, GLES32.GL_UNSIGNED_BYTE, ByteBuffer.wrap(tmp));
        GLES32.glGenerateMipmap(GLES32.GL_TEXTURE_2D);
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, 0);
    }

    public static void update2() {

        byte[] tmp = new byte[120000];
        for (int i = 0; i < 100; ++i) {
            for (int j = 0; j < 100; ++j) {
                tmp[i * 200 * 3 + j * 3] = (byte) 0xff;
                tmp[i * 200 * 3 + j * 3 + 1] = (byte) 0x00;
                tmp[i * 200 * 3 + j * 3 + 2] = (byte) 0x00;
            }
        }

        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, texId);
        GLES32.glTexImage2D(GLES32.GL_TEXTURE_2D, 0, GLES32.GL_RGB,
                200, 200, 0, GLES32.GL_RGB, GLES32.GL_UNSIGNED_BYTE, ByteBuffer.wrap(tmp));
        GLES32.glGenerateMipmap(GLES32.GL_TEXTURE_2D);
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, 0);
    }

    public static int getTexId() {
        if (texId != -1)
            return texId;
        final int[] textureId = new int[1];
        GLES32.glGenTextures(1, textureId, 0);
        int error = GLES32.glGetError();
//        if (error != GLES32.GL_NO_ERROR) {
//            Log.e(TAG, "[zhd] opengl error" + Integer.toHexString(error));
//            throw new RuntimeException("[zhd] opengl error" + Integer.toHexString(error));
//        }
        if (textureId[0] == 0) {
            Log.e(TAG, "Could not generate a new OpenGL texture object");
            return 0;
        }

        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, textureId[0]);
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MIN_FILTER, GLES32.GL_LINEAR_MIPMAP_LINEAR);
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MAG_FILTER, GLES32.GL_LINEAR);
//        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_SWIZZLE_G, GLES32.GL_RED);
//        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_SWIZZLE_B, GLES32.GL_RED);


        byte[] tmp = new byte[120000];
        for (int i = 0; i < 75; ++i) {
            for (int j = 0; j < 75; ++j) {
                tmp[i * 200 * 3 + j * 3] = (byte) 0xff;
                tmp[i * 200 * 3 + j * 3 + 1] = (byte) 0x00;
                tmp[i * 200 * 3 + j * 3 + 2] = (byte) 0x00;
            }
        }
        byte[] tmp2 = new byte[40000];

        for (int i = 0; i < 100; ++i) {
            for (int j = 0; j < 100; ++j) {
                tmp2[i * 200 + j] = (byte) 0xef;
            }
        }
//        GLES32.glPixelStorei(GLES32.GL_UNPACK_ALIGNMENT, 1);

        GLES32.glTexImage2D(GLES32.GL_TEXTURE_2D, 0, GLES32.GL_RED,
                200, 200, 0, GLES32.GL_RED, GLES32.GL_UNSIGNED_BYTE, ByteBuffer.wrap(tmp2));
        GLES32.glGenerateMipmap(GLES32.GL_TEXTURE_2D);
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, 0);
        texId = textureId[0];
        return textureId[0];
    }

    public static void draw() {
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, GetTexture.getFrameId2());
//            GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, 0);
        GLES32.glClearColor(1.00f, 1.00f, 1.00f, 1.0f);
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT);

        // 将程序加入到OpenGLES2.0环境(加载)
        GLES32.glEnable(GLES32.GL_BLEND);
        GLES32.glDisable(GLES32.GL_DEPTH);

        GLES32.glUseProgram(GetTexture.mProgram);

//        mPositionHandle = GLES32.glGetAttribLocation(mProgram, "vPosition");
        GLES32.glBindVertexArray(VAO);
//            GLES32.glEnableVertexAttribArray(mPositionHandle);

//            GLES32.glDrawArrays(GLES32.GL_TRIANGLES, 1, 3);
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, 6, GLES32.GL_UNSIGNED_INT, 0);
        GLES32.glUseProgram(0);
    }

    public static void init2(int width, int height) {
        if (frameId2 != -1)
            return;
        final int[] tmp = new int[1];

        {
            float[] vertices = {
                    1.00f, 1.00f, 0.0f, // top right
                    1.00f, -1.00f, 0.0f,  // bottom right
                    -1.00f, -1.00f, 0.0f, // bottom left
                    -1.00f, 1.00f, 0.0f, // top left
            };

            int[] indices = {
                    0, 1, 3,
                    1, 2, 3
            };

            {
                GLES32.glGenVertexArrays(1, IntBuffer.wrap(tmp));
                GetTexture.VAO = tmp[0];
                GLES32.glGenBuffers(1, IntBuffer.wrap(tmp));
                int VBO = tmp[0];
                GLES32.glGenBuffers(1, IntBuffer.wrap(tmp));
                int EBO = tmp[0];
                GLES32.glBindVertexArray(GetTexture.VAO);
                GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, VBO);
                GLES32.glBufferData(GLES32.GL_ARRAY_BUFFER,
                        vertices.length * 4, FloatBuffer.wrap(vertices), GLES32.GL_STATIC_DRAW);
                GLES32.glBindBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER, EBO);
                GLES32.glBufferData(GLES32.GL_ELEMENT_ARRAY_BUFFER,
                        indices.length * 4, IntBuffer.wrap(indices), GLES32.GL_STATIC_DRAW);

                GLES32.glVertexAttribPointer(0,
                        3, GLES32.GL_FLOAT, false, 3 * 4, 0);
                GLES32.glEnableVertexAttribArray(0);

                GLES32.glBindVertexArray(0);
//                GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, 0);
//                GLES32.glBindBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER, 0);
            }
        }

        {
            int vertexShader = loadShader(GLES32.GL_VERTEX_SHADER,
                    "attribute vec4 vPosition;\n" +
                            "\n" +
                            "varying vec4 tex;\n" +
                            "\n" +
                            "void main() {\n" +
                            "    gl_Position = vPosition;\n" +
                            "    tex = vPosition;\n" +
                            "}");
            int fragmentShader = loadShader(GLES32.GL_FRAGMENT_SHADER,
                    "precision mediump float;\n" +
                            "\n" +
                            "uniform vec4 vColor;\n" +
                            "\n" +
                            "varying vec4 tex;\n" +
                            "\n" +
                            "void main() {\n" +
                            "    gl_FragColor = vec4(tex.xy/2.0+0.5, 1.0, 1.0);\n" +
                            "}");

            mProgram = GLES32.glCreateProgram();
            GLES32.glAttachShader(mProgram, vertexShader);
            GLES32.glAttachShader(mProgram, fragmentShader);
            GLES32.glLinkProgram(mProgram);
        }

        GLES32.glGenFramebuffers(1, IntBuffer.wrap(tmp));
        int cameraFBO = tmp[0];
        frameId2 = cameraFBO;
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, cameraFBO);

        GLES32.glGenTextures(1, IntBuffer.wrap(tmp));
        int cameraTexture = tmp[0];
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
        int cameraDepth = tmp[0];
        GLES32.glBindRenderbuffer(GLES32.GL_RENDERBUFFER, cameraDepth);
        GLES32.glRenderbufferStorage(GLES32.GL_RENDERBUFFER, GLES32.GL_DEPTH_COMPONENT, width, height);
        GLES32.glBindRenderbuffer(GLES32.GL_RENDERBUFFER, 0);

        GLES32.glFramebufferRenderbuffer(GLES32.GL_FRAMEBUFFER,
                GLES32.GL_DEPTH_ATTACHMENT, GLES32.GL_RENDERBUFFER, cameraDepth);

//        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, 0);
        texId2 = tmp[0];
    }

    public static int getTexId2() {
        return texId2;
    }

    public static int getFrameId2() {
        return frameId2;
    }

    public static int loadShader(int type, String shaderCode) {
        // 根据type创建顶点着色器或者片元着色器
        int shader = GLES32.glCreateShader(type);
        // 将资源加入到着色器中，并编译
        GLES32.glShaderSource(shader, shaderCode);
        GLES32.glCompileShader(shader);
        return shader;
    }
}
