package com.android.toolbox;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.android.toolbox.app.ToolBoxApplication;
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
import com.android.toolbox.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class HomeActivity extends BaseActivity<HomePresenter> implements HomeContract.View {

    private ServerThread serverThread;
    @BindView(R.id.tv_tags)
    TextView tvTags;

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
        //serverThread = new ServerThread(2622, 3 * 100000);
        //serverThread.start();
        serverThread = ToolBoxApplication.getInstance().getServerThread();
        serverThread.start();
    }

    @OnClick({R.id.iv_inout_tool, R.id.iv_query_tool, R.id.iv_manager})
    public void performClick(View view) {
        switch (view.getId()) {
            case R.id.iv_inout_tool:
                //startActivity(new Intent(this, VerifyActivity.class));
                break;
            case R.id.iv_query_tool:
                //mPresenter.terminalLogin(new TerminalLoginPara("rfidbox","123456"));
                startRfid();
                ToastUtils.showShort("点击了盘点");
                break;
            case R.id.iv_manager:
                //startActivity(new Intent(this, ManagerLoginActivity.class));
                unlock();
                ToastUtils.showShort("点击的开锁");
                break;
        }
    }

    private void unlock() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                serverThread.getTaskThread().sendLockCmd(ELock.OpenLock, new ILockStatusCallback() {
                    @Override
                    public void OnOpenLock() {
                        Logger.info("OnOpenLock");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showShort("OnOpenLock");
                            }
                        });
                    }

                    @Override
                    public void OnCloseLock() {
                        Logger.info("OnCloseLock");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showShort("OnCloseLock");
                            }
                        });
                    }
                });
            }
        }).start();

    }

    private void startRfid() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                serverThread.getTaskThread().sendStartReadTagsCmd(5, new IRfidReadCallback() {

                    @Override
                    public void OnReceiveData(MsgObjBase msgObj) {

                    }

                    @Override
                    public void OnNotifyReadData(Tags tags) {
                        Logger.info("OnNotifyReadData");
                        Logger.info(tags);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showShort("OnNotifyReadData add====" + tags.add_tag_list);
                                ToastUtils.showShort("OnNotifyReadData loss====" + tags.loss_tag_list);
                                ToastUtils.showShort("OnNotifyReadData all====" + tags.tag_list);
                                tvTags.setText("OnNotifyReadData\n"
                                        +"add====" + tags.add_tag_list + "\n"
                                        + " loss====" + tags.loss_tag_list + "\n"
                                        + "tags====" + tags.tag_list);
                            }
                        });
                    }

                    @Override
                    public void OnGetAllTags(Tags tags) {
                        Logger.info("OnGetAllTags");
                        Logger.info(tags);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showShort("OnGetAllTags add====" + tags.add_tag_list);
                                ToastUtils.showShort("OnGetAllTags loss====" + tags.loss_tag_list);
                                ToastUtils.showShort("OnGetAllTags tags====" + tags.tag_list);
                                tvTags.setText("OnGetAllTags\n"
                                        +"add====" + tags.add_tag_list + "\n"
                                        + " loss====" + tags.loss_tag_list + "\n"
                                        + "tags====" + tags.tag_list);
                            }
                        });
                    }
                });
            }
        }).start();

    }

    @Override
    public void handleTerminalLogin(BaseResponse<TerminalInfo> terminalInfo) {
        if ("200000".equals(terminalInfo.getCode())) {
            DataManager.getInstance().setToken(terminalInfo.getResult().getId());
            startActivity(new Intent(this, ToolQueryActivity.class));
        }
    }
}
