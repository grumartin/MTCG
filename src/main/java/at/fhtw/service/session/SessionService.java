package at.fhtw.service.session;

import at.fhtw.dal.UnitOfWork;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.models.User;
import at.fhtw.service.Service;
import at.fhtw.service.user.UserController;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class SessionService implements Service {
    @Override
    public Response handleRequest(Request request) {
        Properties credentials = null;
        try{
            credentials = new ObjectMapper().readValue(request.getBody(), Properties.class);
        }catch(IOException exception){
            exception.printStackTrace();
        }

        if(credentials.getProperty("Username") != null && credentials.getProperty("Password") != null){
            UserController userController = new UserController();
            UnitOfWork unitOfWork = new UnitOfWork();
            User user = userController.getUserWithUserName(credentials.getProperty("Username"), unitOfWork);
            unitOfWork.commit();
            unitOfWork.close();

            if(user != null){
                if(userController.authorize(user, credentials.getProperty("Password"))){
                    return new Response(HttpStatus.OK,
                            ContentType.PLAIN_TEXT,
                            "User login successful");
                }
            }
        }
        return new Response(HttpStatus.UNAUTHORIZED,
                ContentType.PLAIN_TEXT,
                "Invalid username/password provided");
    }
}
