package net.lzzy.water.activites;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.view.Window;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import net.lzzy.water.R;
import net.lzzy.water.constants.ApiConstants;
import net.lzzy.water.models.Category;
import net.lzzy.water.network.CategoryService;
import net.lzzy.water.utils.AbstractStaticHandler;
import net.lzzy.water.utils.AppUtils;
import net.lzzy.water.utils.ViewUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author 菜鸡
 */
public class SplashActivity extends AppCompatActivity {

    private static final int WHAT_COUNTING = 1;
    private static final int WHAT_EXCEPTION = 2;
    private static final int WHAT_COUNT_DONE = 3;
    private static final int WHAT_CATEGORY = 4;
    public static final int WHAT_SERVER_OFF = 6;
    private static final int WHAT_C_EXCEPTION = 5;
    public static final String RESULT_CATEGORIES = "categories";
    private int seconds = 5;
    private TextView tvCount;
    private boolean isServerOn = true;
    private List<Category> categories =new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        AppUtils.addActivity(this);
        intiView();
        //executor.execute(this::detectServerStatus);
        executor.execute(this::couDown);
    }
    private ThreadPoolExecutor executor = AppUtils.getExecutor();

    private SplashHandler handler = new SplashHandler(this);
    private static class SplashHandler extends AbstractStaticHandler<SplashActivity> {

        private SplashHandler(SplashActivity context) {
            super(context);
        }
        @Override
        public void handleMessage(Message msg, SplashActivity activity) {

            switch (msg.what){
                case WHAT_COUNTING:
                    String display = msg.obj + "";
                    activity.tvCount.setText(display);
                    break;
                case WHAT_EXCEPTION:
                    new AlertDialog.Builder(activity)
                            .setMessage(msg.obj.toString())
                            .setPositiveButton("继续", (dialog, which) -> activity.gotoMain())
                            .setNegativeButton("退出", (dialog, which) -> AppUtils.exit())
                            .show();
                    break;
                case WHAT_COUNT_DONE:
                    if (activity.isServerOn) {
                        activity.gotoMain();
                    }
                    break;
                case WHAT_SERVER_OFF:
                    //region 处理消息
                    Activity context = AppUtils.getRunningActivity();
                    new AlertDialog.Builder(context)
                            .setMessage("服务器没有响应，是否继续\n" + msg.obj)
                            .setNegativeButton("退出", (dialog, which) -> AppUtils.exit())
                            .setNeutralButton("设置", (dialog, which) -> ViewUtils.goSetting(context))
                            .show();
                    break;
                    default:
                        break;
            }
        }
    }
    private void intiView() {
        tvCount = findViewById(R.id.activity_splash_tv_count);
        tvCount.setOnClickListener(v -> {
            seconds = 0;
        });
    }
    private void couDown() {
        while (seconds >= 0) {
            handler.sendMessage(handler.obtainMessage(WHAT_COUNTING, seconds));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                handler.sendMessage(handler.obtainMessage(WHAT_EXCEPTION, e.getMessage()));
            }
            seconds--;
        }
        handler.sendEmptyMessage(WHAT_COUNT_DONE);
    }
    public void gotoMain() {
        Intent intent=new Intent(this, MainActivity.class);
        intent.putParcelableArrayListExtra(RESULT_CATEGORIES, (ArrayList<? extends Parcelable>) categories);
        startActivity(intent);
        finish();
    }
    //region 探测服务器状态

    private void detectServerStatus() {
        try {
            AppUtils.tryConnectServer(ApiConstants.URL_NET_API);
        } catch (IOException e) {
            isServerOn = false;
            handler.sendMessage(handler.obtainMessage(WHAT_SERVER_OFF, e.getMessage()));
        }
    }
    //endregion
    //region 集中处理activity

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtils.removeActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppUtils.setRunning(getLocalClassName());
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppUtils.setStopped(getLocalClassName());
    }
    //endregion
    @Override
    public void onBackPressed() {
        new android.app.AlertDialog.Builder(this)
                .setMessage("退出应用吗？")
                .setPositiveButton("退出",(dialog, which) -> AppUtils.exit())
                .show();
    }
}
