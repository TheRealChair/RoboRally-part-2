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

import Gruppe3.server.model.Game;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

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
    private static final String BASE_URL = "http://localhost:8080/games";
    private RestTemplate restTemplate;

    private GameController gameController;

    public AppController(@NotNull RoboRally roboRally, RestTemplate restTemplate) {

        this.roboRally = roboRally;
        this.restTemplate = restTemplate;
    }

    public void newGame() {
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

            Game game = new Game();
            game.setTurn_id(0);

            // Send the new game to the server
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            HttpEntity<Game> request = new HttpEntity<>(game, headers);
            ResponseEntity<Game> response = restTemplate.exchange(BASE_URL, HttpMethod.POST, request, Game.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Game created successfully: " + response.getBody());
            } else {
                throw new RuntimeException("Failed to create game: " + response.getStatusCode());
            }

            gameController.startProgrammingPhase();
            roboRally.createBoardView(gameController);
        }
    }

    private void sendNewGameToServer(Game game) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<Game> request = new HttpEntity<>(game, headers);
        ResponseEntity<Game> response = restTemplate.exchange(BASE_URL + "/new", HttpMethod.POST, request, Game.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Game created successfully: " + response.getBody());
        } else {
            throw new RuntimeException("Failed to create game: " + response.getStatusCode());
        }
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
