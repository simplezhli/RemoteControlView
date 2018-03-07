package com.weilu.customview.widget;

import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.weilu.customview.bean.DraggableInfo;
import com.weilu.customview.utils.Tools;

import java.util.ArrayList;
import java.util.List;

import static com.weilu.customview.utils.Tools.ONE_BY_ONE_PIC;
import static com.weilu.customview.utils.Tools.ONE_BY_ONE_TEXT;
import static com.weilu.customview.utils.Tools.ONE_BY_TWO_PIC;
import static com.weilu.customview.utils.Tools.THREE_BY_THREE_PIC;

/**
 * @Description: 自定义遥控器
 * @Author: weilu
 * @Time: 2018/2/23 09:59.
 */
public class RemoteControlView extends FrameLayout implements View.OnDragListener{
    
    private final static String TAG = "RemoteControlView";
    
    /**
     * 绘制手机画笔
     */
    private Paint mPhonePaint;
    
    /**
     * 手机返回按键path
     */
    private Path mBackPath;

    /**
     * 手机宽度
     */
    private int mPhoneWidth;
    
    /**
     * 手机内容区域高
     */
    private int mPhoneContentHeight;

    /**
     * 手机内容区域宽
     */
    private int mPhoneContentWidth;
    
    /**
     * 手机右上角x轴点
     */
    private int startX;

    /**
     * 存放按钮位置
     */
    private List<Rect> mRectList = new ArrayList<>();

    /**
     * 内部拖拽的View
     */
    private View dragView;

    /**
     * 内部拖拽View的位置
     */
    private Rect dragRect;

    /**
     * 内部拖拽是否出界
     */
    private boolean isOut;

    /**
     * 提示投影的位置
     */
    private Rect shadowRect;

    /**
     * 拖拽按钮的信息
     */
    private DraggableInfo info;

    /**
     * 文字Rect
     */
    Rect mTextRect = new Rect();

    /**
     * 投影图片的Bitmap
     */
    private Bitmap shadowBitmap;

    /**
     * 临时Rect
     */
    private Rect mRect = new Rect();
    
    private FrameLayout frameLayout;
    private TextView mTextView;
    private final static int WIDTH_COUNT = 4;
    private final static int HEIGHT_COUNT = 7;
    private final static String BORDER_COLOR = "#70FFFFFF";
    private final static String SOLID_COLOR = "#30FFFFFF";
    private final static String DASHED_COLOR = "#20FFFFFF";
    private final static String CONTENT_COLOR = "#0E000000";
    private DashPathEffect mDashPathEffect = new DashPathEffect(new float[] {10, 10}, 0);
    
    public RemoteControlView(Context context) {
        this(context, null);
    }

