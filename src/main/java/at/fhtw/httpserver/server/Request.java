package at.fhtw.httpserver.server;

import at.fhtw.httpserver.http.Method;
import at.fhtw.models.User;
import at.fhtw.service.user.UserController;

import java.util.ArrayList;
import java.util.List;

public class Request {
    private Method method;
    private String urlContent;
    private String pathname;
    private List<String> pathParts;
    private String params;
    private HeaderMap headerMap =  new HeaderMap();
    private String body;
    private User authorizedClient;

    public String getServiceRoute(){
        if (this.pathParts == null ||
            this.pathParts.isEmpty()) {
            return null;
        }

        return '/' + this.pathParts.get(0);
    }

    public String getUrlContent(){
        return this.urlContent;
    }

    public void setUrlContent(String urlContent) {
        this.urlContent = urlContent;
        Boolean hasParams = urlContent.indexOf("?") != -1;

        if (hasParams) {
            String[] pathParts =  urlContent.split("\\?");
            this.setPathname(pathParts[0]);
            this.setParams(pathParts[1]);
        }
        else
        {
            this.setPathname(urlContent);
            this.setParams(null);
        }
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getPathname() {
        return pathname;
    }


    public void setPathname(String pathname) {
        this.pathname = pathname;
        String[] stringParts = pathname.split("/");
        this.pathParts = new ArrayList<>();
        for (String part :stringParts)
        {
            if (part != null &&
                part.length() > 0)
            {
                this.pathParts.add(part);
            }
        }

    }
    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public HeaderMap getHeaderMap() {
        return headerMap;
    }

    public void setHeaderMap(HeaderMap headerMap) {
        this.headerMap = headerMap;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<String> getPathParts() {
        return pathParts;
    }

    public void setPathParts(List<String> pathParts) {
        this.pathParts = pathParts;
    }


    public void requestAuthorization(){
        String authorization = this.headerMap.getHeader("Authorization");

        if(authorization != null){
            String[] authParts = authorization.split(" ");
            String[] tokenParts = authParts[1].split("-");

            if(tokenParts.length < 2)
                return;

            UserController userController = new UserController();
            User user = userController.getUserWithUserName(tokenParts[0]);

            if(user != null && (tokenParts[1].equals(user.getToken()) || tokenParts[0].equals("admin")))
                authorizedClient = user;
        }
    }

    public User getAuthorizedClient() {
        return authorizedClient;
    }
}
