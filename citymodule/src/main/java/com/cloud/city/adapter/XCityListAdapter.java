package com.cloud.city.adapter;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cloud.city.R;
import com.cloud.city.model.City;
import java.util.List;

/**
 * @Author: Bro0cL
 * @Date: 2018/2/5 12:06
 */
public class XCityListAdapter extends RecyclerView.Adapter<XCityListAdapter.BaseViewHolder> {
    private Context mContext;
    private List<City> mData;
    private InnerListener mInnerListener;
    private LinearLayoutManager mLayoutManager;

    public XCityListAdapter(Context context, List<City> data) {
        this.mData = data;
        this.mContext = context;
    }

    public void setLayoutManager(LinearLayoutManager manager){
        this.mLayoutManager = manager;
    }

    public void updateData(List<City> data){
        this.mData = data;
        notifyDataSetChanged();
    }

    /**
     * 滚动RecyclerView到索引位置
     * @param index
     */
    public void scrollToSection(String index){
        if (mData == null || mData.isEmpty()) {
            return;
        }
        if (TextUtils.isEmpty(index)) {
            return;
        }

        int size = mData.size();
        for (int i = 0; i < size; i++) {
            if (TextUtils.equals(index.substring(0, 1), mData.get(i).getSection().substring(0, 1))){
                if (mLayoutManager != null){
                    mLayoutManager.scrollToPositionWithOffset(i, 0);
                    if (TextUtils.equals(index.substring(0, 1), "定")) {
                        //防止滚动时进行刷新
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                 notifyItemChanged(0);
                            }
                        }, 1000);
                    }
                    return;
                }
            }
        }
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cp_list_item_default_layout, parent, false);
        return new DefaultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        final int pos = holder.getAdapterPosition();
        final City data = mData.get(pos);
        if (data == null) {
            return;
        }
        ((DefaultViewHolder)holder).name.setText(data.getName());
        ((DefaultViewHolder) holder).name.setOnClickListener(v -> {
            if (mInnerListener != null){
                mInnerListener.dismiss(pos, data);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }


    public void setInnerListener(InnerListener listener){
        this.mInnerListener = listener;
    }

    static class BaseViewHolder extends RecyclerView.ViewHolder{
        BaseViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class DefaultViewHolder extends BaseViewHolder{
        TextView name;
        DefaultViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cp_list_item_name);
        }
    }
}
