package Gruppe3.roborally.controller;

import Gruppe3.roborally.model.CommandCard;
import Gruppe3.roborally.model.CommandCardField;
import Gruppe3.roborally.model.Player;
import Gruppe3.roborally.model.httpModels.GameStateRequest;
import Gruppe3.roborally.model.httpModels.GameStateResponse;
import Gruppe3.roborally.model.httpModels.PlayerResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ClientController {

    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String BASE_URL = "http://localhost:8080/";
    public static Long gameId;
    public static Long playerId;
    public static int gamePlayerId;
    private static Thread pollingThread;
    private static ClientPolling pollingTask;
    private static GameController gameController;


    public static void setGameController(GameController controller) {
        gameController = controller;
    }
    // Send a request to server with an endpointPath ("player"), requestObject (PlayerRequest)
    // And get back (PlayerResponse), which is the object of type RespondObjectClass
    // object returned is a generic type (can be any type)
    public static <T> T sendRequestToServer(String endpointPath, Object requestObject, Class<T> responseObjectClass)
            throws IOException, InterruptedException {
        String baseUrl = BASE_URL + endpointPath;
        String requestJson = objectMapper.writeValueAsString(requestObject);  // Converts object to JSON string
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);   // Makes it ignore unknown properties

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl))                                   // Begins building the request
                .header("Content-Type", "application/json")     // Sets the URL and content type
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))     // Sets the method to POST and adds the JSON String
                .build();                                                   // Builds the object

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString()); // Sends the request and gets the response

        return handleResponse(response, responseObjectClass);   // Handles the response, returns an object deserialized from the response
    }

    public static <T> T getRequestFromServer(String endpointPath, Class<T> responseObjectClass)
            throws IOException, InterruptedException {
        String requestUrl = BASE_URL + endpointPath;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestUrl))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return handleResponse(response, responseObjectClass);
    }

    public static <T> T getRequestFromServer(String endpointPath, TypeReference<T> typeReference)
            throws IOException, InterruptedException {
        String requestUrl = BASE_URL + endpointPath;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestUrl))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return handleResponse(response, typeReference);
    }

    // Simplified method to send an update request to the server
    public static void sendUpdateToServer(String endpointPath, Object requestObject)
            throws IOException, InterruptedException {
        String baseUrl = BASE_URL + endpointPath;

        // Convert the request object to JSON string
        String requestBody = objectMapper.writeValueAsString(requestObject);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Build the HTTP PUT request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        // Send the request and handle the response
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            System.out.println("Update successful.");
        } else {
            System.out.println("Update failed with status code: " + response.statusCode());
            System.out.println("Response body: " + response.body());
        }
    }

    private static <T> T handleResponse(HttpResponse<String> response, Class<T> responseObjectClass)
            throws JsonProcessingException {
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return objectMapper.readValue(response.body(), responseObjectClass);
        } else {
            System.out.println("Request failed with status code: " + response.statusCode());
            System.out.println("Response body: " + response.body());
            // Handle error or return null based on your application's logic
            throw new RuntimeException("Request failed with status code: " + response.statusCode());
        }
    }

    private static <T> T handleResponse(HttpResponse<String> response, TypeReference<T> typeReference)
            throws JsonProcessingException {
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return objectMapper.readValue(response.body(), typeReference);
        } else {
            System.out.println("Request failed with status code: " + response.statusCode());
            System.out.println("Response body: " + response.body());
            // Handle error or return null based on your application's logic
            throw new RuntimeException("Request failed with status code: " + response.statusCode());
        }
    }

    public static void startPolling(AppController appController) {
        if (pollingThread == null || !pollingThread.isAlive()) {
            pollingTask = new ClientPolling(appController);
            pollingThread = new Thread(pollingTask);
            pollingThread.start();
        }
    }


    public static void stopPolling() {
        if (pollingTask != null) {
            pollingTask.stop();
            try {
                pollingThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }



    public static GameStateResponse postGameState(int register, String card) {
        try {
            GameStateRequest gameStateRequest = new GameStateRequest();
            gameStateRequest.setGamePlayerId(gamePlayerId);
            gameStateRequest.setRegister(register);
            gameStateRequest.setCard(card);

            return sendRequestToServer("game-states/"+gameId+"/"+gamePlayerId, gameStateRequest, GameStateResponse.class);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static void updateGameState (int register, String card) {
        try {
            GameStateRequest gameStateRequest = new GameStateRequest();
            gameStateRequest.setRegister(register);
            gameStateRequest.setCard(card);

            ClientController.sendUpdateToServer("game-states/"+gameId+"/"+gamePlayerId, gameStateRequest);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static void sendRegisterToServer() {
        int register = gameController.getBoard().getStep();
        Player myPlayer = gameController.getBoard().getPlayer(ClientController.gamePlayerId-1);

        if (myPlayer != null) {
            CommandCardField field = myPlayer.getProgramField(register);
            if (field != null) {
                CommandCard card = field.getCard();
                if (card != null) {
                    String command = card.command.toString();
                    ClientController.postGameState(register, command);
                } else {
                    System.out.println("No card found in the program field for the current register.");
                    // Handle the case where the card is null
                }
            } else {
                System.out.println("No program field found for the given register.");
                // Handle the case where the program field is null
            }
        } else {
            System.out.println("No current player found to send register.");
            // Handle the case where there is no current player (should not normally happen in game flow)
        }
    }

}
