package com.android.toolbox.ui.verify;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.toolbox.HomeActivity;
import com.android.toolbox.R;
import com.android.toolbox.SerialPortUtil;
import com.android.toolbox.app.GlobalClient;
import com.android.toolbox.app.ToolBoxApplication;
import com.android.toolbox.base.activity.BaseActivity;
import com.android.toolbox.contract.ManageToolContract;
import com.android.toolbox.core.bean.BaseResponse;
import com.android.toolbox.core.bean.assist.AssetFilterParameter;
import com.android.toolbox.core.bean.assist.AssetsListItemInfo;
import com.android.toolbox.core.bean.terminal.AssetBackPara;
import com.android.toolbox.core.bean.terminal.AssetBorrowPara;
import com.android.toolbox.core.bean.terminal.NewBorrowBackPara;
import com.android.toolbox.core.bean.user.UserInfo;
import com.android.toolbox.presenter.ManageToolPresenter;
import com.android.toolbox.skrfidbox.Logger;
import com.android.toolbox.skrfidbox.ServerThread;
import com.android.toolbox.skrfidbox.callback.ILockStatusCallback;
import com.android.toolbox.skrfidbox.callback.IRfidReadCallback;
import com.android.toolbox.skrfidbox.econst.ELock;
import com.android.toolbox.skrfidbox.entity.MsgObjBase;
import com.android.toolbox.skrfidbox.entity.Tags;
import com.android.toolbox.ui.toolquery.AssetListAdapter;
import com.android.toolbox.utils.ScreenSizeUtils;
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
import com.multilevel.treelist.Node;
import com.xuexiang.xlog.XLog;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;

