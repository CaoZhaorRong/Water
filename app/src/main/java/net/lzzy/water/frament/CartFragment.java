package net.lzzy.water.frament;

import android.content.BroadcastReceiver;
import android.content.Context;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.squareup.picasso.Picasso;

import net.lzzy.sqllib.GenericAdapter;
import net.lzzy.sqllib.ViewHolder;
import net.lzzy.water.R;
import net.lzzy.water.activites.MainActivity;
import net.lzzy.water.activites.SubmitActivity;
import net.lzzy.water.constants.ApiConstants;
import net.lzzy.water.models.Order;
import net.lzzy.water.models.OrderCart;
import net.lzzy.water.models.Product;
import net.lzzy.water.models.User;
import net.lzzy.water.network.OrderCartService;
import net.lzzy.water.network.OrderService;
import net.lzzy.water.utils.AbstractStaticHandler;
import net.lzzy.water.utils.AppUtils;
import net.lzzy.water.utils.MyDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author 菜鸡
 */
public class CartFragment extends BaseFragment {

    private static final int WHAT_CART = 0;
    private static final int WHAT_C_E = 1;
    private static final int WHAT_DELETE = 4;
    private static final int WHAT_COUNT = 6;
    private ListView lv;
    private ImageView ivCheckAll;
    private TextView tvTotal;
    private TextView tvClose;
    private static GenericAdapter<OrderCart> lvAdapter;
    private static List<OrderCart> orderCarts;
    private List<OrderCart> clickOrderCarts = new ArrayList<>();
    private boolean clicks = false;
    private LinearLayout checkAllLayout;
    private User user;
    private boolean isRefresh = true;
    private OnGoToBuyFragment listener;
    private LocalBroadcastManager broadcastManager;

    public CartFragment() {
    }
    private ThreadPoolExecutor executor = AppUtils.getExecutor();

    private Handler handler = new Handler(this);

    private static class Handler extends AbstractStaticHandler<CartFragment> {

