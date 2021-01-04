package com.android.toolbox.core.bean.user;

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
    private int managerClient = 1;
    private CorpInfo corpInfo;

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

    public int getManagerClient() {
        return managerClient;
    }

    public void setManagerClient(int managerClient) {
        this.managerClient = managerClient;
    }

    public CorpInfo getCorpInfo() {
        return corpInfo;
    }

    public void setCorpInfo(CorpInfo corpInfo) {
        this.corpInfo = corpInfo;
    }

    public static class CorpInfo {
        /**
         * create_date : 1583136900000
         * id : e91aebf95c5d11eaabcf00163e0a6695
         * org_code : 00010001
         * org_isleaf : 1
         * org_name : 123部门
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
}