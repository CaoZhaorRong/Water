package net.lzzy.water.models;

import android.os.Parcel;
import android.os.Parcelable;

import net.lzzy.sqllib.Jsonable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author 菜鸡
 */
public class User implements Jsonable , Parcelable {
    private String uid;
    //编号
    private String username;
    //昵称
    private String password;
    //登陆密码
    private String telephone;
    //手机号
    private String headImage;
    //头像
    private String address;
    private String birthday;
    //生日
    private String role;
    //角色

    public  User(){}
    protected User(Parcel in) {
        uid = in.readString();
        username = in.readString();
        password = in.readString();
        telephone = in.readString();
        headImage = in.readString();
        address = in.readString();
        birthday = in.readString();
        role = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    //region
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getHeadImage() {
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
//endregion

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("uid", uid);
        json.put("password", password);
        json.put("telephone", telephone);
        json.put("uid", uid);
        json.put("username", username);
        json.put("headImage", headImage);
        json.put("address", address);
        json.put("birthday", birthday);
        json.put("role", role);
        return json;
    }

    @Override
    public void fromJson(JSONObject object) throws JSONException {
        uid = object.getString("uid");
        username = object.getString("username");
        password = object.getString("password");
        telephone = object.getString("telephone");
        role = object.getString("role");
        headImage = object.getString("headImage");
        if (!object.isNull("birthday") && !object.isNull("address")) {
            birthday = object.getString("birthday");
            address = object.getString("address");
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uid);
        parcel.writeString(username);
        parcel.writeString(password);
        parcel.writeString(telephone);
        parcel.writeString(headImage);
        parcel.writeString(address);
        parcel.writeString(birthday);
        parcel.writeString(role);
    }
}
