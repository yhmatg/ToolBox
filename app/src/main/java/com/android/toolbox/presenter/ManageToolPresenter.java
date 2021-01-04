package com.android.toolbox.presenter;

import com.android.toolbox.base.presenter.BasePresenter;
import com.android.toolbox.contract.ManageToolContract;
import com.android.toolbox.core.DataManager;
import com.android.toolbox.core.bean.BaseResponse;
import com.android.toolbox.core.bean.assist.AssetsListItemInfo;
import com.android.toolbox.core.bean.terminal.AssetBackPara;
import com.android.toolbox.core.bean.terminal.AssetBorrowPara;
import com.android.toolbox.core.http.widget.BaseObserver;
import com.android.toolbox.utils.RxUtils;

import java.util.List;

public class ManageToolPresenter extends BasePresenter<ManageToolContract.View> implements ManageToolContract.Presenter {
    @Override
    public void fetchAllAssetsInfos() {
        addSubscribe(DataManager.getInstance().fetchAllAssetsInfos()
        .compose(RxUtils.rxSchedulerHelper())
        .compose(RxUtils.handleResult())
        .subscribeWith(new BaseObserver<List<AssetsListItemInfo>>(mView, false) {
            @Override
            public void onNext(List<AssetsListItemInfo> assetsListItemInfos) {
                mView.handleFetchAllAssetsInfos(assetsListItemInfos);
            }
        }));
    }

    @Override
    public void borrowTools(AssetBorrowPara borrowPara) {
        addSubscribe(DataManager.getInstance().borrowTools(borrowPara)
                .compose(RxUtils.rxSchedulerHelper())
                .subscribeWith(new BaseObserver<BaseResponse>(mView, false) {
                    @Override
                    public void onNext(BaseResponse baseResponse) {
                        mView.handleBorrowTools(baseResponse);
                    }
                }));
    }

    @Override
    public void backTools(AssetBackPara backPara) {
        addSubscribe(DataManager.getInstance().backTools(backPara)
        .compose(RxUtils.rxSchedulerHelper())
        .subscribeWith(new BaseObserver<BaseResponse>(mView, false) {
            @Override
            public void onNext(BaseResponse baseResponse) {
                mView.handleBackTools(baseResponse);
            }
        }));
    }
}
