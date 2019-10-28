package net.lzzy.water.network;

import net.lzzy.sqllib.JsonConverter;
import net.lzzy.water.constants.ApiConstants;
import net.lzzy.water.models.Category;
import net.lzzy.water.models.Image;
import net.lzzy.water.models.Product;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

/**
 * @author 菜鸡
 */
public class ProductService {
    public static String getProductFromServer(String cid) throws IOException {
        String address= ApiConstants.SPLASH_PRODUCT.concat(cid);
        return ApiService.okGet(address);
    }
    public static List<Product> getProducts(String json) throws IllegalAccessException, JSONException, InstantiationException {
        //数组==调用getArray方法 getSingle得到一个单独的Java数据
        JsonConverter<Product> converter = new JsonConverter<>(Product.class);
        return converter.getArray(json,ApiConstants.KEY_PRODUCTS);
    }
    public  static List<Image> getImage(String json) throws IllegalAccessException, JSONException, InstantiationException {
        JsonConverter<Image> converter=new JsonConverter<>(Image.class);
        return  converter.getArray(json);
    }

}
