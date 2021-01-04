package com.android.toolbox.contract;

import com.android.toolbox.base.presenter.AbstractPresenter;
import com.android.toolbox.base.view.AbstractView;
import com.android.toolbox.core.bean.BaseResponse;
import com.android.toolbox.core.bean.assist.AssetsListItemInfo;
import com.android.toolbox.core.bean.terminal.AssetBackPara;
import com.android.toolbox.core.bean.terminal.AssetBorrowPara;

import java.util.List;

public interface ManageToolContract {
    interface View extends AbstractView {
        void handleFetchAllAssetsInfos(List<AssetsListItemInfo> assetsListItemInfos);

        void handleBorrowTools(BaseResponse borrowToolsResponse);

        void handleBackTools(BaseResponse backToolsResponse);
    }

    interface Presenter extends AbstractPresenter<View> {
        void fetchAllAssetsInfos();

        void borrowTools(AssetBorrowPara borrowPara);

        void backTools(AssetBackPara backPara);
    }
}