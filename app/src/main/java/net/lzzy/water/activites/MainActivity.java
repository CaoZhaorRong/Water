package net.lzzy.water.activites;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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

import java.util.List;

/**
 * @author 菜鸡
 */
public class MainActivity extends BaseActivity implements OneFragment.OnGoToBuyFragment, BuyFragment.OnGoToOneFragment, LoginFragment.OnGoToLogin {
    private List<Category> categories;
    private LinearLayout tab1,tab2,tab3;
    private ImageView img1,img2,img3;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {

        tab1 = findViewById(R.id.tab1);
        tab2 = findViewById(R.id.tab2);
        tab3 = findViewById(R.id.tab3);
        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
        img3 = findViewById(R.id.img3);
        img1.setImageResource(R.drawable.t_shopping);
        tab1.setOnClickListener(view -> {
            getManager().beginTransaction().replace(R.id.fragment_main, OneFragment.newInstance(categories)).commit();
            img1.setImageResource(R.drawable.t_shopping);
            img2.setImageResource(R.drawable.t_cart);
            img3.setImageResource(R.drawable.me);
        });
        tab2.setOnClickListener(view -> {
            getManager().beginTransaction().replace(R.id.fragment_main, new CartFragment()).commit();
            img1.setImageResource(R.drawable.shopping);
            img2.setImageResource(R.drawable.cart);
            img3.setImageResource(R.drawable.me);
        });
        login();

    }

    private void login() {
        user = AppUtils.getUser();
        tab3.setOnClickListener(view -> {
            if (user == null){
                getManager().beginTransaction().replace(R.id.fragment_main, new LoginFragment()).commit();
            }else {
                getManager().beginTransaction().replace(R.id.fragment_main, new OwnFragment()).commit();
            }
            img1.setImageResource(R.drawable.shopping);
            img2.setImageResource(R.drawable.t_cart);
            img3.setImageResource(R.drawable.t_me);
        });
    }

    @Override
    public int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    public int getContainerId() {
        return R.id.fragment_main;
    }

    @Override
    public Fragment createFragment() {
        categories = getIntent().getParcelableArrayListExtra(SplashActivity.RESULT_CATEGORIES);
        return  OneFragment.newInstance(categories);
    }

    @Override
    public void onGoToBuyFragment(Product product) {
        getManager().beginTransaction().replace(R.id.fragment_main, BuyFragment.newInstance(product))
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onGoToOneFragment() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void OnGoToLogin() {
        user = AppUtils.getUser();
        getManager().beginTransaction().replace(R.id.fragment_main, new OwnFragment()).commit();
    }
}
