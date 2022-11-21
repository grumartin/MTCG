package at.fhtw.service.pckg;

import at.fhtw.dal.repo.CardRepo;
import at.fhtw.dal.repo.PackageRepo;
import at.fhtw.dal.repo.UserRepo;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.models.Card;
import at.fhtw.models.Pckg;
import at.fhtw.models.User;
import at.fhtw.service.user.UserController;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static at.fhtw.service.Service.unitOfWork;

public class TransactionController {
    public Response handlePost(Request request){
        if(request.getAuthorizedClient() == null)
            return new Response(HttpStatus.UNAUTHORIZED,
                    ContentType.PLAIN_TEXT,
                    "");

        User user = new UserController().getUserWithUserName(request.getAuthorizedClient().getUsername());

        if(user.getCoins() < 5)
            return new Response(HttpStatus.FORBIDDEN,
                    ContentType.PLAIN_TEXT,
                    "");

        try{
            ResultSet resultSetPackage = new PackageRepo().acquirePackage(user, unitOfWork);       //get random package
            Pckg pckg;
            if(resultSetPackage.next()){
                pckg = new Pckg(resultSetPackage.getInt(1),
                        resultSetPackage.getInt(2),
                        resultSetPackage.getString(3));
            }else{  //No card package available
                return new Response(HttpStatus.NOT_FOUND,
                        ContentType.PLAIN_TEXT,
                        "");
            }

            CardRepo cardRepo = new CardRepo();
            new UserRepo().updateUserCoins(user, 5, unitOfWork);        //Reduce spent coins
            cardRepo.addCardsToUser(pckg, user, unitOfWork);                     //add cards to user
            ResultSet resultSetCards = cardRepo.acquireCards(pckg, unitOfWork);  //get cards for response
            cardRepo.removeCardsFromPackage(pckg, unitOfWork);                   //remove cards from package
            new PackageRepo().deletePackage(pckg, unitOfWork);                   //delete card package


            //Convert result into List of Map => Array of Cards
            List<Map<String, String>> cards = new ArrayList<Map<String, String>>();
            while(resultSetCards.next()){
                cards.add(new Card(resultSetCards.getString(1),
                        resultSetCards.getString(2),
                        resultSetCards.getFloat(3)).getCardProperties());
            }
            unitOfWork.commit();
            return new Response(HttpStatus.OK,
                    ContentType.PLAIN_TEXT,
                    new ObjectMapper().writeValueAsString(cards));

        }catch(Exception e){
            unitOfWork.rollback();
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.PLAIN_TEXT,
                    "");
        }
    }
}
