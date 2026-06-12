package com.cloud.runball.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.cloud.runball.basecomm.utils.AppUtils;
import com.squareup.picasso.Transformation;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.widget
 * @ClassName: CircleTransform
 * @Description: 实现圆形图片
 * @Author: zhd
 * @CreateDate: 2021/3/2 17:15
 * @UpdateUser: zhd
 * @UpdateDate: 2021/3/2 17:15
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class CircleTransform implements Transformation {
    private Context mContext;
    private int radius;
    private int radiusX=0;
    private int radiusY=0;
    public CircleTransform(Context context) {
        this.mContext = context;
        this.radius = AppUtils.dip2px(mContext, 10);
        this.radiusX=0;
        this.radiusY=0;
    }

    public CircleTransform(Context context,int radiusX,int radiusY) {
        this.mContext = context;
        this.radius = AppUtils.dip2px(mContext, 10);
        this.radiusX=radiusX;
        this.radiusY=radiusY;
    }

    @Override
    public Bitmap transform(Bitmap source) {

        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
        BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);

        float r = size / 2f;
//        float r = radius;
        if(radiusX!=0 && radiusY!=0){
            RectF rectF = new RectF(0, 0, size, size);
            canvas.drawRoundRect(rectF, radiusX, radiusY, paint);
        }else{
            canvas.drawCircle(r, r, r, paint);
        }

        squaredBitmap.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return "circle";
    }
}
