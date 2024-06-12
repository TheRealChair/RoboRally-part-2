package Gruppe3.roborally.controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ClientController {

    private static HttpClient httpClient = HttpClient.newHttpClient();
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static final String BASE_URL = "http://localhost:8080/";

    public static void sendRequestToServer(String baseUrl, Object requestObject, Class<?> responseObjectClass)
            throws IOException, InterruptedException, JsonProcessingException {
        baseUrl = BASE_URL + baseUrl;
        String requestJson = objectMapper.writeValueAsString(requestObject);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            Object responseObject = objectMapper.readValue(response.body(), responseObjectClass);
            System.out.println("Request successful: " + responseObject);
        } else {
            System.out.println("Request failed: " + response.body());
        }
    }
}
