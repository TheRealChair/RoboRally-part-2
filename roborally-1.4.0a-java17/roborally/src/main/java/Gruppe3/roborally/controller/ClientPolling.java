package Gruppe3.roborally.controller;

import Gruppe3.roborally.RoboRally;
import Gruppe3.roborally.model.Command;
import Gruppe3.roborally.model.CommandCard;
import Gruppe3.roborally.model.CommandCardField;
import Gruppe3.roborally.model.Player;
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
        try {
            // Fetch the game ID for the current player
            GameResponse game = ClientController.getRequestFromServer("players/game/" + myId, GameResponse.class);
            long myGameId = Long.parseLong(game.getGameId());

            // Fetch game states for all players in the current game
            TypeReference<List<GameStateResponse>> typeReference = new TypeReference<>() {};
            List<GameStateResponse> gameStateList = ClientController.getRequestFromServer("game-states/by-game/" + myGameId, typeReference);

            // Check if all players have submitted their registers
            boolean allPlayersReady = true;
            for (GameStateResponse gameState : gameStateList) {
                if (gameState.getCard() == null) {
                    allPlayersReady = false;
                    break;
                }
            }

            // If all players have submitted their registers, execute the step
            if (allPlayersReady) {
                // Load cards into registers based on gameStateList
                for (GameStateResponse gameState : gameStateList) {
                    int playerGameId = gameState.getGamePlayerId();
                    int registerIndex = gameState.getRegister();
                    String cardName = gameState.getCard();
                    Command card = Command.toCommand(cardName);

                    // Update the local register with the card from server
                    CommandCardField targetField = findRegisterFieldForPlayer(playerGameId, registerIndex);
                    if (targetField != null && card != null) {
                        targetField.setCard(new CommandCard(card)); // Assuming CommandCard constructor exists
                    }
                }

                // Inform user or log
                System.out.println("All players have put in a register.");

                // Execute the step using existing method
                appController.getGameController().executeStep();

            } else {
                System.out.println("Waiting for all players to put in a register...");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private CommandCardField findRegisterFieldForPlayer(int playerGameId, int registerIndex) {
        for (Player player : appController.getGameController().getBoard().getPlayers()) {
            if (player.getGamePlayerID() == playerGameId) {
                return player.getProgramField(registerIndex);
            }
        }
        // Player not found
        return null;
    }

}


