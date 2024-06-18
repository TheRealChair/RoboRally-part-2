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

import Gruppe3.roborally.controller.FieldAction;
import Gruppe3.designpatterns.observer.Subject;

import java.util.ArrayList;
import java.util.List;


/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Space extends Subject {

    private Player player;

    private Checkpoint checkpoint;

    private List<Heading> walls = new ArrayList<>();
    private List<FieldAction> actions = new ArrayList<>();
    private List<Checkpoint> checkpoints = new ArrayList<>();
    private List<Pits> pits = new ArrayList<>();
    private List<Reboots> reboots = new ArrayList<>();

    public final Board board;

    public final int x;
    public final int y;

    private boolean isPit;

    public Space(Board board, int x, int y) {
        this.board = board;
        this.x = x;
        this.y = y;
        player = null;
        this.isPit = false;
        this.actions = new ArrayList<>();
    }

    public Board getBoard() {
        return board;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void addAction(FieldAction action) {
        if (!actions.contains(action)) {
            actions.add(action);
            notifyChange();
        }
    }

    public void addWall(Heading heading) {
        if (x >= 0 && x < board.width && y >= 0 && y < board.height) {
            if (!walls.contains(heading)) {
                walls.add(heading);
                notifyChange();
            }
        } else {
            System.out.println("Wall coordinates out of bounds: " + x + ", " + y);
        }
    }

    public void addCheckpoint(Checkpoint checkpoint) {
        if (!checkpoints.contains(checkpoint)) {
            checkpoints.add(checkpoint);
            notifyChange();
        }
    }
    /**
     * add pits on the board.
     * @author Victor Mazanti
     */
    public void addPit(Pits pit){
        if (!pits.contains(pit)){
            pits.add(pit);
            notifyChange();
        }
    }
    /**
     * add reboot spot on the board.
     * @author Victor Mazanti
     */
    public void addReboot(Reboots reboot) {
        if (!reboots.contains(reboot)) {
            reboots.add(reboot);
            notifyChange();
        }
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        Player oldPlayer = this.player;
        if (player != oldPlayer &&
                (player == null || board == player.board)) {
            this.player = player;
            if (oldPlayer != null) {
                // this should actually not happen
                oldPlayer.setSpace(null);
            }
            if (player != null) {
                player.setSpace(this);
            }
            notifyChange();
        }
    }

    public List<Heading> getWalls() {
        return walls;
    }

    public List<FieldAction> getActions() {
        return actions;
    }


    public List<Checkpoint> getCheckpoints() {
        return checkpoints;
    }

    public List<Pits> getPits(){
        return pits;
    }
    public boolean isPit(){
        return !pits.isEmpty();
    }
    public List<Reboots> getReboots() {
        return reboots;
    }

    /*
    public List<Pits> getPits() {
        isPit = pits[x][y];
        return List.of();
    }
    */

    void playerChanged() {
        // This is a minor hack; since some views that are registered with the space
        // also need to update when some player attributes change, the player can
        // notify the space of these changes by calling this method.
        notifyChange();
    }


    /*
    public boolean isPit(){
        return isPit;
    }

    public void setPit(boolean pit){
        isPit = pit;
        notifyChange();
    }

     */


}
