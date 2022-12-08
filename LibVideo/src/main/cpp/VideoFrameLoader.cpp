//
// Created by pkuzhd on 2022/11/24.
//

#include "VideoFrameLoader.h"
#include "logcat.h"
#include <GLES3/gl32.h>

using namespace std;

VideoFrameLoader::VideoFrameLoader(string videoFilename, string depthFilename, int type)
        : type(type) {
    LOGD("VideoFrameLoader::VideoFrameLoader()");
    LOGD("video filename: %s", videoFilename.c_str());
    LOGD("depth filename: %s", depthFilename.c_str());

    video = new Video2frame(videoFilename);
    depth = new Video2frame(depthFilename);

    if (type == 1) {
        glGenTextures(5, (unsigned int *) &textureId);
        int error = glGetError();
        if (error != GL_NO_ERROR) {
            LOGE("[zhd] opengl error: %d", error);
        }
        if (textureId[0] == 0) {
            LOGE("Could not generate a new OpenGL texture object");
        }

        for (int i = 0; i < 5; ++i) {
            glBindTexture(GL_TEXTURE_2D, textureId[i]);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glBindTexture(GL_TEXTURE_2D, 0);
        }
    }
}

void VideoFrameLoader::run() {
    LOGD("VideoFrameLoader::run()");
    video->run();
    depth->run();
}

int VideoFrameLoader::getTextureId(int textureType) {
    LOGD("VideoFrameLoader::getTextureId()");
    LOGD("TextureId: %d", textureId[textureType]);

    return textureId[textureType];
}

int VideoFrameLoader::_update() {
    if (!next_video_frame || !next_depth_frame)
        return 0;
    LOGD("video pts: %" PRId64", depth pts: %" PRId64,
         next_video_frame->raw_pts,
         next_depth_frame->raw_pts);
    LOGD("video pts: %lf, depth pts: %lf",
         next_video_frame->pts,
         next_depth_frame->pts);

    // video
    {
        int width = next_video_frame->frame->width;
        int height = next_video_frame->frame->height;

        int shift[3] = {0, 0, 0};
        if (next_video_frame->frame->format == AV_PIX_FMT_YUV420P) {
            shift[1] = 1;
            shift[2] = 1;
        }
        for (int i = 0; i < 3; ++i) {
            glBindTexture(GL_TEXTURE_2D, textureId[i]);

            glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE,
                         width >> shift[i],
                         height >> shift[i], 0, GL_LUMINANCE,
                         GL_UNSIGNED_BYTE, next_video_frame->frame->data[i]);
            glGenerateMipmap(GL_TEXTURE_2D);
            glBindTexture(GL_TEXTURE_2D, 0);
        }
        delete last_video_frame;
        last_video_frame = next_video_frame;
        next_video_frame = nullptr;
    }
    // depth
    {
        int width = next_depth_frame->frame->width;
        int height = next_depth_frame->frame->height;

        int shift[3] = {0, 0, 0};
        if (next_depth_frame->frame->format == AV_PIX_FMT_YUV420P) {
            shift[1] = 1;
            shift[2] = 1;
        }
        for (int i = 0; i < 2; ++i) {
            glBindTexture(GL_TEXTURE_2D, textureId[3 + i]);

            glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE,
                         width >> shift[i],
                         height >> shift[i], 0, GL_LUMINANCE,
                         GL_UNSIGNED_BYTE, next_depth_frame->frame->data[i]);
            glGenerateMipmap(GL_TEXTURE_2D);
            glBindTexture(GL_TEXTURE_2D, 0);
        }
        delete last_depth_frame;
        last_depth_frame = next_depth_frame;
        next_depth_frame = nullptr;
    }
    return 1;
}

void VideoFrameLoader::tryGetFrame() {
    if (next_video_frame && next_depth_frame)
        return;

    if (!next_video_frame) {
        next_video_frame = video->getFrame();
    }
    if (!next_depth_frame) {
        next_depth_frame = depth->getFrame();
    }

    if (next_video_frame && next_depth_frame) {
        if (av_cmp_q(next_video_frame->time_base, next_depth_frame->time_base) == 0) {
            while (next_video_frame && next_depth_frame &&
                   next_video_frame->raw_pts > next_depth_frame->raw_pts) {
                LOGD("video pts: %" PRId64", depth pts: %" PRId64,
                     next_video_frame->raw_pts,
                     next_depth_frame->raw_pts);
                LOGD("[0] drop depth");
                delete next_depth_frame;
                next_depth_frame = depth->getFrame();
            }
            while (next_video_frame && next_depth_frame &&
                   next_video_frame->raw_pts < next_depth_frame->raw_pts) {
                LOGD("video pts: %" PRId64", depth pts: %" PRId64,
                     next_video_frame->raw_pts,
                     next_depth_frame->raw_pts);
                LOGD("[0] drop video");
                delete next_video_frame;
                next_video_frame = video->getFrame();
            }
        } else {
            double fps = min(av_q2d(next_video_frame->frame_rate),
                             av_q2d(next_video_frame->frame_rate));
            while (next_video_frame && next_depth_frame &&
                   next_video_frame->pts > next_depth_frame->pts + 0.5 / fps) {
                LOGD("video pts: %lf, depth pts: %lf",
                     next_video_frame->pts,
                     next_depth_frame->pts);
                LOGD("[1] drop depth");
                delete next_depth_frame;
                next_depth_frame = depth->getFrame();
            }
            while (next_video_frame && next_depth_frame &&
                   next_video_frame->pts + 0.5 / fps < next_depth_frame->pts) {
                LOGD("video pts: %lf, depth pts: %lf",
                     next_video_frame->pts,
                     next_depth_frame->pts);
                LOGD("[1] drop video");
                delete next_video_frame;
                next_video_frame = video->getFrame();
            }
        }
    }
}


