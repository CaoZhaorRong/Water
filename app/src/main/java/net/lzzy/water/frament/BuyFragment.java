package net.lzzy.water.frament;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.loader.ImageLoader;

import net.lzzy.sqllib.GenericAdapter;
import net.lzzy.sqllib.ViewHolder;
import net.lzzy.water.R;
import net.lzzy.water.constants.ApiConstants;
import net.lzzy.water.models.Image;
import net.lzzy.water.models.Product;
import net.lzzy.water.utils.MyListView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 菜鸡
 */
public class BuyFragment extends BaseFragment {
    public static final String PRODUCT = "product";
    private OnGoToOneFragment listener;
    private  Product product;
    TextView tvBack,tvName,tvPrice,tvSelect;
    private Banner banner;
    private MyListView lv;
    private GenericAdapter<Image> lvAdapter;

    public static BuyFragment newInstance(Product product) {
        BuyFragment fragment = new BuyFragment();
        Bundle args = new Bundle();
        args.putParcelable(PRODUCT,product);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            product = getArguments().getParcelable(PRODUCT);
        }
    }
    public BuyFragment() {
    }

    @Override
    protected void populate() {
        intiView();
        showBanner();
        showLv();
    }

    private void showLv() {
        lvAdapter = new GenericAdapter<Image>(getContext(),R.layout.product_lv_item,product.getpImage()) {
            @Override
            public void populate(ViewHolder holder, Image image) {
                ImageView view = holder.getView(R.id.fragment_lv_img);
                Picasso.get().load(ApiConstants.NET+image.getImage()).into(view);
            }

            @Override
            public boolean persistInsert(Image image) {
                return false;
            }
            @Override
            public boolean persistDelete(Image image) {
                return false;
            }
        };
        lv.setAdapter(lvAdapter);
    }

    private void showBanner() {
        List<String> title = new ArrayList<>();
        List<String> images = new ArrayList<>();
        for (Image p:product.getpImage()){
            images.add(ApiConstants.NET+p.getImage());
        }
        images.add(String.valueOf(R.drawable.info));
        banner.setImageLoader(new MyLoader());
        banner.setImages(images);
        banner.setBannerAnimation(Transformer.Default);
        banner.setDelayTime(2000);
        banner.isAutoPlay(true);
        banner.setIndicatorGravity(BannerConfig.CENTER);
        banner.start();
    }

    private void intiView() {
        tvBack = findViewById(R.id.fragment_buy_back);
        tvBack.setOnClickListener(view -> listener.onGoToOneFragment());
        banner = findViewById(R.id.fragment_buy_banner);
        tvName = findViewById(R.id.fragment_buy_name);
        tvPrice = findViewById(R.id.fragment_buy_price);
        tvSelect = findViewById(R.id.fragment_buy_select);
        lv = findViewById(R.id.fragment_buy_lv);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_buy;
    }

    @Override
    public void search(String kw) {

    }


    private class MyLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(getContext()).load(path).into(imageView);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnGoToOneFragment) {
            listener = (OnGoToOneFragment) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
    public interface OnGoToOneFragment {
        /**
         *  跳转到首页面
         * @param
         */
        void  onGoToOneFragment();
    }
}
