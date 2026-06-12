package com.cloud.runball.module.mine.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.utils.TimeUtils;
import com.cloud.runball.bean.PlayData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ns467
 */
public class MineDataRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<PlayData> dateInfos;

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    //上拉加载更多
    public static final int PULLUP_LOAD_MORE = 0;
    //正在加载中
    public static final int LOADING_MORE = 1;
    //没有加载更多 隐藏
    public static final int NO_LOAD_MORE = 2;

    //上拉加载更多状态-默认为
    private int mLoadMoreStatus = NO_LOAD_MORE;

    public MineDataRecycleAdapter(List<PlayData> dateInfos) {
        this.dateInfos = dateInfos;
    }

    public MineDataRecycleAdapter() {
        this.dateInfos = new ArrayList<>();
    }

    public void notifyDataSetChanged(List<PlayData> dates) {
        this.dateInfos = dates;
        this.notifyDataSetChanged();
    }

    private OnItemClickListener onItemClickListener;


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 更新加载更多状态
     *
     * @param status
     */
    public void changeMoreStatus(int status) {
        mLoadMoreStatus = status;
        notifyDataSetChanged();
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tv_circle;
        TextView tv_time;
        TextView tv_speed;
        TextView tv_duration;
        TextView tv_distance;
        ImageView ivUpDown;

        View myView;  //自定义View用于recyclerview的点击事件

        public ItemViewHolder(View itemView) {
            super(itemView);
            myView = itemView;
            tv_circle = itemView.findViewById(R.id.tv_circle);
            tv_time = itemView.findViewById(R.id.tv_time);

            tv_speed = itemView.findViewById(R.id.tv_speed);
            tv_duration = itemView.findViewById(R.id.tv_duration);
            tv_distance = itemView.findViewById(R.id.tv_distance);
            ivUpDown = itemView.findViewById(R.id.ivUpDown);

        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        TextView tvLoad;
        LinearLayout lyLoad;
        View myView;

        public FooterViewHolder(View view) {
            super(view);
            myView = view;
            progressBar = itemView.findViewById(R.id.progressBar);
            tvLoad = itemView.findViewById(R.id.tvLoad);
            lyLoad = itemView.findViewById(R.id.lyLoad);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_mine_data_item, parent, false);
        final MineDataRecycleAdapter.ItemViewHolder viewHolder = new MineDataRecycleAdapter.ItemViewHolder(view);
        //点击item的监听器
        viewHolder.myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int postion = viewHolder.getLayoutPosition()-1;
                //程序执行到此，会去执行具体实现的onItemClick()方法
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, dateInfos.get(postion));
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //绑定数据
        if (dateInfos.size() > 0 && dateInfos.size() > position) {
            PlayData data = dateInfos.get(position);

            if (data.getCompare_last() > 0) {
                ((MineDataRecycleAdapter.ItemViewHolder) holder).ivUpDown.setBackgroundResource(R.mipmap.mine_data_up);
            } else {
                ((MineDataRecycleAdapter.ItemViewHolder) holder).ivUpDown.setBackgroundResource(R.mipmap.mine_data_down);
            }
            //圈数
            ((MineDataRecycleAdapter.ItemViewHolder) holder).tv_circle.setText(data.getCircle_count_format() + data.getCircle_count_unit());

            //持续时间
            ((MineDataRecycleAdapter.ItemViewHolder) holder).tv_duration.setText(TimeUtils.formatDuration(data.getDuration()));
            //开始时间
            ((MineDataRecycleAdapter.ItemViewHolder) holder).tv_time.setText(data.getStart_time_format());

            ((MineDataRecycleAdapter.ItemViewHolder) holder).tv_speed.setText(data.getSpeed_max_format() + data.getSpeed_max_unit());

            //距离
            ((MineDataRecycleAdapter.ItemViewHolder) holder).tv_distance.setText(data.getDistance_format() + data.getDistance_unit());
        }
    }


    @Override
    public int getItemCount() {
        return this.dateInfos!=null?dateInfos.size():0;
    }


    public interface OnItemClickListener {
        void onItemClick(View view, PlayData data);
    }
}
