package com.android.toolbox.core.bean.user;

import java.util.List;

/**
 * Auto-generated: 2019-03-05 16:34:54
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class UserInfo {

    private String id;
    private String user_name;
    private String user_real_name;
    private String user_password;
    private String user_mobile;
    private String managerClient = "11";
    private int env = 1;
    private CorpInfo corpInfo;
    private DeptInfo deptInfo;
    private List<Roles> roles;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_real_name() {
        return user_real_name;
    }

    public void setUser_real_name(String user_real_name) {
        this.user_real_name = user_real_name;
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

    public String getManagerClient() {
        return managerClient;
    }

    public void setManagerClient(String managerClient) {
        this.managerClient = managerClient;
    }

    public CorpInfo getCorpInfo() {
        return corpInfo;
    }

    public void setCorpInfo(CorpInfo corpInfo) {
        this.corpInfo = corpInfo;
    }

    public int getEnv() {
        return env;
    }

    public void setEnv(int env) {
        this.env = env;
    }

    public String getUser_mobile() {
        return user_mobile;
    }

    public void setUser_mobile(String user_mobile) {
        this.user_mobile = user_mobile;
    }

    public DeptInfo getDeptInfo() {
        return deptInfo;
    }

    public void setDeptInfo(DeptInfo deptInfo) {
        this.deptInfo = deptInfo;
    }

    public List<Roles> getRoles() {
        return roles;
    }

    public void setRoles(List<Roles> roles) {
        this.roles = roles;
    }

    public static class CorpInfo {
        /**
         * create_date : 1583136900000
         * id : e91aebf95c5d11eaabcf00163e0a6695
         * org_code : 00010001
         * org_isleaf : 1
         * org_name : 123??????
         * org_seq : 4465
         * org_superid : 1faf2bf55c5a11eaabcf00163e0a6695
         * org_type : 0
         * tenantid : tenantid4347
         * update_date : 1601004499000
         */

        private String id;
        private String org_name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getOrg_name() {
            return org_name;
        }

        public void setOrg_name(String org_name) {
            this.org_name = org_name;
        }
    }

    public static class DeptInfo {

        private String id;
        private String org_name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getOrg_name() {
            return org_name;
        }

        public void setOrg_name(String org_name) {
            this.org_name = org_name;
        }
    }

    public static class Roles {
        /**
         * create_date : 1615966231000
         * id : b6dbc92f470c420a94a331585eb00555
         * menu_ids : [101,201,212,209,329]
         * role_code : 0
         * role_name : ??????
         * role_remark :
         * tenantid : tenantid4682
         * update_date : 1615966231000
         */

        private String id;
        private String role_name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getRole_name() {
            return role_name;
        }

        public void setRole_name(String role_name) {
            this.role_name = role_name;
        }
    }
}