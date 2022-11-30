package at.fhtw.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class UserStats {
    private int s_id;
    private int elo;
    private int wins;
    private int losses;
    private int total;
    private String name;

    public UserStats(int elo, int wins, int losses, String name) {
        this.elo = elo;
        this.wins = wins;
        this.losses = losses;
        this.name = name;
    }

    public int getS_id() {
        return s_id;
    }

    public void setS_id(int s_id) {
        this.s_id = s_id;
    }

    public int getElo() {
        return elo;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonAnyGetter
    public Map<String, String> getStats(){
        return new HashMap<>(){{
            put("Name", getName());
            put("Elo", Integer.toString(getElo()));
            put("Wins", Integer.toString(getWins()));
            put("Losses", Integer.toString(getLosses()));
        }};
    }
}
