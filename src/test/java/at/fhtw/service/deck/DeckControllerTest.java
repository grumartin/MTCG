package at.fhtw.service.deck;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class DeckControllerTest {
    @Test
    void testShowCardsFromDeck() throws IOException {
        URL url = new URL("http://localhost:10001/deck");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("Authorization", "Basic kienboec-mtcgToken");

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
    void testShowCardsFromDeckPlain() throws IOException {
        URL url = new URL("http://localhost:10001/deck?format=plain");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("Authorization", "Basic kienboec-mtcgToken");

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
    void testShowCardsFromDeckWithoutAuth() throws IOException {
        URL url = new URL("http://localhost:10001/deck");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("Authorization", "Basic mtcgToken");

        int responseCode = urlConnection.getResponseCode();
        assertEquals(HttpURLConnection.HTTP_UNAUTHORIZED, responseCode);
    }
    @Test
    void testShowCardsFromDeckWithoutCards() throws IOException {
        URL url = new URL("http://localhost:10001/deck");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("Authorization", "Basic admin-mtcgToken");

        int responseCode = urlConnection.getResponseCode();
        if(responseCode == HttpURLConnection.HTTP_NO_CONTENT){
            InputStream inputStream = urlConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            assertEquals("The request was fine, but the deck doesn't have any cards", bufferedReader.readLine());
        }else {
            Assertions.assertFalse(true);
        }
    }

    @Test
    void testConfigureDeck() throws IOException {
        URL url = new URL("http://localhost:10001/deck");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestProperty("Authorization", "Basic Olaf-mtcgToken");
        urlConnection.setDoOutput(true);
        urlConnection.setRequestMethod("PUT");
        OutputStream outputStream = urlConnection.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.write("[\"845f0dc7-37d0-426e-994e-43fc3ac83c08\", \"99f8f8dc-e25e-4a95-aa2c-782823f36e2a\", \"e85e3976-7c86-4d06-9a80-641c2019a79f\", \"dfdd758f-649c-40f9-ba3a-8657f4b3439f\"]");
        printWriter.close();

        int responseCode = urlConnection.getResponseCode();

        assertEquals(HttpURLConnection.HTTP_OK, responseCode);
    }

    @Test
    void testConfigureDeckFalseAmount() throws IOException {
        URL url = new URL("http://localhost:10001/deck");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestProperty("Authorization", "Basic kienboec-mtcgToken");
        urlConnection.setDoOutput(true);
        urlConnection.setRequestMethod("PUT");
        OutputStream outputStream = urlConnection.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.write("[\"99f8f8dc-e25e-4a95-aa2c-782823f36e2a\", \"e85e3976-7c86-4d06-9a80-641c2019a79f\", \"171f6076-4eb5-4a7d-b3f2-2d650cc3d237\"]");
        printWriter.close();

        int responseCode = urlConnection.getResponseCode();

        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, responseCode);
    }

    @Test
    void testConfigureWithoutAuth() throws IOException {
        URL url = new URL("http://localhost:10001/deck");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setDoOutput(true);
        urlConnection.setRequestMethod("PUT");
        OutputStream outputStream = urlConnection.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.write("[\"99f8f8dc-e25e-4a95-aa2c-782823f36e2a\", \"e85e3976-7c86-4d06-9a80-641c2019a79f\", \"171f6076-4eb5-4a7d-b3f2-2d650cc3d237\"]");
        printWriter.close();

        int responseCode = urlConnection.getResponseCode();

        assertEquals(HttpURLConnection.HTTP_UNAUTHORIZED, responseCode);
    }

    @Test
    void testConfigureDeckInvalidCards() throws IOException {
        URL url = new URL("http://localhost:10001/deck");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestProperty("Authorization", "Basic kienboec-mtcgToken");
        urlConnection.setDoOutput(true);
        urlConnection.setRequestMethod("PUT");
        OutputStream outputStream = urlConnection.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.write("[\"b2237eca-0271-43bd-87f6-b22f70d42ca4\", \"99f8f8dc-e25e-4a95-aa2c-782823f36e2a\", \"e85e3976-7c86-4d06-9a80-641c2019a79f\", \"171f6076-4eb5-4a7d-b3f2-2d650cc3d237\"]");
        printWriter.close();

        int responseCode = urlConnection.getResponseCode();

        assertEquals(HttpURLConnection.HTTP_FORBIDDEN, responseCode);
    }
}