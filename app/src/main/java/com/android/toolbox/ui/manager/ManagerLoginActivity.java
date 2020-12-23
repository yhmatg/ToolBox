package com.android.toolbox.ui.manager;

import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.toolbox.R;
import com.android.toolbox.base.activity.BaseActivity;
import com.android.toolbox.contract.ManagerLoginContract;
import com.android.toolbox.core.DataManager;
import com.android.toolbox.core.bean.user.UserInfo;
import com.android.toolbox.core.bean.user.UserLoginResponse;
import com.android.toolbox.presenter.ManagerLoginPresenter;
import com.android.toolbox.utils.Md5Util;
import com.android.toolbox.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class ManagerLoginActivity extends BaseActivity<ManagerLoginPresenter> implements ManagerLoginContract.View {
    private String TAG = "ManagerLoginActivity";
    @BindView(R.id.edit_account)
    EditText mAccountEdit;
    @BindView(R.id.edit_password)
    EditText mPasswordEdit;
    @BindView(R.id.btn_login)
    Button mLoginBtn;
    @BindView(R.id.password_invisible)
    ImageView ivEye;
    @BindView(R.id.title_content)
    TextView mTitle;
    private boolean isOpenEye = false;


    @Override
    public ManagerLoginPresenter initPresenter() {
        return new ManagerLoginPresenter();
    }

    @Override
    protected void initEventAndData() {
        mTitle.setText("管理员登录");
        ivEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOpenEye) {
                    ivEye.setSelected(false);
                    isOpenEye = false;
                    ivEye.setImageResource(R.drawable.psd_invisible);
                    mPasswordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    ivEye.setSelected(true);
                    isOpenEye = true;
                    ivEye.setImageResource(R.drawable.psd_visible);
                    mPasswordEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_manager_login;
    }

    @Override
    protected void initToolbar() {

    }

    @Override
    public void handleLogin(UserLoginResponse userLoginResponse) {
        DataManager.getInstance().setToken(userLoginResponse.getToken());
        Log.e(TAG,userLoginResponse.toString());
    }

    @OnClick({R.id.title_back,R.id.btn_login})
    void performClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.btn_login:
                login();
                break;
        }
    }

    private void login() {
        if (TextUtils.isEmpty(mAccountEdit.getText().toString())) {
            ToastUtils.showShort("请输入账号！");
            return;
        }
        if (TextUtils.isEmpty(mPasswordEdit.getText().toString())) {
            ToastUtils.showShort("请输入密码！");
            return;
        }
        final UserInfo userInfo = new UserInfo();
        userInfo.setUser_name(mAccountEdit.getText().toString());
        userInfo.setUser_password(Md5Util.getMD5(mPasswordEdit.getText().toString()));
        mPresenter.login(userInfo);
    }
}
