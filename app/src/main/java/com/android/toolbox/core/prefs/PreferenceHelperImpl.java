package com.android.toolbox.core.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import com.android.toolbox.app.ToolBoxApplication;
import com.android.toolbox.app.Constants;
/**
 * @author yhm
 * @date 2017/11/27
 */

public class PreferenceHelperImpl implements PreferenceHelper {

    private final SharedPreferences mPreferences;
    private volatile static PreferenceHelperImpl INSTANCE = null;

    private PreferenceHelperImpl() {
        mPreferences = ToolBoxApplication.getInstance().getSharedPreferences(Constants.MY_SHARED_PREFERENCE, Context.MODE_PRIVATE);
    }

    public static PreferenceHelperImpl getInstance(){
        if (INSTANCE == null) {
            synchronized (PreferenceHelperImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new PreferenceHelperImpl();
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void saveHostUrl(String hostUrl) {
        mPreferences.edit().putString(Constants.HOSTURL, hostUrl).apply();
    }

    @Override
    public String getHostUrl() {
        return mPreferences.getString(Constants.HOSTURL, "");
    }

    @Override
    public void setToken(String token) {
        mPreferences.edit().putString(Constants.TOKEN, token).apply();
    }

    @Override
    public String getToken() {
        return mPreferences.getString(Constants.TOKEN, "");
    }


}
