package Gruppe3.roborally.controller;

import Gruppe3.roborally.model.httpModels.PlayerResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    public static Long playerId = null;


    // Send a request to server with a endPath ("player"), requestobject (PlayerRequest)
    // And get back (PlayerResponse), which is the object of type RespondObjectClass
    // object returned is a generic type (can be any type)
    public static <T> T sendRequestToServer(String endpointPath, Object requestObject, Class<T> responseObjectClass)
            throws IOException, InterruptedException, JsonProcessingException {
        String baseUrl = BASE_URL + endpointPath;
        String requestJson = objectMapper.writeValueAsString(requestObject);  // Converts object to jason string
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);   // Makes it ignore unknown properties

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl))                                   // Begins building the request
                .header("Content-Type", "application/json")     // Sets the url and content type
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))     // Sets the method to POST and adds the JSON String
                .build();                                                   // Builds the object

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString()); // Sends the request and gets the response

        return handleResponse(response, responseObjectClass);   // Handles the response, return a object dezerialized from the response
    }

    public static <T> T getRequestFromServer(String endpointPath, Class<T> responseObjectClass)
            throws IOException, InterruptedException, JsonProcessingException {
        String requestUrl = BASE_URL + endpointPath;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestUrl))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return handleResponse(response, responseObjectClass);
    }

    // Simplified method to send an update request to the server
    public static void sendUpdateToServer(String endpointPath, Object requestObject)
            throws IOException, InterruptedException, JsonProcessingException {
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
            System.out.println("Response body for update: " + response.body());
        } else {
            System.out.println("Update failed with status code: " + response.statusCode());
            System.out.println("Response body: " + response.body());
        }
    }

    private static <T> T handleResponse(HttpResponse<String> response, Class<T> responseObjectClass)
            throws JsonProcessingException {
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            System.out.println("Response body: " + response.body());
            return objectMapper.readValue(response.body(), responseObjectClass);
        } else {
            System.out.println("Request failed with status code: " + response.statusCode());
            System.out.println("Response body: " + response.body());
            // Handle error or return null based on your application's logic
            throw new RuntimeException("Request failed with status code: " + response.statusCode());
        }
    }
}

