package net.lzzy.water.activites;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import net.lzzy.water.R;
import net.lzzy.water.constants.ApiConstants;
import net.lzzy.water.models.Evaluate;
import net.lzzy.water.models.Order;
import net.lzzy.water.models.User;
import net.lzzy.water.network.ProductService;
import net.lzzy.water.utils.AbstractStaticHandler;
import net.lzzy.water.utils.AppUtils;

import java.util.concurrent.ThreadPoolExecutor;

import static net.lzzy.water.activites.OrderActivity.RESULT_O;

/**
 * @author 菜鸡
 */
public class EvaluateActivity extends AppCompatActivity {

    public static final int WHAT_E = 0;
    public static final String ACTION_EVALUATE = "1";
    private EditText editText;
    private Order order;
    private User user;

    private ThreadPoolExecutor executor = AppUtils.getExecutor();

    private Handler handler = new Handler(this);

    private static class Handler extends AbstractStaticHandler<EvaluateActivity> {

        private Handler(EvaluateActivity context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg, EvaluateActivity activity) {
            switch (msg.what) {
                case WHAT_E:
                    int code = (int) msg.obj;
                    if (code >= 200 && code <= 300) {
                        Intent intent = new Intent(ACTION_EVALUATE);
                        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
                        new AlertDialog.Builder(activity)
                                .setTitle("发布成功")
                                .setMessage("")
                                .setNegativeButton("返回", (dialog, which) -> {
                                    activity.onBackPressed();
                                })
                                .setPositiveButton("", (dialog, which) -> {
                                })
                                .setCancelable(false)
                                .show();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_evaluate);
        order = getIntent().getParcelableExtra(RESULT_O);
        user = AppUtils.getUser();
        initView();

    }


    private void initView() {
        ImageView back = findViewById(R.id.activity_back);
        back.setOnClickListener(view -> onBackPressed());
        TextView tvPublish = findViewById(R.id.activity_e_publish);
        ImageView imgCover = findViewById(R.id.activity_e_imgCover);
        Picasso.get().load(ApiConstants.URL_API + order.getProduct().getpImage().get(0).getImage()).into(imgCover);
        TextView tvName = findViewById(R.id.activity_e_name);
        tvName.setText(order.getProduct().getPname());
        editText = findViewById(R.id.activity_e_edit);
        tvPublish.setOnClickListener(view -> {
            String port = editText.getText().toString();
            if (TextUtils.isEmpty(port)&&"".equals(port)) {
                Toast.makeText(EvaluateActivity.this, "信息不完整", Toast.LENGTH_SHORT).show();
                return;
            }
            Evaluate evaluate = new Evaluate();
            RatingBar mRatingBar = findViewById(R.id.ratingbar);
//            mRatingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) ->{
//                evaluate.setStar((double) rating);
//            });
            evaluate.setContent(port);
            evaluate.setOid(order.getOid());
            evaluate.setCover(user.getHeadImage());
            evaluate.setName(user.getUsername());
            evaluate.setPid(order.getProduct().getPid());
            evaluate.setStar((double) mRatingBar.getRating());
            executor.execute(() -> {
                try {
                    int code = ProductService.postEvaluate(evaluate);
                    handler.sendMessage(handler.obtainMessage(WHAT_E, code));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });

    }
}
