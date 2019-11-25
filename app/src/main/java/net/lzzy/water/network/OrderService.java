package net.lzzy.water.network;

import net.lzzy.sqllib.JsonConverter;
import net.lzzy.water.constants.ApiConstants;
import net.lzzy.water.models.Order;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * @author 菜鸡
 */
public class OrderService {

    public static String getOrderFromServer(String uid/*,int state*/) throws IOException {
        //String address= ApiConstants.GET_ORDER .concat(uid) +ApiConstants.GET_ORDER_STATE + state;
        String address= ApiConstants.GET_ORDER .concat(uid);
        return ApiService.okGet(address);
    }
    public static String getCount(String uid,int state) throws  IOException{
        String address = ApiConstants.URL_COUNT.concat(uid).concat(ApiConstants.COUNT.concat(state+""));
        return ApiService.okGet(address);
    }
    public static String getState(String uid,int state) throws  IOException{
        String address = ApiConstants.URL_STATE.concat(uid).concat(ApiConstants.STATE.concat(state+""));
        return ApiService.okGet(address);
    }

    public static int posOrderFromServer(Order order) throws IOException, JSONException {
        String address= ApiConstants.POS_ORDER ;
        return ApiService.okPost(address,order.toJson());
    }

    public static List<Order> getOrders(String json) throws IllegalAccessException, JSONException, InstantiationException {
        JSONObject object = new JSONObject(json);
        int count = object.getInt("count");
        if (count > 0){
            JsonConverter<Order> converter = new JsonConverter<>(Order.class);
            return converter.getArray(json,ApiConstants.KEY_ORDER);
        }
        return null;
    }
}
