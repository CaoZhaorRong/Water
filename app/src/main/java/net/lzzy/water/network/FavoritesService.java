package net.lzzy.water.network;

import net.lzzy.sqllib.JsonConverter;
import net.lzzy.water.constants.ApiConstants;
import net.lzzy.water.models.Favorites;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * @author 菜鸡
 */
public class FavoritesService {
    public static boolean postFavorties(String pid,String uid) throws IOException, JSONException {
        String address = ApiConstants.URL_FAVORITES.concat(pid).concat("&u=").concat(uid);
        String json =  ApiService.okGet(address);
        JSONObject object = new JSONObject(json);
        return object.getBoolean("flag");
    }

    public static boolean isExists(String pid,String uid) throws IOException, JSONException {
        String address = ApiConstants.URL_EXISTS.concat(pid).concat("&u=").concat(uid);
        String json =  ApiService.okGet(address);
        JSONObject object = new JSONObject(json);
        return object.getBoolean("flag");
    }
    public static List<Favorites> getFavorites(String json) throws IllegalAccessException, JSONException, InstantiationException {
        JsonConverter<Favorites> converter = new JsonConverter<>(Favorites.class);
        return converter.getArray(json,ApiConstants.KEY_FAVORITES);
    }
}
