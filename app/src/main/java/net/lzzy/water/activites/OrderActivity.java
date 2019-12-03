package net.lzzy.water.activites;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import net.lzzy.water.R;
import net.lzzy.water.frament.AllOrderFragment;
import net.lzzy.water.frament.EvaluateFragment;
import net.lzzy.water.frament.ObligationFragment;
import net.lzzy.water.frament.ShipmentsFragment;
import net.lzzy.water.frament.TakeFragment;
import net.lzzy.water.models.Order;
import net.lzzy.water.utils.StaticViewPager;

import java.util.ArrayList;
import java.util.List;

import static net.lzzy.water.activites.MainActivity.RESULT_INDEX;

/**
 * @author 菜鸡
 */
public class OrderActivity extends AppCompatActivity implements View.OnClickListener, EvaluateFragment.OnGoToEvaluateActivity {
    public static final String RESULT_O = "result_o";
    public static final String AD_DOWNLOAD_ACTION = "0";
    private StaticViewPager pager;
    private List<Fragment> fragments;
    private int[] tabIds = {R.id.o_tab0, R.id.o_tab1, R.id.o_tab2,R.id.o_tab3,R.id.o_tab4};
    private int index;
    private FrameLayout tab0;
    private FrameLayout tab1;
    private FrameLayout tab2;
    private FrameLayout tab3;
    private FrameLayout tab4;
    private List<View> views;
    private List<TextView> textViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_order);
        index = getIntent().getIntExtra(RESULT_INDEX,0);
        pagers();
        initView();
    }

    private void initView() {
        ImageView back = findViewById(R.id.activity_order_back);
        tab0 = findViewById(R.id.o_tab0);
        tab1 = findViewById(R.id.o_tab1);
        tab2 = findViewById(R.id.o_tab2);
        tab3 = findViewById(R.id.o_tab3);
        tab4 = findViewById(R.id.o_tab4);
        tab0.setOnClickListener(view -> pager.setCurrentItem(0));
        tab1.setOnClickListener(view -> pager.setCurrentItem(1));
        tab2.setOnClickListener(view -> pager.setCurrentItem(2));
        tab3.setOnClickListener(view -> pager.setCurrentItem(3));
        tab4.setOnClickListener(view -> pager.setCurrentItem(4));
        TextView tvOne = findViewById(R.id.tv_one);
        TextView tvTow = findViewById(R.id.tv_tow);
        TextView tvThree = findViewById(R.id.tv_three);
        TextView tvFour = findViewById(R.id.tv_four);
        TextView tvFive = findViewById(R.id.tv_five);
        View vOne = findViewById(R.id.v_one);
        View vTow = findViewById(R.id.v_tow);
        View vThree = findViewById(R.id.v_three);
        View vFour = findViewById(R.id.v_four);
        View vFive = findViewById(R.id.v_five);
        views = new ArrayList<>();
        views.add(0,vOne);
        views.add(1,vTow);
        views.add(2,vThree);
        views.add(3,vFour);
        views.add(4,vFive);
        textViews = new  ArrayList<>();
        textViews.add(0,tvOne);
        textViews.add(1,tvTow);
        textViews.add(2,tvThree);
        textViews.add(3,tvFour);
        textViews.add(4,tvFive);
        back.setOnClickListener(view -> {
            onBackPressed();
            Intent intent = new Intent(AD_DOWNLOAD_ACTION);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        });
        selectTv(index);
        selectTab(tabIds[index]);
    }
    private void pagers() {
        fragments = new ArrayList<>();
        fragments.add(0,new AllOrderFragment());
        fragments.add(1,new ObligationFragment());
        fragments.add(2,new ShipmentsFragment());
        fragments.add(3,new TakeFragment());
        fragments.add(4,new EvaluateFragment());
        pager = findViewById(R.id.activity_order_pager);
        pager.enableSlide(true);
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
                selectTv(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        pager.setAdapter(fragmentStatePagerAdapter);
        pager.setOffscreenPageLimit(fragments.size());
        pager.setPageMargin(40);
        fragmentStatePagerAdapter.notifyDataSetChanged();
    }
    private void selectTv(int tvIds){
        for (int i = 0;i<textViews.size();i++){
            if (i == tvIds){
                textViews.get(i).setTextColor(this.getColor(R.color.colorAccent));
            }else {
                textViews.get(i).setTextColor(this.getColor(R.color.colorBlack));
            }
        }
        for (int i = 0;i<views.size();i++){
            if (i == tvIds){
                views.get(i).setBackgroundColor(Color.BLUE);
            }else {
                views.get(i).setBackgroundColor(Color.WHITE);
            }
        }
    }
    private void selectTab(int tabId) {
        switch (tabId) {
            case R.id.o_tab0:
                pager.setCurrentItem(0);
                break;
            case R.id.o_tab1:
                pager.setCurrentItem(1);
                break;
            case R.id.o_tab2:
                pager.setCurrentItem(2);
                break;
            case R.id.o_tab3:
                pager.setCurrentItem(3);
                break;
            case R.id.o_tab4:
                pager.setCurrentItem(4);
            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        selectTab(view.getId());
    }

    @Override
    public void onGoToEvaluateActivity(Order order) {
        Intent intent=new Intent(this, EvaluateActivity.class);
        intent.putExtra(RESULT_O,order);
        startActivity(intent);
    }

}