        private Handler(CartFragment context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg, CartFragment fragment) {
            switch (msg.what) {
                case WHAT_CART:
                    String json = String.valueOf(msg.obj);
                    try {
                        if (orderCarts!=null&&orderCarts.size()>0){
                            orderCarts.clear();
                        }
                        orderCarts = OrderCartService.getCarts(json);
                        if (orderCarts != null) {
                            fragment.showCart(orderCarts);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case WHAT_DELETE:
                    String del = String.valueOf(msg.obj);
                    try {
                        JSONObject object = new JSONObject(del);
                        String s = object.getString("result");
                        if ("S".equals(s)) {
                            fragment.show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case WHAT_COUNT:
                    try {
                        String count = String.valueOf(msg.obj);
                        JSONObject object = new JSONObject(count);
                        boolean flag = object.getBoolean("flag");
                        if (flag) {
                            fragment.clickOrderCarts.clear();
                            fragment.ivCheckAll.setImageResource(R.drawable.no_tick);
                            fragment.clicks = false;
                            fragment.show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                        break;
                default:
                    break;
            }
        }

    }

    private void showCart(List<OrderCart> orderCarts) {
        lvAdapter = new GenericAdapter<OrderCart>(getContext(), R.layout.show_cart_item, orderCarts) {
            @Override
            public void populate(ViewHolder holder, OrderCart orderCart) {
                holder.setTextView(R.id.show_cart_name, orderCart.getProduct().getPname())
                        .setTextView(R.id.show_cart_price, String.valueOf(orderCart.getTotal()))
                        .setTextView(R.id.show_cart_count, String.valueOf(orderCart.getCount()));
                ImageView pImage = holder.getView(R.id.show_cart_imgCover);
                Picasso.get().load(ApiConstants.URL_API + orderCart.getProduct().getpImage().get(0).getImage()).into(pImage);
                ImageView icTick = holder.getView(R.id.show_cart_tick);
                if (clickOrderCarts.contains(orderCart)) {
                    icTick.setImageResource(R.drawable.tick);
                } else {
                    icTick.setImageResource(R.drawable.no_tick);
                }
                icTick.setOnClickListener(view -> {
                    Double total = 0.0;
                    if (clickOrderCarts.contains(orderCart)) {
                        clickOrderCarts.remove(orderCart);
                        clicks = false;
                        ivCheckAll.setImageResource(R.drawable.no_tick);
                    } else {
                        clickOrderCarts.add(orderCart);
                        if (clickOrderCarts.size() == orderCarts.size()) {
                            clicks = true;

                            ivCheckAll.setImageResource(R.drawable.tick);
                        } else {
                            ivCheckAll.setImageResource(R.drawable.no_tick);
                        }
                    }
                    for (OrderCart o : clickOrderCarts) {
                        total += o.getTotal();
                    }
                    tvTotal.setText("总价：".concat(String.valueOf(total)));
                    lvAdapter.notifyDataSetChanged();
                });
                TextView btn1 = holder.getView(R.id.show_cart_count_);
                btn1.setOnClickListener(view -> {
                    decrease(orderCart);
                });
                TextView btn2 = holder.getView(R.id.show_cart_count_add);
                btn2.setOnClickListener(view -> {
                    addCount(orderCart);
                });
            }



            @Override
            public boolean persistInsert(OrderCart orderCart) {
                return false;
            }

            @Override
            public boolean persistDelete(OrderCart orderCart) {
                return false;
            }
        };
        lv.setAdapter(lvAdapter);
        lv.setOnItemClickListener((adapterView, view, i, l) -> {
            OrderCart orderCart = lvAdapter.getItem(i);
            listener.onGotoBuyActivity(orderCart.getProduct());
        });
        checkAll();

    }

    private void addCount(OrderCart orderCart) {
        executor.execute(()->{
            try {
                String json = OrderCartService.updateCount(orderCart.getCount()+1,orderCart.getId(),orderCart.getProduct().getPid());
                handler.sendMessage(handler.obtainMessage(WHAT_COUNT,json));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void decrease(OrderCart orderCart) {
        if (orderCart.getCount()>1){
            executor.execute(()->{
                try {
                    String json = OrderCartService.updateCount(orderCart.getCount()-1,orderCart.getId(),orderCart.getProduct().getPid());
                    handler.sendMessage(handler.obtainMessage(WHAT_COUNT,json));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

    }
    @Override
    protected void populate() {
        notification();
        initView();
        show();
    }
    private void show() {
        user = AppUtils.getUser();
        if (user != null) {
            executor.execute(() -> {
                try {
                    String json = OrderCartService.getCartJson(user.getUid());
                    handler.sendMessage(handler.obtainMessage(WHAT_CART, json));
                } catch (Exception e) {
                    handler.sendMessage(handler.obtainMessage(WHAT_C_E, e.getMessage()));
                }
            });
        }
    }

    private void checkAll() {
        checkAllLayout.setOnClickListener(view -> {
            clickOrderCarts.clear();
            Double total = 0.0;
            if (!clicks) {
                clickOrderCarts.addAll(orderCarts);
                for (OrderCart o : clickOrderCarts) {
                    total += o.getTotal();
                }
                clicks = true;
                ivCheckAll.setImageResource(R.drawable.tick);
            } else {
                ivCheckAll.setImageResource(R.drawable.no_tick);
                total = 0.0;
                clicks = false;
            }
            tvTotal.setText("总价：".concat(String.valueOf(total)));
            lvAdapter.notifyDataSetChanged();
        });

        tvClose.setOnClickListener(view -> {
            if (!clickOrderCarts.isEmpty()) {
                listener.onToSubmitActivity(clickOrderCarts);

            }
        });
    }
    private void initView() {
        lv = findViewById(R.id.fragment_cart_lv);
        ivCheckAll = findViewById(R.id.fragment_cart_check_all);
        tvTotal = findViewById(R.id.fragment_cart_total);
        tvTotal.setText("总价：");
        tvClose = findViewById(R.id.fragment_cart_close);
        tvClose.setText("结算");
        checkAllLayout = findViewById(R.id.fragment_cart_clicks);

    }

    private   void  notification(){
        broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SubmitActivity.S);
        broadcastManager.registerReceiver(mAdDownLoadReceiver, intentFilter);
        IntentFilter filter = new IntentFilter();
        filter.addAction(OwnFragment.M);
        broadcastManager.registerReceiver(receiver, filter);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (orderCarts != null && orderCarts.size() > 0) {
                orderCarts.clear();
                lvAdapter.notifyDataSetChanged();
            }
        }
    };
    private BroadcastReceiver mAdDownLoadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            show();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        broadcastManager.unregisterReceiver(mAdDownLoadReceiver);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (isRefresh) {
                show();
                isRefresh = false;
            }
        }
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_cart;
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
         * @param
         */
        void onGotoBuyActivity(Product product);
        void onToSubmitActivity(List<OrderCart> orderCarts);
    }
}
