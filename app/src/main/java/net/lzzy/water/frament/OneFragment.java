package net.lzzy.water.frament;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.os.StrictMode;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
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
import net.lzzy.water.models.Colors;
import net.lzzy.water.models.Evaluate;
import net.lzzy.water.models.Image;
import net.lzzy.water.models.Order;
import net.lzzy.water.models.OrderCart;
import net.lzzy.water.models.Product;
import net.lzzy.water.models.User;
import net.lzzy.water.models.Yardage;
import net.lzzy.water.network.FavoritesService;
import net.lzzy.water.network.OrderCartService;
import net.lzzy.water.network.OrderService;
import net.lzzy.water.network.ProductService;
import net.lzzy.water.utils.AbstractStaticHandler;
import net.lzzy.water.utils.AppUtils;
import net.lzzy.water.utils.HorizontalListView;
import net.lzzy.water.utils.MyDialog;
import net.lzzy.water.utils.MyGridView;
import net.lzzy.water.utils.MyListView;
import net.lzzy.water.utils.MyLoader;
import net.lzzy.water.utils.ViewUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * @author 菜鸡
 */
public class OneFragment extends BaseFragment {
    //region

    private static final String ARG_CATEGORIES_RESULT = "argCategoriesResult";
    private static final int WHAT_PRODUCT = 0;
    private static final int WHAT_P_EXCEPTION = 1;
    private static final int INT = 9;
    private static final int WHAT_CART = 2;
    private static final int WHAT_CART_EXCEPTION = 3;
    private static final int WHAT_SUBMIT_EXCEPTION = 5;
    private static final int WHAT_SUBMIT = 4;
    private static final int WHAT_STAR = 6;
    private List<Category> categories;
    private GenericAdapter<Category> adapter;
    private GenericAdapter<Product> gvAdapter;
    private OnGoToBuyFragment listener;
    private HorizontalListView hList;
    private Banner myBanner;
    private List<Product> products;
    private MyGridView gv;
    private ImageView icStar;
    private MyDialog submitDialog;
    private MyDialog buyDialog;
    private TextView tvAdd;
    private TextView tvLessen;
    private TextView tvCount;
    private Dialog shopDialog;
    private GridView gvColors;
    private GridView gvYardage;
    private TextView tvHint;


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
    //endregion

    @Override
    protected void populate() {
        if (android.os.Build.VERSION.SDK_INT > INT) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        intiView();
        get();
    }

    private void get() {
        showCategory();
        if (categories!=null&&categories.size() > 0) {
            String cid = categories.get(0).getId();
            if (cid != null) {
                getProducts(cid);
            }
        }
    }

    private void getProducts(String cid) {
        tvHint.setVisibility(View.VISIBLE);
        executor.execute(() -> {
            try {
                String json = ProductService.getProductFromServer(cid);
                handler.sendMessage(handler.obtainMessage(WHAT_PRODUCT, json));
            } catch (IOException e) {
                handler.sendMessage(handler.obtainMessage(WHAT_P_EXCEPTION, e.getMessage()));
            }
        });
    }

