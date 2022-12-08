//
// Created by pkuzhd on 2022/11/24.
//

#ifndef TESTNATIVE_VIDEOFRAMELOADER_H
#define TESTNATIVE_VIDEOFRAMELOADER_H

#include <string>
#include <jni.h>
#include "video/Video2frame.h"

class VideoFrameLoader {
    int textureId[5] = {-1, -1, -1, -1, -1};
    Video2frame *video = nullptr;
    Video2frame *depth = nullptr;
    int type;

    Frame *last_video_frame = nullptr, *next_video_frame = nullptr;
    Frame *last_depth_frame = nullptr, *next_depth_frame = nullptr;

    int _update();

    void tryGetFrame();

public:
    VideoFrameLoader(std::string videoFilename, std::string depthFilename, int type=1);

    void run();

    int getTextureId(int textureType);

    int update();

    int update2(int type);

    double getNextPTS();

    double getLastPTS();

    int getInt(std::string name, int idx);

    uint8_t *getBuffer(int idx);
};


#endif //TESTNATIVE_VIDEOFRAMELOADER_H
