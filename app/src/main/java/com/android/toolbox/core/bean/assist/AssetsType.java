package com.android.toolbox.core.bean.assist;

import com.contrarywind.interfaces.IPickerViewData;

import java.util.Objects;

public class AssetsType implements IPickerViewData {

    /**
     * create_date : 1574674207000
     * id : 2b0ab8bf0f6611eaabcf00163e0a6695
     * type_code : 0001
     * type_feilds : []
     * type_img_url :
     * type_isleaf : 1
     * type_name : 类型1
     * update_date : 1574708690000
     */

    private String id;
    private String type_code;
    private String type_name;
    private String type_superid;
    private Integer type_isleaf;

    public Integer getType_isleaf() {
        return type_isleaf;
    }

    public void setType_isleaf(Integer type_isleaf) {
        this.type_isleaf = type_isleaf;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType_code() {
        return type_code;
    }

    public void setType_code(String type_code) {
        this.type_code = type_code;
    }


    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    public String getType_superid() {
        return type_superid;
    }

    public void setType_superid(String type_superid) {
        this.type_superid = type_superid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AssetsType)) return false;
        AssetsType that = (AssetsType) o;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String getPickerViewText() {
        return type_name;
    }
}
