package com.android.toolbox.ui.verify;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import com.android.toolbox.R;
import com.android.toolbox.base.activity.BaseActivity;
import com.android.toolbox.base.presenter.AbstractPresenter;

import butterknife.BindView;
import butterknife.OnClick;

public class FaceVerifyActivity extends BaseActivity {
    private static final int REQUEST_CODE_TAKE_PICTURE = 100;
    @BindView(R.id.im_face)
    ImageView facePic;

    @Override
    public AbstractPresenter initPresenter() {
        return null;
    }

    @Override
    protected void initEventAndData() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_face_verify;
    }

    @Override
    protected void initToolbar() {

    }

    @OnClick({R.id.bt_camera})
    public void performClick(View view) {
        switch (view.getId()) {
            case R.id.bt_camera:
                 Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // 系统常量， 启动相机的关键
                startActivityForResult(openCameraIntent, REQUEST_CODE_TAKE_PICTURE); // 参数常量为自定义的request code, 在取返回结果时有用
               /* // 打开相机Intent
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // 给拍摄的照片指定存储位置
                File imagePath = new File(getFilesDir(), "images"); // 指定文件
                File newFile = new File(imagePath, "default_image.jpg");
                Uri fileUri = FileProvider.getUriForFile(this, "com.android.toolbox.provider", newFile); // 路径转换
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); //指定图片存放位置，指定后，在onActivityResult里得到的Data将为null
                startActivityForResult(cameraIntent, REQUEST_CODE_TAKE_PICTURE);*/
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_TAKE_PICTURE:
                // 此处写“如何获取图片”...
                Bitmap bm = (Bitmap) data.getExtras().get("data");
                facePic.setImageBitmap(bm);
                break;

        }
    }

}
