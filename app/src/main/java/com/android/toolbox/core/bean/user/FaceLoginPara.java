package com.android.toolbox.core.bean.user;

public class FaceLoginPara {
    private Integer manager_client = 1;

    private String user_name;

    public FaceLoginPara(String user_name) {
        this.user_name = user_name;
    }

    public Integer getManager_client() {
        return manager_client;
    }

    public void setManager_client(Integer manager_client) {
        this.manager_client = manager_client;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}
