package net.lzzy.water.network;

import net.lzzy.sqllib.JsonConverter;
import net.lzzy.water.constants.ApiConstants;
import net.lzzy.water.models.Category;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

/**
 * @author 菜鸡
 */
public class CategoryService {
    public static String getCategoryFromServer() throws IOException {
        String address= ApiConstants.SPLASH_CATEGORY;
        return ApiService.okGet(address);
    }
    public static List<Category> getCategories(String json) throws IllegalAccessException, JSONException, InstantiationException {
        //数组==调用getArray方法 getSingle得到一个单独的Java数据
//        JSONObject jsonObject = new JSONObject(json);
//        String array = jsonObject.getString(ApiConstants.KEY_CATEGORY);
        JsonConverter<Category> converter = new JsonConverter<>(Category.class);
        return converter.getArray(json,ApiConstants.KEY_CATEGORY);
    }
}
