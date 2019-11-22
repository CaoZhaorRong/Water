package net.lzzy.water.frament;

import android.content.Context;
import android.net.Uri;

import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import net.lzzy.sqllib.GenericAdapter;
import net.lzzy.sqllib.ViewHolder;
import net.lzzy.water.R;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author 菜鸡
 */
public class CartFragment extends BaseFragment {

    private static final int WHAT_CART = 0;
    private static final int WHAT_C_E = 1;
    private static final int WHAT_SUBMIT = 2;
    private static final int WHAT_SUBMIT_EXCEPTION = 3;
    public static final int WHAT_DELETE = 4;
    public static final int WHAT_D_EXCEPTION = 5;
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
    private MyDialog submitDialog;
    private boolean isRefresh = true;
    private static boolean refresh = false;


    public CartFragment() {
    }

    public static void newInstance() {
        refresh = true;
    }

    public static void clearData() {
        if (orderCarts != null && orderCarts.size() > 0) {
            orderCarts.clear();
            lvAdapter.notifyDataSetChanged();
        }
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
                        orderCarts = OrderCartService.getCarts(json);
                        if (orderCarts != null) {
                            fragment.showCart(orderCarts);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case WHAT_SUBMIT:
                    int order = (int) msg.obj;
                    if (order >= 200 && order <= 300) {
                        fragment.submitDialog.cancel();
                        fragment.deleteCart();
                        Toast.makeText(fragment.getActivity(), "已下单，请耐心等待您的包裹！", Toast.LENGTH_SHORT).show();
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
                default:
                    break;
            }
        }

    }

    private void deleteCart() {
        for (OrderCart o : clickOrderCarts) {
            executor.execute(() -> {
                try {
                    String json = OrderCartService.deleteCart(o.getId());
                    handler.sendMessage(handler.obtainMessage(WHAT_DELETE, json));
                } catch (Exception e) {
                    handler.sendMessage(handler.obtainMessage(WHAT_D_EXCEPTION, e.getMessage()));
                }
            });
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
                TextView btn2 = holder.getView(R.id.show_cart_count_add);
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
        checkAll();

    }


    @Override
    protected void populate() {
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
                close();
            }

        });
    }

    private void submitOrder() {
        for (OrderCart o : clickOrderCarts) {
            Order order = new Order();
            order.setAddress("柳州职业技术学院");
            order.setName("曹兆荣");
            order.setState(1);
            order.setTelephone("18776504453");
            order.setCount(o.getCount());
            order.setUserId(user.getUid());
            Product product = new Product();
            product.setPid(o.getProduct().getPid());
            order.setProduct(product);
            executor.execute(() -> {
                try {
                    int code = OrderService.posOrderFromServer(order);
                    handler.sendMessage(handler.obtainMessage(WHAT_SUBMIT, code));
                } catch (Exception e) {
                    handler.sendMessage(handler.obtainMessage(WHAT_SUBMIT_EXCEPTION, e.getMessage()));
                }
            });
        }

    }

    private void close() {
        submitDialog = new MyDialog(getActivity());
        View view = getLayoutInflater().inflate(R.layout.submit_order, null);
        ListView lvClose = view.findViewById(R.id.close_submit);
        LinearLayout layout = view.findViewById(R.id.layout_close);
        layout.setVisibility(View.GONE);
        GenericAdapter<OrderCart> adapter = new GenericAdapter<OrderCart>(getActivity(), R.layout.close, clickOrderCarts) {
            @Override
            public void populate(ViewHolder holder, OrderCart orderCart) {
                holder.setTextView(R.id.submit_name, orderCart.getProduct().getPname());
                TextView tvName = holder.getView(R.id.submit_name);
                TextView tvPrice = holder.getView(R.id.submit_price);
                TextView tvCount = holder.getView(R.id.submit_count);
                tvCount.setText("数量×".concat(String.valueOf(orderCart.getCount())));
                tvName.setText(orderCart.getProduct().getPname());
                String p = "￥";
                tvPrice.setText(p.concat(String.valueOf(orderCart.getTotal())));
                ImageView image = holder.getView(R.id.submit_imgCover);
                Picasso.get().load(ApiConstants.URL_API + orderCart.getProduct().getpImage().get(0).getImage()).into(image);
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
        lvClose.setAdapter(adapter);
        ImageView icBack = view.findViewById(R.id.submit_back);
        TextView submitPrice = view.findViewById(R.id.submit_submit_price);
        Double total = 0.0;
        for (OrderCart o : clickOrderCarts) {
            total += o.getTotal();
        }
        submitPrice.setText("实付款：￥".concat(String.valueOf(total)));
        TextView tvSubmit = view.findViewById(R.id.submit_submit_order);
        icBack.setOnClickListener(view1 -> submitDialog.cancel());
        tvSubmit.setOnClickListener(view1 -> submitOrder());
        submitDialog.setContentView(view);
        submitDialog.show();

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


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (refresh) {
                refresh = false;
                isRefresh = true;
            }
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
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
