package at.fhtw.models;

public class Battle {
    private int b_id;
    private int playerA = 0;
    private int playerB = 0;
    private int winner;

    public Battle(int b_id, int playerA, int playerB, int winner) {
        this.b_id = b_id;
        this.playerA = playerA;
        this.playerB = playerB;
        this.winner = winner;
    }

    public Battle() {
    }

    public int getB_id() {
        return b_id;
    }

    public void setB_id(int b_id) {
        this.b_id = b_id;
    }

    public int getPlayerA() {
        return playerA;
    }

    public void setPlayerA(int playerA) {
        this.playerA = playerA;
    }

    public int getPlayerB() {
        return playerB;
    }

    public void setPlayerB(int playerB) {
        this.playerB = playerB;
    }

    public int getWinner() {
        return winner;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }
}
