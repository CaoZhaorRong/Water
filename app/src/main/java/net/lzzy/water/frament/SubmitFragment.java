package net.lzzy.water.frament;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import net.lzzy.water.R;
import net.lzzy.water.constants.ApiConstants;
import net.lzzy.water.models.LocationFactory;
import net.lzzy.water.models.Locations;
import net.lzzy.water.models.Order;
import net.lzzy.water.models.Product;
import net.lzzy.water.models.User;
import net.lzzy.water.network.OrderService;
import net.lzzy.water.utils.AbstractStaticHandler;
import net.lzzy.water.utils.AppUtils;
import net.lzzy.water.utils.UserCookies;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * @author 菜鸡
 */
public class SubmitFragment extends BaseFragment{


    private static final int WHAT_SUBMIT_EXCEPTION = 5;
    private static final int WHAT_SUBMIT = 4;
    private static final String PRODUCT = "product";
    private static final String COUNT = "count";
    private User user;
    private Product product;
    private int count;
private OnGoToFragment listener;
    private Order order;
    private Locations locations;

    public static SubmitFragment newInstance(Product product,int count) {
        //静态工厂方法
        SubmitFragment fragment = new SubmitFragment();
        Bundle args = new Bundle();
        args.putParcelable(PRODUCT,product);
        args.putInt(COUNT,count);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //读静态工厂方法
            product = getArguments().getParcelable(PRODUCT);
            count = getArguments().getInt(COUNT,1);

        }
    }

    private ThreadPoolExecutor executor = AppUtils.getExecutor();
    private Handler handler = new Handler(this);
    private static class Handler extends AbstractStaticHandler<SubmitFragment> {

        private Handler(SubmitFragment context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg, SubmitFragment fragment) {
            switch (msg.what){
                case WHAT_SUBMIT:
                    int order = (int) msg.obj;
                    if (order <= 200) {
                        fragment.listener.onOrderItemActivity(fragment.order);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void initView() {

        locations = LocationFactory.getLocation();
        ImageView icBack = findViewById(R.id.submit_back);
        ImageView image = findViewById(R.id.submit_imgCover);
        TextView tvName = findViewById(R.id.submit_name);
        TextView tvPrice = findViewById(R.id.submit_price);
        TextView submitPrice = findViewById(R.id.submit_submit_price);
        TextView tvSubmit = findViewById(R.id.submit_submit_order);
        LinearLayout tvAdd = findViewById(R.id.add_new_address);
        LinearLayout address = findViewById(R.id.layout_location);
        TextView tvSize = findViewById(R.id.submit_size);
        TextView tvCount = findViewById(R.id.submit_count);
        TextView lName = findViewById(R.id.location_name);
        TextView lPhone = findViewById(R.id.location_phone);
        TextView lLocation = findViewById(R.id.location_location);
        if (locations!=null){
            tvAdd.setVisibility(View.GONE);
            address.setVisibility(View.VISIBLE);
            lName.setText(locations.getName());
            lPhone.setText(locations.getTelephone());
            lLocation.setText(locations.getAddress());
        }else {
            tvAdd.setVisibility(View.VISIBLE);
            address.setVisibility(View.GONE);
            tvAdd.setOnClickListener(view1 -> listener.onGoToFragment());
        }
        icBack.setOnClickListener(view -> getActivity().onBackPressed());

        Picasso.get().load(ApiConstants.URL_API + product.getpImage().get(0).getImage()).into(image);
        tvName.setText(product.getPname());
        tvPrice.setText(String.valueOf(product.getPrice()));
        tvCount.setText("数量×".concat(String.valueOf(count)));
        submitPrice.setText("实付款：￥".concat(String.valueOf(product.getPrice()*count)));
        tvSubmit.setOnClickListener(view1 ->{
            if (locations==null){
                Toast.makeText(getActivity(),"添加地址",Toast.LENGTH_SHORT).show();
            }else {
                new AlertDialog.Builder(getActivity())
                        .setMessage("收银台")
                        .setNegativeButton("取消", (dialog, which) -> {
                        })
                        .setPositiveButton("支付 ￥"+product.getPrice()*count, (dialog, which) -> {
                            submitOrder();
                        })
                        .setCancelable(false)
                        .show();
            }
            }
           );


    }

    private void order(){
        if (locations!=null){
            order = new Order();
            order.setAddress(locations.getAddress());
            order.setCount(count);
            order.setName(locations.getName());
            order.setState(1);
            order.setTelephone(locations.getTelephone());
            order.setUserId(user.getUid());
            product.setPid(product.getPid());
            order.setProduct(product);
            order.setTotal(product.getPrice()*count);
            Date date = new Date();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMss");
            StringBuilder strRand= new StringBuilder();
            for(int i=0;i<7;i++){
                strRand.append((int) (Math.random() * 10));
            }
            String serial= dateFormat.format(date)+strRand.toString();
            order.setSerial(serial);
        }
    }
    private void submitOrder() {
        executor.execute(() -> {
            try {
                int code = OrderService.posOrderFromServer(order);
                handler.sendMessage(handler.obtainMessage(WHAT_SUBMIT, code));
            } catch (Exception e) {
                handler.sendMessage(handler.obtainMessage(WHAT_SUBMIT_EXCEPTION, e.getMessage()));
            }
        });
    }

    @Override
    protected void populate() {
        user = AppUtils.getUser();
        initView();
        order();
    }

    @Override
    public int getLayout() {
        return R.layout.submit_order;
    }

    @Override
    public void search(String kw) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnGoToFragment) {
            listener = (OnGoToFragment) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnGoToFragment {
        void onGoToFragment();
        void onOrderItemActivity(Order order);
    }

}
