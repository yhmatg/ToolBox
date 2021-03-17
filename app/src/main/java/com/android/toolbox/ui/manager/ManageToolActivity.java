package com.android.toolbox.ui.manager;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.android.toolbox.utils.ToastUtils;
import com.multilevel.treelist.Node;
import com.xuexiang.xlog.XLog;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;

public class ManageToolActivity extends BaseActivity<ManageToolPresenter> implements ManageToolContract.View {
    private static String TAG = "ManageToolActivity";
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
    @BindString(R.string.loc_id)
    String locId;
    @BindString(R.string.loc_name)
    String locName;
    @BindView(R.id.bt_open_door)
    Button reOpenBt;
    private ServerThread serverThread;
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
        currentUser = ToolBoxApplication.getInstance().getCurrentUser();
        adapter = new AssetListAdapter(toolList, this, true);
        inOutRecycleView.setLayoutManager(new LinearLayoutManager(this));
        inOutRecycleView.setAdapter(adapter);
        //mPresenter.fetchAllAssetsInfos();
        mPresenter.fetchPageAssetsInfos(pageSize, currentPage, "", "", conditions);
        serverThread = ToolBoxApplication.getInstance().getServerThread();
        initAnimation();
        //todo 开门动作
        if (!isTest) {
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
        return R.layout.activity_manage_tool;
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isDestroy) {
                                    return;
                                }
                                reOpenBt.setEnabled(false);
                                loadingView.setVisibility(View.GONE);
                                resultView.setVisibility(View.GONE);
                                openView.setVisibility(View.VISIBLE);
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
                                if (isDestroy) {
                                    return;
                                }
                                reOpenBt.setEnabled(true);
                                openView.setVisibility(View.GONE);
                                loadingView.setVisibility(View.VISIBLE);
                                waitView.startAnimation(anim);
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

                    }

                    @Override
                    public void OnGetAllTags(Tags tags) {
                        XLog.get().e("标签数目=======" + tags.tag_list.size() + "\n具体标签==" + tags.tag_list);
                        handleAllTags(tags);
                    }
                });
            }
        }).start();
    }

    @Override
    public void handleFetchAllAssetsInfos(List<AssetsListItemInfo> assetsListItemInfos) {
        Log.e(TAG, "all资产数量是=====" + assetsListItemInfos.size());
        epcToolMap.clear();
        epcList.clear();
        for (AssetsListItemInfo tool : assetsListItemInfos) {
            if (locName.equals(tool.getLoc_name())) {
                epcToolMap.put(tool.getAst_epc_code(), tool);
                if (tool.getAst_used_status() == 0) {
                    epcList.add(tool.getAst_epc_code());
                }
            }
        }
    }

    @Override
    public void handleBorrowTools(BaseResponse borrowToolsResponse) {
        if ("200000".equals(borrowToolsResponse.getCode())) {
            ToastUtils.showShort("借用工具成功");
        } else if ("200002".equals(borrowToolsResponse.getCode())) {
            ToastUtils.showShort("请求参数异常");
        }
    }

    @Override
    public void handleBackTools(BaseResponse backToolsResponse) {
        if ("200000".equals(backToolsResponse.getCode())) {
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
            if (locName.equals(tool.getLoc_name())) {
                epcToolMap.put(tool.getAst_epc_code(), tool);
                if (tool.getAst_used_status() == 0) {
                    epcList.add(tool.getAst_epc_code());
                }
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
                if (!isTest) {
                    //mPresenter.fetchAllAssetsInfos();;
                    mPresenter.fetchPageAssetsInfos(pageSize, currentPage, "", "", conditions);
                    invEpcList.clear();
                    wrongList.clear();
                    toolList.clear();
                    epcList.clear();
                    adapter.notifyDataSetChanged();
                    unlock();
                } else {
                    testOpenClock();
                }
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
        }
    }

    public void testOpenClock() {
        openView.setVisibility(View.VISIBLE);
        ToastUtils.showShort("OnOpenLock");
        mPresenter.fetchPageAssetsInfos(pageSize, currentPage, "", "", conditions);
        invEpcList.clear();
        wrongList.clear();
        toolList.clear();
        adapter.notifyDataSetChanged();
    }

    public void testCloseClock() {
        Log.e(TAG, "OnCloseLock");
        ToastUtils.showShort("OnCloseLock");
        openView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
        waitView.startAnimation(anim);
    }

    public void testOnGetAllTags() {
        Log.e(TAG, "testOnGetAllTags");
        ToastUtils.showShort("testOnGetAllTags");
        Tags tags = new Tags();
        tags.tag_list = new ArrayList<>();
        //todo 添加测试epc
        tags.tag_list.add(new Tags._tag("E22020123118399545740202"));
        tags.tag_list.add(new Tags._tag("E22020123118399545760202"));
        tags.tag_list.add(new Tags._tag("E22020123118399545780202"));
        //tags.tag_list.add(new Tags._tag("E22020121626133698580202"));
        //tags.tag_list.add(new Tags._tag("E22020121602221607040202"));
        handleAllTags(tags);
    }

    public void handleAllTags(Tags tags) {
        Logger.info("OnGetAllTags");
        Logger.info(tags);
        invEpcList.clear();
        wrongList.clear();
        toolList.clear();
        for (Tags._tag tag : tags.tag_list) {
            invEpcList.add(tag.epc);
        }
        //去除不属于工具柜得epc start
        //invEpcList.retainAll(epcToolMap.keySet());
        //去除不属于工具柜得epc end
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
        assetBackPara.setBelong_dept_id(currentUser.getDeptInfo().getId());
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
        } else {
            toolList.clear();
            toolList.addAll(wrongList);
            //todo 不需要开门动作
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
                    tvResult.setText(ManageToolActivity.this.getString(R.string.maintenance_result, invEpcList.size(), tempEpcList.size()));
                } else {
                    tvResult.setText(ManageToolActivity.this.getString(R.string.wrong_back_result, wrongList.size()));
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroy = true;
    }
}
