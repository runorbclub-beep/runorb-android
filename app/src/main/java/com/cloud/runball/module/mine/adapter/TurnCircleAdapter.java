package com.cloud.runball.module.mine.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.R;
import com.cloud.runball.bean.UserPlayData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ns467
 */
public class TurnCircleAdapter extends RecyclerView.Adapter<TurnCircleAdapter.ViewHolder> {

    List<UserPlayData.SectionDurationDTO> list;
    public int max = 1;
    public int parentWidth = 0;

    public TurnCircleAdapter(int measureWidth) {
        this.list=new ArrayList<>();
        this.parentWidth = (int) (measureWidth * ((5.1 - 1.0) / 5.1));
    }

    public void updateMeasure(int measureWidth){
        this.parentWidth = (int) (measureWidth * ((5.1 - 1.0) / 5.1));
    }

    public void setData(List<UserPlayData.SectionDurationDTO> list) {
        this.list.clear();
        this.max=maxDuration(list);
        this.list.addAll(list);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_data_detail_item, parent, false);
        return new TurnCircleAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserPlayData.SectionDurationDTO info = list.get(position);

        holder.tv_circle.setText(info.getStart_section()+"~"+info.getStop_section());
        holder.tv_duration.setText((int)info.getSection_duration() + "s");

        //设置进度
        ViewGroup.LayoutParams params = holder.vProgress.getLayoutParams();
        if (max <= 0) {
            params.width = 0;
        } else {
            params.width = (int) (info.getSection_duration() * parentWidth / max);
        }

        holder.vProgress.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * 计算最大值
     * @param list
     * @return
     */
    public static int maxDuration(List<UserPlayData.SectionDurationDTO> list) {
        if(list.size()<=0){
            return 0;
        }
        int max= (int) list.get(0).getSection_duration();

        for(int i=1;i<list.size();i++){
            if(list.get(i).getSection_duration()>=max){
                max= (int) list.get(i).getSection_duration();
            }
        }
        return max;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final View vProgress;
        public final View vProgressTag;
        public final TextView tv_circle;
        public final TextView tv_duration;
        public final FrameLayout fyProgress;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            fyProgress = (FrameLayout) view.findViewById(R.id.fyProgress);
            vProgress = (View) view.findViewById(R.id.vProgress);
            vProgressTag = (View) view.findViewById(R.id.vProgressTag);
            tv_circle = (TextView) view.findViewById(R.id.tv_circle);
            tv_duration = (TextView) view.findViewById(R.id.tv_duration);
        }

    }
}
