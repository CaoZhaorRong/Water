package net.lzzy.water.models;

import android.os.Parcel;
import android.os.Parcelable;

import net.lzzy.sqllib.Jsonable;

import org.json.JSONException;
import org.json.JSONObject;

public class Colors implements Jsonable , Parcelable {
    private String id;
    private  String colors;
    private String pid;

    public  Colors(){}
    protected Colors(Parcel in) {
        id = in.readString();
        colors = in.readString();
        pid = in.readString();
    }

    public static final Creator<Colors> CREATOR = new Creator<Colors>() {
        @Override
        public Colors createFromParcel(Parcel in) {
            return new Colors(in);
        }

        @Override
        public Colors[] newArray(int size) {
            return new Colors[size];
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(colors);
        parcel.writeString(pid);
    }
}
