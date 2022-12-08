package com.example.testnative;

import android.util.Log;

class CppMain extends Thread {
    final String TAG = "CppMain";

    static {
        System.loadLibrary("main");
        System.loadLibrary("ijkffmpeg");
    }

    private native void main();

    @Override
    public void run() {
        super.run();
        Log.d(TAG, "run");
        main();
    }
}
