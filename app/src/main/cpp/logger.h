//
// Created by didi on 7/1/24.
//

#ifndef MOSESKY_LOGGER_H
#define MOSESKY_LOGGER_H
#include <android/log.h>

#define LOGI(...) (__android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__))
#define LOGW(...) (__android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__))
#define LOGE(...) (__android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__))

#ifndef NDEBUG
#define LOGV(...) (__android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__))
#define LOGD(...) (__android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__))
#else
#define LOGV(...) ((void)0)
#define LOGD(...) ((void)0)
#endif
#define TAG "MoseSkySO"
#endif //MOSESKY_LOGGER_H
