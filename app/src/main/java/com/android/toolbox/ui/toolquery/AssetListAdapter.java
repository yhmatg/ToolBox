package com.android.toolbox.ui.toolquery;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.toolbox.R;
import com.android.toolbox.core.bean.assist.AssetsListItemInfo;
import com.android.toolbox.core.bean.emun.AssetsUseStatus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AssetListAdapter extends RecyclerView.Adapter<AssetListAdapter.ViewHolder> {
    private static final String TAG_EPC = "tag_epc";
    private static final String ASSETS_ID="assets_id";
    private List<AssetsListItemInfo> Data;
    private Context context;

    public AssetListAdapter(Context context, List<AssetsListItemInfo> assetsInfos) {
        this.context = context;
        this.Data = assetsInfos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.ast_list_item, viewGroup, false);
        return new ViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        AssetsListItemInfo astItemInfo = Data.get(i);
        String astBarcode = TextUtils.isEmpty(astItemInfo.getAst_barcode()) ? "" : astItemInfo.getAst_barcode();
        viewHolder.astCode.setText(astBarcode);
        String astName = TextUtils.isEmpty(astItemInfo.getAst_name()) ? "" : astItemInfo.getAst_name();
        viewHolder.astName.setText(astName);
        String typeName = astItemInfo.getType_name() == null ? "" : astItemInfo.getType_name();
        viewHolder.userName.setText(typeName);
        String modeName = astItemInfo.getAst_model() == null ? "" : astItemInfo.getAst_model();
        viewHolder.location.setText(modeName);
        int astStatus = astItemInfo.getAst_used_status();
        String statusName = TextUtils.isEmpty(AssetsUseStatus.getName(astStatus)) ? "" : AssetsUseStatus.getName(astStatus);
        viewHolder.astStatus.setText(statusName);
        viewHolder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return Data == null ? 0 : Data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_asset_num)
        TextView astCode;
        @BindView(R.id.ast_real_name)
        TextView astName;
        @BindView(R.id.tv_user_name)
        TextView userName;
        @BindView(R.id.tv_ast_loc_name)
        TextView location;
        @BindView(R.id.ast_status)
        TextView astStatus;
        @BindView(R.id.item_detail)
        RelativeLayout itemLayout;

        public ViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
