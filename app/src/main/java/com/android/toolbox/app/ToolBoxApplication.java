package com.android.toolbox.app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.android.toolbox.BuildConfig;
import com.android.toolbox.R;
import com.android.toolbox.base.activity.BaseActivity;
import com.android.toolbox.core.bean.user.UserInfo;
import com.android.toolbox.skrfidbox.ServerThread;
import com.android.toolbox.utils.Utils;
import com.android.toolbox.utils.logger.MyCrashListener;
import com.android.toolbox.utils.logger.TxtFormatStrategy;
import com.bumptech.glide.Glide;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.xuexiang.xlog.XLog;
import com.xuexiang.xlog.crash.CrashHandler;

import java.util.ArrayList;

/**
 * @author yhm
 * @date 2017/11/27
 */
//public class ToolBoxApplication extends Application implements HasActivityInjector {
public class ToolBoxApplication extends Application {


    private static ToolBoxApplication instance;
    private RefWatcher refWatcher;
    private ArrayList<BaseActivity> activities = new ArrayList<>();
    private ServerThread serverThread;

    public static synchronized ToolBoxApplication getInstance() {
        return instance;
    }
    private UserInfo currentUser;

    public static RefWatcher getRefWatcher(Context context) {
        ToolBoxApplication application = (ToolBoxApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }

        refWatcher = LeakCanary.install(this);
        instance = this;
        initLogger();
        Utils.init(this);
        //崩溃日志保存到本地
        ///storage/emulated/0/Android/data/com.common.esimrfid/cache/crash_log
        XLog.init(this);
        CrashHandler.getInstance().setOnCrashListener(new MyCrashListener());
        serverThread = new ServerThread(5460, 3 * 100000);
        serverThread.start();

    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            Glide.get(this).clearMemory();
        }
        Glide.get(this).trimMemory(level);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(this).clearMemory();
    }


    private void initLogger() {
        //DEBUG版本才打控制台log
        if (BuildConfig.DEBUG) {
            Logger.addLogAdapter(new AndroidLogAdapter(PrettyFormatStrategy.newBuilder().
                    tag(getString(R.string.app_name)).build()));
        }
        //把log存到本地
        Logger.addLogAdapter(new DiskLogAdapter(TxtFormatStrategy.newBuilder().
                tag(getString(R.string.app_name)).build(getPackageName(), getString(R.string.app_name))));
    }

    public void addActivity(BaseActivity activity){
        activities.add(activity);
    }

    public void removeActivity(BaseActivity activity){
        activities.remove(activity);
    }

    public void exitActivitys(){
        for (int i = 0; i < activities.size(); i++) {
            activities.get(i).finish();
        }
    }

    public UserInfo getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserInfo currentUser) {
        this.currentUser = currentUser;
    }

    public ServerThread getServerThread() {
        return serverThread;
    }
}
