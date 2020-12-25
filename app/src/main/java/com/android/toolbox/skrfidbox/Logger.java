package com.android.toolbox.skrfidbox;

import com.alibaba.fastjson.JSON;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    public static void info(Object msg) {
        Date date = new Date();
        String name = Thread.currentThread().getName();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strMsg = ((msg instanceof String) ? msg.toString() : JSON.toJSONString(msg));
        System.out.println(String.format("%s--%s ------ %s ", name, sdf1.format(date), strMsg));
    }
}
