package com.cloud.runball.module.mine;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cloud.runball.R;
import com.cloud.runball.bean.MedalInfo;
import com.cloud.runball.utils.Constant;
import com.squareup.picasso.Picasso;
import java.util.Locale;

/**
 * @author ns467
 */
public class BadgeActivity extends Activity implements View.OnClickListener {

    ImageView ivClose;
    ImageView ivBadge;
    TextView tvBadgeName;
    TextView tvBadgeDesc;


    //TextView tv_douyin;
    //TextView tv_qq;
    //TextView tv_qqzone;
    //TextView tv_wechat;
    //TextView tv_wechat_circle;
    MedalInfo data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hideSystemBar();
        setContentView(R.layout.layout_badge);

        tvBadgeName=this.findViewById(R.id.tvBadgeName);
        tvBadgeDesc=this.findViewById(R.id.tvBadgeDesc);
        ivBadge=this.findViewById(R.id.ivBadge);
        ivClose=this.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(this);

        data = (MedalInfo) this.getIntent().getSerializableExtra("data");
        if (data != null) {
            if (data.getMedal_image_active().startsWith("http")) {
                Picasso.with(this)
                        .load(data.getMedal_image_active())
                        .into(ivBadge);
            } else {
                Picasso.with(this)
                        .load(Constant.getBaseUrl() + "/" + data.getMedal_image_active())
                        .into(ivBadge);
            }


            String medalname=data.getUser_medal_name_cn();
            String medaldesc=data.getDescription_cn();
            Locale locale = getResources().getConfiguration().locale;
            String language = locale.getLanguage();
            if(language.startsWith("zh")){
                medalname=data.getUser_medal_name_cn();
                medaldesc=data.getDescription_cn();
            }else{
                medalname=data.getUser_medal_name_en();
                medaldesc=data.getDescription_en();
            }

            tvBadgeName.setText(medalname);
            tvBadgeDesc.setText(medaldesc);

        }
    }

    private void hideSystemBar() {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            int uiOptions =
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivClose) {
            finish();
        }
    }
    /**
     * public void qq(View view) {
     * new ShareAction(this)
     * .setPlatform(SHARE_MEDIA.QQ)
     * .withText("hello").withMedia()
     * .share();
     * <p>
     * ShareUtils.shareWeb(this, Defaultcontent.url, Defaultcontent.title
     * , Defaultcontent.text, Defaultcontent.imageurl, R.mipmap.icon_logo_share, SHARE_MEDIA.QQ
     * );
     * }
     * <p>
     * public void weiXin(View view) {
     * ShareUtils.shareWeb(this, Defaultcontent.url, Defaultcontent.title
     * , Defaultcontent.text, Defaultcontent.imageurl, R.mipmap.icon_logo_share, SHARE_MEDIA.WEIXIN
     * );
     * }
     * <p>
     * public void weixinCircle(View view) {
     * ShareUtils.shareWeb(this, Defaultcontent.url, Defaultcontent.title
     * , Defaultcontent.text, Defaultcontent.imageurl, R.mipmap.icon_logo_share, SHARE_MEDIA.WEIXIN_CIRCLE
     * );
     * }
     * <p>
     * public void sina(View view) {
     * ShareUtils.shareWeb(this, Defaultcontent.url, Defaultcontent.title
     * , Defaultcontent.text, Defaultcontent.imageurl, R.mipmap.icon_logo_share, SHARE_MEDIA.SINA
     * );
     * }
     * <p>
     * public void Qzone(View view) {
     * ShareUtils.shareWeb(this, Defaultcontent.url, Defaultcontent.title
     * , Defaultcontent.text, Defaultcontent.imageurl, R.mipmap.icon_logo_share, SHARE_MEDIA.QZONE
     * );
     * }
     **/




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //QQ与新浪微博的回调
        //UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
