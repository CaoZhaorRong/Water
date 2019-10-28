package net.lzzy.water.models;

import net.lzzy.sqllib.Jsonable;

import org.json.JSONException;
import org.json.JSONObject;

public class User implements Jsonable {
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
        json.put("password",password);
        json.put("telephone",telephone);
        return json;
    }

    @Override
    public void fromJson(JSONObject object) throws JSONException {
        uid = object.getString("uid");
        username = object.getString("username");
        password = object.getString("password");
        telephone = object.getString("telephone");
        role = object.getString("role");
        birthday = object.getString("birthday");
        address = object.getString("address");
    }
}
