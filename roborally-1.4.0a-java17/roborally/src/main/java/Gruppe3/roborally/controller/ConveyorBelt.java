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

import Gruppe3.roborally.model.Space;
import Gruppe3.roborally.model.Player;
import Gruppe3.roborally.model.Heading;
import Gruppe3.roborally.model.Board;
import Gruppe3.roborally.model.Reboots;
import Gruppe3.roborally.model.Command;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class ConveyorBelt extends FieldAction {
    private final Heading direction;
    private final int speed;

    public ConveyorBelt(Heading direction, int speed) {
        this.direction = direction;
        this.speed = speed;
    }

    public Heading getDirection() {
        return direction;
    }

    public int getSpeed() {
        return speed;
    }

    @Override
    public boolean doAction(GameController gameController, Space space) {
        Player player = gameController.getPlayerOnSpace(space);
        if (player != null) {
            for (int i = 0; i < speed; i++) {
                Space nextSpace = gameController.getBoard().getNeighbour(space, direction);
                if (nextSpace != null) {
                    player.setSpace(nextSpace);
                    space = nextSpace;  // Update the space for the next iteration
                } else {
                    // Handle the case where the player would move out of bounds or into a pit
                    player.rebootPosition();
                    return false;
                }
            }
            return true;
        }
        return false;
    }

}
