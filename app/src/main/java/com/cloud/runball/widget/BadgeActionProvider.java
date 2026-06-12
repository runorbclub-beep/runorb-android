package com.cloud.runball.widget;

import android.content.Context;
import androidx.core.view.ActionProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.cloud.runball.R;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.widget
 * @ClassName: BagesActionProvider
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/3/2 16:18
 * @UpdateUser: zhd
 * @UpdateDate: 2021/3/2 16:18
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class BadgeActionProvider extends ActionProvider {

    private ImageView mIvIcon;
    private TextView mTvBadge;

    // 用来记录是哪个View的点击，这样外部可以用一个Listener接受多个menu的点击。
    private int clickWhat;
    private OnClickListener onClickListener;

    private Context mContext;
    /**
     * Creates a new instance. ActionProvider classes should always implement a
     * constructor that takes a single Context parameter for inflating from menu XML.
     *
     * @param context Context for accessing resources.
     */
    public BadgeActionProvider(Context context) {
        super(context);
        this.mContext=context;
    }

    @Override
    public View onCreateActionView() {

        int size =  getContext().getResources().getDimensionPixelSize(androidx.appcompat.R.dimen.abc_action_bar_default_height_material);

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(size, size);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_badge_view, null, false);

        view.setLayoutParams(layoutParams);
        mIvIcon = (ImageView) view.findViewById(R.id.iv_icon);
        mTvBadge = (TextView) view.findViewById(R.id.tv_badge);
        view.setOnClickListener(onViewClickListener);
        return view;
    }


  View.OnClickListener onViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (onClickListener != null){
                onClickListener.onClick(clickWhat);
            }

        }
    };


    public void setOnClickListener(int what, OnClickListener onClickListener) {
        this.clickWhat = what;
        this.onClickListener = onClickListener;
    }


    public void setIcon(@DrawableRes int icon) {
        mIvIcon.setImageResource(icon);
    }


    public void setBadge(int i) {
        mTvBadge.setText(Integer.toString(i));
    }


    public void setTextInt(@StringRes int i) {
        mTvBadge.setText(i);
    }


    public void setText(CharSequence i) {
        mTvBadge.setText(i);
    }


    public interface OnClickListener {
        void onClick(int what);
    }

}
