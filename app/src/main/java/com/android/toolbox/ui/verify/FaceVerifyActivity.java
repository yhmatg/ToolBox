package com.android.toolbox.ui.verify;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.Base64;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.ViewTreeObserver;

import com.android.toolbox.R;
import com.android.toolbox.app.ToolBoxApplication;
import com.android.toolbox.base.activity.BaseActivity;
import com.android.toolbox.contract.FaceVerifyContract;
import com.android.toolbox.core.DataManager;
import com.android.toolbox.core.bean.terminal.FaceAuthPara;
import com.android.toolbox.core.bean.terminal.FaceFailResponse;
import com.android.toolbox.core.bean.terminal.FaceSucResponse;
import com.android.toolbox.core.bean.user.FaceLoginPara;
import com.android.toolbox.core.bean.user.UserLoginResponse;
import com.android.toolbox.presenter.FaceVerifyPresenter;
import com.android.toolbox.ui.arcface.CameraHelper;
import com.android.toolbox.ui.arcface.CameraListener;
import com.android.toolbox.ui.arcface.FaceRectView;
import com.android.toolbox.ui.arcface.LivenessType;
import com.android.toolbox.ui.arcface.RequestLivenessStatus;
import com.android.toolbox.utils.ToastUtils;
import com.android.toolbox.utils.logger.BitmapUtils;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.LivenessInfo;
import com.arcsoft.face.enums.DetectFaceOrientPriority;
import com.arcsoft.face.enums.DetectMode;
import com.google.gson.Gson;
import com.xuexiang.xlog.XLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.ResponseBody;

public class FaceVerifyActivity extends BaseActivity<FaceVerifyPresenter> implements FaceVerifyContract.View, CameraListener, ViewTreeObserver.OnGlobalLayoutListener {
    private static String TAG = "FaceVerifyActivity";
    private static String TYPE_TAG = "type";
    private static Integer TYPE_CAPTURE = 0;
    private static Integer TYPE_RECORD = 1;
    private static final int REQUEST_CODE_TAKE_PICTURE = 100;
    @BindView(R.id.im_take_pic)
    TextureView surfaceView;
    @BindView(R.id.faceView)
    FaceRectView faceView;
    private CameraHelper mCameraHelper;
    private boolean isNeedRecognize = true;
    private static final int MAX_DETECT_NUM = 5;
    /**
     * VIDEO??????????????????????????????????????????????????????
     */
    private FaceEngine ftEngine;
    private int ftInitCode = -1;
    /**
     * IMAGE????????????????????????????????????????????????????????????
     */
    private FaceEngine flEngine;
    private int flInitCode = -1;
    /**
     * ?????????????????????
     */
    private ConcurrentHashMap<Integer, Integer> livenessMap = new ConcurrentHashMap<>();
    private Camera.Size previewSize;
    private FaceInfo faceInfo;

    @Override
    public FaceVerifyPresenter initPresenter() {
        return new FaceVerifyPresenter();
    }

    @Override
    protected void initEventAndData() {
        initEngine();
        int flQueueSize = 1;
        flThreadQueue = new LinkedBlockingQueue<Runnable>(flQueueSize);
        flExecutor = new ThreadPoolExecutor(1, flQueueSize, 0, TimeUnit.MILLISECONDS, flThreadQueue);
        surfaceView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        mCameraHelper = new CameraHelper(surfaceView, this);
        mCameraHelper.init();
    }

