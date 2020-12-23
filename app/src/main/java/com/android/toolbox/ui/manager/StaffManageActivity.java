package com.android.toolbox.ui.manager;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.toolbox.R;
import com.android.toolbox.app.ToolBoxApplication;
import com.android.toolbox.base.activity.BaseActivity;
import com.android.toolbox.contract.StaffManageContract;
import com.android.toolbox.core.bean.assist.AssetFilterParameter;
import com.android.toolbox.core.bean.assist.DepartmentBean;
import com.android.toolbox.core.bean.assist.MangerUser;
import com.android.toolbox.core.bean.user.UserInfo;
import com.android.toolbox.presenter.StaffManagePresenter;
import com.android.toolbox.ui.toolquery.AssetFilterRecyclerAdapter;
import com.android.toolbox.utils.StringUtils;
import com.multilevel.treelist.Node;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class StaffManageActivity extends BaseActivity<StaffManagePresenter> implements StaffManageContract.View {
    @BindView(R.id.edit_search)
    EditText search;
    @BindView(R.id.titleLeft)
    ImageView titleLeft;
    @BindView(R.id.tv_screen)
    TextView tvScreen;
    @BindView(R.id.asset_recycler)
    RecyclerView recyclerView;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;
    private UserListAdapter adapter;
    private List<MangerUser> mData = new ArrayList<>();
    List<Node> mAssetsDepts = new ArrayList<>();
    List<Node> mSelectDepts = new ArrayList<>();
    private boolean isNeedClearData;
    private String preFilter = "";
    private int currentPage = 1;
    private int pageSize = 10;
    private AssetFilterParameter conditions = new AssetFilterParameter();
    private Dialog filterDialog;
    private View filterView;
    private RecyclerView multiRecycle;
    private AssetFilterRecyclerAdapter multiFilterAdapter;
    ImageView mBackImg;
    Button mConfirm;
    private UserInfo currentUser;

    @Override
    public StaffManagePresenter initPresenter() {
        return new StaffManagePresenter();
    }

    @Override
    protected void initEventAndData() {
        currentUser = ToolBoxApplication.getInstance().getCurrentUser();
        adapter = new UserListAdapter( mData,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                String assetsId = search.getText().toString();
                isNeedClearData = !preFilter.equals(assetsId);
                if (isNeedClearData) {
                    currentPage = 1;
                } else {
                    currentPage++;
                }
                preFilter = assetsId;
                mPresenter.getAllEmpUsers(pageSize, currentPage, assetsId);
            }
        });
        mRefreshLayout.setEnableRefresh(false);//使上拉加载具有弹性效果
        mRefreshLayout.setEnableOverScrollDrag(false);//禁止越界拖动（1.0.4以上版本）
        mRefreshLayout.setEnableOverScrollBounce(false);//关闭越界回弹功能
        mRefreshLayout.setEnableAutoLoadMore(false);
        //分类筛选
        filterView = LayoutInflater.from(this).inflate(R.layout.filter_item_layout, null);
        mBackImg = filterView.findViewById(R.id.title_back);
        mBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog.dismiss();
            }
        });
        mConfirm = filterView.findViewById(R.id.bt_sure);
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Node> allNodes = multiFilterAdapter.getAllNodes();
                mSelectDepts.clear();
                for (Node node : allNodes) {
                    if (node.isChecked()) {
                        mSelectDepts.add(node);
                    }
                }

                isNeedClearData = true;
                currentPage = 1;
                conditions.clearData();
                conditions.setmSelectAssetsTypes(mSelectDepts);
                mPresenter.getAllEmpUsers(pageSize, currentPage, "");
                filterDialog.dismiss();
            }
        });
        multiRecycle = (RecyclerView) filterView.findViewById(R.id.multi_recycle);
        multiRecycle.setLayoutManager(new LinearLayoutManager(this));
        multiFilterAdapter = new AssetFilterRecyclerAdapter(multiRecycle, this,
                mAssetsDepts, 2, R.drawable.tree_reduce, R.drawable.tree_add);
        multiRecycle.setAdapter(multiFilterAdapter);
        mPresenter.getAllDeparts(currentUser.getCorpInfo().getId());
        mPresenter.getAllEmpUsers(pageSize, 1, "");

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_tool_query;
    }

    @Override
    protected void initToolbar() {

    }

    @Override
    public void handleGetAllDeparts(List<DepartmentBean> departs) {
        mAssetsDepts.clear();
        mAssetsDepts.add(new Node("-1", "-2", "全部"));
        for (DepartmentBean deptBean : departs) {
            String pId = StringUtils.isEmpty(deptBean.getOrg_superid()) ? "-1" : deptBean.getOrg_superid();
            mAssetsDepts.add(new Node(deptBean.getId(), pId, deptBean.getOrg_name()));
        }
        multiFilterAdapter.addData(mAssetsDepts);
        multiFilterAdapter.notifyDataSetChanged();
    }

    @Override
    public void handleGetAllEmpUsers(List<MangerUser> users) {
        mRefreshLayout.finishLoadMore();
        if (isNeedClearData) {
            mData.clear();
        }
        mData.addAll(users);
        adapter.notifyDataSetChanged();
    }

    private void searchAssets() {
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    String assetsId = search.getText().toString();
                    search.setSelection(assetsId.length());
                    isNeedClearData = true;
                    currentPage = 1;
                    preFilter = assetsId;
                    conditions.clearData();
                    mPresenter.getAllEmpUsers(pageSize, currentPage, assetsId);
                    return true;
                }
                return false;
            }
        });
    }

    @OnClick({R.id.titleLeft, R.id.edit_search, R.id.tv_screen})
    public void performClick(View view) {
        switch (view.getId()) {
            case R.id.titleLeft:
                finish();
                break;
            case R.id.edit_search:
                searchAssets();
                break;
            case R.id.tv_screen:
                showFilterDialog();
                break;
        }
    }

    public void showFilterDialog() {
        if (filterDialog != null) {
            filterDialog.show();
        } else {
            filterDialog = new Dialog(this);
            filterDialog.setContentView(filterView);
            filterDialog.show();
            Window window = filterDialog.getWindow();
            if (window != null) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.getDecorView().setPadding(0, 0, 0, 0);
                window.getDecorView().setBackgroundColor(Color.WHITE);
                WindowManager.LayoutParams layoutParams = window.getAttributes();
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
                window.setAttributes(layoutParams);
            }
        }
    }
}
