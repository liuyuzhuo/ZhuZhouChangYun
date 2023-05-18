package com.tencent.wxcloudrun.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 *
  * 线程池工具类
  * @author yuzhuoLiu
  * @date 2023/5/18 14:51
  */
public class ThreadPoolUtil {
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolUtil.class);
    private static volatile ThreadPoolExecutor threadPool = null;

    private ThreadPoolUtil() {
    }

    public static void execute(Runnable runnable) {
        getThreadPool().execute(runnable);
        long taskCount = threadPool.getTaskCount();
        long poolSize = threadPool.getPoolSize();
        long completedTaskCount = threadPool.getCompletedTaskCount();
        long activeCount = threadPool.getActiveCount();
        long queueSize = threadPool.getQueue().size();
        logger.info("taskCount=>{},poolSize=>{} completedTaskCount=>{}, activeCount=>{}, queueSize=>{}", taskCount,poolSize, completedTaskCount, activeCount, queueSize);
    }

    public static <T> Future<T> submit(Callable<T> callable) {
        return getThreadPool().submit(callable);
    }

    private static ThreadPoolExecutor getThreadPool() {
        if (threadPool == null) {
            synchronized(ThreadPoolUtil.class) {
                if (threadPool == null) {
                    int cpuNum = Runtime.getRuntime().availableProcessors();
                    threadPool = new ThreadPoolExecutor(cpuNum, cpuNum, 2147483647L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque(2147483647), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
                }
            }
        }

        return threadPool;
    }

    public static long getQueue() {
        return getThreadPool().getQueue().size();
    }


}
