package com.android.toolbox.core.bean.terminal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AssetBorrowPara {
    private String odr_transactor_id;
    private String bor_user_id;
    private Date expect_rever_date;
    private Date bor_date;
    private String odr_remark;
    private List<String> astids = new ArrayList<>();
    private String user_mobile;
    private String tra_user_name;
    private String bor_user_name;

    public String getOdr_transactor_id() {
        return odr_transactor_id;
    }

    public void setOdr_transactor_id(String odr_transactor_id) {
        this.odr_transactor_id = odr_transactor_id;
    }

    public String getBor_user_id() {
        return bor_user_id;
    }

    public void setBor_user_id(String bor_user_id) {
        this.bor_user_id = bor_user_id;
    }

    public Date getExpect_rever_date() {
        return expect_rever_date;
    }

    public void setExpect_rever_date(Date expect_rever_date) {
        this.expect_rever_date = expect_rever_date;
    }

    public Date getBor_date() {
        return bor_date;
    }

    public void setBor_date(Date bor_date) {
        this.bor_date = bor_date;
    }

    public String getOdr_remark() {
        return odr_remark;
    }

    public void setOdr_remark(String odr_remark) {
        this.odr_remark = odr_remark;
    }

    public List<String> getAstids() {
        return astids;
    }

    public void setAstids(List<String> astids) {
        this.astids = astids;
    }

    public String getUser_mobile() {
        return user_mobile;
    }

    public void setUser_mobile(String user_mobile) {
        this.user_mobile = user_mobile;
    }

    public String getTra_user_name() {
        return tra_user_name;
    }

    public void setTra_user_name(String tra_user_name) {
        this.tra_user_name = tra_user_name;
    }

    public String getBor_user_name() {
        return bor_user_name;
    }

    public void setBor_user_name(String bor_user_name) {
        this.bor_user_name = bor_user_name;
    }

    private String getListString() {
        String astIdString = "[";
        for (int i = 0; i < astids.size(); i++) {
            String astId = astids.get(i);
            astId = "\"" + astId + "\"";
            if (0 < astids.size() - 1) {
                astId += ',';
            }
            astIdString += astId;
        }
        astIdString = astIdString + "]";
        return astIdString;
    }

    @Override
    public String toString() {
        return "{" +
                "\"odr_transactor_id\":\"" + odr_transactor_id + '\"' +
                ",\"bor_user_id\":\"" + bor_user_id + '\"' +
                ",\"expect_rever_date\":" + expect_rever_date.getTime() +
                ",\"bor_date\":" + bor_date.getTime() +
                ",\"odr_remark\":\"" + odr_remark + '\"' +
                ",\"astids\":" + getListString() +
                ",\"user_mobile\":\"" + user_mobile + '\"' +
                ",\"tra_user_name\":\"" + tra_user_name + '\"' +
                ",\"bor_user_name\":\"" + bor_user_name + '\"' +
                '}';
    }
}
