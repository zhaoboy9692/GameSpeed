//
// Created by didi on 7/5/24.
//

#ifndef GAMESPEED_HELPER_H
#define GAMESPEED_HELPER_H
#include <string>
#include <jni.h>
int (*old_gettimeofday)(struct timeval *_Nullable __tv, struct timezone *_Nullable __tz);
int (*old_clock_gettime)(clockid_t __clock, struct timespec* _Nonnull __ts);
#endif //GAMESPEED_HELPER_H
