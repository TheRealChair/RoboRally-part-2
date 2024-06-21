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
package Gruppe3.roborally.model;

import Gruppe3.designpatterns.observer.Subject;
import org.jetbrains.annotations.NotNull;

import Gruppe3.roborally.controller.FieldAction;
import Gruppe3.roborally.controller.GameController;
import Gruppe3.roborally.controller.ConveyorBelt;


import java.util.ArrayList;
import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Board extends Subject {

    public final int width;

    public final int height;

    private Integer gameId;

    private static Space[][] spaces;

    private final List<Player> players = new ArrayList<>();

    private Player current;

    private Phase phase = Phase.INITIALISATION;

    private int step = 0;
    private int counter = 0;

    private boolean stepMode;

    /*
    public static boolean[][] pits;

    public static final int[][] prePitPos = {
            {2, 3}, {4, 6}, {6,1}
    };
    */

    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        spaces = new Space[width][height];
        //pits = new boolean[width][height];
        for (int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                Space space = new Space(this, x, y);
                spaces[x][y] = space;
            }
        }
        //initializePrePitPos();
        this.stepMode = false;
        setupWalls();
        setupCheckpoints();
        setupPits();
        setupReboot();
        setupConveyorBelts();
    }

    public void setupWalls() {
        getSpace(8, 0).addWall(Heading.EAST);
        getSpace(8, 1).addWall(Heading.EAST);
        getSpace(8, 2).addWall(Heading.EAST);
        // Ovenfor er væggene, og der kan tilføjes flere ved bare at indtaste koordinaterne

    }

    public void setupConveyorBelts() {
        // Example setup
        getSpace(2, 4).addAction(new ConveyorBelt(Heading.EAST, 1));
        getSpace(3, 4).addAction(new ConveyorBelt(Heading.EAST, 1));
        getSpace(4, 4).addAction(new ConveyorBelt(Heading.EAST, 1));
        getSpace(7, 4).addAction(new ConveyorBelt(Heading.WEST, 1));
        getSpace(8, 4).addAction(new ConveyorBelt(Heading.WEST, 1));
        getSpace(9, 4).addAction(new ConveyorBelt(Heading.WEST, 1));
        getSpace(10, 4).addAction(new ConveyorBelt(Heading.WEST, 2));
        getSpace(11, 4).addAction(new ConveyorBelt(Heading.WEST, 2));
        getSpace(2, 5).addAction(new ConveyorBelt(Heading.EAST, 1));
        getSpace(3, 5).addAction(new ConveyorBelt(Heading.EAST, 1));
        getSpace(4, 5).addAction(new ConveyorBelt(Heading.EAST, 1));
        getSpace(7, 5).addAction(new ConveyorBelt(Heading.WEST, 1));
        getSpace(8, 5).addAction(new ConveyorBelt(Heading.WEST, 1));
        getSpace(9, 5).addAction(new ConveyorBelt(Heading.WEST, 1));
        getSpace(10, 5).addAction(new ConveyorBelt(Heading.WEST, 2));
        getSpace(11, 5).addAction(new ConveyorBelt(Heading.WEST, 2));
        getSpace(5, 0).addAction(new ConveyorBelt(Heading.SOUTH, 2));
        getSpace(5, 1).addAction(new ConveyorBelt(Heading.SOUTH, 1));
        getSpace(5, 2).addAction(new ConveyorBelt(Heading.SOUTH, 1));
        getSpace(5, 3).addAction(new ConveyorBelt(Heading.SOUTH, 1));
        getSpace(5, 6).addAction(new ConveyorBelt(Heading.NORTH, 1));
        getSpace(5, 7).addAction(new ConveyorBelt(Heading.NORTH, 1));
        getSpace(5, 8).addAction(new ConveyorBelt(Heading.NORTH, 1));
        getSpace(5, 9).addAction(new ConveyorBelt(Heading.NORTH, 2));
        getSpace(6, 0).addAction(new ConveyorBelt(Heading.SOUTH, 2));
        getSpace(6, 1).addAction(new ConveyorBelt(Heading.SOUTH, 1));
        getSpace(6, 2).addAction(new ConveyorBelt(Heading.SOUTH, 1));
        getSpace(6, 3).addAction(new ConveyorBelt(Heading.SOUTH, 1));
        getSpace(6, 6).addAction(new ConveyorBelt(Heading.NORTH, 1));
        getSpace(6, 7).addAction(new ConveyorBelt(Heading.NORTH, 1));
        getSpace(6, 8).addAction(new ConveyorBelt(Heading.NORTH, 1));
        getSpace(6, 9).addAction(new ConveyorBelt(Heading.NORTH, 2));

        // Add more conveyor belts as needed
    }

    public void triggerConveyorBelts(GameController gameController) {
        for (Player player : players) {
            Space space = player.getSpace();
            if (space != null) {
                for (FieldAction action : space.getActions()) {
                    if (action instanceof ConveyorBelt) {
                        action.doAction(gameController, space);
                    }
                }
            }
        }
    }



    /**
     * Sets up the checkpoints on the board.
     * @Param id the id of the checkpoint
     * @Author Balder Jacobsen
*/
    public void setupCheckpoints() {
        int[][] checkpointPositions = {
                {9, 9},
                {2, 1},
                {8, 0},
                {2, 7}
        };

        for (int i = 0; i < checkpointPositions.length; i++) {
            int x = checkpointPositions[i][0];
            int y = checkpointPositions[i][1];
            if (x >= 0 && x < width && y >= 0 && y < height) {
                getSpace(x, y).addCheckpoint(new Checkpoint(i + 1));
            } else {
                System.out.println("Checkpoint coordinates out of bounds: " + x + ", " + y);
            }
        }
    }
    /**
     * setup pits for json.
     * @author Victor Mazanti, Balder Jacobsen.
     */
    public void setupPits() {
        int[][] pitPositions = {
                {5, 4},
                {5, 5},
                {6, 4},
                {6, 5}
        };
        for(int i = 0; i < pitPositions.length; i++){
            int x = pitPositions[i][0];
            int y = pitPositions[i][1];
            if (x >= 0 && x < width && y >= 0 && y < height) {
                getSpace(x, y).addPit(new Pits());
            } else {
                System.out.println("Pit coordinates out of bounds: " + x + ", " + y);
            }
        }
        // Ovenfor er pits, og der kan tilføjes flere ved bare at indtaste koordinaterne
    }
    /**
     * setup reboot for json.
     * @author Victor Mazanti, Balder Jacobsen.
     */
    public void setupReboot (){
        int[][] rebootPosition = {
                {11, 0}
        };
        for(int i = 0; i < rebootPosition.length; i++){
            int x = rebootPosition[i][0];
            int y = rebootPosition[i][1];
            if (x >= 0 && x < width && y >= 0 && y < height) {
                getSpace(x, y).addReboot(new Reboots());
            } else {
                System.out.println("Reboot coordinates out of bounds: " + x + ", " + y);
            }
        }
    }



    /**
     * Checks if a wall is present in the given space in the given direction.
     * @param space
     * @param heading
     * @author Kasparas
     * @return
     */
    public boolean hasWall(Space space, Heading heading) {
        if (space.getWalls().contains(heading)) {
            return true;
        }
    
        Space neighbor = getNeighbour(space, heading);
        if (neighbor != null) {
            Heading reverse = Heading.values()[(heading.ordinal() + 2) % Heading.values().length];
            if (neighbor.getWalls().contains(reverse)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasCheckpoint(Space space) {
        return space.getCheckpoints().size() > 0;
    }

    
    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        if (this.gameId == null) {
            this.gameId = gameId;
        } else {
            if (!this.gameId.equals(gameId)) {
                throw new IllegalStateException("A game with a set id may not be assigned a new id!");
            }
        }
    }

    public Space getSpace(int x, int y) {
        if (x >= 0 && x < width &&
                y >= 0 && y < height) {
            return spaces[x][y];
        } else {
            return null;
        }
    }

    public int getPlayersNumber() {
        return players.size();
    }

    public void addPlayer(@NotNull Player player) {
        if (player.board == this && !players.contains(player)) {
            players.add(player);
            notifyChange();
        }
    }

    public Player getPlayer(int i) {
        if (i >= 0 && i < players.size()) {
            return players.get(i);
        } else {
            return null;
        }
    }

    public Player getCurrentPlayer() {
        return current;
    }

    public void setCurrentPlayer(Player player) {
        if (player != this.current && players.contains(player)) {
            this.current = player;
            notifyChange();
        }
    }

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        if (phase != this.phase) {
            this.phase = phase;
            notifyChange();
        }
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        if (step != this.step) {
            this.step = step;
            notifyChange();
        }
    }

    public boolean isStepMode() {
        return stepMode;
    }

    public void setStepMode(boolean stepMode) {
        if (stepMode != this.stepMode) {
            this.stepMode = stepMode;
            notifyChange();
        }
    }

    public int getPlayerNumber(@NotNull Player player) {
        if (player.board == this) {
            return players.indexOf(player);
        } else {
            return -1;
        }
    }

    /**
     * Returns the neighbour of the given space of the board in the given heading.
     * The neighbour is returned only, if it can be reached from the given space
     * (no walls or obstacles in either of the involved spaces); otherwise,
     * null will be returned.
     *
     * @param space the space for which the neighbour should be computed
     * @param heading the heading of the neighbour
     * @return the space in the given direction; null if there is no (reachable) neighbour
     */
    public Space getNeighbour(@NotNull Space space, @NotNull Heading heading) {
        if (space.getWalls().contains(heading)) {
            return null;
        }

        int x = space.x;
        int y = space.y;
        switch (heading) {
            case SOUTH:
                y++;
                break;
            case WEST:
                x--;
                break;
            case NORTH:
                y--;
                break;
            case EAST:
                x++;
                break;
        }

        // Check if the new position is within bounds
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return null; // Treat out-of-bounds as null
        }

        Heading reverse = Heading.values()[(heading.ordinal() + 2) % Heading.values().length];
        Space result = getSpace(x, y);
        if (result != null && result.getWalls().contains(reverse)) {
            return null; // Hit a wall on the other side
        }

        return result;
    }

    /**
     * Constructs a status message that summarizes the current state of the game or process.
     * The message includes details about the current phase, the player, the step, and a counter.
     * Each component of the status is concatenated into a single string.
     * @Author: Balder, Elias, Karl and Viktor
     * @return A string representation of the current status, formatted as:
     *         "Phase: [phase name], Player: [player name], Step: [step], Counter: [counter value]".
     *         Each part of the message is derived from the respective getters:
     *         - Phase name from {@link #getPhase()},
     *         - Player name from {@link #getCurrentPlayer()},
     *         - Step information from {@link #getStep()},
     *         - Counter value from {@link #getCounter()}.
     */
    public String getStatusMessage() {
        Player currentPlayer = getCurrentPlayer();
        if (currentPlayer != null) {
            return "Phase: " + getPhase().name() +
                    ", Player: " + currentPlayer.getGamePlayerID() +
                    ", Step: " + getStep() +
                    ", Counter: " + getCounter();
        } else {
            return "No current player found.";
        }
    }



    public void setCounter(int counter) {
        this.counter = counter;
    }

    public int getCounter() {
        return counter;
    }

    public void incrementCounter() {
        this.counter++;
    }

    public Player[] getPlayers() {
        return players.toArray(new Player[0]);
    }
}
/*
    public void initializePrePitPos() {
        for(int[] pitPos : prePitPos){
            int x = pitPos[0];
            int y = pitPos[1];
            if (x >= 0 && x < width && y >= 0 && y < height) {
                Pits.addPit(x, y);
            } else {
                System.out.println("Pit coordinates out of bounds: " + x + ", " + y);
            }
        }
    }
*/