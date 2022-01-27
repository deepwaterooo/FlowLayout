package com.me.cusviewgroup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class FlowAdapter extends FlowLayout.Adapter<FlowAdapter.FlowViewHolder> {

    private static final String TAG = "FlowAdapter";

    private Context mContext;
    private List<String> mContentList;

    public FlowAdapter(Context mContext, List<String> mContentList) {
        this.mContext = mContext;
        this.mContentList = mContentList;
    }

    @Override
        public FlowViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.flow_item, parent, false);
        // 给 View 设置 margin
        ViewGroup.MarginLayoutParams mlp = new ViewGroup.MarginLayoutParams(view.getLayoutParams());
//        int margin = BaseUtil.dip2px(mContext, 5);
        int margin = 5;
        mlp.setMargins(margin, margin, margin, margin);
        view.setLayoutParams(mlp);

        return new FlowViewHolder(view);
    }

    @Override
        public void onBindViewHolder(FlowViewHolder holder, int position) {
        holder.content.setText(mContentList.get(position));
    }

    @Override
        public int getItemCount() {
        return mContentList.size();
    }

    public class FlowViewHolder extends FlowLayout.ViewHolder {
        TextView content;

        public FlowViewHolder(View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.tv_test_content);
        }
    }
}
