package com.android.toolbox;

import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.gg.reader.api.dal.HandlerGpiStart;
import com.gg.reader.api.dal.HandlerTagEpcLog;
import com.gg.reader.api.dal.HandlerTagEpcOver;
import com.gg.reader.api.protocol.gx.EnumG;
import com.gg.reader.api.protocol.gx.LogAppGpiStart;
import com.gg.reader.api.protocol.gx.LogBaseEpcInfo;
import com.gg.reader.api.protocol.gx.LogBaseEpcOver;
import com.gg.reader.api.protocol.gx.MsgBaseInventoryEpc;
import com.gg.reader.api.protocol.gx.MsgBaseStop;
import com.gg.reader.api.protocol.gx.ParamEpcReadTid;
import com.gg.reader.api.utils.HexUtils;
import com.gg.reader.api.utils.ThreadPoolUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SdkActivity extends BaseActivity {
    private static String TAG = "SdkActivity";
    @BindView(R.id.tv_tags)
    TextView tvTags;
    @BindView(R.id.door1)
    CheckBox door1;
    @BindView(R.id.door1Text)
    EditText door1Text;
    @BindView(R.id.door2)
    CheckBox door2;
    @BindView(R.id.door2Text)
    EditText door2Text;
    @BindView(R.id.door3)
    CheckBox door3;
    @BindView(R.id.door3Text)
    EditText door3Text;
    @BindView(R.id.door4)
    CheckBox door4;
    @BindView(R.id.door4Text)
    EditText door4Text;
    @BindView(R.id.door5)
    CheckBox door5;
    @BindView(R.id.door5Text)
    EditText door5Text;
    @BindView(R.id.door6)
    CheckBox door6;
    @BindView(R.id.door6Text)
    EditText door6Text;
    @BindView(R.id.door7)
    CheckBox door7;
    @BindView(R.id.door7Text)
    EditText door7Text;
    @BindView(R.id.door8)
    CheckBox door8;
    @BindView(R.id.door8Text)
    EditText door8Text;
    private GClient client = GlobalClient.getClient();
    private List<String> invEpcs = new ArrayList<>();
    private boolean isReader = false;
    private ParamEpcReadTid tidParam = null;
    private boolean isAllSelect;

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
        initClient();
    }

    private void initLock() {
        if (client.openAndroidSerial("/dev/ttyXRUSB1:115200", 0)) {
            ToastUtils.showShort("串口连接成功");
        } else {
            ToastUtils.showShort("串口连接失败");
        }
    }

    private void initClient() {
        client.onTagEpcLog = new HandlerTagEpcLog() {
            public void log(String readerName, LogBaseEpcInfo info) {
                if (null != info && 0 == info.getResult()) {
                    if (invEpcs.contains(info.getEpc())) {
                        invEpcs.add(info.getEpc());
                    }
                }
            }
        };

        client.onTagEpcOver = new HandlerTagEpcOver() {
            public void log(String readerName, LogBaseEpcOver info) {
                tvTags.setText("数量：" + invEpcs.size() + "\n" + invEpcs.toString());
            }
        };

        client.onGpiStart = new HandlerGpiStart() {
            @Override
            public void log(String readerName, LogAppGpiStart info) {

            }
        };
    }

    private void initRfid() {
        ThreadPoolUtils.run(new Runnable() {//此线程池为jar内封装工具类，高版本tcp开发可用此工具类
            @Override
            public void run() {
                if (client.openTcp("127.0.0.1:8160", 0)) {
                    isReader = true;
                    ToolBoxApplication.isClient = true;
                    Log.e(TAG,"rfid连接成功");
                } else {
                    isReader = false;
                    ToolBoxApplication.isClient = false;
                    Log.e(TAG,"rfid连接失败");
                }
            }
        });
    }

    @OnClick({R.id.bt_unlock, R.id.bt_inv, R.id.bt_single_inv, R.id.selectAll})
    public void performClick(View view) {
        switch (view.getId()) {
            case R.id.bt_unlock:
                initLock();
                unlock();
                ToastUtils.showShort("开锁");
                break;
            case R.id.bt_inv:
                initRfid();
                invEpcs.clear();
                ToastUtils.showShort("盘点");
                startInv(false, false);
                break;
            case R.id.bt_single_inv:
                initRfid();
                invEpcs.clear();
                ToastUtils.showShort("单次盘点");
                startInv(true, false);
                break;
            case R.id.bt_stop_inv:
                stopInv();
                ToastUtils.showShort("停止盘点");
                break;
            case R.id.selectAll:
                selectAll();
                break;
        }
    }


    private void unlock() {
        if (door1.isChecked()) {
            client.sendUnsynMsg(HexUtils.hexString2Bytes(door1Text.getText().toString().trim()));
        }

        if (door2.isChecked()) {
            client.sendUnsynMsg(HexUtils.hexString2Bytes(door2Text.getText().toString().trim()));
        }

        if (door3.isChecked()) {
            client.sendUnsynMsg(HexUtils.hexString2Bytes(door3Text.getText().toString().trim()));
        }

        if (door4.isChecked()) {
            client.sendUnsynMsg(HexUtils.hexString2Bytes(door4Text.getText().toString().trim()));
        }

        if (door5.isChecked()) {
            client.sendUnsynMsg(HexUtils.hexString2Bytes(door5Text.getText().toString().trim()));
        }

        if (door6.isChecked()) {
            client.sendUnsynMsg(HexUtils.hexString2Bytes(door6Text.getText().toString().trim()));
        }

        if (door7.isChecked()) {
            client.sendUnsynMsg(HexUtils.hexString2Bytes(door7Text.getText().toString().trim()));
        }

        if (door8.isChecked()) {
            client.sendUnsynMsg(HexUtils.hexString2Bytes(door8Text.getText().toString().trim()));
        }

    }

    private void startInv(boolean isSingleInv, boolean isGetTid) {
        if (ToolBoxApplication.isClient && !isReader) {
            MsgBaseInventoryEpc msg = new MsgBaseInventoryEpc();
            msg.setAntennaEnable(getAnt());
            if (isSingleInv) {
                msg.setInventoryMode(EnumG.InventoryMode_Single);
            } else {
                msg.setInventoryMode(EnumG.InventoryMode_Inventory);
            }
            if (isGetTid) {
                tidParam = new ParamEpcReadTid();
                tidParam.setMode(EnumG.ParamTidMode_Auto);
                tidParam.setLen(6);
                msg.setReadTid(tidParam);
            } else {
                tidParam = null;
            }
            ThreadPoolUtils.run(new Runnable() {
                @Override
                public void run() {
                    client.sendSynMsg(msg);
                    //获取操作结果成功还失败
                    if (0x00 == msg.getRtCode()) {
                        //todo 操作成功
                        isReader = true;
                    } else {
                        //todo 操作失败
                        isReader = false;
                    }
                }
            });
        }
    }

    private long getAnt() {
        StringBuffer buffer = new StringBuffer("11111111");
        return Long.valueOf(buffer.reverse().toString(), 2);
    }

    public void stopInv() {
        if (ToolBoxApplication.isClient) {
            MsgBaseStop msgStop = new MsgBaseStop();
            ThreadPoolUtils.run(new Runnable() {
                @Override
                public void run() {
                    client.sendSynMsg(msgStop);
                    if (0x00 == msgStop.getRtCode()) {
                        isReader = false;
                        ToastUtils.showShort("停止成功");
                    } else {
                        ToastUtils.showShort("停止失败");
                    }
                }
            });
        } else {
            ToastUtils.showShort("Rfid未连接");
        }
    }

    private void selectAll(){
        if(!isAllSelect){
            door1.setChecked(true);
            door2.setChecked(true);
            door3.setChecked(true);
            door4.setChecked(true);
            door5.setChecked(true);
            door6.setChecked(true);
            door7.setChecked(true);
            door8.setChecked(true);
            isAllSelect = true;
        }else {
            door1.setChecked(false);
            door2.setChecked(false);
            door3.setChecked(false);
            door4.setChecked(false);
            door5.setChecked(false);
            door6.setChecked(false);
            door7.setChecked(false);
            door8.setChecked(false);
            isAllSelect = false;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(ToolBoxApplication.isClient){
            client.close();
        }
    }
}
