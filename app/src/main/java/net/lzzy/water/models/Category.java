package net.lzzy.water.models;

import android.os.Parcel;
import android.os.Parcelable;

import net.lzzy.sqllib.Jsonable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author 菜鸡
 */
public class Category implements Jsonable, Parcelable {
    private String id;
    //类别主键
    private String name;
    //类别名

    public Category(){}

    protected Category(Parcel in) {
        id = in.readString();
        name = in.readString();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        return null;
    }

    @Override
    public void fromJson(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getString("cid");
        name = jsonObject.getString("cname");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
    }
}
