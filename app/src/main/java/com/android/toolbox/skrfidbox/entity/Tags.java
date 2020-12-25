package com.android.toolbox.skrfidbox.entity;

import java.util.List;

/**
 * 盘点到的标签
 */
public class Tags {
    public int all_tag_num;
    public int add_tag_num;
    public int loss_tag_num;
    public List<_tag> add_tag_list;
    public List<_tag> loss_tag_list;
    public List<_tag> tag_list;

    static class _tag {
        public String epc;
    }
}
