package net.lzzy.water.frament;


import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.hjq.toast.ToastUtils;
import com.squareup.picasso.Picasso;

import net.lzzy.sqllib.GenericAdapter;
import net.lzzy.sqllib.ViewHolder;
import net.lzzy.water.R;
import net.lzzy.water.constants.ApiConstants;
import net.lzzy.water.models.Order;
import net.lzzy.water.models.User;
import net.lzzy.water.network.OrderService;
import net.lzzy.water.utils.AbstractStaticHandler;
import net.lzzy.water.utils.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author 菜鸡
 */
public class TakeFragment extends BaseFragment {

    private EvaluateFragment.OnGoToEvaluateActivity listener;
    private static final int WHAT_TAKE = 1;
    private Order evaluate;
    public TakeFragment() {
    }
    @Override
    protected void populate() {
        intiView();

    }

    private static final int WHAT_ORDER = 0;
    private User user;
    private GenericAdapter<Order> adapter;
    private ListView lv;
    private ThreadPoolExecutor executor = AppUtils.getExecutor();
    private FragmentHandler handler = new FragmentHandler(this);

    private static class FragmentHandler extends AbstractStaticHandler<TakeFragment> {

        private FragmentHandler(TakeFragment context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg, TakeFragment fragment) {
            switch (msg.what) {
                case WHAT_ORDER:
                    String data = String.valueOf(msg.obj);
                    try {
                        List<Order> orders = OrderService.getOrders(data);
                        if (orders != null && orders.size() > 0) {
                            fragment.show(orders);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case WHAT_TAKE:
                    String json = String.valueOf(msg.obj);
                    try {
                        JSONObject object = new JSONObject(json);
                        boolean flag = object.getBoolean("flag");
                        if (flag){
                            ToastUtils.show("收货成功");
                            fragment.getOrder();
                            fragment.listener.onGoToEvaluateActivity(fragment.evaluate);
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

    private void show(List<Order> orders) {
        adapter = new GenericAdapter<Order>(getActivity(), R.layout.evaluate_item, orders) {
            @Override
            public void populate(ViewHolder holder, Order order) {
                holder.setTextView(R.id.fragment_name, order.getName())
                        .setTextView(R.id.fragment_price, String.valueOf(order.getProduct().getPrice()).concat("×")
                                .concat(String.valueOf(order.getCount())));
                ImageView imageView = holder.getView(R.id.fragment_imgCover);
                Picasso.get().load(ApiConstants.URL_API + order.getProduct().getpImage().get(0).getImage()).into(imageView);
                TextView tvHint = holder.getView(R.id.fragment_hint);
                tvHint.setText("等待收货");
                TextView tvObligation = holder.getView(R.id.fragment_btn);
                tvObligation.setText("确认收货");
                tvObligation.setOnClickListener(view -> {
                    take(order);
                });
            }
            @Override
            public boolean persistInsert (Order order){
                return false;
            }

            @Override
            public boolean persistDelete (Order order){
                return false;
            }
        }

        ;
        lv.setAdapter(adapter);
    }

    private void take(Order order) {
            executor.execute(()-> {
                try {
                    String json = OrderService.updateState(order.getOid(),3);
                    evaluate = order;
                    handler.sendMessage(handler.obtainMessage(WHAT_TAKE, json));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
    }

    @Override
    public void setUserVisibleHint ( boolean isVisibleToUser){
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
                executor.execute(this::getOrder);
        }
    }

    private void getOrder () {
        user = AppUtils.getUser();
        if (user != null) {
            try {
                String json = OrderService.getState(user.getUid(), 2);
                handler.sendMessage(handler.obtainMessage(WHAT_ORDER, json));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void intiView () {
        lv = findViewById(R.id.take_iv);
    }

    @Override
    public int getLayout () {
        return R.layout.fragment_take;
    }

    @Override
    public void search (String kw){

    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof EvaluateFragment.OnGoToEvaluateActivity) {
            listener = (EvaluateFragment.OnGoToEvaluateActivity) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

}

