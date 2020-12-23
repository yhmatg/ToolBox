package com.android.toolbox.contract;

import com.android.toolbox.base.presenter.AbstractPresenter;
import com.android.toolbox.base.view.AbstractView;
import com.android.toolbox.core.bean.user.UserInfo;
import com.android.toolbox.core.bean.user.UserLoginResponse;

public interface ManagerLoginContract {
    interface View extends AbstractView {
        void handleLogin(UserLoginResponse userLoginResponse);
    }

    interface Presenter extends AbstractPresenter<View> {
        void login(UserInfo userInfo);
    }
}
