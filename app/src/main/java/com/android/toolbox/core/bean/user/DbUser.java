package com.android.toolbox.core.bean.user;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;

@Entity
public class DbUser implements Serializable {

    @PrimaryKey
    @NonNull
    private String id;

    private String user_name;

    private String user_password;

    private String user_mobile;

    private String user_email;

    private String user_avatar;

    private String user_gender;

    private String user_age;

    private String user_empcode;

    private String user_status;

    private String tenant_id;

    private String dd_userid;

    private String wx_userid;

    private Date create_date;

    private Date update_date;

    private static final long serialVersionUID = 1L;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_name() {
        return this.user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_password() {
        return this.user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public String getUser_mobile() {
        return this.user_mobile;
    }

    public void setUser_mobile(String user_mobile) {
        this.user_mobile = user_mobile;
    }

    public String getUser_email() {
        return this.user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_avatar() {
        return this.user_avatar;
    }

    public void setUser_avatar(String user_avatar) {
        this.user_avatar = user_avatar;
    }

    public String getUser_gender() {
        return this.user_gender;
    }

    public void setUser_gender(String user_gender) {
        this.user_gender = user_gender;
    }

    public String getUser_age() {
        return this.user_age;
    }

    public void setUser_age(String user_age) {
        this.user_age = user_age;
    }

    public String getUser_empcode() {
        return this.user_empcode;
    }

    public void setUser_empcode(String user_empcode) {
        this.user_empcode = user_empcode;
    }

    public String getUser_status() {
        return this.user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }

    public String getTenant_id() {
        return this.tenant_id;
    }

    public void setTenant_id(String tenant_id) {
        this.tenant_id = tenant_id;
    }

    public String getDd_userid() {
        return this.dd_userid;
    }

    public void setDd_userid(String dd_userid) {
        this.dd_userid = dd_userid;
    }

    public String getWx_userid() {
        return this.wx_userid;
    }

    public void setWx_userid(String wx_userid) {
        this.wx_userid = wx_userid;
    }

    public Date getCreate_date() {
        return this.create_date;
    }

    public void setCreate_date(Date create_date) {
        this.create_date = create_date;
    }

    public Date getUpdate_date() {
        return this.update_date;
    }

    public void setUpdate_date(Date update_date) {
        this.update_date = update_date;
    }
    
}
