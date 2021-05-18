package com.android.toolbox.core.prefs;
/**
 * @author yhm
 * @date 2017/11/27
 */

public interface PreferenceHelper {
    void setToken(String token);

    String getToken();

    void saveHostUrl(String hostUrl);

    String getHostUrl();

    void saveFaceActiveStatus(boolean status);

    boolean getFaceActiveStatus();

}

