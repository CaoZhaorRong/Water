package net.lzzy.water.frament;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.widget.ImageView;
import android.widget.ListView;


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

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * @author 菜鸡
 */
public class EvaluateFragment extends BaseFragment {


    public EvaluateFragment() {
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

    private static class FragmentHandler extends AbstractStaticHandler<EvaluateFragment> {


        private FragmentHandler(EvaluateFragment context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg, EvaluateFragment fragment) {
            switch (msg.what) {
                case WHAT_ORDER:
                    String data = String.valueOf(msg.obj);
                    try {
                        List<Order> orders = OrderService.getOrders(data);
                          fragment.show(orders);
                    } catch (Exception e) {
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
                holder.setTextView(R.id.fragment_name,order.getName())
                        .setTextView(R.id.fragment_price,String.valueOf(order.getProduct().getPrice()).concat("×")
                                .concat(String.valueOf(order.getCount())));
                ImageView imageView = holder.getView(R.id.fragment_imgCover);
                Picasso.get().load(ApiConstants.URL_API + order.getProduct().getpImage().get(0).getImage()).into(imageView);
            }

            @Override
            public boolean persistInsert(Order order) {
                return false;
            }

            @Override
            public boolean persistDelete(Order order) {
                return false;
            }
        };
        lv.setAdapter(adapter);
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            user = AppUtils.getUser();
            if (user != null) {
                executor.execute(this::getOrder);
            }

        }
    }

    private void getOrder() {
        try {
            String json = OrderService.getState(user.getUid(), 3);
            handler.sendMessage(handler.obtainMessage(WHAT_ORDER, json));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void intiView() {
        lv = findViewById(R.id.evaluate_iv);
    }


    @Override
    public int getLayout() {
        return R.layout.fragment_evaluate;
    }

    @Override
    public void search(String kw) {

    }


}
