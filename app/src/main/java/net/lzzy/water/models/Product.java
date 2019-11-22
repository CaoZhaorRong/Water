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
    //图片路径
    private String pdate ;
    //上架时间
    private int is_hot;

    //是否热门，1表示热门，0表示不热门

    private String pdesc;
    //商品描述
    private  String cid;
    private List<Image> pImage;
    private  List<Colors> colors;
    private List<Evaluate> evaluates;
    private List<Yardage> yardages;

    public  Product(){}
    protected Product(Parcel in) {
        pid = in.readString();
        pname = in.readString();
        if (in.readByte() == 0) {
            price = null;
        } else {
            price = in.readDouble();
        }
        pdate = in.readString();
        is_hot = in.readInt();
        pdesc = in.readString();
        cid = in.readString();
        pImage = in.readArrayList(Image.class.getClassLoader());
        colors = in.readArrayList(Colors.class.getClassLoader());
        evaluates = in.readArrayList(Evaluate.class.getClassLoader());
        yardages = in.readArrayList(Yardage.class.getClassLoader());
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

    public List<Colors> getColors() {
        return colors;
    }

    public void setColors(List<Colors> colors) {
        this.colors = colors;
    }

    public List<Evaluate> getEvaluates() {
        return evaluates;
    }

    public void setEvaluates(List<Evaluate> evaluates) {
        this.evaluates = evaluates;
    }

    public List<Yardage> getYardages() {
        return yardages;
    }

    public void setYardages(List<Yardage> yardages) {
        this.yardages = yardages;
    }

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
        JSONObject object = new JSONObject();
        object.put("pid",pid);
        object.put("price",price);
        return object;
    }
//endregion

    @Override
    public void fromJson(JSONObject object) throws JSONException {
        pid = object.getString("pid");
        pname = object.getString("pname");
        price = object.getDouble("price");
        pdate = object.getString("pdate");
        is_hot = object.getInt("is_hot");
        pdesc = object.getString("pdesc");
        cid = object.getString("cid");
        try {
            if (!object.isNull("pImage") ){
                String image =object.getString("pImage");
                setpImage(ProductService.getImage(image));
            }
            if (!object.isNull("colors")){
                String json = object.getString("colors");
                setColors(ProductService.getColors(json));
            }
            if (!object.isNull("evaluates")){
                String json = object.getString("evaluates");
                setEvaluates(ProductService.getEvaluate(json));
            }
            if (!object.isNull("yardages")){
                String json = object.getString("yardages");
                setYardages(ProductService.getYardage(json));
            }
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
        parcel.writeString(pdate);
        parcel.writeInt(is_hot);
        parcel.writeString(pdesc);
        parcel.writeString(cid);
        parcel.writeList(pImage);
        parcel.writeList(colors);
        parcel.writeList(evaluates);
        parcel.writeList(yardages);
    }
}
