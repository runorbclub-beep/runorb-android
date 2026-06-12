package com.cloud.runball.module.home.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.listener.OnItemClickListener;
import com.cloud.runball.model.ShakeMatchModel;
import com.cloud.runball.view.HorseView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.module.home.adapter
 * @ClassName: OtherMainMatchAdapter
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/7/21 16:34
 * @UpdateUser: zhd
 * @UpdateDate: 2021/7/21 16:34
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class OtherMainMatchHorseAdapter extends LinearLayout {

    List<ShakeMatchModel.ShakeItem> dataInfo = new ArrayList<>();
    DecimalFormat mDecimalFormat = new DecimalFormat("0.00");

    boolean isSelected=true;
    long mShake_group_id;
    boolean isMoving=false;

    OnItemClickListener onItemClickListener;


    public OtherMainMatchHorseAdapter(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs,9);
    }

    public OtherMainMatchHorseAdapter(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setOrientation(VERTICAL);
        this.isMoving=true;
    }

    public List<ShakeMatchModel.ShakeItem> getDataInfo() {
        return dataInfo;
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.onItemClickListener=listener;
    }

    public void selectMe(long shake_group_id){
        this.mShake_group_id=shake_group_id;
    }

    public void enabledHorseSelected(boolean enabled){
        this.isSelected=enabled;
    }

    public void setList(List<ShakeMatchModel.ShakeItem> infos) {
        this.removeAllViewsInLayout();
        this.dataInfo = infos;
        int size=infos.size();
        for(int pos=0;pos<size;pos++){
            View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_other_main_match_item, null, false);
            onBindViewHolder(view,pos);
            int index=pos;
            view.setOnClickListener(v->{
                if(onItemClickListener!=null && isSelected){
                    onItemClickListener.onItemClick(dataInfo.get(index),index);
                }
            });
            this.addView(view,pos);
        }
    }



    public void setDataInfo(List<ShakeMatchModel.ShakeItem> infos){
        this.dataInfo = infos;
    }

    public void notifyDataSetChanged(){
        int size=this.getChildCount();
        for(int pos=0;pos<size;pos++){
            onBindViewHolder(this.getChildAt(pos),pos);
        }
    }

    public void notifyDataSetChanged(List<ShakeMatchModel.ShakeItem> infos){
        this.dataInfo = infos;
        this.isMoving=true;
        notifyDataSetChanged();
    }

    public void onBindViewHolder(View itemView, int position) {
        HorseView ivFlag = itemView.findViewById(R.id.ivFlag);
        TextView tvTitle = itemView.findViewById(R.id.tvTitle);
        TextView tvNo = itemView.findViewById(R.id.tvNo);
        TextView tvDistance = itemView.findViewById(R.id.tvDistance);
        TextView tvPerson = itemView.findViewById(R.id.tvPerson);
        ProgressBar progressBar = itemView.findViewById(R.id.progressBar);

        ShakeMatchModel.ShakeItem data = dataInfo.get(position);
        if(data.getIndex()==0){
            ivFlag.setBackgroundResource(R.drawable.anima_horse_1);
        }else if(data.getIndex()==1){
            ivFlag.setBackgroundResource(R.drawable.anima_horse_2);
        }else if(data.getIndex()==2){
            ivFlag.setBackgroundResource(R.drawable.anima_horse_3);
        }else if(data.getIndex()==3){
            ivFlag.setBackgroundResource(R.drawable.anima_horse_4);
        }else if(data.getIndex()==4){
            ivFlag.setBackgroundResource(R.drawable.anima_horse_5);
        }else if(data.getIndex()==5){
            ivFlag.setBackgroundResource(R.drawable.anima_horse_6);
        }else if(data.getIndex()==6){
            ivFlag.setBackgroundResource(R.drawable.anima_horse_7);
        }else if(data.getIndex()==7){
            ivFlag.setBackgroundResource(R.drawable.anima_horse_8);
        }else{
            ivFlag.setBackgroundResource(R.drawable.anima_horse_1);
        }

        if(data.getShake_group_id()==mShake_group_id){
            progressBar.setProgressDrawable(getContext().getResources().getDrawable(R.drawable.match_progressbar_me2));
            tvNo.setBackgroundResource(R.drawable.selector_horse_no_selected);
            tvNo.setTextColor(getContext().getResources().getColor(R.color.main_match_horse_box_selected));
        }else{
            progressBar.setProgressDrawable(getContext().getResources().getDrawable(R.drawable.match_progressbar));
            tvNo.setBackgroundResource(R.drawable.selector_horse_no);
            tvNo.setTextColor(getContext().getResources().getColor(R.color.main_match_horse_box));
        }

        tvNo.setText(String.valueOf(position+1));
        //tvNo.setVisibility(View.GONE);

        tvTitle.setText(data.getTitle());
        tvDistance.setText(formatDistance(data.getDistance()/1000.0f));
        tvPerson.setText(String.format(getContext().getString(R.string.lbl_main_match_record_person_2),formatNum(data.getNum())));
        progressBar.setProgress(100);
        ivFlag.startRun();

        //这里的随机百分比是经过计算的
        //ivFlag.setValue(isMoving?getMovePercent(position):0,true);
        if(isMoving){
            int ivFlagWidth = itemView.findViewById(R.id.ivFlag).getWidth();
            ivFlag.setValue(getMovePercent(position), ivFlagWidth, true);
        }
    }


    private String formatDistance(double distance){
        if(distance<1000){
            return mDecimalFormat.format(distance)+"km";
        }else if(distance>=1000 && distance < 10000){
            return mDecimalFormat.format(distance/1000.0)+"k km";
        }else{
            return mDecimalFormat.format(distance/10000.0)+"w km";
        }
    }

    private String formatNum(int num){
        if(num<1000){
            return String.valueOf(num);
        }else if(num>=1000 && num < 10000){
            return String.valueOf(num/1000)+"k";
        }else{
            return String.valueOf(num/10000.0)+"w";
        }
    }

    public double rdmFactorMove(){
        return Math.random();
        //return 1;
    }

    /**
     * 根据最大最小之间算出跑的百分比距离
     * @param pos
     * @return
     */
    public double getMovePercent(int pos){
        if(dataInfo.size()==0){
            return 0;
        }


        double max = dataInfo.get(0).getDistance();
        double mix= dataInfo.get(0).getDistance();
        int size=dataInfo.size();
        for(int x=1; x<size; x++) {
            if(dataInfo.get(x).getDistance() > max) {
                max = dataInfo.get(x).getDistance();
            }

            if(dataInfo.get(x).getDistance() < mix) {
                mix = dataInfo.get(x).getDistance();
            }
        }
        if(max<=0.01f || max-mix==0){
            return 0;
        }
        double target=dataInfo.get(pos).getDistance();
        return (target-mix)/(max-mix);

        //return rdmFactorMove();
    }

    public int getItemCount() {
        return dataInfo!=null?dataInfo.size():0;
    }

//    public void onNotifyHorse(int pos) {
//        View itemView=this.getChildAt(pos);
//        HorseView ivFlag = itemView.findViewById(R.id.ivFlag);
//        ivFlag.setValue(0,true);
//    }
//
//   public void randomMove(){
//
//   }

}
