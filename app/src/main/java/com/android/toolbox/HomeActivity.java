package com.android.toolbox;

import android.content.Intent;
import android.view.View;

import com.android.toolbox.base.activity.BaseActivity;
import com.android.toolbox.contract.HomeContract;
import com.android.toolbox.core.DataManager;
import com.android.toolbox.core.bean.BaseResponse;
import com.android.toolbox.core.bean.terminal.TerminalInfo;
import com.android.toolbox.presenter.HomePresenter;
import com.android.toolbox.skrfidbox.Logger;
import com.android.toolbox.skrfidbox.ServerThread;
import com.android.toolbox.skrfidbox.callback.ILockStatusCallback;
import com.android.toolbox.skrfidbox.callback.IRfidReadCallback;
import com.android.toolbox.skrfidbox.econst.ELock;
import com.android.toolbox.skrfidbox.entity.MsgObjBase;
import com.android.toolbox.skrfidbox.entity.Tags;
import com.android.toolbox.ui.toolquery.ToolQueryActivity;
import com.android.toolbox.ui.verify.VerifyActivity;
import com.android.toolbox.utils.ToastUtils;

import butterknife.OnClick;

public class HomeActivity extends BaseActivity<HomePresenter> implements HomeContract.View {

    private ServerThread serverThread;

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
        serverThread = new ServerThread(5460, 3 * 100000);
        serverThread.start();
    }

    @OnClick({R.id.iv_inout_tool, R.id.iv_query_tool, R.id.iv_manager})
    public void performClick(View view) {
        switch (view.getId()) {
            case R.id.iv_inout_tool:
                startActivity(new Intent(this, VerifyActivity.class));
                break;
            case R.id.iv_query_tool:
                //mPresenter.terminalLogin(new TerminalLoginPara("rfidbox","123456"));
                startRfid();
                break;
            case R.id.iv_manager:
                //startActivity(new Intent(this, ManagerLoginActivity.class));
                unlock();
                break;
        }
    }

    private void unlock() {
        serverThread.getTaskThread().sendLockCmd(ELock.OpenLock, new ILockStatusCallback() {
            @Override
            public void OnOpenLock() {
                Logger.info("OnOpenLock");
                ToastUtils.showShort("OnOpenLock");
            }

            @Override
            public void OnCloseLock() {
                Logger.info("OnCloseLock");
                ToastUtils.showShort("OnCloseLock");
            }
        });
    }

    private void startRfid() {
        serverThread.getTaskThread().sendStartReadTagsCmd(5, new IRfidReadCallback() {

            @Override
            public void OnReceiveData(MsgObjBase msgObj) {

            }

            @Override
            public void OnNotifyReadData(Tags tags) {
                Logger.info("OnNotifyReadData");
                Logger.info(tags);
                ToastUtils.showShort("OnNotifyReadData all_tag_num=======" + tags.all_tag_num);
                ToastUtils.showShort("OnNotifyReadData  add_tag_num=======" + tags.add_tag_num);
                ToastUtils.showShort("OnNotifyReadData loss_tag_num=======" + tags.loss_tag_num);
            }

            @Override
            public void OnGetAllTags(Tags tags) {
                Logger.info("OnGetAllTags");
                Logger.info(tags);
                ToastUtils.showShort("OnGetAllTags all_tag_num=======" + tags.all_tag_num);
                ToastUtils.showShort("OnGetAllTags add_tag_num=======" + tags.add_tag_num);
                ToastUtils.showShort("OnGetAllTags loss_tag_num=======" + tags.loss_tag_num);
            }
        });
    }

    @Override
    public void handleTerminalLogin(BaseResponse<TerminalInfo> terminalInfo) {
        if ("200000".equals(terminalInfo.getCode())) {
            DataManager.getInstance().setToken(terminalInfo.getResult().getId());
            startActivity(new Intent(this, ToolQueryActivity.class));
        }
    }
}
