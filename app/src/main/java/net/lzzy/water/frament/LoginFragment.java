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
import net.lzzy.water.utils.UserCookies;

import java.util.ArrayList;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author 菜鸡
 */
public class LoginFragment extends BaseFragment {

    private static final int WHAT_LOGIN = 0;
    private static final int WHAT_L_EXCEPTION = 1;
    private OnGoToLogin listener;
    private String userPhone;
    private String userPassword;

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
                            //UserCookies.getInstance().insertUser(fragment.userPhone,fragment.userPassword);
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
          //  UserCookies.getInstance().clear();
            User json = new User();
           // String[] array = UserCookies.getInstance().getUser();
        /*    if (array!=null){
                json.setTelephone(array[0]);
                json.setPassword(array[1]);
            }else {*/
                userPhone = phone.getText().toString();
                userPassword = password.getText().toString();
                json.setTelephone(userPhone);
                json.setPassword(userPassword);
           // }
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
