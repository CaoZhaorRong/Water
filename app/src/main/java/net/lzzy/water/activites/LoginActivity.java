package net.lzzy.water.activites;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import net.lzzy.water.R;
import net.lzzy.water.frament.CartFragment;
import net.lzzy.water.frament.OwnFragment;
import net.lzzy.water.models.User;
import net.lzzy.water.network.UserService;
import net.lzzy.water.utils.AbstractStaticHandler;
import net.lzzy.water.utils.AppUtils;
import net.lzzy.water.utils.MyDialog;
import net.lzzy.water.utils.UserCookies;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author 菜鸡
 */
public class LoginActivity extends AppCompatActivity {
    public static final int WHAT_LOGIN = 0;
    public static final int WHAT_L_EXCEPTION = 1;
    public static final int RESULT_CODE_OK = 1;
    public static final String RESULT_USER = "result";
    private  String telephone;
    private  String password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.user_login);
        initView();
    }

    private void initView() {
            User json = new User();
            EditText phone = findViewById(R.id.user_login_phone);
            EditText pass = findViewById(R.id.user_login_password);
            Button btnLogin = findViewById(R.id.user_login);
            ImageView back = findViewById(R.id.user_login_back);
            back.setOnClickListener(view -> finish());
            btnLogin.setOnClickListener(view1 -> {
                telephone = phone.getText().toString();
                password = pass.getText().toString();
                if (TextUtils.isEmpty(telephone) || TextUtils.isEmpty(password)){
                    Toast.makeText(this,"电话号码或密码不可为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                json.setTelephone(telephone);
                json.setPassword(password);
                login(json);
            });
    }
    private void login(User json) {
        executor.execute(() -> {
            try {
                String data = UserService.getUserFromServer(json);
                handler.sendMessage(handler.obtainMessage(WHAT_LOGIN, data));
            } catch (Exception e) {
                handler.sendMessage(handler.obtainMessage(WHAT_L_EXCEPTION, e.getMessage()));
            }
        });
    }
    private ThreadPoolExecutor executor = AppUtils.getExecutor();
    private Handler handler = new Handler(this);
    private static class Handler extends AbstractStaticHandler<LoginActivity> {

        private Handler(LoginActivity context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg, LoginActivity activity) {
            switch (msg.what) {
                case WHAT_LOGIN:
                    String json = String.valueOf(msg.obj);
                    try {
                        User user = UserService.getUser(json);
                        if (user != null) {
                            AppUtils.setUser(user);
                            Intent intent1 = new Intent(SubmitActivity.S);
                            LocalBroadcastManager.getInstance(activity).sendBroadcast(intent1);
                            OwnFragment.newInstance();
                            UserCookies.getInstance().saveUser(activity.telephone,activity.password);
                            Intent intent = new Intent();
                            intent.putExtra(RESULT_USER, user);
                            activity.setResult(RESULT_CODE_OK, intent);
                            activity.finish();
                        }else {
                            Toast.makeText(activity, "登录失败，请重新登录！", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case WHAT_L_EXCEPTION:
                    Toast.makeText(activity, "登录异常，请重试！", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
