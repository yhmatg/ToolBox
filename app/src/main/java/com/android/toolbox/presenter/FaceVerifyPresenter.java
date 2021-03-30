package com.android.toolbox.presenter;

import com.android.toolbox.base.presenter.BasePresenter;
import com.android.toolbox.contract.FaceVerifyContract;
import com.android.toolbox.core.DataManager;
import com.android.toolbox.core.bean.BaseResponse;
import com.android.toolbox.core.bean.terminal.FaceAuthPara;
import com.android.toolbox.core.bean.user.FaceLoginPara;
import com.android.toolbox.core.bean.user.UserLoginResponse;
import com.android.toolbox.core.http.widget.BaseObserver;
import com.android.toolbox.utils.RxUtils;

import okhttp3.ResponseBody;

public class FaceVerifyPresenter extends BasePresenter<FaceVerifyContract.View> implements FaceVerifyContract.Presenter {

    @Override
    public void getUserByFace(String appId, String signature, String requestId, FaceAuthPara faceAuthPara) {
        addSubscribe(DataManager.getInstance().getUserByFace(appId, signature, requestId, faceAuthPara)
                .compose(RxUtils.rxSchedulerHelper())
                .subscribeWith(new BaseObserver<ResponseBody>(mView, false) {
                    @Override
                    public void onNext(ResponseBody responseBody) {
                        mView.handleGetUserByFace(responseBody);
                    }
                }));
    }

    @Override
    public void faceLogin(FaceLoginPara faceLoginPara) {
        addSubscribe(DataManager.getInstance().faceLogin(faceLoginPara)
                .compose(RxUtils.handleResult())
                .compose(RxUtils.rxSchedulerHelper())
                .subscribeWith(new BaseObserver<UserLoginResponse>(mView, false) {

                    @Override
                    public void onNext(UserLoginResponse userLoginResponse) {
                        mView.handleFaceLogin(userLoginResponse);
                    }
                }));
    }
}
