package com.android.toolbox.presenter;

import com.android.toolbox.base.presenter.BasePresenter;
import com.android.toolbox.contract.ManagerLoginContract;
import com.android.toolbox.core.DataManager;
import com.android.toolbox.core.bean.user.UserInfo;
import com.android.toolbox.core.bean.user.UserLoginResponse;
import com.android.toolbox.core.http.widget.BaseObserver;
import com.android.toolbox.utils.RxUtils;

public class ManagerLoginPresenter extends BasePresenter<ManagerLoginContract.View> implements ManagerLoginContract.Presenter {
    @Override
    public void login(UserInfo userInfo) {
        addSubscribe(DataManager.getInstance().login(userInfo)
        .compose(RxUtils.handleResult())
        .compose(RxUtils.rxSchedulerHelper())
        .subscribeWith(new BaseObserver<UserLoginResponse>(mView, false) {
            @Override
            public void onNext(UserLoginResponse userLoginResponse) {
                mView.handleLogin(userLoginResponse);
            }
        }));
    }
}
