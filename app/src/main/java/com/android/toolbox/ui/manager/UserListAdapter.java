package com.android.toolbox.ui.manager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.toolbox.R;
import com.android.toolbox.core.bean.assist.MangerUser;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {
    private List<MangerUser> Data;
    private Context context;

    public UserListAdapter(List<MangerUser> data, Context context) {
        Data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.user_list_item, viewGroup, false);
        return new ViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        MangerUser mangerUser = Data.get(i);
        String name = TextUtils.isEmpty(mangerUser.getUser_real_name()) ? "" : mangerUser.getUser_real_name();
        viewHolder.userName.setText(name);
        String code = TextUtils.isEmpty(mangerUser.getUser_emp_code()) ? "" : mangerUser.getUser_emp_code();
        viewHolder.userCode.setText(code);
        String depName = TextUtils.isEmpty(mangerUser.getDept_name()) ? "" : mangerUser.getDept_name();
        viewHolder.depName.setText(depName);

    }

    @Override
    public int getItemCount() {
        return Data == null ? 0 : Data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_user_name)
        TextView userName;
        @BindView(R.id.tv_code_num)
        TextView userCode;
        @BindView(R.id.tv_dep_name)
        TextView depName;
        @BindView(R.id.bt_input_face)
        Button inputFace;
        @BindView(R.id.bt_input_code)
        Button inputCode;

        public ViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
