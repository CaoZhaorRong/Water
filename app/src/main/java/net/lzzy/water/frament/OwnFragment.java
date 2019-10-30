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

import net.lzzy.water.R;
import net.lzzy.water.models.User;
import net.lzzy.water.network.ProductService;
import net.lzzy.water.network.UserService;
import net.lzzy.water.utils.AbstractStaticHandler;
import net.lzzy.water.utils.AppUtils;

import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;


public class OwnFragment extends BaseFragment {
    private static final int WHAT_LOGIN = 0;
    private static final int WHAT_L_EXCEPTION = 1;
    private  User user;
    private OnGoToLogin listener;
    private Dialog dialog;

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
                case WHAT_LOGIN:
                    String json = String.valueOf(msg.obj);
                    try {
                        fragment.user = UserService.getUser(json);
                        AppUtils.setUser(fragment.user);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case  WHAT_L_EXCEPTION:

                default:
                    break;
            }
        }
    }

    @Override
    protected void populate() {
        user = AppUtils.getUser();
        if (user == null){
            getUser();
            //在执行 show() 和 hide() 方法的时候，会回调一个方法
        }
    }

    private void getUser(){
        dialog = new Dialog(Objects.requireNonNull(getContext()));
        View view = getLayoutInflater().inflate(R.layout.user_login, null);
        EditText phone = view.findViewById(R.id.user_login_phone);
        EditText password = view.findViewById(R.id.user_login_password);
        Button login = view.findViewById(R.id.user_login);
        login.setOnClickListener(view1 -> {
            User json = new User();
            json.setTelephone(phone.getText().toString());
            json.setPassword(password.getText().toString());
            executor.execute(() -> {
                try {
                    String data = UserService.getUserFromServer(json);
                    handler.sendMessage(handler.obtainMessage(WHAT_LOGIN, data));
                } catch (Exception e) {
                    handler.sendMessage(handler.obtainMessage(WHAT_L_EXCEPTION, e.getMessage()));
                }
            });
            dialog.cancel();
        });
        dialog.setContentView(view);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = getResources().getDisplayMetrics().widthPixels;
        layoutParams.height = getResources().getDisplayMetrics().heightPixels / 2;
        view.setLayoutParams(layoutParams);
        Objects.requireNonNull(dialog.getWindow()).setGravity(Gravity.BOTTOM);
        dialog.show();
    }


    @Override
    public int getLayout() {
        return R.layout.fragment_own;
    }

    @Override
    public void search(String kw) {

    }


    public static OwnFragment newInstance(String param1, String param2) {
        OwnFragment fragment = new OwnFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
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
