package net.lzzy.water.activites;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import net.lzzy.sqllib.GenericAdapter;
import net.lzzy.sqllib.ViewHolder;
import net.lzzy.water.R;
import net.lzzy.water.constants.ApiConstants;
import net.lzzy.water.frament.CartFragment;
import net.lzzy.water.frament.OneFragment;
import net.lzzy.water.models.Colors;
import net.lzzy.water.models.Evaluate;
import net.lzzy.water.models.Image;
import net.lzzy.water.models.OrderCart;
import net.lzzy.water.models.Product;
import net.lzzy.water.models.User;
import net.lzzy.water.models.Yardage;
import net.lzzy.water.network.FavoritesService;
import net.lzzy.water.network.OrderCartService;
import net.lzzy.water.utils.AbstractStaticHandler;
import net.lzzy.water.utils.AppUtils;
import net.lzzy.water.utils.HorizontalListView;
import net.lzzy.water.utils.MyListView;
import net.lzzy.water.utils.MyLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

import static net.lzzy.water.activites.MainActivity.RESULT_PRODUCT;

/**
 * @author 菜鸡
 */
public class BuyActivity extends AppCompatActivity {
    private static final int WHAT_STAR = 6;
    private static final int WHAT_CART = 2;
    private static final int WHAT_CART_EXCEPTION = 3;
    public static final String RESULT_P = "result_p";
    public static final String RESULT_COUNT = "result_count";
    private static final int REQUEST_CODE_RESULT = 0;
    public static final String CART = "cart";
    public static final int WHAT_STAR_B = 7;
    private Product product;
    private Banner banner;
    private MyListView lv;
    private TextView tvCount;
    private Dialog shopDialog;
    private GridView gvColors;
    private GridView gvYardage;


    private ThreadPoolExecutor executor = AppUtils.getExecutor();
    private Handler handler = new Handler(this);
    private ImageView icStar;
    private User user;
    private boolean bool;

    private static class Handler extends AbstractStaticHandler<BuyActivity> {