    private void showProducts() {
        gvAdapter = new GenericAdapter<Product>(getContext(), R.layout.product_item, products) {
            @Override
            public void populate(ViewHolder holder, Product product) {
                List<Image> images = product.getpImage();
                if (images.size() > 0) {
                    ImageView imageView = holder.getView(R.id.product_item_iv);
                    String url = ApiConstants.URL_API + images.get(0).getImage();
                    Picasso.get().load(url).into(imageView);
                }

                holder.setTextView(R.id.product_item_name, product.getPname())
                        .setTextView(R.id.product_item_price, "￥" + product.getPrice());
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
        gv.setOnItemClickListener((adapterView, view, pos, l) -> {
            Product product = gvAdapter.getItem(pos);
            assert product != null;
            goBuy(product);
            listener.onHideLayout();
        });
    }

    private Banner banner;
    private MyListView lv;

    private void goBuy(Product product) {
        isExists(product);
        buyDialog = new MyDialog(getActivity());
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.fragment_buy, null);
        ImageView tvBack = view.findViewById(R.id.fragment_buy_back);
        tvBack.setOnClickListener(view1 -> {
            buyDialog.dismiss();
            listener.onShowLayout();
        });
        banner = view.findViewById(R.id.fragment_buy_banner);
        TextView tvName = view.findViewById(R.id.fragment_buy_name);
        tvName.setText(product.getPname());
        TextView tvPrice = view.findViewById(R.id.fragment_buy_price);
        String str = "￥";
        tvPrice.setText(str.concat(String.valueOf(product.getPrice())));
        TextView tvSelect = view.findViewById(R.id.fragment_buy_select);
        TextView tvShopping = view.findViewById(R.id.fragment_buy_shopping);
        icStar = view.findViewById(R.id.fragment_buy_collect);
        tvShopping.setOnClickListener(view1 -> goShop(product));
        icStar.setOnClickListener(view1 -> starProduct(product));
        tvSelect.setOnClickListener(view1 -> goShop(product));
        lv = view.findViewById(R.id.fragment_buy_lv);
        showBanner(product.getpImage());
        evaluate(product.getEvaluates());
        buyDialog.setContentView(view);
        buyDialog.show();

    }

    private void isExists(Product product) {
        User user = AppUtils.getUser();
        if (user != null) {
            executor.execute(() -> {
                try {
                    boolean flag = FavoritesService.isExists(product.getPid(), user.getUid());
                    if (flag) {
                        icStar.setImageResource(R.drawable.star_no);
                    } else {
                        icStar.setImageResource(R.drawable.star_off);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void goShop(Product product) {
        View view = getLayoutInflater().inflate(R.layout.shopping, null);
        shopDialog = new Dialog(Objects.requireNonNull(getActivity()), R.style.style_dialog);
        shopDialog.setContentView(view);
        shopDialog.show();
        ImageView img = view.findViewById(R.id.shop_imgCover);
        TextView tvPrice = view.findViewById(R.id.shop_price);
        TextView tvName = view.findViewById(R.id.shop_name);
        TextView cancel = view.findViewById(R.id.shop_cancel);
        TextView tvShopNow = view.findViewById(R.id.shop_now);
        TextView tvAddCart = view.findViewById(R.id.shop_addCart);
        gvColors = view.findViewById(R.id.shop_view_gvColor);
        gvYardage = view.findViewById(R.id.shop_view_gvYardage);
        LinearLayout tvLayout = findViewById(R.id.shop_view_layout);
        tvLessen = view.findViewById(R.id.shop_count_lessen);
        tvAdd = view.findViewById(R.id.shop_count_add);
        tvCount = view.findViewById(R.id.shop_view_count);
        viewColors(product.getYardages(), product.getColors());
        tvLessen.setOnClickListener(view1 -> count(tvLessen.getId()));
        tvAdd.setOnClickListener(view1 -> count(tvAdd.getId()));
        String url = ApiConstants.URL_API + product.getpImage().get(0).getImage();
        Picasso.get().load(url).into(img);
        tvName.setText(product.getPname());
        tvPrice.setText(String.valueOf(product.getPrice()));
        tvShopNow.setOnClickListener(view1 -> {
            User user = AppUtils.getUser();
            if (user != null) {
                shopNow(product, user);
            }
        });
        tvAddCart.setOnClickListener(view1 -> {
            User user = AppUtils.getUser();
            if (user != null) {
                addCart(product, user);
            }
        });
        cancel.setOnClickListener(view1 -> {
            shopDialog.dismiss();
            count = 1;
        });
        //region
        Window window = shopDialog.getWindow();
        assert window != null;
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.BottomDialog_Animation);
        //region获取屏幕高度
        WindowManager manager = Objects.requireNonNull(getActivity()).getWindowManager();
        int height = manager.getDefaultDisplay().getHeight();
        //endregion
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        //endregion

    }

    private void viewColors(List<Yardage> yardages, List<Colors> colors) {
        GenericAdapter<Yardage> yardageAdapter = new GenericAdapter<Yardage>(getContext(), R.layout.view_item, yardages) {
            @Override
            public void populate(ViewHolder holder, Yardage yardage) {
                holder.setTextView(R.id.view_item_item, yardage.getYardage());
            }

            @Override
            public boolean persistInsert(Yardage yardage) {
                return false;
            }

            @Override
            public boolean persistDelete(Yardage yardage) {
                return false;
            }
        };
        gvYardage.setAdapter(yardageAdapter);
        GenericAdapter<Colors> colorsAdapter = new GenericAdapter<Colors>(getContext(), R.layout.view_item, colors) {
            @Override
            public void populate(ViewHolder holder, Colors colors) {
                holder.setTextView(R.id.view_item_item, colors.getColors());
            }

            @Override
            public boolean persistInsert(Colors colors) {
                return false;
            }

            @Override
            public boolean persistDelete(Colors colors) {
                return false;
            }
        };
        gvColors.setAdapter(colorsAdapter);
    }

    private int count = 1;

    private void count(int click) {
        if (click == tvAdd.getId()) {
            count += 1;
            tvCount.setText(String.valueOf(count));
        }
        if (click == tvLessen.getId()) {
            if (count != 0 && count > 1) {
                count = count - 1;
                tvCount.setText(String.valueOf(count));
            }
        }
    }

    private void shopNow(Product item, User user) {
        if (shopDialog != null) {
            shopDialog.dismiss();
        }
        submitDialog = new MyDialog(getActivity());
        View view = getLayoutInflater().inflate(R.layout.submit_order, null);
        ImageView icBack = view.findViewById(R.id.submit_back);
        ImageView image = view.findViewById(R.id.submit_imgCover);
        TextView tvName = view.findViewById(R.id.submit_name);
        TextView tvPrice = view.findViewById(R.id.submit_price);
        TextView submitPrice = view.findViewById(R.id.submit_submit_price);
        TextView tvSubmit = view.findViewById(R.id.submit_submit_order);
        TextView tvSize = view.findViewById(R.id.submit_size);
        TextView tvCount = view.findViewById(R.id.submit_count);
        icBack.setOnClickListener(view1 -> dismissDialog());
        Picasso.get().load(ApiConstants.URL_API + item.getpImage().get(0).getImage()).into(image);
        tvName.setText(item.getPname());
        tvPrice.setText(String.valueOf(item.getPrice()));
        tvCount.setText("数量×".concat(String.valueOf(count)));
        submitPrice.setText("实付款：￥".concat(String.valueOf(item.getPrice())));
        tvSubmit.setOnClickListener(view1 -> submitOrder(item, user));
        submitDialog.setContentView(view);
        submitDialog.show();
    }

    private void submitOrder(Product item, User user) {
        executor.execute(() -> {
            try {
                Order order = new Order();
                order.setAddress("柳州职业技术学院");
                order.setCount(count);
                order.setName("曹兆荣");
                order.setState(1);
                order.setTelephone("18776504453");
                order.setUserId(user.getUid());
                Product product= new Product();
                product.setPid(item.getPid());
                order.setProduct(product);
                int code = OrderService.posOrderFromServer(order);
                handler.sendMessage(handler.obtainMessage(WHAT_SUBMIT, code));
            } catch (Exception e) {
                handler.sendMessage(handler.obtainMessage(WHAT_SUBMIT_EXCEPTION, e.getMessage()));
            }
        });
    }

    private void addCart(Product pos, User user) {
        executor.execute(() -> {
            try {
                OrderCart orderCart = new OrderCart();
                orderCart.setCount(count);
                orderCart.setProduct(pos);
                orderCart.setUser(user);
                int code = OrderCartService.postCartJson(orderCart);
                handler.sendMessage(handler.obtainMessage(WHAT_CART, code));
            } catch (Exception e) {
                handler.sendMessage(handler.obtainMessage(WHAT_CART_EXCEPTION, e.getMessage()));
            }
        });
    }

    private void starProduct(Product product) {
        User user = AppUtils.getUser();
        if (user != null) {
            executor.execute(() -> {
                try {
                    String json = FavoritesService.postFavorties(product.getPid(), user.getUid());
                    handler.sendMessage(handler.obtainMessage(WHAT_STAR, json));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void dismissDialog() {
        count = 1;
        listener.onShowLayout();
        if (shopDialog != null) {
            shopDialog.dismiss();
        }
        if (submitDialog != null) {
            submitDialog.dismiss();
        }
        listener.onNoticeRefresh();
    }

    private void evaluate(List<Evaluate> evaluates) {
        GenericAdapter<Evaluate> lvAdapter = new GenericAdapter<Evaluate>(getContext(), R.layout.evaluate, evaluates) {
            @Override
            public void populate(ViewHolder holder, Evaluate evaluate) {
                holder.setTextView(R.id.evaluate_name, evaluate.getName())
                        .setTextView(R.id.evaluate_tv_content, evaluate.getContent());
                RoundedImageView imageView = holder.getView(R.id.evaluate_img);
                Picasso.get().load(ApiConstants.URL_API + evaluate.getCover()).into(imageView);
            }

            @Override
            public boolean persistInsert(Evaluate evaluate) {
                return false;
            }

            @Override
            public boolean persistDelete(Evaluate evaluate) {
                return false;
            }
        };
        lv.setAdapter(lvAdapter);
    }

    private void showBanner(List<Image> image) {
        List<String> images = new ArrayList<>();
        for (Image p : image) {
            images.add(ApiConstants.URL_API + p.getImage());
        }
        banner.setImageLoader(new MyLoader());
        banner.setImages(images);
        banner.setBannerAnimation(Transformer.Default);
        banner.setDelayTime(2000);
        banner.isAutoPlay(false);
        banner.setIndicatorGravity(BannerConfig.CENTER);
        banner.start();

    }

    private ThreadPoolExecutor executor = AppUtils.getExecutor();

    private FragmentHandler handler = new FragmentHandler(this);

    private static class FragmentHandler extends AbstractStaticHandler<OneFragment> {

        private static final String FLAG = "flag";

        private FragmentHandler(OneFragment context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg, OneFragment fragment) {
            switch (msg.what) {
                case WHAT_PRODUCT:
                    String json = String.valueOf(msg.obj);
                    try {
                        fragment.products = ProductService.getProducts(json);
                        if (fragment.products!=null&&fragment.products.size()>0){
                            fragment.tvHint.setVisibility(View.GONE);
                        }
                        // fragment.initBanner();
                        fragment.showProducts();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case WHAT_CART:
                    int code = (int) msg.obj;
                    if (code <= 200) {
                        fragment.dismissDialog();
                        Toast.makeText(fragment.getActivity(), "添加成功，在购物车等您！", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case WHAT_SUBMIT:
                    int order = (int) msg.obj;
                    if (order <= 200) {
                        Toast.makeText(fragment.getActivity(), "已下单，请耐心等待您的包裹！", Toast.LENGTH_SHORT).show();
                        fragment.dismissDialog();
                    }
                    break;
                case WHAT_STAR:
                    String flag = String.valueOf(msg.obj);
                    try {
                        JSONObject object = new JSONObject(flag);
                        if (object.getBoolean(FLAG)) {
                            fragment.icStar.setImageResource(R.drawable.star_no);
                        } else {
                            fragment.icStar.setImageResource(R.drawable.star_off);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                default:
                    break;
            }
        }
    }

    private void showCategory() {
        adapter = new GenericAdapter<Category>(getContext(), R.layout.horizontal_lv_item, categories) {
            @Override
            public void populate(ViewHolder holder, Category category) {
                holder.setTextView(R.id.horizontal_lv_item_tv, category.getName());
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
        hList.setOnItemClickListener((adapterView, view, i, l) -> {
            Category category = adapter.getItem(i);
            if (category != null) {
                getProducts(category.getId());
            }

        });
    }

    private void intiView() {
        hList = findViewById(R.id.fragment_one_horizontal_lv);
        ImageView ivMenu = findViewById(R.id.fragment_one_menu);
        ivMenu.setOnClickListener(view ->
            new AlertDialog.Builder(getContext())
                    .setMessage("切换服务器将会退出，是否继续")
                    .setNegativeButton("取消", (dialog, which) -> {
                    })
                    .setNeutralButton("设置", (dialog, which) -> ViewUtils.goSetting(getContext()))
                    .show()
        );
        myBanner = findViewById(R.id.banner);
        gv = findViewById(R.id.one_fragment_gv);
        tvHint = findViewById(R.id.fragment_one_hint);
        tvHint.setVisibility(View.GONE);
        gv.setNumColumns(2);
    }


    //region

    private void initBanner() {
        List<String> title = new ArrayList<>();
        List<String> images = new ArrayList<>();
        for (Product product : products) {
            title.add(product.getPname());
            images.add(ApiConstants.URL_API + product.getpImage().get(0).getImage());
        }
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
         * 跳转到购买页面
         *
         * @param
         */

        void onHideLayout();

        /**
         * 跳转到购买页面
         */
        void onShowLayout();

        /**
         * 通知刷新
         *
         * @param
         */

        void onNoticeRefresh();
    }
    //endregion
}
