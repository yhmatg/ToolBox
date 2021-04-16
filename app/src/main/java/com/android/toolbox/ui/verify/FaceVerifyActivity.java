package com.android.toolbox.ui.verify;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import com.android.toolbox.R;
import com.android.toolbox.app.ToolBoxApplication;
import com.android.toolbox.base.activity.BaseActivity;
import com.android.toolbox.base.presenter.AbstractPresenter;
import com.android.toolbox.contract.FaceVerifyContract;
import com.android.toolbox.core.DataManager;
import com.android.toolbox.core.bean.BaseResponse;
import com.android.toolbox.core.bean.terminal.FaceAuthPara;
import com.android.toolbox.core.bean.terminal.FaceFailResponse;
import com.android.toolbox.core.bean.terminal.FaceSucResponse;
import com.android.toolbox.core.bean.user.FaceLoginPara;
import com.android.toolbox.core.bean.user.UserLoginResponse;
import com.android.toolbox.core.http.api.GeeksApis;
import com.android.toolbox.core.http.client.RetrofitClient;
import com.android.toolbox.core.http.widget.BaseObserver;
import com.android.toolbox.presenter.FaceVerifyPresenter;
import com.android.toolbox.ui.camera.BitmapUtils;
import com.android.toolbox.ui.camera.Bitmaps;
import com.android.toolbox.ui.camera.CameraHelper;
import com.android.toolbox.ui.camera.FaceView;
import com.android.toolbox.ui.manager.ManagerHomeActivity;
import com.android.toolbox.utils.RxUtils;
import com.android.toolbox.utils.ToastUtils;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import butterknife.BindView;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import okhttp3.ResponseBody;

public class FaceVerifyActivity extends BaseActivity<FaceVerifyPresenter> implements FaceVerifyContract.View {
    private static String TAG = "FaceVerifyActivity";
    private static final int REQUEST_CODE_TAKE_PICTURE = 100;
    private File newFile;
    @BindView(R.id.im_take_pic)
    SurfaceView surfaceView;
    @BindView(R.id.faceView)
    FaceView faceView;
    private CameraHelper mCameraHelper;
    private boolean isNeedRecognize = true;
    @Override
    public FaceVerifyPresenter initPresenter() {
        return new FaceVerifyPresenter();
    }

    @Override
    protected void initEventAndData() {
        mCameraHelper = new CameraHelper(this, surfaceView);
        mCameraHelper.addCallBack(new CameraHelper.CallBack() {
            @Override
            public void onPreviewFrame(@Nullable byte[] data) {

            }

            @Override
            public void onTakePic(@Nullable byte[] data) {
                byte[] bytes = Bitmaps.INSTANCE.compressInSampleSize(data, 500, 500);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                //前后摄像头旋转180 后摄像头不用旋转
                Bitmap rotateBitmap = BitmapUtils.INSTANCE.rotate(bitmap, 0);
                byte[] rotateBytes = BitmapUtils.INSTANCE.toByteArray(rotateBitmap);
               /* String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+"/default_image.jpg";
                try {
                    bitmapToFile(path,rotateBitmap,100);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                String faceBase64 = Base64.encodeToString(rotateBytes, Base64.DEFAULT).replaceAll("\r|\n", "");
                String timeStr = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                String signature = "imgBase64=data:image/jpg;base64," + faceBase64 + "&requestTime=" + timeStr;
                String finalSignature = hMacSha("vd938cyyy83edzdc", signature, "HmacSHA256");
                FaceAuthPara faceAuthPara = new FaceAuthPara(timeStr, "data:image/jpg;base64," + faceBase64);
                //getUserByFace("mu2lkq1i", finalSignature, timeStr, faceAuthPara);
                mPresenter.getUserByFace("mu2lkq1i", finalSignature, timeStr, faceAuthPara);
            }

            @Override
            public void onFaceDetect(@NotNull ArrayList<RectF> faces) {
                faceView.setFaces(faces);
                Log.e(TAG, "人脸:" + faces.size());
                if (isNeedRecognize) {
                    mCameraHelper.takePic();
                    isNeedRecognize = false;
                }


            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_face_verify;
    }

    @Override
    protected void initToolbar() {

    }

    @OnClick({R.id.title_back, R.id.bt_retry, R.id.bt_change_card})
    public void performClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.bt_retry:
                mCameraHelper.takePic();
                break;
            case R.id.bt_change_card:
                startActivity(new Intent(this, CardVerifyActivity.class));
                finish();
                break;
        }

    }

    private String hMacSha(String key, String value, String shaType) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes("UTF-8"), shaType);
            Mac mac = Mac.getInstance(shaType);
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(value.getBytes("UTF-8"));

            byte[] hexArray = {
                    (byte) '0', (byte) '1', (byte) '2', (byte) '3',
                    (byte) '4', (byte) '5', (byte) '6', (byte) '7',
                    (byte) '8', (byte) '9', (byte) 'a', (byte) 'b',
                    (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f'
            };
            byte[] hexChars = new byte[rawHmac.length * 2];
            for (int j = 0; j < rawHmac.length; j++) {
                int v = rawHmac[j] & 0xFF;
                hexChars[j * 2] = hexArray[v >>> 4];
                hexChars[j * 2 + 1] = hexArray[v & 0x0F];
            }
            return new String(hexChars);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void handleGetUserByFace(ResponseBody responseBody) {
        try {
            String body = responseBody.string();
            JSONObject json = new JSONObject(body);
            String code = json.getString("code");
            String msg = json.getString("msg");
            if ("1".equals(code)) {
                FaceSucResponse faceSucResponse = new Gson().fromJson(body, FaceSucResponse.class);
                String workNo = "ypzx" + faceSucResponse.getData().getWorkNo();
                mPresenter.faceLogin(new FaceLoginPara(workNo));
                ToastUtils.showLong("用户:" + workNo);
            } else {
                FaceFailResponse faceFailResponse = new Gson().fromJson(body, FaceFailResponse.class);
                isNeedRecognize = true;
                ToastUtils.showShort("人脸登录失败:" + faceFailResponse.getData());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleFaceLogin(UserLoginResponse loginResponse) {
        DataManager.getInstance().setToken(loginResponse.getToken());
        ToolBoxApplication.getInstance().setCurrentUser(loginResponse.getUserinfo());
        startActivity(new Intent(this, BorrowBackToolActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        mCameraHelper.releaseCamera();
        super.onDestroy();
    }

    /**
     * bitmap保存为file
     */
    public static void bitmapToFile(String filePath,
                                    Bitmap bitmap, int quality) throws IOException {
        if (bitmap != null) {
            File file = new File(filePath.substring(0,
                    filePath.lastIndexOf(File.separator)));
            if (!file.exists()) {
                file.mkdirs();
            }
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(filePath));
            bitmap.compress(Bitmap.CompressFormat.PNG, quality, bos);
            bos.flush();
            bos.close();
        }
    }
}
