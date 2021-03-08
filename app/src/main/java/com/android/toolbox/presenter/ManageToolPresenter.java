package com.android.toolbox.presenter;

import com.android.toolbox.R;
import com.android.toolbox.base.presenter.BasePresenter;
import com.android.toolbox.contract.ManageToolContract;
import com.android.toolbox.core.DataManager;
import com.android.toolbox.core.bean.BaseResponse;
import com.android.toolbox.core.bean.assist.AssetFilterParameter;
import com.android.toolbox.core.bean.assist.AssetsListItemInfo;
import com.android.toolbox.core.bean.assist.AssetsListPage;
import com.android.toolbox.core.bean.terminal.NewBorrowBackPara;
import com.android.toolbox.core.http.widget.BaseObserver;
import com.android.toolbox.utils.RxUtils;
import com.android.toolbox.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

public class ManageToolPresenter extends BasePresenter<ManageToolContract.View> implements ManageToolContract.Presenter {

    private ArrayList<AssetsListItemInfo> allAssets = new ArrayList<>();
    ;

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
    public void borrowTools(NewBorrowBackPara borrowPara) {
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
    public void backTools(NewBorrowBackPara backPara) {
        addSubscribe(DataManager.getInstance().backTools(backPara)
                .compose(RxUtils.rxSchedulerHelper())
                .subscribeWith(new BaseObserver<BaseResponse>(mView, false) {
                    @Override
                    public void onNext(BaseResponse baseResponse) {
                        mView.handleBackTools(baseResponse);
                    }
                }));
    }

    @Override
    public void fetchPageAssetsInfos(Integer size, Integer page, String patternName, String userRealName, AssetFilterParameter conditions) {
        addSubscribe(DataManager.getInstance().fetchPageAssetsList(size, page, patternName, userRealName, conditions.toString())
                .compose(RxUtils.rxSchedulerHelper())
                .compose(RxUtils.handleResult())
                .subscribeWith(new BaseObserver<AssetsListPage>(mView, false) {
                    @Override
                    public void onNext(AssetsListPage assetsInfoPage) {
                        int pageNum = assetsInfoPage.getPageNum();
                        int pages = assetsInfoPage.getPages();
                        if(pageNum == 1){
                            allAssets.clear();
                        }
                        allAssets.addAll(assetsInfoPage.getList());
                        if (pageNum + 1 <= pages) {
                            fetchPageAssetsInfos(size, pageNum + 1, patternName, userRealName, conditions);
                        } else {
                            mView.handlefetchPageAssetsInfos(allAssets);
                        }
                    }
                }));
    }
}
