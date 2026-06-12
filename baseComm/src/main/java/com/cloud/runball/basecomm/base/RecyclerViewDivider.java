package com.cloud.runball.basecomm.base;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.adapter
 * @ClassName: RecyclerViewDivider
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/2/1 11:27
 * @UpdateUser: zhd
 * @UpdateDate: 2021/2/1 11:27
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class RecyclerViewDivider extends RecyclerView.ItemDecoration {

    private int mTop=50;
    private int mLeft=0;
    private int mRight=0;
    private int mBottom=0;
    public RecyclerViewDivider(){
        mTop=50;
        mLeft=0;
        mRight=0;
        mBottom=0;
    }

    public RecyclerViewDivider(int top){
        mTop=top;
    }

    public RecyclerViewDivider(int left,int top,int right,int bottom){
        mLeft=left;
        mTop=top;
        mRight=right;
        mBottom=bottom;
    }



    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(mLeft,mTop,mRight,mBottom);
    }
}
