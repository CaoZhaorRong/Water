package net.lzzy.water.models;

import android.os.Parcel;
import android.os.Parcelable;

import net.lzzy.sqllib.Jsonable;
import net.lzzy.water.network.OrderCartService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;


/**
 * @author 菜鸡
 */
public class Order implements Jsonable , Parcelable {

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
    private String serial;
    private  String cartId;

    protected Order(Parcel in) {
        oid = in.readString();
        ordertime = in.readString();
        if (in.readByte() == 0) {
            total = null;
        } else {
            total = in.readDouble();
        }
        state = in.readInt();
        address = in.readString();
        name = in.readString();
        telephone = in.readString();
        product = in.readParcelable(Product.class.getClassLoader());
        userId = in.readString();
        count = in.readInt();
        serial = in.readString();
        cartId = in.readString();
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public  Order(){}


//region
    public Product getProduct() {
        return product;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
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
        object.put("ordertime",ordertime);
        object.put("total",total);
        object.put("serial",serial);
        object.put("cartId",cartId);
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
        serial = object.getString("serial");
        try {
            setProduct(OrderCartService.getProduct(object.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(oid);
        parcel.writeString(ordertime);
        if (total == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(total);
        }
        parcel.writeInt(state);
        parcel.writeString(address);
        parcel.writeString(name);
        parcel.writeString(telephone);
        parcel.writeParcelable(product, i);
        parcel.writeString(userId);
        parcel.writeInt(count);
        parcel.writeString(serial);
        parcel.writeString(cartId);
    }
}
