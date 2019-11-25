package net.lzzy.water.models;

import net.lzzy.sqllib.Jsonable;
import net.lzzy.water.network.OrderCartService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author 菜鸡
 */
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
    private  Product product;
    private String userId;
    private int count;
    public  Order(){}

    //region

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
//endregion

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("state",state);
        object.put("address",address);
        object.put("name",name);
        object.put("telephone",telephone);
        object.put("count",count);
        object.put("userId",userId);
        object.put("product",product.toJson());
        return object;
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
        count= object.getInt("count");
        try {
            setProduct(OrderCartService.getProduct(object.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
