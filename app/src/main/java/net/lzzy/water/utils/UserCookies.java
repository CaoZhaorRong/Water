package net.lzzy.water.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author 菜鸡
 */
public class UserCookies {
    private static final String KEY_TIME = "keyTime";
    private final static String SP_INFO = "Login";
    private final static String USERJSON = "userJson";
    private static final String URL_ID = "url";
    private static final String URL_PORT = "urlPort";
    private static final String SP_SETTING = "spSetting";
    private static final String URL_PHONE = "url_phone";
    private static final String URL_PASSWORD = "url_password";
    public static final String SP_LOCATION = "spLocation";
    private SharedPreferences spUser;
    private SharedPreferences spTime;
    private SharedPreferences spLocation;

    private static final UserCookies INSTANCE = new UserCookies();
    private  SharedPreferences spSetting;

    public static UserCookies getInstance() {
        return INSTANCE;
    }
    private UserCookies(){
        spUser = AppUtils.getContext().getSharedPreferences(SP_INFO,Context.MODE_PRIVATE);
        spSetting = AppUtils.getContext().getSharedPreferences(SP_SETTING, Context.MODE_PRIVATE);
        spLocation = AppUtils.getContext().getSharedPreferences(SP_LOCATION,Context.MODE_PRIVATE);
    }

    public void saveLocations(String json) {
       spLocation.edit()
                .putString(SP_LOCATION, json)
                .apply();
    }

    public  String loadLocations() {
        return spLocation.getString(SP_LOCATION, "");
    }

    public void saveServerSetting(String ip, String port) {
        spSetting.edit()
                .putString(URL_ID, ip)
                .putString(URL_PORT, port)
                .apply();
    }

    public  Pair<String, String> loadServerSetting() {
        String ip = spSetting.getString(URL_ID, "lzzy.info");
        String port = spSetting.getString(URL_PORT, "8080");
        return new Pair<>(ip, port);
    }


    /** 保存登录信息 **/
    public  void saveUser(String phone, String password) {
        spUser.edit()
                .putString(URL_PHONE, phone)
                .putString(URL_PASSWORD, password)
                .apply();
    }
    public  Pair<String, String> loadUser() {
        String ip = spUser.getString(URL_PHONE, "");
        String port = spUser.getString(URL_PASSWORD, "");
        return new Pair<>(ip, port);
    }

    public void clear(){
       spUser.edit().clear().apply();
    }
    public void updateLastRefreshTime() {
        String time = DateTimeUtils.DATE_TIME_FORMAT.format(new Date());
        spTime.edit().putString(KEY_TIME, time).apply();
    }

    public String getLastRefreshTime() {
        return spTime.getString(KEY_TIME, "");
    }
}
