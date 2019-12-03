package net.lzzy.water.models;

import android.os.Parcel;
import android.os.Parcelable;

import net.lzzy.sqllib.Jsonable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author 菜鸡
 */
public class Yardage implements Jsonable, Parcelable {
    private String id;
    private String yardage;
    private String pid;

    public Yardage(){}
    protected Yardage(Parcel in) {
        id = in.readString();
        yardage = in.readString();
        pid = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(yardage);
        dest.writeString(pid);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Yardage> CREATOR = new Creator<Yardage>() {
        @Override
        public Yardage createFromParcel(Parcel in) {
            return new Yardage(in);
        }

        @Override
        public Yardage[] newArray(int size) {
            return new Yardage[size];
        }
    };

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
