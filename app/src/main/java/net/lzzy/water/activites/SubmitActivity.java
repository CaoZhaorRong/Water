package net.lzzy.water.activites;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hengyi.wheelpicker.ppw.CityWheelPickerPopupWindow;
import com.hjq.toast.ToastUtils;
import com.squareup.picasso.Picasso;

import net.lzzy.sqllib.GenericAdapter;
import net.lzzy.sqllib.ViewHolder;
import net.lzzy.water.R;
import net.lzzy.water.constants.ApiConstants;
import net.lzzy.water.frament.AddLocationFragment;
import net.lzzy.water.frament.SubmitFragment;
import net.lzzy.water.models.LocationFactory;
import net.lzzy.water.models.Locations;
import net.lzzy.water.models.Order;
import net.lzzy.water.models.OrderCart;
import net.lzzy.water.models.Product;
import net.lzzy.water.models.User;
import net.lzzy.water.network.OrderService;
import net.lzzy.water.utils.AbstractStaticHandler;
import net.lzzy.water.utils.AppUtils;
import net.lzzy.water.utils.MyDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import static net.lzzy.water.activites.BuyActivity.RESULT_COUNT;
import static net.lzzy.water.activites.BuyActivity.RESULT_P;
import static net.lzzy.water.activites.MainActivity.ORDER_CART;
import static net.lzzy.water.activites.MainActivity.RESULT_INDEX;

/**
 * @author 菜鸡
 */
public class SubmitActivity extends AppCompatActivity {
    public static final String ORDER_ITEM = "OrderItem";
    public static final int REQUEST_LOCATION_CODE = 2;
    public static final String PRESENT = "present";
    public static final int WHAT_SUBMIT_CART = 1;
    public static final int WHAT_SUBMIT_C_EXCEPTION = 0;
    public static final String S = "s";

    private Product product;
    private int count;

