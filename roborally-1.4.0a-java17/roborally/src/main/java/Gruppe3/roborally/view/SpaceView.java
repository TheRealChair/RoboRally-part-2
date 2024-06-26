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
package Gruppe3.roborally.view;

import Gruppe3.roborally.controller.ConveyorBelt;
import Gruppe3.roborally.controller.FieldAction;
import Gruppe3.roborally.model.*;
import Gruppe3.designpatterns.observer.Subject;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Line;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class SpaceView extends StackPane implements ViewObserver {

    final public static int SPACE_HEIGHT = 60; // 75;
    final public static int SPACE_WIDTH = 60; // 75;

    public final Space space;

    private Group playerLayer;
    private Group pitLayer;
    private Group rebootLayer;


    public SpaceView(@NotNull Space space) {
        this.space = space;

        // XXX the following styling should better be done with styles
        this.setPrefWidth(SPACE_WIDTH);
        this.setMinWidth(SPACE_WIDTH);
        this.setMaxWidth(SPACE_WIDTH);

        this.setPrefHeight(SPACE_HEIGHT);
        this.setMinHeight(SPACE_HEIGHT);
        this.setMaxHeight(SPACE_HEIGHT);

        playerLayer = new Group();
        pitLayer = new Group();
        rebootLayer = new Group();

        // Add layers to the scene graph in the desired order
        getChildren().addAll(pitLayer, rebootLayer, playerLayer);

        if ((space.x + space.y) % 2 == 0) {
            double imageWidth = 60.0; // adjust to desired width in pixels
            double imageHeight = 60.0; // adjust to desired height in pixels

            BackgroundSize backgroundSize = new BackgroundSize(imageWidth, imageHeight, false, false, false, false);
            Image image = new Image("BoardPics/empty.png");
            BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, backgroundSize);
            this.setBackground(new Background(backgroundImage));
        } else {
            Image image = new Image("BoardPics/empty.png");
            double imageWidth = 60.0; // adjust to desired width in pixels
            double imageHeight = 60.0; // adjust to desired height in pixels

            BackgroundSize backgroundSize = new BackgroundSize(imageWidth, imageHeight, false, false, false, false);
            BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, backgroundSize);
            this.setBackground(new Background(backgroundImage));
        }

        // updatePlayer();

        // This space view should listen to changes of the space
        space.attach(this);
        update(space);
    }

    /**
     * Places a robot on a space if a player is on that space.
     * @author Viktor
     */
    private void updatePlayer() {
        this.getChildren().clear();
        drawWalls();
        drawCheckpoints();
        drawPits();
        drawReboots();
        drawConveyorBelts();

        Player player = space.getPlayer();
        if (player != null) {
            Image robotImage = new Image("RobotPics/" + player.getColor() + ".png");
            ImageView robotView = new ImageView(robotImage);

            robotView.setRotate((180 + 90 * player.getHeading().ordinal()) % 360);

            robotView.setFitWidth(SPACE_WIDTH);
            robotView.setFitHeight(SPACE_HEIGHT);
            robotView.setLayoutX((SPACE_WIDTH - robotView.getBoundsInLocal().getWidth()) / 2);
            robotView.setLayoutY((SPACE_HEIGHT - robotView.getBoundsInLocal().getHeight()) / 2);


            this.getChildren().add(robotView);
        }
    }

    private void drawWalls() {
        for (Heading heading : space.getWalls()) {
            drawWall(heading);
        }
    }

    private void drawWall(Heading heading) {
        double wallThickness = 5;
        Line line = new Line();
        line.setStroke(Color.ORANGE);
        line.setStrokeWidth(wallThickness);
    
        double offset = wallThickness / 2;
        switch (heading) {
            case NORTH:
                line.setStartX(offset);
                line.setEndX(SPACE_WIDTH - offset);
                line.setStartY(0);
                line.setEndY(0);
                line.setTranslateY(-SPACE_HEIGHT / 2 + wallThickness / 2); 
                break;
            case SOUTH:
                line.setStartX(offset);
                line.setEndX(SPACE_WIDTH - offset);
                line.setStartY(SPACE_HEIGHT - offset);
                line.setEndY(SPACE_HEIGHT - offset);
                line.setTranslateY(SPACE_HEIGHT / 2 - wallThickness / 2); 
                break;
            case EAST:
                line.setStartX(SPACE_WIDTH - offset);
                line.setEndX(SPACE_WIDTH - offset);
                line.setStartY(offset);
                line.setEndY(SPACE_HEIGHT - offset);
                line.setTranslateX(SPACE_WIDTH / 2 - wallThickness / 2);
                break;
            case WEST:
                line.setStartX(offset);
                line.setEndX(offset);
                line.setStartY(offset);
                line.setEndY(SPACE_HEIGHT - offset);
                line.setTranslateX(-SPACE_WIDTH / 2 + wallThickness / 2); 
                break;
        }
        this.getChildren().add(line);
    }

    /**
     * Draw the checkpoints on the board.
     * @Author Balder Jacobsen
     */
    public  void drawCheckpoints() {
        for (Checkpoint checkpoint : space.getCheckpoints()) {
            drawCheckpoint(checkpoint);
        }
    }
    public void drawCheckpoint(Checkpoint checkpoint) {
        double checkpointSize = 10;
        Polygon polygon = new Polygon();
        polygon.getPoints().addAll(new Double[]{
                0.0, 0.0,
                checkpointSize, 0.0,
                checkpointSize, checkpointSize,
                0.0, checkpointSize
        });
        double imageWidth = 60.0; // adjust to desired width in pixels
        double imageHeight = 60.0;
        BackgroundSize backgroundSize = new BackgroundSize(imageWidth, imageHeight, false, false, false, false);
        String imagePath = "BoardPics/" + checkpoint.getId() + ".png";
        Image image = new Image(imagePath);
        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, backgroundSize);
        this.setBackground(new Background(backgroundImage));
    }
    
    
    
    @Override
    public void updateView(Subject subject) {
        if (subject == this.space) {
            updatePit();
            //updateReboot();
            updatePlayer();
        }
    }

    public void updatePit() {
        if (space.isPit()) {
            double imageWidth = 60.0; // adjust to desired width in pixels
            double imageHeight = 60.0;
            BackgroundSize backgroundSize = new BackgroundSize(imageWidth, imageHeight, false, false, false, false);
            String imagePath = "BoardPics/hole.png";
            Image image = new Image(imagePath);
            BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, backgroundSize);
            this.setBackground(new Background(backgroundImage));
        }
    }
    /**
     * Draw the pits on the board.
     * @author Victor Mazanti, Balder Jacobsen
     */
    public void drawPits() {
        for (Pits pit : space.getPits()) {
            drawPit(pit);
        }
    }
    public void drawPit(Pits pit) {
        double pitSize = 10;
        Polygon polygon = new Polygon();
        polygon.getPoints().addAll(new Double[]{
                0.0, 0.0,
                pitSize, 0.0,
                pitSize, pitSize,
                0.0, pitSize
        });
        double imageWidth = 60.0; // adjust to desired width in pixels
        double imageHeight = 60.0;
        BackgroundSize backgroundSize = new BackgroundSize(imageWidth, imageHeight, false, false, false, false);
        String imagePath = "BoardPics/hole.png";
        Image image = new Image(imagePath);
        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, backgroundSize);
        this.setBackground(new Background(backgroundImage));
    }
    public void drawReboots() {
        for (Reboots reboot : space.getReboots()) {
            drawReboot(reboot);
        }
    }

    public void drawReboot(Reboots reboots) {
        double rebootSize = 10;
        Polygon polygon = new Polygon();
        polygon.getPoints().addAll(new Double[]{
                0.0, 0.0,
                rebootSize, 0.0,
                rebootSize, rebootSize,
                0.0, rebootSize
        });
        double imageWidth = 60.0; // adjust to desired width in pixels
        double imageHeight = 60.0;
        BackgroundSize backgroundSize = new BackgroundSize(imageWidth, imageHeight, false, false, false, false);
        String imagePath = "BoardPics/respawn.png";
        Image image = new Image(imagePath);
        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, backgroundSize);
        this.setBackground(new Background(backgroundImage));
    }

    public void drawConveyorBelts() {
        for (FieldAction action : space.getActions()) {
            if (action instanceof ConveyorBelt) {
                drawConveyorBelt((ConveyorBelt) action);
            }
        }
    }

    public void drawConveyorBelt(ConveyorBelt conveyorBelt) {
        String imagePath = conveyorBelt.getSpeed() == 1? "BoardPics/greenBelt.png" : "BoardPics/blueBelt.png";
        Image conveyorBeltImage = new Image(imagePath);
        ImageView conveyorBeltView = new ImageView(conveyorBeltImage);
        conveyorBeltView.setFitWidth(SPACE_WIDTH);
        conveyorBeltView.setFitHeight(SPACE_HEIGHT);


        conveyorBeltView.setRotate(180 + 90 * conveyorBelt.getDirection().ordinal() % 360);
        this.getChildren().add(conveyorBeltView);
    }
}