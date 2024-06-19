package Gruppe3.roborally.controller;

import Gruppe3.roborally.RoboRally;
import Gruppe3.roborally.model.Command;
import Gruppe3.roborally.model.CommandCard;
import Gruppe3.roborally.model.CommandCardField;
import Gruppe3.roborally.model.Player;
import Gruppe3.roborally.model.httpModels.GameResponse;
import Gruppe3.roborally.model.httpModels.GameStateRequest;
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
    private int register = 0;

    public ClientPolling(AppController appController) {
        this.myId = ClientController.playerId;
        this.currentTask = this::startGame; // Initialize with startGame as default task
        this.appController = appController; // Initialize appController
    }

    @Override
    public void run() {
        while (running) {
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

    public void setProgrammingDoneTask() {
        currentTask = this::isProgrammingDone;
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
            RoboRally.setLobbyLabel("Waiting for players... : " + playerResponses.size() + "/" + gameSize+"     Game ID: "+myGameId);
            // Check if the number of players in the response meets or exceeds the game size
            if (playerResponses.size() >= gameSize) {
                appController.getGameController().startProgrammingPhase();
                // Update the board view on the JavaFX Application Thread
                Platform.runLater(() -> {
                    appController.getRoboRally().createBoardView(appController.getGameController());
                });
                System.out.println("Game created successfully.");
                setProgrammingDoneTask();
            } else {
                System.out.println("Waiting for more players to join...");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void isProgrammingDone() {
        try {
            TypeReference<List<GameStateResponse>> typeReference = new TypeReference<>() {};
            List<GameStateResponse> gameStateList = ClientController.getRequestFromServer("game-states/by-game/" + ClientController.gameId, typeReference);

            // Check if all players have submitted their registers
            boolean allPlayersReady = true;
            if(register==0) {
                for (GameStateResponse gameState : gameStateList) {
                    if (gameState.getCard() == null) {
                        allPlayersReady = false;
                        break;
                    }
                }
            }
            else if(register>=1 && register<=4){
                ClientController.sendRegisterToServer(register);
                for (GameStateResponse gameState : gameStateList) {
                    System.out.println("Game States Register have value:" +gameState.getRegister());
                    if (gameState.getRegister()==register) {
                        allPlayersReady = false;
                        break;
                    }
                }
            }
            if (allPlayersReady) {
                System.out.println("All players have finished programming.");
                setRunRegisterTask(); // Set the task to run the registers
            } else {
                System.out.println("Waiting for all players to finish programming...");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Logic for polling when everybody has pressed "programming"
    private void runRegister() {
        try {
            int gamePlayerId = ClientController.gamePlayerId;
            TypeReference<List<GameStateResponse>> typeReference = new TypeReference<>() {};
            List<GameStateResponse> gameStateList = ClientController.getRequestFromServer("game-states/by-game/" + ClientController.gameId, typeReference);
            // If all players have submitted their registers, execute the step
                System.out.println("GameState List length is : " + gameStateList.size());
                // Load cards into registers based on gameStateList
                for (GameStateResponse gameState : gameStateList) {

                    int playerGameId = gameState.getGamePlayerId();
                    String cardName = gameState.getCard();
                    Command card = Command.toCommand(cardName);
                    System.out.print("Player " + playerGameId + " has card: " + cardName + " at register " + register + "\n");

                    // Update the local register with the card from server
                    CommandCardField targetField = findRegisterFieldForPlayer(playerGameId, register);
                    if (targetField != null && card != null) {
                        appController.getGameController().moveCardToTarget(new CommandCard(card), targetField); // Utilize moveCardToTarget
                    }
                    appController.getGameController().executeStep();
                }
            register++;


            if(register==5){
                ClientController.sendRequestToServer("game-states/"+ClientController.gameId+"/reset-all", null, null);
                register = 0;
                System.out.println("Gamestates was reset for all players");
            }
            setProgrammingDoneTask();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }



    private CommandCardField findRegisterFieldForPlayer(int playerGameId, int registerIndex) {
        for (Player player : appController.getGameController().getBoard().getPlayers()) {
            if (player.getGamePlayerID() == playerGameId) {
                CommandCardField field = player.getProgramField(registerIndex);
                if (field != null) {
                    return field;
                } else {
                    System.out.println("No field found at register index: " + registerIndex + " for player: " + playerGameId);
                }
            }
        }
        System.out.println("Player not found with ID: " + playerGameId);
        return null; // Player not found or register index is out of bounds
    }


}


