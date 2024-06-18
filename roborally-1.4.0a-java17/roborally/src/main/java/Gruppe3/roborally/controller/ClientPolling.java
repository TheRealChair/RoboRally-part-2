package Gruppe3.roborally.controller;

import Gruppe3.roborally.RoboRally;
import Gruppe3.roborally.model.httpModels.GameResponse;
import Gruppe3.roborally.model.httpModels.GameStateResponse;
import Gruppe3.roborally.model.httpModels.PlayerResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import javafx.application.Platform;

import java.io.IOException;
import java.util.List;

public class ClientPolling implements Runnable {
    private volatile boolean running = true;
    private Long myId = null; // gets updated. own player id
    private PollingTask currentTask; // Current task to execute during polling
    private AppController appController;

    public ClientPolling(AppController appController) {
        this.myId = ClientController.playerId;
        this.currentTask = this::startGame; // Initialize with startGame as default task
        this.appController = appController; // Initialize appController
    }

    @Override
    public void run() {
        while (running) {
            System.out.println("Polling server for updates...");
            try {
                currentTask.execute(); // Execute the current task
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

    public void setStartGameTask() {
        currentTask = this::startGame;
    }

    public void setRunRegisterTask() {
        currentTask = this::runRegister;
    }

    // Logic for polling when to start the game
    private void startGame() {
        try {
            // Fetch the game information for the current player
            GameResponse game = ClientController.getRequestFromServer("players/game/" + myId, GameResponse.class);
            long myGameId = Long.parseLong(game.getGameId());

            TypeReference<List<PlayerResponse>> typeReference = new TypeReference<>() {};
            List<PlayerResponse> playerResponses = ClientController.getRequestFromServer("players/games/" + myGameId, typeReference);

            int gameSize = game.getNoOfPlayers();
            RoboRally.setLobbyLabel("Waiting for players... : " + playerResponses.size() + "/" + gameSize);
            // Check if the number of players in the response meets or exceeds the game size
            if (playerResponses.size() >= gameSize) {
                appController.getGameController().startProgrammingPhase();
                // Update the board view on the JavaFX Application Thread
                Platform.runLater(() -> {
                    appController.getRoboRally().createBoardView(appController.getGameController());
                });
                System.out.println("Game created successfully.");
                setRunRegisterTask();
            } else {
                System.out.println("Waiting for more players to join...");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    // Logic for polling when everybody has pressed "programming"
    private void runRegister() {
        //fetch that all players have put a register in their game-state
        try {
            GameResponse game = ClientController.getRequestFromServer("players/game/" + myId, GameResponse.class);
            long myGameId = Long.parseLong(game.getGameId());

            TypeReference<List<GameStateResponse>> typeReference = new TypeReference<>() {};
            List<GameStateResponse> gameStateList = ClientController.getRequestFromServer("game-states/by-game/" + myGameId, typeReference);

            boolean allPlayersReady = true;
            for (GameStateResponse gameState : gameStateList) {
                if (gameState.getCard() == null) {
                    allPlayersReady = false;
                    break;
                }
            }

            if (allPlayersReady) {
                appController.getGameController().executeStep();
                System.out.println("All players have put in a register.");
            } else {
                System.out.println("Waiting for all players to put in a register...");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}


