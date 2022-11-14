package at.fhtw.models;

import com.google.gson.annotations.SerializedName;

public class Card {
    @SerializedName(value = "c_id", alternate = "Id")
    private int c_id;
    @SerializedName(value = "c_name", alternate = "Name")
    private String c_name;
    @SerializedName(value = "c_dmg", alternate = "Damage")
    private int c_dmg;
    private int pckg_id;
    private int user_id;
    private int stack_id;
    private int deck_id;

    public Card(int c_id, String c_name, int c_dmg, int pckg_id, int user_id, int stack_id, int deck_id) {
        this.c_id = c_id;
        this.c_name = c_name;
        this.c_dmg = c_dmg;
        this.pckg_id = pckg_id;
        this.user_id = user_id;
        this.stack_id = stack_id;
        this.deck_id = deck_id;
    }

    public int getC_id() {
        return c_id;
    }

    public void setC_id(int c_id) {
        this.c_id = c_id;
    }

    public String getC_name() {
        return c_name;
    }

    public void setC_name(String c_name) {
        this.c_name = c_name;
    }

    public int getC_dmg() {
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
}
