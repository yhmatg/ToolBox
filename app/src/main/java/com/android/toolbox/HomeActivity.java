package com.android.toolbox;

import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.toolbox.app.Constants;
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
import com.arcsoft.face.ActiveFileInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.enums.RuntimeABI;
import com.gg.reader.api.dal.GClient;
import com.gg.reader.api.utils.ThreadPoolUtils;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HomeActivity extends BaseActivity<HomePresenter> implements HomeContract.View {
    private static String TAG = "HomeActivity";
    @BindView(R.id.tv_code)
    TextView tvCode;
    private GClient client = GlobalClient.getClient();
    private SerialPortUtil serialPortUtil;

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
        initLockAndRfid();
        boolean faceActiveStatus = DataManager.getInstance().getFaceActiveStatus();
        if(!faceActiveStatus){
            activeEngine();
        }

    }

    @OnClick({R.id.ll_tool_inout, R.id.ll_tool_query, R.id.tool_manage})
    public void performClick(View view) {
        switch (view.getId()) {
            case R.id.ll_tool_inout:
                startActivity(new Intent(this, VerifyActivity.class));
                break;
            case R.id.ll_tool_query:
                mPresenter.terminalLogin(new TerminalLoginPara("rfidbox", "123456"));
                break;
            case R.id.tool_manage:
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

    private void initLockAndRfid() {
        //初始化锁
        serialPortUtil = SerialPortUtil.getInstance();
        serialPortUtil.openSrialPort("/dev/ttyXRUSB1", 9600);
        //初始化rfid
        if (client.openAndroidSerial("/dev/ttyXRUSB2:115200", 500)) {
            ToolBoxApplication.isClient = true;
            Log.e(TAG, "rfid连接成功");
        } else {
            ToolBoxApplication.isClient = false;
            Log.e(TAG, "rfid连接失败");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ToolBoxApplication.isClient) {
            Log.e("退出应用", "断开连接");
            client.close();
            serialPortUtil.closeSerialPort();
        }
    }

    public void activeEngine() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) {
                RuntimeABI runtimeABI = FaceEngine.getRuntimeABI();
                Log.i(TAG, "subscribe: getRuntimeABI() " + runtimeABI);

                long start = System.currentTimeMillis();
                int activeCode = FaceEngine.activeOnline(HomeActivity.this, Constants.APP_ID, Constants.SDK_KEY);
                Log.i(TAG, "subscribe cost: " + (System.currentTimeMillis() - start));
                emitter.onNext(activeCode);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer activeCode) {
                        if (activeCode == ErrorInfo.MOK) {
                            ToastUtils.showShort("激活成功");
                            DataManager.getInstance().saveFaceActiveStatus(true);
                        } else if (activeCode == ErrorInfo.MERR_ASF_ALREADY_ACTIVATED) {
                            ToastUtils.showShort("已经激活");
                            DataManager.getInstance().saveFaceActiveStatus(true);
                        } else {
                            ToastUtils.showShort("激活失败");
                        }
                        ActiveFileInfo activeFileInfo = new ActiveFileInfo();
                        int res = FaceEngine.getActiveFileInfo(HomeActivity.this, activeFileInfo);
                        if (res == ErrorInfo.MOK) {
                            Log.i("HomeActivity", activeFileInfo.toString());
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        showToast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }
}
