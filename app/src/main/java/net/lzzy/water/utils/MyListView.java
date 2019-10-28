package net.lzzy.water.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * @author 菜鸡
 */
public class MyListView extends ListView {
    public MyListView(Context context) {
        super(context);
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    /**
     *重写该方法，达到使ListView适应ScrollView的效果
          */
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mExpandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, mExpandSpec);
    }


            //其中  android:fillViewport="true" 让listview 填充屏幕
            //而下面这3行，是防止自动跳转到listview的第一行
            //android:focusableInTouchMode="true"
            //android:focusable="true"
            //android:descendantFocusability="beforeDescendants"
}
