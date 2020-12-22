package com.android.toolbox.core.bean.assist;

import java.util.Objects;

public class FilterBean {
    private String id;
    private String name;
    private Boolean isSelected;

    public FilterBean() {
    }

    public FilterBean(String id, String name, Boolean isSelected) {
        this.id = id;
        this.name = name;
        this.isSelected = isSelected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FilterBean)) return false;
        FilterBean that = (FilterBean) o;
        return getId().equals(that.getId()) &&
                getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }
}
