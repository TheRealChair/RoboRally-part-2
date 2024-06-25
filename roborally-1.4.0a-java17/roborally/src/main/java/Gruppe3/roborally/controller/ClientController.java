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


/**
 * Manages all client-server interactions for the game, handling requests and responses
 * to and from the server. This includes sending and retrieving game state updates,
 * and controlling the polling mechanism for ongoing game updates.
 *
 * @author Karl
 */
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


    /**
     * Sends a POST request to the server with optional request body and returns a response of the specified type.
     *
     * @param endpointPath        The endpoint URL to send the request to.
     * @param requestObject       The object to be sent as the request body, can be null.
     * @param responseObjectClass The class of the response object expected.
     * @return The response object of type T, or null if responseObjectClass is null.
     * @throws IOException          If there is an issue with network communication.
     * @throws InterruptedException If the thread execution is interrupted.
     */
    public static <T> T sendRequestToServer(String endpointPath, Object requestObject, Class<T> responseObjectClass)
            throws IOException, InterruptedException {
        String baseUrl = BASE_URL + endpointPath;
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl))
                .header("Content-Type", "application/json");
        if (requestObject != null) {
            String requestJson = objectMapper.writeValueAsString(requestObject); // Converts object to JSON string
            requestBuilder.POST(HttpRequest.BodyPublishers.ofString(requestJson)); // Sets the method to POST and adds the JSON String
        } else {
            requestBuilder.POST(HttpRequest.BodyPublishers.noBody());
        }
        HttpRequest request = requestBuilder.build(); // Builds the HttpRequest object
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString()); // Sends the request and gets the response

        if (responseObjectClass != null) {
            return handleResponse(response, responseObjectClass); // Handles the response, returns an object deserialized from the response
        } else {
            return null; // Return null if responseObjectClass is null
        }
    }


    /**
     * Sends a GET request to the server and returns a response of the specified type.
     *
     * @param endpointPath        The endpoint URL to send the request to.
     * @param responseObjectClass The class of the response object expected.
     * @return The response object of type T.
     * @throws IOException          If there is an issue with network communication.
     * @throws InterruptedException If the thread execution is interrupted.
     */
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

    /**
     * Sends a PUT request to the server to update game state information.
     *
     * @param endpointPath  The endpoint URL to send the request to.
     * @param requestObject The object to be sent as the request body.
     * @throws IOException          If there is an issue with network communication.
     * @throws InterruptedException If the thread execution is interrupted.
     */
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

            return sendRequestToServer("game-states/" + gameId
                    + "/" + gamePlayerId + "/" + register, gameStateRequest, GameStateResponse.class);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static void updateGameState(int register, String card) {
        try {
            GameStateRequest gameStateRequest = new GameStateRequest();
            gameStateRequest.setRegister(register);
            gameStateRequest.setCard(card);
            gameStateRequest.setGamePlayerId(gamePlayerId);

            // Update the URL to include the register in the path as per the new server-side routing
            sendUpdateToServer("game-states/" + gameId + "/" + gamePlayerId + "/" + register, gameStateRequest);
            System.out.println("Updated game state for Game ID: " + gameId + ", Player ID: " + gamePlayerId + ", Register: " + register + " with card: " + card);
        } catch (IOException | InterruptedException e) {
            System.err.println("Error updating game state: " + e.getMessage());
            e.printStackTrace();
        }
    }



    public static void sendRegisterToServer() throws InterruptedException {
        Player myPlayer = gameController.getBoard().getPlayer(ClientController.gamePlayerId-1);
        if (myPlayer != null) {
            for (int register = 0; register < 5; register++) {
                CommandCardField field = myPlayer.getProgramField(register);
                CommandCard card = null;
                if (field != null) {
                    card = field.getCard();
                }

                String command = (card != null) ? card.command.toString() : "NULL";
                ClientController.postGameState(register, command);
                System.out.println("Player " + myPlayer.getGamePlayerID() + " sent register "
                        + register + " with card " + command + " to server.");
            }
        } else {
            System.out.println("No current player found to send register.");
        }
    }

    public static void isReady() {
        pollingTask.setReady(true);
    }

}
