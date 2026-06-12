package com.cloud.runball.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.App;
import com.cloud.runball.BuildConfig;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.utils.ScreenWindowManager;
import com.cloud.runball.share.constant.ShareTargetConstant;
import com.cloud.runball.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

public class ShareTargetDialog {

    View mView;
    Dialog dialog;
    ConfirmCallBack confirmCallBack;

    public void show(Context context, ConfirmCallBack confirmCallBack) {
        if (dialog == null) {
            this.confirmCallBack = confirmCallBack;
            mView = LayoutInflater.from(context).inflate(R.layout.dialog_share_target, null);

            TextView tvCancel = mView.findViewById(R.id.tvCancel);
            tvCancel.setOnClickListener(v -> {
                if (this.confirmCallBack != null) {
                    dismiss();
                    this.confirmCallBack.onCancel();
                }
            });

            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (ScreenUtils.isNavigationBarShow(windowManager)) {
                LinearLayout lyContent = mView.findViewById(R.id.lyContent);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) lyContent.getLayoutParams();
                params.bottomMargin = (int) (context.getResources().getDisplayMetrics().density * 20);
                lyContent.setLayoutParams(params);
            }

            List<ShareTarget> data = new ArrayList<>();
            String channelName = BuildConfig.FLAVOR;
            if(!channelName.equalsIgnoreCase("googleplay")) {
                data.add(
                        new ShareTarget(
                            ShareTargetConstant.SHARE_WEIXIN_CIRCLE,
                            R.mipmap.share_wechat_circle,
                            context.getString(R.string.lbl_share_wechat_circle)
                        )
                );
                data.add(
                        new ShareTarget(
                            ShareTargetConstant.SHARE_WEIXIN,
                            R.mipmap.share_wechat,
                            context.getString(R.string.lbl_share_wechat)
                        )
                );
            }
            data.add(
                    new ShareTarget(
                        ShareTargetConstant.LOCAL_SAVE,
                        R.drawable.share_local_save,
                        context.getString(R.string.lbl_share_album)
                    )
            );

            RecyclerView rvShareTargetList = mView.findViewById(R.id.rvShareTargetList);
            rvShareTargetList.setLayoutManager(new LinearLayoutManager(mView.getContext(), RecyclerView.HORIZONTAL, false));
            ShareAdapter adapter = new ShareAdapter(data);
            rvShareTargetList.setAdapter(adapter);

            dialog = new Dialog(context, R.style.dialog2);
            dialog.setCancelable(true);
            dialog.setContentView(mView);
            dialog.setCanceledOnTouchOutside(true);

            Window window = dialog.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = ScreenWindowManager.widthScreen(context);
            lp.height = ScreenWindowManager.heightScreen(context);
            lp.alpha = 1.0f;
            window.setAttributes(lp);
            dialog.show();
        } else {
            if (!isShowing()) {
                dialog.show();
            }
        }
    }

    public boolean isShowing() {
        if (dialog != null) {
            return dialog.isShowing();
        }
        return false;
    }


    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            mView=null;
            dialog=null;
        }
    }

    public interface ConfirmCallBack {
        void onCancel();
        void onShareTarget(ShareTarget shareTarget);
    }

    public class ShareAdapter extends RecyclerView.Adapter<ShareViewHolder> {

        List<ShareTarget> data;

        public ShareAdapter(List<ShareTarget> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public ShareViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_share_target, parent, false);
            return new ShareViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ShareViewHolder holder, int position) {
            holder.tvShareTarget.setText(data.get(position).name);
            Drawable drawable = holder.itemView.getResources().getDrawable(data.get(position).iconResId);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            holder.tvShareTarget.setCompoundDrawables(null, drawable, null, null);
            holder.itemView.setOnClickListener(v -> {
                if (confirmCallBack != null) {
                    dismiss();
                    confirmCallBack.onShareTarget(data.get(position));
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    public class ShareViewHolder extends RecyclerView.ViewHolder {
        private TextView tvShareTarget;
        public ShareViewHolder(@NonNull View itemView) {
            super(itemView);
            tvShareTarget = itemView.findViewById(R.id.tvShareTarget);
        }
    }

    public class ShareTarget {
        private int type;
        private int iconResId;
        private String name;

        public ShareTarget(int type, int iconResId, String name) {
            this.type = type;
            this.iconResId = iconResId;
            this.name = name;
        }

        public int getType() {
            return type;
        }

        public int getIconResId() {
            return iconResId;
        }

        public String getName() {
            return name;
        }

    }

}
