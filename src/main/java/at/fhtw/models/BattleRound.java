package at.fhtw.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

import java.util.HashMap;
import java.util.Map;

public class BattleRound {
    private String PlayerA;
    private String PlayerB;
    private String cardA;
    private String cardB;
    private float cardADmg;
    private float cardBDmg;
    private String winnerCard;

    public BattleRound(String playerA, String playerB, String cardA, String cardB, float cardADmg, float cardBDmg, String winnerCard) {
        PlayerA = playerA;
        PlayerB = playerB;
        this.cardA = cardA;
        this.cardB = cardB;
        this.cardADmg = cardADmg;
        this.cardBDmg = cardBDmg;
        this.winnerCard = winnerCard;
    }

    public String getPlayerA() {
        return PlayerA;
    }

    public void setPlayerA(String playerA) {
        PlayerA = playerA;
    }

    public String getPlayerB() {
        return PlayerB;
    }

    public void setPlayerB(String playerB) {
        PlayerB = playerB;
    }

    public String getCardA() {
        return cardA;
    }

    public void setCardA(String cardA) {
        this.cardA = cardA;
    }

    public String getCardB() {
        return cardB;
    }

    public void setCardB(String cardB) {
        this.cardB = cardB;
    }

    public float getCardADmg() {
        return cardADmg;
    }

    public void setCardADmg(float cardADmg) {
        this.cardADmg = cardADmg;
    }

    public float getCardBDmg() {
        return cardBDmg;
    }

    public void setCardBDmg(float cardBDmg) {
        this.cardBDmg = cardBDmg;
    }

    public String getWinnerCard() {
        return winnerCard;
    }

    public void setWinnerCard(String winnerCard) {
        this.winnerCard = winnerCard;
    }

    @JsonAnyGetter
    public Map<String, String> getBattleRoundProperties(){
        return new HashMap<>(){{
            put("PlayerA", getPlayerA());
            put("PlayerB", getPlayerB());
            put("CardA", getCardA());
            put("CardB", getCardB());
            put("CardA Dmg", Float.toString(getCardADmg()));
            put("CardB Dmg", Float.toString(getCardBDmg()));
            put("Winner Card", getWinnerCard());
        }};
    }
}
