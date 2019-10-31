package net.lzzy.water.network;

import net.lzzy.sqllib.JsonConverter;
import net.lzzy.water.constants.ApiConstants;
import net.lzzy.water.models.Order;
import org.json.JSONException;
import java.io.IOException;
import java.util.List;

/**
 * @author 菜鸡
 */
public class OrderService {

    public static String getOrderFromServer(String uid,int state) throws IOException {
        String address= ApiConstants.GET_ORDER .concat(uid) +ApiConstants.GET_ORDER_STATE + state;
        return ApiService.okGet(address);
    }
    public static List<Order> getOrders(String json) throws IllegalAccessException, JSONException, InstantiationException {
        JsonConverter<Order> converter = new JsonConverter<>(Order.class);
        return converter.getArray(json,ApiConstants.KEY_ORDER);
    }
}
