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
import com.gg.reader.api.dal.HandlerCacheDataOver;
import com.gg.reader.api.dal.HandlerDebugLog;
import com.gg.reader.api.dal.HandlerGpiOver;
import com.gg.reader.api.dal.HandlerGpiStart;
import com.gg.reader.api.dal.HandlerTagEpcLog;
import com.gg.reader.api.dal.HandlerTagEpcOver;
import com.gg.reader.api.dal.communication.AndroidSerialClient;
import com.gg.reader.api.protocol.gx.EnumG;
import com.gg.reader.api.protocol.gx.LogAppGpiOver;
import com.gg.reader.api.protocol.gx.LogAppGpiStart;
import com.gg.reader.api.protocol.gx.LogBaseEpcInfo;
import com.gg.reader.api.protocol.gx.LogBaseEpcOver;
import com.gg.reader.api.protocol.gx.MsgAppGetCacheTagData;
import com.gg.reader.api.protocol.gx.MsgBaseInventoryEpc;
import com.gg.reader.api.protocol.gx.MsgBaseStop;
import com.gg.reader.api.protocol.gx.ParamEpcReadTid;
import com.gg.reader.api.utils.HexUtils;
import com.gg.reader.api.utils.ThreadPoolUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SdkActivity extends BaseActivity implements SerialPortUtil.OnLockStatusChangeListener {
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
    private SerialPortUtil serialPortUtil;

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
        initLock();
        initRfid();
    }

    private void initLock() {
        serialPortUtil = SerialPortUtil.getInstance();
        serialPortUtil.openSrialPort("/dev/ttyXRUSB1", 9600);
        serialPortUtil.setLockListener(this);

    }

    private void initClient() {
        client.onTagEpcLog = new HandlerTagEpcLog() {
            public void log(String readerName, LogBaseEpcInfo info) {
                if (null != info && 0 == info.getResult()) {
                    Log.e(TAG, "EPC-===i" + info.getEpc());
                    if (!invEpcs.contains(info.getEpc())) {
                        invEpcs.add(info.getEpc());
                    }
                }
            }
        };

        client.onTagEpcOver = new HandlerTagEpcOver() {
            public void log(String readerName, LogBaseEpcOver info) {
                Log.e(TAG, "onTagEpcOver=============" + readerName);
                Log.e(TAG, "size=============" + invEpcs.size());
            }
        };
    }

    private void initRfid() {
        if (client.openAndroidSerial("/dev/ttyXRUSB2:115200", 1000)) {
            isReader = true;
            ToolBoxApplication.isClient = true;
            Log.e(TAG, "rfid连接成功");
        } else {
            isReader = false;
            ToolBoxApplication.isClient = false;
            Log.e(TAG, "rfid连接失败");
        }
    }

    @OnClick({R.id.bt_unlock, R.id.bt_inv, R.id.bt_single_inv, R.id.selectAll, R.id.bt_stop_inv,R.id.bt_lock_status})
    public void performClick(View view) {
        switch (view.getId()) {
            case R.id.bt_unlock:
                unlock();
                ToastUtils.showShort("开锁");
                break;
            case R.id.bt_inv:
                invEpcs.clear();
                startInv(false, false);
                ToastUtils.showShort("盘点");
                break;
            case R.id.bt_single_inv:
                invEpcs.clear();
                startInv(true, false);
                ToastUtils.showShort("单次盘点");
                break;
            case R.id.bt_stop_inv:
                stopInv();
                ToastUtils.showShort("停止盘点");
                break;
            case R.id.selectAll:
                selectAll();
                break;
            case R.id.bt_lock_status:
                serialPortUtil.testReceiveSerialPort();
                break;
        }
    }


    private void unlock() {
        if (door1.isChecked()) {
            serialPortUtil.sendSerialPort(door1Text.getText().toString().trim());
        }

        if (door2.isChecked()) {
            serialPortUtil.sendSerialPort(door2Text.getText().toString().trim());
        }

        if (door3.isChecked()) {
            serialPortUtil.sendSerialPort(door3Text.getText().toString().trim());
        }

        if (door4.isChecked()) {
            serialPortUtil.sendSerialPort(door4Text.getText().toString().trim());
        }

        if (door5.isChecked()) {
            serialPortUtil.sendSerialPort(door5Text.getText().toString().trim());
        }

        if (door6.isChecked()) {
            serialPortUtil.sendSerialPort(door6Text.getText().toString().trim());
        }

        if (door7.isChecked()) {
            serialPortUtil.sendSerialPort(door7Text.getText().toString().trim());
        }

        if (door8.isChecked()) {
            serialPortUtil.sendSerialPort(door8Text.getText().toString().trim());
        }
        serialPortUtil.receiveSerialPort();
    }

    private void startInv(boolean isSingleInv, boolean isGetTid) {
        if (ToolBoxApplication.isClient && isReader) {
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
                        Log.e(TAG, "停止成功");
                    } else {
                        Log.e(TAG, "停止失败");

                    }
                }
            });
        } else {
            ToastUtils.showShort("Rfid未连接");
        }
    }

    private void selectAll() {
        if (!isAllSelect) {
            door1.setChecked(true);
            door2.setChecked(true);
            door3.setChecked(true);
            door4.setChecked(true);
            door5.setChecked(true);
            door6.setChecked(true);
            door7.setChecked(true);
            door8.setChecked(true);
            isAllSelect = true;
        } else {
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
        client.close();
        serialPortUtil.closeSerialPort();
    }

    @Override
    public void onCloseLock() {
        Log.e(TAG,"onCloseLock");
    }

    @Override
    public void onOpenLock() {
        Log.e(TAG,"onOpenLock");
    }
}
