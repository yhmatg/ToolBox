package com.android.toolbox.core.http.api;

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
import com.android.toolbox.core.bean.user.UserInfo;
import com.android.toolbox.core.bean.user.UserLoginResponse;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @author yhm
 * @date 2018/2/12
 */

public interface GeeksApis {

    //用户登录
    @POST("user-server/userauth/loginwithinfo")
    Observable<BaseResponse<UserLoginResponse>> login(@Body UserInfo userInfo);

    //设备登录
    @POST("/inventory-server/terminal/login")
    Observable<BaseResponse<TerminalInfo>> terminalLogin(@Body TerminalLoginPara terminalLoginPara);

    //获取所有资产类型
    @GET("assets-server/assetstypes")
    Observable<BaseResponse<List<AssetsType>>> getAllAssetsType();

    //模糊查询资产详情（写入标签）分页
    @GET("assets-server/assets/multiconditions")
    Observable<BaseResponse<AssetsListPage>> fetchPageAssetsList(@Query("size") Integer size, @Query("page") Integer page, @Query("pattern_name") String patternName, @Query("user_real_name") String userRealName, @Query("conditions") String conditions);

    //获取一个公司下所有部门
    @GET("user-server/orgs/{comId}/subs")
    Observable<BaseResponse<List<DepartmentBean>>> getAllDeparts(@Path("comId") String comId);

    //获取所有的公司部门
    @GET("user-server/orgs/authOrgs")
    Observable<BaseResponse<List<DepartmentBean>>> getAllOrgs();

    //获取所有员工分页
    @GET("user-server/emps")
    Observable<BaseResponse<ManagerListPage>> getAllEmpUsers(@Query("size") Integer size, @Query("page") Integer page, @Query("pattern_name") String patternName,@Query("dept_id") String deptId);

    //获取资产的所有详情
    @GET("assets-server/assets/multiconditions/unpage")
    Observable<BaseResponse<List<AssetsListItemInfo>>> fetchAllAssetsInfos();

    //借用工具
    @POST("assets-server/general/bussiness/apply/BORROW")
    Observable<BaseResponse> borrowTools(@Body NewBorrowBackPara borrowPara);

    //归还工具
    @POST("assets-server/general/bussiness/apply/BACK")
    Observable<BaseResponse> backTools(@Body NewBorrowBackPara backPara);

    //人脸获取用户信息
    @POST("gateway/faceauth")
    Observable<ResponseBody> getUserByFace(@Header("X-APPID") String appId, @Header("X-SIGNATURE") String signature, @Header("X-REQUEST-ID") String requestId, @Body FaceAuthPara faceAuthPara);
}
