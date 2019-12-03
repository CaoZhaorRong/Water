package net.lzzy.water.frament;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.os.StrictMode;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.hjq.toast.ToastUtils;
import com.squareup.picasso.Picasso;
import com.tangguna.searchbox.library.callback.onSearchCallBackListener;
import com.tangguna.searchbox.library.widget.SearchLayout;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import net.lzzy.sqllib.GenericAdapter;
import net.lzzy.sqllib.ViewHolder;
import net.lzzy.water.R;
import net.lzzy.water.constants.ApiConstants;
import net.lzzy.water.models.Category;
import net.lzzy.water.models.Image;
import net.lzzy.water.models.Product;
import net.lzzy.water.network.CategoryService;
import net.lzzy.water.network.ProductService;
import net.lzzy.water.utils.AbstractStaticHandler;
import net.lzzy.water.utils.AppUtils;
import net.lzzy.water.utils.HorizontalListView;
import net.lzzy.water.utils.MyLoader;
import net.lzzy.water.utils.ViewUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author 菜鸡
 */
public class OneFragment extends BaseFragment {
    //region

    private static final int WHAT_PRODUCT = 0;
    private static final int WHAT_P_EXCEPTION = 1;
    private static final int INT = 9;
    private static final int WHAT_CATEGORY = 8;
    private static final int WHAT_EXCEPTION = 10;
    public static final int WHAT = 2;
    private List<Category> categories;
    private GenericAdapter<Category> adapter;
    private GenericAdapter<Product> gvAdapter;
    private OnGoToBuyFragment listener;
    private HorizontalListView hList;
    private Banner myBanner;
    private List<Product> products;
    private GridView gv;
    private TextView tvHint;
    private SwipeRefreshLayout swipe;
    private String cid;
    private LocalBroadcastManager broadcastManager;
    private int checked = 0;
    private EditText editText;
    private  int index = 0;
    private  List<String> searchKey = new ArrayList<>();


    public OneFragment() {
    }
    //endregion

    private ThreadPoolExecutor executor = AppUtils.getExecutor();

    private FragmentHandler handler = new FragmentHandler(this);

    private static class FragmentHandler extends AbstractStaticHandler<OneFragment> {

        private FragmentHandler(OneFragment context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg, OneFragment fragment) {
            switch (msg.what) {

                case WHAT_CATEGORY:
                    String category = String.valueOf(msg.obj);
                    try {
                        fragment.categories = CategoryService.getCategories(category);
                        if (fragment.categories != null && fragment.categories.size() > 0) {
                            fragment.get();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case WHAT_PRODUCT:
                    String json = String.valueOf(msg.obj);
                    try {
                        if (!"".equals(json)) {
                            fragment.products = ProductService.getProducts(json);
                            fragment.showProducts();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    fragment.swipe.setRefreshing(false);
                    break;
                case 2:
                   if (fragment.index == fragment.searchKey.size()){
                       fragment.index = 0;
                   }
                   fragment.editText.setText(fragment.searchKey.get(fragment.index));
                   fragment.index++;
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void populate() {
        if (android.os.Build.VERSION.SDK_INT > INT) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        notification();
        executor.execute(this::getCategory);
        intiView();
        swipe();
    }

    private void getCategory() {
        try {
            String json = CategoryService.getCategoryFromServer();
            handler.sendMessage(handler.obtainMessage(WHAT_CATEGORY, json));
        } catch (IOException e) {
            handler.sendMessage(handler.obtainMessage(WHAT_EXCEPTION, e.getMessage()));
        }
    }

    private void get() {
        showCategory();
        if (categories != null && categories.size() > 0) {
            cid = categories.get(0).getId();
            if (cid != null) {
                getProducts(cid);
            }
        }
    }

    private void getProducts(String cid) {
        swipe.setRefreshing(true);
        tvHint.setVisibility(View.GONE);
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
            listener.onGotoBuyActivity(product);
        });

        gv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean isTop = view.getChildCount() == 0 || view.getChildAt(0).getTop() >= 0;
                swipe.setEnabled(isTop);
            }
        });

    }

    private void showCategory() {
        adapter = new GenericAdapter<Category>(getContext(), R.layout.horizontal_lv_item, categories) {
            @Override
            public void populate(ViewHolder holder, Category category) {
                TextView tv = holder.getView(R.id.horizontal_lv_item_tv);
                if (category == categories.get(checked)){
                    tv.setText(category.getName());
                    tv.setTextColor(getContext().getColor(R.color.colorAccent));
                    //tv.setBackgroundColor(Color.GRAY);
                }else {
                    tv.setText(category.getName());
                }
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
            checked = i;
            adapter.notifyDataSetChanged();
            Category category = adapter.getItem(i);
            if (category != null) {
                cid = category.getId();
                getProducts(cid);
            }

        });
    }

    private void  swipe(){
        swipe.setOnRefreshListener(() -> getProducts(cid));
    }

    private void intiView() {
        swipe = findViewById(R.id.fragment_one_swipe);
        hList = findViewById(R.id.fragment_one_horizontal_lv);
        ImageView ivMenu = findViewById(R.id.fragment_one_menu);
        ivMenu.setOnClickListener(view ->
                new AlertDialog.Builder(getContext())
                        .setMessage("切换服务器")
                        .setNegativeButton("取消", (dialog, which) -> {
                        })
                        .setNeutralButton("设置", (dialog, which) -> ViewUtils.goSetting(getContext(), true))
                        .setCancelable(false)
                        .show()
        );
        myBanner = findViewById(R.id.banner);
        gv = findViewById(R.id.one_fragment_gv);
        tvHint = findViewById(R.id.fragment_one_hint);
        tvHint.setVisibility(View.GONE);
        gv.setNumColumns(2);

        editText = findViewById(R.id.edit_search);
        editText.setOnClickListener(view -> {
            listener.onGotoSearchActivity();
        });
        LinearLayout layout = findViewById(R.id.to_search_activity);
        layout.setOnClickListener(view -> {
            listener.onGotoSearchActivity();
        });
        searchKey.add("棉服女韩版宽松");
        searchKey.add("学生价手机");
        searchKey.add("nova5z手机");

        Timer timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendMessage(handler.obtainMessage(2,1));
            }
        },0,5000);
        //每隔一秒使用handler发送一下消息,也就是每隔一秒执行一次,一直重复执行
    }


    private  void notification(){
        broadcastManager = LocalBroadcastManager.getInstance(getActivity());
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(SubmitActivity.S);
//        broadcastManager.registerReceiver(receiver, intentFilter);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ViewUtils.MM);
        broadcastManager.registerReceiver(receiver, filter);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getCategory();
        }
    };

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
         * 通知刷新
         *
         * @param product
         */
        void onGotoBuyActivity(Product product);
        void onGotoSearchActivity();
    }
    //endregion
}
