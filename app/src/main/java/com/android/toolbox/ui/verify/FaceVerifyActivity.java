package com.android.toolbox.ui.verify;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.View;

import com.android.toolbox.R;
import com.android.toolbox.base.activity.BaseActivity;
import com.android.toolbox.base.presenter.AbstractPresenter;
import com.android.toolbox.core.bean.FaceResponse;
import com.android.toolbox.core.http.api.GeeksApis;
import com.android.toolbox.core.http.client.FaceRetrofitClient;
import com.android.toolbox.core.http.widget.BaseObserver;
import com.android.toolbox.utils.RxUtils;

import java.io.File;

import butterknife.OnClick;
import retrofit2.http.Header;

public class FaceVerifyActivity extends BaseActivity {
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
        return R.layout.activity_face_verify;
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

                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_TAKE_PICTURE:
                // 此处写“如何获取图片”...
                if (data != null) {
                    Bitmap bm = (Bitmap) data.getExtras().get("data");
                    //facePic.setImageBitmap(bm);
                }
                if (newFile != null && newFile.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(newFile.getAbsolutePath());
                    //facePic.setImageBitmap(bitmap);
                    //删除文件，防止上传出错
                    newFile.delete();
                }
                break;
        }
    }

    public void takePicture() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 给拍摄的照片指定存储位置
        newFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "default_image.jpg");
        Uri fileUri = FileProvider.getUriForFile(this, "com.android.toolbox.provider", newFile); // 路径转换
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); //指定图片存放位置，指定后，在onActivityResult里得到的Data将为null
        startActivityForResult(cameraIntent, REQUEST_CODE_TAKE_PICTURE);
    }

    private void getUserByFace(String appID, String signature, String requestId, String requestTime, String imgBase) {
        FaceRetrofitClient.getInstance().create(GeeksApis.class).getUserByFace(appID, signature, requestId, requestTime, imgBase)
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(new BaseObserver<FaceResponse>(this, false) {

                    @Override
                    public void onNext(FaceResponse faceResponse) {

                    }
                });
    }

}
