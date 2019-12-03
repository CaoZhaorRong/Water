package net.lzzy.water.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

/**
 * @author 菜鸡
 */
public class Locations implements Parcelable {
    private UUID id;
    private String address;
    private String name ;
    private String telephone ;
    private boolean isDefault;
    public  Locations(){}
    protected Locations(Parcel in) {
        address = in.readString();
        name = in.readString();
        telephone = in.readString();
        isDefault = in.readByte() != 0;
    }

    public static final Creator<Locations> CREATOR = new Creator<Locations>() {
        @Override
        public Locations createFromParcel(Parcel in) {
            return new Locations(in);
        }

        @Override
        public Locations[] newArray(int size) {
            return new Locations[size];
        }
    };

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(address);
        parcel.writeString(name);
        parcel.writeString(telephone);
        parcel.writeByte((byte) (isDefault ? 1 : 0));
    }
}
