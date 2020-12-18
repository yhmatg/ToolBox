package com.android.toolbox;

import android.content.Intent;
import android.view.View;

import com.android.toolbox.base.activity.BaseActivity;
import com.android.toolbox.base.presenter.AbstractPresenter;

import butterknife.OnClick;

public class MainActivity extends BaseActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
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

    }

    @OnClick({R.id.iv_inout_tool, R.id.iv_query_tool, R.id.iv_manager})
    public void performClick(View view) {
        switch (view.getId()) {
            case R.id.iv_inout_tool:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_MAIN);
               // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //如果是服务里调用，必须加入new task标识
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
                break;
            case R.id.iv_query_tool:
                break;
            case R.id.iv_manager:
                break;
        }
    }
}
