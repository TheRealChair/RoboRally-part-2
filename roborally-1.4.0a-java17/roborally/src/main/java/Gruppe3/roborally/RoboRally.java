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
package Gruppe3.roborally;

import Gruppe3.roborally.controller.AppController;
import Gruppe3.roborally.controller.GameController;
import Gruppe3.roborally.view.BoardView;
import Gruppe3.roborally.view.RoboRallyMenuBar;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import java.io.IOException;
import java.util.Objects;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class RoboRally extends Application {

    private static final int MIN_APP_WIDTH = 600;
    private static final int MIN_APP_HEIGHT = 200;

    private Stage stage;
    private BorderPane boardRoot;

    private Button button1;
    private Button button2;
    private static Label lobbyLabel;


    @Override
    public void init() throws Exception {
        super.init();
    }



    @Override
    public void start(Stage primaryStage) {

        stage = primaryStage;
        lobbyLabel = new Label();


        RestTemplate restTemplate = new RestTemplate();
        AppController appController = new AppController(this, restTemplate);

        // create the primary scene with the a menu bar and a pane for
        // the board view (which initially is empty); it will be filled
        // when the user creates a new game or loads a game
        RoboRallyMenuBar menuBar = new RoboRallyMenuBar(appController);
        boardRoot = new BorderPane();
        VBox vbox = new VBox(menuBar, boardRoot);
        vbox.setMinWidth(MIN_APP_WIDTH);
        vbox.setMinHeight(MIN_APP_HEIGHT);
        Scene primaryScene = new Scene(vbox);

        try {
            String css = getClass().getResource("/Style.css").toExternalForm();
            primaryScene.getStylesheets().add(css);
        } catch (NullPointerException e) {
            System.err.println("Error: Could not load CSS file.");
            e.printStackTrace();
        }

        // Create two buttons
        button1 = new Button("New Game");
        button2 = new Button("Join Game");


        lobbyLabel.setText("Waiting for more players...");
        lobbyLabel.setVisible(false);

        button1.setOnAction(e -> {
            try {
                appController.newGame();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        });
        button2.setOnAction(e -> {
            try {
                Long gameId = 4L; //TEMP GAME ID
                appController.joinGame(gameId);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        });

        VBox vbox1 = new VBox();

        // Create a region to act as a spacer
        Region spacer = new Region();
        spacer.setPrefHeight(25);

        // Create a HBox to hold the buttons
        vbox1.getChildren().addAll(spacer, lobbyLabel, button1, button2);

        vbox1.setAlignment(Pos.CENTER);
        vbox1.setSpacing(10);

        // Set the HBox to the center of the BorderPane
        boardRoot.setCenter(vbox1);

        vbox.getStyleClass().add("root");


        stage.setScene(primaryScene);
        stage.setTitle("RoboRally");
        stage.setOnCloseRequest(
                e -> {
                    e.consume();
                    appController.exit();} );
        stage.setResizable(false);
        stage.sizeToScene();
        stage.show();
    }

    public static void setLobbyLabel(String str) {
        Platform.runLater(() ->{
        lobbyLabel.setText(str);
        });
    }

    public void createBoardView(GameController gameController) {
        // if present, remove old BoardView
        boardRoot.getChildren().clear();

        if (gameController != null) {
            // create and add view for new board
            BoardView boardView = new BoardView(gameController);
            boardRoot.setCenter(boardView);
        }

        stage.sizeToScene();
    }

    @Override
    public void stop() throws Exception {
        super.stop();

        // XXX just in case we need to do something here eventually;
        //     but right now the only way for the user to exit the app
        //     is delegated to the exit() method in the AppController,
        //     so that the AppController can take care of that.
    }

    public Button getButton1() {
        return button1;
    }

    public Button getButton2() {
        return button2;
    }

    public Label getLobbyLabel() {
        return lobbyLabel;
    }

    public static void main(String[] args) {
        launch(args);
    }

}