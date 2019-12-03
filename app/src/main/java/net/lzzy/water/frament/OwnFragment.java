package net.lzzy.water.frament;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.load.engine.Resource;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import net.lzzy.water.R;
import net.lzzy.water.activites.OrderActivity;
import net.lzzy.water.constants.ApiConstants;
import net.lzzy.water.models.Order;
import net.lzzy.water.models.User;
import net.lzzy.water.network.OrderService;
import net.lzzy.water.utils.AbstractStaticHandler;
import net.lzzy.water.utils.AppUtils;
import net.lzzy.water.utils.UserCookies;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * @author 菜鸡
 */
public class OwnFragment extends BaseFragment {
    public static final int WHAT_COUNT1 = 1;
    public static final int WHAT_COUNT0 = 0;
    public static final int WHAT_COUNT2 = 2;
    public static final int WHAT_COUNT3 = 3;
    public static final int WHAT_COUNT4 = 4;
    public static final String M = "M";
    private User user;
    private OnBack listener;
    private OnGoToOrderActivity toListener;
    private RoundedImageView ivHead;
    private TextView tvName;
    private LinearLayout layoutOrder;
    private LinearLayout setting;
    private TextView count1;
    private TextView count2;
    private TextView count3;
    private TextView count4;
    private TextView count5;
    private FrameLayout tab1;
    private FrameLayout tab2;
    private FrameLayout tab3;
    private FrameLayout tab4;
    private LocalBroadcastManager broadcastManager;
    private Dialog dialog;

    public OwnFragment() {
    }

    public static void newInstance() {
    }

    private ThreadPoolExecutor executor = AppUtils.getExecutor();

    private FragmentHandler handler = new FragmentHandler(this);

    private static class FragmentHandler extends AbstractStaticHandler<OwnFragment> {

