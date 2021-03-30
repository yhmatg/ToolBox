package com.android.toolbox.core.http;

import com.android.toolbox.core.bean.BaseResponse;
import com.android.toolbox.core.bean.assist.AssetsListItemInfo;
import com.android.toolbox.core.bean.assist.AssetsListPage;
import com.android.toolbox.core.bean.assist.AssetsType;
import com.android.toolbox.core.bean.assist.DepartmentBean;
import com.android.toolbox.core.bean.assist.ManagerListPage;
import com.android.toolbox.core.bean.terminal.FaceAuthPara;
import com.android.toolbox.core.bean.terminal.NewBorrowBackPara;
import com.android.toolbox.core.bean.terminal.TerminalInfo;
import com.android.toolbox.core.bean.terminal.TerminalLoginPara;
import com.android.toolbox.core.bean.user.FaceLoginPara;
import com.android.toolbox.core.bean.user.UserInfo;
import com.android.toolbox.core.bean.user.UserLoginResponse;
import com.android.toolbox.core.http.api.GeeksApis;
import com.android.toolbox.core.http.client.RetrofitClient;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

/**
 * 对外隐藏进行网络请求的实现细节
 *
 * @author yhm
 * @date 2017/11/27
 */

public class HttpHelperImpl implements HttpHelper {

    private GeeksApis mGeeksApis;

    private volatile static HttpHelperImpl INSTANCE = null;


    private HttpHelperImpl(GeeksApis geeksApis) {
        mGeeksApis = geeksApis;
    }

    public static HttpHelperImpl getInstance() {
        if (INSTANCE == null) {
            synchronized (HttpHelperImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpHelperImpl(RetrofitClient.getInstance().create(GeeksApis.class));
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public Observable<BaseResponse<UserLoginResponse>> login(UserInfo userInfo) {
        return mGeeksApis.login(userInfo);
    }

    @Override
    public Observable<BaseResponse<TerminalInfo>> terminalLogin(TerminalLoginPara terminalLoginPara) {
        return mGeeksApis.terminalLogin(terminalLoginPara);
    }

    @Override
    public Observable<BaseResponse<AssetsListPage>> fetchPageAssetsList(Integer size, Integer page, String patternName, String userRealName, String conditions) {
        return mGeeksApis.fetchPageAssetsList(size, page, patternName, userRealName, conditions);
    }

    @Override
    public Observable<BaseResponse<List<AssetsType>>> getAllAssetsType() {
        return mGeeksApis.getAllAssetsType();
    }

    @Override
    public Observable<BaseResponse<List<DepartmentBean>>> getAllDeparts(String comId) {
        return mGeeksApis.getAllDeparts(comId);
    }

    @Override
    public Observable<BaseResponse<ManagerListPage>> getAllEmpUsers(Integer size, Integer page, String patternName, String deptId) {
        return mGeeksApis.getAllEmpUsers(size, page, patternName, deptId);
    }

    @Override
    public Observable<BaseResponse<List<DepartmentBean>>> getAllOrgs() {
        return mGeeksApis.getAllOrgs();
    }

    @Override
    public Observable<BaseResponse<List<AssetsListItemInfo>>> fetchAllAssetsInfos() {
        return mGeeksApis.fetchAllAssetsInfos();
    }

    @Override
    public Observable<BaseResponse> borrowTools(NewBorrowBackPara borrowPara) {
        return mGeeksApis.borrowTools(borrowPara);
    }

    @Override
    public Observable<BaseResponse> backTools(NewBorrowBackPara backPara) {
        return mGeeksApis.backTools(backPara);
    }

    @Override
    public Observable<ResponseBody> getUserByFace(String appId, String signature, String requestId, FaceAuthPara faceAuthPara) {
        return mGeeksApis.getUserByFace(appId, signature, requestId, faceAuthPara);
    }

    @Override
    public Observable<BaseResponse<UserLoginResponse>> faceLogin(FaceLoginPara faceLoginPara) {
        return mGeeksApis.faceLogin(faceLoginPara);
    }

}
