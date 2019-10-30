package net.lzzy.water.frament;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.lzzy.water.R;
import net.lzzy.water.models.Order;
import net.lzzy.water.models.User;
import net.lzzy.water.network.OrderService;
import net.lzzy.water.network.ProductService;
import net.lzzy.water.network.UserService;
import net.lzzy.water.utils.AbstractStaticHandler;
import net.lzzy.water.utils.AppUtils;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;


public class OwnFragment extends BaseFragment {
    public static final int WHAT_A = 1;
    public static final int WHAT_B = 2;
    public static final int WHAT_C = 3;
    public static final int WHAT_D = 4;
    public static final int WHAT_E = 5;
    public static final int WHAT_O_EXCEPTION = 8;
    private  User user;
    private OnGoToLogin listener;
    private ImageView ivHead;
    private TextView tvName;
    private LinearLayout viewFavorite;
    private LinearLayout viewOrder;
    private FrameLayout view1;
    private FrameLayout view2;
    private FrameLayout view3;
    private FrameLayout view4;
    private FrameLayout view5;
    private TextView tvCount1;
    private TextView tvCount2;
    private TextView tvCount3;
    private TextView tvCount4;
    private TextView tvCount5;

    public OwnFragment() {
    }


    private ThreadPoolExecutor executor = AppUtils.getExecutor();

    private FragmentHandler handler = new FragmentHandler(this);

    private static class FragmentHandler extends AbstractStaticHandler<OwnFragment> {

        private FragmentHandler(OwnFragment context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg, OwnFragment fragment) {
            switch (msg.what){
                case  WHAT_A:
                    String json1 = String.valueOf(msg.obj);
                    try {
                        List<Order> o1 = OrderService.getOrders(json1);
                        fragment.tvCount1.setText(o1.size());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
               /* case  WHAT_B:
                    String json2 = String.valueOf(msg.obj);
                    try {
                       List<Order> o2 = OrderService.getOrders(json2);
                        fragment.tvCount2.setText(o2.size());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case  WHAT_C:
                    String json3 = String.valueOf(msg.obj);
                    try {
                        List<Order> o3 = OrderService.getOrders(json3);
                        fragment.tvCount3.setText(o3.size());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case  WHAT_D:
                    String json4 = String.valueOf(msg.obj);
                    try {
                        List<Order> o4 = OrderService.getOrders(json4);
                        fragment.tvCount4.setText(o4.size());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case  WHAT_E:
                    String json5 = String.valueOf(msg.obj);
                    try {
                        List<Order> o5 = OrderService.getOrders(json5);
                        fragment.tvCount5.setText(o5.size());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;*/
                default:
                    break;
            }
        }
    }

    @Override
    protected void populate() {
        user = AppUtils.getUser();
        intiView();
            //在执行 show() 和 hide() 方法的时候，会回调一个方法
        if (user!=null){
            tvName.setText(user.getUsername());
            executor.execute(() ->{
                try {
                    String json1 = OrderService.getOrderFromServer(0);
                   /* String json2 = OrderService.getOrderFromServer(1);
                    String json3 = OrderService.getOrderFromServer(2);
                    String json4 = OrderService.getOrderFromServer(3);
                    String json5 = OrderService.getOrderFromServer(4);*/
                    handler.sendMessage(handler.obtainMessage(WHAT_A, json1));
                /*    handler.sendMessage(handler.obtainMessage(WHAT_B, json2));
                    handler.sendMessage(handler.obtainMessage(WHAT_C, json3));
                    handler.sendMessage(handler.obtainMessage(WHAT_D, json4));
                    handler.sendMessage(handler.obtainMessage(WHAT_E, json5));*/
                } catch (Exception e) {
                    handler.sendMessage(handler.obtainMessage(WHAT_O_EXCEPTION, e.getMessage()));
                }
            });
        }
    }

    private void intiView() {
        ivHead = findViewById(R.id.own_view_head);
        tvName = findViewById(R.id.own_view_name);
        viewFavorite = findViewById(R.id.own_view_favorite);
        viewOrder = findViewById(R.id.own_view_order);
        view1 = findViewById(R.id.own_view_a);
        view2 = findViewById(R.id.own_view_b);
        view3 = findViewById(R.id.own_view_c);
        view4 = findViewById(R.id.own_view_d);
        view5 = findViewById(R.id.own_view_e);
        tvCount1 = findViewById(R.id.own_view_tvCount1);
        tvCount2 = findViewById(R.id.own_view_tvCount2);
        tvCount3 = findViewById(R.id.own_view_tvCount3);
        tvCount4 = findViewById(R.id.own_view_tvCount4);
        tvCount5 = findViewById(R.id.own_view_tvCount5);

    }


    @Override
    public int getLayout() {
        return R.layout.fragment_own;
    }

    @Override
    public void search(String kw) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnGoToLogin) {
            listener = (OnGoToLogin) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnGoToLogin {
        void OnGoToLogin(User user);
    }
}