int VideoFrameLoader::update() {
    if (!video || !depth) {
        LOGE("VideoFrameLoader::update() ERROR");
        return 0;
    }
    LOGD("VideoFrameLoader::update()");
    int video_buffer_size = video->getBufferSize();
    int depth_buffer_size = depth->getBufferSize();
    int video_packet_buffer_size = video->getPacketBufferSize();
    int depth_packet_buffer_size = depth->getPacketBufferSize();
    LOGD("Video Buffer Size: %d, Depth Buffer Size: %d", video_buffer_size, depth_buffer_size);
    LOGD("Video Packet Buffer Size: %d, Depth Packet Buffer Size: %d",
         video_packet_buffer_size, depth_packet_buffer_size);

    int updated = 0;

    tryGetFrame();
    updated = _update();
    tryGetFrame();
    if (!updated) {
        updated = _update();
        tryGetFrame();
    }

    return updated;
}

int VideoFrameLoader::update2(int type) {
    if (type == 0) {
        if (!video || !depth) {
            LOGE("VideoFrameLoader::update() ERROR");
            return 0;
        }
        LOGD("VideoFrameLoader::update()");
        int video_buffer_size = video->getBufferSize();
        int depth_buffer_size = depth->getBufferSize();
        int video_packet_buffer_size = video->getPacketBufferSize();
        int depth_packet_buffer_size = depth->getPacketBufferSize();
        LOGD("Video Buffer Size: %d, Depth Buffer Size: %d", video_buffer_size, depth_buffer_size);
        LOGD("Video Packet Buffer Size: %d, Depth Packet Buffer Size: %d",
             video_packet_buffer_size, depth_packet_buffer_size);

        int updated = 0;

        tryGetFrame();
        if (next_video_frame && next_depth_frame)
            return 1;
    } else {
        if (!next_video_frame || !next_depth_frame)
            return 0;
        LOGD("video pts: %" PRId64", depth pts: %" PRId64,
             next_video_frame->raw_pts,
             next_depth_frame->raw_pts);
        LOGD("video pts: %lf, depth pts: %lf",
             next_video_frame->pts,
             next_depth_frame->pts);

        // video
        {
            delete last_video_frame;
            last_video_frame = next_video_frame;
            next_video_frame = nullptr;
        }
        // depth
        {
            delete last_depth_frame;
            last_depth_frame = next_depth_frame;
            next_depth_frame = nullptr;
        }
        return 1;
    }
    return 0;
}


double VideoFrameLoader::getNextPTS() {
    LOGD("VideoFrameLoader::getNextPTS()");
    if (next_video_frame)
        return next_video_frame->pts;
    return -1;
}

double VideoFrameLoader::getLastPTS() {
    LOGD("VideoFrameLoader::getLastPTS()");
    if (last_video_frame)
        return last_video_frame->pts;
    return -1;
}

int VideoFrameLoader::getInt(string name, int idx) {
    if (!next_video_frame || !next_depth_frame)
        return -1;

    const AVFrame *frame = nullptr;
    int i = 0;
    int shift[3] = {0, 0, 0};
    if (idx <= 2) {
        i = idx;
        frame = next_video_frame->frame;
    } else {
        i = idx - 3;
        frame = next_depth_frame->frame;
    }
    if (frame->format == AV_PIX_FMT_YUV420P) {
        shift[1] = 1;
        shift[2] = 1;
    }

    if (name == "width") {
        return frame->width >> shift[i];
    } else if (name == "height") {
        return frame->height >> shift[i];
    }

    return -1;
}

uint8_t *VideoFrameLoader::getBuffer(int idx) {
    if (!next_video_frame || !next_depth_frame)
        return nullptr;

    const AVFrame *frame = nullptr;
    int i = 0;
    if (idx <= 2) {
        i = idx;
        frame = next_video_frame->frame;
    } else {
        i = idx - 3;
        frame = next_depth_frame->frame;
    }
    return frame->data[i];
}

