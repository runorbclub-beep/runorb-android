package com.cloud.runball.module.match.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.R;

import java.util.List;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.adapter
 * @ClassName: SearchResultAdapter
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/2/27 14:15
 * @UpdateUser: zhd
 * @UpdateDate: 2021/2/27 14:15
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder>{
    List<String> dataInfo;

    Context mContext;

    public SearchResultAdapter(Context context, List<String> infos) {
        this.mContext = context;
        this.dataInfo = infos;
    }

    public void notifyDataSetChanged(List<String> infos) {
        this.dataInfo = infos;
        this.notifyDataSetChanged();
    }

    private SearchResultAdapter.OnItemClickListener onItemClickListener;

    //提供setter方法
    public void setOnItemClickListener(SearchResultAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    //用于缓存的ViewHolder
    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvContent;
        View myView;

        public ViewHolder(View itemView) {
            super(itemView);
            myView = itemView;
            tvContent = itemView.findViewById(R.id.tvContent);
        }
    }

    @NonNull
    @Override
    public SearchResultAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_item, parent, false);
        final SearchResultAdapter.ViewHolder viewHolder = new SearchResultAdapter.ViewHolder(view);
        //点击item的监听器
        viewHolder.myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int postion = viewHolder.getLayoutPosition();
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, dataInfo.get(postion));
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultAdapter.ViewHolder holder, int position) {
        String data = dataInfo.get(position);

    }

    @Override
    public int getItemCount() {
        return dataInfo.size();
    }


    public interface OnItemClickListener {
        void onItemClick(View view, String data);
    }
}
