package at.fhtw.models;

import java.util.HashMap;
import java.util.Map;

public class TradingDeal {
    private String Id;
    private String CardToTrade;
    private String Type;
    private int MinimumDamage;
    private int Seller;

    public TradingDeal(String id, String cardToTrade, String type, int minimumDamage) {
        Id = id;
        CardToTrade = cardToTrade;
        Type = type;
        MinimumDamage = minimumDamage;
    }

    public TradingDeal(String id, String cardToTrade, int seller, String type, int minimumDamage) {
        Id = id;
        CardToTrade = cardToTrade;
        Type = type;
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

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
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
            put("Type", getType());
            put("MinimumDamage", Integer.toString(getMinimumDamage()));
        }};
    }
}
