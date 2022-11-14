package at.fhtw.models;

import com.google.gson.annotations.SerializedName;
import org.codehaus.jackson.annotate.JsonAnyGetter;

import java.util.HashMap;
import java.util.Map;

public class User {
    private int uid;
    @SerializedName(value = "username", alternate = "Username")
    private String username;
    @SerializedName(value = "password", alternate = "Password")
    private String password;
    @SerializedName(value = "coins", alternate = "Coins")
    private int coins;
    @SerializedName(value = "bio", alternate = "Bio")
    private String bio;
    @SerializedName(value = "token", alternate = "Token")
    private String token;

    public User(int uid, String username, String password, int coins, String bio, String token) {
        this.uid = uid;
        this.username = username;
        this.password = password;
        this.coins = coins;
        this.bio = bio;
        this.token = token;
    }
    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
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

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @JsonAnyGetter
    public Map<String, String> getUserProperties(){
        return new HashMap<>(){{
            put("Uid", Integer.toString(getUid()));
            put("Username", getUsername());
            put("Bio", getBio());
            put("Coins", Integer.toString(getCoins()));
        }};
    }
}
