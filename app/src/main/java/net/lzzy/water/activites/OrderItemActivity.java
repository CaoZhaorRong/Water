package net.lzzy.water.activites;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.lzzy.water.R;
import net.lzzy.water.constants.ApiConstants;
import net.lzzy.water.models.Order;

import static net.lzzy.water.activites.SubmitActivity.ORDER_ITEM;

/**
 * @author 菜鸡
 */
public class OrderItemActivity extends AppCompatActivity {
    private Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_order_item);
        order = getIntent().getParcelableExtra(ORDER_ITEM);
        initView();
    }
    private void initView() {
        ImageView btnBack = findViewById(R.id.btn_back);
        TextView tvName = findViewById(R.id.o_i_name);
        TextView tvPhone = findViewById(R.id.o_i_phone);
        TextView tvLocation = findViewById(R.id.o_i_location);
        TextView tvTotal = findViewById(R.id.o_i_total);
        TextView tvSerial = findViewById(R.id.o_i_serial);
        TextView tvTime = findViewById(R.id.o_i_orderTime);
        ImageView imgCover = findViewById(R.id.submit_imgCover);
        TextView tvTitle = findViewById(R.id.submit_name);
        TextView tvSize = findViewById(R.id.submit_size);
        TextView tvCount = findViewById(R.id.submit_count);
        TextView tvPrice = findViewById(R.id.submit_price);
        btnBack.setOnClickListener(view -> onBackPressed());
        tvName.setText(order.getName());
        tvPhone.setText(order.getTelephone());
        tvLocation.setText(order.getAddress());
        tvTotal.setText(String.valueOf(order.getTotal()));
        tvTitle.setText(order.getProduct().getPname());
        tvCount.setText("数量×".concat(String.valueOf(order.getCount())));
        tvSerial.setText(order.getSerial());
        tvPrice.setText(String.valueOf(order.getProduct().getPrice()));
        tvTime.setText(order.getOrdertime());
        Picasso.get().load(ApiConstants.URL_API + order.getProduct().getpImage().get(0).getImage()).into(imgCover);
    }

}
