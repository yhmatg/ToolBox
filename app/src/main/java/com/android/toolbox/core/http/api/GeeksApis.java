package com.android.toolbox.core.http.api;

import com.android.toolbox.core.bean.BaseResponse;
import com.android.toolbox.core.bean.assist.AssetsListPage;
import com.android.toolbox.core.bean.assist.AssetsType;
import com.android.toolbox.core.bean.terminal.TerminalInfo;
import com.android.toolbox.core.bean.terminal.TerminalLoginPara;
import com.android.toolbox.core.bean.user.UserInfo;
import com.android.toolbox.core.bean.user.UserLoginResponse;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
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

}
