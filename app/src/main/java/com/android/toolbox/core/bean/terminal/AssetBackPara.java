package com.android.toolbox.core.bean.terminal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AssetBackPara {
    //非必须
    private String belong_dept_id;
    private String rev_user_id;
    private String rev_user_name;
    private Date actual_rever_date;
    private String odr_remark;
    private List<String> ast_ids = new ArrayList<>();

    public String getBelong_dept_id() {
        return belong_dept_id;
    }

    public void setBelong_dept_id(String belong_dept_id) {
        this.belong_dept_id = belong_dept_id;
    }

    public String getRev_user_id() {
        return rev_user_id;
    }

    public void setRev_user_id(String rev_user_id) {
        this.rev_user_id = rev_user_id;
    }

    public String getRev_user_name() {
        return rev_user_name;
    }

    public void setRev_user_name(String rev_user_name) {
        this.rev_user_name = rev_user_name;
    }

    public Date getActual_rever_date() {
        return actual_rever_date;
    }

    public void setActual_rever_date(Date actual_rever_date) {
        this.actual_rever_date = actual_rever_date;
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

    @Override
    public String toString() {
        return  "{" +
                "\"belong_dept_id\":\"" + belong_dept_id + '\"' +
                ",\"rev_user_id\":\"" + rev_user_id + '\"' +
                ",\"rev_user_name\":\"" + rev_user_name + '\"' +
                ",\"actual_rever_date\":" + actual_rever_date.getTime() +
                ",\"odr_remark\":\"" + odr_remark + '\"' +
                ",\"ast_ids\":" + getListString() +
                '}';
    }

    private String getListString() {
        String astIdString = "[";
        for (int i = 0; i < ast_ids.size(); i++) {
            String astId = ast_ids.get(i);
            astId = "\"" + astId + "\"";
            if (0 < ast_ids.size() - 1) {
                astId += ',';
            }
            astIdString += astId;
        }
        astIdString = astIdString + "]";
        return astIdString;
    }
}