        private Handler(BuyActivity context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg, BuyActivity activity) {
            switch (msg.what) {
                case WHAT_STAR:
                    boolean flag = (boolean) msg.obj;
                    if (flag) {
                        activity.icStar.setImageResource(R.drawable.star_no);
                    } else {
                        activity.icStar.setImageResource(R.drawable.star_off);
                    }
                    break;
                case WHAT_STAR_B:
                    boolean flag1 = (boolean) msg.obj;
                    if (flag1) {
                        activity.icStar.setImageResource(R.drawable.star_no);
                    } else {
                        activity.icStar.setImageResource(R.drawable.star_off);
                    }
                    break;
                case WHAT_CART:
                    int code = (int) msg.obj;
                    if (code <= 200) {
                        activity.dismiss();
                        Intent intent = new Intent(SubmitActivity.S);
                        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
                        Toast.makeText(activity, "添加成功，在购物车等您！", Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.fragment_buy);
        user = AppUtils.getUser();
        product = getIntent().getParcelableExtra(RESULT_PRODUCT);
        initView();
    }

    private void initView() {
        isExists();
        ImageView tvBack = findViewById(R.id.fragment_buy_back);
        tvBack.setOnClickListener(view1 -> {
            onBackPressed();
        });
        banner = findViewById(R.id.fragment_buy_banner);
        TextView tvName = findViewById(R.id.fragment_buy_name);
        tvName.setText(product.getPname());
        TextView tvPrice = findViewById(R.id.fragment_buy_price);
        String str = "￥";
        tvPrice.setText(str.concat(String.valueOf(product.getPrice())));
        TextView tvSelect = findViewById(R.id.fragment_buy_select);
        TextView tvShopping = findViewById(R.id.fragment_buy_shopping);
        icStar = findViewById(R.id.fragment_buy_collect);
        tvShopping.setOnClickListener(view1 -> goShop(BuyActivity.this));
        icStar.setOnClickListener(view1 -> starProduct());
        tvSelect.setOnClickListener(view1 -> goShop(BuyActivity.this));
        lv = findViewById(R.id.fragment_buy_lv);
        showBanner(product.getpImage());
        evaluate(product.getEvaluates());
    }

    private void goShop(Context context) {
        View view = getLayoutInflater().inflate(R.layout.shopping, null);
        shopDialog = new Dialog(context, R.style.style_dialog);
        shopDialog.setContentView(view);
        shopDialog.show();
        ImageView img = view.findViewById(R.id.shop_imgCover);
        TextView tvPrice = view.findViewById(R.id.shop_price);
        TextView tvName = view.findViewById(R.id.shop_name);
        TextView cancel = view.findViewById(R.id.shop_cancel);
        TextView tvShopNow = view.findViewById(R.id.shop_now);
        TextView tvAddCart = view.findViewById(R.id.shop_addCart);
        gvColors = view.findViewById(R.id.shop_view_gvColor);

        TextView tvLessen = view.findViewById(R.id.shop_count_lessen);
        TextView tvAdd = view.findViewById(R.id.shop_count_add);
        tvCount = view.findViewById(R.id.shop_view_count);
        gvYardage = view.findViewById(R.id.shop_view_gvYardage);
        if (product.getYardages() != null && product.getYardages().size() > 0) {
            GenericAdapter<Yardage> yardageAdapter = new GenericAdapter<Yardage>(context, R.layout.view_item, product.getYardages()) {
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
        }
        tvLessen.setOnClickListener(view1 -> count(false));
        tvAdd.setOnClickListener(view1 -> count(true));
        String url = ApiConstants.URL_API + product.getpImage().get(0).getImage();
        Picasso.get().load(url).into(img);
        tvName.setText(product.getPname());
        tvPrice.setText(String.valueOf(product.getPrice()));
        tvShopNow.setOnClickListener(view1 -> {
            if (user != null) {
                toSubmit();
            } else {
                bool = true;
                startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_CODE_RESULT);
            }
        });
        tvAddCart.setOnClickListener(view1 -> {
            if (user != null) {
                addCart();
            } else {
                bool = false;
                startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_CODE_RESULT);
            }
        });
        cancel.setOnClickListener(view1 -> {
            dismiss();
        });
        //region
        Window window = shopDialog.getWindow();
        assert window != null;
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.BottomDialog_Animation);
        //region获取屏幕高度
       /* WindowManager manager = Objects.requireNonNull(this).getWindowManager();
        int height = manager.getDefaultDisplay().getHeight();*/
        //endregion
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        viewYardage();
        //viewColors();
        //endregion
    }

