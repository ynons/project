package yinonx.apitest.services;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class RawgApiService {

 private static String apiKey = "54db4509a1bb4ed38b1d38d0ec8b2d5f"; 
public static String getGameDetails(String gameName) {
        String baseUrl = "https://api.rawg.io/api/games";

        // Set up parameters for the request
        String apiUrl = String.format("%s?key=%s&search=%s&page_size=1", baseUrl, apiKey, gameName);
        String jsonResponse = "";
        try {
            // Create URL object
            URL url = new URL(apiUrl);

            // Open connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Get response code
            int responseCode = connection.getResponseCode();

            // Check if the request has nt failed
            if (responseCode == 200) {
                // Read response
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                jsonResponse = reader.lines().collect(Collectors.joining());

                // Parse the JSON given
                //System.out.println(jsonResponse);

                // Close the reader
                reader.close();
            } else {
                // Print an error message if the request was not successful
                System.out.println("Error: " + responseCode + ", " + connection.getResponseMessage());
            }

            // Close the connection
            connection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonResponse;
    }

    public String searchGame(String gameName) {
         String gameNameToSearch = gameName;
        System.out.println("succsusfully got game from the database api");
        // Call the function
        return getGameDetails(gameName);
        

    }
}