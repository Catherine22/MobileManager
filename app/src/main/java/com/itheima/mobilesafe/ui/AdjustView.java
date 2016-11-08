package com.itheima.mobilesafe.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;

import com.itheima.mobilesafe.R;

/**
 * Created by Catherine on 2016/9/21.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class AdjustView {
    /**
     * Draw an image in circular shape
     *
     * @param images
     * @param showShadow
     * @return
     */
    public static Bitmap drawCircularImage(Bitmap images, boolean showShadow) {
        Bitmap circleBitmap = Bitmap.createBitmap(images.getWidth(), images.getHeight(), Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        Canvas c = new Canvas(circleBitmap);
        //圆的外圈
        int strokeWidth = images.getWidth() / 20;
        //半径
        int radius = images.getWidth() / 2 - strokeWidth;

        if (showShadow) {
            //第一层外圈,阴影
            paint.setColor(Color.GRAY);
            paint.setAntiAlias(true);//抗锯齿
            paint.setStrokeWidth(strokeWidth);
            c.drawCircle(images.getWidth() / 2 + strokeWidth / 2, images.getHeight() / 2 + strokeWidth / 2, radius + strokeWidth / 2, paint);
        }
        //第二层外圈,圆的外围线
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);//抗锯齿
        paint.setStrokeWidth(strokeWidth / 2);
        c.drawCircle(images.getWidth() / 2, images.getHeight() / 2, radius + strokeWidth / 2, paint);

        //最内层的图片
        BitmapShader shader = new BitmapShader(images, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setAntiAlias(true);
        paint.setShader(shader);
        //This will draw the image.
        c.drawCircle(images.getWidth() / 2, images.getHeight() / 2, radius, paint);
        return circleBitmap;
    }

    public static Drawable getDrawable(Context ctx, int id) {
        Drawable drawable = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = ctx.getResources().getDrawable(id, null);
        } else
            drawable = ctx.getResources().getDrawable(id);

        return drawable;
    }
}
