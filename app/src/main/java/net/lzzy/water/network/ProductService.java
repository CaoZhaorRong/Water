package net.lzzy.water.network;

import net.lzzy.sqllib.JsonConverter;
import net.lzzy.water.constants.ApiConstants;
import net.lzzy.water.models.Category;
import net.lzzy.water.models.Colors;
import net.lzzy.water.models.Evaluate;
import net.lzzy.water.models.Image;
import net.lzzy.water.models.Product;
import net.lzzy.water.models.Yardage;

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

    public  static List<Colors> getColors(String json) throws IllegalAccessException, JSONException, InstantiationException {
        JsonConverter<Colors> converter=new JsonConverter<>(Colors.class);
        return  converter.getArray(json);
    }

    public  static List<Evaluate> getEvaluate(String json) throws IllegalAccessException, JSONException, InstantiationException {
        JsonConverter<Evaluate> converter=new JsonConverter<>(Evaluate.class);
        return  converter.getArray(json);
    }
    public  static List<Yardage> getYardage(String json) throws IllegalAccessException, JSONException, InstantiationException {
        JsonConverter<Yardage> converter=new JsonConverter<>(Yardage.class);
        return  converter.getArray(json);
    }
}