    private static final int WHAT_SUBMIT_EXCEPTION = 5;
    private static final int WHAT_SUBMIT = 4;
    private User user;
    private Order order22;
    private Locations locations;
    private ImageView image;
    private TextView tvName;
    private TextView tvPrice;
    private TextView submitPrice;
    private TextView tvSubmit;
    private LinearLayout tvAdd;
    private LinearLayout address;
    private TextView tvCount;
    private TextView lName;
    private TextView lPhone;
    private TextView lLocation;
    private List<OrderCart> orderCarts ;
    private ListView lvClose;
    private LinearLayout layout;
    private Double total = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.submit_order);
        user = AppUtils.getUser();
        initView();
        location();
        boolean cc = getIntent().getBooleanExtra(BuyActivity.CART,true);
        if (cc){
            product = getIntent().getParcelableExtra(RESULT_P);
            count = getIntent().getIntExtra(RESULT_COUNT, 1);
            submit();
        }else {
            orderCarts = getIntent().getParcelableArrayListExtra(ORDER_CART);
            close();
        }
    }

    private void close() {
        layout.setVisibility(View.GONE);
        GenericAdapter<OrderCart> adapter = new GenericAdapter<OrderCart>(this, R.layout.close, orderCarts) {
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
        for(OrderCart order:orderCarts){
             total += order.getTotal();
        }
        submitPrice.setText("实付款：￥".concat(String.valueOf(total)));
        tvSubmit.setOnClickListener(view -> {
            if (locations != null) {
                new AlertDialog.Builder(SubmitActivity.this)
                        .setMessage("收银台")
                        .setNegativeButton("取消", (dialog, which) -> {
                        })
                        .setPositiveButton("支付 ￥" + total, (dialog, which) -> {
                            submitCartOrder();
                        })
                        .setCancelable(false)
                        .show();
            } else {
                Toast.makeText(this, "添加地址", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitCartOrder() {
        JSONArray array = new JSONArray();
            for (OrderCart o : orderCarts) {

                Order cartOrder = new Order();
                cartOrder.setAddress(locations.getAddress());
                cartOrder.setName(locations.getName());
                cartOrder.setState(1);
                cartOrder.setTelephone(locations.getTelephone());
                cartOrder.setCount(o.getCount());
                cartOrder.setUserId(user.getUid());
                cartOrder.setProduct(o.getProduct());
                cartOrder.setTotal(o.getProduct().getPrice() * o.getCount());
                cartOrder.setCartId(o.getId());
                Date date = new Date();
                StringBuilder strRand = new StringBuilder();
                for (int i = 0; i < 7; i++) {
                    strRand.append((int) (Math.random() * 10));
                }
                String serial = date(date, "yyMMss") + strRand.toString();
                cartOrder.setSerial(serial);
                cartOrder.setOrdertime(date(date, "yyyy-MM-dd HH:mm:ss"));
                try {
                    array.put(cartOrder.toJson());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        try {
            executor.execute(() -> {
                try {
                    int code = OrderService.post(array);
                    handler.sendMessage(handler.obtainMessage(WHAT_SUBMIT_CART, code));
                } catch (Exception e) {
                    handler.sendMessage(handler.obtainMessage(WHAT_SUBMIT_C_EXCEPTION, e.getMessage()));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//region

    private ThreadPoolExecutor executor = AppUtils.getExecutor();
    private Handler handler = new Handler(this);
    private static class Handler extends AbstractStaticHandler<SubmitActivity> {

        private Handler(SubmitActivity context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg, SubmitActivity activity) {
            switch (msg.what) {
                case WHAT_SUBMIT:
                    int code = (int) msg.obj;
                    if (code <= 200) {
                        Intent intent = new Intent(activity, OrderItemActivity.class);
                        intent.putExtra(ORDER_ITEM, activity.order22);
                        activity.startActivity(intent);
                        activity.finish();
                    }
                    break;
                case WHAT_SUBMIT_CART:
                    ToastUtils.show("下单成功");
                    Intent intent = new Intent(S);
                    LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
                    Intent i=new Intent(activity, OrderActivity.class);
                    i.putExtra(RESULT_INDEX,2);
                    activity.startActivity(i);
                    activity.finish();
                        break;
                default:
                    break;
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            locations = data.getParcelableExtra(AddLocationFragment.RESULT_LOCATION);
            location();
        }
    }

    private void location(){
        if (locations == null) {
            tvAdd.setVisibility(View.VISIBLE);
            address.setVisibility(View.GONE);
            tvAdd.setOnClickListener(view1 -> {
                Intent intent = new Intent(this, LocationActivity.class);
                intent.putExtra(SubmitActivity.PRESENT,true);
                startActivityForResult(intent, REQUEST_LOCATION_CODE);
            });
        } else {
            tvAdd.setVisibility(View.GONE);
            address.setVisibility(View.VISIBLE);
            lName.setText(locations.getName());
            lPhone.setText(locations.getTelephone());
            lLocation.setText(locations.getAddress());
            address.setOnClickListener(view -> {
                Intent intent = new Intent(this, LocationActivity.class);
                startActivity(intent);
            });
        }
    }
    private void submit() {
        Picasso.get().load(ApiConstants.URL_API + product.getpImage().get(0).getImage()).into(image);
        tvName.setText(product.getPname());
        tvPrice.setText(String.valueOf(product.getPrice()));
        tvCount.setText("数量×".concat(String.valueOf(count)));
        submitPrice.setText("实付款：￥".concat(String.valueOf(product.getPrice() * count)));
        tvSubmit.setOnClickListener(view1 -> {
                    if (locations != null) {
                        new AlertDialog.Builder(SubmitActivity.this)
                                .setMessage("收银台")
                                .setNegativeButton("取消", (dialog, which) -> {
                                })
                                .setPositiveButton("支付 ￥" + product.getPrice() * count, (dialog, which) -> {
                                    order();
                                })
                                .setCancelable(false)
                                .show();
                    } else {
                        Toast.makeText(this, "添加地址", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void initView() {
        locations = LocationFactory.getLocation();
        ImageView icBack = findViewById(R.id.submit_back);
        image = findViewById(R.id.submit_imgCover);
        tvName = findViewById(R.id.submit_name);
        tvPrice = findViewById(R.id.submit_price);
        submitPrice = findViewById(R.id.submit_submit_price);
        tvSubmit = findViewById(R.id.submit_submit_order);
        tvAdd = findViewById(R.id.add_new_address);
        address = findViewById(R.id.submit_location);
        TextView tvSize = findViewById(R.id.submit_size);
        tvCount = findViewById(R.id.submit_count);
        lName = findViewById(R.id.location_name);
        lPhone = findViewById(R.id.location_phone);
        lLocation = findViewById(R.id.location_location);
        lvClose = findViewById(R.id.close_submit);
        layout = findViewById(R.id.layout_close);
        icBack.setOnClickListener(view -> onBackPressed());
    }

    private void order() {
        if (locations != null) {
            Order order = new Order();
            order.setAddress(locations.getAddress());
            order.setCount(count);
            order.setName(locations.getName());
            order.setState(1);
            order.setTelephone(locations.getTelephone());
            order.setUserId(user.getUid());
            product.setPid(product.getPid());
            order.setProduct(product);
            order.setTotal(product.getPrice() * count);
            Date date = new Date();
            StringBuilder strRand = new StringBuilder();
            for (int i = 0; i < 7; i++) {
                strRand.append((int) (Math.random() * 10));
            }
            String serial = date(date, "yyMMss") + strRand.toString();
            order.setSerial(serial);
            order.setOrdertime(date(date, "yyyy-MM-dd HH:mm:ss"));
            order22 = order;
            submitOrder();
        }
    }

    private String date(Date date, String str) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(str);
        return dateFormat.format(date);
    }

    private void submitOrder() {
        executor.execute(() -> {
            try {
                int code = OrderService.posOrderFromServer(order22);
                handler.sendMessage(handler.obtainMessage(WHAT_SUBMIT, code));
            } catch (Exception e) {
                handler.sendMessage(handler.obtainMessage(WHAT_SUBMIT_EXCEPTION, e.getMessage()));
            }
        });
    }
    //endregion
}
