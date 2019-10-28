package net.lzzy.water.models;

import net.lzzy.sqllib.Jsonable;

import org.json.JSONException;
import org.json.JSONObject;

public class Image implements Jsonable {
    private String id;
    private String image;
    private String pid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        return null;
    }

    @Override
    public void fromJson(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getString("id");
        image = jsonObject.getString("image");
        pid = jsonObject.getString("pid");
    }
}
