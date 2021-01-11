package com.android.toolbox.ui.verify;

import android.content.Intent;
import android.view.View;

import com.android.toolbox.R;
import com.android.toolbox.base.activity.BaseActivity;
import com.android.toolbox.base.presenter.AbstractPresenter;

import java.io.File;

import butterknife.OnClick;

public class CardVerifyActivity extends BaseActivity {
    private static final int REQUEST_CODE_TAKE_PICTURE = 100;
    private File newFile;

    @Override
    public AbstractPresenter initPresenter() {
        return null;
    }

    @Override
    protected void initEventAndData() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_card_verify;
    }

    @Override
    protected void initToolbar() {

    }

    @OnClick({R.id.title_back, R.id.bt_retry})
    public void performClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.bt_retry:
                startActivity(new Intent(this, BorrowBackToolActivity.class));
                finish();
                break;

        }

    }
}
