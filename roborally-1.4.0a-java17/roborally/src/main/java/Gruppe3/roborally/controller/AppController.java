/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package Gruppe3.roborally.controller;

import Gruppe3.roborally.fileaccess.LoadBoard;
import Gruppe3.designpatterns.observer.Observer;
import Gruppe3.designpatterns.observer.Subject;

import Gruppe3.roborally.RoboRally;

import Gruppe3.roborally.model.Board;
import Gruppe3.roborally.model.Phase;
import Gruppe3.roborally.model.Player;

import Gruppe3.roborally.model.httpModels.PlayerRequest;
import Gruppe3.roborally.model.httpModels.PlayerResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.web.client.RestTemplate;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import Gruppe3.roborally.model.httpModels.GameRequest;
import Gruppe3.roborally.model.httpModels.GameResponse;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class AppController implements Observer {

    final private List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(2, 3, 4, 5, 6);
    final private List<String> PLAYER_COLORS = Arrays.asList("red", "green", "blue", "orange", "grey", "magenta");
    final private int BOARD_WIDTH = 12;
    final private int BOARD_HEIGHT = 5;
    final private RoboRally roboRally;
    private HttpClient httpClient;
    private ObjectMapper objectMapper;
    private static final String BASE_URL = "http://localhost:8080/";

    private GameController gameController;

    public AppController(@NotNull RoboRally roboRally, RestTemplate restTemplate) {

        this.roboRally = roboRally;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public void newGame() throws IOException, InterruptedException {
        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(PLAYER_NUMBER_OPTIONS.get(0), PLAYER_NUMBER_OPTIONS);
        dialog.setTitle("Player number");
        dialog.setHeaderText("Select number of players");
        Optional<Integer> result = dialog.showAndWait();


        if (result.isPresent()) {
            if (gameController != null) {
                if (!stopGame()) {
                    return;
                }
            }

            Board board = new Board(BOARD_WIDTH, BOARD_HEIGHT);
            gameController = new GameController(board);
            int no = result.get();
            int[] startPoints = new int[]{0, 2, 3, 6, 7, 9};
            for (int i = 0; i < no; i++) {
                Player player = new Player(board, PLAYER_COLORS.get(i), "Player " + (i + 1), false);
                board.addPlayer(player);
                player.setSpace(board.getSpace(0, startPoints[i]));
            }

            // Prepare the game request
            GameRequest gameRequest = new GameRequest();
            gameRequest.setNoOfPlayers(board.getPlayers().length);

            PlayerRequest playerRequest = new PlayerRequest();
            playerRequest.setPlayerId(1L);


            try {
                // Send the request to the server
                String endpointUrl = "games";
                ClientController.sendRequestToServer(endpointUrl, gameRequest, GameResponse.class);
                endpointUrl = "players";
                ClientController.sendRequestToServer(endpointUrl, playerRequest, PlayerResponse.class);
                // Proceed with game initialization
                gameController.startProgrammingPhase();
                roboRally.createBoardView(gameController);
            } catch (IOException | InterruptedException e) {
                System.out.println("Failed to create game: " + e.getMessage());
                e.printStackTrace();
                // Handle the exception as needed
            }
        }
    }


    private void sendNewGameToServer(GameRequest gameRequest) throws IOException, InterruptedException {
        String gameRequestJson = objectMapper.writeValueAsString(gameRequest);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gameRequestJson)) // set HTTP method to POST and provide request body
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            // Parse the response body to GameResponse
            GameResponse gameResponse = objectMapper.readValue(response.body(), GameResponse.class);
            System.out.println("Game created successfully: " + gameResponse);
        } else {
            System.out.println("Failed to create game: " + response.body());
        }
    }

    public void joinGame() {
        // XXX implement this method
    }



    /**
         * Loads a game from a file and updates the game state accordingly.
         * @Author: Karl
         */
    public void saveGame() {
        LoadBoard.saveBoard(gameController.board, "save");
    }


    /**
         * Loads a game from a file and updates the game state accordingly.
         * @Author: Karl
         */
    public void loadGame() {
        Board board = LoadBoard.loadBoard("save");
        gameController = new GameController(board);

        // Set the phase of the game controller's board to the phase loaded from the JSON file
        Phase loadedPhase = Phase.valueOf(board.getPhase().toString());
        gameController.board.setPhase(loadedPhase);

        // Depending on the loaded phase, call the appropriate method
        switch (loadedPhase) {
            case INITIALISATION:
                // Call method related to initialisation phase
                break;
            case PROGRAMMING:
                //Default, no action needed
                break;
            case ACTIVATION:
                gameController.startExecuteStep();
                break;
            case PLAYER_INTERACTION:
                // Call method related to player interaction phase
                break;
        }

        roboRally.createBoardView(gameController);
    }

    /**
     * Stop playing the current game, giving the user the option to save
     * the game or to cancel stopping the game. The method returns true
     * if the game was successfully stopped (with or without saving the
     * game); returns false, if the current game was not stopped. In case
     * there is no current game, false is returned.
     *
     * @return true if the current game was stopped, false otherwise
     */
    public boolean stopGame() {
        if (gameController != null) {

            // here we save the game (without asking the user).
            saveGame();

            gameController = null;
            roboRally.createBoardView(null);
            return true;
        }
        return false;
    }

    public void exit() {
        if (gameController != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Exit RoboRally?");
            alert.setContentText("Are you sure you want to exit RoboRally?");
            Optional<ButtonType> result = alert.showAndWait();

            if (!result.isPresent() || result.get() != ButtonType.OK) {
                return; // return without exiting the application
            }
        }

        // If the user did not cancel, the RoboRally application will exit
        // after the option to save the game
        if (gameController == null || stopGame()) {
            Platform.exit();
        }
    }

    public boolean isGameRunning() {
        return gameController != null;
    }


    @Override
    public void update(Subject subject) {
        // XXX do nothing for now
    }

}
