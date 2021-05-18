package com.android.toolbox.core;

import com.android.toolbox.core.bean.BaseResponse;
import com.android.toolbox.core.bean.terminal.FaceAuthPara;
import com.android.toolbox.core.bean.assist.AssetsListItemInfo;
import com.android.toolbox.core.bean.assist.AssetsListPage;
import com.android.toolbox.core.bean.assist.AssetsType;
import com.android.toolbox.core.bean.assist.DepartmentBean;
import com.android.toolbox.core.bean.assist.ManagerListPage;
import com.android.toolbox.core.bean.terminal.NewBorrowBackPara;
import com.android.toolbox.core.bean.terminal.TerminalInfo;
import com.android.toolbox.core.bean.terminal.TerminalLoginPara;
import com.android.toolbox.core.bean.user.FaceLoginPara;
import com.android.toolbox.core.bean.user.UserInfo;
import com.android.toolbox.core.bean.user.UserLoginResponse;
import com.android.toolbox.core.http.HttpHelper;
import com.android.toolbox.core.http.HttpHelperImpl;
import com.android.toolbox.core.prefs.PreferenceHelper;
import com.android.toolbox.core.prefs.PreferenceHelperImpl;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

/**
 * @author yhm
 * @date 2017/11/27
 */

public class DataManager implements HttpHelper, PreferenceHelper {

    private HttpHelper mHttpHelper;
    private PreferenceHelper mPreferenceHelper;
    private volatile static DataManager INSTANCE = null;

    private DataManager(HttpHelper httpHelper, PreferenceHelper preferencesHelper) {
        mHttpHelper = httpHelper;
        mPreferenceHelper = preferencesHelper;
    }

    public static DataManager getInstance() {
        if (INSTANCE == null) {
            synchronized (DataManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DataManager(HttpHelperImpl.getInstance(), PreferenceHelperImpl.getInstance());
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void saveHostUrl(String hostUrl) {
        mPreferenceHelper.saveHostUrl(hostUrl);
    }

    @Override
    public String getHostUrl() {
        return mPreferenceHelper.getHostUrl();
    }

    @Override
    public void setToken(String token) {
        mPreferenceHelper.setToken(token);
    }

    @Override
    public String getToken() {
        return mPreferenceHelper.getToken();
    }

    @Override
    public void saveFaceActiveStatus(boolean status) {
        mPreferenceHelper.saveFaceActiveStatus(status);
    }

    @Override
    public boolean getFaceActiveStatus() {
        return mPreferenceHelper.getFaceActiveStatus();
    }

    @Override
    public Observable<BaseResponse<UserLoginResponse>> login(UserInfo userInfo) {
        return mHttpHelper.login(userInfo);
    }

    @Override
    public Observable<BaseResponse<TerminalInfo>> terminalLogin(TerminalLoginPara terminalLoginPara) {
        return mHttpHelper.terminalLogin(terminalLoginPara);
    }

    @Override
    public Observable<BaseResponse<AssetsListPage>> fetchPageAssetsList(Integer size, Integer page, String patternName, String userRealName, String conditions) {
        return mHttpHelper.fetchPageAssetsList(size, page, patternName, userRealName, conditions);
    }

    @Override
    public Observable<BaseResponse<List<AssetsType>>> getAllAssetsType() {
        return mHttpHelper.getAllAssetsType();
    }

    @Override
    public Observable<BaseResponse<List<DepartmentBean>>> getAllDeparts(String comId) {
        return mHttpHelper.getAllDeparts(comId);
    }

    @Override
    public Observable<BaseResponse<ManagerListPage>> getAllEmpUsers(Integer size, Integer page, String patternName, String deptId) {
        return mHttpHelper.getAllEmpUsers(size, page, patternName, deptId);
    }

    @Override
    public Observable<BaseResponse<List<DepartmentBean>>> getAllOrgs() {
        return mHttpHelper.getAllOrgs();
    }

    @Override
    public Observable<BaseResponse<List<AssetsListItemInfo>>> fetchAllAssetsInfos() {
        return mHttpHelper.fetchAllAssetsInfos();
    }

    @Override
    public Observable<BaseResponse> borrowTools(NewBorrowBackPara borrowPara) {
        return mHttpHelper.borrowTools(borrowPara);
    }

    @Override
    public Observable<BaseResponse> backTools(NewBorrowBackPara backPara) {
        return mHttpHelper.backTools(backPara);
    }

    @Override
    public Observable<ResponseBody> getUserByFace(String appId, String signature, String requestId, FaceAuthPara faceAuthPara) {
        return mHttpHelper.getUserByFace(appId, signature, requestId, faceAuthPara);
    }

    @Override
    public Observable<BaseResponse<UserLoginResponse>> faceLogin(FaceLoginPara faceLoginPara) {
        return mHttpHelper.faceLogin(faceLoginPara);
    }
}
