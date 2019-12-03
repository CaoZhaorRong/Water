package net.lzzy.water.activites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.hjq.toast.ToastUtils;
import com.squareup.picasso.Picasso;

import net.lzzy.sqllib.GenericAdapter;
import net.lzzy.sqllib.ViewHolder;
import net.lzzy.water.R;
import net.lzzy.water.constants.ApiConstants;
import net.lzzy.water.models.Image;
import net.lzzy.water.models.Product;
import net.lzzy.water.network.CategoryService;
import net.lzzy.water.network.ProductService;
import net.lzzy.water.utils.AbstractStaticHandler;
import net.lzzy.water.utils.AppUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import static net.lzzy.water.activites.MainActivity.KEY;
import static net.lzzy.water.activites.MainActivity.RESULT_PRODUCT;
import static net.lzzy.water.utils.AppUtils.getContext;

/**
 * @author 菜鸡
 */
public class SearchActivity extends AppCompatActivity {

    private static final int WHAT_PRODUCT = 0;
    private List<Product> products;
    private GenericAdapter<Product> gvAdapter;
    private GridView gv;
    private ThreadPoolExecutor executor = AppUtils.getExecutor();

    private Handler handler = new Handler(this);
    private EditText editKey;
    private String key;

    private static class Handler extends AbstractStaticHandler<SearchActivity> {

        private Handler(SearchActivity context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg, SearchActivity activity) {
            switch (msg.what) {
                case WHAT_PRODUCT:
                    String json = String.valueOf(msg.obj);
                    try {
                        if (!"".equals(json)) {
                            activity.products = ProductService.getProducts(json);
                            activity.showProducts();
                            activity.editKey.clearFocus();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                default:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search);
        editKey = findViewById(R.id.search_key);
        gv = findViewById(R.id.search_gv);
        gv.setNumColumns(2);
        editKey.setText(key);
        ImageView imageView = findViewById(R.id.search_back);
        imageView.setOnClickListener(view -> {
            finish();
            //失去焦点
           /* editKey.clearFocus();*/
        });
        TextView tvSearch = findViewById(R.id.search_product);
        tvSearch.setOnClickListener(view -> {
            String key = editKey.getText().toString();
            if (TextUtils.isEmpty(key)){
                ToastUtils.show("关键词不可为空");
                return;
            }
            executor.execute(()->{
                try {
                    String data = ProductService.getSearch(key);
                    handler.sendMessage(handler.obtainMessage(WHAT_PRODUCT,data));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });

        editKey.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                String key = editKey.getText().toString();
                if (TextUtils.isEmpty(key)){
                    ToastUtils.show("关键词不可为空");
                }else {
                    executor.execute(()->{
                        try {
                            String data = ProductService.getSearch(key);
                            handler.sendMessage(handler.obtainMessage(WHAT_PRODUCT,data));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
            return false;
        });

        editKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void showProducts() {
        gvAdapter = new GenericAdapter<Product>(getContext(), R.layout.product_item, products) {
            @Override
            public void populate(ViewHolder holder, Product product) {
                List<Image> images = product.getpImage();
                if (images.size() > 0) {
                    ImageView imageView = holder.getView(R.id.product_item_iv);
                    String url = ApiConstants.URL_API + images.get(0).getImage();
                    Picasso.get().load(url).into(imageView);
                }
                holder.setTextView(R.id.product_item_name, product.getPname())
                        .setTextView(R.id.product_item_price, "￥" + product.getPrice());
            }
            @Override
            public boolean persistInsert(Product product) {
                return false;
            }

            @Override
            public boolean persistDelete(Product product) {
                return false;
            }
        };
        gv.setAdapter(gvAdapter);
        gv.setOnItemClickListener((adapterView, view, pos, l) -> {
            Product product = gvAdapter.getItem(pos);
            Intent intent=new Intent(this, BuyActivity.class);
            intent.putExtra(RESULT_PRODUCT,product);
            startActivity(intent);
        });
    }

}
