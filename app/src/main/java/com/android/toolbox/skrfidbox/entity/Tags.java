package com.android.toolbox.skrfidbox.entity;

import java.util.List;
import java.util.Objects;

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

    public static class _tag {
        public String epc;

        public _tag() {

        }
        public _tag(String epc) {
            this.epc = epc;
        }

        @Override
        public String toString() {
            return epc;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof _tag)) return false;
            _tag tag = (_tag) o;
            return epc.equals(tag.epc);
        }

        @Override
        public int hashCode() {
            return Objects.hash(epc);
        }
    }

}
