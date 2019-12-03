package net.lzzy.water.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.Pair;

import com.hjq.toast.ToastUtils;

import net.lzzy.water.frament.OwnFragment;
import net.lzzy.water.models.User;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author lzzy_gxy
 * @date 2019/3/11
 * Description:
 */
public class AppUtils extends Application {
    public static final String URL_ID = "url";
    public static final String URL_PORT = "urlPort";
    public static final String SP_SETTING = "spSetting";
    private static WeakReference<Context> wContext;
    private static List<Activity> activities = new LinkedList<>();
    private static String runningActivity;


    private static User user;
    public static void clearUser(){
        user = null;
    }
    public static User getUser(){
       return user;
    }
    public static void setUser(User date){
        user = new User();
        user = date;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        wContext = new WeakReference<>(this);
        ToastUtils.init(this);

    }
    public static Context getContext() {
        return wContext.get();
    }
    //region Activity的集中管理

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static void exit() {
        for (Activity activity : activities) {
            if (activity != null) {
                activity.finish();
            }
        }
        System.exit(0);
    }

    public static Activity getRunningActivity() {
        for (Activity activity : activities) {
            //首先取到activity的名字
            String name = activity.getLocalClassName();
            if (AppUtils.runningActivity.equals(name)) {
                return activity;
            }
        }
        return null;
    }

    public static void setRunning(String runningActivity) {
        AppUtils.runningActivity = runningActivity;
    }

    public static void setStopped(String stoppedActivity) {
        if (stoppedActivity.equals(AppUtils.runningActivity)) {
            AppUtils.runningActivity = "";
        }
    }
    //endregion
    //region 检测网络不可用

    public static boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager != null ? manager.getActiveNetworkInfo() : null;
        return info != null && info.isConnected();
    }
    //endregion
    //region 创建线程池执行

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    private static final int MAX_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE_SECOUNDS = 30;
    private static final ThreadFactory THREAD_FACTORY = new ThreadFactory() {
        private final AtomicInteger count = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "thread#" + count.getAndIncrement());
        }
    };
    private static final BlockingQueue<Runnable> POOL_QUEUE = new LinkedBlockingDeque<>(128);

    public static ThreadPoolExecutor getExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE,
                KEEP_ALIVE_SECOUNDS, TimeUnit.SECONDS, POOL_QUEUE, THREAD_FACTORY);
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }
    //endregion
    //region 尝试连接的方法

    public static void tryConnectServer(String address) throws IOException {
        URL url = new URL(address);
        //建立一个连接
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(5000);
        connection.getContent();
    }
    //endregion
    //region SharedPreferences保存数据

    public static void saveServerSetting(String ip, String port, Context context) {
        SharedPreferences spSetting = context.getSharedPreferences(SP_SETTING, MODE_PRIVATE);
        spSetting.edit()
                .putString(URL_ID, ip)
                .putString(URL_PORT, port)
                .apply();
    }

    public static Pair<String, String> loadServerSetting(Context context) {
        SharedPreferences spSetting = context.getSharedPreferences(SP_SETTING, MODE_PRIVATE);
        String ip = spSetting.getString(URL_ID, "192.168.1.100");
        String port = spSetting.getString(URL_PORT, "8080");
        return new Pair<>(ip, port);
    }
    //endregion
    //region 获取各类网络的mac地址 包括WiFi和移动数据的mac地址

    public static List<String> getMacAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            List<String> items = new ArrayList<>();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                byte[] address = ni.getHardwareAddress();
                if (address == null || address.length == 0) {
                    continue;
                }
                StringBuilder builder = new StringBuilder();
                for (byte a : address) {
                    builder.append(String.format("%02x:", a));
                }
                if (builder.length() > 0) {
                    builder.deleteCharAt(builder.length() - 1);
                }
                if (ni.isUp()) {
                    items.add(ni.getName() + ":" + builder.toString());
                }
            }
            return items;
        } catch (SocketException e) {
            return new ArrayList<>();
        }
    }
    //endregion


    /**
     * 将日期转换为"yyyyMMdd"类型的String
     * @param date
     * @return
     */
    public static String dateConversion(Date date){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
        String stringDate=sdf.format(date);

        return stringDate;
    }


    /**
     * 获取过去或者未来 任意天内的日期数组
     * @param intervals      intervals天内
     * @return              日期数组
     */
    public static ArrayList<String> getDateListOfNum(int intervals) {
        ArrayList<String> pastDaysList = new ArrayList<>();
        //ArrayList<String> fetureDaysList = new ArrayList<>();
        for (int i = 0; i <intervals; i++) {
            pastDaysList.add(getPastDate(i));
            //fetureDaysList.add(getFetureDate(i));
        }
        return pastDaysList;
    }

    /**
     * 获取过去第几天的日期
     *
     * @param past
     * @return
     */
    public static String getPastDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        Date today = calendar.getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String result = format.format(today);
        Log.e(null, result);
        return result;
    }

}
