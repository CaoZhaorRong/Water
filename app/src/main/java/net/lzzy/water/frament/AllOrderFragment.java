package net.lzzy.water.frament;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.hjq.toast.ToastUtils;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author 菜鸡
 */
public class AllOrderFragment extends BaseFragment {

    private static final int WHAT_ORDER = 0;
    public static final int WHAT_DEL = 1;
    private EvaluateFragment.OnGoToEvaluateActivity listener;
    private LocalBroadcastManager broadcastManager;
    private ListView lv;
    private static final int WHAT_TAKE = 2;
    private User user;
    private GenericAdapter<Order> adapter;
    private Order evaluate;

    public AllOrderFragment() {
    }

    @Override
    protected void populate() {
        notification();
        intiView();
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
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        broadcastManager.unregisterReceiver(mAdDownLoadReceiver);
    }

    private ThreadPoolExecutor executor = AppUtils.getExecutor();

    private FragmentHandler handler = new FragmentHandler(this);

    private static class FragmentHandler extends AbstractStaticHandler<AllOrderFragment> {

        public static final String S = "S";

        public FragmentHandler(AllOrderFragment context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg, AllOrderFragment fragment) {
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
                case WHAT_DEL:
                    String json = String.valueOf(msg.obj);
                    try {
                        String s = ApiConstants.json(json,"result");
                        if (s.equals(S)){
                            fragment.getOrder();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case WHAT_TAKE:
                    String ss = String.valueOf(msg.obj);
                    try {
                        JSONObject object = new JSONObject(ss);
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
        adapter = new GenericAdapter<Order>(getActivity(), R.layout.all_item, orders) {
            @Override
            public void populate(ViewHolder holder, Order order) {
                holder.setTextView(R.id.fragment_name,order.getName())
                        .setTextView(R.id.fragment_price,String.valueOf(order.getProduct().getPrice()).concat("×")
                                .concat(String.valueOf(order.getCount())));
                ImageView imageView = holder.getView(R.id.fragment_imgCover);
                TextView tvBtn = holder.getView(R.id.all_btn);
                TextView tvDel = holder.getView(R.id.all_delete);
                tvBtn.setVisibility(View.GONE);
                Picasso.get().load(ApiConstants.URL_API + order.getProduct().getpImage().get(0).getImage()).into(imageView);
                if (order.getState() == 2){
                    tvBtn.setVisibility(View.VISIBLE);
                    tvBtn.setText("确认收货");
                    tvBtn.setOnClickListener(view -> {
                        take(order);
                    });
                }
                if (order.getState() == 3){
                    tvBtn.setVisibility(View.VISIBLE);
                    tvBtn.setText("立即评价");
                    tvBtn.setOnClickListener(view -> {
                        listener.onGoToEvaluateActivity(order);
                    });
                }
                tvDel.setOnClickListener(view -> {
                    new AlertDialog.Builder(getContext())
                            .setTitle("删除订单？")
                            .setMessage("")
                            .setNegativeButton("取消", (dialog, which) -> {
                            })
                            .setPositiveButton("删除", (dialog, which) -> {
                                executor.execute(()-> {
                                    try {
                                        String json = OrderService.delete(order.getOid());
                                        handler.sendMessage(handler.obtainMessage(WHAT_DEL, json));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                            }).show();
                });
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
                String json = OrderService.getOrderFromServer(user.getUid());
                handler.sendMessage(handler.obtainMessage(WHAT_ORDER, json));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void intiView() {
        lv = findViewById(R.id.all_iv);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_all;
    }

    @Override
    public void search(String kw) {

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