    private void viewYardage() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            user = data.getParcelableExtra(LoginActivity.RESULT_USER);
            if (user != null) {
                if (bool) {
                    toSubmit();
                } else {
                    shopDialog.hide();
                    addCart();
                }
            }
        }
    }

    private void toSubmit() {
        Intent intent = new Intent(this, SubmitActivity.class);
        intent.putExtra(CART, true);
        intent.putExtra(RESULT_P, product);
        intent.putExtra(RESULT_COUNT, count);
        startActivity(intent);
        dismiss();
    }

    private void dismiss() {
        shopDialog.dismiss();
        count = 1;
    }

    private int count = 1;

    private void count(boolean click) {
        if (click) {
            count += 1;
            tvCount.setText(String.valueOf(count));
        } else {
            if (count > 1) {
                count -= 1;
                tvCount.setText(String.valueOf(count));
            }
        }
    }

    private void addCart() {
        executor.execute(() -> {
            try {
                OrderCart orderCart = new OrderCart();
                orderCart.setCount(count);
                orderCart.setProduct(product);
                orderCart.setUser(user);
                int code = OrderCartService.postCartJson(orderCart);
                handler.sendMessage(handler.obtainMessage(WHAT_CART, code));
            } catch (Exception e) {
                handler.sendMessage(handler.obtainMessage(WHAT_CART_EXCEPTION, e.getMessage()));
            }
        });
    }

    private void viewColors() {

        if (product.getColors() != null && product.getColors().size() > 0) {
            GenericAdapter<Colors> colorsAdapter = new GenericAdapter<Colors>(this, R.layout.view_item, product.getColors()) {
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

    }

    private void isExists() {
        User user = AppUtils.getUser();
        if (user != null) {
            executor.execute(() -> {
                try {
                    boolean flag = FavoritesService.isExists(product.getPid(), user.getUid());
                    handler.sendMessage(handler.obtainMessage(WHAT_STAR_B, flag));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void starProduct() {
        User user = AppUtils.getUser();
        if (user != null) {
            executor.execute(() -> {
                try {
                    boolean json = FavoritesService.postFavorties(product.getPid(), user.getUid());
                    handler.sendMessage(handler.obtainMessage(WHAT_STAR, json));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void evaluate(List<Evaluate> evaluates) {
        GenericAdapter<Evaluate> lvAdapter = new GenericAdapter<Evaluate>(this, R.layout.evaluate, evaluates) {
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

        ViewPager viewPager = findViewById(R.id.activity_pager);
        List<ImageView> list = new ArrayList<>();
        for (Image i : image) {
            ImageView imageView = new ImageView(BuyActivity.this);
//            imageView.setMaxWidth(view1.getWidth());
//            imageView.setMaxHeight(view1.getHeight());
//            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//            ViewGroup.LayoutParams params = imageView.getLayoutParams();
//            params.width = WindowManager.LayoutParams.MATCH_PARENT;
//            params.height = 250;
//            imageView.setLayoutParams(params);

          //  Picasso.get().load(ApiConstants.URL_API + i.getImage()).into(imageView);
            list.add(imageView);
        }


        //数据适配器
        PagerAdapter mPagerAdapter = new PagerAdapter() {

            @Override
            //获取当前窗体界面数
            public int getCount() {
                return list.size();
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
        }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView(list.get(position));
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                ImageView imageView = list.get(position);
                Picasso.get().load(ApiConstants.URL_API + image.get(position).getImage()).into(imageView);
                container.addView(imageView);
                return container;
            }
        };
        viewPager.setAdapter(mPagerAdapter);

//        HorizontalListView hList =findViewById(R.id.horizontal_lv);
//
//        GenericAdapter adapter = new GenericAdapter<Image>(this, R.layout.product_image, image) {
//
//            @Override
//            public void populate(ViewHolder holder, Image image) {
//                ImageView imageView =holder.getView(R.id.product_image);
//                Picasso.get().load(ApiConstants.URL_API + image.getImage()).into(imageView);
//            }
//
//            @Override
//            public boolean persistInsert(Image image) {
//                return false;
//            }
//
//            @Override
//            public boolean persistDelete(Image image) {
//                return false;
//            }
//        };
//        hList.setAdapter(adapter);





/*
        LinearLayout layout = findViewById(R.id.image);
        HorizontalScrollView view1 = findViewById(R.id.scroll_view);
        for (Image i : image) {
            ImageView imageView = new ImageView(BuyActivity.this);
//            imageView.setMaxWidth(view1.getWidth());
//            imageView.setMaxHeight(view1.getHeight());
//            imageView.setScaleType(ImageView.ScaleType.FIT_XY);


            ViewGroup.LayoutParams params = imageView.getLayoutParams();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = 250;
            imageView.setLayoutParams(params);
            Picasso.get().load(ApiConstants.URL_API + i.getImage()).into(imageView);
            layout.addView(imageView);
        }*/

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

    @Override
    protected void onResume() {
        super.onResume();
        user = AppUtils.getUser();
    }
}
