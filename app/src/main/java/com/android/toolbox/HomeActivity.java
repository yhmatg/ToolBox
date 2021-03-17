package com.android.toolbox;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.android.toolbox.base.activity.BaseActivity;
import com.android.toolbox.contract.HomeContract;
import com.android.toolbox.core.DataManager;
import com.android.toolbox.core.bean.BaseResponse;
import com.android.toolbox.core.bean.terminal.TerminalInfo;
import com.android.toolbox.core.bean.terminal.TerminalLoginPara;
import com.android.toolbox.presenter.HomePresenter;
import com.android.toolbox.ui.manager.ManagerLoginActivity;
import com.android.toolbox.ui.toolquery.ToolQueryActivity;
import com.android.toolbox.ui.verify.VerifyActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class HomeActivity extends BaseActivity<HomePresenter> implements HomeContract.View {
    @BindView(R.id.tv_code)
    TextView tvCode;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initToolbar() {

    }

    @Override
    public HomePresenter initPresenter() {
        return new HomePresenter();
    }

    @Override
    protected void initEventAndData() {
        String SerialNumber = android.os.Build.SERIAL;
        tvCode.setText(SerialNumber);
    }

    @OnClick({R.id.iv_inout_tool, R.id.iv_query_tool, R.id.iv_manager})
    public void performClick(View view) {
        switch (view.getId()) {
            case R.id.iv_inout_tool:
                startActivity(new Intent(this, VerifyActivity.class));
                break;
            case R.id.iv_query_tool:
                mPresenter.terminalLogin(new TerminalLoginPara("rfidbox","123456"));
                break;
            case R.id.iv_manager:
                startActivity(new Intent(this, ManagerLoginActivity.class));
                break;
        }
    }

    @Override
    public void handleTerminalLogin(BaseResponse<TerminalInfo> terminalInfo) {
        if ("200000".equals(terminalInfo.getCode())) {
            DataManager.getInstance().setToken(terminalInfo.getResult().getId());
            startActivity(new Intent(this, ToolQueryActivity.class));
        }
    }
}
