package com.android.toolbox.ui.verify;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
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
import com.android.toolbox.ui.manager.ManagerHomeActivity;
import com.android.toolbox.utils.RxUtils;
import com.android.toolbox.utils.ToastUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import butterknife.BindView;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import okhttp3.ResponseBody;

public class FaceVerifyActivity extends BaseActivity<FaceVerifyPresenter> implements FaceVerifyContract.View {
    private String TAG = "FaceVerifyActivity";
    private static final int REQUEST_CODE_TAKE_PICTURE = 100;
    private File newFile;
    @BindView(R.id.im_take_pic)
    ImageView imTakePic;

    @Override
    public FaceVerifyPresenter initPresenter() {
        return new FaceVerifyPresenter();
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

    @OnClick({R.id.title_back, R.id.bt_retry, R.id.bt_change_card, R.id.im_take_pic})
    public void performClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.bt_retry:
                recognizeFace();
                break;
            case R.id.bt_change_card:
                startActivity(new Intent(this, CardVerifyActivity.class));
                finish();
                break;
            case R.id.im_take_pic:
                takePicture();
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_TAKE_PICTURE:
                // 此处写“如何获取图片”...
                if (newFile != null && newFile.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(newFile.getAbsolutePath());
                    imTakePic.setImageBitmap(bitmap);
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

    @SuppressLint("StaticFieldLeak")
    private void recognizeFace() {
        if (newFile != null && newFile.exists()) {
            new AsyncTask<Void, Void, String>() {

                @Override
                protected String doInBackground(Void... voids) {
                    String absolutePath = newFile.getAbsolutePath();
                    try {
                        File file = new Compressor(ToolBoxApplication.getInstance()).compressToFile(newFile);
                        absolutePath = file.getAbsolutePath();
                        Log.e("face", absolutePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String faceBase64 = imageToBase64(absolutePath).replaceAll("\r|\n", "");
                    return faceBase64;

                }

                @Override
                protected void onPostExecute(String faceBase64) {
                    super.onPostExecute(faceBase64);
                    String timeStr = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                    String signature = "imgBase64=data:image/jpg;base64," + faceBase64 + "&requestTime=" + timeStr;
                    String finalSignature = hMacSha("vd938cyyy83edzdc", signature, "HmacSHA256");
                    FaceAuthPara faceAuthPara = new FaceAuthPara(timeStr, "data:image/jpg;base64," + faceBase64);
                    //getUserByFace("mu2lkq1i", finalSignature, timeStr, faceAuthPara);
                    mPresenter.getUserByFace("mu2lkq1i", finalSignature, timeStr, faceAuthPara);
                }
            }.execute();
        } else {
            ToastUtils.showShort("请先拍照");
        }
    }

    private void getUserByFace(String appID, String signature, String requestId, FaceAuthPara faceAuthPara) {
        RetrofitClient.getInstance().create(GeeksApis.class).getUserByFace(appID, signature, requestId, faceAuthPara)
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(new BaseObserver<ResponseBody>(this, false) {
                    @Override
                    public void onNext(ResponseBody faceResponse) {
                        try {
                            String body = faceResponse.string();
                            JSONObject json = new JSONObject(body);
                            String code = json.getString("code");
                            String msg = json.getString("msg");
                            if ("1".equals(code)) {
                                FaceSucResponse faceSucResponse = new Gson().fromJson(body, FaceSucResponse.class);
                                String workNo = faceSucResponse.getData().getWorkNo();
                                ToastUtils.showShort("用户:" + workNo);
                            } else {
                                FaceFailResponse faceFailResponse = new Gson().fromJson(body, FaceFailResponse.class);
                                ToastUtils.showShort("人脸登录失败:" + faceFailResponse.getData());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        Log.e(TAG, e.toString());
                    }
                });
    }


    public String imageToBase64(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        InputStream is = null;
        byte[] data;
        String result = null;
        try {
            is = new FileInputStream(path);
            //创建一个字符流大小的数组。
            data = new byte[is.available()];
            //写入数组
            is.read(data);
            //用默认的编码格式进行编码
            result = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
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
                String workNo = faceSucResponse.getData().getWorkNo();
                mPresenter.faceLogin(new FaceLoginPara(workNo));
                ToastUtils.showShort("用户:" + workNo);
            } else {
                FaceFailResponse faceFailResponse = new Gson().fromJson(body, FaceFailResponse.class);
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
}
