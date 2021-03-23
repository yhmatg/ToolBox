package com.android.toolbox.core.bean;

public class FaceResponse {

    /**
     * code : 1
     * msg : SUCCESS
     * data : {"workNo":"xxxx"}
     */

    private int code;
    private String msg;
    private Data data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        /**
         * workNo : xxxx
         */

        private String workNo;

        public String getWorkNo() {
            return workNo;
        }

        public void setWorkNo(String workNo) {
            this.workNo = workNo;
        }
    }
}
