package at.fhtw.service.card;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class CardControllerTest {

    @Test
    void testShowUsersCards() throws IOException {
        URL url = new URL("http://localhost:10001/cards");
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
                System.out.println(inputLine + "\n");
            }
        }else {
            Assertions.assertFalse(true);
        }
    }

    @Test
    void testShowUsersCardsWithoutAuth() throws IOException {
        URL url = new URL("http://localhost:10001/cards");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("Authorization", "Basic mtcgToken");

        int responseCode = urlConnection.getResponseCode();
        assertEquals(HttpURLConnection.HTTP_UNAUTHORIZED, responseCode);
    }
    @Test
    void testShowUsersCardsWithoutCards() throws IOException {
        URL url = new URL("http://localhost:10001/cards");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("Authorization", "Basic admin-mtcgToken");

        int responseCode = urlConnection.getResponseCode();
        assertEquals(HttpURLConnection.HTTP_NO_CONTENT, responseCode);
    }

}