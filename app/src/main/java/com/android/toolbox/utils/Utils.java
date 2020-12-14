package com.android.toolbox.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.KeyEvent;

/**
 * Created by rylai on 2017/5/14.
 * 常用工具类
 */
public final class Utils {

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    private Utils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 初始化工具类
     *
     * @param context 上下文
     */
    public static void init(final Context context) {
        Utils.context = context.getApplicationContext();
    }

    /**
     * 获取ApplicationContext
     *
     * @return ApplicationContext
     */
    public static Context getContext() {
        if (context != null) {
            return context;
        }
        throw new NullPointerException("should be initialized in application");
    }

    public static int getDiffDownKey(){
        int key = 0;
        String model=android.os.Build.MODEL;
        if("ESUR-H600".equals(model)|| "SD60".equals(model)){
            key = KeyEvent.KEYCODE_F1;
        }else if("common".equals(model)|| "ESUR-H500".equals(model)){
            key = KeyEvent.KEYCODE_F4;
        }
        return key;
    }
}