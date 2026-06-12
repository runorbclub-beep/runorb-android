package com.cloud.runball.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import com.cloud.runball.basecomm.utils.AppUtils;
import com.squareup.picasso.Transformation;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.widget
 * @ClassName: RoundTransform
 * @Description: 实现圆角图片
 * @Author: zhd
 * @CreateDate: 2021/3/2 17:21
 * @UpdateUser: zhd
 * @UpdateDate: 2021/3/2 17:21
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class RoundTransform implements Transformation {
    private Context mContext;

    public RoundTransform(Context context) {
        mContext = context;
    }

    @Override
    public Bitmap transform(Bitmap source) {

        int widthLight = source.getWidth();
        int heightLight = source.getHeight();
        int radius = AppUtils.dip2px(mContext, 8); // 圆角半径

        Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);
        Paint paintColor = new Paint();
        paintColor.setFlags(Paint.ANTI_ALIAS_FLAG);

        RectF rectF = new RectF(new Rect(0, 0, widthLight, heightLight));

        canvas.drawRoundRect(rectF, radius, radius, paintColor);
//        canvas.drawRoundRect(rectF, widthLight / 5, heightLight / 5, paintColor);

        Paint paintImage = new Paint();
        paintImage.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        canvas.drawBitmap(source, 0, 0, paintImage);
        source.recycle();
        return output;
    }

    @Override
    public String key() {
        return "roundcorner";
    }
}
