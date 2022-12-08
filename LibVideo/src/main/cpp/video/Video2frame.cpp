//
// Created by pkuzhd on 2022/11/24.
//

#include "Video2frame.h"
#include "../logcat.h"

using namespace std;

void Video2frame::init() {
    int ret = 0;

    format_ctx = avformat_alloc_context();
    ret = avformat_open_input(&format_ctx, url.c_str(), nullptr, nullptr);
    if (ret != 0) {
        LOGD("avformat_open_input: %d", ret);
    }
    avformat_find_stream_info(format_ctx, nullptr);

    video_stream_idx = -1;
    for (int i = 0; i < format_ctx->nb_streams; ++i) {
        if (format_ctx->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_VIDEO) {
            video_stream_idx = i;
            break;
        }
    }

    AVCodecParameters *codec_para = format_ctx->streams[video_stream_idx]->codecpar;
    AVCodec *codec = avcodec_find_decoder(codec_para->codec_id);
    codec_ctx = avcodec_alloc_context3(codec);
    avcodec_parameters_to_context(codec_ctx, codec_para);
    avcodec_open2(codec_ctx, codec, nullptr);

    frame_raw = av_frame_alloc();
}

void demux_decode_thread(Video2frame *arg) {
    arg->init();
    bool term = false;
    int ret;

    AVFormatContext *format_ctx = arg->format_ctx;
    AVPacket *pkt;
    AVCodecContext *codec_ctx = arg->codec_ctx;
    AVFrame *frame_raw = arg->frame_raw;

    std::queue<Frame *> &buffer = arg->buffer;
    std::queue<AVPacket *> &pkt_buffer = arg->pkt_buffer;

    auto start_all = chrono::high_resolution_clock::now();
    int pkt_cnt = 0;
    while (!term) {
        pkt = av_packet_alloc();
        ret = av_read_frame(format_ctx, pkt);
        if (ret != 0) {
            av_packet_free(&pkt);
            continue;
        }

        if (pkt->stream_index == arg->video_stream_idx) {
            int size;
            arg->m.lock();
            size = buffer.size();
            pkt_buffer.push(pkt);
            arg->m.unlock();

            if (size < 100) {
                arg->m.lock();
                pkt = pkt_buffer.front();
                pkt_buffer.pop();
                arg->m.unlock();

                avcodec_send_packet(codec_ctx, pkt);

                av_packet_free(&pkt);

                using std::cout;
                using std::endl;

                while ((ret = avcodec_receive_frame(codec_ctx, frame_raw)) != AVERROR(EAGAIN)) {


                    double pts = frame_raw->pts * 1.0 *
                                 av_q2d(format_ctx->streams[arg->video_stream_idx]->time_base);
                    AVFrame *f = av_frame_alloc();
                    av_frame_ref(f, frame_raw);
                    av_frame_unref(frame_raw);

                    Frame *frame = new Frame(pts, f,
                                             format_ctx->streams[arg->video_stream_idx]->time_base,
                                             format_ctx->streams[arg->video_stream_idx]->avg_frame_rate);
                    arg->m.lock();
                    buffer.push(frame);
                    arg->m.unlock();
                }
            }
        } else {
            av_packet_free(&pkt);
        }
        arg->m.lock();
        term = arg->term;
        arg->m.unlock();
    }
    std::cout << "end" << std::endl;
    arg->m.lock();
    arg->is_finish = true;
    arg->m.unlock();

}

Video2frame::Video2frame(std::string url) : url(url) {

}

Video2frame::~Video2frame() {
    m.lock();
    term = true;
    m.unlock();
    thread->join();
}

Frame *Video2frame::getFrame() {
    m.lock();
    Frame *frame;
    if (buffer.empty()) {
        frame = NULL;
    } else {
        frame = buffer.front();
        buffer.pop();
    }
    m.unlock();
    return frame;
}

float Video2frame::getPTS() {
    m.lock();
    float pts;
    if (buffer.empty()) {
        pts = -1;
    } else {
        pts = buffer.front()->pts;
    }
    m.unlock();
    return pts;
}


int Video2frame::getBufferSize() {
    m.lock();
    int size = buffer.size();
    if (is_finish && size == 0)
        size = -1;
    m.unlock();
    return size;
}

void Video2frame::run() {
    thread = new std::thread(demux_decode_thread, this);
}

int Video2frame::getPacketBufferSize() {
    m.lock();
    int size = pkt_buffer.size();
    m.unlock();
    return size;
}

