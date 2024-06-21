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

import Gruppe3.roborally.controller.GameController;
import Gruppe3.designpatterns.observer.Subject;
import org.jetbrains.annotations.NotNull;

import static Gruppe3.roborally.model.Heading.EAST;
import static Gruppe3.roborally.model.Heading.SOUTH;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Player extends Subject {

    final public static int NO_REGISTERS = 5;
    final public static int NO_CARDS = 8;

    final public Board board;
    private int gamePlayerID;
    private String color;

    private Space space;
    private Heading heading = EAST;

    private int points = 0;
    private int currentCheckpoint = 1;

    private CommandCardField[] program;
    private CommandCardField[] cards;

    public boolean hasBeenInPit = false;
    private CommandCard lastExecutedCommand;

    public Player(@NotNull Board board, String color, @NotNull int gamePlayerID, boolean hasBeenInPit) {
        this.board = board;
        this.gamePlayerID = gamePlayerID;
        this.color = color;
        this.hasBeenInPit = false;
        this.space = null;

        program = new CommandCardField[NO_REGISTERS];
        for (int i = 0; i < program.length; i++) {
            program[i] = new CommandCardField(this);
        }

        cards = new CommandCardField[NO_CARDS];
        for (int i = 0; i < cards.length; i++) {
            cards[i] = new CommandCardField(this);
        }
    }

    public int getGamePlayerID() {
        return gamePlayerID;
    }

    public void setGamePlayerID(int gamePlayerID) {
        if (gamePlayerID != 0 && gamePlayerID != this.gamePlayerID) {
            this.gamePlayerID = gamePlayerID;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
        notifyChange();
        if (space != null) {
            space.playerChanged();
        }
    }

    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        Space oldSpace = this.space;
        if (space != oldSpace &&
                (space == null || space.board == this.board)) {
            this.space = space;
            if (oldSpace != null) {
                oldSpace.setPlayer(null);
            }
            if (space != null) {
                space.setPlayer(this);
            }
            notifyChange();
        }
    }

    public Heading getHeading() {
        return heading;
    }

    public void setHeading(@NotNull Heading heading) {
        if (heading != this.heading) {
            this.heading = heading;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    }
    /**
     * move the player to reboot position.
     * @author Victor Mazanti
     */
    public void rebootPosition() {
        Player playerAtReboot = null;
        if (this.board.getSpace(Reboots.getX(), Reboots.getY()) != null) {
            playerAtReboot = this.board.getSpace(Reboots.getX(), Reboots.getY()).getPlayer();
            if (playerAtReboot != null) {
                GameController gameController = new GameController(board);
                gameController.moveForward(playerAtReboot, SOUTH);
            }
        }
        clearRegisters();
        this.space.setPlayer(null);
        this.setHeading(SOUTH);
        this.space = this.board.getSpace(Reboots.getX(), Reboots.getY());
        this.space.setPlayer(this);
    }


    /**
     * clear player register.
     * @author Victor Mazanti
     */
    public void clearRegisters(){
        for (CommandCardField commandCardField : program) {
            commandCardField.setCard(null);
        }
    }

    /**
     @author Viktor,
     @return the last executed command
     */
    public CommandCard getLastExecutedCommand() {
        return lastExecutedCommand;
    }

    /**
     @author Viktor,
     @param lastExecutedCommand the last executed command
     */
    public void setLastExecutedCommand(CommandCard lastExecutedCommand) {
        this.lastExecutedCommand = lastExecutedCommand;
    }

    public CommandCardField getProgramField(int i) {
        return program[i];
    }

    public CommandCardField getCardField(int i) {
        return cards[i];
    }

    public CommandCardField[] getCards() {
        return cards;
    }
    public void setCards(CommandCardField[] cards) {
        this.cards = cards;
    }

    public int getProgramFieldCount() {
        return program.length;
    }


    public int getPoints() {
        return points;
    }

    public void incrementPoints() {
        this.points++;
    }

    public int getCurrentCheckpoint() {
        return currentCheckpoint;
    }

    public void incrementCurrentCheckpoint() {
        this.currentCheckpoint++;
    }

}
