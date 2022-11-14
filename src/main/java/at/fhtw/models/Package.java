package at.fhtw.models;

import com.google.gson.annotations.SerializedName;

public class Package {
    @SerializedName(value = "p_id", alternate = "P_id")
    private int p_id;
    @SerializedName(value = "price", alternate = "Price")
    private int price;
    @SerializedName(value = "name", alternate = "Name")
    private String name;

    public Package(int p_id, int price, String name) {
        this.p_id = p_id;
        this.price = price;
        this.name = name;
    }

    public int getP_id() {
        return p_id;
    }

    public void setP_id(int p_id) {
        this.p_id = p_id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
