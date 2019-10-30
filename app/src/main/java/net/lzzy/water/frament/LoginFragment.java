package net.lzzy.water.frament;

import android.content.Context;
import android.os.Message;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import net.lzzy.water.R;
import net.lzzy.water.models.User;
import net.lzzy.water.network.UserService;
import net.lzzy.water.utils.AbstractStaticHandler;
import net.lzzy.water.utils.AppUtils;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author 菜鸡
 */
public class LoginFragment extends BaseFragment {

    private static final int WHAT_LOGIN = 0;
    private static final int WHAT_L_EXCEPTION = 1;
    private OnGoToLogin listener;

    public LoginFragment() {
    }

    private ThreadPoolExecutor executor = AppUtils.getExecutor();

    private FragmentHandler handler = new FragmentHandler(this);

    private static class FragmentHandler extends AbstractStaticHandler<LoginFragment> {

        private FragmentHandler(LoginFragment context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg, LoginFragment fragment) {
            switch (msg.what) {
                case WHAT_LOGIN:
                    String json = String.valueOf(msg.obj);
                    try {
                        User user = UserService.getUser(json);
                        if (user != null) {
                            AppUtils.setUser(user);
                            fragment.listener.OnGoToLogin();
                            Toast.makeText(fragment.getContext(), "登录成功", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(fragment.getContext(), "登录失败，请重新登录！", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case WHAT_L_EXCEPTION:
                    Toast.makeText(fragment.getContext(), "登录失败，请重新登录！", Toast.LENGTH_SHORT).show();
                default:
                    break;
            }
        }
    }

    @Override
    protected void populate() {
        getUser();
    }

    private void getUser() {

        EditText phone = findViewById(R.id.user_login_phone);
        EditText password = findViewById(R.id.user_login_password);
        Button login = findViewById(R.id.user_login);
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
        });
    }

    @Override
    public int getLayout() {
        return R.layout.user_login;
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
        void OnGoToLogin();
    }

}
