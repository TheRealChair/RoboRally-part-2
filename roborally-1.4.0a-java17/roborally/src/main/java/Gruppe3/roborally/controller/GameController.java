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

import Gruppe3.roborally.model.*;
import javafx.scene.control.Alert;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class GameController {

    final public Board board;
    private Player playerToInteract;


    public GameController(Board board) {
        this.board = board;
    }


    /**
     * Moves the given player one step forward in the direction it is facing.
     * @param player the player to move
     * @Author: Balder, Elias, Karl and Viktor
     */
    public void moveForward(@NotNull Player player) {
        if (player.board == board) {
            Space space = player.getSpace();
            Heading heading = player.getHeading();

            if (!board.hasWall(space, heading)) {
            Space target = board.getNeighbour(space, heading);
            if (target != null) {
                try {
                    moveToSpace(player, target, heading);
                } catch (ImpossibleMoveException e) {
                    // we don't do anything here  for now; we just catch the
                    // exception so that we do no pass it on to the caller
                    // (which would be very bad style).
                }
            }
        }
    }
    }

    /**
     * Moves the given player two steps forward in the direction it is facing.
     * @param player the player to move
     * @Author: Balder, Elias, Karl and Viktor
     */
    // TODO Assignment A3
    public void fastForward(@NotNull Player player) {
        for(int i = 0 ; i < 2 ; i++) {
            if (player.board == board) {
                Space space = player.getSpace();
                Heading heading = player.getHeading();

                Space target1 = board.getNeighbour(space, heading);
                if (target1 != null) {
                    try {
                        moveToSpace(player, target1, heading);
                    } catch (ImpossibleMoveException e) {
                        // we don't do anything here  for now; we just catch the
                        // exception so that we do no pass it on to the caller
                        // (which would be very bad style).
                    }
                }
            }
            if(player.hasBeenInPit){
                player.hasBeenInPit = false;
                break;
            }
        }
    }
    /**
     * Moves the given player three steps forward in the direction it is facing.
     * @param player the player to move
     * @Author: Balder, Elias and Viktor
     */
    public void superFastForward(@NotNull Player player) {
        for(int i = 0 ; i < 3 ; i++) {
            if (player.board == board) {
                Space space = player.getSpace();
                Heading heading = player.getHeading();

                Space target1 = board.getNeighbour(space, heading);
                if (target1 != null) {
                    try {
                        moveToSpace(player, target1, heading);
                    } catch (ImpossibleMoveException e) {
                        // we don't do anything here  for now; we just catch the
                        // exception so that we do no pass it on to the caller
                        // (which would be very bad style).
                    }
                }
            }
            if(player.hasBeenInPit){
                player.hasBeenInPit = false;
                break;
            }
        }
    }

    /**
     * Turns the given player to the right.
     * @param player the player to turn
     * @Author: Balder, Elias, Karl and Viktor
     */
    // TODO Assignment A3
    public void turnRight(@NotNull Player player) {
        Heading heading = player.getHeading();
        Heading newHeading = heading.next();
        player.setHeading(newHeading);
    }

    /**
     * Turns the given player to the left.
     * @param player the player to turn
     * @Author: Balder, Elias, Karl and Viktor
     */
    // TODO Assignment A3
    public void turnLeft(@NotNull Player player) {
        Heading heading = player.getHeading();
        Heading newHeading = heading.prev();
        player.setHeading(newHeading);
    }
    public void leftOrRight(@NotNull Player player) {
        Heading heading = player.getHeading();
        Heading newHeading = heading.next();
        player.setHeading(newHeading);
    }

    /**
     * Moves the given player to the given space in the given direction.
     * @param player the player to move
     * @param space the space the player is on / trying to move to
     * @param heading the direction the player should face after the move
     * @throws ImpossibleMoveException if the move is not possible
     */
    void moveToSpace(@NotNull Player player, @NotNull Space space, @NotNull Heading heading) throws ImpossibleMoveException {
        assert board.getNeighbour(player.getSpace(), heading) == space; // make sure the move to here is possible in principle
        Player other = space.getPlayer();
        if (other != null){
            Space target = board.getNeighbour(space, heading);
            if (target != null) {
                // XXX Note that there might be additional problems with
                //     infinite recursion here (in some special cases)!
                //     We will come back to that!
                moveToSpace(other, target, heading);

                // Note that we do NOT embed the above statement in a try catch block, since
                // the thrown exception is supposed to be passed on to the caller

                assert target.getPlayer() == null : target; // make sure target is free now
            } else {
                throw new ImpossibleMoveException(player, space, heading);
            }
        }
        player.setSpace(space);
    }


    /**
     * Moves the current player to the given space, if the space is free.
     * @param space, the space to move the player to
     * @Author: Balder, Elias, Karl and Viktor
     */
    public void moveCurrentPlayerToSpace(Space space) {
        Player currentPlayer = board.getCurrentPlayer();
        Player possiblePlayer = space.getPlayer();
        if (currentPlayer != null && possiblePlayer == null) {
            currentPlayer.setSpace(space);
            int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
            if (nextPlayerNumber < board.getPlayersNumber()) {
                board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
            } else {
                board.setCurrentPlayer(board.getPlayer(0));
            }
            board.setStep(board.getStep()+1);
            board.incrementCounter();
        }
    }

    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
    }

    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    private void continuePrograms() {
        do {
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }

    public void startExecuteStep() {
        board.setStepMode(true);
        }

    private void executeNextStep() {
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    Command command = card.command;
                    executeCommand(currentPlayer, command);
                }
                if (board.getPhase() != Phase.PLAYER_INTERACTION) {
                    int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
                    if (nextPlayerNumber < board.getPlayersNumber()) {
                        board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
                    } else {
                        step++;
                        if (step < Player.NO_REGISTERS) {
                            makeProgramFieldsVisible(step);
                            board.setStep(step);
                            board.setCurrentPlayer(board.getPlayer(0));
                        } else {
                            startProgrammingPhase();
                        }
                    }
                }
            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
    }

    public void continueFromPlayerInteraction() {
        // Reset the game phase to ACTIVATION
        board.setPhase(Phase.ACTIVATION);

        // Move to the next player
        int nextPlayerNumber = board.getPlayerNumber(board.getCurrentPlayer()) + 1;
        if (nextPlayerNumber < board.getPlayersNumber()) {
            board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
        } else {
            int step = board.getStep() + 1;
            if (step < Player.NO_REGISTERS) {
                board.setStep(step);
                board.setCurrentPlayer(board.getPlayer(0));
            } else {
                startProgrammingPhase();
            }
        }
    }

    /**
     * Executes the given command for the given player.
     * @param player, the player to execute the command for
     * @param command, the command to execute
     * @Author: Balder, Elias, Karl and Viktor
     */

    private void executeCommand(@NotNull Player player, Command command) {
        if (player != null && player.board == board && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).

            switch (command) {
                case FORWARD:
                    this.moveForward(player);
                    break;
                case RIGHT:
                    this.turnRight(player);
                    break;
                case LEFT:
                    this.turnLeft(player);
                    break;
                case FAST_FORWARD:
                    this.fastForward(player);
                    break;
                case SUPER_FAST_FORWARD:
                    this.superFastForward(player);
                    break;
                case U_TURN:
                    this.turnRight(player);
                    this.turnRight(player);
                    break;
                case BACK_UP:
                    this.turnRight(player);
                    this.turnRight(player);
                    this.moveForward(player);
                    if (player.hasBeenInPit) {
                        player.hasBeenInPit = false;
                        break;
                    }
                    this.turnRight(player);
                    this.turnRight(player);
                    break;
                case OPTION_LEFT_RIGHT:
                    board.setPhase(Phase.PLAYER_INTERACTION);
                    playerToInteract = player;
                    break;
                case AGAIN:
                    if (player.getLastExecutedCommand() != null) {
                        if (player.getLastExecutedCommand().command == Command.OPTION_LEFT_RIGHT){
                            board.setPhase(Phase.PLAYER_INTERACTION);
                            playerToInteract = player;
                        } else {
                            executeCommand(player, player.getLastExecutedCommand().command);
                        }
                    }
                    break;
                 default:
                    // DO NOTHING (for now)
            }
            if (command != Command.AGAIN) {
                player.setLastExecutedCommand(new CommandCard(command));
            }
            Space space = player.getSpace();
            for (Checkpoint checkpoint : space.getCheckpoints()) {
                if (checkpoint.getId() == player.getCurrentCheckpoint()) {
                    player.incrementPoints();
                    player.incrementCurrentCheckpoint();
                    break;
                }
            }
            checkWinCondition(player);
        }
    }

    public Player getPlayerToInteract() {
        return playerToInteract;
    }

    public boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target) {
        CommandCard sourceCard = source.getCard();
        CommandCard targetCard = target.getCard();
        if (sourceCard != null && targetCard == null) {
            target.setCard(sourceCard);
            source.setCard(null);
            return true;
        } else {
            return false;
        }
    }


    public void startProgrammingPhase() {
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                }
                for (int j = 0; j < Player.NO_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    field.setCard(generateRandomCommandCard());
                    field.setVisible(true);
                }
            }
        }
    }

    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    private void checkWinCondition(Player player) {
        // Get the total number of checkpoints
        int totalCheckpoints = 0;
        for (int x = 0; x < board.width; x++) {
            for (int y = 0; y < board.height; y++) {
                totalCheckpoints += board.getSpace(x, y).getCheckpoints().size();
            }
        }
        // Print the player's points and total checkpoints for debugging
        System.out.println("Player's points: " + player.getPoints());
        System.out.println("Total checkpoints: " + totalCheckpoints);


        // If the player's points are equal to the total number of checkpoints, the player has won
        if (player.getPoints() == totalCheckpoints) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText(null);
            alert.setContentText(player.getName() + " has won the game!");

            alert.showAndWait();
        }
    }

    /**
     * A method called when no corresponding controller operation is implemented yet. This
     * should eventually be removed.
     */
    public void notImplemented() {
        // XXX just for now to indicate that the actual method is not yet implemented
        assert false;
    }

    public Board getBoard() {
        return board;
    }


    class ImpossibleMoveException extends Exception {

        private Player player;
        private Space space;
        private Heading heading;

        public ImpossibleMoveException(Player player, Space space, Heading heading) {
            super("Move impossible");
            this.player = player;
            this.space = space;
            this.heading = heading;
        }
    }
}
