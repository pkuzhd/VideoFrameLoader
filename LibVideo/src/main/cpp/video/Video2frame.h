//
// Created by pkuzhd on 2022/11/24.
//

#ifndef TESTNATIVE_VIDEO2FRAME_H
#define TESTNATIVE_VIDEO2FRAME_H


#include <iostream>
#include <cstring>
#include <queue>
#include <thread>
#include <mutex>

#ifdef __cplusplus
extern "C" {
#endif
#include <libavformat/avformat.h>
#include <libavfilter/avfilter.h>
#include <libavcodec/avcodec.h>
#include <libswscale/swscale.h>
#include <libavutil/imgutils.h>
#ifdef __cplusplus
};
#endif

class Frame {
public:
    double pts;
    int64_t raw_pts;
    AVRational time_base;
    AVRational frame_rate;
    uint8_t *data = NULL;
    AVFrame *frame;

    Frame(double pts, AVFrame *frame, AVRational time_base, AVRational frame_rate)
            : pts(pts), frame(frame), time_base(time_base), frame_rate(frame_rate) {
        data = frame->data[0];
        raw_pts = frame->pts;
    }

    ~Frame() {
        av_frame_free(&frame);
    }
};

class Video2frame {
public:
    std::string url;
    std::queue<Frame *> buffer;
    std::queue<AVPacket *> pkt_buffer;
    AVFormatContext *format_ctx;
    AVFrame *frame_raw;
    AVCodecContext *codec_ctx;
    std::mutex m;
    std::thread *thread;
    bool term = false;

    bool is_finish = false;

    int video_stream_idx;

    Video2frame(std::string url);

    ~Video2frame();

    void init();

    void run();

    Frame *getFrame();

    float getPTS();

    int getBufferSize();

    int getPacketBufferSize();
};


#endif //TESTNATIVE_VIDEO2FRAME_H
