package yinonx.apitest.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import yinonx.apitest.classes.Game;

import org.springframework.stereotype.Service;

@Service
public class GamesService {

    private Game gameFromQueryGame;
    private RawgApiService rawgApiService;

    public GamesService(RawgApiService rawgApiService) {
        this.rawgApiService = rawgApiService;
        gameFromQueryGame = new Game();
    }

    public Game getGameDetailsByName(String name) {
        System.out.println("Searching for a game by the name of: " + name + " in the database");
        String json = rawgApiService.searchGame(name);

        gameFromQueryGame = parseJsonToGame(json);
       // System.out.println("-------------------\n" + json + "\n--------------------");
        return gameFromQueryGame;
    }

    private Game parseJsonToGame(String jsonString) {
        System.out.println("Function parseJsonToGame has been activated");

        Game gameToReturn = new Game();

        // Parse JSON string to a JsonNode
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonString);

            // Extract and set game details
            JsonNode gameNode = jsonNode.path("results").get(0); // Assuming there is at least one result

            String gameName = gameNode.path("name").asText();
            String releaseDate = gameNode.path("released").asText();
            int rating = gameNode.path("rating").asInt();
            String coverImageUrl = gameNode.path("background_image").asText();

            gameToReturn.setCoverImageLink(coverImageUrl);
            gameToReturn.setName(gameName);
            gameToReturn.setReleaseDate(releaseDate);
            gameToReturn.setRating(rating);

            // Parse platforms array
            JsonNode platformsNode = gameNode.path("platforms");
            String[] platforms = new String[platformsNode.size()];
            for (int i = 0; i < platformsNode.size(); i++) {
                platforms[i] = platformsNode.get(i).path("platform").path("name").asText();
            }
            gameToReturn.setPlatforms(platforms);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        System.out.println("The JSON response has been parsed successfully");
        return gameToReturn;
    }
}
