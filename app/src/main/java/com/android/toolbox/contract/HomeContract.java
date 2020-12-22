package com.android.toolbox.contract;

import com.android.toolbox.base.presenter.AbstractPresenter;
import com.android.toolbox.base.view.AbstractView;
import com.android.toolbox.core.bean.BaseResponse;
import com.android.toolbox.core.bean.terminal.TerminalInfo;
import com.android.toolbox.core.bean.terminal.TerminalLoginPara;

public interface HomeContract {
    interface View extends AbstractView {
        void handleTerminalLogin(BaseResponse<TerminalInfo> terminalInfo);
    }

    interface Presenter extends AbstractPresenter<View> {
        void terminalLogin( TerminalLoginPara terminalLoginPara);
    }
}
