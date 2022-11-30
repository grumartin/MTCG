package at.fhtw.service.user;

import at.fhtw.dal.UnitOfWork;
import at.fhtw.dal.repo.UserRepo;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.models.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static at.fhtw.service.Service.unitOfWork;

public class UserController {
    private Gson gson;
    private UserRepo userRepo;
    private ObjectMapper mapper;
    public UserController() {
        this.gson = new Gson();
        this.userRepo = new UserRepo();
        this.mapper = new ObjectMapper();
    }

    public Response handlePost(Request request){
        User user = gson.fromJson(request.getBody(), User.class);

        if(user == null || user.getUsername() == null || user.getPassword() == null){
            return new Response(HttpStatus.NOT_FOUND,
                    ContentType.PLAIN_TEXT,
                    "");
        }

        //create hashed password
        /*
        String hashedPw = null;
        try{
            MessageDigest md = MessageDigest.getInstance("SHA3-256");
            byte[] result = md.digest(user.getPassword().getBytes());
            hashedPw = result.toString();
        }catch (NoSuchAlgorithmException exception){
            exception.printStackTrace();
        }


        //create token
        SecureRandom secureRandom = new SecureRandom();
        byte bytes[] = new byte[20];
        secureRandom.nextBytes(bytes);
        String token = bytes.toString();
         */
        user.setBio("MTCG Player");
        user.setToken("mtcgToken");
        user.setCoins(20);

        HttpStatus httpStatus = userRepo.addUser(user, unitOfWork);

        String content;
        switch (httpStatus){
            case CREATED -> {
                content = "User successfully created";
                unitOfWork.commit();
            }
            case CONFLICT -> {
                content = "User with same username already registered";
                unitOfWork.rollback();
            }
            default -> {
                content = "";
                unitOfWork.rollback();
            }
        }
        return new Response(httpStatus,
                ContentType.PLAIN_TEXT,
                content);
    }

    public HttpStatus handleAuthorization(Request request){
        if(request.getAuthorizedClient() == null){
            System.out.println("Unauthorized!");
            return HttpStatus.UNAUTHORIZED;
        }

        List<String> pathParts = request.getPathParts();
        if(pathParts.get(1) == null){
            return HttpStatus.NOT_FOUND;
        }

        User user = getUserWithUserName(pathParts.get(1));
        if(user != null){
            if(user.getUid() == request.getAuthorizedClient().getUid() || request.getAuthorizedClient().getUsername().equals("admin")){
                return HttpStatus.OK;
            }
        }

        return HttpStatus.NOT_FOUND;
    }

    public Response handleGet(Request request){
        HttpStatus httpStatus = handleAuthorization(request);
        switch (httpStatus){
            case NOT_FOUND:
                return new Response(HttpStatus.NOT_FOUND,
                        ContentType.PLAIN_TEXT,
                        "");
            case UNAUTHORIZED:
                return new Response(HttpStatus.UNAUTHORIZED,
                        ContentType.PLAIN_TEXT,
                        "");
        }

        List<String> pathParts = request.getPathParts();
        User user = getUserWithUserName(pathParts.get(1));

        try{
            return new Response(HttpStatus.OK,
                    ContentType.JSON,
                    mapper.writeValueAsString(user.getUserProperties()));
        }catch(IOException exception){
            exception.printStackTrace();
        }

        return new Response(HttpStatus.NOT_FOUND,
                ContentType.PLAIN_TEXT,
                "");
    }

    public Response handlePut(Request request){
        HttpStatus httpStatus = handleAuthorization(request);
        switch (httpStatus){
            case NOT_FOUND:
                return new Response(HttpStatus.NOT_FOUND,
                        ContentType.PLAIN_TEXT,
                        "");
            case UNAUTHORIZED:
                return new Response(HttpStatus.UNAUTHORIZED,
                        ContentType.PLAIN_TEXT,
                        "");
        }


        List<String> pathParts = request.getPathParts();
        User oldUser = getUserWithUserName(pathParts.get(1));
        User newUser = gson.fromJson(request.getBody(), User.class);

        try{
            userRepo.updateUser(newUser, oldUser, unitOfWork);
            unitOfWork.commit();
            return new Response(HttpStatus.OK,
                    ContentType.PLAIN_TEXT,
                    "User successfully updated.");
        }catch (SQLException exception){
            unitOfWork.rollback();
            exception.printStackTrace();
        }

        return new Response(HttpStatus.NOT_FOUND,
                ContentType.PLAIN_TEXT,
                "User not found");
    }

    public User getUserWithUserName(String username){
        try{
            return buildUser(this.userRepo.getUser(username, unitOfWork));
        }catch(SQLException exception){
            exception.printStackTrace();
        }
        return null;
    }

    public User buildUser(ResultSet result) throws SQLException {
        if (result.next()) {
            User user = new User(result.getInt(1),
                    result.getString(2),
                    result.getString(3),
                    result.getInt(4),
                    result.getString(5),
                    result.getString(6));
            return user;
        }
        return null;
    }

    public boolean authorize(User user, String password){
        /*
        String hashedPw = null;
        try{
            MessageDigest md = MessageDigest.getInstance("SHA3-256");
            byte[] result = md.digest(password.getBytes());
            hashedPw = result.toString();
        }catch (NoSuchAlgorithmException exception){
            exception.printStackTrace();
        }
        System.out.println(hashedPw);
        System.out.println(user.getPassword());
         */
        return password.equals(user.getPassword());
    }
}
