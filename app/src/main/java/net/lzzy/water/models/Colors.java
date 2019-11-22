package net.lzzy.water.models;

import net.lzzy.sqllib.Jsonable;

import org.json.JSONException;
import org.json.JSONObject;

public class Colors implements Jsonable {
    private String id;
    private  String colors;
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
    public String getColors() {
        return colors;
    }
    public void setColors(String colors) {
        this.colors = colors;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        return null;
    }

    @Override
    public void fromJson(JSONObject object) throws JSONException {
        id = object.getString("id");
        pid = object.getString("pid");
        colors = object.getString("colors");
    }
}
