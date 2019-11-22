package net.lzzy.water.models;

import net.lzzy.sqllib.Jsonable;

import org.json.JSONException;
import org.json.JSONObject;

public class Yardage implements Jsonable {
    private String id;
    private String yardage;
    private String pid;

    public String getPid() {
        return pid;
    }
    public void setPid(String pid) {
        this.pid = pid;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getYardage() {
        return yardage;
    }
    public void setYardage(String yardage) {
        this.yardage = yardage;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        return null;
    }

    @Override
    public void fromJson(JSONObject object) throws JSONException {
        id = object.getString("id");
        pid = object.getString("pid");
        yardage = object.getString("yardage");
    }
}
