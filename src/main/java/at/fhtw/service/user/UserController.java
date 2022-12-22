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

        UnitOfWork unitOfWork = new UnitOfWork();
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
        unitOfWork.close();
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
        UnitOfWork unitOfWork = new UnitOfWork();
        User user = getUserWithUserName(pathParts.get(1), unitOfWork);
        unitOfWork.commit();
        if(user != null){
            if(user.getUid() == request.getAuthorizedClient().getUid() || request.getAuthorizedClient().getUsername().equals("admin")){
                unitOfWork.close();
                return HttpStatus.OK;
            }
        }
        unitOfWork.close();
        return HttpStatus.NOT_FOUND;
    }

    public Response handleGet(Request request){
        HttpStatus httpStatus = handleAuthorization(request);
        switch (httpStatus){
            case NOT_FOUND:
                return new Response(HttpStatus.NOT_FOUND,
                        ContentType.PLAIN_TEXT,
                        "User not found.");
            case UNAUTHORIZED:
                return new Response(HttpStatus.UNAUTHORIZED,
                        ContentType.PLAIN_TEXT,
                        "Authentication information is missing or invalid");
        }

        List<String> pathParts = request.getPathParts();
        UnitOfWork unitOfWork = new UnitOfWork();
        User user = getUserWithUserName(pathParts.get(1), unitOfWork);
        unitOfWork.commit();

        try{
            unitOfWork.close();
            return new Response(HttpStatus.OK,
                    ContentType.JSON,
                    mapper.writeValueAsString(user.getUserProperties()));
        }catch(IOException exception){
            exception.printStackTrace();
        }
        unitOfWork.close();
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
                        "User not found.");
            case UNAUTHORIZED:
                return new Response(HttpStatus.UNAUTHORIZED,
                        ContentType.PLAIN_TEXT,
                        "Authentication information is missing or invalid");
        }


        List<String> pathParts = request.getPathParts();
        UnitOfWork unitOfWork = new UnitOfWork();
        User oldUser = getUserWithUserName(pathParts.get(1), unitOfWork);
        User newUser = gson.fromJson(request.getBody(), User.class);


        try{
            userRepo.updateUser(newUser, oldUser, unitOfWork);
            unitOfWork.commit();
            unitOfWork.close();
            return new Response(HttpStatus.OK,
                    ContentType.PLAIN_TEXT,
                    "User successfully updated.");
        }catch (SQLException exception){
            unitOfWork.rollback();
            exception.printStackTrace();
        }
        unitOfWork.close();
        return new Response(HttpStatus.NOT_FOUND,
                ContentType.PLAIN_TEXT,
                "User not found");
    }

    public User getUserWithUserName(String username, UnitOfWork unitOfWork){
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

    public Response handleDelete(Request request) {
        if(request.getAuthorizedClient() == null )
            return new Response(HttpStatus.UNAUTHORIZED,
                    ContentType.PLAIN_TEXT,
                    "Unauthorized");

        if(!request.getAuthorizedClient().getUsername().equals("admin"))
            return new Response(HttpStatus.FORBIDDEN,
                    ContentType.PLAIN_TEXT,
                    "Only the admin can delete a user!");

        int uid = Integer.parseInt(request.getBody().trim());
        UnitOfWork unitOfWork = new UnitOfWork();
        UserRepo userRepo = new UserRepo();
        try{
            userRepo.getUserById(uid, unitOfWork);      //try if user with given uid exists
            userRepo.deleteUser(uid, unitOfWork);
        }catch (Exception e){
            unitOfWork.rollback();
            unitOfWork.close();
            if(e.getClass().equals(SQLException.class))     //No user with given id exists
                return new Response(HttpStatus.NOT_FOUND,
                        ContentType.PLAIN_TEXT,
                        "");

            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.PLAIN_TEXT,
                    "");
        }
        unitOfWork.commit();
        unitOfWork.close();
        return new Response(HttpStatus.OK,
                ContentType.PLAIN_TEXT,
                "User successfully deleted");

    }
}
