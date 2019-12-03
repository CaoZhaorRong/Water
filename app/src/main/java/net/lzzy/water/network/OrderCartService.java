package net.lzzy.water.network;

import net.lzzy.sqllib.JsonConverter;
import net.lzzy.water.constants.ApiConstants;
import net.lzzy.water.models.Image;
import net.lzzy.water.models.OrderCart;
import net.lzzy.water.models.Product;
import net.lzzy.water.models.User;
import net.lzzy.water.utils.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * @author 菜鸡
 */
public class OrderCartService {

    public static String updateCount(int count,String id,String pid) throws IOException {
        String address = ApiConstants.UPDATE_COUNT.concat(String.valueOf(count)).concat("/").concat(id).concat("/").concat(pid);
        return ApiService.okGet(address);
    }

    public static int postCartJson(OrderCart orderCart) throws IOException, JSONException {
        String address = ApiConstants.POST_CART;
        return ApiService.okPost(address,orderCart.toJson());
    }

    public static String deleteCart(String id) throws IOException {
        String address= ApiConstants.DELETE_CART.concat(id) ;
        return ApiService.okGet(address);
    }

    public static String getCartJson(String uid) throws IOException, JSONException {
        String address = ApiConstants.GET_CART.concat(uid);
        return ApiService.okGet(address);
    }

    public static List<OrderCart> getCarts(String json) throws IllegalAccessException, JSONException, InstantiationException {
        JsonConverter<OrderCart> converter = new JsonConverter<>(OrderCart.class);
        return converter.getArray(json,ApiConstants.KEY_CARTS);
    }

    public static Product getProduct(String json) throws  IllegalAccessException, JSONException, InstantiationException {
        JsonConverter<Product> converter = new JsonConverter<>(Product.class);
        return converter.getSingle(json,"product");
    }
    public static User getUser() {
        User user = null;
        if (AppUtils.getUser()!=null){
            user= AppUtils.getUser();
        }
        return user;
    }
}
