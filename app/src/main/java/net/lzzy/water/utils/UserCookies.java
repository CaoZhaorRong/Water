package net.lzzy.water.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

public class UserCookies {
    private final static String SP_INFO = "Login";
    private final static String PHONE = "phone";
    private final static String PASSWORD = "password";
    private SharedPreferences spUser;

    private static final UserCookies INSTANCE = new UserCookies();

    public static UserCookies getInstance() {
        return INSTANCE;
    }
    private UserCookies(){
        spUser = AppUtils.getContext().getSharedPreferences(SP_INFO,Context.MODE_PRIVATE);
    }
    /** 保存地区名称 **/
    public void insertUser(String phone,String password){
        spUser.edit().putString(PHONE,phone).apply();
        spUser.edit().putString(PASSWORD,password).apply();
    }
    public String[] getUser(){
        String[] array=new String[2];
            String phone = spUser.getString(PHONE,"");
            array[0] = phone;
            String password = spUser.getString(PASSWORD, "");
            array[1] = password;

        return array;
    }

    public void clear(){
       spUser.edit().clear().apply();
    }
}
