#include <jni.h>
#include <string>
#include "lsp_native_api.h"
#include "logger.h"
#include "helper.h"

static HookFunType hook_func = nullptr;
static float SpeedMultiple = 5; //变速倍数
static struct timeval timebase_gettimeofday;
static struct timespec timebase_monotonic;
static struct timespec timebase_realtime;

int new_gettimeofday(struct timeval *tv, struct timezone *tz) {
    int ret = old_gettimeofday(tv, tz);
    if (SpeedMultiple == 0) {
        return ret;
    }
    long tv_sec = (tv->tv_sec - timebase_gettimeofday.tv_sec);
    long tv_usec = (tv->tv_usec - timebase_gettimeofday.tv_usec);
    if (tv_sec == 0)return ret;
    if (ret == 0 && tv != NULL) {
        long adjusted_sec = tv_sec * SpeedMultiple;
        tv->tv_sec = (long) adjusted_sec;
        tv->tv_usec = tv->tv_usec + (long) tv_usec * SpeedMultiple;
        if (tv->tv_usec >= 1000000) {
            tv->tv_usec -= 1000000;
            tv->tv_sec += 1;
        }
    }

    return ret;
}

int new_clock_gettime(clockid_t clock, struct timespec *ts) {
    int ret = old_clock_gettime(clock, ts);
    if (SpeedMultiple == 0) {
        return ret;
    }
    long tv_sec = (ts->tv_sec - timebase_monotonic.tv_sec);
    long tv_nsec = (ts->tv_nsec - timebase_monotonic.tv_nsec);
    if (ret != 0) {
        return ret;
    }
    if (tv_sec == 0)return ret;


    long adjusted_sec = tv_sec * SpeedMultiple;
    ts->tv_sec = (long) adjusted_sec;
    ts->tv_nsec = ts->tv_nsec + (long) tv_nsec * SpeedMultiple;
    if (ts->tv_nsec >= 1000000000) {
        ts->tv_nsec -= 1000000000;
        ts->tv_sec += 1;
    }
    return ret;
}

void on_library_loaded(const char *name, void *handle) {

}

extern "C" [[gnu::visibility("default")]] [[gnu::used]]
NativeOnModuleLoaded native_init(const NativeAPIEntries *entries) {
    hook_func = entries->hook_func;
    hook_func((void *) gettimeofday, (void *) new_gettimeofday, (void **) &old_gettimeofday);
    hook_func((void *) clock_gettime, (void *) new_clock_gettime, (void **) &old_clock_gettime);
    (*old_gettimeofday)(&timebase_gettimeofday, NULL);
    (*old_clock_gettime)(CLOCK_REALTIME, &timebase_realtime);
    return on_library_loaded;
}