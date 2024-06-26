package Gruppe3.roborally.controller;

import Gruppe3.roborally.RoboRally;
import Gruppe3.roborally.model.*;
import Gruppe3.roborally.model.httpModels.GameResponse;
import Gruppe3.roborally.model.httpModels.GameStateRequest;
import Gruppe3.roborally.model.httpModels.GameStateResponse;
import Gruppe3.roborally.model.httpModels.PlayerResponse;
import ch.qos.logback.core.net.SyslogOutputStream;
import com.fasterxml.jackson.core.type.TypeReference;
import javafx.application.Platform;

import java.io.IOException;
import java.util.List;
import java.util.Objects;


/**
 * Manages client-side polling to continuously check server updates and respond to game state changes.
 * This class implements Runnable to be executed in a separate thread for continuous operation.
 * The primary role of this class is to handle the setup and execution of various tasks
 * required for game progression, such as starting the game, sending data to the server,
 * checking if programming is done, and updating the game state based on player interactions.
 *
 * @author Karl
 */
public class ClientPolling implements Runnable {
    private volatile boolean running = true;
    private Long myId = null; // gets updated. own player id
    private PollingTask currentTask; // Current task to execute during polling
    private AppController appController;
    private boolean isReady = false;

    public ClientPolling(AppController appController) {
        this.myId = ClientController.playerId;
        this.currentTask = this::startGame; // Initialize with startGame as default task
        this.appController = appController; // Initialize appController
    }

    /**
     * Continuously executes tasks at fixed time intervals while the thread is running.
     * This method is designed to run in a separate thread and handles the execution of {@code currentTask}.
     * It includes error handling for interruptions and I/O exceptions during task execution.
     *
     * The method will execute the current task and then sleep for one second before checking if it should continue running.
     *
     * @author Karl
     * @override
     */
    @Override
    public void run() {
        while (running) {
            try {
                currentTask.execute(); // Execute the current task
                Thread.sleep(1000); // Sleep for 2 seconds before the next poll
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt(); // Restore the interrupted status
            }
        }
    }

    public void stop() {
        running = false;
    }

    public void setplaceInRegisterTast() {
        currentTask = this::placeInRegister;
    }

    public void setProgrammingDoneTask() {
        currentTask = this::isProgrammingDone;
    }

    public void setSendToServerTask() throws IOException, InterruptedException{
        currentTask = this::sendToServer;
    }

    public void setExecuteRegistersTask() {
        currentTask = this::executeRegisters;
    }


    public void setReady(boolean ready) {
        isReady = ready;
    }

    /**
     * Initiates the game once the required number of players have joined.
     * Retrieves game and player information from the server and checks if the player count meets
     * the game requirements to start. Updates the UI to reflect the current state of the game lobby.
     */
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
                setSendToServerTask();
            } else {
                System.out.println("Waiting for more players to join...");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends current player state to the server if the client is ready.
     * Transition to checking if programming is done after sending the data.
     */
    private void sendToServer() throws IOException, InterruptedException{
        if(isReady) {
            ClientController.sendRegisterToServer();
            setProgrammingDoneTask();
        }
    }

    private void isProgrammingDone() {
        try {
            int gamePlayerId = ClientController.gamePlayerId;
            TypeReference<List<GameStateResponse>> typeReference = new TypeReference<>() {};
            List<GameStateResponse> gameStateList = ClientController.getRequestFromServer(
                    "game-states/by-game/" + ClientController.gameId, typeReference);
            boolean allPlayersReady = true;
            for(GameStateResponse gameState : gameStateList){
                System.out.println("gameStateList.size(): "+  gameStateList.size()+" gameState.getGame().getNoOfPlayers()*5: "+
                        + gameState.getGame().getNoOfPlayers() * 5 );
                if (gameStateList.size() < gameState.getGame().getNoOfPlayers()*5) {
                    allPlayersReady = false;
                    break;
                }
            }
            if (allPlayersReady) {
                System.out.println("All players have finished programming.");
                setplaceInRegisterTast();
            } else {
                System.out.println("Waiting for all players to finish programming...");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Assigns cards to registers based on the game state information from the server.
     * This method loads cards into registers and executes movements on the board according to the retrieved game states.
     */
    private void placeInRegister() {
        try {
            int gamePlayerId = ClientController.gamePlayerId;
            TypeReference<List<GameStateResponse>> typeReference = new TypeReference<>() {};
            List<GameStateResponse> gameStateList = ClientController.getRequestFromServer("game-states/by-game/" + ClientController.gameId, typeReference);
            // Load cards into registers based on gameStateList
            for (GameStateResponse gameState : gameStateList) {
                int playerGameId = gameState.getGamePlayerId();
                String cardName = gameState.getCard();
                if(!Objects.equals(cardName, "NULL")){ // Removed the condition to check for the client's gamePlayerId
                    Command card = Command.toCommand(cardName);
                    int tempRegister = gameState.getRegister();
                    System.out.print("Player " + playerGameId + " has card: " + cardName + " at register " + tempRegister + "\n");

                    CommandCardField targetField = findRegisterFieldForPlayer(playerGameId, tempRegister);
                    if (targetField != null && card != null) {
                        appController.getGameController().moveCardToTarget(new CommandCard(card), targetField);
                    }
                }
            }
            isReady = false;
            setExecuteRegistersTask();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void executeRegisters() throws IOException, InterruptedException {
        appController.getGameController().executeStep();

        //If register is 4 after, reset all game states
        if(appController.getGameController().getBoard().getStep() == 4) {
            ClientController.sendRequestToServer("game-states/by-game/" + ClientController.gameId + "/reset-all", null, null);
            appController.getGameController().startProgrammingPhase();
            setSendToServerTask();
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


