package net.lzzy.water.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.WindowManager;

import java.util.Objects;

/**
 * @author 菜鸡
 */
public class MyDialog extends Dialog {

    //Context mContext;

    public MyDialog(Context context) {
        super(context);
        Objects.requireNonNull(getWindow()).requestFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onStart() {
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(0xffffffff));
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }
}