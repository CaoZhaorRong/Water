package net.lzzy.water.network;

import net.lzzy.sqllib.JsonConverter;
import net.lzzy.water.constants.ApiConstants;
import net.lzzy.water.models.User;

import org.json.JSONException;

import java.io.IOException;

/**
 * @author 菜鸡
 */
public class UserService {
    public static String getUserFromServer(User user) throws JSONException, IOException {
        String address= ApiConstants.LOGIN_USER;
        return ApiService.okRequest(address,user.toJson());
    }
    public static User getUser(String json) throws IllegalAccessException, JSONException, InstantiationException {
        //数组==调用getArray方法 getSingle得到一个单独的Java数据
        JsonConverter<User> converter = new JsonConverter<>(User.class);
        return converter.getSingle(json,ApiConstants.KEY_USER);
    }

    public static User getUser(String json,String kw) throws IllegalAccessException, JSONException, InstantiationException {
        JsonConverter<User> converter = new JsonConverter<>(User.class);
        return converter.getSingle(json,kw);
    }
}
