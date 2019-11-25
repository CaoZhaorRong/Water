package net.lzzy.water.constants;


import net.lzzy.water.utils.AppUtils;
import net.lzzy.water.utils.UserCookies;

/**
 *
 * @author lzzy_gxy
 * @date 2019/5/24
 * Description:
 */
public class ApiConstants {
    private static final String IP= UserCookies.getInstance().loadServerSetting().first;
    private static final String PORT= UserCookies.getInstance().loadServerSetting().second;
    private static final String PROTOCOl="http://";
    public static final String URL_NET_API=PROTOCOl.concat(IP).concat(":").concat(PORT);
    public static final String URL_API = URL_NET_API.concat("/BalalaShopping/");
    //获取分类

    public static final String SPLASH_CATEGORY =URL_API+"api/getCategorys";
    public  static final String KEY_CATEGORY = "category";

    public static final String SPLASH_PRODUCT =URL_API+"api/getProductBy/";
    public  static final String KEY_PRODUCTS = "products";

    public static final String LOGIN_USER =URL_API.concat("api/user_login");
    public  static final String KEY_USER = "user";

    public static final String GET_ORDER =URL_API.concat("api/getOrders/id=");
    public static final String POS_ORDER =URL_API.concat("api/addOrder");
    public  static final String KEY_ORDER = "orders";

    public  static  final  String POST_CART = URL_API.concat("api/addCart");
    public  static  final  String GET_CART = URL_API.concat("api/getCarts/p=");
    public  static final String KEY_CARTS = "carts";
    public  static  final String DELETE_CART = URL_API.concat("api/delete/one&");

    public static final  String URL_FAVORITES = URL_API.concat("api/favorites/p=");
    public  static final String KEY_FAVORITES = "favorites";
    public static final  String URL_EXISTS = URL_API.concat("api/favorites/exists/p=");
    public static final  String URL_COUNT = URL_API.concat("api/count/u=");
    public static  final String COUNT = "&state=";

    public static final  String URL_STATE = URL_API.concat("api/order/u=");
    public static  final String STATE = "&state=";

}
