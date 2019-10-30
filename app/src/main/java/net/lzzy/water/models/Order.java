package net.lzzy.water.models;

import net.lzzy.sqllib.Jsonable;

import org.json.JSONException;
import org.json.JSONObject;

public class Order implements Jsonable {

    private String oid;
    //订单id
    private String ordertime ;
    //订单时间
    private Double total ;
    //订单总价
    private int state ;
    //订单状态 0待付款，1待发货，2代收货，3评价，4退款/售后
    private String address;
    //地址
    private String name ;
    //名字
    private String telephone ;
    //电话
    private String productId;
    private String userId;
    private int count;
//region
    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getOrdertime() {
        return ordertime;
    }

    public void setOrdertime(String ordertime) {
        this.ordertime = ordertime;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
//endregion

    @Override
    public JSONObject toJson() throws JSONException {
        return null;
    }

    @Override
    public void fromJson(JSONObject object) throws JSONException {
        oid = object.getString("oid");
        ordertime = object.getString("ordertime");
        total = object.getDouble("total");
        state = object.getInt("state");
        address= object.getString("address");
        name = object.getString("name");
        telephone = object.getString("telephone");
        productId= object.getString("productId");
        userId= object.getString("userId");
        count= object.getInt("count");
    }
}
