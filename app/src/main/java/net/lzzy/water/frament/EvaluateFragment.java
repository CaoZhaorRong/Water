package net.lzzy.water.frament;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.squareup.picasso.Picasso;

import net.lzzy.sqllib.GenericAdapter;
import net.lzzy.sqllib.ViewHolder;
import net.lzzy.water.R;
import net.lzzy.water.activites.EvaluateActivity;
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

    private  OnGoToEvaluateActivity listener;
    private LocalBroadcastManager broadcastManager;
    public EvaluateFragment() {
    }

    @Override
    protected void populate() {
        notification();
        intiView();
    }
    private static final int WHAT_ORDER = 0;
    private User user;
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
                        if (orders!=null&&orders.size()>0){
                            fragment.show(orders);
                        }
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
        GenericAdapter<Order> adapter = new GenericAdapter<Order>(getActivity(), R.layout.evaluate_item, orders) {
            @Override
            public void populate(ViewHolder holder, Order order) {
                holder.setTextView(R.id.fragment_name, order.getName())
                        .setTextView(R.id.fragment_price, String.valueOf(order.getProduct().getPrice()));
                ImageView imageView = holder.getView(R.id.fragment_imgCover);
                Picasso.get().load(ApiConstants.URL_API + order.getProduct().getpImage().get(0).getImage()).into(imageView);
                TextView tvBtn = holder.getView(R.id.fragment_btn);
                tvBtn.setOnClickListener(view -> listener.onGoToEvaluateActivity(order));
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
                executor.execute(this::getOrder);

        }
    }
    private void getOrder() {
        user = AppUtils.getUser();
        if (user != null) {
            try {
                String json = OrderService.getState(user.getUid(), 3);
                handler.sendMessage(handler.obtainMessage(WHAT_ORDER, json));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void intiView() {
        lv = findViewById(R.id.evaluate_iv);
    }


    private   void  notification(){
        broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(EvaluateActivity.ACTION_EVALUATE);
        broadcastManager.registerReceiver(mAdDownLoadReceiver, intentFilter);
    }
    private BroadcastReceiver mAdDownLoadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getOrder();
            //这里接收到广播和数据，进行处理就是了
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        broadcastManager.unregisterReceiver(mAdDownLoadReceiver);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_evaluate;
    }

    @Override
    public void search(String kw) {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnGoToEvaluateActivity) {
            listener = (OnGoToEvaluateActivity) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
    public interface OnGoToEvaluateActivity {
        /**
         * order
         */
        void onGoToEvaluateActivity(Order order);
    }

}
