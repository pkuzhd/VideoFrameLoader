//
// Created by pkuzhd on 2022/11/22.
//
#include "logcat.h"

#ifdef __cplusplus
extern "C" {
#endif
#include "libavformat/avformat.h"
#ifdef __cplusplus
};
#endif

#include "VideoLib/Video2frame.h"

using namespace std;

void CppMain() {
    LOGD("main");

    string video_url = "rtmp://222.29.111.196:1935/live/rgb";
    shared_ptr<Video2frame> video =
//            make_shared<Video2frame>(video_url, 1920, 1080, AV_PIX_FMT_RGB24);
            make_shared<Video2frame>(video_url, 1920, 1080, AV_PIX_FMT_YUV420P);
    video->run();


    int frame_cnt = 0;
    auto start_all = chrono::high_resolution_clock::now();
    Frame *frame;
    while (true) {
        if (video->getBufferSize() > 0) {
            if (frame_cnt == 0) {
                start_all = chrono::high_resolution_clock::now();
            }
            frame = video->getFrame();
            if (frame) {
                delete frame;
                ++frame_cnt;
                auto frame_time = chrono::high_resolution_clock::now();
                float fps = frame_cnt * 1000.0 /
                            chrono::duration<double, milli>(frame_time - start_all).count();
                LOGD("fps: %.1f", fps);
            }
        } else {
            this_thread::sleep_for(chrono::duration<double, milli>(50));
        }
    }
}
