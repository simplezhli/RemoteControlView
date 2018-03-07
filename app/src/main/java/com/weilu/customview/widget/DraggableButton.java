package com.weilu.customview.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.util.AttributeSet;

/**
 * @Description:自定义按钮
 * @Author: weilu
 * @Time: 2018/2/23 11:48.
 */
public class DraggableButton extends AppCompatImageView{
    
    private Paint mPaint;
    private Rect mRect = new Rect();
    private String text;
    
    public DraggableButton(Context context) {
        this(context, null);
    }

    public DraggableButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DraggableButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth(), height = getMeasuredHeight();
        int size = Math.min(width, height);
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int radius = getWidth() / 2;
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.parseColor("#cdcdcd"));
        canvas.drawCircle(radius, radius , radius - 2, mPaint);
        // 按下时有背景变化
        if (isPressed()){
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(Color.parseColor("#20000000"));
            canvas.drawCircle(radius, radius , radius - 4, mPaint);
        }
        
        if (!TextUtils.isEmpty(text)){
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(Color.WHITE);
            mPaint.setTextSize(radius / 2);
            mPaint.getTextBounds(text, 0, text.length(), mRect);

            int textHeight = mRect.bottom - mRect.top;
            int textWidth = mRect.right - mRect.left;
            canvas.drawText(text, radius - textWidth / 2, radius + textHeight / 2, mPaint);
        }
        
        super.onDraw(canvas);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        // View的状态有发生改变的触发
        invalidate();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
