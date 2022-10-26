package at.fhtw.httpserver.request;

import at.fhtw.httpserver.enums.Method;

import java.util.HashMap;
import java.util.Map;

public class Request {
    private Method method;
    private String pathname;
    private String[] pathParts;
    private String params;
    private Map<String, String> headerMap = new HashMap<>();
    private String body;
    private int contentLength = 0;

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
        String pathWithoutSlash = pathname;
        if(pathWithoutSlash.startsWith("/"))
            pathWithoutSlash = pathWithoutSlash.substring(1);
        if(pathWithoutSlash.endsWith("/"))
            pathWithoutSlash.substring(0, pathWithoutSlash.length() - 1);
        if(pathWithoutSlash.equals(""))
            this.pathParts = null;
        else
            this.pathParts = pathWithoutSlash.split("/");
    }

    public String addHeader(String key, String value) {
        if(key != null && key.equals("Content-Length")){
            this.contentLength = Integer.parseInt(value);
        }
        return headerMap.put(key, value);
    }

    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    public void setHeaderMap(Map<String, String> headerMap) {
        this.headerMap = headerMap;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getContentLength() {
        return contentLength;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String[] getPathParts() {
        return pathParts;
    }

    public String getServiceRoute(){
        if(this.pathParts == null)
            return null;
        return "/" + this.pathParts[0];
    }
}