public class BorrowBackToolActivity extends BaseActivity<ManageToolPresenter> implements ManageToolContract.View {
    private static String TAG = "BorrowBackToolActivity";
    private boolean isTest = false;
    @BindView(R.id.rv_result)
    RelativeLayout resultView;
    @BindView(R.id.open_layout)
    RelativeLayout openView;
    @BindView(R.id.loading_layout)
    FrameLayout loadingView;
    @BindView(R.id.rv_tools)
    RecyclerView inOutRecycleView;
    @BindView(R.id.tv_result)
    TextView tvResult;
    @BindView(R.id.iv_loading)
    ImageView waitView;
    @BindView(R.id.test_layout)
    LinearLayout testLayout;
    @BindView(R.id.bottom_layout)
    LinearLayout bottomLayout;
    @BindString(R.string.loc_id)
    String locId;
    @BindString(R.string.loc_name)
    String locName;
    //工具箱中闲置的工具
    private HashMap<String, AssetsListItemInfo> epcToolMap = new HashMap<>();
    private List<String> epcList = new ArrayList<>();
    //盘点到的工具
    private List<String> invEpcList = new ArrayList<>();
    //进出的工具集合
    private List<AssetsListItemInfo> toolList = new ArrayList<>();
    private AssetListAdapter adapter;
    private UserInfo currentUser;
    private List<AssetsListItemInfo> wrongList = new ArrayList<>();
    private Animation anim;
    private MaterialDialog closeDoorDialog;
    private int recLen = 10;
    private TextView autoBack;
    private Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isDestroy) {
                        return;
                    }
                    recLen--;
                    autoBack.setText(getString(R.string.auto_return, recLen));
                    if (recLen < 1) {
                        cancel();
                        startActivity(new Intent(BorrowBackToolActivity.this, HomeActivity.class));
                        finish();
                    }
                }
            });
        }
    };
    private GClient client = GlobalClient.getClient();
    private ParamEpcReadTid tidParam = null;
    private boolean isReader = false;
    private List<String> invEpcs = new ArrayList<>();
    private SerialPortUtil serialPortUtil = SerialPortUtil.getInstance();
    private boolean isDestroy;
    private int currentPage = 1;
    private int pageSize = 500;
    private AssetFilterParameter conditions = new AssetFilterParameter();

    @Override
    public ManageToolPresenter initPresenter() {
        return new ManageToolPresenter();
    }

    @Override
    protected void initEventAndData() {
        List<Node> mSelectAssetsLocations = new ArrayList<>();
        mSelectAssetsLocations.add(new Node(locId, "-1", locName));
        conditions.setmSelectAssetsLocations(mSelectAssetsLocations);
        isTest = getResources().getBoolean(R.bool.is_test);
        if (isTest) {
            testLayout.setVisibility(View.VISIBLE);
        }
        bottomLayout.setVisibility(View.GONE);
        currentUser = ToolBoxApplication.getInstance().getCurrentUser();
        if (currentUser == null) {
            currentUser = new UserInfo();
            UserInfo.DeptInfo deptInfo = new UserInfo.DeptInfo();
            deptInfo.setId("ea722bc877854ac19e910ecfe85d03d6");
            deptInfo.setOrg_name("admin0");
            currentUser.setDeptInfo(deptInfo);
            currentUser.setId("85ddbc5649cb4131adfa1db222fd9d9b");
            currentUser.setUser_real_name("周");
            currentUser.setUser_mobile("18408002925");
        }
        adapter = new AssetListAdapter(toolList, this, true);
        inOutRecycleView.setLayoutManager(new LinearLayoutManager(this));
        inOutRecycleView.setAdapter(adapter);
        //mPresenter.fetchAllAssetsInfos();
        mPresenter.fetchPageAssetsInfos(pageSize, currentPage, "", "", conditions);
        initAnimation();
        if (!isTest) {
            initClient();
            serialPortUtil.totalReceiveSerialPort();
            unlock();
        }
    }

    private void initAnimation() {
        anim = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setFillAfter(true); // 设置保持动画最后的状态
        anim.setDuration(2000); // 设置动画时间
        anim.setRepeatCount(Animation.INFINITE);//设置动画重复次数 无限循环
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatMode(Animation.RESTART);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_borrow_back_tool;
    }

    @Override
    protected void initToolbar() {

    }

    private void unlock() {
        serialPortUtil.sendSerialPort("5A080101000F5D0D");
    }

    @Override
    public void handleFetchAllAssetsInfos(List<AssetsListItemInfo> assetsListItemInfos) {
        Log.e(TAG, "all资产数量是=====" + assetsListItemInfos.size());
        epcToolMap.clear();
        epcList.clear();
        for (AssetsListItemInfo tool : assetsListItemInfos) {
            if (tool.getAst_used_status() == 0 && locName.equals(tool.getLoc_name())) {
                epcList.add(tool.getAst_epc_code());
            }
            if (locName.equals(tool.getLoc_name())) {
                epcToolMap.put(tool.getAst_epc_code(), tool);
            }
        }
    }

    @Override
    public void handleBorrowTools(BaseResponse borrowToolsResponse) {
        if ("200000".equals(borrowToolsResponse.getCode())) {
            showCloseDoorDialog();
            ToastUtils.showShort("借用工具成功");
        } else if ("200002".equals(borrowToolsResponse.getCode())) {
            ToastUtils.showShort("请求参数异常");
        }
    }

    @Override
    public void handleBackTools(BaseResponse backToolsResponse) {
        if ("200000".equals(backToolsResponse.getCode())) {
            showCloseDoorDialog();
            ToastUtils.showShort("归还工具成功");
        } else if ("200002".equals(backToolsResponse.getCode())) {
            ToastUtils.showShort("请求参数异常");
        }
    }

    @Override
    public void handlefetchPageAssetsInfos(List<AssetsListItemInfo> assetsInfos) {
        Log.e(TAG, "page资产数量是=====" + assetsInfos.size());
        epcToolMap.clear();
        epcList.clear();
        for (AssetsListItemInfo tool : assetsInfos) {
            if (tool.getAst_used_status() == 0 && locName.equals(tool.getLoc_name())) {
                epcList.add(tool.getAst_epc_code());
            }
            if (locName.equals(tool.getLoc_name())) {
                epcToolMap.put(tool.getAst_epc_code(), tool);
            }
        }
    }

    @OnClick({R.id.titleLeft, R.id.bt_open_door, R.id.bt_know, R.id.bt_close, R.id.bt_open, R.id.bt_get_tag})
    public void performClick(View view) {
        switch (view.getId()) {
            case R.id.titleLeft:
                finish();
                break;
            case R.id.bt_open_door:
                resultView.setVisibility(View.GONE);
                testOpenClock();
                break;
            case R.id.bt_know:
                finish();
                break;
            case R.id.bt_close:
                testCloseClock();
                break;
            case R.id.bt_open:
                testOpenClock();
                break;
            case R.id.bt_get_tag:
                testOnGetAllTags();
                break;
        }
    }

    public void testOpenClock() {
        openView.setVisibility(View.VISIBLE);
        ToastUtils.showShort("OnOpenLock");
    }

    public void testCloseClock() {
        Log.e(TAG, "testOnCloseLock");
        ToastUtils.showShort("OnCloseLock");
        openView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
        waitView.startAnimation(anim);
    }

    public void testOnGetAllTags() {
        Log.e(TAG, "testOnGetAllTags");
        ToastUtils.showShort("testOnGetAllTags");
        List<String> epcList = new ArrayList<>();
        //todo 添加测试epc
       /* epcList.add("E22021022060197121060202");
        epcList.add("E22021022060197121070202");
        epcList.add("E22021022060197121080202");
        epcList.add("E22021022060197121100202");*/
        handleAllTags(epcList);
    }

    public void showCloseDoorDialog() {
        if (isTest) {
            return;
        }
        if (closeDoorDialog != null && !closeDoorDialog.isShowing()) {
            closeDoorDialog.show();
            timer.schedule(task, 1000, 1000);
        } else {
            View contentView = LayoutInflater.from(this).inflate(R.layout.close_door_dialog, null);
            View goHome = contentView.findViewById(R.id.tv_go_home);
            autoBack = contentView.findViewById(R.id.tv_auto_back);
            goHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeDoorDialog.dismiss();
                    startActivity(new Intent(BorrowBackToolActivity.this, HomeActivity.class));
                    finish();
                }
            });
            MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                    .customView(contentView, false);
            closeDoorDialog = builder
                    .canceledOnTouchOutside(false)
                    .cancelable(false)
                    .show();
            timer.schedule(task, 1000, 1000);
            Window window = closeDoorDialog.getWindow();
            window.setBackgroundDrawableResource(android.R.color.transparent);
            // 设置宽度
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.width = (int) (ScreenSizeUtils.getInstance(getApplication()).getScreenWidth() * 0.75f);
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(layoutParams);
        }
    }

    private void initClient() {
        client.onTagEpcLog = new HandlerTagEpcLog() {
            public void log(String readerName, LogBaseEpcInfo info) {
                if (null != info && 0 == info.getResult()) {
                    Log.e(TAG, "Epc=====" + info.getEpc());
                    if (!invEpcs.contains(info.getEpc())) {
                        invEpcs.add(info.getEpc());
                    }
                }
            }
        };

        client.onTagEpcOver = new HandlerTagEpcOver() {
            public void log(String readerName, LogBaseEpcOver info) {
                Log.e(TAG, "数量：" + invEpcs.size());
                handleAllTags(invEpcs);
            }
        };

        serialPortUtil.setLockListener(new SerialPortUtil.OnLockStatusChangeListener() {
            @Override
            public void onCloseLock() {
                Log.e(TAG, "onCloseLock");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isDestroy) {
                            return;
                        }
                        openView.setVisibility(View.GONE);
                        loadingView.setVisibility(View.VISIBLE);
                        waitView.startAnimation(anim);
                        startInv(true, false);
                        ToastUtils.showShort("OnCloseLock");
                    }
                });
            }

            @Override
            public void onOpenLock() {
                Log.e(TAG, "onOpenLock");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isDestroy) {
                            return;
                        }
                        openView.setVisibility(View.VISIBLE);
                        ToastUtils.showShort("OnOpenLock");
                    }
                });
            }
        });
    }

    private void startInv(boolean isSingleInv, boolean isGetTid) {
        if (ToolBoxApplication.isClient) {
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

    public void handleAllTags(List<String> epcs) {
        Log.e(TAG, "handleAllTags");
        Log.e(TAG, "tags====" + epcs);
        invEpcList.clear();
        wrongList.clear();
        toolList.clear();
        for (String epc : epcs) {
            invEpcList.add(epc);
        }
        Date today = new Date();
        AssetBorrowPara assetBorrowPara = new AssetBorrowPara();
        assetBorrowPara.setOdr_transactor_id(currentUser.getId());
        assetBorrowPara.setBor_user_id(currentUser.getId());
        assetBorrowPara.setExpect_rever_date(new Date(today.getTime() + 604800000));
        assetBorrowPara.setBor_date(today);
        assetBorrowPara.setOdr_remark("");
        assetBorrowPara.setUser_mobile(currentUser.getUser_mobile());
        assetBorrowPara.setTra_user_name(currentUser.getUser_real_name());
        assetBorrowPara.setBor_user_name(currentUser.getUser_real_name());

        AssetBackPara assetBackPara = new AssetBackPara();
        if (currentUser.getDeptInfo() != null) {
            assetBackPara.setBelong_dept_id(currentUser.getDeptInfo().getId());
        }
        assetBackPara.setRev_user_id(currentUser.getId());
        assetBackPara.setRev_user_name(currentUser.getUser_real_name());
        assetBackPara.setActual_rever_date(today);
        assetBackPara.setOdr_remark("back");
        List<String> tempEpcList = new ArrayList<>();
        tempEpcList.addAll(epcList);
        //获取借走的工具
        tempEpcList.removeAll(invEpcList);
        for (String borrowEpc : tempEpcList) {
            AssetsListItemInfo borrowTool = epcToolMap.get(borrowEpc);
            if (borrowTool != null) {
                borrowTool.setAst_used_status(6);
                toolList.add(borrowTool);
                assetBorrowPara.getAstids().add(borrowTool.getId());
            }
        }
        //获取新添加的工具
        invEpcList.removeAll(epcList);
        for (String backEpc : invEpcList) {
            AssetsListItemInfo backTool = epcToolMap.get(backEpc);
            if (backTool != null) {
                if (locName.equals(backTool.getLoc_name())) {
                    backTool.setAst_used_status(0);
                    toolList.add(backTool);
                    assetBackPara.getAst_ids().add(backTool.getId());
                } else {
                    backTool.setAst_used_status(-1);
                    wrongList.add(backTool);
                }
            }
        }
        if (wrongList.size() == 0) {
            if (assetBackPara.getAst_ids().size() > 0) {
                String formData = assetBackPara.toString();
                String title = currentUser.getUser_real_name() + "提交的归还申请";
                mPresenter.backTools(new NewBorrowBackPara(formData, "[]", title));
            }
            if (assetBorrowPara.getAstids().size() > 0) {
                String formData = assetBorrowPara.toString();
                String title = currentUser.getUser_real_name() + "提交的借用申请";
                mPresenter.borrowTools(new NewBorrowBackPara(formData, "[]", title));
            }
            if (assetBackPara.getAst_ids().size() == 0 && assetBorrowPara.getAstids().size() == 0) {
                showCloseDoorDialog();
            }
        } else {
            toolList.clear();
            toolList.addAll(wrongList);
            //todo 开门动作
            if (!isTest) {
                unlock();
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isDestroy) {
                    return;
                }
                loadingView.setVisibility(View.GONE);
                waitView.clearAnimation();
                resultView.setVisibility(View.VISIBLE);
                if (wrongList.size() == 0) {
                    tvResult.setText(BorrowBackToolActivity.this.getString(R.string.maintenance_result, invEpcList.size(), tempEpcList.size()));
                } else {
                    tvResult.setText(BorrowBackToolActivity.this.getString(R.string.wrong_back_result, wrongList.size()));
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroy = true;
        serialPortUtil.setStart(false);
        stopInv();
    }

    public void stopInv() {
        if (ToolBoxApplication.isClient) {
            MsgBaseStop msgStop = new MsgBaseStop();
            ThreadPoolUtils.run(new Runnable() {
                @Override
                public void run() {
                    client.sendSynMsg(msgStop);
                    if (0x00 == msgStop.getRtCode()) {
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
}
