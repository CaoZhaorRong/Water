package net.lzzy.water.models;

import android.os.Parcel;
import android.os.Parcelable;

import net.lzzy.sqllib.Jsonable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author 菜鸡
 */
public class Evaluate implements Jsonable , Parcelable {
    private String id;
    private String content;
    private String cover;
    private String name;
    private String pid;
    private String oid;
    private Double star;
     public  Evaluate(){}


    protected Evaluate(Parcel in) {
        id = in.readString();
        content = in.readString();
        cover = in.readString();
        name = in.readString();
        pid = in.readString();
        oid = in.readString();
        star = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(content);
        dest.writeString(cover);
        dest.writeString(name);
        dest.writeString(pid);
        dest.writeString(oid);
        dest.writeDouble(star);
    }

    public static final Creator<Evaluate> CREATOR = new Creator<Evaluate>() {
        @Override
        public Evaluate createFromParcel(Parcel in) {
            return new Evaluate(in);
        }

        @Override
        public Evaluate[] newArray(int size) {
            return new Evaluate[size];
        }
    };

    public String getOid() {
        return oid;
    }

    public Double getStar() {
        return star;
    }

    public void setStar(Double star) {
        this.star = star;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("content",content);
        object.put("cover",cover);
        object.put("name",name);
        object.put("oid",oid);
        object.put("pid",pid);
        object.put("star",star);
        return object;
    }

    @Override
    public void fromJson(JSONObject object) throws JSONException {
        id = object.getString("id");
        content = object.getString("content");
        cover = object.getString("cover");
        name = object.getString("name");
        star = object.getDouble("star");
    }

    @Override
    public int describeContents() {
        return 0;
    }


}
