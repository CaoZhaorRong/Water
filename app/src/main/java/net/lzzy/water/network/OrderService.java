package net.lzzy.water.network;

import net.lzzy.sqllib.JsonConverter;
import net.lzzy.water.constants.ApiConstants;
import net.lzzy.water.models.Order;
import org.json.JSONException;
import java.io.IOException;
import java.util.List;

public class OrderService {

    public static String getOrderFromServer(int state) throws IOException {
        String address= ApiConstants.GET_ORDER+state;
        return ApiService.okGet(address);
    }
    public static List<Order> getOrders(String json) throws IllegalAccessException, JSONException, InstantiationException {
        JsonConverter<Order> converter = new JsonConverter<>(Order.class);
        return converter.getArray(json,ApiConstants.KEY_ORDER);
    }
}