    public RemoteControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RemoteControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setWillNotDraw(false);
        mPhonePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackPath = new Path();
        // 不使用硬件加速，否则虚线显示不出
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        // 拖拽有效区域
        frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(Color.parseColor(CONTENT_COLOR));
        frameLayout.setOnDragListener(this);
        addView(frameLayout);
        // 提示文字
        mTextView = new TextView(context);
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        mTextView.setTextColor(Color.WHITE);
        mTextView.setText("长按并拖拽下方按钮到这里");
        LayoutParams fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        fl.gravity = Gravity.CENTER;
        mTextView.setLayoutParams(fl);
        mTextView.measure(0, 0);
        addView(mTextView);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec), measure(heightMeasureSpec));
        // 手机高度为View高度减去上下间隔24dp
        int phoneHeight = getMeasuredHeight() - dp2px(24);
        // 手机内容区域高 ：手机高度 - 手机头尾（48dp）- 手机屏幕间距（5dp） * 2）
        mPhoneContentHeight = phoneHeight - dp2px(58);
        // 手机内容区域宽 ：手机内容区域高/ 7 * 4（手机内容区域为4：7）
        mPhoneContentWidth = mPhoneContentHeight / HEIGHT_COUNT * WIDTH_COUNT;
        // 手机宽度为手机内容区域宽 + 手机屏幕间距 * 2
        mPhoneWidth = mPhoneContentWidth + dp2px(10);
        // 绘制起始点
        startX = (getMeasuredWidth() - mPhoneWidth) / 2;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        frameLayout.layout(startX, dp2px(36), getMeasuredWidth() - startX, getMeasuredHeight() - dp2px(36));
        if(frameLayout.getChildCount() > 0){
            for (int i = 0; i < frameLayout.getChildCount(); i++){
                Rect rect = mRectList.get(i);
                frameLayout.getChildAt(i).layout(rect.left, rect.top, rect.right, rect.bottom);
            }
        }
    }

    private int measure(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.AT_MOST:  
                // 子容器可以是声明大小内的任意大小
                result = specSize;
                break;
            case MeasureSpec.EXACTLY: 
                // 父容器已经为子容器设置了尺寸,子容器应当服从这些边界,不论子容器想要多大的空间
                result = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:  
                // 父容器对于子容器没有任何限制,子容器想要多大就多大. 所以完全取决于子view的大小
                result = dp2px(300);
                break;
            default:
                break;
        }
        return result;
    }
    
    private RectF mRectF = new RectF();
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPhonePaint.setColor(Color.parseColor(BORDER_COLOR));
        mPhonePaint.setStyle(Paint.Style.STROKE);
        mPhonePaint.setStrokeWidth(2);
        int i = dp2px(12);
        // 绘制手机外壳
        mRectF.left = startX;
        mRectF.right = getMeasuredWidth() - startX;
        mRectF.top = i;
        mRectF.bottom = getMeasuredHeight() - i;
        canvas.drawRoundRect(mRectF, i, i, mPhonePaint);
        // 绘制手机上下两条线
        canvas.drawLine(startX, i * 3, getMeasuredWidth() - startX, i * 3, mPhonePaint);
        canvas.drawLine(startX, getMeasuredHeight() - i * 3, getMeasuredWidth() - startX, getMeasuredHeight() - i * 3, mPhonePaint);
        // 绘制手机上方听筒、摄像头
        mRectF.left = getMeasuredWidth() / 2 - dp2px(25);
        mRectF.right = getMeasuredWidth() / 2 + dp2px(25);
        mRectF.top = dp2px(22);
        mRectF.bottom = dp2px(26);
        canvas.drawRoundRect(mRectF, dp2px(2), dp2px(2), mPhonePaint);
        canvas.drawCircle(getMeasuredWidth() / 2 - dp2px(40), i * 2, i / 3, mPhonePaint);
        canvas.drawCircle(getMeasuredWidth() / 2 + dp2px(40), i * 2, i / 3, mPhonePaint);
        // 绘制手机下方按键
        canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() - i * 2, i / 2, mPhonePaint);
        canvas.drawRect(startX + mPhoneWidth / 5, getMeasuredHeight() - dp2px(29), startX + mPhoneWidth / 5 + dp2px(10), getMeasuredHeight() - dp2px(19), mPhonePaint);
        mBackPath.moveTo(getMeasuredWidth() - startX - mPhoneWidth / 5, getMeasuredHeight() - dp2px(30));
        mBackPath.lineTo(getMeasuredWidth() - startX - mPhoneWidth / 5 - dp2px(10), getMeasuredHeight() - dp2px(24));
        mBackPath.lineTo(getMeasuredWidth() - startX - mPhoneWidth / 5, getMeasuredHeight() - dp2px(18));
        mBackPath.close();
        canvas.drawPath(mBackPath, mPhonePaint);

        // 绘制网格（4 * 7的田字格）田字格外框为实线，内侧为虚线
        // 手机屏幕间距5pd
        int j = dp2px(5);
        // 格子的宽高
        int size = mPhoneContentHeight / HEIGHT_COUNT;

        // 横线
        for (int z = 0; z <= HEIGHT_COUNT; z++){
            mPhonePaint.setPathEffect(null);
            mPhonePaint.setColor(Color.parseColor(SOLID_COLOR));
            mPhonePaint.setStrokeWidth(1);
            // 实线
            canvas.drawLine(startX + j, dp2px(41) + z * size,
                    getMeasuredWidth() - startX - j, dp2px(41) + z * size, mPhonePaint);
            // 虚线
            if (z != HEIGHT_COUNT){
                mPhonePaint.setPathEffect(mDashPathEffect);
                mPhonePaint.setColor(Color.parseColor(DASHED_COLOR));
                canvas.drawLine(startX + j, dp2px(41) + z * size + size / 2,
                        getMeasuredWidth() - startX - j, dp2px(41) + z * size + size / 2, mPhonePaint);
            }
        }

        // 竖线
        for (int z = 0; z <= WIDTH_COUNT; z++){
            mPhonePaint.setPathEffect(null);
            mPhonePaint.setColor(Color.parseColor(SOLID_COLOR));
            mPhonePaint.setStrokeWidth(1);
            // 实线
            canvas.drawLine(startX + j + z * size, dp2px(41),
                    startX + j + z * size, getMeasuredHeight() - dp2px(41), mPhonePaint);
            // 虚线
            if (z != WIDTH_COUNT){
                mPhonePaint.setPathEffect(mDashPathEffect);
                mPhonePaint.setColor(Color.parseColor(DASHED_COLOR));
                canvas.drawLine(startX + j + z * size + size / 2, dp2px(41),
                        startX + j + z * size + size / 2, getMeasuredHeight() - dp2px(41), mPhonePaint);
            }
        }
       
        if (shadowRect != null){
            int type = info.getType();
            mPhonePaint.setStyle(Paint.Style.FILL);
            mPhonePaint.setColor(Color.WHITE);
            shadowRect.left = shadowRect.left + startX;
            shadowRect.right = shadowRect.right + startX;
            shadowRect.top = shadowRect.top + dp2px(36);
            shadowRect.bottom = shadowRect.bottom + dp2px(36);
            
            if (type == ONE_BY_ONE_TEXT){
                int width = shadowRect.right - shadowRect.left;
                String text = info.getText();
                mPhonePaint.setTextSize(width / 4);
                mPhonePaint.getTextBounds(text, 0, text.length(), mTextRect);

                int textHeight = mTextRect.bottom - mTextRect.top;
                int textWidth = mTextRect.right - mTextRect.left;
                canvas.drawText(text, shadowRect.left + width / 2 - textWidth / 2, shadowRect.top + width / 2 + textHeight / 2, mPhonePaint);
            }else {
                if (type == ONE_BY_ONE_PIC){
                    // 1 * 1 方格
                    int padding = dp2px(12);
                    shadowRect.left = shadowRect.left + padding;
                    shadowRect.right = shadowRect.right - padding;
                    shadowRect.top = shadowRect.top + padding;
                    shadowRect.bottom = shadowRect.bottom - padding;
                }else if (type == THREE_BY_THREE_PIC){
                    // 3 * 3 方格
                    int padding = dp2px(10);
                    shadowRect.left = shadowRect.left + padding;
                    shadowRect.right = shadowRect.right - padding;
                    shadowRect.top = shadowRect.top + padding;
                    shadowRect.bottom = shadowRect.bottom -padding;
                }else if (type == ONE_BY_TWO_PIC){
                    int padding = dp2px(4);
                    shadowRect.left = shadowRect.left + padding;
                    shadowRect.right = shadowRect.right - padding;
                }
                canvas.drawBitmap(shadowBitmap, null, shadowRect, mPhonePaint);
            }
        }
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        final int action = event.getAction();
        switch(action) {
            case DragEvent.ACTION_DRAG_STARTED:
                // 判断是否是需要接收的数据
                if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_INTENT)) {
                    Log.e(TAG, "开始拖动");
                }else {
                    return false;
                }
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                Log.e(TAG, "进入");
                mTextView.setVisibility(GONE);
                isOut = false;
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                Log.e(TAG, "移出");
                if (frameLayout.getChildCount() == 0){
                    mTextView.setVisibility(VISIBLE);
                }
                isOut = true;
                shadowRect = null;
                invalidate();
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                Log.e(TAG, "停止拖动");
                if (dragView != null && isOut){
                    mRectList.remove(dragRect);
                    frameLayout.removeView(dragView);
                }
                if (frameLayout.getChildCount() == 0){
                    mTextView.setVisibility(VISIBLE);
                }
                dragView = null;
                dragRect = null;
                stopDrag();
                break;
            case DragEvent.ACTION_DRAG_LOCATION:
                // 停留
                if (dragView != null){
                    info = (DraggableInfo) dragView.getTag();
                }
                if (info == null){
                    break;
                }
                compute(info.getType(), mRect, event);
                adjust(info.getType(), mRect, event);
                if (isEffectiveArea(mRect) && !isOverlap(mRect)){
                    shadowRect = mRect;
                }else {
                    shadowRect = null;
                }
                try {
                    Thread.sleep(33);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                invalidate();
                break;
            case DragEvent.ACTION_DROP:
                Log.e(TAG, "释放拖动");
                if (dragView == null){
                    final DraggableInfo data = (DraggableInfo) event.getClipData().getItemAt(0).getIntent().getSerializableExtra("data");
                    if (data != null){
                        int size = mPhoneContentWidth / WIDTH_COUNT - dp2px(10);
                        int padding = size / 4;

                        final ImageView imageView;

                        if (data.getType() == ONE_BY_TWO_PIC){
                            imageView = new ImageView(getContext());
                            imageView.setImageResource(data.getPic());
                        }else {
                            imageView = new DraggableButton(getContext());
                            imageView.setPadding(padding, padding, padding, padding);
                            if (data.getType() == ONE_BY_ONE_TEXT){
                                ((DraggableButton)imageView).setText(data.getText());
                            }else {
                                imageView.setImageResource(data.getPic());
                            }
                        }
                        imageView.setTag(data);
                        final Rect rect = new Rect();
                        imageView.setOnLongClickListener(new OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                Tools.startDrag(imageView);
                                dragView = imageView;
                                dragRect = rect;
                                setDragInfo(data);
                                imageView.setVisibility(GONE);
                                return false;
                            }
                        });

                        compute(data.getType(), rect, event);
                        adjust(data.getType(), rect, event);

                        if (isEffectiveArea(rect) && !isOverlap(rect)){
                            mRectList.add(rect);
                            frameLayout.addView(imageView);
                        }
                    }
                }else {
                    DraggableInfo data = (DraggableInfo) dragView.getTag();
                    Rect rect = dragRect;
                    compute(data.getType(), rect, event);
                    adjust(data.getType(), rect, event);

                    if (isEffectiveArea(rect) && !isOverlap(rect)){
                        dragView.setVisibility(VISIBLE);
                    }else {
                        mRectList.remove(dragRect);
                        frameLayout.removeView(dragView);
                    }
                    dragView = null;
                    dragRect = null;
                }
                stopDrag();
                break;
            default:
                return false;
        }    
        return true;
    }

    private void stopDrag(){
        shadowRect = null;
        info = null;
        invalidate();
    }
    
    /**
     * 是否在有效区域
     */
    private boolean isEffectiveArea(Rect rect){
        return rect.left >= 0 && rect.top >= 0 && rect.right >= 0 && rect.bottom >= 0 &&
                rect.right <= frameLayout.getWidth() && rect.bottom <= frameLayout.getHeight();
    }

    /**
     * 计算控件位置
     */
    private void compute(int type, Rect rect, DragEvent event){

        int size = mPhoneContentWidth / WIDTH_COUNT - dp2px(10);
        int x = (int) event.getX();
        int y = (int) event.getY();

        if (type == ONE_BY_ONE_PIC || type == ONE_BY_ONE_TEXT){
            // 1 * 1 方格
            rect.left = x - size / 2;
            rect.top = y - size / 2;
            rect.right = x + size / 2;
            rect.bottom = y + size / 2;
        }else if (type == THREE_BY_THREE_PIC){
            // 3 * 3 方格
            rect.left = x - size * 3 / 2;
            rect.top = y - size * 3 / 2;
            rect.right = x + size * 3 / 2;
            rect.bottom = y + size * 3 / 2;
        }else if (type == ONE_BY_TWO_PIC){
            // 1 * 2 方格
            rect.left = x - size / 2;
            rect.top = y - size;
            rect.right = x + size / 2;
            rect.bottom = y + size;
        }
    }

    /**
     * 调整控件位置
     */
    private void adjust(int type, Rect rect, DragEvent event){
        // 最小单元格宽高
        int size = mPhoneContentWidth / WIDTH_COUNT / 2;
        // 手机屏幕间距
        int padding = dp2px(5);
        // 1 * 1方格宽高
        int width = size * 2 - dp2px(10);

        int offsetX = (rect.left - padding) % size;
        if (offsetX < size / 2){
            rect.left = rect.left + padding - offsetX;
        }else {
            rect.left = rect.left + padding - offsetX + size;
        }

        int offsetY = (rect.top - padding) % size;
        if (offsetY < size / 2){
            rect.top = rect.top + padding - offsetY;
        }else {
            rect.top = rect.top + padding - offsetY + size;
        }
        
        if (type == ONE_BY_ONE_PIC || type == ONE_BY_ONE_TEXT){
            rect.right = rect.left + width;
            rect.bottom = rect.top + width;
            
        }else if (type == ONE_BY_TWO_PIC){
            rect.top = rect.top + padding;
            rect.right = rect.left + width;
            rect.bottom = rect.top + width * 2;
        }else if (type == THREE_BY_THREE_PIC){
            rect.top = rect.top + padding * 2;
            rect.left = rect.left + padding * 2;
            rect.right = rect.left + width * 3;
            rect.bottom = rect.top + width * 3;
        }

        //超出部分修正(超出部分)
        if (rect.right > frameLayout.getWidth() || rect.bottom > frameLayout.getHeight()){

            int currentX = (int) event.getX();
            int currentY = (int) event.getY();

            int centerX = frameLayout.getWidth() / 2;
            int centerY = frameLayout.getHeight() / 2;

            if (currentX <= centerX && currentY <= centerY){
                //左上角区域

            }else if (currentX >= centerX && currentY <= centerY){
                //右上角区域
                rect.left = rect.left - size;
                rect.right = rect.right - size;
            }else if (currentX <= centerX && currentY >= centerY){
                //左下角区域
                rect.top = rect.top - size;
                rect.bottom = rect.bottom - size;
            }else if (currentX >= centerX && currentY >= centerY){
                //右下角区域
                if (rect.right > frameLayout.getWidth()){
                    rect.left = rect.left - size;
                    rect.right = rect.right - size;
                }
                if (rect.bottom > frameLayout.getHeight()){
                    rect.top = rect.top - size;
                    rect.bottom = rect.bottom - size;
                }
            }
        }
    }

    /**
     * 判断是否重叠
     */
    private boolean isOverlap(Rect rect){
        for (Rect mRect : mRectList){
            if (!isEqual(mRect)){
                if (isRectOverlap(mRect, rect)){
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * 判断两Rect是否重叠
     */
    private boolean isRectOverlap(Rect oldRect, Rect newRect) {
        return (oldRect.right > newRect.left &&
                newRect.right  > oldRect.left &&
                oldRect.bottom > newRect.top &&
                newRect.bottom > oldRect.top);
    }

    /**
     * 判断与拖拽的Rect是否相等
     */
    private boolean isEqual(Rect rect) {
        if (dragRect == null){
            return false;
        }
        
        return (rect.left == dragRect.left &&
                rect.right == dragRect.right &&
                rect.top == dragRect.top &&
                rect.bottom == dragRect.bottom);
    }

    /**
     * 设置拖拽按钮信息
     */
    public void setDragInfo(DraggableInfo info){
        this.info = info;
        if (info.getType() != ONE_BY_ONE_TEXT){
            shadowBitmap = BitmapFactory.decodeResource(getResources(), info.getPic());
        }
    }

    private int dp2px(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        float density = displayMetrics.scaledDensity;
        return (int) (dp * density + 0.5f);
    }

}
