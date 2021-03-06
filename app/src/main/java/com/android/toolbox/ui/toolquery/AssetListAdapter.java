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
import com.android.toolbox.utils.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AssetListAdapter extends RecyclerView.Adapter<AssetListAdapter.ViewHolder> {
    private static final String TAG_EPC = "tag_epc";
    private static final String ASSETS_ID = "assets_id";
    private List<AssetsListItemInfo> Data;
    private Context context;
    private boolean isManager = false;

    public AssetListAdapter(List<AssetsListItemInfo> data, Context context, boolean isManager) {
        Data = data;
        this.context = context;
        this.isManager = isManager;
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
        viewHolder.typeName.setText(typeName);
        String modeName = astItemInfo.getAst_model() == null ? "" : astItemInfo.getAst_model();
        viewHolder.mode.setText(modeName);
        String locName = astItemInfo.getLoc_name() == null ? "" : astItemInfo.getLoc_name();
        viewHolder.location.setText(locName);
        int astStatus = astItemInfo.getAst_used_status();
        if (isManager) {
            if (0 == astStatus) {
                viewHolder.astStatus.setText("??????");
                viewHolder.astStatus.setBackground(context.getDrawable(R.drawable.inuse_status_back));
            } else if (6 == astStatus) {
                viewHolder.astStatus.setText("??????");
                viewHolder.astStatus.setBackground(context.getDrawable(R.drawable.free_status_back));
            }else if( -1 == astStatus){
                viewHolder.astStatus.setText("????????????");
                viewHolder.astStatus.setBackground(context.getDrawable(R.drawable.inuse_status_back));
            }
        } else {
            String statusName = TextUtils.isEmpty(AssetsUseStatus.getName(astStatus)) ? "" : AssetsUseStatus.getName(astStatus);
            viewHolder.astStatus.setText(statusName);
            if ("??????".equals(statusName)) {
                viewHolder.astStatus.setBackground(context.getDrawable(R.drawable.free_status_back));
                viewHolder.astUser.setVisibility(View.GONE);
                viewHolder.astUserName.setVisibility(View.GONE);
            } else {
                viewHolder.astStatus.setBackground(context.getDrawable(R.drawable.inuse_status_back));
                String userName = astItemInfo.getUser_name();
                if(!StringUtils.isEmpty(userName)){
                    viewHolder.astUser.setVisibility(View.VISIBLE);
                    viewHolder.astUserName.setVisibility(View.VISIBLE);
                    viewHolder.astUserName.setText(userName);
                }
            }
        }


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
        @BindView(R.id.tv_type_name)
        TextView typeName;
        @BindView(R.id.tv_ast_loc_name)
        TextView location;
        @BindView(R.id.tv_ast_mode_name)
        TextView mode;
        @BindView(R.id.ast_status)
        TextView astStatus;
        @BindView(R.id.item_detail)
        RelativeLayout itemLayout;
        @BindView(R.id.tv_ast_user)
        TextView astUser;
        @BindView(R.id.tv_ast_user_name)
        TextView astUserName;

        public ViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
