package net.lzzy.water.models;

import android.os.Parcel;
import android.os.Parcelable;

import net.lzzy.sqllib.Jsonable;
import net.lzzy.water.network.ProductService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 菜鸡
 */
public class Product implements Jsonable , Parcelable {
    private String pid;
    //编号
    private String pname;
    //商品名
    private Double price;
    //商城价
    private String pimage;
    //图片路径
    private String pdate ;
    //上架时间
    private int is_hot;
    //是否热门，1表示热门，0表示不热门
    private String pdesc;
    //商品描述
    private  String cid;
    private List<Image> pImage;

    public  Product(){}
    protected Product(Parcel in) {
        pid = in.readString();
        pname = in.readString();
        if (in.readByte() == 0) {
            price = null;
        } else {
            price = in.readDouble();
        }
        pimage = in.readString();
        pdate = in.readString();
        is_hot = in.readInt();
        pdesc = in.readString();
        cid = in.readString();
        pImage = in.readArrayList(Image.class.getClassLoader());
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    //region
    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getPimage() {
        return pimage;
    }

    public void setPimage(String pimage) {
        this.pimage = pimage;
    }

    public String getPdate() {
        return pdate;
    }

    public void setPdate(String pdate) {
        this.pdate = pdate;
    }

    public int getIs_hot() {
        return is_hot;
    }

    public void setIs_hot(int is_hot) {
        this.is_hot = is_hot;
    }

    public String getPdesc() {
        return pdesc;
    }

    public void setPdesc(String pdesc) {
        this.pdesc = pdesc;
    }

    public List<Image> getpImage() {
        return pImage;
    }

    public void setpImage(List<Image> pImage) {
        this.pImage = pImage;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        return null;
    }
//endregion

    @Override
    public void fromJson(JSONObject jsonObject) throws JSONException {
        pid = jsonObject.getString("pid");
        pname = jsonObject.getString("pname");
        price = jsonObject.getDouble("price");
        //pimage = jsonObject.getString("pimage");
        pdate = jsonObject.getString("pdate");
        is_hot = jsonObject.getInt("is_hot");
        pdesc = jsonObject.getString("pdesc");
        cid = jsonObject.getString("cid");
        String image =jsonObject.getString("pImage");
        try {
            List<Image> data = ProductService.getImage(image);
            setpImage(data);
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
        parcel.writeString(pid);
        parcel.writeString(pname);
        if (price == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(price);
        }
        parcel.writeString(pimage);
        parcel.writeString(pdate);
        parcel.writeInt(is_hot);
        parcel.writeString(pdesc);
        parcel.writeString(cid);
        parcel.writeList(pImage);
    }
}
