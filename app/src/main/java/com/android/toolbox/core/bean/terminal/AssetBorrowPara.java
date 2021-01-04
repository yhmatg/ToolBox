package com.android.toolbox.core.bean.terminal;

import java.util.Date;
import java.util.List;

public class AssetBorrowPara {
    private String bor_user_id;
    private String bor_user_name;
    private Date bor_date;
    private Date expect_rever_date;
    //非必须
    private String odr_remark;
    private List<String> ast_ids;

    public String getBor_user_id() {
        return bor_user_id;
    }

    public void setBor_user_id(String bor_user_id) {
        this.bor_user_id = bor_user_id;
    }

    public String getBor_user_name() {
        return bor_user_name;
    }

    public void setBor_user_name(String bor_user_name) {
        this.bor_user_name = bor_user_name;
    }

    public Date getBor_date() {
        return bor_date;
    }

    public void setBor_date(Date bor_date) {
        this.bor_date = bor_date;
    }

    public Date getExpect_rever_date() {
        return expect_rever_date;
    }

    public void setExpect_rever_date(Date expect_rever_date) {
        this.expect_rever_date = expect_rever_date;
    }

    public String getOdr_remark() {
        return odr_remark;
    }

    public void setOdr_remark(String odr_remark) {
        this.odr_remark = odr_remark;
    }

    public List<String> getAst_ids() {
        return ast_ids;
    }

    public void setAst_ids(List<String> ast_ids) {
        this.ast_ids = ast_ids;
    }
}
