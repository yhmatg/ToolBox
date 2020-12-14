package com.android.toolbox.core.bean.user;

import java.io.Serializable;

public class UserOrganization implements Serializable {
    private String corp_id;
    private String id;
    private String org_id;
    private String role_id;
    private Long update_date;
    private String user_id;
    public void setCorp_id(String corp_id) {
        this.corp_id = corp_id;
    }
    public String getCorp_id() {
        return corp_id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public void setOrg_id(String org_id) {
        this.org_id = org_id;
    }
    public String getOrg_id() {
        return org_id;
    }

    public void setRole_id(String role_id) {
        this.role_id = role_id;
    }
    public String getRole_id() {
        return role_id;
    }

    public void setUpdate_date(Long update_date) {
        this.update_date = update_date;
    }
    public Long getUpdate_date() {
        return update_date;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    public String getUser_id() {
        return user_id;
    }

}