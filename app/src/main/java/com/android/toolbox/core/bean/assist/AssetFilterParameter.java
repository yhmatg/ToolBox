package com.android.toolbox.core.bean.assist;

import com.multilevel.treelist.Node;

import java.util.List;

public class AssetFilterParameter {
    private List<Node> mSelectAssetsStatus;
    private FilterBean mSelectUseCompany;
    private List<Node> mSelectDepartments;
    private List<Node> mSelectAssetsTypes;
    private List<Node> mSelectAssetsLocations;
    private List<Node> mSelectMangerUsers;
    private String userRealName;

    public List<Node> getmSelectAssetsStatus() {
        return mSelectAssetsStatus;
    }

    public void setmSelectAssetsStatus(List<Node> mSelectAssetsStatus) {
        this.mSelectAssetsStatus = mSelectAssetsStatus;
    }

    public FilterBean getmSelectUseCompany() {
        return mSelectUseCompany;
    }

    public void setmSelectUseCompany(FilterBean mSelectUseCompany) {
        this.mSelectUseCompany = mSelectUseCompany;
    }

    public List<Node> getmSelectDepartments() {
        return mSelectDepartments;
    }

    public void setmSelectDepartments(List<Node> mSelectDepartments) {
        this.mSelectDepartments = mSelectDepartments;
    }

    public List<Node> getmSelectAssetsTypes() {
        return mSelectAssetsTypes;
    }

    public void setmSelectAssetsTypes(List<Node> mSelectAssetsTypes) {
        this.mSelectAssetsTypes = mSelectAssetsTypes;
    }

    public List<Node> getmSelectAssetsLocations() {
        return mSelectAssetsLocations;
    }

    public void setmSelectAssetsLocations(List<Node> mSelectAssetsLocations) {
        this.mSelectAssetsLocations = mSelectAssetsLocations;
    }

    public List<Node> getmSelectMangerUsers() {
        return mSelectMangerUsers;
    }

    public void setmSelectMangerUsers(List<Node> mSelectMangerUsers) {
        this.mSelectMangerUsers = mSelectMangerUsers;
    }

    public String getUserRealName() {
        return userRealName;
    }

    public void setUserRealName(String userRealName) {
        this.userRealName = userRealName;
    }

    @Override
    public String toString() {
        return "[" +
                getStatusListString() + "," +
                getUseCompanyString() + "," +
                getDepartListString() + "," +
                getTypeListString() + "," +
                getLocListString() + "," +
                getManagerListString() + "," +
                "]";
    }

    private String getStatusListString() {
        String statusString = "";
        if (mSelectAssetsStatus != null && mSelectAssetsStatus.size() > 0) {
            statusString += "{\"name\":\"ast_used_status\",\"condition\":\"In\",\"values\":[";
            for (int i = 0; i < mSelectAssetsStatus.size(); i++) {
                Node node = mSelectAssetsStatus.get(i);
                String status = "\"" + node.getId() + "\"";
                if (i < mSelectAssetsStatus.size() - 1) {
                    status += ',';
                }
                statusString += status;
            }
            statusString += "]}";
        }
        return statusString;
    }
    
    private String getUseCompanyString(){
        String companyString = "";
        if(mSelectUseCompany!= null && !"-1".equals(mSelectUseCompany.getId())){
            companyString += "{\"name\":\"org_usedcorp_id\",\"condition\":\"EqualTo\",\"values\":[\"" + mSelectUseCompany.getId() + "\"]}";
        }
        return companyString;
    }

    private String getDepartListString() {
        String departString = "";
        if (mSelectDepartments != null && mSelectDepartments.size() > 0) {
            departString += "{\"name\":\"org_useddept_id\",\"condition\":\"In\",\"values\":[";
            for (int i = 0; i < mSelectDepartments.size(); i++) {
                Node node = mSelectDepartments.get(i);
                String status = "\"" + node.getId() + "\"";
                if (i < mSelectDepartments.size() - 1) {
                    status += ',';
                }
                departString += status;
            }
            departString += "]}";
        }
        return departString;
    }

    private String getTypeListString() {
        String typeString = "";
        if (mSelectAssetsTypes != null && mSelectAssetsTypes.size() > 0) {
            typeString += "{\"name\":\"type_id\",\"condition\":\"In\",\"values\":[";
            for (int i = 0; i < mSelectAssetsTypes.size(); i++) {
                Node node = mSelectAssetsTypes.get(i);
                String status = "\"" + node.getId() + "\"";
                if (i < mSelectAssetsTypes.size() - 1) {
                    status += ',';
                }
                typeString += status;
            }
            typeString += "]}";
        }
        return typeString;
    }

    private String getLocListString() {
        String locString = "";
        if (mSelectAssetsLocations != null && mSelectAssetsLocations.size() > 0) {
            locString += "{\"name\":\"loc_id\",\"condition\":\"In\",\"values\":[";
            for (int i = 0; i < mSelectAssetsLocations.size(); i++) {
                Node node = mSelectAssetsLocations.get(i);
                String status = "\"" + node.getId() + "\"";
                if (i < mSelectAssetsLocations.size() - 1) {
                    status += ',';
                }
                locString += status;
            }
            locString += "]}";
        }
        return locString;
    }

    private String getManagerListString() {
        String managerString = "";
        if (mSelectMangerUsers != null && mSelectMangerUsers.size() > 0) {
            managerString += "{\"name\":\"manager_id\",\"condition\":\"In\",\"values\":[";
            for (int i = 0; i < mSelectMangerUsers.size(); i++) {
                Node node = mSelectMangerUsers.get(i);
                String status = "\"" + node.getId() + "\"";
                if (i < mSelectMangerUsers.size() - 1) {
                    status += ',';
                }
                managerString += status;
            }
            managerString += "]}";
        }
        return managerString;
    }

    public void clearData() {
        mSelectAssetsStatus = null;
        mSelectUseCompany = null;
        mSelectDepartments = null;
        mSelectAssetsTypes = null;
        mSelectAssetsLocations = null;
        mSelectMangerUsers = null;
        userRealName = null;
    }
}
