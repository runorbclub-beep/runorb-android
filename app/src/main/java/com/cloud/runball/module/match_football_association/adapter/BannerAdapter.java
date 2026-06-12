package com.cloud.runball.module.match_football_association.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.cloud.runball.R;
import com.cloud.runball.bean.BannerData;
import com.cloud.runball.utils.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.adapter
 * @ClassName: ImageAdapter
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/2/1 9:44
 * @UpdateUser: zhd
 * @UpdateDate: 2021/2/1 9:44
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.ViewHolder> {

    private List<BannerData> dataInfo = new ArrayList<>();
    private Context mContext;

    public BannerAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<BannerData> infos){
        this.dataInfo = infos;
    }

    private BannerAdapter.OnItemClickListener onItemClickListener;


    public void setOnItemClickListener(BannerAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        View myView;

        public ViewHolder(View itemView) {
            super(itemView);
            myView = itemView;
            ivImage = itemView.findViewById(R.id.ivImage);
        }
    }

    @NonNull
    @Override
    public BannerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_match_banner, parent, false);
        final BannerAdapter.ViewHolder viewHolder = new BannerAdapter.ViewHolder(view);
        //点击item的监听器
        viewHolder.myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //程序执行到此，会去执行具体实现的onItemClick()方法
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, (BannerData) v.getTag());
                }
            }
        });
        return viewHolder;
    }


//    RoundedCorners roundedCorners = new RoundedCorners(20);
//    RequestOptions options = RequestOptions.bitmapTransform(roundedCorners).override(480, 480);

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BannerData data = dataInfo.get(position);
        holder.myView.setTag(data);
        if (data.getImg_path().startsWith("http")) {
            Glide.with(mContext)
                .load(data.getImg_path())
                .error(R.mipmap.banner_default)
//                .apply(options)
                .into(holder.ivImage);
        } else {
            Glide.with(mContext)
                .load(Constant.getBaseUrl() + "/" + data.getImg_path())
                .error(R.mipmap.banner_default)
//                .apply(options)
                .into(holder.ivImage);
        }
    }

    @Override
    public int getItemCount() {
        return dataInfo != null ? dataInfo.size() : 0;
    }


    public interface OnItemClickListener {
        void onItemClick(View view, BannerData data);
    }

}