package com.android.toolbox.contract;

import com.android.toolbox.base.presenter.AbstractPresenter;
import com.android.toolbox.base.view.AbstractView;
import com.android.toolbox.core.bean.terminal.FaceAuthPara;
import com.android.toolbox.core.bean.user.FaceLoginPara;
import com.android.toolbox.core.bean.user.UserLoginResponse;

import okhttp3.ResponseBody;

public interface FaceVerifyContract {
    interface View extends AbstractView {
        void handleGetUserByFace(ResponseBody responseBody);

        void handleFaceLogin(UserLoginResponse loginResponse);
    }

    interface Presenter extends AbstractPresenter<View> {
        void getUserByFace(String appId, String signature, String requestId, FaceAuthPara faceAuthPara);

        void faceLogin(FaceLoginPara faceLoginPara);
    }
}
