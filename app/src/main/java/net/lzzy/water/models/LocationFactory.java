package net.lzzy.water.models;

import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.lzzy.water.utils.UserCookies;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author 菜鸡
 */
public class LocationFactory {


    public static List<Locations> getLocations(){
        String location = UserCookies.getInstance().loadLocations();
        Gson gson = new Gson();
        List<Locations> list ;
        list= gson.fromJson(location, new TypeToken<List<Locations>>() {}.getType());
        if (list!=null&&list.size()>0){
            return list;
        }
        return new ArrayList<>();
    }

    public static Locations getLocation(){
        String location = UserCookies.getInstance().loadLocations();
        Gson gson = new Gson();
        List<Locations> list ;
        list= gson.fromJson(location, new TypeToken<List<Locations>>() {}.getType());
        if (list!=null&&list.size()>0){
            return list.get(0);
        }
        return null;
    }

    public static void insert(Locations locations){
        List<Locations> list = getLocations();
        list.add(locations);
        Gson gson=new Gson();
        String str=gson.toJson(list);
        UserCookies.getInstance().saveLocations(str);
    }

    public static int  delete(UUID uuid){
        List<Locations> list = getLocations();
        for (Locations l:list){
            if (l.getId().equals(uuid)){
                list.remove(l);
            }
        }
        Gson gson=new Gson();
        String str=gson.toJson(list);
        UserCookies.getInstance().saveLocations(str);
        return 1;
    }
}
