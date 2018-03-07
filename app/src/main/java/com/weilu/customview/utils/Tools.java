package com.weilu.customview.utils;

import android.content.ClipData;
import android.content.Intent;
import android.os.Build;
import android.view.HapticFeedbackConstants;
import android.view.View;

import com.weilu.customview.bean.DraggableInfo;

/**
 * @Description:
 * @Author: weilu
 * @Time: 2018/2/26 13:42.
 */

public class Tools {
    
    public final static int ONE_BY_ONE_PIC = 0;
    public final static int ONE_BY_ONE_TEXT = 1;
    public final static int THREE_BY_THREE_PIC = 2;
    public final static int ONE_BY_TWO_PIC = 3;
    
    public static void startDrag(View view){
        DraggableInfo tag = (DraggableInfo) view.getTag();
        if (tag == null){
            tag = new DraggableInfo("Text", 0, 0, 1);
        }
        Intent intent = new Intent();
        intent.putExtra("data", tag);
        ClipData dragData = ClipData.newIntent("value", intent);
        View.DragShadowBuilder myShadow = new View.DragShadowBuilder(view);
        // 震动反馈，不需要震动权限
        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            view.startDragAndDrop(dragData, myShadow, null, 0);
        }else{
            view.startDrag(dragData, myShadow, null, 0);
        }
    }
}
