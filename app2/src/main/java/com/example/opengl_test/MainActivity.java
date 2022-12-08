package com.example.opengl_test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.opengl.GLES32;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;

import com.example.gettexture.GetTexture;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    private MyGLSurfaceView mGLSurfaceView;

    private static MainActivity mainActivity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        GetTexture.init2(2000, 1600);

        mainActivity = this;
        super.onCreate(savedInstanceState);
        mGLSurfaceView = new MyGLSurfaceView(this);
        setContentView(mGLSurfaceView);

//        SurfaceView a=new SurfaceView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }

    public static String readAssertResource(String strAssertFileName) {
        AssetManager assetManager = mainActivity.getAssets();
        String strResponse = "";
        try {
            InputStream ims = assetManager.open(strAssertFileName);
            strResponse = getStringFromInputStream(ims);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strResponse;
    }

    private static String getStringFromInputStream(InputStream a_is) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(a_is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    public static int loadBitmapTexture(int resId) {
        final int[] textureId = new int[1];
        GLES32.glGenTextures(1, textureId, 0);
        if (textureId[0] == 0) {
            Log.e(TAG, "Could not generate a new OpenGL texture object");
            return 0;
        }
        // load bitmap
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(mainActivity.getResources(), resId, options);
        if (bitmap == null) {
            Log.e(TAG, "resource id: " + resId + " could not be decoded");
            GLES32.glDeleteTextures(1, textureId, 0);
            return 0;
        }
        // bind
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, textureId[0]);
        // 缩小的情况，使用三线性过滤
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MIN_FILTER, GLES32.GL_LINEAR_MIPMAP_LINEAR);
        // 放大的情况，使用双线性过滤
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MAG_FILTER, GLES32.GL_LINEAR);
        // 加载位图到OpenGL中
        GLUtils.texImage2D(GLES32.GL_TEXTURE_2D, 0, bitmap, 0);
        GLES32.glGenerateMipmap(GLES32.GL_TEXTURE_2D);
        bitmap.recycle();
        // unbind
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, 0);
        return textureId[0];
    }
}