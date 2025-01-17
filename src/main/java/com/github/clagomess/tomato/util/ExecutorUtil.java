package com.github.clagomess.tomato.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorUtil {
    private ExecutorUtil() {}

    private static ExecutorService singleThreadExecutor;
    public static synchronized ExecutorService getSingleThreadExecutor(){
        if(singleThreadExecutor == null) singleThreadExecutor = Executors.newSingleThreadExecutor();
        return singleThreadExecutor;
    }
}
