package at.fhtw.service.battle;

import at.fhtw.dal.repo.*;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.models.*;
import at.fhtw.service.card.CardController;
import at.fhtw.service.deck.DeckController;
import at.fhtw.service.user.UserController;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static at.fhtw.service.Service.unitOfWork;

public class BattleController {
    public BattleRepo battleRepo;
    public BattleController() {
        this.battleRepo = new BattleRepo();
    }

    public Response handlePost(Request request) {
        if(request.getAuthorizedClient() == null)
            return new Response(HttpStatus.UNAUTHORIZED,
                    ContentType.PLAIN_TEXT,
                    "Authentication information is missing or invalid");

        User user = new UserController().getUserWithUserName(request.getAuthorizedClient().getUsername());

        try{
            Battle battle =  createBattle(user);
            battleRepo.addUser(user, battle, unitOfWork);       //add user to battle
            if(battle.getPlayerA() != 0 && battle.getPlayerB() != 0) {      //check if lobby is full
                if (startBattle(battle)){
                    battleRepo.setWinner(battle, unitOfWork);
                    //clear deck
                    new DeckRepo().clearDeck(battle, unitOfWork);
                    unitOfWork.commit();
                }
                else{
                    unitOfWork.rollback();      //TODO maybe increase total of user_stats
                    return new Response(HttpStatus.OK,
                            ContentType.PLAIN_TEXT,
                            "");
                }
            }else
                waitForBattle(battle);

            //get battle stats
            ResultSet resultSetBattle = battleRepo.getBattle(battle.getB_id(), unitOfWork);
            if(resultSetBattle.next()){
                battle.setPlayerA(resultSetBattle.getInt(2));
                battle.setPlayerB(resultSetBattle.getInt(3));
                battle.setWinner(resultSetBattle.getInt(4));
            }else{
                throw new SQLException("Error fetching battle");
            }

            ResultSet resultSetRounds = battleRepo.getAllRounds(battle.getB_id(), unitOfWork);
            String playerAName = new UserRepo().getUserById(battle.getPlayerA(), unitOfWork);
            String playerBName = new UserRepo().getUserById(battle.getPlayerB(), unitOfWork);

            List<Map<String, String>> rounds = new ArrayList<Map<String, String>>();
            String cardAId, cardBId, winnerId;
            CardRepo cardRepo = new CardRepo();
            while(resultSetRounds.next()) {
                cardAId = resultSetRounds.getString(2);
                cardBId = resultSetRounds.getString(3);
                winnerId = resultSetRounds.getString(4);

                ResultSet rsCardA = cardRepo.getCardById(cardAId, unitOfWork);
                ResultSet rsCardB = cardRepo.getCardById(cardBId, unitOfWork);

                Card cardA = null, cardB = null;

                if (rsCardA.next()) {
                    cardA = new Card(cardAId,
                            rsCardA.getString(2),
                            rsCardA.getFloat(3));
                } else {
                    throw new SQLException();
                }

                if (rsCardB.next()) {
                    cardB = new Card(cardBId,
                            rsCardB.getString(2),
                            rsCardB.getFloat(3));
                } else {
                    throw new SQLException();
                }

                String winnerCardName;
                if (winnerId.equals(cardA.getC_id()))
                    winnerCardName = cardA.getC_name();
                else
                    winnerCardName = cardB.getC_name();

                rounds.add(new BattleRound(playerAName,
                        playerBName,
                        cardA.getC_name(),
                        cardB.getC_name(),
                        cardA.getC_dmg(),
                        cardB.getC_dmg(),
                        winnerCardName).getBattleRoundProperties());
            }
            //get winner
            String winner = new UserRepo().getUserById(battle.getWinner(), unitOfWork);
            rounds.add(
                    new HashMap<>(){{
                            put("Winner:", winner);
                    }}
            );

            return new Response(HttpStatus.OK,
                    ContentType.JSON,
                    new ObjectMapper().writeValueAsString(rounds));
        }catch(Exception e){
            e.printStackTrace();
            unitOfWork.rollback();
        }
        return new Response(HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.PLAIN_TEXT,
                "");
    }

    private void waitForBattle(Battle battle) throws InterruptedException, SQLException {
        for(int i = 0; i < 60; i++){
            if(battleRepo.checkFinished(battle, unitOfWork))
                return;
            Thread.sleep(1000);
        }
    }

