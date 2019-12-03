package net.lzzy.water.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import net.lzzy.water.R;
import net.lzzy.water.activites.MainActivity;
import net.lzzy.water.activites.SplashActivity;
import net.lzzy.water.frament.OneFragment;


/**
 * @author lzzy_gxy
 * @date 2019/4/15
 * Description:
 */
public class ViewUtils {

    public static final String MM = "mm";

    public static void goSetting(Context context, boolean e) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_setting, null);
        Pair<String, String> url = UserCookies.getInstance().loadServerSetting();
        EditText edtIp = view.findViewById(R.id.dialog_setting_edt_ip);
        edtIp.setText(url.first);
        EditText edtPort = view.findViewById(R.id.dialog_setting_edt_);
        edtPort.setText(url.second);
        new AlertDialog.Builder(context)
                .setView(view)
                .setNegativeButton("取消", (dialog, which) -> {
                })
                .setPositiveButton("保存", (dialog, which) -> {
                    String ip = edtIp.getText().toString();
                    String port = edtPort.getText().toString();
                    if (TextUtils.isEmpty(ip) || TextUtils.isEmpty(port)) {
                        Toast.makeText(context, "信息不完整", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    UserCookies.getInstance().saveServerSetting(ip, port);
                    Intent intent = new Intent(MM);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                        gotoMain(context);
                })
                .setCancelable(false)
                .show();
    }

    private static void gotoMain(Context context) {
        if (context instanceof SplashActivity) {
            ((SplashActivity) context).gotoMain();
        }
    }

    public abstract static class AbstractTouchLisener implements View.OnTouchListener {
        @SuppressWarnings("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return handleTouch(event);
        }

        /**
         * 触摸
         * @param event id
         * @return e
         */
        public abstract boolean handleTouch(MotionEvent event);
    }

    public abstract static class AbstractQueryListener implements SearchView.OnQueryTextListener {
        //搜索网络请求

        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            handleQuery(newText);
            return true;
        }

        /**
         * 处理搜索逻辑
         *
         * @param kw 搜索关键字
         */
        protected abstract void handleQuery(String kw);
    }

    //通用显示进度条

 /*   private static AlertDialog dialog;
    public static void showProgress(Context context, String message){
        if (dialog==null){
            View view= LayoutInflater.from(context).inflate(R.layout.dialog_practice,null);
            TextView tv=view.findViewById(R.id.dialog_practices_tv);
            tv.setText(message);
            dialog=new AlertDialog.Builder(context).create();
            dialog.setView(view);
        }
        dialog.show();
    }
    public static void dismissPragress(){
        if (dialog!=null&&dialog.isShowing()){
            dialog.dismiss();
        }
    }*/

    public static int px2dp(int pxValue, Context context) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dp2px(int dpValue, Context context) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
