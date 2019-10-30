package net.lzzy.water.frament;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
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
import net.lzzy.water.models.Category;
import net.lzzy.water.models.Image;
import net.lzzy.water.models.Product;
import net.lzzy.water.models.User;
import net.lzzy.water.network.ProductService;
import net.lzzy.water.network.UserService;
import net.lzzy.water.utils.AbstractStaticHandler;
import net.lzzy.water.utils.AppUtils;
import net.lzzy.water.utils.HorizontalListView;
import net.lzzy.water.utils.MyGridView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * @author 菜鸡
 */
public class OneFragment extends BaseFragment {
    private static final String ARG_CATEGORIES_RESULT = "argCategoriesResult";
    private static final int WHAT_PRODUCT = 0;
    private static final int WHAT_P_EXCEPTION = 1;
    public static final int INT = 9;
    private List<Category> categories;
    private GenericAdapter<Category> adapter;
    private GenericAdapter<Product> gvAdapter;
    private OnGoToBuyFragment listener;
    private HorizontalListView hList;
    Banner myBanner;
    private List<Product> products;
    private MyGridView gv;

    public OneFragment() {
    }

    public static OneFragment newInstance(List<Category> categories) {
        OneFragment fragment = new OneFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_CATEGORIES_RESULT, (ArrayList<? extends Parcelable>) categories);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categories = getArguments().getParcelableArrayList(ARG_CATEGORIES_RESULT);
        }

    }

    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void populate() {
        if (android.os.Build.VERSION.SDK_INT > INT) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        intiView();
        showCategory();
        getProducts(categories.get(0).getId());
        hList.setOnItemClickListener((adapterView, view, i, l) -> {
            Category category = adapter.getItem(i);
            if (category != null) {
                getProducts(category.getId());
            }

           /* new Thread(() -> {
                User user =new User();
                user.setPassword("1");
                user.setAddress("12");
                user.setBirthday("12");
                user.setHeadImage("12");
                user.setRole("User");
                user.setUid("12");
                user.setUsername("12");
                user.setTelephone("15277979884");
                try {
                    String json = UserService.getUserFromServer(user);
                    System.out.println(json);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }).start();*/

        });


    }

    private void getProducts(String cid) {
        executor.execute(() -> {
            try {
                String json = ProductService.getProductFromServer(cid);
                handler.sendMessage(handler.obtainMessage(WHAT_PRODUCT, json));
            } catch (IOException e) {
                handler.sendMessage(handler.obtainMessage(WHAT_P_EXCEPTION, e.getMessage()));
            }
        });
    }
    private void showProducts(){
        gvAdapter = new GenericAdapter<Product>(getContext(),R.layout.product_item,products) {
            @Override
            public void populate(ViewHolder holder, Product product) {
                List<Image> images = product.getpImage();
                if (images .size() > 0){
                    ImageView imageView = holder.getView(R.id.product_item_iv);
                    String url = ApiConstants.NET + images.get(0).getImage();
                        Picasso.get().load(url).into(imageView);
                }

               holder.setTextView(R.id.product_item_name,product.getPname())
                       .setTextView(R.id.product_item_price, "￥"+product.getPrice());
            }

            @Override
            public boolean persistInsert(Product product) {
                return false;
            }

            @Override
            public boolean persistDelete(Product product) {
                return false;
            }
        };
        gv.setAdapter(gvAdapter);
        gv.setOnItemClickListener((adapterView, view, i, l) -> listener.onGoToBuyFragment(gvAdapter.getItem(i)));
    }

    private ThreadPoolExecutor executor = AppUtils.getExecutor();

    private FragmentHandler handler = new FragmentHandler(this);

    private static class FragmentHandler extends AbstractStaticHandler<OneFragment> {

        private FragmentHandler(OneFragment context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg, OneFragment fragment) {
            switch (msg.what){
                case WHAT_PRODUCT:
                    String json = String.valueOf(msg.obj);
                    try {
                        fragment.products = ProductService.getProducts(json);
                        fragment.initBanner();
                        fragment.showProducts();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                        break;
            }
        }
    }

    private void showCategory(){
            adapter = new GenericAdapter<Category>(getContext(),R.layout.horizontal_lv_item,categories) {
                @Override
                public void populate(ViewHolder holder, Category category) {
                    holder.setTextView(R.id.horizontal_lv_item_tv,category.getName());
                }

                @Override
                public boolean persistInsert(Category category) {
                    return false;
                }

                @Override
                public boolean persistDelete(Category category) {
                    return false;
                }
            };
            hList.setAdapter(adapter);

    }

    private  void intiView(){
        hList = findViewById(R.id.fragment_one_horizontal_lv);
        myBanner = findViewById(R.id.banner);
        gv = findViewById(R.id.one_fragment_gv);
        gv.setNumColumns(3);

    }


    //region
    private void initBanner() {
        List<String> title = new ArrayList<>();
        List<String> images = new ArrayList<>();
        for (Product product:products){
            title.add(product.getPname());
            images.add(ApiConstants.NET+product.getpImage().get(0).getImage());
        }
//        images.add(R.drawable.category);
//        images.add(R.drawable.info);
//        images.add(R.drawable.scan);
//        title.add("小明");
//        title.add("小花");
//        title.add("小芳");
       // myBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        myBanner.setImageLoader(new MyLoader());
        myBanner.setImages(images);
        myBanner.setBannerTitles(title);
        myBanner.setBannerAnimation(Transformer.Default);
        //切换频率
        myBanner.setDelayTime(2000);
        //自动启动
        myBanner.isAutoPlay(true);
        //位置设置
        myBanner.setIndicatorGravity(BannerConfig.CENTER);
        //开始运行
        myBanner.start();
       // myBanner.onPageSelected();
    }
    private class MyLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(getContext()).load(path).into(imageView);
        }
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_one;
    }

    @Override
    public void search(String kw) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnGoToBuyFragment) {
            listener = (OnGoToBuyFragment) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnGoToBuyFragment {
        /**
         *  跳转到购买页面
         * @param product 产品
         */
        void  onGoToBuyFragment(Product product);
    }

    //endregion
}
