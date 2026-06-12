package com.cloud.runball.widget;

import android.content.Context;
import android.graphics.Bitmap;

import com.cloud.runball.basecomm.utils.AppUtils;
import com.squareup.picasso.Transformation;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.widget
 * @ClassName: CircleTransform2
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/4/17 11:26
 * @UpdateUser: zhd
 * @UpdateDate: 2021/4/17 11:26
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class CircleTransform2 implements Transformation {

    Context mContext;
    int width;
    int height;
    public CircleTransform2(Context context,int width,int height){
      this.mContext=context;
      this.width=width;
      this.height=height;
    }

    /**
     * @param source :还未处理的矩形的Bitmap对象
     * @return ：返回的是处理后的圆形Bitmap对象
     */
    @Override
    public Bitmap transform(Bitmap source) {
        //1.压缩处理
        Bitmap zoomBitmp = BitmapUtils.zoom(source, AppUtils.dip2px(mContext,width), AppUtils.dip2px(mContext,height));
        //2.圆形处理
        Bitmap bitmap = BitmapUtils.circleBitmap(zoomBitmp);
        //必须要回收source，否则会报错
        source.recycle();
        return bitmap;//返回圆形的Bitmap对象
    }

    /**
     * 该方法没有什么实际意义，但是要保证其返回的值不能为null！
     * @return
     */
    @Override
    public String key() {
        return "";
    }
}
