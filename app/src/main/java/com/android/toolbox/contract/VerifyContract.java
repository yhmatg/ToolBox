package com.android.toolbox.contract;

import com.android.toolbox.base.presenter.AbstractPresenter;
import com.android.toolbox.base.view.AbstractView;

public interface VerifyContract {
    interface View extends AbstractView {
    }

    interface Presenter extends AbstractPresenter<View> {
    }
}
