package com.example.opengl_test;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

public class MyGLSurfaceView extends GLSurfaceView {
    private final MyRenderer mRenderer;

    public MyGLSurfaceView(Context context) {
        super(context);

        setEGLContextClientVersion(2);

        mRenderer = new MyRenderer();

        //设置Renderer用于绘图
        setRenderer(mRenderer);

        //只有在绘制数据改变时才绘制view，可以防止GLSurfaceView帧重绘
//        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
