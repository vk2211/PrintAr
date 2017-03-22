
#ifndef __EASYAR_SAMPLE_LOG_H__
#define __EASYAR_SAMPLE_LOG_H__

#ifdef ANDROID
#include <android/log.h>
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, "EasyAR", __VA_ARGS__)
#else
#define LOGI(...) printf(__VA_ARGS__)
#endif

#endif
