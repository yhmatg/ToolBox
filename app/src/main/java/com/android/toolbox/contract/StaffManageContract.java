package com.android.toolbox.contract;

import com.android.toolbox.base.presenter.AbstractPresenter;
import com.android.toolbox.base.view.AbstractView;
import com.android.toolbox.core.bean.assist.DepartmentBean;
import com.android.toolbox.core.bean.assist.MangerUser;

import java.util.List;

public interface StaffManageContract {
    interface View extends AbstractView {
        void handleGetAllDeparts(List<DepartmentBean> departs);

        void handleGetAllEmpUsers(List<MangerUser> users);
    }

    interface Presenter extends AbstractPresenter<View> {
        void getAllDeparts(String comId);

        void getAllEmpUsers(Integer size, Integer page, String patternName);
    }
}
