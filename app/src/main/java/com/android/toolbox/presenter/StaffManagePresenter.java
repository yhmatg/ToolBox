package com.android.toolbox.presenter;

import com.android.toolbox.base.presenter.BasePresenter;
import com.android.toolbox.contract.StaffManageContract;
import com.android.toolbox.core.DataManager;
import com.android.toolbox.core.bean.assist.DepartmentBean;
import com.android.toolbox.core.bean.assist.ManagerListPage;
import com.android.toolbox.core.http.widget.BaseObserver;
import com.android.toolbox.utils.RxUtils;

import java.util.ArrayList;
import java.util.List;

public class StaffManagePresenter extends BasePresenter<StaffManageContract.View> implements StaffManageContract.Presenter {
    @Override
    public void getAllOrgs() {
        addSubscribe(DataManager.getInstance().getAllOrgs()
        .compose(RxUtils.handleResult())
        .compose(RxUtils.rxSchedulerHelper())
        .subscribeWith(new BaseObserver<List<DepartmentBean>>(mView, false) {
            @Override
            public void onNext(List<DepartmentBean> assetsTypes) {
                mView.handleGetAllDeparts(assetsTypes);
            }
        }));
    }

    @Override
    public void getAllEmpUsers(Integer size, Integer page, String patternName, String deptId) {
        addSubscribe(DataManager.getInstance().getAllEmpUsers(size, page, patternName,deptId)
        .compose(RxUtils.handleResult())
        .compose(RxUtils.rxSchedulerHelper())
        .subscribeWith(new BaseObserver<ManagerListPage>(mView, false) {
            @Override
            public void onNext(ManagerListPage managerListPage) {
                if (page <= managerListPage.getPages()) {
                    mView.handleGetAllEmpUsers(managerListPage.getList());
                } else {
                    mView.handleGetAllEmpUsers(new ArrayList<>());
                }
            }
        }));
    }
}