        private FragmentHandler(OwnFragment context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg, OwnFragment fragment) {
            switch (msg.what) {
                case WHAT_COUNT0:
                    try {
                        JSONObject object = new JSONObject(String.valueOf(msg.obj));
                        String count = object.getString("count");
                        if ("0".equals(count)) {
                            fragment.count1.setVisibility(View.GONE);
                            fragment.count1.setText("");
                        } else {
                            fragment.count1.setText(count);
                            fragment.count1.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case WHAT_COUNT1:
                    try {
                        JSONObject object = new JSONObject(String.valueOf(msg.obj));
                        String count1 = object.getString("count");
                        if ("0".equals(count1)) {
                            fragment.count2.setVisibility(View.GONE);
                            fragment.count2.setText("");
                        } else {
                            fragment.count2.setText(count1);
                            fragment.count2.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case WHAT_COUNT2:
                    try {
                        JSONObject object = new JSONObject(String.valueOf(msg.obj));
                        String count2 = object.getString("count");
                        if ("0".equals(count2)) {
                            fragment.count3.setVisibility(View.GONE);
                            fragment.count3.setText("");
                        } else {
                            fragment.count3.setText(count2);
                            fragment.count3.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case WHAT_COUNT3:
                    try {
                        JSONObject object = new JSONObject(String.valueOf(msg.obj));
                        String count3 = object.getString("count");
                        if ("0".equals(count3)) {
                            fragment.count4.setVisibility(View.GONE);
                            fragment.count4.setText("");
                        } else {
                            fragment.count4.setText(count3);
                            fragment.count4.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case WHAT_COUNT4:
                    try {
                        String s = String.valueOf(msg.obj);
                        JSONObject object = new JSONObject(s);
                        String count4 = object.getString("count");
                        if ("0".equals(count4)) {
                            fragment.count5.setVisibility(View.GONE);
                            fragment.count5.setText("");
                        } else {
                            fragment.count5.setText(count4);
                            fragment.count5.setVisibility(View.VISIBLE);
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

    private void setting() {
        setting.setOnClickListener(view -> {
           /* //创建弹出式菜单对象（最低版本11）
            PopupMenu popup = new PopupMenu(Objects.requireNonNull(getContext()), view);//第二个参数是绑定的那个view
            //获取菜单填充器
            MenuInflater inflater = popup.getMenuInflater();
            //填充菜单
            inflater.inflate(R.menu.menu_search, popup.getMenu());
            //绑定菜单项的点击事件
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_exit) {
                    back();
                }
                return false;
            });
            //显示(这一行代码不要忘记了)
            popup.show();*/
            View view1 = getLayoutInflater().inflate(R.layout.exit, null);
            dialog = new Dialog(getContext(), R.style.style_dialog);
            dialog.setContentView(view1);
            dialog.show();
            TextView exit = view1.findViewById(R.id.action_exit);
            TextView dismiss = view1.findViewById(R.id.action_dismiss);
            exit.setOnClickListener(view2 -> back() );
            dismiss.setOnClickListener(view2 -> dialog.dismiss());
            Window window = dialog.getWindow();
            assert window != null;
            window.setGravity(Gravity.BOTTOM);
            window.setWindowAnimations(R.style.BottomDialog_Animation);
           // window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
        });
    }

    @Override
    protected void populate() {

        notification();
        intiView();
    }



    private void show() {
        user = AppUtils.getUser();
        if (user != null) {
            tvName.setText(user.getUsername());
            Picasso.get().load(ApiConstants.URL_API + user.getHeadImage()).into(ivHead);
            executor.execute(this::getCount0);
            executor.execute(this::getCount1);
            executor.execute(this::getCount2);
            executor.execute(this::getCount3);
            executor.execute(this::getCount4);
        }
    }

    private void getCount0() {
        try {
            String json = OrderService.getCount(user.getUid(), 0);
            handler.sendMessage(handler.obtainMessage(WHAT_COUNT0, json));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getCount1() {
        try {
            String json = OrderService.getCount(user.getUid(), 1);
            handler.sendMessage(handler.obtainMessage(WHAT_COUNT1, json));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getCount2() {
        try {
            String json = OrderService.getCount(user.getUid(), 2);
            handler.sendMessage(handler.obtainMessage(WHAT_COUNT2, json));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getCount3() {
        try {
            String json = OrderService.getCount(user.getUid(), 3);
            handler.sendMessage(handler.obtainMessage(WHAT_COUNT3, json));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getCount4() {
        try {
            String json = OrderService.getCount(user.getUid(), 4);
            handler.sendMessage(handler.obtainMessage(WHAT_COUNT4, json));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void intiView() {
        ivHead = findViewById(R.id.own_view_head);
        tvName = findViewById(R.id.own_view_name);
        setting = findViewById(R.id.own_view_setting);
        layoutOrder = findViewById(R.id.own_view_order);
        count1 = findViewById(R.id.own_view_tvCount1);
        count2 = findViewById(R.id.own_view_tvCount2);
        count3 = findViewById(R.id.own_view_tvCount3);
        count4 = findViewById(R.id.own_view_tvCount4);
        count5 = findViewById(R.id.own_view_tvCount5);
        tab1 = findViewById(R.id.own_view_tab1);
        tab2 = findViewById(R.id.own_view_tab2);
        tab3 = findViewById(R.id.own_view_tab3);
        tab4 = findViewById(R.id.own_view_tab4);
        FrameLayout tab5 = findViewById(R.id.own_view_tab5);
        LinearLayout location = findViewById(R.id.own_view_location);
        location.setOnClickListener(view -> {
           toListener.onGoToLocationActivity();
        });
        view();
    }

    private void back() {
        dialog.dismiss();
        try {
            AppUtils.clearUser();
            count1.setVisibility(View.GONE);
            count2.setVisibility(View.GONE);
            count3.setVisibility(View.GONE);
            count4.setVisibility(View.GONE);
            count5.setVisibility(View.GONE);
            tvName.setText("请登录");
            ivHead.setImageResource(R.drawable.no_login);
            show();
            UserCookies.getInstance().clear();
            Intent intent = new Intent(M);
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
            listener.onBack();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            show();
            setting();
        }
    }

    private void view() {
        layoutOrder.setOnClickListener(view -> toListener.onGoToOrderActivity(0));
        tab1.setOnClickListener(view -> toListener.onGoToOrderActivity(1));
        tab2.setOnClickListener(view -> toListener.onGoToOrderActivity(2));
        tab3.setOnClickListener(view -> toListener.onGoToOrderActivity(3));
        tab4.setOnClickListener(view -> toListener.onGoToOrderActivity(4));

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
        if (context instanceof OnBack) {
            listener = (OnBack) context;
        }
        if (context instanceof OnGoToOrderActivity) {
            toListener = (OnGoToOrderActivity) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        toListener = null;
    }

    private   void  notification(){
        broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(OrderActivity.AD_DOWNLOAD_ACTION);
        broadcastManager.registerReceiver(mAdDownLoadReceiver, intentFilter);

    }
    BroadcastReceiver mAdDownLoadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            show();
            //这里接收到广播和数据，进行处理就是了
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        broadcastManager.unregisterReceiver(mAdDownLoadReceiver);
    }

    public interface OnBack {
        /**
         * hh
         */
        void onBack();
    }

    public interface OnGoToOrderActivity {
        /**
         * index
         */
        void onGoToOrderActivity(int index);
        void  onGoToLocationActivity();
    }
}
