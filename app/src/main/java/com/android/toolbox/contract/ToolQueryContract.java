package com.android.toolbox.contract;

import com.android.toolbox.base.presenter.AbstractPresenter;
import com.android.toolbox.base.view.AbstractView;
import com.android.toolbox.core.bean.assist.AssetsListItemInfo;
import com.android.toolbox.core.bean.assist.AssetsType;

import java.util.List;

public interface ToolQueryContract {
    interface View extends AbstractView {
        void handleGetAllAssetsType(List<AssetsType> assetTypes);

        void handleFetchPageAssetsList(List<AssetsListItemInfo> assetsInfos);
    }

    interface Presenter extends AbstractPresenter<View> {
        void getAllAssetsType();

        void fetchPageAssetsList(Integer size, Integer page, String patternName, String userRealName, String conditions);

    }
}
