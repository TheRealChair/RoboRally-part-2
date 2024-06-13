package Gruppe3.roborally.controller;

import Gruppe3.roborally.model.httpModels.GameResponse;
import Gruppe3.roborally.model.httpModels.PlayerResponse;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.List;

public class ClientPolling implements Runnable {
    private volatile boolean running = true;
    private Long myId = null; // gets updated. id own player id

    public ClientPolling() {
        this.myId = ClientController.playerId;
    }

    @Override
    public void run() {
        while (running) {
            System.out.println("Polling server for player updates...");
            try {
                startGame();
                Thread.sleep(2000); // Sleep for 2 seconds before the next poll
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt(); // Restore the interrupted status
            }
        }
    }

    public void stop() {
        running = false;
    }


    public void startGame() {
        try {
            // Fetch the game information for the current player
            GameResponse game = ClientController.getRequestFromServer("players/game/" + myId, GameResponse.class);
            long myGameId = Long.parseLong(game.getGameId());

            TypeReference<List<PlayerResponse>> typeReference = new TypeReference<>() {};
            List<PlayerResponse> playerResponses = ClientController.getRequestFromServer("players/games/" + myGameId, typeReference);

            int gameSize = game.getNoOfPlayers();

            // Check if the number of players in the response meets or exceeds the game size
            if (playerResponses.size() >= gameSize) {
                System.out.println("Game is ready to start!");
            } else {
                System.out.println("Waiting for more players to join...");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
