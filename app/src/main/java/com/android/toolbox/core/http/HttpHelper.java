package com.android.toolbox.core.http;

import com.android.toolbox.core.bean.BaseResponse;
import com.android.toolbox.core.bean.assist.AssetsListItemInfo;
import com.android.toolbox.core.bean.assist.AssetsListPage;
import com.android.toolbox.core.bean.assist.AssetsType;
import com.android.toolbox.core.bean.assist.DepartmentBean;
import com.android.toolbox.core.bean.assist.ManagerListPage;
import com.android.toolbox.core.bean.terminal.AssetBackPara;
import com.android.toolbox.core.bean.terminal.AssetBorrowPara;
import com.android.toolbox.core.bean.terminal.TerminalInfo;
import com.android.toolbox.core.bean.terminal.TerminalLoginPara;
import com.android.toolbox.core.bean.user.UserInfo;
import com.android.toolbox.core.bean.user.UserLoginResponse;

import java.util.List;

import io.reactivex.Observable;

/**
 * @author yhm
 * @date 2017/11/27
 */

public interface HttpHelper {

    Observable<BaseResponse<UserLoginResponse>> login(UserInfo userInfo);

    Observable<BaseResponse<TerminalInfo>> terminalLogin(TerminalLoginPara terminalLoginPara);

    Observable<BaseResponse<AssetsListPage>> fetchPageAssetsList(Integer size, Integer page, String patternName, String userRealName, String conditions);

    Observable<BaseResponse<List<AssetsType>>> getAllAssetsType();

    Observable<BaseResponse<List<DepartmentBean>>> getAllDeparts(String comId);

    Observable<BaseResponse<ManagerListPage>> getAllEmpUsers(Integer size, Integer page, String patternName, String deptId);

    Observable<BaseResponse<List<DepartmentBean>>> getAllOrgs();

    Observable<BaseResponse<List<AssetsListItemInfo>>> fetchAllAssetsInfos();

    Observable<BaseResponse> borrowTools(AssetBorrowPara borrowPara);

    Observable<BaseResponse> backTools(AssetBackPara backPara);
}
