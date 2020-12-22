package com.android.toolbox.ui.toolquery;

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
import com.android.toolbox.base.activity.BaseActivity;
import com.android.toolbox.contract.ToolQueryContract;
import com.android.toolbox.core.bean.assist.AssetFilterParameter;
import com.android.toolbox.core.bean.assist.AssetsListItemInfo;
import com.android.toolbox.core.bean.assist.AssetsType;
import com.android.toolbox.presenter.QueryToolPresenter;
import com.android.toolbox.utils.StringUtils;
import com.multilevel.treelist.Node;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ToolQueryActivity extends BaseActivity<QueryToolPresenter> implements ToolQueryContract.View {
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
    private AssetListAdapter adapter;
    private List<AssetsListItemInfo> mData = new ArrayList<>();
    List<Node> mAssetsTypes = new ArrayList<>();
    List<Node> mSelectAssetsTypes = new ArrayList<>();
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

    @Override
    public QueryToolPresenter initPresenter() {
        return new QueryToolPresenter();
    }

    @Override
    protected void initEventAndData() {
        adapter = new AssetListAdapter(this, mData);
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
                mPresenter.fetchPageAssetsList(pageSize, currentPage, assetsId, "", conditions.toString());
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
                mSelectAssetsTypes.clear();
                for (Node node : allNodes) {
                    if (node.isChecked()) {
                        mSelectAssetsTypes.add(node);
                    }
                }

                isNeedClearData = true;
                currentPage = 1;
                conditions.clearData();
                conditions.setmSelectAssetsTypes(mSelectAssetsTypes);
                mPresenter.fetchPageAssetsList(pageSize, currentPage, "", "",conditions.toString());
                filterDialog.dismiss();
            }
        });
        multiRecycle = (RecyclerView) filterView.findViewById(R.id.multi_recycle);
        multiRecycle.setLayoutManager(new LinearLayoutManager(this));
        multiFilterAdapter = new AssetFilterRecyclerAdapter(multiRecycle, this,
                mAssetsTypes, 2, R.drawable.tree_reduce, R.drawable.tree_add);
        multiRecycle.setAdapter(multiFilterAdapter);
        mPresenter.getAllAssetsType();
        mPresenter.fetchPageAssetsList(pageSize, 1, "", "", conditions.toString());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_tool_query;
    }

    @Override
    protected void initToolbar() {

    }

    @Override
    public void handleGetAllAssetsType(List<AssetsType> assetTypes) {
        mAssetsTypes.clear();
        mAssetsTypes.add(new Node("-1", "-2", "全部"));
        for (AssetsType assetsType : assetTypes) {
            String pId = StringUtils.isEmpty(assetsType.getType_superid()) ? "-1" : assetsType.getType_superid();
            mAssetsTypes.add(new Node(assetsType.getId(), pId, assetsType.getType_name()));
        }
        multiFilterAdapter.addData(mAssetsTypes);
        multiFilterAdapter.notifyDataSetChanged();

    }

    @Override
    public void handleFetchPageAssetsList(List<AssetsListItemInfo> assetsInfos) {
        mRefreshLayout.finishLoadMore();
        if (isNeedClearData) {
            mData.clear();
        }
        mData.addAll(assetsInfos);
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
                    mPresenter.fetchPageAssetsList(pageSize, currentPage, assetsId, "", conditions.toString());
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
