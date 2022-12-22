package at.fhtw.service.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    @Test
    void testUserServiceDuplicateUsername() throws IOException {     //should throw IOException, because user already exists
        Assertions.assertThrows(IOException.class, () -> {
            URL url = new URL("http://localhost:10001/users");
            URLConnection urlConnection = url.openConnection();
            urlConnection.setDoOutput(true);
            OutputStream outputStream = urlConnection.getOutputStream();
            PrintWriter printWriter = new PrintWriter(outputStream);
            printWriter.write("{\"Username\": \"Hans\", \r\n \"Password\":\"65432\"}");
            printWriter.close();
            InputStream inputStream = urlConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        });
    }

    @Test
    void testUserServiceCreateUser() throws IOException {
        URL url = new URL("http://localhost:10001/users");
        URLConnection urlConnection = url.openConnection();
        urlConnection.setDoOutput(true);
        OutputStream outputStream = urlConnection.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.write("{\"Username\": \"admin\", \r\n \"Password\":\"12345\"}");
        printWriter.close();
        InputStream inputStream = urlConnection.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        Assertions.assertEquals(bufferedReader.readLine(), "User successfully created");
    }

    @Test
    void testUserServiceGetUser() throws IOException {
        URL url = new URL("http://localhost:10001/users/Hans");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("Authorization", "Basic Hans-mtcgToken");
        int responseCode = urlConnection.getResponseCode();

        if(responseCode == HttpURLConnection.HTTP_OK){
            InputStream inputStream = urlConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null) {
                System.out.println(inputLine);
            }
        }else {
            Assertions.assertFalse(true);
        }
    }

    @Test
    void testLogin() throws IOException {
        URL url = new URL("http://localhost:10001/sessions");
        URLConnection urlConnection = url.openConnection();
        urlConnection.setDoOutput(true);
        OutputStream outputStream = urlConnection.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.write("{\"Username\": \"Seppi\", \r\n \"Password\":\"1234\"}");
        printWriter.close();
        InputStream inputStream = urlConnection.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        Assertions.assertEquals(bufferedReader.readLine(), "User login successful");
    }

    @Test
    void testUserServiceUpdateUser() throws IOException {
        URL url = new URL("http://localhost:10001/users/Seppi");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("PUT");
        urlConnection.setRequestProperty("Authorization", "Basic Seppi-mtcgToken");
        urlConnection.setDoOutput(true);
        OutputStream outputStream = urlConnection.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.write("{\"Username\": \"Peter\", \r\n \"Bio\":\"Different Bio\"}");
        printWriter.close();

        InputStream inputStream = urlConnection.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        Assertions.assertEquals(bufferedReader.readLine(), "User successfully updated.");
    }

    @Test
    void testUserServiceUpdateUserWithAdmin() throws IOException {
        URL url = new URL("http://localhost:10001/users/Peter");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("PUT");
        urlConnection.setRequestProperty("Authorization", "Basic admin-mtcgToken");
        urlConnection.setDoOutput(true);
        OutputStream outputStream = urlConnection.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.write("{\"Username\": \"Seppi\", \r\n \"Bio\":\"Different Bio\"}");
        printWriter.close();

        InputStream inputStream = urlConnection.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        Assertions.assertEquals(bufferedReader.readLine(), "User successfully updated.");
    }

    @Test
    void testUserServiceUpdateWithoutAuth() throws IOException {
        URL url = new URL("http://localhost:10001/users/Peter");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("PUT");
        urlConnection.setDoOutput(true);
        OutputStream outputStream = urlConnection.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.write("{\"Username\": \"marting\", \r\n \"Bio\":\"Different Bio\"}");
        printWriter.close();
        int responseCode = urlConnection.getResponseCode();

        Assertions.assertEquals(HttpURLConnection.HTTP_UNAUTHORIZED, responseCode);
    }

    @Test
    void testUserServiceGetUserWithoutAuthorization() throws IOException {
        URL url = new URL("http://localhost:10001/users/martin");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        int responseCode = urlConnection.getResponseCode();
        Assertions.assertEquals(HttpURLConnection.HTTP_UNAUTHORIZED, responseCode);
    }

    @Test
    void testDeleteUser() throws IOException {
        URL url = new URL("http://localhost:10001/users");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("DELETE");
        urlConnection.setRequestProperty("Authorization", "Basic admin-mtcgToken");
        urlConnection.setDoOutput(true);
        OutputStream outputStream = urlConnection.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.write("149");
        printWriter.close();
        int responseCode = urlConnection.getResponseCode();

        Assertions.assertEquals(HttpURLConnection.HTTP_OK, responseCode);
    }

    @Test
    void testDeleteUserWithoutAdmin() throws IOException {
        URL url = new URL("http://localhost:10001/users");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("DELETE");
        urlConnection.setRequestProperty("Authorization", "Basic Olaf-mtcgToken");
        urlConnection.setDoOutput(true);
        OutputStream outputStream = urlConnection.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.write("149");
        printWriter.close();
        int responseCode = urlConnection.getResponseCode();

        Assertions.assertEquals(HttpURLConnection.HTTP_FORBIDDEN, responseCode);
    }

    @Test
    void testDeleteUserWrongUid() throws IOException {
        URL url = new URL("http://localhost:10001/users");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("DELETE");
        urlConnection.setRequestProperty("Authorization", "Basic admin-mtcgToken");
        urlConnection.setDoOutput(true);
        OutputStream outputStream = urlConnection.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.write("324324");
        printWriter.close();
        int responseCode = urlConnection.getResponseCode();

        Assertions.assertEquals(HttpURLConnection.HTTP_NOT_FOUND, responseCode);
    }
}