package com.android.toolbox.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.toolbox.HomeActivity;

public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("BootReceiver", "BootReceiver start");
        //启动MainActivity
        Intent intent1 = new Intent(context, HomeActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//添加标记
        context.startActivity(intent1);
        Log.v("BootReceiver", "BootReceiver end");
    }
}
