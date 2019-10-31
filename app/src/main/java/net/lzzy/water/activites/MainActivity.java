package net.lzzy.water.activites;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import net.lzzy.water.R;
import net.lzzy.water.frament.BuyFragment;
import net.lzzy.water.frament.CartFragment;
import net.lzzy.water.frament.LoginFragment;
import net.lzzy.water.frament.OneFragment;
import net.lzzy.water.frament.OwnFragment;
import net.lzzy.water.models.Category;
import net.lzzy.water.models.Product;
import net.lzzy.water.models.User;
import net.lzzy.water.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 菜鸡
 */
public class MainActivity extends AppCompatActivity implements OneFragment.OnGoToBuyFragment,
        BuyFragment.OnGoToOneFragment, LoginFragment.OnGoToLogin, View.OnClickListener {
    private List<Category> categories;
    private ImageView img1, img2, img3;
    private User user;
    private ViewPager pager;
    private List<Fragment> fragments;
    private FragmentStatePagerAdapter fragmentStatePagerAdapter;
    private int[] tabIds = {R.id.tab1, R.id.tab2, R.id.tab3, 4,5};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        categories = getIntent().getParcelableArrayListExtra(SplashActivity.RESULT_CATEGORIES);
        initView();
        viewPagers();
    }

    private void viewPagers() {
        //region
        fragments = new ArrayList<>();
        fragments.add(0,OneFragment.newInstance(categories));
        fragments.add(1,new CartFragment());
        fragments.add(2,new OwnFragment());
        fragments.add(3,new LoginFragment());
        fragments.add(4,new BuyFragment());
        pager = findViewById(R.id.activity_main_pager);
        fragmentStatePagerAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
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
                pager.setCurrentItem(0);
                break;
            case R.id.tab2:
                img2.setImageResource(R.drawable.cart);
                pager.setCurrentItem(1);
                break;
            case R.id.tab3:
                img3.setImageResource(R.drawable.t_me);
                user = AppUtils.getUser();
                if (user == null) {
                    pager.setCurrentItem(3);
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

    }

    @Override
    public void onGoToBuyFragment(Product data) {
        fragments.set(4, BuyFragment.newInstance(data));
        pager.setCurrentItem(4);

    }

    @Override
    public void onGoToOneFragment() {
        pager.setCurrentItem(0);
    }

    @Override
    public void OnGoToLogin() {
        user = AppUtils.getUser();
        pager.setCurrentItem(2);
    }

    @Override
    public void onClick(View v) {
        selectTab(v.getId());
    }
}
