package net.lzzy.water.constants;


/**
 *
 * @author lzzy_gxy
 * @date 2019/5/24
 * Description:
 */
public class ApiConstants {

    //获取分类

    public static final String NET ="http://192.168.1.101:8080/BalalaShopping/";
    public static final String SPLASH_CATEGORY =NET+"api/getCategorys";
    public  static final String KEY_CATEGORY = "category";

    public static final String SPLASH_PRODUCT =NET+"api/getProductBy/";
    public  static final String KEY_PRODUCTS = "products";
    public static final String LOGIN_USER =NET.concat("api/user_login");
    public  static final String KEY_USER = "user";
}
