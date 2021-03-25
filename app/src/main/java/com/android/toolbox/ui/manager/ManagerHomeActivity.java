package com.android.toolbox.ui.manager;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.toolbox.HomeActivity;
import com.android.toolbox.R;
import com.android.toolbox.base.activity.BaseActivity;
import com.android.toolbox.base.presenter.AbstractPresenter;
import com.android.toolbox.ui.toolquery.ToolQueryActivity;
import com.android.toolbox.ui.verify.BorrowBackToolActivity;
import com.android.toolbox.utils.ScreenSizeUtils;

import butterknife.OnClick;

public class ManagerHomeActivity extends BaseActivity {

    private MaterialDialog loginOutDialog;
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

    @OnClick({R.id.title_back, R.id.ll_manage_tool, R.id.ll_manager_user, R.id.ll_return_home, R.id.ll_login_out})
    void performClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.ll_manage_tool:
                startActivity(new Intent(this,ManageToolActivity.class));
                break;
            case R.id.ll_manager_user:
                startActivity(new Intent(this, ToolQueryActivity.class));
                break;
            case R.id.ll_return_home:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
                break;
            case R.id.ll_login_out:
                showLoginOutDialog();
                break;
        }
    }

    public void showLoginOutDialog() {
        if (loginOutDialog != null && !loginOutDialog.isShowing()) {
            loginOutDialog.show();
        } else {
            View contentView = LayoutInflater.from(this).inflate(R.layout.login_out_dialog, null);
            TextView cancel = contentView.findViewById(R.id.tv_no);
            TextView sure = contentView.findViewById(R.id.tv_yes);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginOutDialog.dismiss();
                }
            });
            sure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginOutDialog.dismiss();
                    finish();
                }
            });
            MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                    .customView(contentView, false);
            loginOutDialog = builder
                    .canceledOnTouchOutside(false)
                    .cancelable(false)
                    .show();
            Window window = loginOutDialog.getWindow();
            window.setBackgroundDrawableResource(android.R.color.transparent);
            // 设置宽度
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.width = (int) (ScreenSizeUtils.getInstance(getApplication()).getScreenWidth() * 0.75f);
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(layoutParams);
        }
    }
}
