package net.lzzy.water.activites;


import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.view.Window;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import net.lzzy.water.R;
import net.lzzy.water.models.Category;
import net.lzzy.water.network.CategoryService;
import net.lzzy.water.utils.AbstractStaticHandler;
import net.lzzy.water.utils.AppUtils;
import net.lzzy.water.utils.StatusBarUtil;

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
    private static final int WHAT_C_EXCEPTION = 5;
    public static final String RESULT_CATEGORIES = "categories";
    private int seconds = 2;
    private TextView tvCount;
    private boolean isServerOn = true;
    private List<Category> categories =new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setStatusBar();
        setContentView(R.layout.activity_splash);
        intiView();
        executor.execute(this::couDown);
        executor.execute(this::getCategory);
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
                case WHAT_CATEGORY:
                    String json = String.valueOf(msg.obj);
                    try {
                        activity.categories = CategoryService.getCategories(json);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case WHAT_C_EXCEPTION:

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
    public  void  getCategory(){
       try {
                String json = CategoryService.getCategoryFromServer();
                handler.sendMessage(handler.obtainMessage(WHAT_CATEGORY, json));
       } catch (IOException e) {
                handler.sendMessage(handler.obtainMessage(WHAT_C_EXCEPTION, e.getMessage()));
       }
    }

    protected void setStatusBar() {
        //这里做了两件事情，1.使状态栏透明并使contentView填充到状态栏 2.预留出状态栏的位置，防止界面上的控件离顶部靠的太近。这样就可以实现开头说的第二种情况的沉浸式状态栏了
        StatusBarUtil.setTransparent(this);
    }

}
