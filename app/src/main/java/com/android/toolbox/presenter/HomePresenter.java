package com.android.toolbox.presenter;

import com.android.toolbox.base.presenter.BasePresenter;
import com.android.toolbox.contract.HomeContract;
import com.android.toolbox.core.DataManager;
import com.android.toolbox.core.bean.BaseResponse;
import com.android.toolbox.core.bean.terminal.TerminalInfo;
import com.android.toolbox.core.bean.terminal.TerminalLoginPara;
import com.android.toolbox.core.http.widget.BaseObserver;
import com.android.toolbox.utils.RxUtils;

public class HomePresenter extends BasePresenter<HomeContract.View> implements HomeContract.Presenter {
    @Override
    public void terminalLogin(TerminalLoginPara terminalLoginPara) {
        addSubscribe(DataManager.getInstance().terminalLogin(terminalLoginPara)
                .compose(RxUtils.rxSchedulerHelper())
                .subscribeWith(new BaseObserver<BaseResponse<TerminalInfo>>(mView, false) {
                    @Override
                    public void onNext(BaseResponse<TerminalInfo> terminalInfoBaseResponse) {
                        mView.handleTerminalLogin(terminalInfoBaseResponse);
                    }
                }));
    }
}
