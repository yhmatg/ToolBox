package com.android.toolbox.core.bean;

public class FaceAuthPara {
    private String requestTime;
    private String imgBase64;

    public FaceAuthPara(String requestTime, String imgBase64) {
        this.requestTime = requestTime;
        this.imgBase64 = imgBase64;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getImgBase64() {
        return imgBase64;
    }

    public void setImgBase64(String imgBase64) {
        this.imgBase64 = imgBase64;
    }
}
