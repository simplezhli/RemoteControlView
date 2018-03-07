package com.weilu.customview.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * @Description: 可以自定义你想要的阴影样式
 * @Author: weilu
 * @Time: 2018/2/24 17:49.
 */

public class MyDragShadowBuilder extends View.DragShadowBuilder {

    /**
     * 拖动的阴影图像
     */
    private static Drawable shadow;
    
    private int width, height;

    public MyDragShadowBuilder(View v) {
        // 保存传给myDragShadowBuilder的View参数
        super(v);
        // 将view转为Drawable
        v.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(v.getDrawingCache(true));
        shadow = new BitmapDrawable(null, bitmap);
        v.destroyDrawingCache();
        v.setDrawingCacheEnabled(false);
    }

    /**
     * 用于设置拖动阴影的大小和触摸点位置
     * @param size
     * @param touch
     */
    @Override
    public void onProvideShadowMetrics(Point size, Point touch){
        
        width = getView().getWidth();
        height = getView().getHeight();

        // 设置阴影大小
        shadow.setBounds(0, 0, width, height);
        // 设置长宽值，通过size参数返回给系统。
        size.set(width, height);

        // 把触摸点的位置设为拖动阴影的中心
        touch.set(width / 2, height / 2);
    }

    /**
     * 绘制拖动阴影
     * @param canvas
     */
    @Override
    public void onDrawShadow(Canvas canvas) {
        // 在系统传入的Canvas上绘制Drawable
        shadow.draw(canvas);
    }
}
