package at.fhtw.models;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class TradingDeal {
    private String Id;
    private String CardToTrade;
    @SerializedName(value = "type", alternate = "Type")
    private CardType type;
    private int MinimumDamage;
    private int Seller;

    public TradingDeal(String id, String cardToTrade, String type, int minimumDamage) {
        Id = id;
        CardToTrade = cardToTrade;
        if(type.equals("monster"))
            this.type = CardType.Monster;
        else
            this.type = CardType.Spell;
        MinimumDamage = minimumDamage;
    }

    public TradingDeal(String id, String cardToTrade, int seller, String type, int minimumDamage) {
        Id = id;
        CardToTrade = cardToTrade;
        if(type.equals("monster"))
            this.type = CardType.Monster;
        else
            this.type = CardType.Spell;
        Seller = seller;
        MinimumDamage = minimumDamage;
    }

    public int getSeller() {
        return Seller;
    }

    public void setSeller(int seller) {
        Seller = seller;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getCardToTrade() {
        return CardToTrade;
    }

    public void setCardToTrade(String cardToTrade) {
        CardToTrade = cardToTrade;
    }

    public CardType getType() {
        return type;
    }

    public String getTypeString(){
        if(this.type != null){
            if(this.type.equals(CardType.Monster))
                return "monster";
            else
                return "spell";
        }
        return null;
    }

    public void setType(String type) {
        if(type.equals("monster"))
            this.type = CardType.Monster;
        else
            this.type = CardType.Spell;
    }

    public int getMinimumDamage() {
        return MinimumDamage;
    }

    public void setMinimumDamage(int minimumDamage) {
        MinimumDamage = minimumDamage;
    }

    public Map<String, String> getDealProperties() {
        return new HashMap<>(){{
            put("Id", getId());
            put("CardToTrade", getCardToTrade());
            put("Type", getTypeString());
            put("MinimumDamage", Integer.toString(getMinimumDamage()));
        }};
    }
}
