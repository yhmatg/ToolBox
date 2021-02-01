package com.android.toolbox;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.android.toolbox.app.GlobalClient;
import com.android.toolbox.app.ToolBoxApplication;
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
import com.android.toolbox.utils.ToastUtils;
import com.gg.reader.api.dal.GClient;
import com.gg.reader.api.utils.ThreadPoolUtils;

import butterknife.OnClick;

public class HomeActivity extends BaseActivity<HomePresenter> implements HomeContract.View {
    private GClient client = GlobalClient.getClient();
    private boolean isClient = false;

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
        initToolCarInventory();
    }

    @OnClick({R.id.iv_inout_tool, R.id.iv_query_tool, R.id.iv_manager})
    public void performClick(View view) {
        switch (view.getId()) {
            case R.id.iv_inout_tool:
                startActivity(new Intent(this, VerifyActivity.class));
                break;
            case R.id.iv_query_tool:
                mPresenter.terminalLogin(new TerminalLoginPara("rfidbox", "123456"));
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

    private void initToolCarInventory() {
        ThreadPoolUtils.run(new Runnable() {//此线程池为jar内封装工具类，高版本tcp开发可用此工具类
            @Override
            public void run() {
                if (client.openTcp("127.0.0.1:8160", 0)) {
                    isClient = true;
                    ToolBoxApplication.isClient = true;
                    ToastUtils.showShort("连接成功");
                } else {
                    isClient = false;
                    ToolBoxApplication.isClient = false;
                    ToastUtils.showShort("连接失败");
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isClient) {
            Log.e("退出应用", "断开连接");
            client.close();
        }
    }
}
