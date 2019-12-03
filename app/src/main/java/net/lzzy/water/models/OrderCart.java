package net.lzzy.water.models;

import android.os.Parcel;
import android.os.Parcelable;

import net.lzzy.sqllib.Jsonable;
import net.lzzy.water.network.OrderCartService;
import net.lzzy.water.network.ProductService;
import net.lzzy.water.network.UserService;
import net.lzzy.water.utils.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author 菜鸡
 */
public class OrderCart implements Jsonable , Parcelable {
    private String id;
    ///购物车id

    private int count;
    private Double total;
    ///订单总价

    private User user;
    private Product product;
    ///商品

    public OrderCart(){}

    protected OrderCart(Parcel in) {
        id = in.readString();
        count = in.readInt();
        if (in.readByte() == 0) {
            total = null;
        } else {
            total = in.readDouble();
        }
        user = in.readParcelable(User.class.getClassLoader());
        product = in.readParcelable(Product.class.getClassLoader());
    }

    public static final Creator<OrderCart> CREATOR = new Creator<OrderCart>() {
        @Override
        public OrderCart createFromParcel(Parcel in) {
            return new OrderCart(in);
        }

        @Override
        public OrderCart[] newArray(int size) {
            return new OrderCart[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject object =new JSONObject();
        object.put("count",count);
        object.put("user",user.toJson());
        object.put("product",product.toJson());
        return object;
    }

    @Override
    public void fromJson(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getString("id");
        total = jsonObject.getDouble("total");
        count = jsonObject.getInt("count");
        try {
            Product pro = OrderCartService.getProduct(jsonObject.toString());
            setProduct(pro);
            setUser(OrderCartService.getUser());
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
        parcel.writeString(id);
        parcel.writeInt(count);
        if (total == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(total);
        }
        parcel.writeParcelable(user, i);
        parcel.writeParcelable(product, i);
    }
}
