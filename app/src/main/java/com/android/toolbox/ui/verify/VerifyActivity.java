package com.android.toolbox.ui.verify;

import android.content.Intent;
import android.view.View;

import com.android.toolbox.R;
import com.android.toolbox.base.activity.BaseActivity;
import com.android.toolbox.base.presenter.AbstractPresenter;

import butterknife.OnClick;

public class VerifyActivity extends BaseActivity {
    @Override
    public AbstractPresenter initPresenter() {
        return null;
    }

    @Override
    protected void initEventAndData() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_verify;
    }

    @Override
    protected void initToolbar() {

    }

    @OnClick({R.id.title_back,R.id.iv_face_verify,R.id.iv_code_verify})
    public void performClick(View view){
        switch (view.getId()){
            case R.id.title_back:
                finish();
                break;
            case R.id.iv_face_verify:
                startActivity(new Intent(this,FaceVerifyActivity.class));
                break;
            case R.id.iv_code_verify:

                break;
        }

    }
}
