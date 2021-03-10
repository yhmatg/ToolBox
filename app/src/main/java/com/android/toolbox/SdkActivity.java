package com.android.toolbox;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.toolbox.app.GlobalClient;
import com.android.toolbox.app.ToolBoxApplication;
import com.android.toolbox.base.activity.BaseActivity;
import com.android.toolbox.base.presenter.AbstractPresenter;
import com.android.toolbox.skrfidbox.Logger;
import com.android.toolbox.skrfidbox.ServerThread;
import com.android.toolbox.skrfidbox.callback.ILockStatusCallback;
import com.android.toolbox.skrfidbox.callback.IRfidReadCallback;
import com.android.toolbox.skrfidbox.econst.ELock;
import com.android.toolbox.skrfidbox.entity.MsgObjBase;
import com.android.toolbox.skrfidbox.entity.Tags;
import com.android.toolbox.utils.ToastUtils;
import com.gg.reader.api.dal.GClient;
import com.gg.reader.api.dal.HandlerTagEpcLog;
import com.gg.reader.api.dal.HandlerTagEpcOver;
import com.gg.reader.api.protocol.gx.EnumG;
import com.gg.reader.api.protocol.gx.LogBaseEpcInfo;
import com.gg.reader.api.protocol.gx.LogBaseEpcOver;
import com.gg.reader.api.protocol.gx.MsgBaseInventoryEpc;
import com.gg.reader.api.protocol.gx.MsgBaseStop;
import com.gg.reader.api.protocol.gx.ParamEpcReadTid;
import com.gg.reader.api.utils.ThreadPoolUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SdkActivity extends BaseActivity {
    private static String TAG = "SdkActivity";
    private GClient client = GlobalClient.getClient();
    private ServerThread serverThread;
    private boolean isInited = false;
    private boolean isInvStarted = false;
    private ParamEpcReadTid tidParam = null;
    private List<String> invEpcs = new ArrayList<>();
    @BindView(R.id.tv_tags)
    TextView tvTags;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_skd;
    }

    @Override
    protected void initToolbar() {

    }

    @Override
    public AbstractPresenter initPresenter() {
        return null;
    }

    @Override
    protected void initEventAndData() {
        serverThread = ToolBoxApplication.getInstance().getServerThread();
    }

    @OnClick({R.id.bt_unlock, R.id.bt_inv, R.id.bt_open_light, R.id.bt_close_light, R.id.bt_open_alarm, R.id.bt_close_alarm})
    public void performClick(View view) {
        switch (view.getId()) {
           case R.id.bt_unlock:
                unlock();
                ToastUtils.showShort("开锁");
                break;
            case R.id.bt_inv:
                startRfid();
                ToastUtils.showShort("盘点");
                break;
            case R.id.bt_open_light:
                sendLightLedOpen();
                ToastUtils.showShort("打开照明");
                break;
            case R.id.bt_close_light:
                sendLightLedClose();
                ToastUtils.showShort("关闭照明");
                break;
            case R.id.bt_open_alarm:
                sendAlarmLedOpen();
                ToastUtils.showShort("打开警报");
                break;
            case R.id.bt_close_alarm:
                sendAlarmLedClose();
                ToastUtils.showShort("关闭警报");
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
                               /* ToastUtils.showShort("OnNotifyReadData add====" + tags.add_tag_list);
                                ToastUtils.showShort("OnNotifyReadData loss====" + tags.loss_tag_list);
                                ToastUtils.showShort("OnNotifyReadData all====" + tags.tag_list);
                                tvTags.setText("OnNotifyReadData\n"
                                        + "add====" + tags.add_tag_list + "\n"
                                        + " loss====" + tags.loss_tag_list + "\n"
                                        + "tags====" + tags.tag_list);*/
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
                                        + "add====" + tags.add_tag_list + "\n"
                                        + " loss====" + tags.loss_tag_list + "\n"
                                        + "tags====" + tags.tag_list.size());
                            }
                        });
                    }
                });
            }
        }).start();
    }

    public void sendLightLedOpen() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                serverThread.getTaskThread().sendLightLedOpen();
            }
        }).start();
    }

    /**
     * 发送关闭照明指令
     */
    public void sendLightLedClose() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                serverThread.getTaskThread().sendLightLedClose();
            }
        }).start();
    }

    /**
     * 发送打开报警指令
     */
    public void sendAlarmLedOpen() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                serverThread.getTaskThread().sendAlarmLedOpen();
            }
        }).start();
    }

    /**
     * 发送关闭报警指令
     */
    public void sendAlarmLedClose() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                serverThread.getTaskThread().sendAlarmLedClose();
            }
        }).start();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isInited){
            client.close();
        }
    }
}
