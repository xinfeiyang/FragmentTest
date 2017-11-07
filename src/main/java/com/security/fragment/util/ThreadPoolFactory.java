package com.security.fragment.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RunnableFuture;

/**
 * Created by Feng on 2017/4/20 0020.
 */

public class ThreadPoolFactory {

    private static volatile ThreadPoolFactory instance;

    private ThreadPoolFactory(){

    }

    public static ThreadPoolFactory getInstance(){
        if(instance==null){
            synchronized (ThreadPoolFactory.class){
                if(instance==null){
                    instance=new ThreadPoolFactory();
                }
            }
        }
        return instance;
    }

    public static void execute(Runnable runnable){
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(runnable);

    }

}