    private void initEngine() {
        ftEngine = new FaceEngine();
        int faceMask = FaceEngine.ASF_FACE_DETECT;
        ftInitCode = ftEngine.init(this, DetectMode.ASF_DETECT_MODE_VIDEO, DetectFaceOrientPriority.ASF_OP_ALL_OUT,
                16, MAX_DETECT_NUM, faceMask);
        int livenessMask = FaceEngine.ASF_LIVENESS;
        flEngine = new FaceEngine();
        flInitCode = flEngine.init(this, DetectMode.ASF_DETECT_MODE_IMAGE, DetectFaceOrientPriority.ASF_OP_0_ONLY,
                16, MAX_DETECT_NUM, livenessMask);
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
                //mCameraHelper.takePic();
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
                XLog.get().e("login start time===" + System.currentTimeMillis());
                mPresenter.faceLogin(new FaceLoginPara(workNo));
                ToastUtils.showLong("??????:" + workNo);
                isNeedRecognize = false;
            } else {
                FaceFailResponse faceFailResponse = new Gson().fromJson(body, FaceFailResponse.class);
                ToastUtils.showShort("??????????????????:" + faceFailResponse.getData());
                isNeedRecognize = true;
                livenessMap.put(faceInfo.getFaceId(), -1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleFaceLogin(UserLoginResponse loginResponse) {
        XLog.get().e("login end time===" + System.currentTimeMillis());
        DataManager.getInstance().setToken(loginResponse.getToken());
        ToolBoxApplication.getInstance().setCurrentUser(loginResponse.getUserinfo());
        startActivity(new Intent(this, BorrowBackToolActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        if (!flExecutor.isShutdown()) {
            flExecutor.shutdownNow();
            flThreadQueue.clear();
        }
        mCameraHelper.release();
        ftEngine.unInit();
        flEngine.unInit();
        super.onDestroy();
    }

    /**
     * bitmap?????????file
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

    private void drawPreviewInfo(List<FaceInfo> faceInfoList) {
        faceView.clearFaceInfo();
        ArrayList<FaceInfo> drawFaceInfo = new ArrayList<>();
        if (faceInfoList == null || faceInfoList.size() == 0) {
            return;
        }
        for (int i = 0; i < faceInfoList.size(); i++) {
            FaceInfo faceInfo = new FaceInfo();
            faceInfo.setRect(adjustRect(faceInfoList.get(0).getRect()));
            drawFaceInfo.add(faceInfo);
        }
        faceView.addFaceInfo(drawFaceInfo);
    }

    /**
     * ????????????????????????
     */
    private LinkedBlockingQueue<Runnable> flThreadQueue = null;

    /**
     * ?????????????????????
     */
    private ExecutorService flExecutor;

    @Override
    public void onCameraOpened(Camera camera, int cameraId, int displayOrientation, boolean isMirror) {

    }

    @Override
    public void onPreview(byte[] data, Camera camera) {
            previewSize = mCameraHelper.getPreviewSize();
            List<FaceInfo> faceInfoList = new ArrayList<>();
            int code = ftEngine.detectFaces(data, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, faceInfoList);
            drawPreviewInfo(faceInfoList);
            if (code == ErrorInfo.MOK && faceInfoList.size() > 0 && isNeedRecognize) {
                faceInfo = faceInfoList.get(0);
                Integer liveness = livenessMap.get(faceInfo.getFaceId());
                if (liveness == null
                        || (liveness != LivenessInfo.ALIVE && liveness != RequestLivenessStatus.ANALYZING)) {
                    livenessMap.put(faceInfo.getFaceId(), RequestLivenessStatus.ANALYZING);
                    if (ftEngine != null && flThreadQueue.remainingCapacity() > 0) {
                        flExecutor.execute(new FaceLivenessDetectRunnable(data, faceInfo, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, faceInfo.getFaceId(), LivenessType.RGB));
                    }
                }
            }
    }

    @Override
    public void onCameraClosed() {

    }

    @Override
    public void onCameraError(Exception e) {

    }

    @Override
    public void onCameraConfigurationChanged(int cameraID, int displayOrientation) {

    }

    @Override
    public void onGlobalLayout() {
        surfaceView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        mCameraHelper.start();
    }

    /**
     * ?????????????????????
     */
    public class FaceLivenessDetectRunnable implements Runnable {
        private FaceInfo faceInfo;
        private int width;
        private int height;
        private int format;
        private Integer trackId;
        private byte[] nv21Data;
        private LivenessType livenessType;

        private FaceLivenessDetectRunnable(byte[] nv21Data, FaceInfo faceInfo, int width, int height, int format, Integer trackId, LivenessType livenessType) {
            if (nv21Data == null) {
                return;
            }
            this.nv21Data = nv21Data;
            this.faceInfo = new FaceInfo(faceInfo);
            this.width = width;
            this.height = height;
            this.format = format;
            this.trackId = trackId;
            this.livenessType = livenessType;
        }

        @Override
        public void run() {
            if (nv21Data != null) {
                if (flEngine != null) {
                    List<LivenessInfo> livenessInfoList = new ArrayList<>();
                    int flCode;
                    Log.e(TAG, "width==" + width + "   height==" + height);
                    synchronized (flEngine) {
                        if (livenessType == LivenessType.RGB) {
                            flCode = flEngine.process(nv21Data, width, height, format, Arrays.asList(faceInfo), FaceEngine.ASF_LIVENESS);
                        } else {
                            flCode = flEngine.processIr(nv21Data, width, height, format, Arrays.asList(faceInfo), FaceEngine.ASF_IR_LIVENESS);
                        }
                    }
                    Log.e(TAG, "process ??????code==" + flCode);
                    if (flCode == ErrorInfo.MOK) {
                        if (livenessType == LivenessType.RGB) {
                            flCode = flEngine.getLiveness(livenessInfoList);
                        } else {
                            flCode = flEngine.getIrLiveness(livenessInfoList);
                        }
                    } else {
                        livenessMap.put(faceInfo.getFaceId(), -1);
                    }
                    if (flCode == ErrorInfo.MOK && livenessInfoList.size() > 0) {
                        if (nv21Data != null) {
                            LivenessInfo livenessInfo = livenessInfoList.get(0);
                            //?????????????????????
                            livenessMap.put(faceInfo.getFaceId(), livenessInfo.getLiveness());
                            onFaceLivenessInfoGet(livenessInfo, nv21Data);
                            Log.e(TAG, "????????????????????????" + livenessInfo.getLiveness());
                        }
                    } else {
                        Log.e(TAG, "???????????????????????? code==" + flCode);
                    }
                } else {
                    Log.e(TAG, "??????????????????");
                }
            }
            nv21Data = null;
        }

    }

    private void onFaceLivenessInfoGet(LivenessInfo livenessInfo, byte[] nv21Data) {
        if (livenessInfo != null) {
            int liveness = livenessInfo.getLiveness();
            if (liveness == LivenessInfo.ALIVE) {
                XLog.get().e("get face data start time===" + System.currentTimeMillis());
                YuvImage yuvimage = new YuvImage(nv21Data, ImageFormat.NV21, previewSize.width,
                        previewSize.height, null);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                yuvimage.compressToJpeg(new Rect(0, 0, previewSize.width,
                        previewSize.height), 100, baos);
                byte[] jpegData = baos.toByteArray();
                byte[] compressData = BitmapUtils.compressInSampleSize(jpegData, 600, 600);
                //?????????????????? start
                /*Bitmap bitmap = BitmapFactory.decodeByteArray(compressData, 0, compressData.length);
               //???????????????90?????????????????????
                Bitmap rotateBitmap = BitmapUtils.rotate(bitmap, 0f);
                byte[] rotateBytes = BitmapUtils.toByteArray(rotateBitmap);
                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/default_image.jpg";
                try {
                    bitmapToFile(path, rotateBitmap, 100);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                //?????????????????? end
                String faceBase64 = Base64.encodeToString(compressData, Base64.DEFAULT).replaceAll("\r|\n", "");
                String timeStr = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                String signature = "imgBase64=data:image/jpg;base64," + faceBase64 + "&requestTime=" + timeStr;
                String finalSignature = hMacSha("vd938cyyy83edzdc", signature, "HmacSHA256");
                FaceAuthPara faceAuthPara = new FaceAuthPara(timeStr, "data:image/jpg;base64," + faceBase64);
                XLog.get().e("face recognize start time===" + System.currentTimeMillis());
                mPresenter.getUserByFace("mu2lkq1i", finalSignature, timeStr, faceAuthPara);
            }
        }
    }

    public Rect adjustRect(Rect ftRect) {
        int previewWidth = mCameraHelper.getPreviewSize().width;
        int previewHeight = mCameraHelper.getPreviewSize().height;
        int canvasWidth = surfaceView.getWidth();
        int canvasHeight = surfaceView.getHeight();
        //??????????????????????????????270???????????????0???
        int cameraDisplayOrientation = 0;
        int cameraId = 1;
        boolean isMirror = false;
        boolean mirrorHorizontal = false;
        boolean mirrorVertical = false;
        if (ftRect == null) {
            return null;
        }
        Rect rect = new Rect(ftRect);
        float horizontalRatio;
        float verticalRatio;
        if (cameraDisplayOrientation % 180 == 0) {
            horizontalRatio = (float) canvasWidth / (float) previewWidth;
            verticalRatio = (float) canvasHeight / (float) previewHeight;
        } else {
            horizontalRatio = (float) canvasHeight / (float) previewWidth;
            verticalRatio = (float) canvasWidth / (float) previewHeight;
        }
        rect.left *= horizontalRatio;
        rect.right *= horizontalRatio;
        rect.top *= verticalRatio;
        rect.bottom *= verticalRatio;

        Rect newRect = new Rect();
        switch (cameraDisplayOrientation) {
            case 0:
                if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    newRect.left = canvasWidth - rect.right;
                    newRect.right = canvasWidth - rect.left;
                } else {
                    newRect.left = rect.left;
                    newRect.right = rect.right;
                }
                newRect.top = rect.top;
                newRect.bottom = rect.bottom;
                break;
            case 90:
                newRect.right = canvasWidth - rect.top;
                newRect.left = canvasWidth - rect.bottom;
                if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    newRect.top = canvasHeight - rect.right;
                    newRect.bottom = canvasHeight - rect.left;
                } else {
                    newRect.top = rect.left;
                    newRect.bottom = rect.right;
                }
                break;
            case 180:
                newRect.top = canvasHeight - rect.bottom;
                newRect.bottom = canvasHeight - rect.top;
                if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    newRect.left = rect.left;
                    newRect.right = rect.right;
                } else {
                    newRect.left = canvasWidth - rect.right;
                    newRect.right = canvasWidth - rect.left;
                }
                break;
            case 270:
                newRect.left = rect.top;
                newRect.right = rect.bottom;
                if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    newRect.top = rect.left;
                    newRect.bottom = rect.right;
                } else {
                    newRect.top = canvasHeight - rect.right;
                    newRect.bottom = canvasHeight - rect.left;
                }
                break;
            default:
                break;
        }
        /**
         * isMirror mirrorHorizontal finalIsMirrorHorizontal
         * true         true                false
         * false        false               false
         * true         false               true
         * false        true                true
         *
         * XOR
         */
        if (isMirror ^ mirrorHorizontal) {
            int left = newRect.left;
            int right = newRect.right;
            newRect.left = canvasWidth - right;
            newRect.right = canvasWidth - left;
        }
        if (mirrorVertical) {
            int top = newRect.top;
            int bottom = newRect.bottom;
            newRect.top = canvasHeight - bottom;
            newRect.bottom = canvasHeight - top;
        }
        return newRect;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isNeedRecognize = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isNeedRecognize = false;
    }
}
