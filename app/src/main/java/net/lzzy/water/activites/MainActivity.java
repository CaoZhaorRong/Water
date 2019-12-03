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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;
import net.lzzy.water.R;
import net.lzzy.water.frament.CartFragment;
import net.lzzy.water.frament.OneFragment;
import net.lzzy.water.frament.OwnFragment;
import net.lzzy.water.models.OrderCart;
import net.lzzy.water.models.Product;
import net.lzzy.water.models.User;
import net.lzzy.water.network.UserService;
import net.lzzy.water.utils.AbstractStaticHandler;
import net.lzzy.water.utils.AppUtils;
import net.lzzy.water.utils.UserCookies;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author 菜鸡
 */
public class MainActivity extends AppCompatActivity implements OneFragment.OnGoToBuyFragment,
        View.OnClickListener , OwnFragment.OnBack , OwnFragment.OnGoToOrderActivity , CartFragment.OnGoToBuyFragment {
    public static final int WHAT_LOGIN = 0;
    public static final int WHAT_L_EXCEPTION = 1;
    public static final String RESULT_INDEX = "result_index";
    public static final String RESULT_PRODUCT = "result_product";
    private static final int REQUEST_CODE_RESULT = 0;
    public static final String ORDER_CART = "order_cart";
    public static final String KEY = "key";

    private ImageView img1, img2, img3;
    private ViewPager pager;
    private List<Fragment> fragments;
    private int[] tabIds = {R.id.tab1, R.id.tab2, R.id.tab3, 4,5};

    private ThreadPoolExecutor executor = AppUtils.getExecutor();

    private Handler handler = new Handler(this);
    private User user;

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
                        }else {
                            Toast.makeText(activity, "登录信息已失效，请重新登录！", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case WHAT_L_EXCEPTION:
                    Toast.makeText(activity, "登录信息已失效，请重新登录！", Toast.LENGTH_SHORT).show();
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
        initView();
        autoLogin();
        viewPagers();
    }

//region

    private void autoLogin() {
            User json = new User();
            Pair<String, String> user = UserCookies.getInstance().loadUser();
            if (!"".equals(user.first)&&!"".equals(user.second)){
                json.setTelephone(user.first);
                json.setPassword(user.second);
                executor.execute(() -> {
                    try {
                        String data = UserService.getUserFromServer(json);
                        handler.sendMessage(handler.obtainMessage(WHAT_LOGIN, data));
                    } catch (Exception e) {
                        handler.sendMessage(handler.obtainMessage(WHAT_L_EXCEPTION, e.getMessage()));
                    }
                });
            }
    }

    public void viewPagers() {
        fragments = new ArrayList<>();
        fragments.add(0,new OneFragment());
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
                user = AppUtils.getUser();
                if (user == null) {
                    startActivityForResult(new Intent(this,LoginActivity.class), REQUEST_CODE_RESULT);
                } else {
                    img3.setImageResource(R.drawable.t_me);
                    img1.setImageResource(R.drawable.shopping);
                    img2.setImageResource(R.drawable.t_cart);
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        user = AppUtils.getUser();
    }

    @Override
    public void onBackPressed() {
        new android.app.AlertDialog.Builder(this)
                .setMessage("退出应用吗？")
                .setPositiveButton("退出",(dialog, which) -> AppUtils.exit())
                .show();
    }

    @Override
    public void onGotoBuyActivity(Product product) {
        Intent intent=new Intent(this, BuyActivity.class);
        intent.putExtra(RESULT_PRODUCT,product);
        startActivity(intent);
    }

    @Override
    public void onGotoSearchActivity() {
        Intent intent = new Intent(this,SearchActivity.class);
        startActivity(intent);
    }

    @Override
    public void onToSubmitActivity(List<OrderCart> orderCarts) {
        Intent intent=new Intent(this, SubmitActivity.class);
        intent.putExtra(BuyActivity.CART,false);
        intent.putParcelableArrayListExtra(ORDER_CART, (ArrayList<? extends Parcelable>) orderCarts);
        startActivity(intent);
    }
//endregion

    @Override
    public void onClick(View v) {
        selectTab(v.getId());
    }


    @Override
    public void onBack() {
        pager.setCurrentItem(0);
        startActivityForResult(new Intent(this,LoginActivity.class), REQUEST_CODE_RESULT);
    }

    @Override
    public void onGoToOrderActivity( int index) {
        Intent intent=new Intent(this, OrderActivity.class);
        intent.putExtra(RESULT_INDEX,index);
        startActivity(intent);
    }

    @Override
    public void onGoToLocationActivity() {
        Intent intent = new Intent(this,LocationActivity.class);
        intent.putExtra(SubmitActivity.PRESENT,false);
        startActivity(intent);
    }
}
