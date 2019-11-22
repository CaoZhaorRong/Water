package net.lzzy.water.models;

import net.lzzy.sqllib.Jsonable;
import net.lzzy.water.network.OrderCartService;

import org.json.JSONException;
import org.json.JSONObject;

public class Favorites implements Jsonable {
    public static final String PRODUCT = "product";
    public static final String ID = "id";
    private String id;
    private Product product;
    public Favorites() {

    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        return null;
    }

    @Override
    public void fromJson(JSONObject object) throws JSONException {
        id = object.getString(ID);
        if (object.isNull(PRODUCT)){
            try {
                setProduct(OrderCartService.getProduct(object.getString(PRODUCT)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
