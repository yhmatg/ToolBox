package com.android.toolbox.ui.verify;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.toolbox.HomeActivity;
import com.android.toolbox.R;
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
import com.multilevel.treelist.Node;
import com.xuexiang.xlog.XLog;

import java.io.IOException;
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
   /* @BindView(R.id.iv_loading)
    ImageView waitView;*/
    @BindView(R.id.test_layout)
    LinearLayout testLayout;
    @BindView(R.id.bottom_layout)
    LinearLayout bottomLayout;
    @BindString(R.string.loc_id)
    String locId;
    @BindString(R.string.loc_name)
    String locName;
    private ServerThread serverThread;
    //???????????????????????????
    private HashMap<String, AssetsListItemInfo> epcToolMap = new HashMap<>();
    private List<String> epcList = new ArrayList<>();
    //??????????????????
    private List<String> invEpcList = new ArrayList<>();
    //?????????????????????
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
    private boolean isDestroy;
    private int currentPage = 1;
    private int pageSize = 500;
    private AssetFilterParameter conditions = new AssetFilterParameter();
    private boolean isAutoReopen = false;
    private AssetManager assetManager;
    public List<Tags._tag> notifyAddTags = new ArrayList<>();
    public List<Tags._tag> notifyDeleteTags = new ArrayList<>();

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
            currentUser.setUser_real_name("???");
            currentUser.setUser_mobile("18408002925");
        }
        adapter = new AssetListAdapter(toolList, this, true);
        inOutRecycleView.setLayoutManager(new LinearLayoutManager(this));
        inOutRecycleView.setAdapter(adapter);
        mPresenter.fetchAllAssetsInfos();
        //mPresenter.fetchPageAssetsInfos(pageSize, currentPage, "", "", conditions);
        serverThread = ToolBoxApplication.getInstance().getServerThread();
        //initAnimation();
        //todo ????????????
        if (!isTest) {
            isAutoReopen = false;
            unlock();
        }

    }

    private void initAnimation() {
        anim = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setFillAfter(true); // ?????????????????????????????????
        anim.setDuration(2000); // ??????????????????
        anim.setRepeatCount(Animation.INFINITE);//???????????????????????? ????????????
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                serverThread.getTaskThread().sendLockCmd(ELock.OpenLock, new ILockStatusCallback() {
                    @Override
                    public void OnOpenLock() {
                        Logger.info("OnOpenLock");
                        playMusic("open_success.mp3");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isDestroy) {
                                    return;
                                }
                                if (isAutoReopen) {
                                    loadingView.setVisibility(View.GONE);
                                    resultView.setVisibility(View.VISIBLE);
                                    openView.setVisibility(View.GONE);
                                } else {
                                    loadingView.setVisibility(View.GONE);
                                    resultView.setVisibility(View.GONE);
                                    openView.setVisibility(View.VISIBLE);
                                }
                                ToastUtils.showShort("OnOpenLock");
                            }
                        });
                    }

                    @Override
                    public void OnCloseLock() {
                        Logger.info("OnCloseLock");
                        playMusic("close_success.mp3");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isDestroy) {
                                    return;
                                }
                                openView.setVisibility(View.GONE);
                                loadingView.setVisibility(View.VISIBLE);
                                //waitView.startAnimation(anim);
                                startRfid();
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
                        notifyAddTags.clear();
                        notifyDeleteTags.clear();
                        if (tags.add_tag_list != null) {
                            notifyAddTags.addAll(tags.add_tag_list);
                        }
                        if (tags.loss_tag_list != null) {
                            notifyDeleteTags.addAll(tags.loss_tag_list);
                        }
                    }

                    @Override
                    public void OnGetAllTags(Tags tags) {
                        notifyAddTags.removeAll(tags.tag_list);
                        if (notifyAddTags.size() > 0) {
                            XLog.get().e("BorrowBackToolActivity exteraTag number=======" + notifyAddTags.size() + "\nexteraTags==" + notifyAddTags);
                            tags.tag_list.addAll(notifyAddTags);
                        }
                        if (notifyDeleteTags.size() > 0) {
                            tags.tag_list.removeAll(notifyDeleteTags);
                        }
                        XLog.get().e("BorrowBackToolActivity end allTag number=======" + tags.tag_list.size() + "\nallTags==" + tags.tag_list);
                        handleAllTags(tags);
                    }
                });
            }
        }).start();
    }

    @Override
    public void handleFetchAllAssetsInfos(List<AssetsListItemInfo> assetsListItemInfos) {
        Log.e(TAG, "all???????????????=====" + assetsListItemInfos.size());
        epcToolMap.clear();
        epcList.clear();
        for (AssetsListItemInfo tool : assetsListItemInfos) {
            epcToolMap.put(tool.getAst_epc_code(), tool);
            if (tool.getAst_used_status() == 0 && locName.equals(tool.getLoc_name())) {
                epcList.add(tool.getAst_epc_code());
            }
        }
    }

    @Override
    public void handleBorrowTools(BaseResponse borrowToolsResponse) {
        if ("200000".equals(borrowToolsResponse.getCode())) {
            playMusic("borrow_success.mp3");
            showCloseDoorDialog();
            ToastUtils.showShort("??????????????????");
        } else if ("200002".equals(borrowToolsResponse.getCode())) {
            playMusic("borrow_failed.mp3");
            ToastUtils.showShort("??????????????????");
        } else {
            playMusic("borrow_failed.mp3");
            ToastUtils.showShort("????????????:" + borrowToolsResponse.getMessage() + borrowToolsResponse.getCode());
        }
    }

    @Override
    public void handleBackTools(BaseResponse backToolsResponse) {
        if ("200000".equals(backToolsResponse.getCode())) {
            playMusic("back_success.mp3");
            showCloseDoorDialog();
            ToastUtils.showShort("??????????????????");
        } else if ("200002".equals(backToolsResponse.getCode())) {
            playMusic("back_failed.mp3");
            ToastUtils.showShort("??????????????????");
        } else {
            playMusic("back_failed.mp3");
            ToastUtils.showShort("????????????:" + backToolsResponse.getMessage() + backToolsResponse.getCode());
        }
    }

    @Override
    public void handlefetchPageAssetsInfos(List<AssetsListItemInfo> assetsInfos) {
        Log.e(TAG, "page???????????????=====" + assetsInfos.size());
        epcToolMap.clear();
        epcList.clear();
        for (AssetsListItemInfo tool : assetsInfos) {
            epcToolMap.put(tool.getAst_epc_code(), tool);
            if (tool.getAst_used_status() == 0 && locName.equals(tool.getLoc_name())) {
                epcList.add(tool.getAst_epc_code());
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
        Log.e(TAG, "OnCloseLock");
        ToastUtils.showShort("OnCloseLock");
        openView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
        //waitView.startAnimation(anim);
    }

    public void testOnGetAllTags() {
        Log.e(TAG, "testOnGetAllTags");
        ToastUtils.showShort("testOnGetAllTags");
        Tags tags = new Tags();
        tags.tag_list = new ArrayList<>();
        //todo ????????????epc
        tags.tag_list.add(new Tags._tag("E22021031674406266200202"));
        tags.tag_list.add(new Tags._tag("E22021031674406266190202"));
        tags.tag_list.add(new Tags._tag("E22021031674406266180202"));
        tags.tag_list.add(new Tags._tag("E22021031674406266170202"));
        tags.tag_list.add(new Tags._tag("E22021031674406266160202"));
        tags.tag_list.add(new Tags._tag("E22021031674406266150202"));
        tags.tag_list.add(new Tags._tag("E22021031674406266140202"));
        tags.tag_list.add(new Tags._tag("E22021031674406266130202"));
        tags.tag_list.add(new Tags._tag("E22021031674406266120202"));
        tags.tag_list.add(new Tags._tag("aaaaaaaa"));
        tags.tag_list.add(new Tags._tag("E20161596781242564560202"));
        tags.tag_list.add(new Tags._tag("E22021031739351451030202"));
        handleAllTags(tags);
    }

    public void handleAllTags(Tags tags) {
        Log.e(TAG, "handleAllTags");
        Log.e(TAG, "tags====" + tags);
        invEpcList.clear();
        wrongList.clear();
        toolList.clear();
        for (Tags._tag tag : tags.tag_list) {
            invEpcList.add(tag.epc);
        }
        //????????????????????????epc start
        invEpcList.retainAll(epcToolMap.keySet());
        //???????????????????????????epc end
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
        //?????????????????????
        tempEpcList.removeAll(invEpcList);
        for (String borrowEpc : tempEpcList) {
            AssetsListItemInfo borrowTool = epcToolMap.get(borrowEpc);
            if (borrowTool != null) {
                borrowTool.setAst_used_status(6);
                toolList.add(borrowTool);
                assetBorrowPara.getAstids().add(borrowTool.getId());
            }
        }
        //????????????????????????
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
                String title = currentUser.getUser_real_name() + "?????????????????????";
                mPresenter.backTools(new NewBorrowBackPara(formData, "[]", title));
            }
            if (assetBorrowPara.getAstids().size() > 0) {
                String formData = assetBorrowPara.toString();
                String title = currentUser.getUser_real_name() + "?????????????????????";
                mPresenter.borrowTools(new NewBorrowBackPara(formData, "[]", title));
            }
            if (assetBackPara.getAst_ids().size() == 0 && assetBorrowPara.getAstids().size() == 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isDestroy) {
                            return;
                        }
                        showCloseDoorDialog();
                    }
                });
            }
        } else {
            toolList.clear();
            toolList.addAll(wrongList);
            //todo ????????????
            if (!isTest) {
                isAutoReopen = true;
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
                //waitView.clearAnimation();
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

    public void showCloseDoorDialog() {
        if (isTest || (closeDoorDialog != null && closeDoorDialog.isShowing())) {
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
            // ????????????
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.width = (int) (ScreenSizeUtils.getInstance(getApplication()).getScreenWidth() * 0.75f);
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(layoutParams);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroy = true;
    }

    private void playMusic(String name) {
        MediaPlayer player = new MediaPlayer();
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.stop();
                mp.release();
            }
        });
        if (assetManager == null) {
            assetManager = ToolBoxApplication.getInstance().getResources().getAssets();
        }
        try {
            AssetFileDescriptor fileDescriptor = assetManager.openFd(name);
            player.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
