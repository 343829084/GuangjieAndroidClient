package com.taobao.daogou.client.util;

import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 一个简单的线程池
 */
public class ThreadPoolHelper {
    static ThreadPoolHelper mInstance;
    public static synchronized ThreadPoolHelper getInstance() {
        if (mInstance == null)
            mInstance = new ThreadPoolHelper();
        return mInstance;
    }

    final ThreadPoolExecutor mExecutor;
    public ThreadPoolHelper() {
        mExecutor = new ThreadPoolExecutor(5, 10, 15, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    }

    public Future submit(Runnable runnable) {
        return mExecutor.submit(runnable);
    }
}
