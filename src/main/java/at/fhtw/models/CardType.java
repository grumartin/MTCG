package at.fhtw.models;

import com.google.gson.annotations.SerializedName;

public enum CardType {
    @SerializedName("monster")
    Monster,
    @SerializedName("spell")
    Spell
}
