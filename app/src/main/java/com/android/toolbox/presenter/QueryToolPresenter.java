package com.android.toolbox.presenter;

import com.android.toolbox.base.presenter.BasePresenter;
import com.android.toolbox.contract.ToolQueryContract;
import com.android.toolbox.core.DataManager;
import com.android.toolbox.core.bean.assist.AssetsListPage;
import com.android.toolbox.core.bean.assist.AssetsType;
import com.android.toolbox.core.http.widget.BaseObserver;
import com.android.toolbox.utils.RxUtils;

import java.util.ArrayList;
import java.util.List;

public class QueryToolPresenter extends BasePresenter<ToolQueryContract.View> implements ToolQueryContract.Presenter {
    @Override
    public void getAllAssetsType() {
        addSubscribe(DataManager.getInstance().getAllAssetsType()
                .compose(RxUtils.rxSchedulerHelper())
                .compose(RxUtils.handleResult())
                .subscribeWith(new BaseObserver<List<AssetsType>>(mView, false) {
                    @Override
                    public void onNext(List<AssetsType> assetsTypes) {
                        mView.handleGetAllAssetsType(assetsTypes);
                    }
                }));
    }

    @Override
    public void fetchPageAssetsList(Integer size, Integer page, String patternName, String userRealName, String conditions) {
        addSubscribe(DataManager.getInstance().fetchPageAssetsList(size, page, patternName, userRealName, conditions)
        .compose(RxUtils.rxSchedulerHelper())
        .compose(RxUtils.handleResult())
        .subscribeWith(new BaseObserver<AssetsListPage>(mView, false) {
            @Override
            public void onNext(AssetsListPage assetsListPage) {
                if (page <= assetsListPage.getPages()) {
                    mView.handleFetchPageAssetsList(assetsListPage.getList());
                } else {
                    mView.handleFetchPageAssetsList(new ArrayList<>());
                }
            }
        }));
    }
}
