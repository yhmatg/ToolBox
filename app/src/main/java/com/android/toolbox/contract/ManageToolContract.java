package com.android.toolbox.contract;

import com.android.toolbox.base.presenter.AbstractPresenter;
import com.android.toolbox.base.view.AbstractView;
import com.android.toolbox.core.bean.BaseResponse;
import com.android.toolbox.core.bean.assist.AssetFilterParameter;
import com.android.toolbox.core.bean.assist.AssetsListItemInfo;
import com.android.toolbox.core.bean.terminal.NewBorrowBackPara;

import java.util.List;

public interface ManageToolContract {
    interface View extends AbstractView {
        void handleFetchAllAssetsInfos(List<AssetsListItemInfo> assetsListItemInfos);

        void handleBorrowTools(BaseResponse borrowToolsResponse);

        void handleBackTools(BaseResponse backToolsResponse);

        void handlefetchPageAssetsInfos(List<AssetsListItemInfo> assetsInfos);
    }

    interface Presenter extends AbstractPresenter<View> {
        void fetchAllAssetsInfos();

        void borrowTools(NewBorrowBackPara borrowPara);

        void backTools(NewBorrowBackPara backPara);

        void fetchPageAssetsInfos(Integer size, Integer page, String patternName, String userRealName, AssetFilterParameter conditions);

    }
}
