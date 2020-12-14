package com.android.toolbox.core.bean.user;

import java.io.Serializable;
import java.util.List;

/**
 * Auto-generated: 2019-03-05 16:34:54
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class UserInfo implements Serializable {

    private String id;
    private String tenant_id;
    private Long update_date;
    private List<UserOrganization> userOrganizations;
    private String user_age;
    private String user_avatar;
    private String user_email;
    private String user_empcode;
    private String user_mobile;
    private String user_name;
    private String user_real_name;
    private String user_password;
    private String user_status;

    public UserInfo(String user_name, String user_password) {
        this.user_name = user_name;
        this.user_password = user_password;
    }

    public UserInfo() {

    }

    public String getUser_real_name() {
        return user_real_name;
    }

    public void setUser_real_name(String user_real_name) {
        this.user_real_name = user_real_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenant_id() {
        return tenant_id;
    }

    public void setTenant_id(String tenant_id) {
        this.tenant_id = tenant_id;
    }

    public Long getUpdate_date() {
        return update_date;
    }

    public void setUpdate_date(Long update_date) {
        this.update_date = update_date;
    }

    public List<UserOrganization> getUserOrganizations() {
        return userOrganizations;
    }

    public void setUserOrganizations(List<UserOrganization> userOrganizations) {
        this.userOrganizations = userOrganizations;
    }

    public String getUser_age() {
        return user_age;
    }

    public void setUser_age(String user_age) {
        this.user_age = user_age;
    }

    public String getUser_avatar() {
        return user_avatar;
    }

    public void setUser_avatar(String user_avatar) {
        this.user_avatar = user_avatar;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_empcode() {
        return user_empcode;
    }

    public void setUser_empcode(String user_empcode) {
        this.user_empcode = user_empcode;
    }

    public String getUser_mobile() {
        return user_mobile;
    }

    public void setUser_mobile(String user_mobile) {
        this.user_mobile = user_mobile;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public String getUser_status() {
        return user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }

    /**
     * Auto-generated: 2019-03-05 16:34:54
     *
     * @author bejson.com (i@bejson.com)
     * @website http://www.bejson.com/java2pojo/
     */
    public class MapWhereFilter {

        private String additionalProp1;
        private String additionalProp2;
        private String additionalProp3;
        public void setAdditionalProp1(String additionalProp1) {
            this.additionalProp1 = additionalProp1;
        }
        public String getAdditionalProp1() {
            return additionalProp1;
        }

        public void setAdditionalProp2(String additionalProp2) {
            this.additionalProp2 = additionalProp2;
        }
        public String getAdditionalProp2() {
            return additionalProp2;
        }

        public void setAdditionalProp3(String additionalProp3) {
            this.additionalProp3 = additionalProp3;
        }
        public String getAdditionalProp3() {
            return additionalProp3;
        }

    }


    public class Result {
        private UserInfo user;
        private String token;

        public UserInfo getUser() {
            return user;
        }

        public void setUser(UserInfo user) {
            this.user = user;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

}