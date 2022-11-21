package at.fhtw.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class Card {
    @SerializedName(value = "c_id", alternate = "Id")
    private String c_id;
    @SerializedName(value = "c_name", alternate = "Name")
    private String c_name;
    @SerializedName(value = "c_dmg", alternate = "Damage")
    private float c_dmg;
    private int pckg_id;
    private int user_id;
    private int stack_id;
    private int deck_id;

    public Card(String c_id, String c_name, float c_dmg, int pckg_id, int user_id, int stack_id, int deck_id) {
        this.c_id = c_id;
        this.c_name = c_name;
        this.c_dmg = c_dmg;
        this.pckg_id = pckg_id;
        this.user_id = user_id;
        this.stack_id = stack_id;
        this.deck_id = deck_id;
    }

    public Card(String c_id, String c_name, float c_dmg) {
        this.c_id = c_id;
        this.c_name = c_name;
        this.c_dmg = c_dmg;
    }

    public String getC_id() {
        return c_id;
    }

    public void setC_id(String c_id) {
        this.c_id = c_id;
    }

    public String getC_name() {
        return c_name;
    }

    public void setC_name(String c_name) {
        this.c_name = c_name;
    }

    public float getC_dmg() {
        return c_dmg;
    }

    public void setC_dmg(int c_dmg) {
        this.c_dmg = c_dmg;
    }

    public int getPckg_id() {
        return pckg_id;
    }

    public void setPckg_id(int pckg_id) {
        this.pckg_id = pckg_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getStack_id() {
        return stack_id;
    }

    public void setStack_id(int stack_id) {
        this.stack_id = stack_id;
    }

    public int getDeck_id() {
        return deck_id;
    }

    public void setDeck_id(int deck_id) {
        this.deck_id = deck_id;
    }

    @JsonAnyGetter
    public Map<String, String> getCardProperties(){
        return new HashMap<>(){{
            put("Id", getC_id());
            put("Name", getC_name());
            put("Damage", Float.toString(getC_dmg()));
        }};
    }
}
