package net.lzzy.water.activites;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import net.lzzy.water.R;
import net.lzzy.water.frament.AllOrderFragment;
import net.lzzy.water.frament.EvaluateFragment;
import net.lzzy.water.frament.ObligationFragment;
import net.lzzy.water.frament.ShipmentsFragment;
import net.lzzy.water.frament.TakeFragment;
import net.lzzy.water.utils.StaticViewPager;

import java.util.ArrayList;
import java.util.List;

import static net.lzzy.water.activites.MainActivity.RESULT_INDEX;

/**
 * @author 菜鸡
 */
public class OrderActivity extends AppCompatActivity implements View.OnClickListener {
    private StaticViewPager pager;
    private List<Fragment> fragments;
    private int[] tabIds = {R.id.o_tab0, R.id.o_tab1, R.id.o_tab2,R.id.o_tab3,R.id.o_tab4};
    private int index;
    private TextView tab0;
    private TextView tab1;
    private TextView tab2;
    private TextView tab3;
    private TextView tab4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_order);
        index = getIntent().getIntExtra(RESULT_INDEX,0);
        pagers();
        pager.setCurrentItem(index);
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
        back.setOnClickListener(view -> onBackPressed());
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
}
