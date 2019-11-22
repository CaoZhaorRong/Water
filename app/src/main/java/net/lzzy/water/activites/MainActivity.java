package net.lzzy.water.activites;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import net.lzzy.water.R;
import net.lzzy.water.frament.CartFragment;
import net.lzzy.water.frament.OneFragment;
import net.lzzy.water.frament.OwnFragment;
import net.lzzy.water.models.Category;
import net.lzzy.water.models.Order;
import net.lzzy.water.models.User;
import net.lzzy.water.network.CategoryService;
import net.lzzy.water.network.UserService;
import net.lzzy.water.utils.AbstractStaticHandler;
import net.lzzy.water.utils.AppUtils;
import net.lzzy.water.utils.MyDialog;
import net.lzzy.water.utils.UserCookies;
import net.lzzy.water.utils.ViewUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author 菜鸡
 */
public class MainActivity extends AppCompatActivity implements OneFragment.OnGoToBuyFragment,
        View.OnClickListener , OwnFragment.OnBack , OwnFragment.OnGoToOrderActivity {
    public static final int WHAT_LOGIN = 0;
    public static final int WHAT_L_EXCEPTION = 1;
    private static final int WHAT_CATEGORY = 2;
    public static final String RESULT_ORDER = "result_order";
    public static final String RESULT_INDEX = "result_index";
    private List<Category> categories;
    private ImageView img1, img2, img3;
    private  String telephone;
    private  String password;
    private ViewPager pager;
    private  boolean isAutoLogin = false;
    private  boolean isBack = false;
    private List<Fragment> fragments;
    private int[] tabIds = {R.id.tab1, R.id.tab2, R.id.tab3, 4,5};

    private ThreadPoolExecutor executor = AppUtils.getExecutor();

    private Handler handler = new Handler(this);
    private MyDialog dialog;
    private LinearLayout fundusBtn;



    private static class Handler extends AbstractStaticHandler<MainActivity> {
        private Handler(MainActivity context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg, MainActivity activity) {
            switch (msg.what) {
                case WHAT_LOGIN:
                    String json = String.valueOf(msg.obj);
                    try {
                        User user = UserService.getUser(json);
                        if (user != null) {
                            AppUtils.setUser(user);
                            if (activity.isAutoLogin){
                                Toast.makeText(activity, "登录成功", Toast.LENGTH_SHORT).show();
                                activity.isAutoLogin = false;
                            }else {
                                activity.dialog.dismiss();
                                CartFragment.newInstance();
                                OwnFragment.newInstance();
                                activity.pager.setCurrentItem(2);
                                UserCookies.getInstance().saveUser(activity.telephone,activity.password);
                                Toast.makeText(activity, "登录成功", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(activity, "登录失败，请重新登录！", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case WHAT_L_EXCEPTION:
                    Toast.makeText(activity, "登录失败，请重新登录！", Toast.LENGTH_SHORT).show();
                    break;
                case WHAT_CATEGORY:
                    String category = String.valueOf(msg.obj);
                    try {
                        activity.categories = CategoryService.getCategories(category);
                        if (activity.categories!=null&&activity.categories.size()>0){
                            activity.viewPagers();
                            activity.autoLogin();
                        }else {
                            new AlertDialog.Builder(activity)
                                    .setMessage("服务器没有响应，是否继续\n")
                                    .setNegativeButton("退出", (dialog, which) -> AppUtils.exit())
                                    .setNeutralButton("设置", (dialog, which) -> ViewUtils.goSetting(activity))
                                    .show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        executor.execute(this::getCategory);
        initView();
    }
    public void getCategory(){
        try {
            String json = CategoryService.getCategoryFromServer();
            handler.sendMessage(handler.obtainMessage(WHAT_CATEGORY, json));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//region

    private void autoLogin() {
            User json = new User();
            Pair<String, String> user = UserCookies.getInstance().loadUser();
            if (!"".equals(user.first)&&!"".equals(user.second)){
                json.setTelephone(user.first);
                json.setPassword(user.second);
                login(json);
                isAutoLogin = true;
            }
    }

    private void viewPagers() {
        fragments = new ArrayList<>();
        fragments.add(0,OneFragment.newInstance(categories));
        fragments.add(1,new CartFragment());
        fragments.add(2,new OwnFragment());
        pager = findViewById(R.id.activity_main_pager);
        FragmentStatePagerAdapter fragmentStatePagerAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        };

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }
            @Override
            public void onPageSelected(int i) {
                selectTab(tabIds[i]);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        pager.setAdapter(fragmentStatePagerAdapter);
        pager.setOffscreenPageLimit(3);
        pager.setPageMargin(40);
        fragmentStatePagerAdapter.notifyDataSetChanged();
    }

    private void selectTab(int tabId) {
        switch (tabId) {
            case R.id.tab1:
                img1.setImageResource(R.drawable.t_shopping);
                img2.setImageResource(R.drawable.t_cart);
                img3.setImageResource(R.drawable.me);
                pager.setCurrentItem(0);
                break;
            case R.id.tab2:
                img2.setImageResource(R.drawable.cart);
                img1.setImageResource(R.drawable.shopping);
                img3.setImageResource(R.drawable.me);
                pager.setCurrentItem(1);
                break;
            case R.id.tab3:
                img3.setImageResource(R.drawable.t_me);
                img1.setImageResource(R.drawable.shopping);
                img2.setImageResource(R.drawable.t_cart);
                User user = AppUtils.getUser();
                if (user == null) {
                    goLogin();
                } else {
                    pager.setCurrentItem(2);
                }
                break;
            default:
                break;
        }
    }

    private void initView() {
        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
        img3 = findViewById(R.id.img3);
        findViewById(R.id.tab1).setOnClickListener(this);
        findViewById(R.id.tab2).setOnClickListener(this);
        findViewById(R.id.tab3).setOnClickListener(this);
        img1.setImageResource(R.drawable.t_shopping);
        fundusBtn = findViewById(R.id.activity_main_fundus);

    }

    private void goLogin() {
            User json = new User();
            dialog = new MyDialog(this);
            View view = getLayoutInflater().inflate(R.layout.user_login,null);
            EditText phone = view.findViewById(R.id.user_login_phone);
            EditText pass = view.findViewById(R.id.user_login_password);
            Button btnLogin = view.findViewById(R.id.user_login);
            ImageView back = view.findViewById(R.id.user_login_back);
            back.setOnClickListener(view1 -> dialogDismiss());
            btnLogin.setOnClickListener(view1 -> {
                telephone = phone.getText().toString();
                password = pass.getText().toString();
                json.setTelephone(telephone);
                json.setPassword(password);
                login(json);
            });
            dialog.setContentView(view);
            dialog.show();
    }

    private void dialogDismiss() {
        if (isBack){
            dialog.dismiss();
            pager.setCurrentItem(0);
            isBack = false;
        }else {
            dialog.dismiss();
        }

    }

    private void login(User json) {
        executor.execute(() -> {
            try {
                String data = UserService.getUserFromServer(json);
                handler.sendMessage(handler.obtainMessage(WHAT_LOGIN, data));
            } catch (Exception e) {
                handler.sendMessage(handler.obtainMessage(WHAT_L_EXCEPTION, e.getMessage()));
            }
        });
    }

    @Override
    public void onBackPressed() {
        new android.app.AlertDialog.Builder(this)
                .setMessage("退出应用吗？")
                .setPositiveButton("退出",(dialog, which) -> AppUtils.exit())
                .show();
    }

    @Override
    public void onHideLayout() {
        fundusBtn.setVisibility(View.GONE);
    }

    @Override
    public void onShowLayout() {
        fundusBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNoticeRefresh() {
        CartFragment.newInstance();
        OwnFragment.newInstance();
    }
//endregion



    @Override
    public void onClick(View v) {
        selectTab(v.getId());
    }

    @Override
    public void onBack() {
        isBack = true;
        goLogin();
        CartFragment.clearData();
    }

    @Override
    public void onGoToOrderActivity( int index) {
        Intent intent=new Intent(this, OrderActivity.class);
        intent.putExtra(RESULT_INDEX,index);
        startActivity(intent);
    }
}
