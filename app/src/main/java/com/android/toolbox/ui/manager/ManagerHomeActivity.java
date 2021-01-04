package com.android.toolbox.ui.manager;

import android.content.Intent;
import android.view.View;

import com.android.toolbox.R;
import com.android.toolbox.base.activity.BaseActivity;
import com.android.toolbox.base.presenter.AbstractPresenter;

import butterknife.OnClick;

public class ManagerHomeActivity extends BaseActivity {

    @Override
    public AbstractPresenter initPresenter() {
        return null;
    }

    @Override
    protected void initEventAndData() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_manager_home;
    }

    @Override
    protected void initToolbar() {

    }

    @OnClick({R.id.title_back, R.id.iv_manage_tool, R.id.iv_manager_user, R.id.iv_return_home, R.id.iv_login_out})
    void performClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.iv_manage_tool:
                startActivity(new Intent(this,ManageToolActivity.class));
                break;
            case R.id.iv_manager_user:
                startActivity(new Intent(this,StaffManageActivity.class));
                break;
            case R.id.iv_return_home:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
                break;
            case R.id.iv_login_out:
                finish();
                break;

        }
    }
}