    private boolean startBattle(Battle battle) throws SQLException {
        System.out.println("Battle started");
        //get cards from players
        DeckController deckController = new DeckController();
        ResultSet rsDeckA = deckController.getDeckFromPlayer(battle.getPlayerA());
        ResultSet rsDeckB = deckController.getDeckFromPlayer(battle.getPlayerB());
        if(rsDeckB == null || rsDeckA == null){
            return false;
        }
        List<Card> deckA = new ArrayList<>();
        List<Card> deckB = new ArrayList<>();

        while(rsDeckA.next()){
            deckA.add(new Card(rsDeckA.getString(1),
                    rsDeckA.getString(2),
                    rsDeckA.getFloat(3),
                    rsDeckA.getInt(6),
                    rsDeckA.getString(7)));
        }

        while(rsDeckB.next()){
            deckB.add(new Card(rsDeckB.getString(1),
                    rsDeckB.getString(2),
                    rsDeckB.getFloat(3),
                    rsDeckB.getInt(6),
                    rsDeckB.getString(7)));
        }

        //play max 100 rounds
        StatsRepo statsRepo = new StatsRepo();
        CardRepo cardRepo = new CardRepo();
        for(int round = 0; round < 100; round++){
            //get random cards
            Card cardA = deckA.get(new Random().nextInt(deckA.size()));
            Card cardB = deckB.get(new Random().nextInt(deckB.size()));
            Card winner;

            //start battle
            Card winnerCard = battleRound(cardA, cardB);
            if(winnerCard.equals(cardA)){       //PlayerA wins this round
                //transfer card
                winner = cardA;
                deckB.remove(cardB);
                deckA.add(cardB);
                cardB.setDeck_id(cardA.getDeck_id());
                cardRepo.transferCard(cardB.getC_id(), battle.getPlayerA(), cardB.getDeck_id(), unitOfWork);
            }else{                              //PlayerB wins this round
                winner = cardB;
                deckA.remove(cardA);
                deckB.add(cardA);
                cardA.setDeck_id(cardB.getDeck_id());
                cardRepo.transferCard(cardA.getC_id(), battle.getPlayerB(), cardA.getDeck_id(), unitOfWork);
            }

            battleRepo.addRound(battle, cardA, cardB, winner, unitOfWork);

            //check for winner
            if(deckA.isEmpty()){        //PlayerB won
                //update stats
                statsRepo.updateStats(battle.getPlayerA(), false, unitOfWork);
                statsRepo.updateStats(battle.getPlayerB(), true, unitOfWork);
                battle.setWinner(battle.getPlayerB());
                return true;
            }else if(deckB.isEmpty()){        //PlayerA won
                //update stats
                statsRepo.updateStats(battle.getPlayerA(), true, unitOfWork);
                statsRepo.updateStats(battle.getPlayerB(), false, unitOfWork);
                battle.setWinner(battle.getPlayerA());
                return true;
            }
        }

        //draw
        return false;
    }

    private Card battleRound(Card cardA, Card cardB) {
        //pure monster fight
        if(cardA.getType().equals(CardType.Monster) && cardB.getType().equals(CardType.Monster)){
            return monsterFight(cardA, cardB);
        }else
            return mixedFight(cardA, cardB);

    }

    private Card mixedFight(Card cardA, Card cardB) {
        //specialties
        if(cardA.getC_name().contains("Knight") && cardB.getC_name().contains("WaterSpell"))
            return cardB;
        else if(cardB.getC_name().contains("Knight") && cardA.getC_name().contains("WaterSpell"))
            return cardA;
        else if(cardA.getC_name().contains("Kraken") && cardB.getType().equals(CardType.Spell))
            return cardA;
        else if(cardB.getC_name().contains("Kraken") && cardA.getType().equals(CardType.Spell))
            return cardB;

        if(calcDmg(cardA, cardB) > calcDmg(cardB, cardA))
            return cardA;
        else
            return cardB;
    }

    private float calcDmg(Card self, Card opponent) {
        if(self.getC_name().contains("Water") && opponent.getC_name().contains("Fire"))
            return self.getC_dmg() * 2;
        else if(self.getC_name().contains("Fire") && !opponent.getC_name().contains("Water") && !opponent.getC_name().contains("Fire"))
            return self.getC_dmg() * 2;
        else if(opponent.getC_name().contains("Water") && !self.getC_name().contains("Water") && !self.getC_name().contains("Fire"))
            return self.getC_dmg() * 2;
        else if(self.getC_name().contains("Water") && opponent.getC_name().contains("Water"))
            return self.getC_dmg();
        else if(self.getC_name().contains("Fire") && opponent.getC_name().contains("Fire"))
            return self.getC_dmg();
        else if(!self.getC_name().contains("Water") && !self.getC_name().contains("Fire") && !opponent.getC_name().contains("Water") && !opponent.getC_name().contains("Fire"))
            return self.getC_dmg();
        else
            return self.getC_dmg() / 2;
    }

    private Card monsterFight(Card cardA, Card cardB) {
        //specialties
        if(cardA.getC_name().contains("Goblin") && cardB.getC_name().contains("Dragon"))
            return cardB;
        else if(cardB.getC_name().contains("Goblin") && cardA.getC_name().contains("Dragon"))
            return cardA;
        else if(cardA.getC_name().contains("Wizzard") && cardB.getC_name().contains("Ork"))
            return cardA;
        else if(cardB.getC_name().contains("Wizzard") && cardA.getC_name().contains("Ork"))
            return cardB;
        else if(cardA.getC_name().contains("FireElve") && cardB.getC_name().contains("Dragon"))
            return cardA;
        else if(cardB.getC_name().contains("FireElve") && cardA.getC_name().contains("Dragon"))
            return cardB;

        if(cardA.getC_dmg() > cardB.getC_dmg())
            return cardA;
        else
            return cardB;
    }

    private Battle createBattle(User user) throws Exception {
        //check if there is already an open lobby
        ResultSet resultSetBattle = battleRepo.fetchLobby(unitOfWork);
        Battle battle = new Battle();
        if(resultSetBattle.next()) {     //lobby exists
            battle.setB_id(resultSetBattle.getInt(1));
            battle.setPlayerA(resultSetBattle.getInt(2));
            return battle;
        }
        battle.setB_id(battleRepo.createLobby(unitOfWork));  //no lobby exists, create new one
        return battle;
    }
}
